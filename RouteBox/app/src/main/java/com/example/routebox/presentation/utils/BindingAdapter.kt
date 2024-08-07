package com.example.routebox.presentation.utils

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

object BindingAdapter {
    @JvmStatic
    @BindingAdapter("app:imageUrl", "app:placeHolder")
    fun setImage (imageview : ImageView, url : String?, placeHolder: Drawable){
        Glide.with(imageview.context)
            .load(url)
            .placeholder(placeHolder)
            .into(imageview)
    }
}