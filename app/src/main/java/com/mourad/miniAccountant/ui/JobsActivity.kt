package com.mourad.miniAccountant.ui

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mourad.miniAccountant.R

import kotlinx.android.synthetic.main.activity_jobs.*
import kotlinx.android.synthetic.main.content_jobs.*
import kotlinx.android.synthetic.main.dialog_add_job.*
import com.mourad.miniAccountant.model.Job
import com.mourad.miniAccountant.ui.ShiftActivity.Companion.JOB_EXTRA
import com.mourad.miniAccountant.viewmodel.JobViewModel

class JobsActivity : AppCompatActivity() {

    private lateinit var jobViewModel: JobViewModel
    private var jobs = arrayListOf<Job>()
    private var jobAdapter = JobAdapter(jobs) { clickedJob: Job -> onJobClicked(clickedJob) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jobs)

        initViews()
        initViewModels()
    }

    private fun initViews() {
        rvJobs.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        rvJobs.adapter = jobAdapter
        createItemTouchHelper().attachToRecyclerView(rvJobs)

        fabAddJob.setOnClickListener { buildDialogAddJobDate() }
    }

    private fun initViewModels() {
        jobViewModel = ViewModelProviders.of(this).get(JobViewModel::class.java)

        jobViewModel.jobs.observe(this, Observer { jobs ->
            this@JobsActivity.jobs.clear()
            this@JobsActivity.jobs.addAll(jobs)
            jobAdapter.notifyDataSetChanged()
        })
    }

    fun onJobClicked(clickedJob: Job) {
        val intent = Intent(this, ShiftActivity::class.java)
        intent.putExtra(JOB_EXTRA, clickedJob)
        startActivity(intent)
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

//                buildDialogDeleteShift(shiftToDelete, position)
            }
        }
        return ItemTouchHelper(callback)
    }

    private fun buildDialogAddJobDate() {
        var dialog = Dialog(this@JobsActivity)
        dialog.setContentView(R.layout.dialog_add_job)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.clAddJobDialog.visibility = View.VISIBLE
        dialog.clAddJobDialog2.visibility = View.INVISIBLE

        dialog.btnNextJob.setOnClickListener {
            // todo Check voor double name "name already exists!"
            if (dialog.etJobTitle.text.toString() != "") {
                dialog.clAddJobDialog.visibility = View.INVISIBLE
                dialog.clAddJobDialog2.visibility = View.VISIBLE
            }
        }

        dialog.btnAddJob.setOnClickListener {
            if (dialog.etHourlyWage.text.toString() != "") {
                var job = Job(dialog.etJobTitle.text.toString(), dialog.etHourlyWage.text.toString().toDouble())
                addJob(job)
                dialog.cancel()
            }
        }

        dialog.show()
    }

    private fun addJob(job: Job) {
        jobViewModel.updateViewModelJob(job).insertJob()
    }

}
