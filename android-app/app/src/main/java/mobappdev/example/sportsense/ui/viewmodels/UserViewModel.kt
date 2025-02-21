package mobappdev.example.sportsense.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mobappdev.example.sportsense.data.SensorDatabase
import mobappdev.example.sportsense.data.User
import mobappdev.example.sportsense.utils.PasswordUtils

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = SensorDatabase.getDatabase(application).userDao()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    fun registerUser(username: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val existingUser = userDao.getUserByUsername(username)
            if (existingUser != null) {
                onError("Username already taken!")
            } else {
                val hashedPassword = PasswordUtils.hashPassword(password)
                val newUser = User(username = username, hashedPassword = hashedPassword)
                userDao.insertUser(newUser)

                _isLoggedIn.value = true
                _currentUser.value = newUser

                onSuccess()
            }
        }
    }

    fun loginUser(username: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val user = userDao.getUserByUsername(username)
            if (user != null && PasswordUtils.verifyPassword(password, user.hashedPassword)) {
                _isLoggedIn.value = true
                _currentUser.value = user

                onSuccess()
            } else {
                onError("Invalid username/password!")
            }
        }
    }

    fun logoutUser() {
        _isLoggedIn.value = false
        _currentUser.value = null
    }

    fun getCurrentUser(): String? {
        return _currentUser.value?.username
    }
}