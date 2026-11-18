package com.example.studysync.ui.planner

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studysync.R
import com.example.studysync.data.AppDatabase
import com.example.studysync.data.StudySession
import com.example.studysync.data.Subject
import com.example.studysync.data.SubjectRepository
import com.example.studysync.viewmodel.PlannerViewModel
import com.example.studysync.viewmodel.PlannerViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PlannerFragment : Fragment() {

    private lateinit var viewModel: PlannerViewModel
    private lateinit var sessionAdapter: StudySessionAdapter

    private lateinit var tvPlannerDate: TextView
    private lateinit var tvDailyProgress: TextView
    private lateinit var progressDaily: ProgressBar
    private lateinit var recyclerSessions: RecyclerView
    private lateinit var layoutEmptyPlanner: View
    private lateinit var btnAddSession: Button

    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment_planner,
            container,
            false
        )

        tvPlannerDate =
            view.findViewById(R.id.tvPlannerDate)

        tvDailyProgress =
            view.findViewById(R.id.tvDailyProgress)

        progressDaily =
            view.findViewById(R.id.progressDaily)

        recyclerSessions =
            view.findViewById(R.id.recyclerSessions)

        layoutEmptyPlanner =
            view.findViewById(R.id.layoutEmptyPlanner)

        btnAddSession =
            view.findViewById(R.id.btnAddSession)

        setupViewModel()

        setupRecyclerView()

        updateDateText()

        observeSessions()

        btnAddSession.setOnClickListener {
            showAddSessionDialog()
        }

        return view
    }

    private fun setupViewModel() {

        val database =
            AppDatabase.getDatabase(
                requireContext()
            )

        val repository =
            SubjectRepository(
                subjectDao = database.subjectDao(),
                topicDao = database.topicDao(),
                studySessionDao = database.studySessionDao()
            )
        val factory =
            PlannerViewModelFactory(
                repository
            )

        viewModel =
            ViewModelProvider(
                this,
                factory
            )[PlannerViewModel::class.java]
    }

    private fun setupRecyclerView() {

        sessionAdapter =
            StudySessionAdapter { session, completed ->

                viewModel.updateSessionStatus(
                    session.id,
                    completed
                )
            }

        recyclerSessions.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        recyclerSessions.adapter =
            sessionAdapter
    }

    private fun updateDateText() {

        val formatter =
            SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
            )

        tvPlannerDate.text =
            formatter.format(
                calendar.time
            )
    }

    private fun getSelectedDate(): String {

        val formatter =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            )

        return formatter.format(
            calendar.time
        )
    }

    private fun observeSessions() {

        lifecycleScope.launch {

            viewModel
                .getSessionsForDate(
                    getSelectedDate()
                )
                .collectLatest { sessions ->

                    sessionAdapter.submitList(
                        sessions
                    )

                    updateProgress(
                        sessions
                    )

                    layoutEmptyPlanner.visibility =
                        if (sessions.isEmpty()) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                }
        }
    }

    private fun updateProgress(
        sessions: List<StudySession>
    ) {

        if (sessions.isEmpty()) {

            tvDailyProgress.text = "0%"
            progressDaily.progress = 0

            return
        }

        val completed =
            sessions.count {
                it.completed
            }

        val progress =
            (completed * 100) / sessions.size

        tvDailyProgress.text =
            "$progress%"

        progressDaily.progress =
            progress
    }

    private fun showAddSessionDialog() {

        val dialogView =
            LayoutInflater.from(
                requireContext()
            ).inflate(
                R.layout.dialog_add_study_session,
                null
            )

        val spinnerSubject =
            dialogView.findViewById<Spinner>(
                R.id.spinnerSessionSubject
            )

        val etTopic =
            dialogView.findViewById<EditText>(
                R.id.etSessionTopic
            )

        val etDate =
            dialogView.findViewById<EditText>(
                R.id.etSessionDate
            )

        val etTime =
            dialogView.findViewById<EditText>(
                R.id.etSessionTime
            )

        val etDuration =
            dialogView.findViewById<EditText>(
                R.id.etSessionDuration
            )

        val subjects = mutableListOf<Subject>()

        lifecycleScope.launch {

            AppDatabase
                .getDatabase(
                    requireContext()
                )
                .subjectDao()
                .getAllSubjects()
                .collectLatest { subjectList ->

                    subjects.clear()
                    subjects.addAll(subjectList)

                    val names =
                        subjectList.map {
                            it.name
                        }

                    spinnerSubject.adapter =
                        ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            names
                        )
                }
        }

        etDate.setText(
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(calendar.time)
        )

        etDate.setOnClickListener {

            showDatePicker(
                etDate
            )
        }

        etTime.setOnClickListener {

            showTimePicker(
                etTime
            )
        }

        val dialog =
            AlertDialog.Builder(
                requireContext()
            )
                .setTitle(
                    "Add Study Session 📚"
                )
                .setView(dialogView)
                .setNegativeButton(
                    "Cancel",
                    null
                )
                .setPositiveButton(
                    "Add",
                    null
                )
                .create()

        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                if (subjects.isEmpty()) {

                    Toast.makeText(
                        requireContext(),
                        "Please add a subject first",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                val topic =
                    etTopic.text
                        .toString()
                        .trim()

                val date =
                    etDate.text
                        .toString()
                        .trim()

                val time =
                    etTime.text
                        .toString()
                        .trim()

                val durationText =
                    etDuration.text
                        .toString()
                        .trim()

                if (topic.isEmpty()) {

                    etTopic.error =
                        "Enter topic"

                    return@setOnClickListener
                }

                if (date.isEmpty()) {

                    etDate.error =
                        "Select date"

                    return@setOnClickListener
                }

                if (time.isEmpty()) {

                    etTime.error =
                        "Select time"

                    return@setOnClickListener
                }

                if (durationText.isEmpty()) {

                    etDuration.error =
                        "Enter duration"

                    return@setOnClickListener
                }

                val duration =
                    durationText.toIntOrNull()

                if (duration == null ||
                    duration <= 0
                ) {

                    etDuration.error =
                        "Enter valid duration"

                    return@setOnClickListener
                }

                val selectedSubject =
                    subjects[
                        spinnerSubject.selectedItemPosition
                    ]

                val session =
                    StudySession(
                        subjectId =
                            selectedSubject.id,
                        subjectName =
                            selectedSubject.name,
                        topicName =
                            topic,
                        date =
                            date,
                        startTime =
                            time,
                        duration =
                            duration,
                        completed =
                            false
                    )

                viewModel.addSession(
                    session
                )

                Toast.makeText(
                    requireContext(),
                    "Study session added! 🎯",
                    Toast.LENGTH_SHORT
                ).show()

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showDatePicker(
        editText: EditText
    ) {

        val year =
            calendar.get(
                Calendar.YEAR
            )

        val month =
            calendar.get(
                Calendar.MONTH
            )

        val day =
            calendar.get(
                Calendar.DAY_OF_MONTH
            )

        DatePickerDialog(
            requireContext(),
            { _: DatePicker, selectedYear, selectedMonth, selectedDay ->

                val selectedCalendar =
                    Calendar.getInstance()

                selectedCalendar.set(
                    selectedYear,
                    selectedMonth,
                    selectedDay
                )

                val formatter =
                    SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    )

                editText.setText(
                    formatter.format(
                        selectedCalendar.time
                    )
                )
            },
            year,
            month,
            day
        ).show()
    }

    private fun showTimePicker(
        editText: EditText
    ) {

        val hour =
            calendar.get(
                Calendar.HOUR_OF_DAY
            )

        val minute =
            calendar.get(
                Calendar.MINUTE
            )

        TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->

                val selectedCalendar =
                    Calendar.getInstance()

                selectedCalendar.set(
                    Calendar.HOUR_OF_DAY,
                    selectedHour
                )

                selectedCalendar.set(
                    Calendar.MINUTE,
                    selectedMinute
                )

                val formatter =
                    SimpleDateFormat(
                        "hh:mm a",
                        Locale.getDefault()
                    )

                editText.setText(
                    formatter.format(
                        selectedCalendar.time
                    )
                )
            },
            hour,
            minute,
            false
        ).show()
    }
}