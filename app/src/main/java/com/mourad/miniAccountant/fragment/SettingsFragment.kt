package com.mourad.miniAccountant.fragment


import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.mourad.miniAccountant.R
import com.mourad.miniAccountant.ui.JobsActivity
import com.mourad.miniAccountant.ui.ShiftActivity
import com.mourad.miniAccountant.viewmodel.JobViewModel
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.activity_shift.*
import kotlinx.android.synthetic.main.content_shift.tvTitle
import kotlinx.android.synthetic.main.dialog_delete_job.*

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment() {

    lateinit var jobViewModel: JobViewModel
    var unicode: Int = 0x1F4B0
    var unicodeWink: Int = 0x1F609
    private lateinit var parentActivity: ShiftActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentActivity = (activity as ShiftActivity)

        initViews()
        initViewModels()
    }

    private fun initViews() {
        // Set texts
        etTitle.setText(parentActivity.job.name)
        etHourlyWage.setText(parentActivity.job.hourlyWage.toString())
        btnDeleteJob.setText(getString(R.string.settings_text_delete_job, parentActivity.job.name))
        credits.setText(getString(R.string.credits, getEmojiByUnicode(unicode), getEmojiByUnicode(unicode), getEmojiByUnicode(unicodeWink)))

        // Set the onClickListeners
        parentActivity.tvClose.setOnClickListener { findNavController().navigate(R.id.action_settingsFragment_to_shiftFragment) }
        btnSaveChanges.setOnClickListener {
            if (isSettingsChanged()) {
                Log.e("tag", parentActivity.job.toString())

                parentActivity.job.hourlyWage = etHourlyWage.text.toString().toDouble()
                parentActivity.job.name = etTitle.text.toString()
                Log.e("tag", parentActivity.job.toString())

                jobViewModel.updateViewModelJob(parentActivity.job).updateJob()

                //After changes are saved
                btnSaveChanges.isEnabled = false
                parentActivity.tvTitle.text = parentActivity.job.name

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

        // Set the current job
        parentActivity.tvSettingsClick.visibility = View.INVISIBLE
        parentActivity.tvClose.visibility = View.VISIBLE
        parentActivity.tvTitle.text = parentActivity.job.name
        jobViewModel.updateViewModelJobById(parentActivity.job.id!!)
    }

    private fun isSettingsChanged(): Boolean {
        if ((etHourlyWage.text.toString() == parentActivity.job.hourlyWage.toString()) && (etTitle.text.toString() == parentActivity.job.name)) return false
        return true
    }

    private fun buildDialogDeleteJob() {
        var dialog = Dialog(parentActivity)
        dialog.setContentView(R.layout.dialog_delete_job)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.tvQuestion.text = getString(R.string.dialog_delete_job_title, parentActivity.job.name)
        dialog.tvPrompt.text = getString(R.string.dialog_delete_job_prompt, parentActivity.job.name)
        dialog.etPrompt.hint = parentActivity.job.name

        dialog.btnCancel.setOnClickListener {
            dialog.cancel()
        }

        dialog.etPrompt.addTextChangedListener {
            if (!dialog.etPrompt.text.toString().isEmpty()) {
                dialog.btnDelete.isEnabled = dialog.etPrompt.text?.toString() == parentActivity.job.name
            }
        }

        dialog.btnDelete.setOnClickListener {
            dialog.cancel()

            jobViewModel.updateViewModelJob(parentActivity.job).deleteJob()

            val i = Intent(parentActivity.applicationContext, JobsActivity::class.java)        // Specify any activity here e.g. home or splash or login etc
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.putExtra("EXIT", true)
            startActivity(i)
            parentActivity.finish()
        }

        dialog.show()
    }

    private fun getEmojiByUnicode(unicode: Int): String? {
        return String(Character.toChars(unicode))
    }
}
