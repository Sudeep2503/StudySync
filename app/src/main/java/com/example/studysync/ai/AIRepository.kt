
package com.example.studysync.ai

import com.example.studysync.data.SubjectRepository

class AIRepository(
    private val repository: SubjectRepository,
    private val aiService: AIService
) {
    private val contextBuilder = StudyContextBuilder(repository)

    suspend fun getStudyRecommendation(): AIRecommendation {
        val context = contextBuilder.buildContext()

        // Do not recommend a topic if there are no incomplete topics.
        val availableTopics = context.subjects.flatMap { subject ->
            subject.topics
                .filter { topic -> !topic.completed }
                .map { topic -> subject to topic }
        }

        if (availableTopics.isEmpty()) {
            throw IllegalStateException(
                "No incomplete topics found. Add a topic or mark one incomplete."
            )
        }

        // Ask Gemini for a recommendation.
        val recommendation = aiService.getStudyRecommendation(context)

        // Verify that the subject and topic exist in Room.
        val validSubject = context.subjects.firstOrNull {
            it.name.equals(
                recommendation.subjectName.trim(),
                ignoreCase = true
            )
        }

        val validTopic = validSubject?.topics?.firstOrNull {
            it.name.equals(
                recommendation.topicName.trim(),
                ignoreCase = true
            ) && !it.completed
        }

        // Accept only a recommendation that matches real database data.
        if (validSubject != null && validTopic != null) {
            return recommendation.copy(
                subjectName = validSubject.name,
                topicName = validTopic.name,
                durationMinutes = recommendation.durationMinutes.coerceIn(5, 120),
                priority = recommendation.priority
                    .uppercase()
                    .takeIf { it in listOf("HIGH", "MEDIUM", "LOW") }
                    ?: "MEDIUM"
            )
        }

        // Preserve the existing fallback for invalid recommendation topics.
        val fallback = availableTopics.first()
        val subject = fallback.first
        val topic = fallback.second

        return AIRecommendation(
            subjectName = subject.name,
            topicName = topic.name,
            durationMinutes = 25,
            reason = "Suggested from your actual study plan because the AI response did not match an available topic.",
            priority = "MEDIUM"
        )
    }

    /**
     * Generates personalized Study DNA insights using Gemini.
     * This method is called only when the user requests AI insights.
     */
    suspend fun getStudyDNAInsights(): String {
        val context = contextBuilder.buildContext()

        return aiService.getStudyDNAInsights(context)
    }
}
