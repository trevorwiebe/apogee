package com.trevorwiebe.apogee.logtime.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trevorwiebe.apogee.logtime.presentation.LogTimeUiSlot

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogTimeSlotItem(
    uiSlot: LogTimeUiSlot,
    onClick: (LogTimeUiSlot) -> Unit
) {
    val defaultBackground = MaterialTheme.colorScheme.surfaceVariant
    val defaultTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    val hasScheduled = uiSlot.scheduledName != null
    val hasActual = uiSlot.actualName != null
    val hasContent = hasScheduled || hasActual

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .clip(MaterialTheme.shapes.small)
            .clickable{ onClick(uiSlot) },
        colors = CardDefaults.cardColors(
            containerColor = if (uiSlot.selected) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        shape = MaterialTheme.shapes.small
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 8.dp,
                        end = 8.dp,
                        top = if (hasContent) 6.dp else 10.dp,
                        bottom = if (hasContent) 6.dp else 10.dp
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = uiSlot.slot.startTime.toString())

                if (hasContent) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Scheduled (left side)
                        uiSlot.scheduledName?.let { scheduledName ->
                            Text(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(CircleShape)
                                    .background(uiSlot.lightColor ?: defaultBackground)
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                textAlign = TextAlign.Center,
                                text = scheduledName,
                                fontSize = 14.sp,
                                color = uiSlot.color ?: defaultTextColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        } ?: Spacer(modifier = Modifier.weight(1f))

                        // Actual (right side)
                        uiSlot.actualName?.let { actualName ->
                            Text(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(CircleShape)
                                    .background(uiSlot.actualLightColor ?: defaultBackground)
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                textAlign = TextAlign.Center,
                                text = actualName,
                                fontSize = 14.sp,
                                color = uiSlot.actualColor ?: defaultTextColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        } ?: Spacer(modifier = Modifier.weight(1f))
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                Text(text = uiSlot.slot.endTime.plusNanos(1).toString())
            }
        }
    }
}
