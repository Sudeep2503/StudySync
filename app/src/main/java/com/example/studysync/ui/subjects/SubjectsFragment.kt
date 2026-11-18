package com.example.studysync.ui.subjects

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studysync.R
import com.example.studysync.data.AppDatabase
import com.example.studysync.data.SubjectRepository
import com.example.studysync.viewmodel.SubjectViewModel
import com.example.studysync.viewmodel.SubjectViewModelFactory
import kotlinx.coroutines.launch
import java.util.Calendar
import android.widget.TextView
class SubjectsFragment : Fragment() {

    private lateinit var viewModel: SubjectViewModel

    private lateinit var recyclerSubjects: RecyclerView

    private lateinit var btnAddSubject: Button

    private lateinit var adapter: SubjectsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_subjects,
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

        recyclerSubjects =
            view.findViewById(
                R.id.recyclerSubjects
            )

        btnAddSubject =
            view.findViewById(
                R.id.btnAddSubject
            )

        setupRecyclerView()

        setupViewModel()

        observeSubjects()

        btnAddSubject.setOnClickListener {

            showAddSubjectDialog()
        }
    }

    private fun setupRecyclerView() {


        adapter = SubjectsAdapter(
            onDelete = { subject ->
                showDeleteConfirmation(subject)
            },
            onUpdateProgress = { subject ->
                showProgressDialog(subject)
            },
            onSubjectClick = { subject ->
                val fragment = com.example.studysync.ui.subjectdetails.SubjectDetailsFragment()

                fragment.arguments = Bundle().apply {
                    putInt("subjectId", subject.id)
                }

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        )

        recyclerSubjects.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        recyclerSubjects.adapter =
            adapter
    }

    private fun setupViewModel() {

        val database =
            AppDatabase.getDatabase(
                requireContext()
            )

        val repository = SubjectRepository(
            subjectDao = database.subjectDao(),
            topicDao = database.topicDao(),
            studySessionDao = database.studySessionDao()
        )

        val factory =
            SubjectViewModelFactory(
                repository
            )

        viewModel =
            ViewModelProvider(
                this,
                factory
            )[SubjectViewModel::class.java]
    }

    private fun observeSubjects() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewModel.subjects.collect { subjects ->

                adapter.submitList(
                    subjects
                )
            }
        }
    }

    private fun showAddSubjectDialog() {

        val dialogView =
            layoutInflater.inflate(
                R.layout.dialog_add_subject,
                null
            )

        val etName =
            dialogView.findViewById<EditText>(
                R.id.etSubjectName
            )

        val etExamDate =
            dialogView.findViewById<EditText>(
                R.id.etExamDate
            )

        val etTarget =
            dialogView.findViewById<EditText>(
                R.id.etTargetScore
            )

        etExamDate.setOnClickListener {

            showDatePicker(
                etExamDate
            )
        }

        val dialog =
            AlertDialog.Builder(
                requireContext()
            )
                .setTitle(
                    "Add Subject 📚"
                )
                .setView(
                    dialogView
                )
                .setNegativeButton(
                    "CANCEL",
                    null
                )
                .setPositiveButton(
                    "SAVE",
                    null
                )
                .create()

        dialog.setOnShowListener {

            dialog
                .getButton(
                    AlertDialog.BUTTON_POSITIVE
                )
                .setOnClickListener {

                    val name =
                        etName.text
                            .toString()
                            .trim()

                    val examDate =
                        etExamDate.text
                            .toString()
                            .trim()

                    val targetText =
                        etTarget.text
                            .toString()
                            .trim()

                    if (name.isEmpty()) {

                        etName.error =
                            "Enter subject name"

                        return@setOnClickListener
                    }

                    if (examDate.isEmpty()) {

                        etExamDate.error =
                            "Select exam date"

                        return@setOnClickListener
                    }

                    val target =
                        targetText.toIntOrNull()

                    if (
                        target == null ||
                        target !in 1..100
                    ) {

                        etTarget.error =
                            "Enter a score between 1 and 100"

                        return@setOnClickListener
                    }

                    viewModel.addSubject(
                        name,
                        examDate,
                        target
                    )

                    Toast.makeText(
                        requireContext(),
                        "$name added successfully 🎉",
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

        val calendar =
            Calendar.getInstance()

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

        val datePicker =
            DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->

                    val formattedDate =
                        String.format(
                            "%02d/%02d/%04d",
                            selectedDay,
                            selectedMonth + 1,
                            selectedYear
                        )

                    editText.setText(
                        formattedDate
                    )
                },
                year,
                month,
                day
            )

        datePicker.show()
    }

    private fun showDeleteConfirmation(
        subject: com.example.studysync.data.Subject
    ) {

        AlertDialog.Builder(
            requireContext()
        )
            .setTitle(
                "Delete Subject?"
            )
            .setMessage(
                "Are you sure you want to delete ${subject.name}?"
            )
            .setNegativeButton(
                "CANCEL",
                null
            )
            .setPositiveButton(
                "DELETE"
            ) { _, _ ->

                viewModel.deleteSubject(
                    subject
                )

                Toast.makeText(
                    requireContext(),
                    "${subject.name} deleted",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }

    private fun showProgressDialog(
        subject: com.example.studysync.data.Subject
    ) {

        val dialogView =
            layoutInflater.inflate(
                R.layout.dialog_progress,
                null
            )

        val progressBar =
            dialogView.findViewById<android.widget.SeekBar>(
                R.id.seekProgress
            )

        val tvValue =
            dialogView.findViewById<TextView>(
                R.id.tvProgressValue
            )

        progressBar.progress =
            subject.progress

        tvValue.text =
            "${subject.progress}%"

        progressBar.setOnSeekBarChangeListener(
            object : android.widget.SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(
                    seekBar: android.widget.SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {

                    tvValue.text =
                        "$progress%"
                }

                override fun onStartTrackingTouch(
                    seekBar: android.widget.SeekBar?
                ) {
                }

                override fun onStopTrackingTouch(
                    seekBar: android.widget.SeekBar?
                ) {
                }
            }
        )

        AlertDialog.Builder(
            requireContext()
        )
            .setTitle(
                "${subject.name} Progress 📈"
            )
            .setView(
                dialogView
            )
            .setNegativeButton(
                "CANCEL",
                null
            )
            .setPositiveButton(
                "SAVE"
            ) { _, _ ->

                viewModel.updateProgress(
                    subject,
                    progressBar.progress
                )

                Toast.makeText(
                    requireContext(),
                    "Progress updated to ${progressBar.progress}% 🎉",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }
}