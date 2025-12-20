package com.example.roboremote.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// List all your tables here inside entities = [...]
@Database(entities = [RobotProfile::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun robotDao(): RobotDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "robo_remote_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}