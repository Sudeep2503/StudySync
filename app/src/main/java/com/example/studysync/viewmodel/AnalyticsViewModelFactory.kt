
package com.example.studysync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studysync.data.SubjectDao
import com.example.studysync.data.StudySessionDao
import com.example.studysync.data.TopicDao

class AnalyticsViewModelFactory(
    private val studySessionDao: StudySessionDao,
    private val subjectDao: SubjectDao,
    private val topicDao: TopicDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(AnalyticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnalyticsViewModel(
                studySessionDao,
                subjectDao,
                topicDao
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
