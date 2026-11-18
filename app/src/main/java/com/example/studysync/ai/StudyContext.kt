package com.example.studysync.ai

data class StudyContext(

    val subjects: List<SubjectContext>,

    val recentSessions: List<SessionContext>,

    val totalStudyMinutes: Int,

    val totalSessions: Int
)

data class SubjectContext(

    val id: Int,

    val name: String,

    val examDate: String,

    val targetScore: Int,

    val progress: Int,

    val topics: List<TopicContext>
)

data class TopicContext(

    val name: String,

    val completed: Boolean
)

data class SessionContext(

    val subjectName: String,

    val topicName: String,

    val date: String,

    val startTime: String,

    val duration: Int,

    val completed: Boolean
)