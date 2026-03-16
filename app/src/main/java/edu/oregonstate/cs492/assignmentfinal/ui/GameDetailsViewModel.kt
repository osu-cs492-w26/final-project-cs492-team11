package edu.oregonstate.cs492.assignmentfinal.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import edu.oregonstate.cs492.assignmentfinal.data.Game
import edu.oregonstate.cs492.assignmentfinal.data.GameScreenshots
import edu.oregonstate.cs492.assignmentfinal.data.GameScreenshotsRepository
import edu.oregonstate.cs492.assignmentfinal.data.RAWGService
import edu.oregonstate.cs492.assignmentfinal.data.SingleGameRepository
import kotlinx.coroutines.launch

class GameDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val gameRepo = SingleGameRepository(RAWGService.create())
    private val screenshotRepo = GameScreenshotsRepository(RAWGService.create())

    private val prefs = application.getSharedPreferences("daily_game", Context.MODE_PRIVATE)

    private val _game = MutableLiveData<Game?>(null)
    val game: LiveData<Game?> = _game

    private val _screenshots = MutableLiveData<GameScreenshots?>(null)
    val screenshots: LiveData<GameScreenshots?> = _screenshots

    private val _error = MutableLiveData<Throwable?>(null)
    val error: LiveData<Throwable?> = _error

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _gameCompleted = MutableLiveData<Boolean>(false)
    val gameCompleted: LiveData<Boolean> get() = _gameCompleted

    private val _guessResult = MutableLiveData<GuessResult?>(null)
    val guessResult: LiveData<GuessResult?> = _guessResult

    private var correctAnswer: String? = null

    fun loadGameData(slug: String, key: String) {
        viewModelScope.launch {
            _loading.value = true

            val gameResult = gameRepo.loadSingleGame(slug, key)
            val screenshotsResult = screenshotRepo.loadGameScreenshots(slug, key)

            _loading.value = false

            if (gameResult.isSuccess) {
                val gameObj = gameResult.getOrNull()
                _game.value = gameObj
                correctAnswer = gameObj?.name
            } else {
                _error.value = gameResult.exceptionOrNull()
            }

            if (screenshotsResult.isSuccess) {
                _screenshots.value = screenshotsResult.getOrNull()
            } else if (_error.value == null) {
                _error.value = screenshotsResult.exceptionOrNull()
            }
        }
    }

    fun submitGuess(guess: String) {
        if (guess.isBlank()) {
            _guessResult.value = GuessResult.EMPTY
            return
        }

        if (guess.equals(correctAnswer, ignoreCase = true)) {
            _guessResult.value = GuessResult.CORRECT
            _gameCompleted.value = true
        } else {
            _guessResult.value = GuessResult.INCORRECT
        }
    }

    fun resetCompletion() {
        _gameCompleted.value = false
        _guessResult.value = null
    }
}

enum class GuessResult {
    CORRECT, INCORRECT, EMPTY
}
