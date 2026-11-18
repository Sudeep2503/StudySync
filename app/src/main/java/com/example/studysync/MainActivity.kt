package com.example.studysync

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.studysync.ui.analytics.AnalyticsFragment
import com.example.studysync.ui.dashboard.DashboardFragment
import com.example.studysync.ui.planner.PlannerFragment
import com.example.studysync.ui.profile.ProfileFragment
import com.example.studysync.ui.subjects.SubjectsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var navHome: LinearLayout
    private lateinit var navSubjects: LinearLayout
    private lateinit var navPlanner: LinearLayout
    private lateinit var navAnalytics: LinearLayout
    private lateinit var navProfile: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        navHome = findViewById(R.id.navHome)
        navSubjects = findViewById(R.id.navSubjects)
        navPlanner = findViewById(R.id.navPlanner)
        navAnalytics = findViewById(R.id.navAnalytics)
        navProfile = findViewById(R.id.navProfile)

        if (savedInstanceState == null) {
            loadFragment(DashboardFragment())
        }

        navHome.setOnClickListener {
            loadFragment(DashboardFragment())
        }

        navSubjects.setOnClickListener {
            loadFragment(SubjectsFragment())
        }

        navPlanner.setOnClickListener {
            loadFragment(PlannerFragment())
        }

        navAnalytics.setOnClickListener {
            loadFragment(AnalyticsFragment())
        }

        navProfile.setOnClickListener {
            loadFragment(ProfileFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}