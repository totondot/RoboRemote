package com.example.roboremote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.roboremote.screens.DashboardScreen
import com.example.roboremote.screens.LoginScreen
import com.example.roboremote.screens.RobotListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RoboRemoteApp()
        }
    }
}

@Composable
fun RoboRemoteApp() {
    val navController = rememberNavController()

    // START DESTINATION is 'login'
    NavHost(navController = navController, startDestination = "login") {

        // 1. LOGIN SCREEN
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    // HERE IS THE FIX: Go to 'robot_list', NOT the scan screen
                    navController.navigate("robot_list") {
                        // This removes the login screen from the back stack so you can't go back to it
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // 2. ROBOT LIST SCREEN (The new Database screen)
        composable("robot_list") {
            RobotListScreen(
                onRobotClick = { robot ->
                    // Pass the MAC address to the dashboard
                    navController.navigate("dashboard/${robot.macAddress}")
                }
            )
        }

        // 3. DASHBOARD (Updated to accept arguments)
        composable("dashboard/{macAddress}") { backStackEntry ->
            val macAddress = backStackEntry.arguments?.getString("macAddress") ?: ""

            DashboardScreen(
                macAddress = macAddress,
                onBack = { navController.popBackStack() }
            )
        }
    }
}