package com.mourad.miniAccountant.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mourad.miniAccountant.R
import com.mourad.miniAccountant.model.Job
import kotlinx.android.synthetic.main.activity_settings.*


const val JOB_SETTINGS = "JOB_SETTINGS"
class SettingsActivity : AppCompatActivity() {

    lateinit var job: Job
    var unicode: Int = 0x1F4B0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        job = intent.getParcelableExtra(JOB_SETTINGS)

        initViews()
    }

    private fun initViews() {
        etTitle.setText(job.name)
        etHourlyWage.setText(job.hourlyWage.toString())
        btnDeleteJob.setText(getString(R.string.settings_text_delete_job, job.name))
        credits.setText(getString(R.string.credits, getEmojiByUnicode(unicode), getEmojiByUnicode(unicode)))
        tvClose.setOnClickListener { finish() }
    }

    fun getEmojiByUnicode(unicode: Int): String? {
        return String(Character.toChars(unicode))
    }
}
