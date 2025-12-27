package com.example.roboremote.utils

import android.content.Context

data class CustomButton(val name: String, val command: String)

object ButtonStorage {
    private const val PREFS_NAME = "robot_buttons_prefs"

    // Save list of buttons for a specific Robot (MAC address)
    fun saveButtons(context: Context, macAddress: String, buttons: List<CustomButton>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // We convert the list to a simple string: "Jump:CMD_JUMP||Dance:CMD_DANCE"
        val serialized = buttons.joinToString("||") { "${it.name}:${it.command}" }
        prefs.edit().putString(macAddress, serialized).apply()
    }

    // Load buttons for a specific Robot
    fun loadButtons(context: Context, macAddress: String): List<CustomButton> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val serialized = prefs.getString(macAddress, "") ?: ""

        if (serialized.isEmpty()) return emptyList()

        return serialized.split("||").mapNotNull { entry ->
            val parts = entry.split(":")
            if (parts.size == 2) CustomButton(parts[0], parts[1]) else null
        }
    }
}