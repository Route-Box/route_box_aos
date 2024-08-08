package com.example.routebox.presentation.utils.picker

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.routebox.R
import com.example.routebox.presentation.ui.route.record.RouteCreateActivity.Companion.TODAY
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class CalendarRVAdapter(private val selectedDatePosition: Int, private val selectedMonth: Int) : RecyclerView.Adapter<CalendarRVAdapter.ViewHolder>() {

    private var dateList = listOf<LocalDate?>() // 달력에 표시될 날짜 목록
    private var selectedItemPosition = -1 // 달이 넘어가더라도 선택한 날짜는 유일하게 표시해주기 위함
    private lateinit var mItemClickListener: MyItemClickListener

    private lateinit var context: Context

    interface MyItemClickListener {
        fun onItemClick(selectedDate: LocalDate)
    }

    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addDateList(dateList: List<LocalDate?>) {
        this.dateList = dateList
        this.selectedItemPosition = if (dateList[10]!!.monthValue == selectedMonth) selectedDatePosition else -1
        notifyDataSetChanged()
    }

    // 보여지는 화면 설정
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_date, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    // 내부 데이터 설정
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val day = dateList[position]

        holder.dayText.text = day.toString()

        if (day == null) { // 날짜 데이터가 없을 경우
            holder.dayText.text = null
            return
        }

        // 날짜의 일만 표시
        holder.dayText.text = day.dayOfMonth.toString()
        if (day < TODAY){ // 오늘 이전 날짜 회색 처리
            holder.dayText.setTextColor(ContextCompat.getColor(context, R.color.gray5))
            return
        }
        if (selectedItemPosition == position) { // 선택 날짜 표시
            holder.bg.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.main))
            holder.dayText.setTextColor(ContextCompat.getColor(context, R.color.white))
            holder.dayText.setTypeface(null, Typeface.BOLD) // 볼드 처리
        } else { // 선택하지 않은 날짜 표시
            holder.bg.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.transparent))
            holder.dayText.setTextColor(ContextCompat.getColor(context, R.color.title_black))
            holder.dayText.setTypeface(null, Typeface.NORMAL)
        }

        // 날짜 클릭 이벤트
        holder.bg.setOnClickListener {
            if (dateList[position]!! >= TODAY) { // 오늘 이전의 날짜는 선택할 수 없음
                notifyItemChanged(selectedItemPosition) // 이전에 선택한 아이템 notify
                selectedItemPosition = position // 선택한 날짜 position 업데이트
                mItemClickListener.onItemClick(dateList[selectedItemPosition]!!) // 클릭 이벤트 처리
                notifyItemChanged(selectedItemPosition) // 새로 선택한 아이템 notify
            }
        }
    }

    override fun getItemCount(): Int = dateList.size

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val bg: LinearLayout = itemView.findViewById(R.id.item_calendar_date_bg)
        var dayText: TextView = itemView.findViewById(R.id.item_calendar_date_tv)
    }
}