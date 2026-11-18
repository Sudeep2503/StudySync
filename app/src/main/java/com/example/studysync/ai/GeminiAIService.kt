
package com.example.studysync.ai

import com.example.studysync.BuildConfig
import com.google.genai.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class GeminiAIService : AIService {

    private val client: Client by lazy {
        Client.builder()
            .apiKey(BuildConfig.GEMINI_API_KEY)
            .build()
    }

    override suspend fun getStudyRecommendation(
        context: StudyContext
    ): AIRecommendation = withContext(Dispatchers.IO) {

        val prompt = buildRecommendationPrompt(context)

        val response = client.models.generateContent(
            "gemini-3.8-flash",
            prompt,
            null
        )

        val responseText = response.text()?.trim()
            ?: throw Exception("Gemini returned an empty response")

        parseRecommendation(responseText)
    }

    override suspend fun getStudyDNAInsights(
        context: StudyContext
    ): String = withContext(Dispatchers.IO) {

        val prompt = buildStudyDNAInsightsPrompt(context)

        val response = client.models.generateContent(
            "gemini-3.8-flash",
            prompt,
            null
        )

        response.text()?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: throw Exception("Gemini returned an empty Study DNA analysis")
    }

    private fun buildRecommendationPrompt(
        context: StudyContext
    ): String {

        val subjectsText = context.subjects.joinToString("\n") { subject ->
            val topics = subject.topics.joinToString(", ") { topic ->
                "${topic.name} (${if (topic.completed) "completed" else "incomplete"})"
            }

            """
            Subject: ${subject.name}
            Exam Date: ${subject.examDate}
            Target Score: ${subject.targetScore}
            Progress: ${subject.progress}%
            Topics: $topics
            """.trimIndent()
        }

        val sessionsText = context.recentSessions
            .takeLast(20)
            .joinToString("\n") { session ->
                "${session.date} | ${session.subjectName} | " +
                        "${session.topicName} | " +
                        "${session.duration} minutes | " +
                        "Completed: ${session.completed}"
            }

        return """
            You are the AI study recommendation engine for StudySync.

            Analyze the student's study data and recommend exactly ONE
            subject and ONE incomplete topic that the student should study next.

            Consider:
            1. Exam date
            2. Current subject progress
            3. Incomplete topics
            4. Target score
            5. Recent study history
            6. Avoid repeatedly recommending a topic that was just studied
            7. Choose a realistic study duration

            Student data:

            SUBJECTS:
            $subjectsText

            RECENT STUDY SESSIONS:
            $sessionsText

            TOTAL STUDY MINUTES:
            ${context.totalStudyMinutes}

            TOTAL SESSIONS:
            ${context.totalSessions}

            Return ONLY valid JSON.
            Do not use markdown.
            Do not use code fences.

            Required JSON format:

            {
              "subjectName": "Subject name",
              "topicName": "Topic name",
              "durationMinutes": 30,
              "reason": "Short explanation",
              "priority": "HIGH"
            }

            Priority must be one of:
            HIGH
            MEDIUM
            LOW
        """.trimIndent()
    }

    private fun buildStudyDNAInsightsPrompt(
        context: StudyContext
    ): String {

        val subjectsText = context.subjects.joinToString("\n") { subject ->
            val topics = subject.topics.joinToString("\n") { topic ->
                "- ${topic.name}: ${
                    if (topic.completed) "Completed" else "Incomplete"
                }"
            }

            """
            Subject: ${subject.name}
            Exam date: ${subject.examDate}
            Target score: ${subject.targetScore}
            Recorded progress: ${subject.progress}%
            Topics:
            ${topics.ifBlank { "No topics recorded" }}
            """.trimIndent()
        }

        val sessionsText = context.recentSessions
            .takeLast(20)
            .joinToString("\n") { session ->
                """
                Date: ${session.date}
                Start time: ${session.startTime}
                Subject: ${session.subjectName}
                Topic: ${session.topicName}
                Duration: ${session.duration} minutes
                Completed: ${session.completed}
                """.trimIndent()
            }

        return """
            You are StudySync's personalized Study DNA analytics assistant.

            Analyze only the student data provided below.

            Write a concise, personalized report with these headings:

            1. STUDY CONSISTENCY
            Assess consistency only to the extent supported by the
            available session dates. Do not invent streaks or frequencies.

            2. SUBJECT FOCUS
            Identify subjects represented in the recorded sessions.
            Do not claim a preference or weakness without supporting data.

            3. TOPIC PROGRESS
            Summarize completed and incomplete topics using the supplied data.

            4. WHAT IS WORKING
            Identify a positive pattern only if the data supports it.

            5. RECOMMENDED NEXT STEP
            Give one specific, practical action based on the student's
            incomplete topics, exam dates, and recorded study history.

            6. IMPROVEMENT SUGGESTION
            Give one realistic suggestion to improve the student's study routine.

            Rules:
            - Use clear, encouraging, professional language.
            - Be specific and actionable.
            - Do not invent statistics, study habits, weaknesses, or trends.
            - Distinguish observed facts from suggestions.
            - If there is insufficient data, state what is missing.
            - Do not claim that a topic was studied unless a session supports it.
            - Return plain text with short headings and bullet points.
            - Do not return JSON or code fences.

            STUDENT SUBJECTS AND TOPICS:
            ${subjectsText.ifBlank { "No subject data available." }}

            RECENT STUDY SESSIONS:
            ${sessionsText.ifBlank { "No recent study sessions available." }}

            TOTAL RECORDED STUDY MINUTES:
            ${context.totalStudyMinutes}

            TOTAL RECORDED SESSIONS:
            ${context.totalSessions}
        """.trimIndent()
    }

    private fun parseRecommendation(
        responseText: String
    ): AIRecommendation {

        val cleanedText = responseText
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val json = JSONObject(cleanedText)

        return AIRecommendation(
            subjectName = json.getString("subjectName"),
            topicName = json.getString("topicName"),
            durationMinutes = json.getInt("durationMinutes"),
            reason = json.getString("reason"),
            priority = json.getString("priority")
        )
    }
}
