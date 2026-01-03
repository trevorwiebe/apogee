package com.trevorwiebe.apogee.logtime.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.trevorwiebe.apogee.R

@Composable
fun LogTimeScreen(
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit,
) {

    Row(
        modifier = modifier
            .displayCutoutPadding()
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.End
    ) {
        FilledTonalIconButton(
            onClick = { onNavigate("schedule") },
            modifier = Modifier.size(54.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.edit_calendar),
                contentDescription = null
            )
        }
    }

}