
package com.example.studysync.ai

import android.util.Log
import com.example.studysync.data.SubjectRepository
import kotlinx.coroutines.flow.first

class StudyContextBuilder(
    private val repository: SubjectRepository
) {

    suspend fun buildContext(): StudyContext {
        val subjects = repository.allSubjects.first()

        val recentSessions =
            repository.getAllStudySessions().first()

        val subjectContexts = subjects.map { subject ->
            val topics =
                repository.getTopicsForSubject(subject.id).first()

            Log.d(
                "StudySyncAI",
                "Subject id=${subject.id}, name=${subject.name}, " +
                        "topics=${topics.map { "${it.topicName}:${it.completed}" }}"
            )

            SubjectContext(
                id = subject.id,
                name = subject.name,
                examDate = subject.examDate,
                targetScore = subject.targetScore,
                progress = subject.progress,
                topics = topics.map { topic ->
                    TopicContext(
                        name = topic.topicName,
                        completed = topic.completed
                    )
                }
            )
        }

        val sessionContexts = recentSessions.map { session ->
            SessionContext(
                subjectName = session.subjectName,
                topicName = session.topicName,
                date = session.date,
                startTime = session.startTime,
                duration = session.duration,
                completed = session.completed
            )
        }

        val totalStudyMinutes =
            recentSessions
                .filter { it.completed }
                .sumOf { it.duration }

        val context = StudyContext(
            subjects = subjectContexts,
            recentSessions = sessionContexts,
            totalStudyMinutes = totalStudyMinutes,
            totalSessions = recentSessions.size
        )

        Log.d(
            "StudySyncAI",
            "Context summary: subjects=${context.subjects.size}, " +
                    "topics=${context.subjects.sumOf { it.topics.size }}, " +
                    "incompleteTopics=${context.subjects.sumOf { subject ->
                        subject.topics.count { !it.completed }
                    }}"
        )

        return context
    }
}
