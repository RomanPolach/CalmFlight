package com.anxiousflyer.peacefulflight.viewmodel

import androidx.lifecycle.ViewModel
import com.anxiousflyer.peacefulflight.data.AppContent
import com.anxiousflyer.peacefulflight.model.LearnItem
import com.anxiousflyer.peacefulflight.model.LearnSection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LearnUiState(
    val sections: List<LearnSection> = emptyList(),
    val expandedSectionId: String? = null
)

class LearnViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LearnUiState(sections = AppContent.learnSections))
    val uiState: StateFlow<LearnUiState> = _uiState.asStateFlow()

    fun toggleSection(sectionId: String) {
        _uiState.update {
            it.copy(expandedSectionId = if (it.expandedSectionId == sectionId) null else sectionId)
        }
    }

}

