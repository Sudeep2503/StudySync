package com.example.studysync.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class Subject(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val examDate: String,

    val targetScore: Int,

    val progress: Int = 0
)