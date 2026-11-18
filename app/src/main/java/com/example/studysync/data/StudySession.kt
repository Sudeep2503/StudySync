package com.example.studysync.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_sessions")
data class StudySession(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val subjectId: Int = 0,

    val subjectName: String = "General Study",

    val topicName: String = "Pomodoro Session",

    val date: String = "",

    val startTime: String = "",

    val duration: Int = 25,

    val completed: Boolean = false
)