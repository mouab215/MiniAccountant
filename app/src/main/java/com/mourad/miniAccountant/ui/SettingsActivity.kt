package com.mourad.miniAccountant.ui

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProviders
import com.mourad.miniAccountant.R
import com.mourad.miniAccountant.model.Job
import com.mourad.miniAccountant.viewmodel.JobViewModel
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.dialog_delete_job.*


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
        btnDeleteJob.setOnClickListener { buildDialogDeleteJob() }

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

    private fun buildDialogDeleteJob() {
        var dialog = Dialog(this@SettingsActivity)
        dialog.setContentView(R.layout.dialog_delete_job)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.tvQuestion.text = getString(R.string.dialog_delete_job_title, job.name)
        dialog.tvPrompt.text = getString(R.string.dialog_delete_job_prompt, job.name)
        dialog.etPrompt.hint = job.name

        dialog.btnCancel.setOnClickListener {
            dialog.cancel()
        }

        dialog.etPrompt.addTextChangedListener {
            if (!dialog.etPrompt.text.toString().isEmpty()) {
                dialog.btnDelete.isEnabled = dialog.etPrompt.text?.toString() == job.name
            }
        }

        dialog.btnDelete.setOnClickListener {
            dialog.cancel()

            jobViewModel.updateViewModelJob(job).deleteJob()

            val i = Intent(applicationContext, JobsActivity::class.java)        // Specify any activity here e.g. home or splash or login etc
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.putExtra("EXIT", true)
            startActivity(i)
            finish()
        }

        dialog.show()
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
