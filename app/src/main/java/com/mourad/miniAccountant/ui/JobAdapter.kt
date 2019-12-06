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
    val colors = arrayOf(R.drawable.rectangle_accent_ripple_effect,
        R.drawable.rectangle_grey_ripple_effect,
        R.drawable.rectangle_white_ripple_effect,
        R.drawable.rectangle_primary_ripple_effect)
    var i = 0

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

            itemView.background = context.getDrawable(getNextColor(colors))
            if (i == 2 || i == 3) {
                itemView.tvJobName.setTextColor(context.getColor(android.R.color.black))
            }
            itemView.tvJobName.text = job.name
        }

        private fun getNextColor(array: Array<Int>): Int {
//            val rnd = Random().nextInt(array.size)
//            return array[rnd]
            if (i == array.size) {
                i = 0
            }
            i++
            return array[i - 1]
        }
    }


}