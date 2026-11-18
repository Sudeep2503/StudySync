
package com.example.studysync.ui.subjects

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studysync.R
import com.example.studysync.data.Subject

class SubjectsAdapter(
    private val onDelete: (Subject) -> Unit,
    private val onUpdateProgress: (Subject) -> Unit,
    private val onSubjectClick: (Subject) -> Unit
) : RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder>() {

    private var subjects = emptyList<Subject>()

    fun submitList(newSubjects: List<Subject>) {
        subjects = newSubjects
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SubjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_subject,
            parent,
            false
        )
        return SubjectViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: SubjectViewHolder,
        position: Int
    ) {
        holder.bind(subjects[position])
    }

    override fun getItemCount(): Int = subjects.size

    inner class SubjectViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvSubjectName =
            itemView.findViewById<TextView>(R.id.tvSubjectName)

        private val tvExamDate =
            itemView.findViewById<TextView>(R.id.tvExamDate)

        private val tvTarget =
            itemView.findViewById<TextView>(R.id.tvTarget)

        private val progressSubject =
            itemView.findViewById<ProgressBar>(R.id.progressSubject)

        private val tvProgress =
            itemView.findViewById<TextView>(R.id.tvProgress)

        private val btnDelete =
            itemView.findViewById<Button>(R.id.btnDeleteSubject)

        private val btnUpdateProgress =
            itemView.findViewById<Button>(R.id.btnUpdateProgress)

        fun bind(subject: Subject) {
            tvSubjectName.text = subject.name
            tvExamDate.text = "Exam: ${subject.examDate}"
            tvTarget.text = "${subject.targetScore}%"
            progressSubject.progress = subject.progress
            tvProgress.text = "${subject.progress}% completed"

            itemView.setOnClickListener {
                onSubjectClick(subject)
            }

            btnDelete.setOnClickListener {
                onDelete(subject)
            }

            btnUpdateProgress.setOnClickListener {
                onUpdateProgress(subject)
            }
        }
    }
}
