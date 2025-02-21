package mobappdev.example.sportsense.ui.viewmodels

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import mobappdev.example.sportsense.bluetooth.BluetoothManager
import mobappdev.example.sportsense.data.SensorData
import mobappdev.example.sportsense.data.SensorStorage
import mobappdev.example.sportsense.data.SensorDao
import mobappdev.example.sportsense.data.SensorDatabase
import mobappdev.example.sportsense.network.RetrofitInstance
import java.io.File

class SensorVM(application: Application) : AndroidViewModel(application) {

    private val bluetoothManager = BluetoothManager(application)
    private val dao: SensorDao = SensorDatabase.getDatabase(application).sensorDao()

    val devices: StateFlow<List<String>> = bluetoothManager.scannedDevices
    val heartRate: StateFlow<Int> = bluetoothManager.heartRate
    val connectedDevices: StateFlow<List<String>> = bluetoothManager.connectedDevices
    val sensorData: StateFlow<SensorData> = bluetoothManager.sensorData

    private val _currentConnectedDevice = MutableStateFlow<String?>(null)
    val currentConnectedDevice: StateFlow<String?> = _currentConnectedDevice

    fun startScanning() {
        bluetoothManager.startScan()
    }

    fun connectToDevice(deviceId: String) {
        bluetoothManager.connectToDevice(deviceId)
        _currentConnectedDevice.value = deviceId
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
        _currentConnectedDevice.value = null
    }

    suspend fun exportDataAsCSV(context: Context): String {
        val data = dao.getAllSensorData()
        return SensorStorage.exportSensorDataAsCSV(context, data)
    }

    suspend fun exportDataAsJSON(context: Context): String {
        val data = dao.getAllSensorData()
        return SensorStorage.exportSensorDataAsJSON(context, data)
    }

    suspend fun sendDataToPython(context: Context) {
        val data = getAllSensorData()
        try {
            val response = RetrofitInstance.api.analyzeData(data)
            if (response.isSuccessful && response.body() != null) {
                val analyzedData = response.body()
                analyzedData?.let { results ->
                    for (record in results) {
                        val analyzedRecord = SensorData(
                            timestamp = record.timestamp,
                            heartRate = record.heartRate,
                            accelX = record.accelX,
                            accelY = record.accelY,
                            accelZ = record.accelZ,
                            gyroX = record.gyroX,
                            gyroY = record.gyroY,
                            gyroZ = record.gyroZ,
                            movementDetected = record.movementDetected ?: "No movement"
                        )
                        dao.insertSensorData(analyzedRecord)
                    }
                }
                Toast.makeText(
                    context,
                    "Analyzed data saved! ${analyzedData?.size} records processed.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(context, "Error: ${response.code()} - ${response.message()}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to send data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun fetchAnalyzedData(context: Context) {
        try {
            val response = RetrofitInstance.api.getAnalysis()
            if (response.isSuccessful && response.body() != null) {
                val analyzedData = response.body()
                analyzedData?.let { results ->
                    for (record in results) {
                        val analyzedRecord = SensorData(
                            timestamp = record.timestamp,
                            heartRate = record.heartRate,
                            accelX = record.accelX,
                            accelY = record.accelY,
                            accelZ = record.accelZ,
                            gyroX = record.gyroX,
                            gyroY = record.gyroY,
                            gyroZ = record.gyroZ,
                            movementDetected = record.movementDetected ?: "No movement"
                        )
                        dao.insertSensorData(analyzedRecord)
                    }
                }
                Toast.makeText(
                    context,
                    "Fetched and saved analyzed data!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(context, "Error: ${response.code()} - ${response.message()}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to fetch analysis: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }



    suspend fun downloadModel(context: Context) {
        try {
            val response = RetrofitInstance.api.downloadModel().execute()

            if (response.isSuccessful && response.body() != null) {
                val file = File(context.filesDir, "model.pkl")
                response.body()?.byteStream()?.use { inputStream ->
                    file.outputStream().use { output ->
                        inputStream.copyTo(output)
                    }
                }

                Toast.makeText(context, "ML model downloaded successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to download model: ${response.code()}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error downloading model: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    suspend fun getAllSensorData(): List<SensorData> {
        return dao.getAllSensorData()
    }
}