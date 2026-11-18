
package com.example.studysync.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysync.ai.AIRecommendation
import com.example.studysync.ai.AIRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AIViewModel(
    private val aiRepository: AIRepository
) : ViewModel() {

    // Existing study recommendation state
    private val _recommendation =
        MutableStateFlow<AIRecommendation?>(null)

    val recommendation: StateFlow<AIRecommendation?> =
        _recommendation.asStateFlow()

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    private val _error =
        MutableStateFlow<String?>(null)

    val error: StateFlow<String?> =
        _error.asStateFlow()

    private var recommendationJob: Job? = null

    // Separate Study DNA insights state
    private val _studyDNAInsights =
        MutableStateFlow<String?>(null)

    val studyDNAInsights: StateFlow<String?> =
        _studyDNAInsights.asStateFlow()

    private val _isLoadingStudyDNA =
        MutableStateFlow(false)

    val isLoadingStudyDNA: StateFlow<Boolean> =
        _isLoadingStudyDNA.asStateFlow()

    private val _studyDNAError =
        MutableStateFlow<String?>(null)

    val studyDNAError: StateFlow<String?> =
        _studyDNAError.asStateFlow()

    private var studyDNAJob: Job? = null

    /**
     * Generates a study recommendation.
     * Existing Dashboard functionality is preserved.
     */
    fun generateStudyRecommendation() {
        if (_isLoading.value || recommendationJob?.isActive == true) {
            return
        }

        recommendationJob = viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val result =
                    aiRepository.getStudyRecommendation()

                _recommendation.value = result

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                Log.e(
                    "StudySyncAI",
                    "Gemini recommendation request failed",
                    e
                )

                _error.value = getUserFriendlyError(e)

            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Generates Study DNA insights only when explicitly requested.
     * Opening the Analytics screen does not call this method.
     */
    fun generateStudyDNAInsights() {
        if (_isLoadingStudyDNA.value || studyDNAJob?.isActive == true) {
            return
        }

        studyDNAJob = viewModelScope.launch {
            _isLoadingStudyDNA.value = true
            _studyDNAError.value = null

            try {
                val result =
                    aiRepository.getStudyDNAInsights()

                _studyDNAInsights.value = result

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                Log.e(
                    "StudySyncAI",
                    "Gemini Study DNA insights request failed",
                    e
                )

                _studyDNAError.value = getUserFriendlyError(e)

            } finally {
                _isLoadingStudyDNA.value = false
            }
        }
    }

    private fun getUserFriendlyError(e: Exception): String {
        val message = e.message.orEmpty()

        return when {
            message.contains("429") ||
                    message.contains(
                        "RESOURCE_EXHAUSTED",
                        ignoreCase = true
                    ) -> {
                "Gemini AI quota reached. Please try again when your quota resets."
            }

            message.contains("503") ||
                    message.contains(
                        "UNAVAILABLE",
                        ignoreCase = true
                    ) -> {
                "Gemini AI is temporarily unavailable. Please try again later."
            }

            else -> {
                "Unable to generate AI insights. Please try again later."
            }
        }
    }
}
