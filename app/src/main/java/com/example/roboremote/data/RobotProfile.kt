package com.example.roboremote.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// This class represents one row in your database table
@Entity(tableName = "robot_profiles")
data class RobotProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,             // Unique ID for each robot

    val name: String,            // User's custom name (e.g., "Mars Rover")

    val macAddress: String,      // The Bluetooth Address (e.g., "00:11:22:33:FF:EE")

    val connectionType: String,  // "BLE" or "CLASSIC"

    val createdAt: Long = System.currentTimeMillis() // Timestamp
)