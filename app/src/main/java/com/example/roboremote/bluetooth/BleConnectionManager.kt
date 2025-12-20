package com.example.roboremote.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

@SuppressLint("MissingPermission")
class BleConnectionManager(private val context: Context) {

    private var bluetoothGatt: BluetoothGatt? = null
    private var writeCharacteristic: BluetoothGattCharacteristic? = null

    // Live variables to tell the UI what is happening
    private val _connectionState = MutableStateFlow("Disconnected")
    val connectionState: StateFlow<String> = _connectionState

    fun connect(macAddress: String) {
        // 1. Safety Check: Is this a valid MAC address?
        if (!BluetoothAdapter.checkBluetoothAddress(macAddress)) {
            _connectionState.value = "Error: Invalid MAC Address"
            return
        }

        val adapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

        // 2. Safety Check: Is Bluetooth on?
        if (adapter == null || !adapter.isEnabled) {
            _connectionState.value = "Error: Bluetooth is Off"
            return
        }

        try {
            val device = adapter.getRemoteDevice(macAddress)
            _connectionState.value = "Connecting..."

            // Connect to the device
            bluetoothGatt = device.connectGatt(context, false, gattCallback)
        } catch (e: SecurityException) {
            _connectionState.value = "Error: Permission Missing"
        } catch (e: IllegalArgumentException) {
            _connectionState.value = "Error: Bad Address"
        }
    }

    fun disconnect() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
        _connectionState.value = "Disconnected"
    }

    // Send data to the robot (e.g., Joystick coordinates)
    fun sendData(data: String) {
        val char = writeCharacteristic ?: return
        char.value = data.toByteArray()
        char.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        bluetoothGatt?.writeCharacteristic(char)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                _connectionState.value = "Discovering Services..."
                gatt?.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                _connectionState.value = "Disconnected"
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // AUTOMATICALLY FIND THE WRITABLE CHARACTERISTIC
                // This loops through all services to find one we can write to.
                // It works for HC-05 (BLE mode), HM-10, and ESP32.
                val serviceList = gatt?.services
                serviceList?.forEach { service ->
                    service.characteristics.forEach { characteristic ->
                        if (isWritable(characteristic)) {
                            writeCharacteristic = characteristic
                            _connectionState.value = "Connected & Ready!"
                            return // Found it, stop searching
                        }
                    }
                }
            }
        }
    }

    // Helper to check if a characteristic can be written to
    private fun isWritable(char: BluetoothGattCharacteristic): Boolean {
        return (char.properties and (BluetoothGattCharacteristic.PROPERTY_WRITE or BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) != 0
    }
}