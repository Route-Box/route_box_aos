package com.daval.routebox.presentation.utils

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.daval.routebox.R

object BindingAdapter {
    @JvmStatic
    @BindingAdapter("app:imageUrl", "app:placeHolder")
    fun setImage (imageView : ImageView, url : String?, placeHolder: Drawable?){
        if (placeHolder != null) {
            Glide.with(imageView.context)
                .load(url)
                .centerCrop()
                .into(imageView)
        } else {
            Glide.with(imageView.context)
                .load(url)
                .placeholder(placeHolder)
                .centerCrop()
                .into(imageView)
        }
    }

    @JvmStatic
    @BindingAdapter("app:imageUri")
    fun setUriImage(imageView: ImageView, uri: Uri?) {
        Glide.with(imageView.context)
            .load(uri)
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("app:imageSrc")
    fun setSrcImage(imageView: ImageView, resource: Int) {
        imageView.setImageResource(resource)
    }

    @JvmStatic
    @BindingAdapter("app:backgroundTintBasedOnCategory")
    fun setBackgroundTintBasedOnCategory(textView: TextView, category: String) {
        var numberColor = when (category) {
            "숙소" -> R.color.accommodation
            "관광지" -> R.color.tourist_attraction
            "음식점" -> R.color.restaurant
            "카페" -> R.color.cafe
            "SNS 스팟" -> R.color.sns_spot
            "문화 공간" -> R.color.cultural_space
            "화장실" -> R.color.toilet
            "주차장" -> R.color.parking_lot
            else -> R.color.etc
        }
        textView.background.setTint(ContextCompat.getColor(textView.rootView.context, numberColor))
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