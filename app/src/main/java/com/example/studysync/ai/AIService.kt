
package com.example.studysync.ai

interface AIService {

    suspend fun getStudyRecommendation(
        context: StudyContext
    ): AIRecommendation

    suspend fun getStudyDNAInsights(
        context: StudyContext
    ): String
}
