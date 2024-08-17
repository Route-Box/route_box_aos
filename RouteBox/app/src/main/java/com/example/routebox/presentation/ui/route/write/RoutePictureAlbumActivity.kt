package com.example.routebox.presentation.ui.route.write

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.routebox.R
import com.example.routebox.databinding.ActivityRoutePictureAlbumBinding

class RoutePictureAlbumActivity: AppCompatActivity() {

    private lateinit var binding: ActivityRoutePictureAlbumBinding
    private val viewModel: RoutePictureAlbumViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_route_picture_album)

        viewModel.getActivityPictureAlbumList(this)

        initClickListener()
        setAdapter()
        initObserve()
    }

    private fun initClickListener() {

    }

    private fun setAdapter() {

    }

    private fun initObserve() {
        viewModel.ActivityPictureAlbumList.observe(this) {

        }
    }
}