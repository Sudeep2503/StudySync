package com.example.studysync.ui.subjectdetails

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studysync.R
import com.example.studysync.data.AppDatabase
import com.example.studysync.data.SubjectRepository
import com.example.studysync.data.Topic
import com.example.studysync.viewmodel.SubjectViewModel
import com.example.studysync.viewmodel.SubjectViewModelFactory
import kotlinx.coroutines.launch

class SubjectDetailsFragment : Fragment() {

    private var subjectId: Int = -1

    private lateinit var viewModel: SubjectViewModel
    private lateinit var topicAdapter: TopicAdapter

    private lateinit var tvSubjectName: TextView
    private lateinit var tvExamDate: TextView
    private lateinit var tvDifficulty: TextView
    private lateinit var tvProgress: TextView
    private lateinit var progressSubject: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        subjectId = requireArguments().getInt("subjectId")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment_subject_details,
            container,
            false
        )

        tvSubjectName =
            view.findViewById(R.id.tvSubjectName)

        tvExamDate =
            view.findViewById(R.id.tvExamDate)

        tvDifficulty =
            view.findViewById(R.id.tvDifficulty)

        tvProgress =
            view.findViewById(R.id.tvProgress)

        progressSubject =
            view.findViewById(R.id.progressSubject)

        val recyclerTopics =
            view.findViewById<RecyclerView>(
                R.id.recyclerTopics
            )

        val btnAddTopic =
            view.findViewById<Button>(
                R.id.btnAddTopic
            )

        setupViewModel()

        setupRecyclerView(recyclerTopics)

        loadSubject()

        observeTopics()

        btnAddTopic.setOnClickListener {
            showAddTopicDialog()
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
            SubjectViewModelFactory(repository)

        viewModel =
            ViewModelProvider(
                this,
                factory
            )[SubjectViewModel::class.java]
    }

    private fun setupRecyclerView(
        recyclerView: RecyclerView
    ) {

        topicAdapter =
            TopicAdapter { topic, completed ->

                viewModel.updateTopicStatus(
                    topicId = topic.id,
                    subjectId = subjectId,
                    completed = completed
                )
            }

        recyclerView.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        recyclerView.adapter = topicAdapter
    }

    private fun loadSubject() {

        lifecycleScope.launch {

            val database =
                AppDatabase.getDatabase(
                    requireContext()
                )

            val subject =
                database.subjectDao()
                    .getSubjectById(subjectId)

            if (subject != null) {

                tvSubjectName.text =
                    subject.name

                tvExamDate.text =
                    "Exam: ${subject.examDate}"

                // Subject does not have a difficulty field.
                // Difficulty is stored for individual topics.
                tvDifficulty.text =
                    "Target Score: ${subject.targetScore}%"
            }
        }
    }

    private fun observeTopics() {

        lifecycleScope.launch {

            viewModel
                .getTopicsForSubject(subjectId)
                .collect { topics: List<Topic> ->

                    topicAdapter.submitList(topics)

                    updateProgress(topics)
                }
        }
    }

    private fun updateProgress(
        topics: List<Topic>
    ) {

        if (topics.isEmpty()) {

            tvProgress.text = "0%"
            progressSubject.progress = 0

            return
        }

        val completed =
            topics.count { topic ->
                topic.completed
            }

        val progress =
            (completed * 100) / topics.size

        tvProgress.text =
            "$progress%"

        progressSubject.progress =
            progress
    }

    private fun showAddTopicDialog() {

        val dialogView =
            LayoutInflater
                .from(requireContext())
                .inflate(
                    R.layout.dialog_add_topic,
                    null
                )

        val etTopicName =
            dialogView.findViewById<EditText>(
                R.id.etTopicName
            )

        val spinnerDifficulty =
            dialogView.findViewById<Spinner>(
                R.id.spinnerTopicDifficulty
            )

        val options =
            arrayOf(
                "Easy",
                "Medium",
                "Hard"
            )

        spinnerDifficulty.adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                options
            )

        val dialog =
            AlertDialog.Builder(
                requireContext()
            )
                .setTitle("Add Topic 📖")
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

                val topicName =
                    etTopicName.text
                        .toString()
                        .trim()

                if (topicName.isEmpty()) {

                    etTopicName.error =
                        "Enter topic name"

                    return@setOnClickListener
                }

                val topic = Topic(
                    subjectId = subjectId,
                    topicName = topicName,
                    difficulty = spinnerDifficulty.selectedItem.toString()
                )

                android.util.Log.d(
                    "StudySyncAI",
                    "Adding topic='$topicName', subjectId=$subjectId"
                )

                viewModel.addTopic(topic)

                dialog.dismiss()
            }
        }

        dialog.show()
    }
}