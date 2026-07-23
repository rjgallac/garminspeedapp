package com.example.garminspeedapp

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@SuppressLint("MissingPermission")
class BluetoothScanner(private val context: Context) {

    private val bluetoothAdapter: BluetoothAdapter? =
        BluetoothAdapter.getDefaultAdapter()

    private val _discoveredDevices = MutableStateFlow<Set<BluetoothDevice>>(emptySet())
    val discoveredDevices = _discoveredDevices.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning = _isScanning.asStateFlow()

    private var scanCallback: ScanCallback? = null

    fun startScan(stopOnDiscovery: Boolean = false) {
        if (bluetoothAdapter == null) {
            android.util.Log.e("BluetoothScanner", "Bluetooth adapter is null")
            return
        }
        if (!bluetoothAdapter.isEnabled) {
            android.util.Log.e("BluetoothScanner", "Bluetooth is disabled")
            return
        }

        val scanner = bluetoothAdapter.bluetoothLeScanner
        if (scanner == null) {
            android.util.Log.e("BluetoothScanner", "Bluetooth LE scanner is null")
            return
        }

        android.util.Log.d("BluetoothScanner", "Starting scan...")
        _isScanning.value = true
        _discoveredDevices.value = emptySet()

        scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                android.util.Log.d("BluetoothScanner", "Found device: ${result.device.name ?: "Unknown"} [${result.device.address}]")
                _discoveredDevices.update { it + result.device }
                
                if (stopOnDiscovery) {
                    stopScan()
                }
            }

            override fun onScanFailed(errorCode: Int) {
                android.util.Log.e("BluetoothScanner", "Scan failed with error code: $errorCode")
                _isScanning.value = false
                scanCallback = null
            }
        }

        scanner.startScan(scanCallback)
    }

    fun stopScan() {
        android.util.Log.d("BluetoothScanner", "Stopping scan...")
        val scanner = bluetoothAdapter?.bluetoothLeScanner ?: return
        scanCallback?.let {
            scanner.stopScan(it)
        }
        _isScanning.value = false
        scanCallback = null
    }
}
