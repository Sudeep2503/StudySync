package com.example.studysync.ui.planner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studysync.R
import com.example.studysync.data.StudySession

class StudySessionAdapter(
    private val onSessionChecked: (
        StudySession,
        Boolean
    ) -> Unit
) : RecyclerView.Adapter<StudySessionAdapter.SessionViewHolder>() {

    private var sessions: List<StudySession> = emptyList()

    fun submitList(
        newSessions: List<StudySession>
    ) {
        sessions = newSessions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SessionViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_study_session,
                    parent,
                    false
                )

        return SessionViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: SessionViewHolder,
        position: Int
    ) {
        holder.bind(sessions[position])
    }

    override fun getItemCount(): Int {
        return sessions.size
    }

    inner class SessionViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvEmoji =
            itemView.findViewById<TextView>(
                R.id.tvSessionEmoji
            )

        private val tvSubject =
            itemView.findViewById<TextView>(
                R.id.tvSessionSubject
            )

        private val tvTopic =
            itemView.findViewById<TextView>(
                R.id.tvSessionTopic
            )

        private val tvTime =
            itemView.findViewById<TextView>(
                R.id.tvSessionTime
            )

        private val checkSession =
            itemView.findViewById<CheckBox>(
                R.id.checkSession
            )

        fun bind(
            session: StudySession
        ) {

            tvEmoji.text = "📚"

            tvSubject.text =
                session.subjectName

            tvTopic.text =
                session.topicName

            tvTime.text =
                "${session.startTime} • ${session.duration} min"

            // Prevent the old listener from
            // triggering while RecyclerView reuses the view.
            checkSession.setOnCheckedChangeListener(
                null
            )

            checkSession.isChecked =
                session.completed

            checkSession.setOnCheckedChangeListener { _, isChecked ->

                onSessionChecked(
                    session,
                    isChecked
                )
            }
        }
    }
}