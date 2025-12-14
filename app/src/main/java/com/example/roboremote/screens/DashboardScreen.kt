package com.example.roboremote.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
// Make sure to import your Joystick widget here
import com.example.roboremote.widgets.Joystick

@Composable
fun DashboardScreen() {
    // State for the UI
    var speedLimit by remember { mutableFloatStateOf(0.5f) }
    var joystickX by remember { mutableFloatStateOf(0f) }
    var joystickY by remember { mutableFloatStateOf(0f) }
    var statusText by remember { mutableStateOf("Ready") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Status Bar
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
            Text(
                text = "Status: $statusText",
                modifier = Modifier.padding(16.dp)
            )
        }

        // Feature: Speed Slider
        Text("Speed Limiter: ${(speedLimit * 100).toInt()}%")
        Slider(
            value = speedLimit,
            onValueChange = { speedLimit = it }
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Feature: The Joystick
        Joystick(size = 250f) { x, y ->
            joystickX = x
            joystickY = y
            // This is where you WOULD send Bluetooth data in the final milestone
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Live Debug Data (Show this in your video)
        Text("Debug Output:")
        Text("X: ${String.format("%.2f", joystickX)}")
        Text("Y: ${String.format("%.2f", joystickY)}")
    }
}