package mobappdev.example.sportsense.bluetooth

import android.Manifest
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.PolarBleApiDefaultImpl
import com.polar.sdk.api.PolarBleApiCallback
import com.polar.sdk.api.model.PolarDeviceInfo
import com.polar.sdk.api.model.PolarHrData
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable

class BluetoothManager(private val context: Context) {

    val api: PolarBleApi = PolarBleApiDefaultImpl.defaultImplementation(
        context,
        PolarBleApi.ALL_FEATURES // FIX: Ersätter `FEATURE_ALL`
    )

    private var heartRateDisposable: Disposable? = null

    init {
        api.setApiCallback(object : PolarBleApiCallback() {

            override fun blePowerStateChanged(powered: Boolean) {
                Log.d("BluetoothManager", "BLE Power: $powered")
            }

            override fun deviceConnected(polarDeviceInfo: PolarDeviceInfo) {
                Log.d("BluetoothManager", "CONNECTED: ${polarDeviceInfo.deviceId}")
            }

            override fun deviceDisconnected(polarDeviceInfo: PolarDeviceInfo) {
                Log.d("BluetoothManager", "DISCONNECTED: ${polarDeviceInfo.deviceId}")
            }

            override fun batteryLevelReceived(identifier: String, level: Int) {
                Log.d("BluetoothManager", "BATTERY LEVEL: $level%")
            }
        })
    }

    fun connectToDevice(deviceId: String) {
        api.connectToDevice(deviceId)
    }

    /*
    fun startHeartRateStreaming(deviceId: String): Observable<Int> {
        return api.startHrStreaming(deviceId) // FIX: Använder `.toObservable()`
            .toObservable()
            .map { hrSample: PolarHrData -> hrSample.hr } // FIX: Specifierar datatypen korrekt
            .doOnSubscribe { Log.d("BluetoothManager", "HR Stream started") }
    }

     */

    fun requestPermissions(activity: android.app.Activity, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT),
                requestCode
            )
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                requestCode
            )
        }
    }
}
