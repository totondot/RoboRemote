package com.example.roboremote.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class BleScannerManager(context: Context) {

    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val adapter: BluetoothAdapter? = bluetoothManager.adapter

    // This function returns a "Stream" (Flow) of devices as they are found
    @SuppressLint("MissingPermission") // We will handle permissions in the UI
    fun scanForDevices(): Flow<ScanResult> = callbackFlow {
        val scanner = adapter?.bluetoothLeScanner
        if (scanner == null) {
            close() // Bluetooth is off or not supported
            return@callbackFlow
        }

        val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                // When a device is found, send it to the listener
                trySend(result)
            }

            override fun onScanFailed(errorCode: Int) {
                close()
            }
        }

        // Start Scanning
        scanner.startScan(scanCallback)

        // When we stop listening, stop scanning to save battery
        awaitClose {
            scanner.stopScan(scanCallback)
        }
    }
}