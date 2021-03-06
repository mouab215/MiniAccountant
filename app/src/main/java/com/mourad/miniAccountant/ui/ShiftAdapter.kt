package com.mourad.miniAccountant.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mourad.miniAccountant.R
import com.mourad.miniAccountant.model.Job
import com.mourad.miniAccountant.model.Shift
import kotlinx.android.synthetic.main.item_shift.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class ShiftAdapter(private val shifts: List<Shift>, private val job: Job, private val clickListener: (Shift) -> Unit) : RecyclerView.Adapter<ShiftAdapter.ViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_shift, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return shifts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(shifts[position], job, clickListener)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bind(shift: Shift, job: Job, clickListener: (Shift) -> Unit) {
            itemView.setOnClickListener { clickListener(shift) }

            CoroutineScope(Dispatchers.Main).launch {
                itemView.tvDate.text = SimpleDateFormat("EEE dd MMM").format(shift.startDateTime.time)
                itemView.tvTimes.text = SimpleDateFormat("HH:mm").format(shift.startDateTime.time) +
                        " till " + SimpleDateFormat("HH:mm").format(shift.endDateTime.time)
                itemView.tvWorkedHours.text = shift.getWorkedHours().toString()
                itemView.tvSalary.text = context.getString(R.string.money, shift.getSalary(job.hourlyWage))

                if (shift.isPaid) {
                    itemView.tvSalary.setTextColor(context.getColor(R.color.moneyPaid))
                } else if (!shift.isPaid) {
                    itemView.tvSalary.setTextColor(context.getColor(R.color.moneyToBePaid))
                }
            }



        }
    }


}