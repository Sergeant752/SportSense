package mobappdev.example.sportsense.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mobappdev.example.sportsense.data.SensorStorage
import mobappdev.example.sportsense.networking.SensorDataSource
import mobappdev.example.sportsense.utils.Result
import mobappdev.example.sportsense.data.SensorData

class SensorVM(application: Application) : AndroidViewModel(application) {

    private val _sensorData = MutableStateFlow(SensorData(0, 0, 0f, 0f, 0f, 0f, 0f, 0f))
    val sensorData: StateFlow<SensorData> = _sensorData.asStateFlow()

    fun fetchNewSensorData() {
        viewModelScope.launch {
            try {
                val result = SensorDataSource.fetchSensorData()
                if (result is Result.Success) {
                    _sensorData.value = result.data
                    SensorStorage.saveSensorData(getApplication(), result.data)
                }
            } catch (e: Exception) {
                // Log error
            }
        }
    }

    init {
        getSavedSensorData()
    }

    private fun getSavedSensorData() {
        val storedData = SensorStorage.getSavedSensorData(getApplication())
        if (storedData != null) {
            _sensorData.value = storedData
        }
    }
}