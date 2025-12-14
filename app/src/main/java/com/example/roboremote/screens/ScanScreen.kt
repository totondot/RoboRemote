package com.example.roboremote.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height

// Simple data model for the list
data class ScannedDevice(val name: String, val address: String)

@Composable
fun ScanScreen(onDeviceConnected: () -> Unit) {
    // Mock data for the video presentation
    val devices = listOf(
        ScannedDevice("HC-05 Robot", "00:11:22:33:44:55"),
        ScannedDevice("ESP32 BLE", "AA:BB:CC:DD:EE:FF"),
        ScannedDevice("Living Room TV", "12:34:56:78:90:AB")
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Connection Manager", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = { /* TODO: Trigger Real Bluetooth Scan */ }) {
            Text("Scan for Devices")
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn {
            items(devices) { device ->
                ListItem(
                    headlineContent = { Text(device.name) },
                    supportingContent = { Text(device.address) },
                    leadingContent = { Icon(Icons.Default.Bluetooth, contentDescription = null) },
                    modifier = Modifier.clickable {
                        // Simulate connection for the video
                        onDeviceConnected()
                    }
                )
                Divider()
            }
        }
    }
}