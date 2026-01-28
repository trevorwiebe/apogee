package com.trevorwiebe.apogee.schedule.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trevorwiebe.apogee.R
import com.trevorwiebe.apogee.schedule.data.ScheduleCould
import com.trevorwiebe.apogee.schedule.presentation.ScheduleVMUtils.lightenColor

@Composable
fun ScheduleBottomSheet(
    modifier: Modifier = Modifier,
    scheduleCoulds: List<ScheduleCould>,
    onItemClick: (ScheduleCould) -> Unit,
    onClose: () -> Unit
) {

    val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 250.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 8.dp,
                    end = 8.dp,
                    bottom = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
        ){
            IconButton(onClick = onClose) {
                Icon(
                    painter = painterResource(R.drawable.close),
                    contentDescription = "Close"
                )
            }
            Text(
                modifier = Modifier.weight(1f),
                text = "Set Activity",
                fontSize = 22.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            Button(
                onClick = {}
            ) {
                Text(text = "Edit Activities")
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(scheduleCoulds) { scheduleCould ->
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(lightenColor(scheduleCould.color))
                        .fillMaxWidth()
                        .clickable { onItemClick(scheduleCould) }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(scheduleCould.color)
                            .size(32.dp)
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(
                        modifier = Modifier.weight(1f),
                        text = scheduleCould.name,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
    Spacer(Modifier.height(navBarPadding))
}
