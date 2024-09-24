package com.daval.routebox.presentation.ui.route.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daval.routebox.databinding.ItemActivityBinding
import com.daval.routebox.domain.model.ActivityResult

@SuppressLint("NotifyDataSetChanged")
class ActivityRVAdapter(private val isEditMode: Boolean): RecyclerView.Adapter<ActivityRVAdapter.ViewHolder>(){

    private var activityList = mutableListOf<ActivityResult>()
    private lateinit var mItemClickListener: MyItemClickListener

    fun setActivityClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    fun removeItem(position: Int) {
        activityList.removeAt(position)
        this.notifyDataSetChanged()
    }

    fun addAllActivities(activityList: MutableList<ActivityResult>) {
        this.activityList = activityList
        this.notifyDataSetChanged()
    }

    fun returnActivityId(position: Int): Int {
        return activityList[position].activityId
    }

    interface MyItemClickListener {
        fun onEditButtonClick(position: Int, data: ActivityResult)
        fun onDeleteButtonClick(position: Int)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemActivityBinding = ItemActivityBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(activityList[position])
        holder.apply {
            // 수정 버튼 클릭
            binding.itemActivityEditIv.setOnClickListener {
                mItemClickListener.onEditButtonClick(position, activityList[position])
            }
            // 삭제 버튼 클릭
            binding.itemActivityDeleteIv.setOnClickListener {
                mItemClickListener.onDeleteButtonClick(position)
            }
        }
    }

    override fun getItemCount(): Int = activityList.size

    inner class ViewHolder(val binding: ItemActivityBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(activity: ActivityResult) {
            binding.isEditMode = isEditMode
            binding.activity = activity
            //TODO: 활동 번호에 따른 색상 변경
            binding.activityOrder = adapterPosition

            if (!activity.activityImages.isNullOrEmpty()) { // 이미지 표시
                binding.itemActivityImageRv.apply {
                    adapter = ActivityImageRVAdapter(activity.activityImages!!.toList())
                    layoutManager = GridLayoutManager(context, 3)
                }
            }
        }
    }
}