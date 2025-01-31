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

    val devices: StateFlow<List<String>> = bluetoothManager.scannedDevices
    val heartRate: StateFlow<Int> = bluetoothManager.heartRate
    val connectedDevice: StateFlow<String?> = bluetoothManager.connectedDevice

    fun startScanning() {
        bluetoothManager.startScan()
    }

    fun connectToDevice(deviceId: String) {
        bluetoothManager.connectToDevice(deviceId)
    }

    fun startHeartRateMeasurement() {
        bluetoothManager.startHeartRateMeasurement()
    }

    fun stopHeartRateMeasurement() {
        bluetoothManager.stopHeartRateMeasurement()
    }
}
