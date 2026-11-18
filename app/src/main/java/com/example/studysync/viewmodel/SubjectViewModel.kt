package com.example.studysync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysync.data.Subject
import com.example.studysync.data.SubjectRepository
import com.example.studysync.data.Topic
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
class SubjectViewModel(
    private val repository: SubjectRepository
) : ViewModel() {

    val subjects: StateFlow<List<Subject>> =
        repository.allSubjects.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addSubject(
        name: String,
        examDate: String,
        targetScore: Int
    ) {
        viewModelScope.launch {

            val subject = Subject(
                name = name,
                examDate = examDate,
                targetScore = targetScore,
                progress = 0
            )

            repository.insertSubject(subject)
        }
    }

    fun deleteSubject(
        subject: Subject
    ) {
        viewModelScope.launch {
            repository.deleteSubject(subject)
        }
    }

    fun updateProgress(
        subject: Subject,
        progress: Int
    ) {
        viewModelScope.launch {

            repository.updateProgress(
                subject.id,
                progress.coerceIn(0, 100)
            )
        }
    }

    suspend fun getSubjectById(
        id: Int
    ): Subject? {
        return repository.getSubjectById(id)
    }


    fun addTopic(topic: Topic) {
        viewModelScope.launch {
            try {
                android.util.Log.d(
                    "StudySyncAI",
                    "Inserting topic=${topic.topicName}, subjectId=${topic.subjectId}"
                )

                repository.addTopic(topic)

                val savedTopics = repository
                    .getTopicsForSubject(topic.subjectId)
                    .first()

                android.util.Log.d(
                    "StudySyncAI",
                    "Topics after insert: ${savedTopics.map { it.topicName }}"
                )

            } catch (e: Exception) {
                android.util.Log.e(
                    "StudySyncAI",
                    "Topic insertion failed",
                    e
                )
            }
        }
    }


    fun updateTopicStatus(
        topicId: Int,
        subjectId: Int,
        completed: Boolean
    ) {
        viewModelScope.launch {

            repository.updateTopicStatus(
                topicId = topicId,
                subjectId = subjectId,
                completed = completed
            )
        }
    }

    fun getTopicsForSubject(
        subjectId: Int
    ) =
        repository.getTopicsForSubject(subjectId)
}