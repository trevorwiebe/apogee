package com.trevorwiebe.apogee.schedule.presentation.addScheduleCould

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.LineHeightStyle
import com.trevorwiebe.apogee.R
import com.trevorwiebe.apogee.global.presentation.ATextField
import com.trevorwiebe.apogee.ui.SystemBarColorForFullScreenDialog
import org.stanzamusic.stanza.presentation.saveCollection.coverCreator.colorPicker.ColorPicker

@Composable
fun AddScheduleCouldDialog(
    sheetOpen: MutableState<Boolean>,
    onSave: (title: String, description: String, color: Color) -> Unit
) {

    val primaryColor = MaterialTheme.colorScheme.primary
    val scrollState = rememberScrollState()

    val colorSaver = Saver<MutableState<Color>, Long>(
        save = { it.value.value.toLong() },
        restore = { mutableStateOf(Color(it.toULong())) }
    )

    val scheduleCouldTitle = rememberSaveable { mutableStateOf("") }
    val scheduleCouldDescription = rememberSaveable { mutableStateOf("") }
    val color = rememberSaveable(saver = colorSaver) { mutableStateOf(primaryColor) }

    fun resetState(){
        scheduleCouldTitle.value = ""
        scheduleCouldDescription.value = ""
        color.value = primaryColor
    }

    if (sheetOpen.value) {
        Dialog(
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            ),
            onDismissRequest = {
                sheetOpen.value = false
                resetState()
            },
        ) {

            SystemBarColorForFullScreenDialog()

            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    Column(
                        modifier = Modifier.safeDrawingPadding()
                    ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                sheetOpen.value = false
                                resetState()
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.close),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Add Activity",
                            fontSize = 22.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.weight(1f))
                        Button(
                            onClick = {
                                onSave(
                                    scheduleCouldTitle.value,
                                    scheduleCouldDescription.value,
                                    color.value
                                )
                                resetState()
                            },
                            enabled = scheduleCouldTitle.value.isNotEmpty()
                        ) {
                            Text(
                                text = "Save"
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ){
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.Top
                        ){

                            Icon(
                                painter = painterResource(R.drawable.info),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Add activities that describe how you want to spend your time, for example; Sleeping, Exercising, Working, etc. \n \nOnce created, place them on your schedule to follow.",
                                fontSize = 14.sp,
                                style = TextStyle(
                                    platformStyle = PlatformTextStyle(
                                        includeFontPadding = false
                                    ),
                                    lineHeightStyle = LineHeightStyle(
                                        alignment = LineHeightStyle.Alignment.Top,
                                        trim = LineHeightStyle.Trim.Both
                                    )
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    ATextField(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth(),
                        value = scheduleCouldTitle,
                        placeHolder = "Title",
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    ATextField(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth(),
                        value = scheduleCouldDescription,
                        placeHolder = "Description (optional)"
                    )

                    Spacer(Modifier.height(8.dp))

                        ColorPicker(
                            color = color
                        )
                    }
                }

                // Translucent top bar overlay
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                        .fillMaxWidth()
                        .height(WindowInsets.systemBars.asPaddingValues().calculateTopPadding())
                )
            }
        }
    }
}