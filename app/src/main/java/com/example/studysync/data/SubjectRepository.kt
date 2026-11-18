package com.example.studysync.data

import kotlinx.coroutines.flow.Flow

class SubjectRepository(
    private val subjectDao: SubjectDao,
    private val topicDao: TopicDao,
    private val studySessionDao: StudySessionDao
) {

    // =========================
    // SUBJECTS
    // =========================

    val allSubjects: Flow<List<Subject>>
        get() = subjectDao.getAllSubjects()

    suspend fun insertSubject(
        subject: Subject
    ) {
        subjectDao.insertSubject(subject)
    }

    suspend fun updateSubject(
        subject: Subject
    ) {
        subjectDao.updateSubject(subject)
    }

    suspend fun deleteSubject(
        subject: Subject
    ) {
        subjectDao.deleteSubject(subject)
    }

    suspend fun getSubjectById(
        id: Int
    ): Subject? {
        return subjectDao.getSubjectById(id)
    }

    suspend fun updateProgress(
        subjectId: Int,
        progress: Int
    ) {
        subjectDao.updateProgress(
            subjectId,
            progress
        )
    }

    // =========================
    // PROFILE STATISTICS
    // =========================

    fun getTotalSubjects(): Flow<Int> {

        return subjectDao.getTotalSubjects()
    }

    fun getCompletedTopics(): Flow<Int> {

        return topicDao.getCompletedTopics()
    }

    fun getTotalStudyMinutes(): Flow<Int> {

        return studySessionDao.getTotalStudyMinutes()
    }

    // =========================
    // TOPICS
    // =========================

    fun getTopicsForSubject(
        subjectId: Int
    ): Flow<List<Topic>> {

        return topicDao.getTopicsForSubject(
            subjectId
        )
    }

    suspend fun addTopic(
        topic: Topic
    ) {

        topicDao.insertTopic(
            topic
        )

        updateSubjectProgress(
            topic.subjectId
        )
    }

    suspend fun updateTopicStatus(
        topicId: Int,
        subjectId: Int,
        completed: Boolean
    ) {

        topicDao.updateTopicStatus(
            topicId,
            completed
        )

        updateSubjectProgress(
            subjectId
        )
    }

    suspend fun deleteTopic(
        topic: Topic
    ) {

        topicDao.deleteTopic(
            topic
        )

        updateSubjectProgress(
            topic.subjectId
        )
    }

    private suspend fun updateSubjectProgress(
        subjectId: Int
    ) {

        val topics =
            topicDao.getTopicsForSubjectOnce(
                subjectId
            )

        if (topics.isEmpty()) {

            subjectDao.updateProgress(
                subjectId,
                0
            )

            return
        }

        val completedTopics =
            topics.count {
                it.completed
            }

        val progress =
            (completedTopics * 100) / topics.size

        subjectDao.updateProgress(
            subjectId,
            progress
        )
    }

    // =========================
    // STUDY SESSIONS
    // =========================

    fun getAllStudySessions():
            Flow<List<StudySession>> {

        return studySessionDao.getAllSessions()
    }

    fun getSessionsForDate(
        date: String
    ): Flow<List<StudySession>> {

        return studySessionDao.getSessionsForDate(
            date
        )
    }

    suspend fun addStudySession(
        session: StudySession
    ) {

        studySessionDao.insertSession(
            session
        )
    }

    suspend fun updateStudySession(
        session: StudySession
    ) {

        studySessionDao.updateSession(
            session
        )
    }

    suspend fun deleteStudySession(
        session: StudySession
    ) {

        studySessionDao.deleteSession(
            session
        )
    }

    suspend fun updateStudySessionStatus(
        sessionId: Int,
        completed: Boolean
    ) {

        studySessionDao.updateSessionStatus(
            sessionId,
            completed
        )
    }
}