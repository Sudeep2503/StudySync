package com.example.studysync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysync.data.SubjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel(
    private val repository: SubjectRepository
) : ViewModel() {

    val totalSubjects: Flow<Int> =
        repository
            .getTotalSubjects()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                0
            )

    val completedTopics: Flow<Int> =
        repository
            .getCompletedTopics()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                0
            )

    val totalStudyMinutes: Flow<Int> =
        repository
            .getTotalStudyMinutes()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                0
            )
}