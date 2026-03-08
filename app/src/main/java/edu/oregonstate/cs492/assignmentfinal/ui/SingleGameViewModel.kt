package edu.oregonstate.cs492.assignmentfinal.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.oregonstate.cs492.assignmentfinal.data.Game
import edu.oregonstate.cs492.assignmentfinal.data.RAWGService
import edu.oregonstate.cs492.assignmentfinal.data.SingleGameRepository
import kotlinx.coroutines.launch

class SingleGameViewModel : ViewModel() {
    private val repository = SingleGameRepository(RAWGService.create())

    private val _game = MutableLiveData<Game>(null)

    val game: LiveData<Game?> = _game

    private val _error = MutableLiveData<Throwable?>(null)

    val error: LiveData<Throwable?> = _error

    private val _loading = MutableLiveData<Boolean>(false)

    val loading: LiveData<Boolean> = _loading

    fun loadSingleGame(slug: String, key: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.loadSingleGame(slug, key)
            _loading.value = false
            _error.value = result.exceptionOrNull()
            _game.value = result.getOrNull()
        }
    }
}