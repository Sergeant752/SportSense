package mobappdev.example.sportsense.bluetooth

import android.content.Context
import android.util.Log
import com.polar.androidcommunications.api.ble.model.DisInfo
import com.polar.sdk.api.*
import com.polar.sdk.api.model.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mobappdev.example.sportsense.data.SensorData
import mobappdev.example.sportsense.data.SensorStorage

class BluetoothManager(private val context: Context) {

    private val api: PolarBleApi = PolarBleApiDefaultImpl.defaultImplementation(
        context,
        setOf(
            PolarBleApi.PolarBleSdkFeature.FEATURE_HR,
            PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_ONLINE_STREAMING,
            PolarBleApi.PolarBleSdkFeature.FEATURE_DEVICE_INFO
        )
    )

    private var hrDisposables = mutableMapOf<String, Disposable>()
    private var accDisposables = mutableMapOf<String, Disposable>()
    private var gyroDisposables = mutableMapOf<String, Disposable>()

    private val _heartRate = MutableStateFlow(0)
    val heartRate: StateFlow<Int> = _heartRate

    private val _sensorData = MutableStateFlow(
        SensorData(0, 0, 0, 0f, 0f, 0f, 0f, 0f, 0f)
    )
    val sensorData: StateFlow<SensorData> = _sensorData

    private val _connectedDevices = MutableStateFlow<List<String>>(emptyList())
    val connectedDevices: StateFlow<List<String>> = _connectedDevices

    private val _scannedDevices = MutableStateFlow<List<String>>(emptyList())
    val scannedDevices: StateFlow<List<String>> = _scannedDevices

    private val backgroundScope = CoroutineScope(Dispatchers.IO)

    init {
        api.setApiCallback(object : PolarBleApiCallback() {
            override fun deviceConnected(polarDeviceInfo: PolarDeviceInfo) {
                Log.d("BluetoothManager", "Connected: ${polarDeviceInfo.deviceId}")
                _connectedDevices.value += polarDeviceInfo.deviceId
                fetchAvailableGyroSettings(polarDeviceInfo.deviceId)
            }

            override fun deviceDisconnected(polarDeviceInfo: PolarDeviceInfo) {
                Log.d("BluetoothManager", "Disconnected: ${polarDeviceInfo.deviceId}")
                _connectedDevices.value -= polarDeviceInfo.deviceId
            }

            override fun disInformationReceived(identifier: String, disInfo: DisInfo) {
                Log.d("BluetoothManager", "Device Info: $identifier, Manufacturer: ${disInfo.value}")
            }

            override fun bleSdkFeatureReady(identifier: String, feature: PolarBleApi.PolarBleSdkFeature) {
                Log.d("BluetoothManager", "Feature ready: $feature on $identifier")
                if (feature == PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_ONLINE_STREAMING) {
                    fetchAvailableGyroSettings(identifier)
                }
            }
        })
    }

    fun startScan() {
        api.searchForDevice()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { deviceInfo ->
                    _scannedDevices.value += "${deviceInfo.name} (${deviceInfo.deviceId})"
                    Log.d("BluetoothManager", "Found device: ${deviceInfo.deviceId}")
                },
                { error -> Log.e("BluetoothManager", "Scan error: ${error.message}") }
            )
    }

    fun connectToDevice(deviceId: String) {
        if (_connectedDevices.value.contains(deviceId)) {
            Log.d("BluetoothManager", "Device $deviceId is already connected.")
            return
        }
        api.connectToDevice(deviceId)
        _connectedDevices.value += deviceId
    }

    fun disconnectDevice(deviceId: String) {
        stopAllMeasurements()
        api.disconnectFromDevice(deviceId)
        _connectedDevices.value -= deviceId
        Log.d("BluetoothManager", "Disconnected from: $deviceId")
    }

    fun startHeartRateMeasurement(deviceId: String) {
        hrDisposables[deviceId] = api.startHrStreaming(deviceId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { hrData ->
                    backgroundScope.launch {
                        hrData.samples.forEach { sample ->
                            _heartRate.value = sample.hr
                            saveSensorData(heartRate = sample.hr)
                        }
                    }
                },
                { error -> Log.e("BluetoothManager", "HR stream failed: $error") }
            )
    }

    fun startAccelerometerMeasurement(deviceId: String) {
        accDisposables[deviceId] = api.startAccStreaming(deviceId, getDefaultSensorSettings())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { accData ->
                    backgroundScope.launch {
                        accData.samples.forEach { sample ->
                            val tag = when {
                                detectKnocking(sample.z.toFloat()) -> "Knackning"
                                detectStationary(sample.x.toFloat(), sample.y.toFloat(), sample.z.toFloat()) -> "Stillastående"
                                else -> null
                            }

                            saveSensorData(
                                accelX = sample.x.toFloat(),
                                accelY = sample.y.toFloat(),
                                accelZ = sample.z.toFloat(),
                                tag = tag
                            )
                        }
                    }
                },
                { error -> Log.e("BluetoothManager", "ACC stream failed: $error") }
            )
    }

    fun startGyroscopeMeasurement(deviceId: String) {
        gyroDisposables[deviceId] = api.startGyroStreaming(deviceId, getGyroSensorSettings())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { gyroData ->
                    backgroundScope.launch {
                        gyroData.samples.forEach { sample ->
                            saveSensorData(
                                gyroX = sample.x,
                                gyroY = sample.y,
                                gyroZ = sample.z,
                                tag = if (detectRotation(sample.z)) "Vridning" else null
                            )
                        }
                    }
                },
                { error -> Log.e("BluetoothManager", "GYRO stream failed: $error") }
            )
    }

    private suspend fun saveSensorData(
        heartRate: Int? = null,
        accelX: Float? = null,
        accelY: Float? = null,
        accelZ: Float? = null,
        gyroX: Float? = null,
        gyroY: Float? = null,
        gyroZ: Float? = null,
        tag: String? = null
    ) {
        val updatedData = SensorData(
            timestamp = System.currentTimeMillis(),
            heartRate = heartRate ?: _sensorData.value.heartRate,
            accelX = accelX ?: _sensorData.value.accelX,
            accelY = accelY ?: _sensorData.value.accelY,
            accelZ = accelZ ?: _sensorData.value.accelZ,
            gyroX = gyroX ?: _sensorData.value.gyroX,
            gyroY = gyroY ?: _sensorData.value.gyroY,
            gyroZ = gyroZ ?: _sensorData.value.gyroZ,
            movementDetected = _sensorData.value.movementDetected ?: "No movement detected"  // 🔹 Använd `movementDetected` istället för `tag`
        )

        _sensorData.value = updatedData
        SensorStorage.saveSensorData(context, updatedData)
    }

    fun stopHeartRateMeasurement(deviceId: String) {
        hrDisposables[deviceId]?.dispose() // Stoppar HR-strömmen
        hrDisposables.remove(deviceId)     // Tar bort referensen
        Log.d("BluetoothManager", "HR-mätning stoppad för enhet: $deviceId")
    }

    fun stopAccelerometerMeasurement(deviceId: String) {
        accDisposables[deviceId]?.dispose() // Stoppar ACC-strömmen
        accDisposables.remove(deviceId)     // Tar bort referensen
        Log.d("BluetoothManager", "ACC-mätning stoppad för enhet: $deviceId")
    }

    fun stopGyroscopeMeasurement(deviceId: String) {
        gyroDisposables[deviceId]?.dispose() // Stoppar GYRO-strömmen
        gyroDisposables.remove(deviceId)     // Tar bort referensen
        Log.d("BluetoothManager", "GYRO-mätning stoppad för enhet: $deviceId")
    }


    private fun detectKnocking(accelZ: Float) = accelZ > 15
    private fun detectRotation(gyroZ: Float) = kotlin.math.abs(gyroZ) > 200
    private fun detectStationary(accelX: Float, accelY: Float, accelZ: Float) =
        kotlin.math.abs(accelX) < 0.5f && kotlin.math.abs(accelY) < 0.5f && kotlin.math.abs(accelZ) < 0.5f

    fun stopAllMeasurements() {
        _connectedDevices.value.forEach { deviceId ->
            hrDisposables[deviceId]?.dispose()
            accDisposables[deviceId]?.dispose()
            gyroDisposables[deviceId]?.dispose()
        }
        Log.d("BluetoothManager", "Stopped all measurements")
    }

    private fun getDefaultSensorSettings() = PolarSensorSetting(
        mapOf(
            PolarSensorSetting.SettingType.SAMPLE_RATE to 52,
            PolarSensorSetting.SettingType.RESOLUTION to 16,
            PolarSensorSetting.SettingType.RANGE to 8,
            PolarSensorSetting.SettingType.CHANNELS to 3
        )
    )

    private fun getGyroSensorSettings() = PolarSensorSetting(
        mapOf(
            PolarSensorSetting.SettingType.SAMPLE_RATE to 52,
            PolarSensorSetting.SettingType.RESOLUTION to 16,
            PolarSensorSetting.SettingType.RANGE to 2000,
            PolarSensorSetting.SettingType.CHANNELS to 3
        )
    )

    fun fetchAvailableGyroSettings(deviceId: String) {
        api.startGyroStreaming(deviceId, getGyroSensorSettings())
        api.requestStreamSettings(deviceId, PolarBleApi.PolarDeviceDataType.GYRO)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { settings -> Log.d("BluetoothManager", "Available GYRO settings: ${settings.settings}") },
                { error -> Log.e("BluetoothManager", "Failed to get GYRO settings: $error") }
            )
    }
}
