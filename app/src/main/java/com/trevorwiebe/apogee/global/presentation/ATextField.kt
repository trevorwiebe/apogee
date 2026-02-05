package com.trevorwiebe.apogee.global.presentation

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier

@Composable
fun ATextField(
    modifier: Modifier = Modifier,
    value: MutableState<String>,
    placeHolder: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {

    OutlinedTextField(
        modifier = modifier,
        value = value.value,
        onValueChange = { value.value = it },
        placeholder = if(placeHolder.isNullOrEmpty()){
            null
        }else{
            {
                Text(text = placeHolder)
            }
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        maxLines = 1
    )
}