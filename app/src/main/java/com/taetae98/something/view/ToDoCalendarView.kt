package com.taetae98.something.view

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.get
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.taetae98.something.R
import com.taetae98.something.base.BaseAdapter
import com.taetae98.something.base.BaseHolder
import com.taetae98.something.databinding.HolderCalendarBinding
import com.taetae98.something.dto.ToDo
import com.taetae98.something.utility.Time
import java.util.*
import kotlin.properties.Delegates

class ToDoCalendarView : MaterialCardView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /*
    좌우 스크롤을 사용해서 달력을 표시하기 위해 ViewPager 사용.
    ViewPager2의 ListAdapter형식으로 달력을 표시한다.
     */
    private val viewPager by lazy { ViewPager2(context) }
    private val viewPagerAdapter by lazy { CalendarViewAdapter() }


    // 현재 ViewPager의 Position과 ViewPager가 나타내는 Page의 Time
    private var currentPosition = Int.MAX_VALUE / 2
    private var current = Time()

    // CalendarView에 표시하는 ToDo List
    // Delegates.observable를 사용하여 List가 변경되면 ViewPager를 업데이트한다.
    var todoList by Delegates.observable(emptyList<ToDo>()) { _, _, _ ->
        viewPagerAdapter.notifyDataSetChanged()
    }

    // CalendarView의 Date를 선택했을 때 실행되는 Callback
    var onDateClickListener: ((Time) -> Unit)? = null

    init {
        with(viewPager) {
            adapter = viewPagerAdapter
            setCurrentItem(currentPosition, false)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    /*
                    페이지가 변경됐을 때 currentPosition, current를 업데이트한다.
                     */
                    super.onPageSelected(position)
                    if (currentPosition != position) {
                        current.month += position - currentPosition
                        currentPosition = position
                    }
                }
            })
        }

        addView(viewPager)
    }

    fun setTime(time: Time) {
        /*
        년도와 월을 비교하여 notifyItemChanged를 현재 보는 화면이 바꼈을 때만 호출한다.
        갱신된 Time이 현재보다 느리면 --, 빠르면 ++을 사용한다.
        setCurrentItem으로 Position을 바꾸고 smoothScroll을 true로 주면서 ViewPager의 Animation을 사용할 수 있다.
         */
        if (current != time) {
            if (current.year != time.year || current.month != time.month) {
                if (current < time) {
                    current = time
                    currentPosition++
                    viewPager.setCurrentItem(currentPosition, true)
                } else {
                    current = time
                    currentPosition--
                    viewPager.setCurrentItem(currentPosition, true)
                }

                viewPagerAdapter.notifyItemChanged(currentPosition)

            }

            current = time
        }
    }

    fun setTime(year: Int, month: Int, dayOfMonth: Int) {
        val time = Time().apply {
            this.year = year
            this.month = month
            this.dayOfMonth = dayOfMonth
        }

        setTime(time)
    }

    inner class CalendarViewAdapter : BaseAdapter<Time>(TimeItemCallback()) {
        init {
            setHasStableIds(true)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<out ViewDataBinding, Time> {
            return CalendarHolder(HolderCalendarBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        inner class CalendarHolder(binding: HolderCalendarBinding) : BaseHolder<HolderCalendarBinding, Time>(binding) {
            private val beginTime: Time
                get() {
                    return Time(element.timeInMillis).apply {
                        dayOfMonth = 1
                        dayOfMonth += 1 - dayOfWeek
                    }
                }

            private val rows: List<GridLayout>
                get() {
                    return arrayListOf<View>().apply {
                        itemView.findViewsWithText(this, "row", View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION)
                    }.map {
                        it as GridLayout
                    }
                }

            init {
                binding.setOnDateChange {
                    DatePickerDialog(context, { _, year, month, dayOfMonth ->
                        setTime(year, month, dayOfMonth)
                    }, current.year, current.month, current.dayOfMonth).show()
                }

                rows.forEachIndexed { y, view ->
                    val detector = GestureDetector(context, object : GestureDetector.OnGestureListener {
                        override fun onDown(e: MotionEvent?): Boolean {
                            return true
                        }

                        override fun onShowPress(e: MotionEvent?) {

                        }

                        override fun onSingleTapUp(e: MotionEvent): Boolean {
                            val x = (e.x / (view.width / 7)).toInt()
                            val time = beginTime.apply { dayOfMonth += (y * 7 + x) }
                            setTime(time)
                            onDateClickListener?.invoke(time)
                            return true
                        }

                        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                            return true
                        }

                        override fun onLongPress(e: MotionEvent?) {

                        }

                        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
                            return true
                        }
                    })

                    view.setOnTouchListener { _, event ->
                        view.performClick()
                        detector.onTouchEvent(event)
                    }
                }
            }

            override fun bind(element: Time) {
                super.bind(element)
                binding.time = element

                bindDay()
                bindToDo()
            }

            private fun setTodayDayView(view: TextView, time: Time) {
                with(view) {
                    setBackgroundResource(R.drawable.ic_circle)
                    val outValue = TypedValue()
                    context.theme.resolveAttribute(R.attr.onBackgroundAccent, outValue, true)

                    text = time.dayOfMonth.toString()
                    alpha = if (time.month == element.month) 1.0F else 0.3F
                    setTextColor(context.getColor(outValue.resourceId))
                }
            }

            private fun setDayView(view: TextView, time: Time) {
                with(view) {
                    background = null
                    text = time.dayOfMonth.toString()
                    alpha = if (time.month == element.month) 1.0F else 0.3F
                    setTextColor(context.getColor(when(time.dayOfWeek) {
                        Calendar.SATURDAY -> {
                            val outValue = TypedValue()
                            context.theme.resolveAttribute(R.attr.onSaturdayTextColor, outValue, true)
                            outValue.resourceId
                        }
                        Calendar.SUNDAY -> {
                            val outValue = TypedValue()
                            context.theme.resolveAttribute(R.attr.onSundayTextColor, outValue, true)
                            outValue.resourceId
                        }
                        else -> {
                            val outValue = TypedValue()
                            context.theme.resolveAttribute(R.attr.onWeekdayTextColor, outValue, true)
                            outValue.resourceId
                        }
                    }))
                }
            }

            private fun bindDay() {
                val time = beginTime
                val today = Time(System.currentTimeMillis())
                for (gridLayout in rows) {
                    for (i in 0 until 7) {
                        with((gridLayout[i] as LinearLayout)[0] as TextView) {
                            if (time == today) {
                                setTodayDayView(this, time)
                            } else {
                                setDayView(this, time)
                            }
                        }

                        time.dayOfMonth += 1
                    }
                }
            }

            private fun bindToDo() {
                val begin = beginTime
                for (gridLayout in rows) {
                    val end = Time(begin.timeInMillis).apply { dayOfMonth += 6 }
                    val list = todoList.filter {
                        (it.beginTime <= begin && begin <= it.endTime) || (begin <= it.beginTime && it.endTime <= end) || (it.beginTime <= end && end <= it.endTime)
                    }.toMutableList()

                    with(gridLayout) {
                        removeViews(7, childCount - 7)
                        var pos: Int
                        var beginPos: Int
                        var endPos: Int
                        while (list.isNotEmpty()) {
                            pos = 0
                            val iterator = list.iterator()
                            while (iterator.hasNext()) {
                                val todo = iterator.next()
                                if (todo.beginTime <= begin && pos == 0) {
                                    beginPos = 0
                                    endPos = if (todo.endTime <= end) todo.endTime.dayOfWeek - 1 else 6
                                    pos = endPos + 1
                                    addView(ToDoView(context).apply { bind(todo, beginPos, endPos) })
                                    iterator.remove()
                                } else if (todo.beginTime >= begin && pos <= todo.beginTime.dayOfWeek - 1) {
                                    beginPos = todo.beginTime.dayOfWeek - 1
                                    endPos = if (todo.endTime <= end) todo.endTime.dayOfWeek - 1 else 6

                                    addView(ToDoView(context).apply { bind(todo, beginPos, endPos) })
                                    pos = endPos + 1
                                    iterator.remove()
                                }
                            }
                        }
                    }

                    begin.dayOfMonth += 7
                }
            }

            inner class ToDoView : MaterialTextView {
                constructor(context: Context) : super(context)
                constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
                constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

                private lateinit var todo: ToDo

                init {
                    gravity = Gravity.CENTER
                    setTextColor(context.getColor(R.color.white))
                    setTypeface(null, Typeface.BOLD)

                    isSingleLine = true
                    ellipsize = TextUtils.TruncateAt.MARQUEE
                    isSelected = true
                }

                fun bind(todo: ToDo, begin: Int, end: Int) {
                    this.todo = todo
                    text = todo.title

                    val colorId = resources.getIdentifier("item${todo.id % 16}", "color", context.packageName)
                    setBackgroundColor(context.getColor(colorId))

                    layoutParams = GridLayout.LayoutParams(GridLayout.spec(GridLayout.UNDEFINED, 1), GridLayout.spec(begin, end - begin + 1, 1F)).apply {
                        width = 0
                    }
                }
            }
        }

        override fun getItem(position: Int): Time {
            return Time(current.timeInMillis).apply {
                month += position - currentPosition
            }
        }

        override fun getItemId(position: Int): Long {
            return getItem(position).timeInMillis
        }

        override fun getItemCount(): Int {
            return Int.MAX_VALUE
        }
    }

    inner class TimeItemCallback : DiffUtil.ItemCallback<Time>() {
        override fun areItemsTheSame(oldItem: Time, newItem: Time): Boolean {
            return oldItem.timeInMillis == newItem.timeInMillis
        }

        override fun areContentsTheSame(oldItem: Time, newItem: Time): Boolean {
            return oldItem.timeInMillis == newItem.timeInMillis
        }
    }
}