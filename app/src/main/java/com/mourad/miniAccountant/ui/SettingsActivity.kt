package com.mourad.miniAccountant.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProviders
import com.mourad.miniAccountant.R
import com.mourad.miniAccountant.model.Job
import com.mourad.miniAccountant.viewmodel.JobViewModel
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity() {

    companion object {
        const val JOB_SETTINGS = "JOB_SETTINGS"
        const val JOB_SETTINGS_SAVED = "JOB_SETTINGS_SAVED"
    }

    lateinit var job: Job
    lateinit var jobViewModel: JobViewModel
    var unicode: Int = 0x1F4B0
    var unicodeWink: Int = 0x1F609

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        job = intent.getParcelableExtra(JOB_SETTINGS)

        initViews()
        initViewModels()
    }

    private fun initViews() {
        // Set texts
        tvSubTitle.setText(job.name)
        etTitle.setText(job.name)
        etHourlyWage.setText(job.hourlyWage.toString())
        btnDeleteJob.setText(getString(R.string.settings_text_delete_job, job.name))
        credits.setText(getString(R.string.credits, getEmojiByUnicode(unicode), getEmojiByUnicode(unicode), getEmojiByUnicode(unicodeWink)))

        // Set the onClickListeners
        tvClose.setOnClickListener { finish() }
        btnSaveChanges.setOnClickListener {
            if (isSettingsChanged()) {
                job.hourlyWage = etHourlyWage.text.toString().toDouble()
                job.name = etTitle.text.toString()
                jobViewModel.updateViewModelJob(job).updateJob()

                //After changes are saved
                btnSaveChanges.isEnabled = false
                tvSubTitle.setText(job.name)
            }
        }

        etTitle.addTextChangedListener {
            btnSaveChanges.isEnabled = isSettingsChanged()
        }
        etHourlyWage.addTextChangedListener {
            btnSaveChanges.isEnabled = isSettingsChanged()
        }
    }

    private fun initViewModels() {
        jobViewModel = ViewModelProviders.of(this).get(JobViewModel::class.java)
    }

    private fun isSettingsChanged(): Boolean {
        if ((etHourlyWage.text.toString() == job.hourlyWage.toString()) && (etTitle.text.toString() == job.name)) return false
        return true
    }

    private fun getEmojiByUnicode(unicode: Int): String? {
        return String(Character.toChars(unicode))
    }

    override fun finish() {
        var data = Intent()
        data.putExtra(JOB_SETTINGS_SAVED, job)
        setResult(Activity.RESULT_OK, data)

        super.finish()
    }
}
