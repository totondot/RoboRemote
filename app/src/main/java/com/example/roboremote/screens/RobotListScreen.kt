package com.example.roboremote.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roboremote.data.RobotProfile
import com.example.roboremote.viewmodel.RobotViewModel
// ... imports ...
// Add imports for ScanScreen if needed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RobotListScreen(
    onRobotClick: (RobotProfile) -> Unit,
    viewModel: RobotViewModel = viewModel()
) {
    val robotList by viewModel.allRobots.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    // NEW: State to show the scanner
    var showScanner by remember { mutableStateOf(false) }

    if (showScanner) {
        // Show the full-screen scanner
        ScanScreen(
            onDeviceSelected = { name, mac ->
                // When user picks a device, save it immediately
                viewModel.addRobot(name, mac, "BLE")
                showScanner = false
            },
            onCancel = { showScanner = false }
        )
    } else {
        // Show the normal list
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { showDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Robot")
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                Text("My Robots", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                if (robotList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No robots found. Tap + to add one!")
                    }
                } else {
                    LazyColumn {
                        items(robotList) { robot ->
                            RobotItem(
                                robot = robot,
                                onClick = { onRobotClick(robot) },
                                onDelete = { viewModel.deleteRobot(robot) }
                            )
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AddRobotDialog(
                onDismiss = { showDialog = false },
                onScanClick = {
                    showDialog = false
                    showScanner = true // Switch to scan mode
                },
                onAdd = { name, mac, type ->
                    viewModel.addRobot(name, mac, type)
                    showDialog = false
                }
            )
        }
    }
}

// Updated Dialog with Scan Button
@Composable
fun AddRobotDialog(
    onDismiss: () -> Unit,
    onScanClick: () -> Unit, // New callback
    onAdd: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var mac by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("BLE") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Robot") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Robot Name") })
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = mac,
                        onValueChange = { mac = it },
                        label = { Text("MAC Address") },
                        modifier = Modifier.weight(1f)
                    )
                }
                // NEW: SCAN BUTTON
                Button(
                    onClick = onScanClick,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Scan for Devices 🔍")
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { type = if (type == "BLE") "CLASSIC" else "BLE" }) {
                    Text("Connection: $type")
                }
            }
        },
        confirmButton = {
            Button(onClick = { if (name.isNotEmpty()) onAdd(name, mac, type) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
// ... (Your existing RobotListScreen and AddRobotDialog code is above here) ...

// PASTE THIS AT THE VERY BOTTOM OF THE FILE
@Composable
fun RobotItem(robot: RobotProfile, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(robot.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("MAC: ${robot.macAddress}", fontSize = 14.sp, color = Color.Gray)
                Text("Type: ${robot.connectionType}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
            }
        }
    }
}