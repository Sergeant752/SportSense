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
    val connectedDevice: StateFlow<String?> = bluetoothManager.connectedDevice
    val sensorData: StateFlow<SensorData> = bluetoothManager.sensorData

    fun startScanning() {
        bluetoothManager.startScan()
    }

    fun connectToDevice(deviceId: String) {
        bluetoothManager.connectToDevice(deviceId)
    }

    fun startHeartRateMeasurement() {
        bluetoothManager.startHeartRateMeasurement()
    }

    fun startAccelerometerMeasurement() {
        bluetoothManager.startAccelerometerMeasurement()
    }

    fun startGyroscopeMeasurement() {
        bluetoothManager.startGyroscopeMeasurement()
    }

    fun stopHeartRateMeasurement() {
        bluetoothManager.stopHeartRateMeasurement()
    }

    fun stopAccelerometerMeasurement() {
        bluetoothManager.stopAccelerometerMeasurement()
    }

    fun stopGyroscopeMeasurement() {
        bluetoothManager.stopGyroscopeMeasurement()
    }

    fun stopAllMeasurements() {
        bluetoothManager.stopMeasurements()
    }
}
