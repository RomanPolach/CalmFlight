package com.example.calmflight.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.calmflight.model.LearnItem

@Composable
fun LearnItem.answer(): String {
    return stringResource(id = this.answerRes)
}

