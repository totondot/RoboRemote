package com.example.roboremote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Import your screens from the 'screens' package
import com.example.roboremote.screens.LoginScreen
import com.example.roboremote.screens.ScanScreen
import com.example.roboremote.screens.DashboardScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            // This sets up the navigation flow: Login -> Scan -> Dashboard
            NavHost(navController = navController, startDestination = "login") {

                // Screen 1: Login
                composable("login") {
                    LoginScreen(onLoginSuccess = {
                        // When login finishes, go to Scan screen
                        navController.navigate("scan")
                    })
                }

                // Screen 2: Connection Manager
                composable("scan") {
                    ScanScreen(onDeviceConnected = {
                        // When a device is clicked, go to Dashboard
                        navController.navigate("dashboard")
                    })
                }

                // Screen 3: The Joystick Controller
                composable("dashboard") {
                    DashboardScreen()
                }
            }
        }
    }
}