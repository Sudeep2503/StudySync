
package com.example.studysync.ui.timer

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.studysync.R
import com.example.studysync.data.AppDatabase
import com.example.studysync.data.StudySession
import com.example.studysync.data.SubjectRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PomodoroFragment : Fragment() {

    private lateinit var tvTimer: TextView
    private lateinit var tvStatus: TextView
    private lateinit var progressTimer: ProgressBar

    private lateinit var btnStart: Button
    private lateinit var btnPause: Button
    private lateinit var btnReset: Button

    private var countDownTimer: CountDownTimer? = null

    private var totalTimeMillis = DEFAULT_DURATION_MINUTES * 60 * 1000L
    private var timeLeftMillis = totalTimeMillis

    private var isRunning = false
    private var sessionSaved = false

    private var subjectId = 0
    private var subjectName = "Focus Session"
    private var topicName = "Pomodoro"
    private var durationMinutes = DEFAULT_DURATION_MINUTES

    private var sessionStartDate = ""
    private var sessionStartTime = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments

        subjectName = args?.getString(ARG_SUBJECT_NAME)
            ?: "Focus Session"

        topicName = args?.getString(ARG_TOPIC_NAME)
            ?: "Pomodoro"

        durationMinutes = (
                args?.getInt(
                    ARG_DURATION,
                    DEFAULT_DURATION_MINUTES
                ) ?: DEFAULT_DURATION_MINUTES
                ).coerceIn(1, 180)

        totalTimeMillis = durationMinutes * 60 * 1000L
        timeLeftMillis = totalTimeMillis

        subjectId = args?.getInt(ARG_SUBJECT_ID, 0) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_pomodoro,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        tvTimer = view.findViewById(R.id.tvTimer)
        tvStatus = view.findViewById(R.id.tvStatus)
        progressTimer = view.findViewById(R.id.progressTimer)

        btnStart = view.findViewById(R.id.btnStart)
        btnPause = view.findViewById(R.id.btnPause)
        btnReset = view.findViewById(R.id.btnReset)

        tvStatus.text =
            "Study: $subjectName\nTopic: $topicName"

        progressTimer.max = 100
        updateTimerDisplay()

        btnStart.setOnClickListener {
            startTimer()
        }

        btnPause.setOnClickListener {
            pauseTimer()
        }

        btnReset.setOnClickListener {
            resetTimer()
        }
    }

    private fun startTimer() {
        if (isRunning || sessionSaved) return

        if (sessionStartDate.isEmpty()) {
            val now = Calendar.getInstance().time

            sessionStartDate = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(now)

            sessionStartTime = SimpleDateFormat(
                "hh:mm a",
                Locale.getDefault()
            ).format(now)
        }

        isRunning = true
        tvStatus.text = "Focusing on $topicName 🔥"

        countDownTimer = object : CountDownTimer(
            timeLeftMillis,
            1000
        ) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftMillis = millisUntilFinished
                updateTimerDisplay()
            }

            override fun onFinish() {
                timeLeftMillis = 0
                isRunning = false
                countDownTimer = null

                updateTimerDisplay()
                progressTimer.progress = 100

                tvStatus.text = "Session completed! 🎉"
                sessionSaved = true

                saveCompletedSession()

                Toast.makeText(
                    requireContext(),
                    "Great work! Study session completed 🎉",
                    Toast.LENGTH_LONG
                ).show()
            }
        }.start()
    }

    private fun pauseTimer() {
        if (!isRunning) return

        countDownTimer?.cancel()
        countDownTimer = null
        isRunning = false

        tvStatus.text = "Session paused ⏸️"
        updateTimerDisplay()
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        countDownTimer = null

        isRunning = false
        sessionSaved = false
        timeLeftMillis = totalTimeMillis

        sessionStartDate = ""
        sessionStartTime = ""

        progressTimer.progress = 0
        tvStatus.text = "Ready to study $topicName? 🚀"

        updateTimerDisplay()
    }

    private fun updateTimerDisplay() {
        val totalSeconds = timeLeftMillis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        tvTimer.text = String.format(
            Locale.getDefault(),
            "%02d:%02d",
            minutes,
            seconds
        )

        val elapsed = totalTimeMillis - timeLeftMillis

        val progress = (
                elapsed.toDouble() /
                        totalTimeMillis.toDouble() * 100
                ).toInt().coerceIn(0, 100)

        progressTimer.progress = progress
    }

    private fun saveCompletedSession() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val database = AppDatabase.getDatabase(
                    requireContext()
                )

                val repository = SubjectRepository(
                    subjectDao = database.subjectDao(),
                    topicDao = database.topicDao(),
                    studySessionDao = database.studySessionDao()
                )

                // Resolve the real subject ID from Room.
                val subjects = repository.allSubjects.first()

                val matchingSubject = subjects.firstOrNull {
                    it.name.equals(subjectName, ignoreCase = true)
                }

                val resolvedSubjectId =
                    matchingSubject?.id ?: subjectId

                val session = StudySession(
                    subjectId = resolvedSubjectId,
                    subjectName = subjectName,
                    topicName = topicName,
                    date = sessionStartDate,
                    startTime = sessionStartTime,
                    duration = durationMinutes,
                    completed = true
                )

                repository.addStudySession(session)

                Toast.makeText(
                    requireContext(),
                    "Study session saved successfully",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                sessionSaved = false

                Toast.makeText(
                    requireContext(),
                    "Could not save session: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        countDownTimer?.cancel()
        countDownTimer = null
        isRunning = false

        super.onDestroyView()
    }

    companion object {
        private const val DEFAULT_DURATION_MINUTES = 25

        private const val ARG_SUBJECT_ID = "subjectId"
        private const val ARG_SUBJECT_NAME = "subjectName"
        private const val ARG_TOPIC_NAME = "topicName"
        private const val ARG_DURATION = "durationMinutes"

        fun newInstance(
            subjectName: String = "Focus Session",
            topicName: String = "Pomodoro",
            durationMinutes: Int = DEFAULT_DURATION_MINUTES,
            subjectId: Int = 0
        ): PomodoroFragment {
            return PomodoroFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SUBJECT_ID, subjectId)
                    putString(ARG_SUBJECT_NAME, subjectName)
                    putString(ARG_TOPIC_NAME, topicName)
                    putInt(ARG_DURATION, durationMinutes)
                }
            }
        }
    }
}
