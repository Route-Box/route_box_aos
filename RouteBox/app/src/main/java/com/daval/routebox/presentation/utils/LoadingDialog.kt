package com.daval.routebox.presentation.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.bumptech.glide.Glide
import com.daval.routebox.R
import com.daval.routebox.databinding.DialogLoadingBinding

class LoadingDialog(context: Context): Dialog(context) {

    private lateinit var binding: DialogLoadingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 주변 클릭 및 뒤로가기 막기
        setCancelable(false)

        Glide.with(context).load(R.drawable.anim_loading).into(binding.loadingIv)
    }
}