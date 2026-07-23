package com.example.garminspeedapp

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.content.Context
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@SuppressLint("MissingPermission")
class BluetoothManager(private val context: Context) {

    private val bluetoothAdapter: BluetoothAdapter? =
        BluetoothAdapter.getDefaultAdapter()

    private var bluetoothGatt: BluetoothGatt? = null




    // CSC Service and Characteristic UUIDs
    companion object {
        val CSC_SERVICE_UUID: UUID = UUID.fromString("00001816-0000-1000-8000-00805f9b34fb")
        val CSC_MEASUREMENT_CHAR_UUID: UUID = UUID.fromString("00002a5b-0000-1000-8000-00805f9b34fb")
        val CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState = _connectionState.asStateFlow()

    private val _speed = MutableStateFlow<Float>(0f)
    val speed = _speed.asStateFlow()

    sealed class ConnectionState {
        object Disconnected : ConnectionState()
        object Connecting : ConnectionState()
        object Connected : ConnectionState()
        data class Error(val message: String) : ConnectionState()
    }

    fun connect(device: BluetoothDevice) {
        if (bluetoothAdapter == null) {
            _connectionState.value = ConnectionState.Error("Bluetooth not available")
            return
        }

        _connectionState.value = ConnectionState.Connecting

        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    fun disconnect() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
        _connectionState.value = ConnectionState.Disconnected
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    _connectionState.value = ConnectionState.Connected
                    // Here we would discover services
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    _connectionState.value = ConnectionState.Disconnected
                    gatt.close()
                    bluetoothGatt = null
                }
                else -> {
                    if (status != BluetoothGatt.GATT_SUCCESS) {
                        _connectionState.value = ConnectionState.Error("Connection failed with status: $status")
                    }
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val service = gatt.getService(CSC_SERVICE_UUID)
                if (service != null) {
                    val characteristic = service.getCharacteristic(CSC_MEASUREMENT_CHAR_UUID)
                    if (characteristic != null) {
                        // 1. Enable notifications
                        gatt.setCharacteristicNotification(characteristic, true)

                        // 2. Enable CCCD
                        val descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID)
                        if (descriptor != null) {
                            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                            gatt.writeDescriptor(descriptor)
                        }
                    }
                }
            } else {
                _connectionState.value = ConnectionState.Error("Service discovery failed")
            }
        }

        @SuppressLint("MissingPermission")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            if (characteristic.uuid == CSC_MEASUREMENT_CHAR_UUID) {
                parseCscMeasurement(value)
            }
        }

        private fun parseCscMeasurement(data: ByteArray) {
            if (data.isEmpty()) return

            // Flags byte: bit 0 indicates if cadence is present
            val flags = data[0].toInt()
            val hasCadence = (flags and 0x01) != 0

            var offset = 1

            // Speed is the first 16-bit value in the payload
            if (data.size >= offset + 2) {
                // Using ByteBuffer for easy conversion, or manual bit shifting
                val speedInHph = ((data[offset].toInt() and 0xFF) shl 8) or (data[offset + 1].toInt() and 0xFF)
                
                // 1. Convert HPH to km/h: 1 hph = 0.1 km/h
                val speedInKmh = speedInHph / 10f
                
                // 2. Convert km/h to mph: 1 km/h ≈ 0.621371 mph
                _speed.value = speedInKmh * 0.621371f
                
                offset += 2
            }

            // If flags indicate cadence, parse it too
            if (hasCadence && data.size >= offset + 2) {
                val cadenceInRpm = ((data[offset].toInt() and 0xFF) shl 8) or (data[offset + 1].toInt() and 0xFF)
                // We could add a _cadence StateFlow if needed later
                offset += 2
            }
        }

    }
}
