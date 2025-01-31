package mobappdev.example.sportsense.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mobappdev.example.sportsense.bluetooth.BluetoothManager
import mobappdev.example.sportsense.data.SensorData

class SensorVM(application: Application) : AndroidViewModel(application) {

    private val bluetoothManager = BluetoothManager(application)

    private val _sensorData = MutableStateFlow(SensorData(0, 0, 0f, 0f, 0f, 0f, 0f, 0f))
    val sensorData: StateFlow<SensorData> = _sensorData.asStateFlow()

    private val _devices = MutableStateFlow<List<String>>(emptyList()) // Lista för enheter
    val devices: StateFlow<List<String>> = _devices.asStateFlow()

    fun startScanning() {
        bluetoothManager.api.searchForDevice()
            .subscribe(
                { deviceInfo ->
                    val newDevice = "${deviceInfo.name} (${deviceInfo.deviceId})"
                    _devices.value = _devices.value + newDevice
                    Log.d("SensorVM", "Hittade enhet: $newDevice")
                },
                { error -> Log.e("SensorVM", "Scanning error: ${error.message}") }
            )
    }

    fun connectToDevice(deviceId: String) {
        bluetoothManager.connectToDevice(deviceId)
    }
}