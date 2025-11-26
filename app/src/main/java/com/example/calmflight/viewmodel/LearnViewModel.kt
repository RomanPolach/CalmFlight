package com.example.calmflight.viewmodel

import androidx.lifecycle.ViewModel
import com.example.calmflight.data.AppContent
import com.example.calmflight.model.LearnItem
import com.example.calmflight.model.LearnSection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LearnViewModel : ViewModel() {
    private val _sections = MutableStateFlow(AppContent.learnSections)
    val sections: StateFlow<List<LearnSection>> = _sections.asStateFlow()

    private val _expandedSection = MutableStateFlow<String?>(null)
    val expandedSection: StateFlow<String?> = _expandedSection.asStateFlow()

    fun toggleSection(sectionId: String) {
        _expandedSection.value = if (_expandedSection.value == sectionId) null else sectionId
    }

    fun getItemById(itemId: String): LearnItem? {
        return _sections.value.flatMap { it.items }.find { it.id == itemId }
    }
}

