package com.trevorwiebe.apogee.ui

import android.app.Activity
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat

@Composable
fun SystemBarColorForFullScreenDialog(){

    val darkTheme = isSystemInDarkTheme()
    val view = LocalView.current

    DisposableEffect(darkTheme) {
        val dialogWindow = (view.parent as? DialogWindowProvider)?.window
        val window = dialogWindow ?: run {
            var context = view.context
            while (context is ContextWrapper) {
                if (context is Activity) {
                    break
                }
                context = context.baseContext
            }
            (context as Activity).window
        }
        val insetsController = WindowCompat.getInsetsController(window, view)
        val wasLightStatusBars = insetsController.isAppearanceLightStatusBars
        insetsController.isAppearanceLightStatusBars = !darkTheme
        onDispose {
            insetsController.isAppearanceLightStatusBars = wasLightStatusBars
        }
    }
}