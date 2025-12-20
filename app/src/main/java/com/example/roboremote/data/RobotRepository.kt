package com.example.roboremote.data

import kotlinx.coroutines.flow.Flow

class RobotRepository(private val robotDao: RobotDao) {

    // Get all robots from the database
    val allRobots: Flow<List<RobotProfile>> = robotDao.getAllRobots()

    // Add a robot
    suspend fun insert(robot: RobotProfile) {
        robotDao.insertRobot(robot)
    }

    // Delete a robot
    suspend fun delete(robot: RobotProfile) {
        robotDao.deleteRobot(robot)
    }
}