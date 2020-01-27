package com.mourad.miniAccountant.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mourad.miniAccountant.R
import com.mourad.miniAccountant.model.Job
import kotlinx.android.synthetic.main.activity_shift.*
import kotlinx.android.synthetic.main.content_shift.*

class ShiftActivity : AppCompatActivity() {

    companion object {
        const val JOB_EXTRA = "JOB_EXTRA"
    }

    lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shift)

        job = intent.getParcelableExtra(JOB_EXTRA)

        initViews()
    }

    fun initViews() {
        // Set the page title
        tvTitle.text = job.name

        // Set the onClickListeners
        tvBackClick.setOnClickListener { finish() }
    }

}
