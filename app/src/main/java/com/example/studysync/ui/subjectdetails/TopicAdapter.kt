package com.example.studysync.ui.subjectdetails

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studysync.R
import com.example.studysync.data.Topic

class TopicAdapter(
    private val onTopicChecked: (Topic, Boolean) -> Unit
) : RecyclerView.Adapter<TopicAdapter.TopicViewHolder>() {

    private var topics = listOf<Topic>()

    fun submitList(newTopics: List<Topic>) {

        topics = newTopics

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TopicViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_topic,
                parent,
                false
            )

        return TopicViewHolder(view, onTopicChecked)
    }

    override fun onBindViewHolder(
        holder: TopicViewHolder,
        position: Int
    ) {

        holder.bind(topics[position])
    }

    override fun getItemCount(): Int {

        return topics.size
    }

    class TopicViewHolder(
        itemView: View,
        private val onTopicChecked: (Topic, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val checkTopic =
            itemView.findViewById<CheckBox>(
                R.id.checkTopic
            )

        private val tvTopicName =
            itemView.findViewById<TextView>(
                R.id.tvTopicName
            )

        private val tvTopicDifficulty =
            itemView.findViewById<TextView>(
                R.id.tvTopicDifficulty
            )

        fun bind(topic: Topic) {

            tvTopicName.text = topic.topicName

            tvTopicDifficulty.text =
                topic.difficulty

            checkTopic.setOnCheckedChangeListener(null)

            checkTopic.isChecked =
                topic.completed

            updateAppearance(topic.completed)

            checkTopic.setOnCheckedChangeListener { _, checked ->

                updateAppearance(checked)

                onTopicChecked(
                    topic,
                    checked
                )
            }
        }

        private fun updateAppearance(
            completed: Boolean
        ) {

            if (completed) {

                tvTopicName.paintFlags =
                    tvTopicName.paintFlags or
                            Paint.STRIKE_THRU_TEXT_FLAG

                tvTopicName.alpha = 0.5f

            } else {

                tvTopicName.paintFlags =
                    tvTopicName.paintFlags and
                            Paint.STRIKE_THRU_TEXT_FLAG.inv()

                tvTopicName.alpha = 1f
            }
        }
    }
}