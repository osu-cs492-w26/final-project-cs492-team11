import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.oregonstate.cs492.assignmentfinal.data.Game
import edu.oregonstate.cs492.assignmentfinal.data.GameScreenshots
import edu.oregonstate.cs492.assignmentfinal.data.GameScreenshotsRepository
import edu.oregonstate.cs492.assignmentfinal.data.RAWGService
import edu.oregonstate.cs492.assignmentfinal.data.SingleGameRepository
import kotlinx.coroutines.launch
import android.util.Log


class GameDetailsViewModel : ViewModel() {
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

    fun loadGameData(slug: String, key: String) {
        viewModelScope.launch {
            _loading.value = true

            val gameResult = gameRepo.loadSingleGame(slug, key)
            val screenshotsResult = screenshotRepo.loadGameScreenshots(slug, key)

            _loading.value = false

            if (gameResult.isSuccess) {
                _game.value = gameResult.getOrNull()
            } else {
                _error.value = gameResult.exceptionOrNull()
            }

            if (screenshotsResult.isSuccess) {
                _screenshots.value = screenshotsResult.getOrNull()
                Log.d("ViewModel", "Screenshots are good or smth")
            } else if (_error.value == null) { // Update error value only if main one did not
                _error.value = screenshotsResult.exceptionOrNull()
                Log.d("ViewModel", "Screenshots are bad or smth" + _error.value)
            }
        }
    }
}