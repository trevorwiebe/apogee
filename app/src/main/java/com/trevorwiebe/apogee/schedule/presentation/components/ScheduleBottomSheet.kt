package com.trevorwiebe.apogee.schedule.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScheduleBottomSheet() {

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxWidth()
            .height(300.dp)
    )

}