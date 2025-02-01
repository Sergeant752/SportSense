package mobappdev.example.sportsense.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.StateFlow
import mobappdev.example.sportsense.bluetooth.BluetoothManager
import mobappdev.example.sportsense.data.SensorData

class SensorVM(application: Application) : AndroidViewModel(application) {

    private val bluetoothManager = BluetoothManager(application)

    val devices: StateFlow<List<String>> = bluetoothManager.scannedDevices
    val heartRate: StateFlow<Int> = bluetoothManager.heartRate
    val connectedDevices: StateFlow<List<String>> = bluetoothManager.connectedDevices  // ✅ Lagt till för multisensorhantering
    val sensorData: StateFlow<SensorData> = bluetoothManager.sensorData

    fun startScanning() {
        bluetoothManager.startScan()
    }

    fun connectToDevice(deviceId: String) {
        bluetoothManager.connectToDevice(deviceId)
    }

    // ✅ Uppdaterade metoder för att stödja flera enheter
    fun startHeartRateMeasurement(deviceId: String) {
        bluetoothManager.startHeartRateMeasurement(deviceId)
    }

    fun startAccelerometerMeasurement(deviceId: String) {
        bluetoothManager.startAccelerometerMeasurement(deviceId)
    }

    fun startGyroscopeMeasurement(deviceId: String) {
        bluetoothManager.startGyroscopeMeasurement(deviceId)
    }

    fun stopHeartRateMeasurement(deviceId: String) {
        bluetoothManager.stopHeartRateMeasurement(deviceId)
    }

    fun stopAccelerometerMeasurement(deviceId: String) {
        bluetoothManager.stopAccelerometerMeasurement(deviceId)
    }

    fun stopGyroscopeMeasurement(deviceId: String) {
        bluetoothManager.stopGyroscopeMeasurement(deviceId)
    }

    fun stopAllMeasurements() {
        bluetoothManager.stopMeasurements()
    }
}
