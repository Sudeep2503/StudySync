package com.example.studysync.ai

data class AIRecommendation(

    val subjectName: String,

    val topicName: String,

    val durationMinutes: Int,

    val reason: String,

    val priority: String
)