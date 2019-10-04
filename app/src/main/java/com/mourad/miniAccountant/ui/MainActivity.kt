package com.mourad.miniAccountant.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mourad.miniAccountant.R
import com.mourad.miniAccountant.model.Job
import com.mourad.miniAccountant.repository.JobRepository

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.dialog_add_job.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private val mainScope = CoroutineScope(Dispatchers.Main)
    private lateinit var jobRepository: JobRepository
    private var jobs = arrayListOf<Job>()
    private var jobAdapter = JobAdapter(jobs) { clickedJob: Job -> onJobClicked(clickedJob) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        jobRepository = JobRepository(this)
        initViews()
    }

    fun initViews() {
        rvJobs.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvJobs.adapter = jobAdapter
        createItemTouchHelper().attachToRecyclerView(rvJobs)
        getJobsFromDatabase()

        fab.setOnClickListener { buildDialogAddJobDate() }
    }

    private fun buildDialogAddJobDate() {
        var dialog = Dialog(this@MainActivity)
        dialog.setContentView(R.layout.dialog_add_job)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog.btnAdd.setOnClickListener { AddJob(dialog) }

        dialog.clDialog2.visibility = View.INVISIBLE
        dialog.clDialog3.visibility = View.INVISIBLE


        var today = Calendar.getInstance()
        dialog.etYear.setText(SimpleDateFormat("yyyy").format(today.time))
        dialog.etMonth.setText(SimpleDateFormat("MM").format(today.time))
        dialog.etDay.setText(SimpleDateFormat("dd").format(today.time))

        dialog.btnNext.setOnClickListener {
            if (validateDate(dialog.etYear, dialog.etMonth, dialog.etDay)) {
                dialog.clDialog2.visibility = View.VISIBLE
            }
        }
        dialog.btnNext2.setOnClickListener {
            if (validateTime(dialog.etStartHours, dialog.etStartMinutes)) {
                dialog.clDialog3.visibility = View.VISIBLE
            }
        }
        dialog.btnAdd.setOnClickListener {
            if (validateTime(dialog.etEndHours, dialog.etEndMinutes)) {
                addJob(dialog)
                dialog.cancel()
            }
        }
        dialog.show()

    }

    private fun getJobsFromDatabase() {
        mainScope.launch {
            val jobs = withContext(Dispatchers.IO) {
                jobRepository.getAllJobs()
            }
            this@MainActivity.jobs.clear()
            this@MainActivity.jobs.addAll(jobs)
            this@MainActivity.jobAdapter.notifyDataSetChanged()

            var toBePaidTotal = 0.0
            var earnedTotal = 0.0

            jobs.forEach() {
                if (it.isPaid) {
                    earnedTotal += it.getSalary()
                } else {
                    toBePaidTotal += it.getSalary()
                }
            }

            tvToBePaid.text = getString(R.string.money, toBePaidTotal)
            tvEarned.text = getString(R.string.money, earnedTotal)
        }
    }

    private fun validateDate(year: EditText, month: EditText, day: EditText): Boolean {
        return if (year.text.toString().isNotBlank() &&
            month.text.toString().isNotBlank() &&
            day.text.toString().isNotBlank()) {
            true
        } else {
            Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_LONG).show()
            false
        }
    }

    private fun validateTime(hours: EditText, minutes: EditText): Boolean {
        return if (
            hours.text.toString().isNotBlank() &&
            minutes.text.toString().isNotBlank()) {
            true
        } else {
            Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_LONG).show()
            false
        }
    }

    private fun addJob(dialog: Dialog) {

        if (validateDate(dialog.etYear, dialog.etMonth, dialog.etDay)) {
            if (validateTime(dialog.etStartHours, dialog.etStartMinutes) && validateTime(dialog.etEndHours, dialog.etEndMinutes)) {

                mainScope.launch {
                    var startDate = Calendar.getInstance()
                    startDate.set(
                        dialog.etYear.text.toString().toInt(),
                        dialog.etMonth.text.toString().toInt() - 1,
                        dialog.etDay.text.toString().toInt(),
                        dialog.etStartHours.text.toString().toInt(),
                        dialog.etStartMinutes.text.toString().toInt()
                    )
                    var endDate = Calendar.getInstance()
                    endDate.set(
                        dialog.etYear.text.toString().toInt(),
                        dialog.etMonth.text.toString().toInt() - 1,
                        dialog.etDay.text.toString().toInt(),
                        dialog.etEndHours.text.toString().toInt(),
                        dialog.etEndMinutes.text.toString().toInt()
                    )

                    val job = Job(startDate, endDate, false)
                    withContext(Dispatchers.IO) {
                        jobRepository.insertJob(job)
                    }
                    getJobsFromDatabase()
                    dialog.cancel()
                }

            }
        }
    }

    fun onJobClicked(clickedJob: Job) {
        if (!clickedJob.isPaid) {
            mainScope.launch {
                withContext(Dispatchers.IO) {
                    clickedJob.isPaid = true
                    jobRepository.updateJob(clickedJob)
                }
                getJobsFromDatabase()
            }
        }
    }

    private fun createItemTouchHelper(): ItemTouchHelper {

        // Callback which is used to create the ItemTouch helper. Only enables left swipe.
        // Use ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) to also enable right swipe.
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            // Enables or Disables the ability to move items up and down.
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            // Callback triggered when a user swiped an item.
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val jobToDelete = jobs[position]

                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        jobRepository.deleteJob(jobToDelete)
                    }
                    getJobsFromDatabase()
                }

            }
        }
        return ItemTouchHelper(callback)
    }

}
