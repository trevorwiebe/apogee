package com.trevorwiebe.apogee.schedule.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trevorwiebe.apogee.global.presentation.ATextField

@Composable
fun ScheduleBottomSheet(
    onSave: () -> Unit
) {

    val taskTitle = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .navigationBarsPadding()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Add task",
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = onSave,
            ) {
                Text(
                    text = "Save"
                )
            }
        }

        ATextField(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth(),
            value = taskTitle,
            placeHolder = "Title"
        )
    }

}