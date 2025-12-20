package com.example.roboremote.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.roboremote.bluetooth.BleConnectionManager
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    macAddress: String, // NEW: We now know WHICH robot
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Create the Connection Manager
    val connectionManager = remember { BleConnectionManager(context) }
    val connectionState by connectionManager.connectionState.collectAsState()

    // Connect when screen opens, Disconnect when it closes
    DisposableEffect(Unit) {
        connectionManager.connect(macAddress)
        onDispose { connectionManager.disconnect() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Controller")
                        Text(connectionState, style = MaterialTheme.typography.bodySmall) // Show Status
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // JOYSTICK LOGIC
            Joystick(
                onMoved = { x, y ->
                    // SEND DATA TO ROBOT!
                    // Format: "X:50,Y:-20\n"
                    val command = "X:${x.toInt()},Y:${y.toInt()}\n"
                    connectionManager.sendData(command)
                }
            )
        }
    }
}
@Composable
fun Joystick(onMoved: (Float, Float) -> Unit) {
    var position by remember { mutableStateOf(Offset.Zero) }
    val maxRadius = 150f
    val knobRadius = 50f

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size((maxRadius * 2).dp) // Size of the outer circle
    ) {
        // Outer Circle (Background)
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(color = Color.LightGray.copy(alpha = 0.5f))
        }

        // The Inner Knob (Draggable)
        Box(
            modifier = Modifier
                .offset { IntOffset(position.x.roundToInt(), position.y.roundToInt()) }
                .size((knobRadius * 2).dp)
                .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            // Reset to center when let go
                            position = Offset.Zero
                            onMoved(0f, 0f)
                        }
                    ) { change, dragAmount ->
                        change.consume()

                        // Calculate new position
                        val newPos = position + dragAmount
                        val distance = newPos.getDistance()

                        // Constrain knob inside the circle
                        position = if (distance > maxRadius) {
                            newPos * (maxRadius / distance)
                        } else {
                            newPos
                        }

                        // Send normalized values (-100 to 100)
                        val xPercent = (position.x / maxRadius) * 100
                        val yPercent = -(position.y / maxRadius) * 100 // Invert Y so Up is Positive
                        onMoved(xPercent, yPercent)
                    }
                }
        )
    }
}