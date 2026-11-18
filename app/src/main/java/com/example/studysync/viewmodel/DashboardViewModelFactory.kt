package com.example.studysync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studysync.data.SubjectRepository

class DashboardViewModelFactory(
    private val repository: SubjectRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(
                DashboardViewModel::class.java
            )
        ) {

            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(
                repository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}