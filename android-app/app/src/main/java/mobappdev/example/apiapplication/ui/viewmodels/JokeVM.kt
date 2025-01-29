package mobappdev.example.apiapplication.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mobappdev.example.apiapplication.data.JokeStorage
import mobappdev.example.apiapplication.networking.JokeDataSource
import mobappdev.example.apiapplication.utils.Result
import mobappdev.example.apiapplication.utils.localDateToString
import java.time.LocalDate


interface JokeViewModel {
    val joke: StateFlow<String>

    fun fetchNewJoke()
}

class JokeVM(
    application: Application
) : AndroidViewModel(application), JokeViewModel {

    private val _joke = MutableStateFlow("")
    override val joke: StateFlow<String>
        get() = _joke.asStateFlow()

    private val _jokeState = MutableStateFlow<Result<String>>(Result.Loading)
    val jokeState: StateFlow<Result<String>> = _jokeState

    init {
        getSavedJoke()
    }

    override fun fetchNewJoke() {
        viewModelScope.launch {
            _jokeState.value = Result.Loading
            try {
                val result = JokeDataSource.getRandomJoke()
                if (result is Result.Success) {
                    when(result.data.type){
                        "single" -> _joke.update { "${result.data.joke}" }
                        "twopart" -> _joke.update { "${result.data.setup}\n\n${result.data.delivery}" }
                    }
                    // Save joke
                    JokeStorage.saveJoke(getApplication<Application>().applicationContext, _joke.value, LocalDate.now())
                    _jokeState.value = Result.Success(result.data.type)
                } else {
                    _jokeState.value = Result.Error(Exception("Failed to fetch joke"))
                }
            } catch (e: Exception) {
                _jokeState.value = Result.Error(e)
            }
        }
    }

    private fun getSavedJoke(){
        val storedJoke = JokeStorage.getSavedJoke(getApplication<Application>().applicationContext)
        if (storedJoke[1] != localDateToString(LocalDate.now()) || (storedJoke[0] ?: "") == ""){
            fetchNewJoke()
        } else {
            _joke.update { storedJoke[0]?:"Something went wrong with saving\nPress Refresh to get your joke" }
        }
    }


}