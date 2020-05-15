package com.devis.calendartest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.devis.calendartest.singlerowcalendar.calendar.CalendarChangesObserver
import com.devis.calendartest.singlerowcalendar.calendar.CalendarViewManager
import com.devis.calendartest.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.devis.calendartest.singlerowcalendar.selection.CalendarSelectionManager
import com.devis.calendartest.singlerowcalendar.utils.DateUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_calendar.view.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val calendar = Calendar.getInstance()
    private var currentMonth = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
        initCalendar()
    }

    private fun initCalendar() {
        // set current date to calendar and current month to currentMonth variable
        calendar.time = Date()
        currentMonth = calendar[Calendar.MONTH]

        val calendarViewManager = object : CalendarViewManager {
            override fun bindDataToCalendarView(
                holder: SingleRowCalendarAdapter.CalendarViewHolder,
                date: Date,
                position: Int,
                isSelected: Boolean
            ) {
                holder.itemView.tv_date_calendar_item.text = DateUtils.getDayNumber(date)
                holder.itemView.tv_day_calendar_item.text = DateUtils.getDay3LettersName(date)
            }

            override fun setCalendarViewResourceId(
                    position: Int,
                    date: Date,
                    isSelected: Boolean
            ): Int {
                val cal = Calendar.getInstance()
                cal.time = date

                return if (isSelected) {
                    when (cal[Calendar.DAY_OF_WEEK]) {
                        Calendar.MONDAY -> R.layout.item_first_special_selected_calendar
                        Calendar.WEDNESDAY -> R.layout.item_second_special_selected_calendar
                        Calendar.FRIDAY -> R.layout.item_third_special_selected_calendar
                        else -> R.layout.item_selected_calendar
                    }
                } else {
                    when (cal[Calendar.DAY_OF_WEEK]) {
                        Calendar.MONDAY -> R.layout.item_first_special_calendar
                        Calendar.WEDNESDAY -> R.layout.item_second_special_calendar
                        Calendar.FRIDAY -> R.layout.item_third_special_calendar
                        else -> R.layout.item_calendar
                    }
                }
            }
        }

        val changesObserver = object : CalendarChangesObserver {
            override fun whenSelectionChanged(isSelected: Boolean, position: Int, date: Date) {
                tvDate.text = "${DateUtils.getMonthName(date)}, ${DateUtils.getDayNumber(date)} "
                tvDay.text = DateUtils.getDayName(date)
                super.whenSelectionChanged(isSelected, position, date)
            }
        }

        val selectionManager = object : CalendarSelectionManager {
            override fun canBeItemSelected(position: Int, date: Date): Boolean {
                val cal = Calendar.getInstance()
                cal.time = date

                return when (cal[Calendar.DAY_OF_WEEK]) {
                    Calendar.SATURDAY -> false
                    Calendar.SUNDAY -> false
                    else -> true
                }
            }
        }

        val singleRowCalendar = main_single_row_calendar.apply {
            this.calendarViewManager = calendarViewManager
            this.calendarChangesObserver = changesObserver
            this.calendarSelectionManager = selectionManager
            setDates(getFutureDatesOfCurrentMonth())
            init()
            isSelected(23)
            init()
        }

        btnRight.setOnClickListener {
            singleRowCalendar.setDates(getDatesOfNextMonth())
        }

        btnLeft.setOnClickListener {
            singleRowCalendar.setDates(getDatesOfPreviousMonth())
        }
    }

    private fun getDatesOfNextMonth(): List<Date> {
        currentMonth++
        if (currentMonth == 12) {
            calendar.set(Calendar.YEAR, calendar[Calendar.YEAR] + 1)
            currentMonth = 0
        }
        return getDates(mutableListOf())
    }

    private fun getDatesOfPreviousMonth(): List<Date> {
        currentMonth--
        if (currentMonth == -1) {
            calendar.set(Calendar.YEAR, calendar[Calendar.YEAR] - 1)
            currentMonth = 11
        }
        return getDates(mutableListOf())
    }

    private fun getFutureDatesOfCurrentMonth(): List<Date> {
        currentMonth = calendar[Calendar.MONTH]
        return getDates(mutableListOf())
    }

    private fun getDates(list: MutableList<Date>): List<Date> {
        calendar.set(Calendar.MONTH, currentMonth)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        list.add(calendar.time)
        while (currentMonth == calendar[Calendar.MONTH]) {
            calendar.add(Calendar.DATE, +1)
            if (calendar[Calendar.MONTH] == currentMonth) {
                list.add(calendar.time)
            }
        }
        calendar.add(Calendar.DATE, -1)
        return list
    }

}
