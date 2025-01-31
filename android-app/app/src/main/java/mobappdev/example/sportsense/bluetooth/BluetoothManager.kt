package mobappdev.example.sportsense.bluetooth

import android.content.Context
import android.util.Log
import com.polar.androidcommunications.api.ble.model.DisInfo
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.PolarBleApiDefaultImpl
import com.polar.sdk.api.PolarBleApiCallback
import com.polar.sdk.api.model.PolarDeviceInfo
import com.polar.sdk.api.model.PolarHrData
import com.polar.sdk.api.model.*
import com.polar.sdk.api.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BluetoothManager(private val context: Context) {

    private val api: PolarBleApi = PolarBleApiDefaultImpl.defaultImplementation(
        context,
        setOf(
            PolarBleApi.PolarBleSdkFeature.FEATURE_HR,               // Hjärtfrekvensmätning
            PolarBleApi.PolarBleSdkFeature.FEATURE_DEVICE_INFO,      // Enhetsinformation
            PolarBleApi.PolarBleSdkFeature.FEATURE_BATTERY_INFO,     // Batteristatus
            PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_ONLINE_STREAMING // Online streaming
        )
    )

    private var hrDisposable: Disposable? = null
    private val _heartRate = MutableStateFlow(0)
    val heartRate: StateFlow<Int> = _heartRate

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
                Log.d("BluetoothManager", "Device Info Received: $identifier, Manufacturer: ${disInfo.toString()}")
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
        val deviceId = _connectedDevice.value
        if (deviceId == null) {
            Log.e("BluetoothManager", "No device connected")
            return
        }

        hrDisposable = api.startHrStreaming(deviceId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { hrData: PolarHrData ->
                    for (sample in hrData.samples) {
                        _heartRate.value = sample.hr
                        Log.d("BluetoothManager", "HR: ${sample.hr}")
                    }
                },
                { error: Throwable ->
                    Log.e("BluetoothManager", "HR stream failed: $error")
                }
            )
    }

    fun stopHeartRateMeasurement() {
        hrDisposable?.dispose()
        _heartRate.value = 0
        Log.d("BluetoothManager", "HR measurement stopped")
    }
}
