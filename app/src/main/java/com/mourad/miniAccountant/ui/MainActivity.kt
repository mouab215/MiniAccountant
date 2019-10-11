package com.mourad.miniAccountant.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mourad.miniAccountant.R
import com.mourad.miniAccountant.model.Shift
import com.mourad.miniAccountant.repository.ShiftRepository

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.dialog_add_shift.*
import kotlinx.android.synthetic.main.dialog_add_shift.btnCancel
import kotlinx.android.synthetic.main.dialog_delete_shift.*
import kotlinx.android.synthetic.main.dialog_update_shift.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private val mainScope = CoroutineScope(Dispatchers.Main)
    private lateinit var shiftRepository: ShiftRepository
    private var shifts = arrayListOf<Shift>()
    private var shiftAdapter = ShiftAdapter(shifts) { clickedShift: Shift -> onJobClicked(clickedShift) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        shiftRepository = ShiftRepository(this)
        initViews()
    }

    fun initViews() {
        rvShifts.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvShifts.adapter = shiftAdapter
        createItemTouchHelper().attachToRecyclerView(rvShifts)
        getShiftsFromDatabase()

        fab.setOnClickListener { buildDialogAddJobDate() }
    }

    private fun buildDialogAddJobDate() {
        var dialog = Dialog(this@MainActivity)
        dialog.setContentView(R.layout.dialog_add_shift)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.clDialog.visibility = View.VISIBLE
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
                addJob(dialog)
                dialog.cancel()
            }
        }
        dialog.show()

    }

    private fun buildDialogDeleteShift(shiftToDelete: Shift, position: Int) {
        var dialog = Dialog(this@MainActivity)
        dialog.setContentView(R.layout.dialog_delete_shift)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.btnCancel.setOnClickListener {
            shiftAdapter.notifyItemChanged(position)
            dialog.cancel()
        }

        dialog.btnDelete.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    shiftRepository.deleteShift(shiftToDelete)
                }
                getShiftsFromDatabase()
            }
            dialog.cancel()
        }

        dialog.show()
    }

    private fun getShiftsFromDatabase() {
        mainScope.launch {
            val shifts = withContext(Dispatchers.IO) {
                shiftRepository.getAllShifts().sortedByDescending { it.startDateTime }
            }
            this@MainActivity.shifts.clear()
            this@MainActivity.shifts.addAll(shifts)
            this@MainActivity.shiftAdapter.notifyDataSetChanged()

            var toBePaidTotal = 0.0
            var toBePaidHours = 0.0
            var earnedTotal = 0.0
            var earnedHours = 0.0

            shifts.forEach() {
                if (it.isPaid) {
                    earnedTotal += it.getSalary()
                    earnedHours += it.getWorkedHours()
                } else {
                    toBePaidTotal += it.getSalary()
                    toBePaidHours += it.getWorkedHours()
                }
            }

            tvToBePaid.text = getString(R.string.money, toBePaidTotal)
            tvToBePaidInHours.text = getString(R.string.hours, toBePaidHours)
            tvEarned.text = getString(R.string.money, earnedTotal)
            tvEarnedInHours.text = getString(R.string.hours, earnedHours)
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

            val shift = Shift(startDate, endDate, false)
            withContext(Dispatchers.IO) {
                shiftRepository.insertShift(shift)
            }
            getShiftsFromDatabase()
            dialog.cancel()
        }
    }

    fun onJobClicked(clickedShift: Shift) {
        if (clickedShift.isPaid) {
            buildDialogUpdateShift(clickedShift)
        } else {
            mainScope.launch {
                withContext(Dispatchers.IO) {
                    clickedShift.isPaid = true
                    shiftRepository.updateShift(clickedShift)
                }
                getShiftsFromDatabase()
            }
        }
    }

    private fun buildDialogUpdateShift(clickedShift: Shift) {
        var dialog = Dialog(this@MainActivity)
        dialog.setContentView(R.layout.dialog_update_shift)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.btnCancel.setOnClickListener {
            dialog.cancel()
        }

        dialog.btnAgree.setOnClickListener {
            mainScope.launch {
                withContext(Dispatchers.IO) {
                    clickedShift.isPaid = false
                    shiftRepository.updateShift(clickedShift)
                }
                getShiftsFromDatabase()
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
