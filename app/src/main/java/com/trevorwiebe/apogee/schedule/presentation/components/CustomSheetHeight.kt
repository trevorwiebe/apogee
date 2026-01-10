package com.trevorwiebe.apogee.schedule.presentation.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun customSheetHeight(heightInt: Int, scaffoldState: BottomSheetScaffoldState): Dp {
    val system = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
    val ime = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
    val density = LocalDensity.current
    val height = heightInt.toFloat()
    val visibleHeightDp by remember(scaffoldState.bottomSheetState) {
        derivedStateOf {
            val currentOffset = try {
                scaffoldState.bottomSheetState.requireOffset()
            } catch (e: IllegalStateException) {
                height
            }
            val visibleHeightPx = (height - currentOffset).coerceAtLeast(0f)

            with(density) { visibleHeightPx.toDp() }
        }
    }

    return (visibleHeightDp - max(system, ime)).coerceAtLeast(0.dp)
}