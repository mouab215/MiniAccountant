package com.mourad.miniAccountant.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
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

        dialog.clDialog2.visibility = View.INVISIBLE
        dialog.clDialog3.visibility = View.INVISIBLE

        dialog.etYear.minValue = 2000
        dialog.etYear.maxValue = 2019

        dialog.etMonth.minValue = 1
        dialog.etMonth.maxValue = 12

        dialog.etDay.minValue = 1
        dialog.etDay.maxValue = 31

        dialog.etStartHours.minValue = 1
        dialog.etStartHours.maxValue = 24

        dialog.etStartMinutes.minValue = 0
        dialog.etStartMinutes.maxValue = 60

        dialog.etEndHours.minValue = 1
        dialog.etEndHours.maxValue = 24

        dialog.etEndMinutes.minValue = 0
        dialog.etEndMinutes.maxValue = 60

        var hoursInString = arrayOf("01", "02", "03", "04", "05", "06", "07", "08",
            "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "00")
        dialog.etStartHours.displayedValues = hoursInString
        dialog.etEndHours.displayedValues = hoursInString

        var minutesInString = arrayOf("00", "01", "02", "03", "04", "05", "06", "07", "08",
            "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22",
            "23", "24", "25", "26", "27" ,"28", "29", "30", "31", "32", "33", "34", "35", "36",
            "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
            "51", "52", "53", "54", "55", "56", "57", "58", "58", "59")
        dialog.etStartMinutes.displayedValues = minutesInString
        dialog.etEndMinutes.displayedValues = minutesInString

        var today = Calendar.getInstance()
        dialog.etYear.value = SimpleDateFormat("yyyy").format(today.time).toInt()
        dialog.etMonth.value = SimpleDateFormat("MM").format(today.time).toInt()
        dialog.etDay.value = SimpleDateFormat("dd").format(today.time).toInt()
        dialog.etStartHours.value = SimpleDateFormat("HH").format(today.time).toInt()
        dialog.etStartMinutes.value = SimpleDateFormat("mm").format(today.time).toInt()
        dialog.etEndHours.value = SimpleDateFormat("HH").format(today.time).toInt()
        dialog.etEndMinutes.value = SimpleDateFormat("mm").format(today.time).toInt()

        dialog.btnNext.setOnClickListener {
            if (validateDate(dialog.etYear.value, dialog.etMonth.value, dialog.etDay.value)) {
                dialog.clDialog2.visibility = View.VISIBLE
            }
        }
        dialog.btnNext2.setOnClickListener {
            if (validateTime(dialog.etStartHours.value, dialog.etStartMinutes.value)) {
                dialog.clDialog3.visibility = View.VISIBLE
            }
        }
        dialog.btnAdd.setOnClickListener {
            if (validateTime(dialog.etEndHours.value, dialog.etEndMinutes.value)) {
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

    // todo schrijf heir iets boeiends wat empty bestaat niet meer
    private fun validateDate(year: Int, month: Int, day: Int): Boolean {
//        return if (year.text.toString().isNotBlank() &&
////            month.text.toString().isNotBlank() &&
////            day.text.toString().isNotBlank()) {
////            true
////        } else {
////            Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_LONG).show()
////            false
////        }
        return true
    }

    private fun validateTime(hours: Int, minutes: Int): Boolean {
//        return if (
//            hours.text.toString().isNotBlank() &&
//            minutes.text.toString().isNotBlank()) {
//            true
//        } else {
//            Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_LONG).show()
//            false
//        }
        return true
    }

    private fun addJob(dialog: Dialog) {
        mainScope.launch {
            var startDate = Calendar.getInstance()
            startDate.set(
                dialog.etYear.value,
                dialog.etMonth.value - 1,
                dialog.etDay.value,
                dialog.etStartHours.value,
                dialog.etStartMinutes.value
            )
            var endDate = Calendar.getInstance()
            endDate.set(
                dialog.etYear.value,
                dialog.etMonth.value - 1,
                dialog.etDay.value,
                dialog.etEndHours.value,
                dialog.etEndMinutes.value
            )

            val job = Job(startDate, endDate, false)
            withContext(Dispatchers.IO) {
                jobRepository.insertJob(job)
            }
            getJobsFromDatabase()
            dialog.cancel()
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
