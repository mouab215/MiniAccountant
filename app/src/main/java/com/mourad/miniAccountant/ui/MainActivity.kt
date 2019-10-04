package com.mourad.miniAccountant.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
        getJobsFromDatabase()

        fab.setOnClickListener { buildDialogTransferQuestion() }
    }

    private fun buildDialogTransferQuestion() {
        var dialog = Dialog(this@MainActivity)
        dialog.setContentView(R.layout.dialog_add_job)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.btnAdd.setOnClickListener { AddJob(dialog) }

        var today = Calendar.getInstance()
        dialog.etYear.setText(SimpleDateFormat("yyyy").format(today.time))
        dialog.etMonth.setText(SimpleDateFormat("MM").format(today.time))
        dialog.etDay.setText(SimpleDateFormat("dd").format(today.time))

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

    private fun AddJob(dialog: Dialog) {

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

}
