package com.example.roboremote.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.roboremote.bluetooth.BleScannerManager
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@Composable
fun ScanScreen(
    onDeviceSelected: (String, String) -> Unit, // Returns Name and MAC
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scanner = remember { BleScannerManager(context) }

    // List of found devices (Using a Set to avoid duplicates)
    val foundDevices = remember { mutableStateListOf<Pair<String, String>>() }
    var isScanning by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val allGranted = perms.values.all { it }
        if (allGranted) {
            isScanning = true
        } else {
            errorMessage = "Bluetooth permissions are required to scan."
        }
    }

    // Start scanning when screen opens
    LaunchedEffect(Unit) {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }

        if (permissions.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }) {
            isScanning = true
        } else {
            permissionLauncher.launch(permissions)
        }
    }

    // The actual scanning logic
    LaunchedEffect(isScanning) {
        if (isScanning) {
            scanner.scanForDevices().collect { result ->
                val name = result.device.name ?: "Unknown Device"
                val mac = result.device.address
                // Only add if not already in list
                if (foundDevices.none { it.second == mac }) {
                    foundDevices.add(name to mac)
                }
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Scanning for Robots...", fontSize = 24.sp)
        if (errorMessage != null) {
            Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(foundDevices) { (name, mac) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onDeviceSelected(name, mac) },
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(name, style = MaterialTheme.typography.titleMedium)
                        Text(mac, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
            Text("Cancel")
        }
    }
}