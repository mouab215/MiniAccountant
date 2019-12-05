package com.mourad.miniAccountant.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mourad.miniAccountant.R
import com.mourad.miniAccountant.model.Job
import kotlinx.android.synthetic.main.item_job.view.*

class JobAdapter(private val jobs: List<Job>, private val clickListener: (Job) -> Unit) : RecyclerView.Adapter<JobAdapter.ViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_job, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return jobs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(jobs[position], clickListener)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bind(job: Job, clickListener: (Job) -> Unit) {
            itemView.setOnClickListener { clickListener(job) }

            itemView.tvJobName.text = job.name
        }
    }


}