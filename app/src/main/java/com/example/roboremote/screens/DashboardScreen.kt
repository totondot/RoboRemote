package com.example.roboremote.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roboremote.bluetooth.BleConnectionManager
import com.example.roboremote.utils.ButtonStorage
import com.example.roboremote.utils.CustomButton
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    macAddress: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Connection Manager
    val connectionManager = remember { BleConnectionManager(context) }
    val connectionState by connectionManager.connectionState.collectAsState()

    // Custom Buttons State
    // We utilize mutableStateListOf so the UI updates immediately when we add one
    val customButtons = remember { mutableStateListOf<CustomButton>() }
    var showAddDialog by remember { mutableStateOf(false) }

    // Load buttons when screen starts
    LaunchedEffect(macAddress) {
        val saved = ButtonStorage.loadButtons(context, macAddress)
        customButtons.addAll(saved)

        // Connect to robot
        connectionManager.connect(macAddress)
    }

    // Save buttons whenever the list changes
    fun saveNewButton(name: String, cmd: String) {
        val newBtn = CustomButton(name, cmd)
        customButtons.add(newBtn)
        ButtonStorage.saveButtons(context, macAddress, customButtons)
    }

    // Cleanup on exit
    DisposableEffect(Unit) {
        onDispose { connectionManager.disconnect() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Controller")
                        Text(connectionState, style = MaterialTheme.typography.bodySmall)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    // The "Add Button" icon in the top right
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Custom Button")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. THE JOYSTICK (Top Half)
            Box(
                modifier = Modifier
                    .weight(1f) // Takes up top space
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Joystick(
                    onMoved = { x, y ->
                        val command = "X:${x.toInt()},Y:${y.toInt()}\n"
                        connectionManager.sendData(command)
                    }
                )
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // 2. THE CUSTOM BUTTON PAD (Bottom Half)
            Text("Custom Actions", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))

            if (customButtons.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("Tap + to add buttons", color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3), // 3 buttons per row
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(customButtons) { btn ->
                        Button(
                            onClick = {
                                // Send the custom command!
                                connectionManager.sendData("${btn.command}\n")
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text(btn.name, fontSize = 12.sp, maxLines = 1)
                        }
                    }
                }
            }
        }
    }

    // 3. THE POPUP DIALOG TO ADD BUTTONS
    if (showAddDialog) {
        AddButtonDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, cmd ->
                saveNewButton(name, cmd)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddButtonDialog(onDismiss: () -> Unit, onAdd: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var command by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Button") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Button Label (e.g. 'Jump')") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = command,
                    onValueChange = { command = it },
                    label = { Text("Command (e.g. 'CMD_JUMP')") },
                    supportingText = { Text("This string is sent to the robot.") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { if (name.isNotEmpty() && command.isNotEmpty()) onAdd(name, command) }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// Re-pasting Joystick here to ensure it doesn't get lost
@Composable
fun Joystick(onMoved: (Float, Float) -> Unit) {
    var position by remember { mutableStateOf(Offset.Zero) }
    val maxRadius = 150f
    val knobRadius = 50f

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size((maxRadius * 2).dp)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(color = Color.LightGray.copy(alpha = 0.5f))
        }
        Box(
            modifier = Modifier
                .offset { IntOffset(position.x.roundToInt(), position.y.roundToInt()) }
                .size((knobRadius * 2).dp)
                .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            position = Offset.Zero
                            onMoved(0f, 0f)
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        val newPos = position + dragAmount
                        val distance = newPos.getDistance()
                        position = if (distance > maxRadius) newPos * (maxRadius / distance) else newPos
                        val xPercent = (position.x / maxRadius) * 100
                        val yPercent = -(position.y / maxRadius) * 100
                        onMoved(xPercent, yPercent)
                    }
                }
        )
    }
}