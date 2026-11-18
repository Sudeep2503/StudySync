package com.example.studysync.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topics")
data class Topic(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val subjectId: Int,

    val topicName: String,

    val difficulty: String,

    val completed: Boolean = false

)