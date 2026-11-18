package com.example.studysync.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    @Insert
    suspend fun insertSubject(
        subject: Subject
    )

    @Update
    suspend fun updateSubject(
        subject: Subject
    )

    @Delete
    suspend fun deleteSubject(
        subject: Subject
    )

    @Query("SELECT * FROM subjects ORDER BY examDate ASC")
    fun getAllSubjects(): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE id = :id LIMIT 1")
    suspend fun getSubjectById(
        id: Int
    ): Subject?

    @Query(
        "UPDATE subjects SET progress = :progress WHERE id = :subjectId"
    )
    suspend fun updateProgress(
        subjectId: Int,
        progress: Int
    )

    @Query("SELECT COUNT(*) FROM subjects")
    fun getTotalSubjects(): Flow<Int>
}