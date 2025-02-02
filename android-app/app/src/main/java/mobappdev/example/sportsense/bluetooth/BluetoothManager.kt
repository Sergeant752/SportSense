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

    private val _sensorData = MutableStateFlow(SensorData(0, 0, 0f, 0f, 0f, 0f, 0f, 0f))
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
                _connectedDevices.value = _connectedDevices.value + polarDeviceInfo.deviceId
                fetchAvailableGyroSettings(polarDeviceInfo.deviceId)
            }

            override fun deviceDisconnected(polarDeviceInfo: PolarDeviceInfo) {
                Log.d("BluetoothManager", "Disconnected: ${polarDeviceInfo.deviceId}")
                _connectedDevices.value = _connectedDevices.value - polarDeviceInfo.deviceId
            }

            override fun disInformationReceived(identifier: String, disInfo: DisInfo) {
                Log.d("BluetoothManager", "Device Info Received: $identifier, Manufacturer: ${disInfo.value}")
            }

            override fun bleSdkFeatureReady(identifier: String, feature: PolarBleApi.PolarBleSdkFeature) {
                Log.d("BluetoothManager", "Feature ready: $feature on device $identifier")
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
                    val newDevice = "${deviceInfo.name} (${deviceInfo.deviceId})"
                    _scannedDevices.value = _scannedDevices.value + newDevice
                    Log.d("BluetoothManager", "Found device: $newDevice")
                },
                { error -> Log.e("BluetoothManager", "Scan error: ${error.message}") }
            )
    }

    fun connectToDevice(deviceId: String) {
        api.connectToDevice(deviceId)
        _connectedDevices.value = _connectedDevices.value + deviceId
    }

    fun disconnectDevice(deviceId: String) {
        stopHeartRateMeasurement(deviceId)
        stopAccelerometerMeasurement(deviceId)
        stopGyroscopeMeasurement(deviceId)
        api.disconnectFromDevice(deviceId) // 🔗 Kopplar bort enheten via API
        _connectedDevices.value = _connectedDevices.value.filterNot { it == deviceId } // Uppdaterar state
        Log.d("BluetoothManager", "Disconnected from device: $deviceId")
    }


    fun startHeartRateMeasurement(deviceId: String) {
        hrDisposables[deviceId] = api.startHrStreaming(deviceId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { hrData: PolarHrData ->
                    backgroundScope.launch {
                        hrData.samples.forEach { sample ->
                            _heartRate.value = sample.hr
                            saveSensorData(heartRate = sample.hr)
                            Log.d("BluetoothManager", "HR: ${sample.hr}")
                        }
                    }
                },
                { error -> Log.e("BluetoothManager", "HR stream failed: $error") }
            )
    }

    private fun detectKnocking(accelX: Float, accelY: Float, accelZ: Float): Boolean {
        return accelZ > 15 // Tröskelvärde för att detektera knackningar
    }

    fun startAccelerometerMeasurement(deviceId: String) {
        accDisposables[deviceId] = api.startAccStreaming(deviceId, getDefaultSensorSettings())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { accData: PolarAccelerometerData ->
                    backgroundScope.launch {
                        accData.samples.forEach { sample ->
                            val knockingDetected = detectKnocking(sample.x.toFloat(), sample.y.toFloat(), sample.z.toFloat())

                            val tag = if (knockingDetected) "Knackning" else null
                            val updatedData = SensorData(
                                timestamp = System.currentTimeMillis(),
                                heartRate = _heartRate.value,
                                accelX = sample.x.toFloat(),
                                accelY = sample.y.toFloat(),
                                accelZ = sample.z.toFloat(),
                                gyroX = _sensorData.value.gyroX,
                                gyroY = _sensorData.value.gyroY,
                                gyroZ = _sensorData.value.gyroZ,
                                tag = tag
                            )

                            _sensorData.value = updatedData
                            SensorStorage.saveSensorData(context, updatedData)

                            if (knockingDetected) {
                                Log.d("BluetoothManager", "Knackning upptäckt!")
                            }
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
                { gyroData: PolarGyroData ->
                    backgroundScope.launch {
                        gyroData.samples.forEach { sample ->
                            saveSensorData(
                                gyroX = sample.x,
                                gyroY = sample.y,
                                gyroZ = sample.z
                            )
                            Log.d("BluetoothManager", "GYRO: X=${sample.x}, Y=${sample.y}, Z=${sample.z}")
                        }
                    }
                },
                { error ->
                    if (error is com.polar.sdk.api.errors.PolarNotificationNotEnabled) {
                        Log.d("BluetoothManager", "Trying to enable notifications for gyro...")
                        enableGyroNotification(deviceId) // ✅ Lägger till anropet igen
                    } else {
                        Log.e("BluetoothManager", "GYRO stream failed: $error")
                    }
                }
            )
    }

    private fun enableGyroNotification(deviceId: String) {
        api.startGyroStreaming(deviceId, getGyroSensorSettings())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { Log.d("BluetoothManager", "Gyro notification enabled for device $deviceId") },
                { error -> Log.e("BluetoothManager", "Failed to enable gyro notification: $error") }
            )
    }

    fun stopHeartRateMeasurement(deviceId: String) {
        hrDisposables[deviceId]?.dispose()
        hrDisposables.remove(deviceId)
        Log.d("BluetoothManager", "HR measurement stopped for device: $deviceId")
    }

    fun stopAccelerometerMeasurement(deviceId: String) {
        accDisposables[deviceId]?.dispose()
        accDisposables.remove(deviceId)
        Log.d("BluetoothManager", "Accelerometer measurement stopped for device: $deviceId")
    }

    fun stopGyroscopeMeasurement(deviceId: String) {
        gyroDisposables[deviceId]?.dispose()
        gyroDisposables.remove(deviceId)
        Log.d("BluetoothManager", "Gyroscope measurement stopped for device: $deviceId")
    }

    fun stopMeasurements() {
        _connectedDevices.value.forEach { deviceId ->
            stopHeartRateMeasurement(deviceId)
            stopAccelerometerMeasurement(deviceId)
            stopGyroscopeMeasurement(deviceId)
        }
        Log.d("BluetoothManager", "All measurements stopped for all devices")
    }

    private fun saveSensorData(
        heartRate: Int? = null,
        accelX: Float? = null,
        accelY: Float? = null,
        accelZ: Float? = null,
        gyroX: Float? = null,
        gyroY: Float? = null,
        gyroZ: Float? = null
    ) {
        val currentData = _sensorData.value

        val updatedData = SensorData(
            timestamp = System.currentTimeMillis(),
            heartRate = heartRate ?: currentData.heartRate,
            accelX = accelX ?: currentData.accelX,
            accelY = accelY ?: currentData.accelY,
            accelZ = accelZ ?: currentData.accelZ,
            gyroX = gyroX ?: currentData.gyroX,
            gyroY = gyroY ?: currentData.gyroY,
            gyroZ = gyroZ ?: currentData.gyroZ
        )

        _sensorData.value = updatedData
        SensorStorage.saveSensorData(context, updatedData)
    }

    private fun getDefaultSensorSettings(): PolarSensorSetting {
        return PolarSensorSetting(
            mapOf(
                PolarSensorSetting.SettingType.SAMPLE_RATE to 52,
                PolarSensorSetting.SettingType.RESOLUTION to 16,
                PolarSensorSetting.SettingType.RANGE to 8,
                PolarSensorSetting.SettingType.CHANNELS to 3
            )
        )
    }

    private fun getGyroSensorSettings(): PolarSensorSetting {
        return PolarSensorSetting(
            mapOf(
                PolarSensorSetting.SettingType.SAMPLE_RATE to 52,
                PolarSensorSetting.SettingType.RESOLUTION to 16,
                PolarSensorSetting.SettingType.RANGE to 2000,
                PolarSensorSetting.SettingType.CHANNELS to 3
            )
        )
    }

    fun fetchAvailableGyroSettings(deviceId: String) {
        api.requestStreamSettings(deviceId, PolarBleApi.PolarDeviceDataType.GYRO)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { settings ->
                    Log.d("BluetoothManager", "Available GYRO settings: ${settings.settings}")
                },
                { error ->
                    Log.e("BluetoothManager", "Failed to get GYRO settings: $error")
                }
            )
    }
}
