package com.trevorwiebe.apogee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.trevorwiebe.apogee.schedule.presentation.ScheduleScreen
import com.trevorwiebe.apogee.ui.theme.ApogeeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ApogeeTheme {
                ScheduleScreen()
            }
        }
    }
}