package com.example.routebox.presentation.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.routebox.R

object BindingAdapter {
    @JvmStatic
    @BindingAdapter("app:imageUrl", "app:placeHolder")
    fun setImage (imageview : ImageView, url : String?, placeHolder: Drawable){
        Glide.with(imageview.context)
            .load(url)
            .placeholder(placeHolder)
            .into(imageview)
    }

    @JvmStatic
    @BindingAdapter("app:backgroundTintBasedOnOrder")
    fun setBackgroundTintBasedOnOrder(textView: TextView, activityOrder: Int) {
        val categoryColorArr = textView.context.resources.getIntArray(R.array.categoryColorArr) // 카테고리 색상 리스트
        val tintColor = categoryColorArr[activityOrder % categoryColorArr.size]
        textView.background.setTint(tintColor)
    }
}