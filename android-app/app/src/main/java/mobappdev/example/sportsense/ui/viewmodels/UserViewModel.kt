package mobappdev.example.sportsense.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mobappdev.example.sportsense.data.SensorDatabase
import mobappdev.example.sportsense.data.User
import mobappdev.example.sportsense.utils.PasswordUtils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = SensorDatabase.getDatabase(application).userDao()

    // 🔹 Skapa en StateFlow för att hålla inloggningsstatus
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    fun registerUser(username: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val existingUser = userDao.getUserByUsername(username)
            if (existingUser != null) {
                onError("Username already taken!")
            } else {
                val hashedPassword = PasswordUtils.hashPassword(password)
                userDao.insertUser(User(username = username, hashedPassword = hashedPassword))
                _isLoggedIn.value = true // 🔹 Uppdatera status till inloggad
                onSuccess()
            }
        }
    }

    fun loginUser(username: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val user = userDao.getUserByUsername(username)
            if (user != null && PasswordUtils.verifyPassword(password, user.hashedPassword)) {
                _isLoggedIn.value = true // 🔹 Uppdatera status till inloggad
                onSuccess()
            } else {
                onError("Invalid username/password!")
            }
        }
    }

    fun logoutUser() {
        _isLoggedIn.value = false // 🔹 Uppdatera status till utloggad
    }
}
