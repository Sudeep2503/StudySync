package com.example.studysync.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {

    @Insert
    suspend fun insertTopic(
        topic: Topic
    )

    @Delete
    suspend fun deleteTopic(
        topic: Topic
    )

    @Query(
        "SELECT * FROM topics WHERE subjectId = :subjectId"
    )
    fun getTopicsForSubject(
        subjectId: Int
    ): Flow<List<Topic>>

    @Query(
        "UPDATE topics SET completed = :completed WHERE id = :topicId"
    )
    suspend fun updateTopicStatus(
        topicId: Int,
        completed: Boolean
    )

    @Query(
        "SELECT * FROM topics WHERE subjectId = :subjectId"
    )
    suspend fun getTopicsForSubjectOnce(
        subjectId: Int
    ): List<Topic>

    @Query(
        "SELECT COUNT(*) FROM topics WHERE completed = 1"
    )
    fun getCompletedTopics(): Flow<Int>
}