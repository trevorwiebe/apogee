package com.trevorwiebe.apogee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.trevorwiebe.apogee.editschedulecould.presentation.EditScheduleCouldScreen
import com.trevorwiebe.apogee.logtime.presentation.LogTimeScreen
import com.trevorwiebe.apogee.schedule.presentation.ScheduleScreen
import com.trevorwiebe.apogee.ui.theme.ApogeeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()

            ApogeeTheme {
                NavHost(
                    navController = navController,
                    startDestination = "logtime"
                ) {

                    composable("logtime"){
                        LogTimeScreen(
                            onNavigate = navController::navigate
                        )
                    }

                    composable("schedule"){
                        ScheduleScreen(
                            onNavigate = navController::navigate
                        )
                    }

                    composable("editschedulecould") {
                        EditScheduleCouldScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}