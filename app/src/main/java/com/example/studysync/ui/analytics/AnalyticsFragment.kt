
package com.example.studysync.ui.analytics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.studysync.R
import com.example.studysync.ai.AIRepository
import com.example.studysync.ai.GeminiAIService
import com.example.studysync.data.AppDatabase
import com.example.studysync.data.StudySession
import com.example.studysync.data.SubjectRepository
import com.example.studysync.viewmodel.AIViewModel
import com.example.studysync.viewmodel.AIViewModelFactory
import com.example.studysync.viewmodel.AnalyticsViewModel
import com.example.studysync.viewmodel.AnalyticsViewModelFactory
import kotlinx.coroutines.launch

class AnalyticsFragment : Fragment() {

    private lateinit var analyticsViewModel: AnalyticsViewModel
    private lateinit var aiViewModel: AIViewModel

    // Existing analytics views
    private lateinit var tvTotalTime: TextView
    private lateinit var tvSessionCount: TextView
    private lateinit var tvOverviewTitle: TextView
    private lateinit var tvOverviewDescription: TextView
    private lateinit var tvRecentSession: TextView

    // Study DNA views
    private lateinit var tvStudyDNAStatus: TextView
    private lateinit var tvStudyDNAConsistency: TextView
    private lateinit var tvStudyDNASubjects: TextView
    private lateinit var tvStudyDNATopics: TextView
    private lateinit var tvStudyDNANextStep: TextView

    // AI Insights views
    private lateinit var btnGenerateStudyDNAInsights: Button
    private lateinit var tvStudyDNAInsights: TextView
    private lateinit var tvStudyDNAInsightsError: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_analytics,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupViewModels()
        observeAnalyticsData()
        observeStudyDNAInsights()
        setupStudyDNAInsightsButton()
    }

    private fun initializeViews(view: View) {

        // Existing analytics views
        tvTotalTime = view.findViewById(R.id.tvTotalTime)
        tvSessionCount = view.findViewById(R.id.tvSessionCount)
        tvOverviewTitle = view.findViewById(R.id.tvOverviewTitle)
        tvOverviewDescription =
            view.findViewById(R.id.tvOverviewDescription)
        tvRecentSession = view.findViewById(R.id.tvRecentSession)

        // Study DNA views
        tvStudyDNAStatus = view.findViewById(R.id.tvStudyDNAStatus)
        tvStudyDNAConsistency =
            view.findViewById(R.id.tvStudyDNAConsistency)
        tvStudyDNASubjects =
            view.findViewById(R.id.tvStudyDNASubjects)
        tvStudyDNATopics =
            view.findViewById(R.id.tvStudyDNATopics)
        tvStudyDNANextStep =
            view.findViewById(R.id.tvStudyDNANextStep)

        // AI Insights views
        btnGenerateStudyDNAInsights =
            view.findViewById(R.id.btnGenerateStudyDNAInsights)
        tvStudyDNAInsights =
            view.findViewById(R.id.tvStudyDNAInsights)
        tvStudyDNAInsightsError =
            view.findViewById(R.id.tvStudyDNAInsightsError)
    }

    private fun setupViewModels() {

        val database = AppDatabase.getDatabase(requireContext())

        // Existing Analytics ViewModel
        val analyticsFactory = AnalyticsViewModelFactory(
            studySessionDao = database.studySessionDao(),
            subjectDao = database.subjectDao(),
            topicDao = database.topicDao()
        )

        analyticsViewModel = ViewModelProvider(
            this,
            analyticsFactory
        )[AnalyticsViewModel::class.java]

        // AI ViewModel for Study DNA insights
        val subjectRepository = SubjectRepository(
            subjectDao = database.subjectDao(),
            topicDao = database.topicDao(),
            studySessionDao = database.studySessionDao()
        )

        val aiRepository = AIRepository(
            repository = subjectRepository,
            aiService = GeminiAIService()
        )

        val aiFactory = AIViewModelFactory(aiRepository)

        aiViewModel = ViewModelProvider(
            this,
            aiFactory
        )[AIViewModel::class.java]
    }

    private fun observeAnalyticsData() {

        viewLifecycleOwner.lifecycleScope.launch {
            analyticsViewModel.totalStudyMinutes.collect { minutes ->
                updateStudyTime(minutes)
                updateLearningOverview(minutes)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            analyticsViewModel.sessionCount.collect { count ->
                tvSessionCount.text = count.toString()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            analyticsViewModel.sessions.collect { sessions ->
                updateRecentSession(sessions)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            analyticsViewModel.studyDNA.collect { stats ->

                tvStudyDNAStatus.text =
                    if (stats.studyDays == 0 && stats.totalTopics == 0) {
                        "📊 Your learning profile is starting"
                    } else {
                        "📊 Your learning profile"
                    }

                tvStudyDNAConsistency.text =
                    "Study consistency: ${stats.studyDays} distinct study day(s)"

                tvStudyDNASubjects.text =
                    "Subject focus: ${stats.focusedSubject}"

                val topicProgress =
                    if (stats.totalTopics == 0) {
                        "No topics added yet"
                    } else {
                        "${stats.completedTopics}/${stats.totalTopics} topics completed"
                    }

                tvStudyDNATopics.text = "Topic progress: $topicProgress"

                tvStudyDNANextStep.text =
                    if (stats.studyDays == 0 && stats.totalTopics == 0) {
                        "Complete a study session and add topics to build your profile."
                    } else {
                        "Completed study time: ${stats.totalStudyMinutes} minutes. Keep making progress!"
                    }
            }
        }
    }


    private fun observeStudyDNAInsights() {

        // Observe generated insights
        viewLifecycleOwner.lifecycleScope.launch {
            aiViewModel.studyDNAInsights.collect { insights ->

                if (!insights.isNullOrBlank()) {
                    tvStudyDNAInsights.text = insights
                    tvStudyDNAInsights.visibility = View.VISIBLE
                    tvStudyDNAInsightsError.visibility = View.GONE
                }
            }
        }

        // Observe loading state
        viewLifecycleOwner.lifecycleScope.launch {
            aiViewModel.isLoadingStudyDNA.collect { loading ->

                btnGenerateStudyDNAInsights.isEnabled = !loading

                btnGenerateStudyDNAInsights.text =
                    if (loading) {
                        "⏳ Generating AI Insights..."
                    } else {
                        "✨ Generate AI Insights"
                    }

                if (loading) {
                    tvStudyDNAInsightsError.visibility = View.GONE
                    tvStudyDNAInsights.text =
                        "Gemini is analyzing your study data..."
                    tvStudyDNAInsights.visibility = View.VISIBLE
                }
            }
        }

        // Observe errors
        viewLifecycleOwner.lifecycleScope.launch {
            aiViewModel.studyDNAError.collect { error ->

                if (!error.isNullOrBlank()) {

                    tvStudyDNAInsightsError.text = error
                    tvStudyDNAInsightsError.visibility = View.VISIBLE

                    tvStudyDNAInsights.text =
                        "Your AI insights could not be generated."

                    tvStudyDNAInsights.visibility = View.VISIBLE
                } else {
                    tvStudyDNAInsightsError.visibility = View.GONE
                }
            }
        }
    }


    private fun setupStudyDNAInsightsButton() {

        btnGenerateStudyDNAInsights.setOnClickListener {

            // Avoid repeated taps while the request is running.
            if (aiViewModel.isLoadingStudyDNA.value) {
                return@setOnClickListener
            }

            tvStudyDNAInsightsError.text = ""
            tvStudyDNAInsightsError.visibility = View.GONE

            tvStudyDNAInsights.text =
                "Preparing your personalized Study DNA analysis..."
            tvStudyDNAInsights.visibility = View.VISIBLE

            // The only place this screen initiates a Gemini insights request.
            aiViewModel.generateStudyDNAInsights()
        }
    }

    private fun updateStudyTime(minutes: Int) {

        tvTotalTime.text =
            if (minutes >= 60) {
                "${minutes / 60}h ${minutes % 60}m"
            } else {
                "$minutes min"
            }
    }

    private fun updateLearningOverview(minutes: Int) {

        if (minutes <= 0) {
            tvOverviewTitle.text = "Start your learning journey 🚀"
            tvOverviewDescription.text =
                "Complete your first study session to start building your learning statistics."
        } else {
            tvOverviewTitle.text = "Your learning journey is growing 🎯"
            tvOverviewDescription.text =
                "You have recorded $minutes minutes of study time. Keep building your learning habit with regular sessions."
        }
    }

    private fun updateRecentSession(sessions: List<StudySession>) {

        val latestSession = sessions.lastOrNull()

        if (latestSession == null) {
            tvRecentSession.text = "No sessions completed yet"
        } else {
            tvRecentSession.text =
                "${latestSession.subjectName} • " +
                        "${latestSession.topicName} • " +
                        "${latestSession.duration} min"
        }
    }
}
