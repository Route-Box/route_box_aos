package com.example.routebox.presentation.utils

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.routebox.R

object BindingAdapter {
    @JvmStatic
    @BindingAdapter("app:imageUrl", "app:placeHolder")
    fun setImage (imageView : ImageView, url : String?, placeHolder: Drawable){
        Glide.with(imageView.context)
            .load(url)
            .placeholder(placeHolder)
            .centerCrop()
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("app:imageUri")
    fun setUriImage (imageView: ImageView, uri: Uri?) {
        Glide.with(imageView.context)
            .load(uri)
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("app:backgroundTintBasedOnOrder")
    fun setBackgroundTintBasedOnOrder(textView: TextView, activityOrder: Int) {
        val categoryColorArr = textView.context.resources.getIntArray(R.array.categoryColorArr) // 카테고리 색상 리스트
        val tintColor = categoryColorArr[activityOrder % categoryColorArr.size]
        textView.background.setTint(tintColor)
    }

    @JvmStatic
    @BindingAdapter("app:myRouteTitleText")
    fun setMyRouteTitle(textView: TextView, title: String?) {
        textView.text = title ?: "아직 루트 제목이 없어요"
    }

    @JvmStatic
    @BindingAdapter("app:myRouteContentText")
    fun setMyRouteContent(textView: TextView, content: String?) {
        textView.text = content ?: "수정하기를 눌러 제목과 내용을 추가해주세요"
    }
}