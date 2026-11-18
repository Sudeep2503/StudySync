package com.example.studysync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysync.data.Subject
import com.example.studysync.data.SubjectDao
import com.example.studysync.data.StudySession
import com.example.studysync.data.StudySessionDao
import com.example.studysync.data.TopicDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn

data class StudyDNAStats(
    val studyDays: Int = 0,
    val totalSubjects: Int = 0,
    val focusedSubject: String = "No completed study sessions yet",
    val completedTopics: Int = 0,
    val totalTopics: Int = 0,
    val totalStudyMinutes: Int = 0
)

class AnalyticsViewModel(
    private val studySessionDao: StudySessionDao,
    private val subjectDao: SubjectDao,
    private val topicDao: TopicDao
) : ViewModel() {

    val totalStudyMinutes =
        studySessionDao.getTotalStudyMinutes().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

    val sessionCount =
        studySessionDao.getSessionCount().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

    // Keep this property name because AnalyticsFragment uses it.
    val sessions =
        studySessionDao.getAllSessions().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val studyDNA: StateFlow<StudyDNAStats> =
        combine(
            sessions,
            subjectDao.getAllSubjects()
        ) { sessionList, subjectList ->
            sessionList to subjectList
        }.mapLatest { (sessionList, subjectList) ->

            val completedSessions =
                sessionList.filter { it.completed }

            val studyDays =
                completedSessions
                    .map { it.date }
                    .filter { it.isNotBlank() }
                    .distinct()
                    .size

            val minutesBySubject =
                completedSessions
                    .groupBy { it.subjectName }
                    .mapValues { (_, subjectSessions) ->
                        subjectSessions.sumOf { it.duration }
                    }

            val focusedSubject =
                minutesBySubject.maxByOrNull { it.value }
                    ?.key
                    ?: "No completed study sessions yet"

            var totalTopics = 0
            var completedTopics = 0

            subjectList.forEach { subject ->
                val topics =
                    topicDao.getTopicsForSubjectOnce(subject.id)

                totalTopics += topics.size
                completedTopics += topics.count { it.completed }
            }

            StudyDNAStats(
                studyDays = studyDays,
                totalSubjects = subjectList.size,
                focusedSubject = focusedSubject,
                completedTopics = completedTopics,
                totalTopics = totalTopics,
                totalStudyMinutes = completedSessions.sumOf { it.duration }
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            StudyDNAStats()
        )
}
