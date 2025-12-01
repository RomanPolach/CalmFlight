package com.anxiousflyer.peacefulflight.viewmodel

import androidx.lifecycle.ViewModel
import com.anxiousflyer.peacefulflight.data.AppContent
import com.anxiousflyer.peacefulflight.model.Tool
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ToolsViewModel : ViewModel() {
    private val _tools = MutableStateFlow(AppContent.tools)
    val tools: StateFlow<List<Tool>> = _tools.asStateFlow()
}

