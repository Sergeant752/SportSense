package mobappdev.example.sportsense.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.StateFlow
import mobappdev.example.sportsense.bluetooth.BluetoothManager
import mobappdev.example.sportsense.data.SensorData
import mobappdev.example.sportsense.data.SensorStorage
import mobappdev.example.sportsense.data.SensorDao
import mobappdev.example.sportsense.data.SensorDatabase


class SensorVM(application: Application) : AndroidViewModel(application) {

    private val bluetoothManager = BluetoothManager(application)
    private val dao: SensorDao = SensorDatabase.getDatabase(application).sensorDao()

    val devices: StateFlow<List<String>> = bluetoothManager.scannedDevices
    val heartRate: StateFlow<Int> = bluetoothManager.heartRate
    val connectedDevices: StateFlow<List<String>> = bluetoothManager.connectedDevices
    val sensorData: StateFlow<SensorData> = bluetoothManager.sensorData


    fun startScanning() {
        bluetoothManager.startScan()
    }

    fun connectToDevice(deviceId: String) {
        bluetoothManager.connectToDevice(deviceId)
    }

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
        bluetoothManager.stopAllMeasurements()
    }

    fun disconnectDevice(deviceId: String) {
        bluetoothManager.disconnectDevice(deviceId)
    }

    suspend fun exportDataAsCSV(context: Context): String {
        val data = dao.getAllSensorData()  // Hämta data från Room
        return SensorStorage.exportSensorDataAsCSV(context, data)
    }

    suspend fun exportDataAsJSON(context: Context): String {
        val data = dao.getAllSensorData()  // Hämta data från Room
        return SensorStorage.exportSensorDataAsJSON(context, data)
    }

    suspend fun getAllSensorData(): List<SensorData> {
        return dao.getAllSensorData()
    }

}
