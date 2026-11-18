package com.example.studysync.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySessionDao {

    @Insert
    suspend fun insertSession(
        session: StudySession
    )

    @Update
    suspend fun updateSession(
        session: StudySession
    )

    @Delete
    suspend fun deleteSession(
        session: StudySession
    )

    // Get all study sessions
    @Query(
        "SELECT * FROM study_sessions ORDER BY date ASC, startTime ASC"
    )
    fun getAllSessions(): Flow<List<StudySession>>

    // Get sessions for a particular date
    @Query(
        "SELECT * FROM study_sessions WHERE date = :date ORDER BY startTime ASC"
    )
    fun getSessionsForDate(
        date: String
    ): Flow<List<StudySession>>

    // Update completion status
    @Query(
        "UPDATE study_sessions SET completed = :completed WHERE id = :sessionId"
    )
    suspend fun updateSessionStatus(
        sessionId: Int,
        completed: Boolean
    )

    // Total study minutes
    @Query(
        "SELECT COALESCE(SUM(duration), 0) FROM study_sessions"
    )
    fun getTotalStudyMinutes(): Flow<Int>

    // Total number of sessions
    @Query(
        "SELECT COUNT(*) FROM study_sessions"
    )
    fun getSessionCount(): Flow<Int>

    // Completed study minutes for a particular date
    @Query(
        "SELECT COALESCE(SUM(duration), 0) FROM study_sessions WHERE date = :date AND completed = 1"
    )
    suspend fun getCompletedStudyMinutes(
        date: String
    ): Int
}