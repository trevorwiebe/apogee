package com.trevorwiebe.apogee.ui

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp

object Utils {

    @Composable
    fun displayCutoutPadding(context: Context): PaddingValues{
        val orientation by remember(context) {
            mutableIntStateOf(context.resources.configuration.orientation)
        }
        return if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
            WindowInsets.displayCutout.asPaddingValues()
        }else{
            PaddingValues(0.dp)
        }
    }
}