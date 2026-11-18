package com.example.studysync.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.studysync.R
import com.example.studysync.data.AppDatabase
import com.example.studysync.data.StudySession
import com.example.studysync.data.SubjectRepository
import com.example.studysync.ui.timer.PomodoroFragment
import com.example.studysync.viewmodel.DashboardViewModel
import com.example.studysync.viewmodel.DashboardViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.studysync.ai.AIRepository
import com.example.studysync.ai.GeminiAIService
import com.example.studysync.viewmodel.AIViewModel
import com.example.studysync.viewmodel.AIViewModelFactory
import com.example.studysync.ui.profile.ProfileFragment
import com.example.studysync.ui.planner.PlannerFragment
class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var aiViewModel: AIViewModel
    private lateinit var tvGreeting: TextView
    private lateinit var tvSubjectCount: TextView
    private lateinit var tvAIStatus: TextView
    private lateinit var tvAISubject: TextView
    private lateinit var tvAITopic: TextView
    private lateinit var tvAIDuration: TextView
    private lateinit var tvAIReason: TextView
    private lateinit var tvAIPriority: TextView
    private lateinit var btnAIStart: Button
    private lateinit var tvProgress: TextView
    private lateinit var tvStudyTime: TextView
    private lateinit var progressToday: ProgressBar
    private lateinit var btnStartStudy: Button
    private lateinit var scheduleContainer: LinearLayout
    private lateinit var btnOpenProfile: TextView
    private lateinit var btnSeeAllSchedule: TextView
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_dashboard,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        initializeViews(view)

        setupDatabase()

        setupViewModel()

        setupClickListeners()

        updateGreeting()

        observeSubjects()

        observeTodaySessions()

        observeAIRecommendation()
    }

    private fun initializeViews(
        view: View
    ) {

        tvGreeting =
            view.findViewById(
                R.id.tvGreeting
            )

        tvSubjectCount =
            view.findViewById(
                R.id.tvSubjectCount
            )

        tvProgress =
            view.findViewById(
                R.id.tvProgress
            )

        tvStudyTime =
            view.findViewById(
                R.id.tvStudyTime
            )

        progressToday =
            view.findViewById(
                R.id.progressToday
            )

        btnStartStudy =
            view.findViewById(
                R.id.btnStartStudy
            )

        scheduleContainer =
            view.findViewById(
                R.id.scheduleContainer
            )

        tvSubjectCount.text =
            "0"

        tvProgress.text =
            "0% completed"

        tvStudyTime.text =
            "0 min"

        progressToday.progress =
            0
        tvAIStatus =
            view.findViewById(
                R.id.tvAIStatus
            )

        tvAISubject =
            view.findViewById(
                R.id.tvAISubject
            )

        tvAITopic =
            view.findViewById(
                R.id.tvAITopic
            )

        tvAIDuration =
            view.findViewById(
                R.id.tvAIDuration
            )

        tvAIReason =
            view.findViewById(
                R.id.tvAIReason
            )

        tvAIPriority =
            view.findViewById(
                R.id.tvAIPriority
            )

        btnAIStart =
            view.findViewById(
                R.id.btnAIStart
            )
        btnOpenProfile =
            view.findViewById(R.id.btnOpenProfile)

        btnSeeAllSchedule =
            view.findViewById(R.id.btnSeeAllSchedule)
    }

    private fun setupDatabase() {

        database =
            AppDatabase.getDatabase(
                requireContext()
            )
    }

    private fun setupViewModel() {

        val repository =
            SubjectRepository(
                subjectDao =
                    database.subjectDao(),

                topicDao =
                    database.topicDao(),

                studySessionDao =
                    database.studySessionDao()
            )

        val factory =
            DashboardViewModelFactory(
                repository
            )

        dashboardViewModel =
            ViewModelProvider(
                this,
                factory
            )[DashboardViewModel::class.java]
        val aiRepository =
            AIRepository(
                repository = repository,
                aiService = GeminiAIService()
            )

        val aiFactory =
            AIViewModelFactory(
                aiRepository
            )

        aiViewModel =
            ViewModelProvider(
                this,
                aiFactory
            )[AIViewModel::class.java]
    }


    private fun setupClickListeners() {
        btnStartStudy.setOnClickListener {
            openPomodoro()
        }


        btnAIStart.setOnClickListener {
            val recommendation = aiViewModel.recommendation.value

            when {
                aiViewModel.isLoading.value -> {
                    // A Gemini request is already in progress.
                }

                recommendation != null &&
                        aiViewModel.error.value == null -> {
                    openPomodoro(
                        subjectName = recommendation.subjectName,
                        topicName = recommendation.topicName,
                        durationMinutes = recommendation.durationMinutes
                    )
                }

                else -> {
                    aiViewModel.generateStudyRecommendation()
                }
            }
        }
        btnOpenProfile.setOnClickListener {
            openProfile()
        }

        btnSeeAllSchedule.setOnClickListener {
            openPlanner()
        }

    }



    private fun openPomodoro(
        subjectName: String = "Focus Session",
        topicName: String = "Pomodoro",
        durationMinutes: Int = 25
    ) {
        parentFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragmentContainer,
                PomodoroFragment.newInstance(
                    subjectName = subjectName,
                    topicName = topicName,
                    durationMinutes = durationMinutes
                )
            )
            .addToBackStack(null)
            .commit()
    }


    private fun updateGreeting() {

        val hour =
            Calendar.getInstance()
                .get(Calendar.HOUR_OF_DAY)

        tvGreeting.text =
            when {

                hour < 12 ->
                    "Good Morning 👋"

                hour < 17 ->
                    "Good Afternoon 👋"

                else ->
                    "Good Evening 👋"
            }
    }

    private fun observeSubjects() {

        viewLifecycleOwner.lifecycleScope.launch {

            dashboardViewModel
                .subjects
                .collect { subjects ->

                    updateDashboard(
                        subjects
                    )
                }
        }
    }

    private fun updateDashboard(
        subjects: List<com.example.studysync.data.Subject>
    ) {

        val subjectCount =
            subjects.size

        tvSubjectCount.text =
            subjectCount.toString()

        if (subjects.isEmpty()) {

            tvProgress.text =
                "0% completed"

            progressToday.progress =
                0

            return
        }

        val totalProgress =
            subjects.sumOf {
                it.progress
            }

        val averageProgress =
            totalProgress / subjects.size

        tvProgress.text =
            "$averageProgress% completed"

        progressToday.progress =
            averageProgress
    }

    private fun observeTodaySessions() {

        val today =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(
                Calendar.getInstance().time
            )

        viewLifecycleOwner.lifecycleScope.launch {

            dashboardViewModel
                .getSessionsForDate(today)
                .collect { sessions ->

                    updateStudyTime(
                        sessions
                    )

                    updateSchedule(
                        sessions
                    )
                }
        }
    }

    private fun updateStudyTime(
        sessions: List<StudySession>
    ) {

        val completedMinutes =
            sessions
                .filter {
                    it.completed
                }
                .sumOf {
                    it.duration
                }

        tvStudyTime.text =
            if (completedMinutes < 60) {

                "$completedMinutes min"

            } else {

                val hours =
                    completedMinutes / 60

                val minutes =
                    completedMinutes % 60

                if (minutes == 0) {
                    "${hours}h"
                } else {
                    "${hours}h ${minutes}m"
                }
            }
    }

    private fun updateSchedule(
        sessions: List<StudySession>
    ) {

        scheduleContainer.removeAllViews()

        if (sessions.isEmpty()) {

            val emptyText =
                TextView(requireContext())

            emptyText.text =
                "No study sessions planned for today.\nAdd a session from the Planner 📅"

            emptyText.textSize =
                15f

            emptyText.setTextColor(
                resources.getColor(
                    R.color.text_secondary,
                    requireContext().theme
                )
            )

            emptyText.setPadding(
                18,
                18,
                18,
                18
            )

            scheduleContainer.addView(
                emptyText
            )

            return
        }

        sessions.forEachIndexed { index, session ->

            val sessionText =
                TextView(requireContext())

            val status =
                if (session.completed) {
                    "✓ Completed"
                } else {
                    "○ Planned"
                }

            sessionText.text =
                "${session.startTime}  •  ${session.subjectName}\n" +
                        "${session.topicName}  •  ${session.duration} min  •  $status"

            sessionText.textSize =
                15f

            sessionText.setTextColor(
                resources.getColor(
                    R.color.text_primary,
                    requireContext().theme
                )
            )

            sessionText.setPadding(
                18,
                18,
                18,
                18
            )

            scheduleContainer.addView(
                sessionText
            )

            if (index < sessions.lastIndex) {

                val divider =
                    View(requireContext())

                divider.layoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                    )

                divider.setBackgroundColor(
                    resources.getColor(
                        R.color.divider,
                        requireContext().theme
                    )
                )

                scheduleContainer.addView(
                    divider
                )
            }
        }
    }


    private fun observeAIRecommendation() {

        viewLifecycleOwner.lifecycleScope.launch {
            aiViewModel.recommendation.collect { recommendation ->

                if (recommendation != null) {
                    tvAISubject.text = recommendation.subjectName
                    tvAITopic.text = "Topic: ${recommendation.topicName}"
                    tvAIDuration.text =
                        "⏱ ${recommendation.durationMinutes} minutes"
                    tvAIReason.text = recommendation.reason
                    tvAIPriority.text = recommendation.priority

                    if (aiViewModel.error.value == null) {
                        tvAIStatus.text =
                            "Personalized for your current study progress"
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            aiViewModel.isLoading.collect { loading ->

                if (loading) {
                    tvAIStatus.text = "✨ Gemini is analyzing your study data..."
                    tvAIReason.text = "Preparing your personalized recommendation..."
                    btnAIStart.text = "Generating..."
                    btnAIStart.isEnabled = false
                } else {
                    btnAIStart.text = if (aiViewModel.error.value != null) {
                        "↻ Retry AI Recommendation"
                    } else {
                        "▶ Start Recommended Study"
                    }

                    btnAIStart.isEnabled =
                        !aiViewModel.recommendation.value.let {
                            it == null
                        } || aiViewModel.error.value != null
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            aiViewModel.error.collect { error ->

                if (error != null) {
                    tvAIStatus.text = "AI temporarily unavailable"

                    tvAIReason.text = error

                    tvAISubject.text = "No new recommendation"
                    tvAITopic.text = "Waiting for Gemini AI"
                    tvAIDuration.text = "—"
                    tvAIPriority.text = "—"

                    btnAIStart.text = "↻ Retry AI Recommendation"
                    btnAIStart.isEnabled = !aiViewModel.isLoading.value
                } else if (!aiViewModel.isLoading.value) {
                    btnAIStart.text =
                        if (aiViewModel.recommendation.value != null) {
                            "▶ Start Recommended Study"
                        } else {
                            "✨ Generate AI Recommendation"
                        }

                    btnAIStart.isEnabled =
                        aiViewModel.recommendation.value != null
                }
            }
        }

    }
    private fun openProfile() {

        parentFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragmentContainer,
                ProfileFragment()
            )
            .addToBackStack("profile")
            .commit()
    }

    private fun openPlanner() {

        parentFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragmentContainer,
                PlannerFragment()
            )
            .addToBackStack("planner")
            .commit()
    }

    override fun onDestroyView() {

        super.onDestroyView()
    }
}