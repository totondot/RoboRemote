package com.example.roboremote.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RobotDao {

    // Get all robots, ordered by newest first.
    // Flow<> means the UI updates automatically if data changes!
    @Query("SELECT * FROM robot_profiles ORDER BY createdAt DESC")
    fun getAllRobots(): Flow<List<RobotProfile>>

    // Insert a new robot (or replace if it exists)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRobot(robot: RobotProfile)

    // Delete a specific robot
    @Delete
    suspend fun deleteRobot(robot: RobotProfile)
}