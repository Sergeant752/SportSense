package mobappdev.example.sportsense.bluetooth

import android.content.Context
import android.util.Log
import com.polar.androidcommunications.api.ble.model.DisInfo
import com.polar.sdk.api.*
import com.polar.sdk.api.model.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import mobappdev.example.sportsense.data.SensorData
import mobappdev.example.sportsense.data.SensorStorage

class BluetoothManager(private val context: Context) {

    private val api: PolarBleApi = PolarBleApiDefaultImpl.defaultImplementation(
        context,
        setOf(
            PolarBleApi.PolarBleSdkFeature.FEATURE_HR,
            PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_ONLINE_STREAMING
        )
    )

    private var hrDisposable: Disposable? = null
    private var accDisposable: Disposable? = null
    private var gyroDisposable: Disposable? = null

    private val _heartRate = MutableStateFlow(0)
    val heartRate: StateFlow<Int> = _heartRate

    private val _sensorData = MutableStateFlow(SensorData(0, 0, 0f, 0f, 0f, 0f, 0f, 0f))
    val sensorData: StateFlow<SensorData> = _sensorData

    private val _connectedDevice = MutableStateFlow<String?>(null)
    val connectedDevice: StateFlow<String?> = _connectedDevice

    private val _scannedDevices = MutableStateFlow<List<String>>(emptyList())
    val scannedDevices: StateFlow<List<String>> = _scannedDevices

    init {
        api.setApiCallback(object : PolarBleApiCallback() {
            override fun deviceConnected(polarDeviceInfo: PolarDeviceInfo) {
                Log.d("BluetoothManager", "Connected: ${polarDeviceInfo.deviceId}")
                _connectedDevice.value = polarDeviceInfo.deviceId
            }

            override fun deviceDisconnected(polarDeviceInfo: PolarDeviceInfo) {
                Log.d("BluetoothManager", "Disconnected: ${polarDeviceInfo.deviceId}")
                _connectedDevice.value = null
            }

            override fun disInformationReceived(identifier: String, disInfo: DisInfo) {
                Log.d("BluetoothManager", "Device Info Received: $identifier, Manufacturer: ${disInfo.value}")
            }

            override fun bleSdkFeatureReady(identifier: String, feature: PolarBleApi.PolarBleSdkFeature) {
                Log.d("BluetoothManager", "Feature ready: $feature on device $identifier")
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
    }

    fun startHeartRateMeasurement() {
        val deviceId = _connectedDevice.value ?: return

        hrDisposable = api.startHrStreaming(deviceId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { hrData: PolarHrData ->
                    hrData.samples.forEach { sample ->
                        _heartRate.value = sample.hr
                        saveSensorData(heartRate = sample.hr)
                        Log.d("BluetoothManager", "HR: ${sample.hr}")
                    }
                },
                { error -> Log.e("BluetoothManager", "HR stream failed: $error") }
            )
    }

    fun startAccelerometerMeasurement() {
        val deviceId = _connectedDevice.value ?: return
        accDisposable = api.startAccStreaming(deviceId, getDefaultSensorSettings())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { accData: PolarAccelerometerData ->
                    accData.samples.forEach { sample ->
                        saveSensorData(accelX = sample.x.toFloat(), accelY = sample.y.toFloat(), accelZ = sample.z.toFloat())
                        Log.d("BluetoothManager", "ACC: X=${sample.x}, Y=${sample.y}, Z=${sample.z}")
                    }
                },
                { error -> Log.e("BluetoothManager", "ACC stream failed: $error") }
            )
    }

    fun startGyroscopeMeasurement() {
        val deviceId = _connectedDevice.value ?: return
        gyroDisposable = api.startGyroStreaming(deviceId, getDefaultSensorSettings())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { gyroData: PolarGyroData ->
                    gyroData.samples.forEach { sample ->
                        saveSensorData(gyroX = sample.x.toFloat(), gyroY = sample.y.toFloat(), gyroZ = sample.z.toFloat())
                        Log.d("BluetoothManager", "GYRO: X=${sample.x}, Y=${sample.y}, Z=${sample.z}")
                    }
                },
                { error -> Log.e("BluetoothManager", "GYRO stream failed: $error") }
            )
    }

    fun stopHeartRateMeasurement() {
        hrDisposable?.dispose()
        Log.d("BluetoothManager", "HR measurement stopped")
    }

    fun stopAccelerometerMeasurement() {
        accDisposable?.dispose()
        Log.d("BluetoothManager", "Accelerometer measurement stopped")
    }

    fun stopGyroscopeMeasurement() {
        gyroDisposable?.dispose()
        Log.d("BluetoothManager", "Gyroscope measurement stopped")
    }

    fun stopMeasurements() {
        stopHeartRateMeasurement()
        stopAccelerometerMeasurement()
        stopGyroscopeMeasurement()
        Log.d("BluetoothManager", "All measurements stopped")
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
}
