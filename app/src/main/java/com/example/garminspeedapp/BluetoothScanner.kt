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

    fun startScan() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            return
        }

        val scanner = bluetoothAdapter.bluetoothLeScanner
        if (scanner == null) return

        _isScanning.value = true
        _discoveredDevices.value = emptySet()

        scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                _discoveredDevices.update { it + result.device }
            }

            override fun onScanFailed(errorCode: Int) {
                _isScanning.value = false
                scanCallback = null
            }
        }

        scanner.startScan(scanCallback)
    }

    fun stopScan() {
        val scanner = bluetoothAdapter?.bluetoothLeScanner ?: return
        scanCallback?.let {
            scanner.stopScan(it)
        }
        _isScanning.value = false
        scanCallback = null
    }
}
