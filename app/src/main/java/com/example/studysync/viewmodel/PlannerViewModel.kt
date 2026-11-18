package com.example.studysync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysync.data.StudySession
import com.example.studysync.data.SubjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PlannerViewModel(
    private val repository: SubjectRepository
) : ViewModel() {

    // Get all study sessions
    fun getAllSessions(): Flow<List<StudySession>> {

        return repository.getAllStudySessions()
    }

    // Get sessions for a particular date
    fun getSessionsForDate(
        date: String
    ): Flow<List<StudySession>> {

        return repository.getSessionsForDate(
            date
        )
    }

    // Add a new study session
    fun addSession(
        session: StudySession
    ) {

        viewModelScope.launch {

            repository.addStudySession(
                session
            )
        }
    }

    // Update an existing session
    fun updateSession(
        session: StudySession
    ) {

        viewModelScope.launch {

            repository.updateStudySession(
                session
            )
        }
    }

    // Delete a session
    fun deleteSession(
        session: StudySession
    ) {

        viewModelScope.launch {

            repository.deleteStudySession(
                session
            )
        }
    }

    // Mark session completed/uncompleted
    fun updateSessionStatus(
        sessionId: Int,
        completed: Boolean
    ) {

        viewModelScope.launch {

            repository.updateStudySessionStatus(
                sessionId,
                completed
            )
        }
    }
}