package com.trevorwiebe.apogee.schedule.presentation.addScheduleCould

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.trevorwiebe.apogee.R
import com.trevorwiebe.apogee.global.presentation.ATextField
import com.trevorwiebe.apogee.ui.SystemBarColorForFullScreenDialog
import org.stanzamusic.stanza.presentation.saveCollection.coverCreator.colorPicker.ColorPicker

@Composable
fun AddScheduleCouldDialog(
    sheetOpen: MutableState<Boolean>
) {

    val primaryColor = MaterialTheme.colorScheme.primary

    val scheduleCouldTitle = remember { mutableStateOf("") }
    val scheduleCouldDescription = remember { mutableStateOf("") }
    val color = remember { mutableStateOf(primaryColor) }

    if (sheetOpen.value) {
        Dialog(
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            ),
            onDismissRequest = { sheetOpen.value = false },
        ) {

            SystemBarColorForFullScreenDialog()

            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .fillMaxSize()
            ) {

                Column(
                    modifier = Modifier.safeDrawingPadding()
                ){
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { sheetOpen.value = false }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.close),
                                contentDescription = null
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Add Schedule Item",
                            fontSize = 22.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.weight(1f))
                        Button(
                            onClick = {}
                        ) {
                            Text(
                                text = "Save"
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    ATextField(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth(),
                        value = scheduleCouldTitle,
                        placeHolder = "Title"
                    )

                    Spacer(Modifier.height(8.dp))

                    ATextField(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth(),
                        value = scheduleCouldDescription,
                        placeHolder = "Description"
                    )

                    Spacer(Modifier.height(8.dp))

                    ColorPicker(
                        color = color
                    )
                }
            }
        }
    }
}