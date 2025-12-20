package com.example.roboremote.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.roboremote.data.AppDatabase
import com.example.roboremote.data.RobotProfile
import com.example.roboremote.data.RobotRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RobotViewModel(application: Application) : AndroidViewModel(application) {

    // FIX: Initialize the repository HERE immediately
    private val repository: RobotRepository = RobotRepository(
        AppDatabase.getDatabase(application).robotDao()
    )

    // Now this works because 'repository' already exists
    val allRobots = repository.allRobots.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Function to Add a Robot
    fun addRobot(name: String, mac: String, type: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val newRobot = RobotProfile(
                name = name,
                macAddress = mac,
                connectionType = type
            )
            repository.insert(newRobot)
        }
    }

    // Function to Delete a Robot
    fun deleteRobot(robot: RobotProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(robot)
        }
    }
}