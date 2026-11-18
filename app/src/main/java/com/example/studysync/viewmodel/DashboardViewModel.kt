package com.example.studysync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysync.data.StudySession
import com.example.studysync.data.Subject
import com.example.studysync.data.SubjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(
    private val repository: SubjectRepository
) : ViewModel() {

    val subjects: StateFlow<List<Subject>> =
        repository.allSubjects.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getSessionsForDate(
        date: String
    ): Flow<List<StudySession>> {

        return repository.getSessionsForDate(date)
    }
}