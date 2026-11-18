package com.example.studysync.ui.profile

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope

import com.example.studysync.R
import com.example.studysync.data.AppDatabase
import com.example.studysync.data.SubjectRepository
import com.example.studysync.viewmodel.ProfileViewModel
import com.example.studysync.viewmodel.ProfileViewModelFactory

import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel

    private lateinit var tvStudentName: android.widget.TextView
    private lateinit var tvStudentEmail: android.widget.TextView
    private lateinit var tvStudyGoal: android.widget.TextView

    private lateinit var tvTotalSubjects: android.widget.TextView
    private lateinit var tvTotalTopics: android.widget.TextView
    private lateinit var tvTotalStudyHours: android.widget.TextView

    private lateinit var btnEditProfile: Button
    private lateinit var btnSettings: Button
    private lateinit var btnLogout: Button


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_profile,
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

        setupViewModel()

        loadProfile()

        observeStatistics()

        setupClickListeners()
    }


    private fun initializeViews(
        view: View
    ) {

        tvStudentName =
            view.findViewById(
                R.id.tvStudentName
            )

        tvStudentEmail =
            view.findViewById(
                R.id.tvStudentEmail
            )

        tvStudyGoal =
            view.findViewById(
                R.id.tvStudyGoal
            )

        tvTotalSubjects =
            view.findViewById(
                R.id.tvTotalSubjects
            )

        tvTotalTopics =
            view.findViewById(
                R.id.tvTotalTopics
            )

        tvTotalStudyHours =
            view.findViewById(
                R.id.tvTotalStudyHours
            )

        btnEditProfile =
            view.findViewById(
                R.id.btnEditProfile
            )

        btnSettings =
            view.findViewById(
                R.id.btnSettings
            )

        btnLogout =
            view.findViewById(
                R.id.btnLogout
            )
    }


    private fun setupViewModel() {

        val database =
            AppDatabase.getDatabase(
                requireContext()
            )

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
            ProfileViewModelFactory(
                repository
            )

        profileViewModel =
            ViewModelProvider(
                this,
                factory
            )[ProfileViewModel::class.java]
    }


    private fun loadProfile() {

        val preferences =
            requireContext()
                .getSharedPreferences(
                    "StudySyncPrefs",
                    Context.MODE_PRIVATE
                )

        val name =
            preferences.getString(
                "studentName",
                "Student Name"
            )

        val email =
            preferences.getString(
                "studentEmail",
                "student@example.com"
            )

        val goal =
            preferences.getInt(
                "dailyGoal",
                4
            )

        tvStudentName.text =
            name

        tvStudentEmail.text =
            email

        tvStudyGoal.text =
            "$goal Hours per Day"
    }


    private fun observeStatistics() {

        viewLifecycleOwner.lifecycleScope.launch {

            profileViewModel
                .totalSubjects
                .collect { count ->

                    tvTotalSubjects.text =
                        "Total Subjects: $count"
                }
        }


        viewLifecycleOwner.lifecycleScope.launch {

            profileViewModel
                .completedTopics
                .collect { count ->

                    tvTotalTopics.text =
                        "Topics Completed: $count"
                }
        }


        viewLifecycleOwner.lifecycleScope.launch {

            profileViewModel
                .totalStudyMinutes
                .collect { minutes ->

                    val hours =
                        minutes / 60

                    val remainingMinutes =
                        minutes % 60

                    val text =
                        if (remainingMinutes == 0) {

                            hours.toString()

                        } else {

                            String.format(
                                java.util.Locale.getDefault(),
                                "%.1f",
                                minutes / 60.0
                            )
                        }

                    tvTotalStudyHours.text =
                        "Total Study Hours: $text"
                }
        }
    }


    private fun setupClickListeners() {

        btnEditProfile.setOnClickListener {

            showEditProfileDialog()
        }


        btnSettings.setOnClickListener {

            showSettingsDialog()
        }


        btnLogout.setOnClickListener {

            showLogoutDialog()
        }
    }


    private fun showEditProfileDialog() {

        val currentName =
            tvStudentName.text.toString()

        val currentEmail =
            tvStudentEmail.text.toString()

        val profileLayout =
            LinearLayoutHelper.createProfileLayout(
                requireContext(),
                currentName,
                currentEmail
            )

        AlertDialog.Builder(
            requireContext()
        )
            .setTitle("Edit Profile")
            .setView(
                profileLayout.root
            )
            .setPositiveButton(
                "Save"
            ) { _, _ ->

                val newName =
                    profileLayout.nameInput
                        .text
                        .toString()
                        .trim()

                val newEmail =
                    profileLayout.emailInput
                        .text
                        .toString()
                        .trim()

                if (
                    newName.isEmpty() ||
                    newEmail.isEmpty()
                ) {

                    Toast.makeText(
                        requireContext(),
                        "Please fill all fields",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                requireContext()
                    .getSharedPreferences(
                        "StudySyncPrefs",
                        Context.MODE_PRIVATE
                    )
                    .edit()
                    .putString(
                        "studentName",
                        newName
                    )
                    .putString(
                        "studentEmail",
                        newEmail
                    )
                    .apply()

                tvStudentName.text =
                    newName

                tvStudentEmail.text =
                    newEmail

                Toast.makeText(
                    requireContext(),
                    "Profile updated successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton(
                "Cancel",
                null
            )
            .show()
    }


    private fun showSettingsDialog() {

        val preferences =
            requireContext()
                .getSharedPreferences(
                    "StudySyncPrefs",
                    Context.MODE_PRIVATE
                )

        val currentGoal =
            preferences.getInt(
                "dailyGoal",
                4
            )

        AlertDialog.Builder(
            requireContext()
        )
            .setTitle("App Settings")
            .setItems(
                arrayOf(
                    "Daily Study Goal: $currentGoal Hours",
                    "Notifications",
                    "About StudySync"
                )
            ) { _, which ->

                when (which) {

                    0 -> {

                        showStudyGoalDialog()
                    }

                    1 -> {

                        Toast.makeText(
                            requireContext(),
                            "Notifications settings selected",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    2 -> {

                        AlertDialog.Builder(
                            requireContext()
                        )
                            .setTitle("StudySync")
                            .setMessage(
                                "StudySync helps you organize subjects, track progress, plan your studies and maintain focused study sessions."
                            )
                            .setPositiveButton(
                                "OK",
                                null
                            )
                            .show()
                    }
                }
            }
            .setNegativeButton(
                "Close",
                null
            )
            .show()
    }


    private fun showStudyGoalDialog() {

        val goals =
            arrayOf(
                "2 Hours per Day",
                "3 Hours per Day",
                "4 Hours per Day",
                "5 Hours per Day",
                "6 Hours per Day",
                "8 Hours per Day"
            )

        AlertDialog.Builder(
            requireContext()
        )
            .setTitle("Select Daily Study Goal")
            .setItems(
                goals
            ) { _, which ->

                val selectedGoal =
                    goals[which]

                val hours =
                    selectedGoal
                        .substringBefore(" ")
                        .toInt()

                requireContext()
                    .getSharedPreferences(
                        "StudySyncPrefs",
                        Context.MODE_PRIVATE
                    )
                    .edit()
                    .putInt(
                        "dailyGoal",
                        hours
                    )
                    .apply()

                tvStudyGoal.text =
                    selectedGoal

                Toast.makeText(
                    requireContext(),
                    "Daily study goal updated",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }


    private fun showLogoutDialog() {

        AlertDialog.Builder(
            requireContext()
        )
            .setTitle("Logout")
            .setMessage(
                "Are you sure you want to logout?"
            )
            .setPositiveButton(
                "Logout"
            ) { _, _ ->

                requireContext()
                    .getSharedPreferences(
                        "StudySyncPrefs",
                        Context.MODE_PRIVATE
                    )
                    .edit()
                    .putBoolean(
                        "isLoggedIn",
                        false
                    )
                    .apply()

                try {

                    val intent =
                        Intent(
                            requireContext(),
                            Class.forName(
                                "com.example.studysync.LoginActivity"
                            )
                        )

                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intent)

                } catch (
                    e: ClassNotFoundException
                ) {

                    Toast.makeText(
                        requireContext(),
                        "Login screen could not be opened",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton(
                "Cancel",
                null
            )
            .show()
    }
}