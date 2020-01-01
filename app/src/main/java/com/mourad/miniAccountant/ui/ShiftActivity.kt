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
import com.mourad.miniAccountant.R
import com.mourad.miniAccountant.model.Job
import com.mourad.miniAccountant.model.Shift
import com.mourad.miniAccountant.viewmodel.MyViewModelFactory
import com.mourad.miniAccountant.viewmodel.ShiftActivityViewModel

import kotlinx.android.synthetic.main.activity_shift.*
import kotlinx.android.synthetic.main.content_shift.*
import kotlinx.android.synthetic.main.dialog_add_shift.*
import kotlinx.android.synthetic.main.dialog_add_shift.btnCancel
import kotlinx.android.synthetic.main.dialog_delete_shift.*
import kotlinx.android.synthetic.main.dialog_update_shift.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar

const val JOB_EXTRA = "JOB_EXTRA"
class ShiftActivity : AppCompatActivity() {

    private val mainScope = CoroutineScope(Dispatchers.Main)
    private lateinit var job: Job
    private lateinit var shiftActivityViewModel: ShiftActivityViewModel
    private var shifts = arrayListOf<Shift>()
    private var shiftAdapter = ShiftAdapter(shifts) { clickedShift: Shift -> onShiftClicked(clickedShift) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shift)

        job = intent.getParcelableExtra(JOB_EXTRA)

        initViews()
        initViewModel()
    }

    fun initViews() {
        // Set the page title
        tvTitle.text = job.name

        // Initialize the recyclerView
        rvShifts.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvShifts.adapter = shiftAdapter
        createItemTouchHelper().attachToRecyclerView(rvShifts)

        // Set the onClickListeners
        fab.setOnClickListener { buildDialogAddShiftDate() }
        tvBackClick.setOnClickListener { finish() }
        tvSettingsClick.setOnClickListener { onSettingsClicked() }
    }

    private fun initViewModel() {
        shiftActivityViewModel = ViewModelProviders.of(this, MyViewModelFactory(job, application)).get(ShiftActivityViewModel::class.java)

        shiftActivityViewModel.shifts.observe(this, Observer { shifts ->
            this@ShiftActivity.shifts.clear()
            this@ShiftActivity.shifts.addAll(shifts)
//            this@ShiftActivity.shifts.sortedByDescending { it.startDateTime }
            shiftAdapter.notifyDataSetChanged()
            checkShifts()
        })
    }

    private fun updateViewModelShift(shift: Shift) {
        mainScope.launch {
            shiftActivityViewModel.shift.value = shift
        }
    }

    fun onSettingsClicked() {
        val intent = Intent(this, SettingsActivity::class.java)
        intent.putExtra(JOB_SETTINGS, job)
        startActivity(intent)
    }

    private fun buildDialogAddShiftDate() {
        var dialog = Dialog(this@ShiftActivity)
        dialog.setContentView(R.layout.dialog_add_shift)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.clDialog.visibility = View.VISIBLE
        dialog.clDialog2.visibility = View.INVISIBLE
        dialog.clDialog3.visibility = View.INVISIBLE

        dialog.etYear.minValue = 2000
        dialog.etYear.maxValue = 2020

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

        dialog.btnCancel.setOnClickListener {
            if (validateDate(dialog.etYear.value, dialog.etMonth.value, dialog.etDay.value)) {
                dialog.clDialog2.visibility = View.VISIBLE
                dialog.clDialog.visibility = View.INVISIBLE
            }
        }
        dialog.btnNext2.setOnClickListener {
            if (validateTime(dialog.etStartHours.value, dialog.etStartMinutes.value)) {
                dialog.clDialog2.visibility = View.INVISIBLE
                dialog.clDialog3.visibility = View.VISIBLE
            }
        }
        dialog.ibBack3.setOnClickListener {
            dialog.clDialog3.visibility = View.INVISIBLE
            dialog.clDialog2.visibility = View.VISIBLE
        }
        dialog.ibBack2.setOnClickListener {
            dialog.clDialog2.visibility = View.INVISIBLE
            dialog.clDialog.visibility = View.VISIBLE
        }
        dialog.btnAdd.setOnClickListener {
            if (validateTime(dialog.etEndHours.value, dialog.etEndMinutes.value)) {
                addShift(dialog)
                dialog.cancel()
            }
        }
        dialog.show()

    }

    private fun buildDialogDeleteShift(shiftToDelete: Shift, position: Int) {
        var dialog = Dialog(this@ShiftActivity)
        dialog.setContentView(R.layout.dialog_delete_shift)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.btnCancel.setOnClickListener {
            shiftAdapter.notifyItemChanged(position)
            dialog.cancel()
        }

        dialog.btnDelete.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    updateViewModelShift(shiftToDelete)
                    shiftActivityViewModel.deleteShift()
                }
            }
            dialog.cancel()
        }

        dialog.show()
    }

    private fun checkShifts() {
        var toBePaidTotal = 0.0
        var toBePaidHours = 0.0
        var earnedTotal = 0.0
        var earnedHours = 0.0

        shifts.forEach() {
            if (it.isPaid) {
                earnedTotal += it.getSalary(job.hourlyWage)
                earnedHours += it.getWorkedHours()
            } else {
                toBePaidTotal += it.getSalary(job.hourlyWage)
                toBePaidHours += it.getWorkedHours()
            }
        }

        tvToBePaid.text = getString(R.string.money, toBePaidTotal)
        tvToBePaidInHours.text = getString(R.string.hours, toBePaidHours)
        tvEarned.text = getString(R.string.money, earnedTotal)
        tvEarnedInHours.text = getString(R.string.hours, earnedHours)

        // Check for no shifts
        if (shifts.isEmpty()) {
            clNoShifts.visibility = View.VISIBLE
        } else {
            clNoShifts.visibility = View.INVISIBLE
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

    private fun addShift(dialog: Dialog) {
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

            val shift = Shift(startDate, endDate, false, job.id!!.toLong())
            withContext(Dispatchers.IO) {
                updateViewModelShift(shift)
                shiftActivityViewModel.insertShift()
            }
            dialog.cancel()
        }
    }

    fun onShiftClicked(clickedShift: Shift) {
        if (clickedShift.isPaid) {
            buildDialogUpdateShift(clickedShift)
        } else {
            mainScope.launch {
                withContext(Dispatchers.IO) {
                    clickedShift.isPaid = true
                    updateViewModelShift(clickedShift)
                    shiftActivityViewModel.updateShift()
                }
            }
        }
    }

    private fun buildDialogUpdateShift(clickedShift: Shift) {
        var dialog = Dialog(this@ShiftActivity)
        dialog.setContentView(R.layout.dialog_update_shift)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.btnCancel.setOnClickListener {
            dialog.cancel()
        }

        dialog.btnAgree.setOnClickListener {
            mainScope.launch {
                withContext(Dispatchers.IO) {
                    clickedShift.isPaid = false
                    updateViewModelShift(clickedShift)
                    shiftActivityViewModel.updateShift()
                }
            }
            dialog.cancel()
        }

        dialog.show()
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
                val shiftToDelete = shifts[position]

                buildDialogDeleteShift(shiftToDelete, position)
            }
        }
        return ItemTouchHelper(callback)
    }

}
