package edu.oregonstate.cs492.assignmentfinal.ui

import android.app.Application
import android.util.Log
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

    private val _hintIndex = MutableLiveData(1)
    val hintIndex: LiveData<Int> = _hintIndex

    val maxHints = 5

    var score = 0
    var puzzleNum = 0

    private var correctAnswer: String? = null

    fun loadGameData(slug: String, key: String) {
        resetCompletion()
        puzzleNum += 1
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
            score += 1
        } else {
            _guessResult.value = GuessResult.INCORRECT
            incrementHint()

            if ((_hintIndex.value ?: 0) > maxHints) {
                _gameCompleted.value = true
            }
        }
        Log.d("Guess", _guessResult.value.toString())
    }

    fun resetCompletion() {
        _game.value = null
        _screenshots.value = null
        _error.value = null
        _loading.value = false
        _gameCompleted.value = false
        _guessResult.value = null
        _hintIndex.value = 1
        correctAnswer = null
    }

    fun newRun() {
        resetCompletion()
        score = 0
        puzzleNum = 0
    }

    fun incrementHint() {
        val current = _hintIndex.value ?: 1
        if (current <= maxHints) {
            _hintIndex.value = current + 1
        }
    }
}

enum class GuessResult {
    CORRECT, INCORRECT, EMPTY
}
