package com.example.routebox.presentation.ui.route.write

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routebox.R
import com.example.routebox.databinding.ActivityRouteWriteBinding
import com.example.routebox.databinding.BottomSheetActivityBinding
import com.example.routebox.domain.model.Activity
import com.example.routebox.presentation.ui.route.RouteActivityActivity
import com.example.routebox.presentation.ui.route.RouteViewModel
import com.example.routebox.presentation.ui.route.adapter.ActivityRVAdapter
import com.example.routebox.presentation.ui.route.edit.RouteEditViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import dagger.hilt.android.AndroidEntryPoint

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class RouteWriteActivity: AppCompatActivity() {

    private lateinit var binding: ActivityRouteWriteBinding
    private val viewModel: RouteViewModel by viewModels()
    private val writeViewModel: RouteWriteViewModel by viewModels()
    private val editViewModel: RouteEditViewModel by viewModels()
    private lateinit var bottomSheetDialog: BottomSheetActivityBinding
    private val activityAdapter = ActivityRVAdapter(true)

    override fun onResume() {
        super.onResume()

        if (writeViewModel.activity.value?.locationName != "") {
            activityAdapter.addActivities(writeViewModel.activity.value!!)
            editViewModel.route.value?.activities?.add(writeViewModel.activity.value)
        }
        writeViewModel.resetActivityResult()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_route_write)
        binding.apply {
            viewModel = this@RouteWriteActivity.viewModel
            lifecycleOwner = this@RouteWriteActivity
        }

        initObserve()
        setTabLayout()
        setInit()
        initClickListener()
    }

    private fun setInit() {
        bottomSheetDialog = binding.routeWriteActivityBottomSheet
        bottomSheetDialog.apply {
            this.viewModel = this@RouteWriteActivity.editViewModel
            this.lifecycleOwner = this@RouteWriteActivity
        }
    }

    private fun initClickListener() {
        // 안드로이드 기본 뒤로가기 버튼 클릭
        onBackPressedDispatcher.addCallback(this) {
            finish()
        }

        binding.closeIv.setOnClickListener {
            finish()
        }

        // 활동이 없을 때 나타나는 활동 추가 버튼
        binding.addCv.setOnClickListener {
//            findNavController().navigate(R.id.action_routeWriteFragment_to_routeActivityFragment)
            startActivity(Intent(this, RouteActivityActivity::class.java))
        }

        // 활동이 1개 이상일 때 나타나는 활동 추가 버튼
        bottomSheetDialog.activityAddBtn.setOnClickListener {
//            findNavController().navigate(R.id.action_routeWriteFragment_to_routeActivityFragment)
            startActivity(Intent(this, RouteActivityActivity::class.java))
        }
    }

    private fun setTabLayout() {
        binding.mapTb.addOnTabSelectedListener(object: OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab!!.position) {
                    0 -> {
                        Navigation.findNavController(binding.routeContainer).navigate(R.id.action_routeTrackingFragment_to_routeConvenienceFragment)
                        Toast.makeText(this@RouteWriteActivity, ContextCompat.getString(this@RouteWriteActivity, R.string.update), Toast.LENGTH_LONG).show()
                    }
                    1 -> {
                        Navigation.findNavController(binding.routeContainer).navigate(R.id.action_routeConvenienceFragment_to_routeTrackingFragment)
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) { }
            override fun onTabReselected(tab: TabLayout.Tab?) { }
        })
    }

    private fun initObserve() {
        editViewModel.route.observe(this) { route ->
            if (route.activities.isNotEmpty()) {
                setActivityAdapter()
            }
        }
    }

    private fun setActivityAdapter() {
        bottomSheetDialog.activityRv.apply {
            this.adapter = activityAdapter
            this.layoutManager = LinearLayoutManager(this@RouteWriteActivity, LinearLayoutManager.VERTICAL, false)
        }
        activityAdapter.setActivityClickListener(object: ActivityRVAdapter.MyItemClickListener {
            override fun onEditButtonClick(position: Int, data: Activity) {
//                writeViewModel.setPlaceName(data.name)
//                writeViewModel.setPlaceSearchKeyword(data.name)
//                writeViewModel.updateDate(data.date)
//                writeViewModel.startTimePair.value = Pair(Integer.parseInt(data.startTime.substring(0, 2)), Integer.parseInt(data.startTime.substring(3, 5)))
//                writeViewModel.endTimePair.value = Pair(Integer.parseInt(data.endTime.substring(0, 2)), Integer.parseInt(data.endTime.substring(3, 5)))
//
//                if (data.type == Category.ETC) {
//                    // writeViewModel.categoryETC.value = Category.ETC.categoryName
//                } else writeViewModel.category.value = data.type
//
//                writeViewModel.placeImage.value = data.imgUrls as ArrayList<String>?
//                Log.d("ROUTE-TEST", "writeViewModel.placeImage = ${writeViewModel.placeImage.value}")
//                writeViewModel.locationContent.value = data.description
//
//                findNavController().navigate(R.id.action_routeWriteFragment_to_routeActivityFragment)
            }
            override fun onDeleteButtonClick(position: Int) {
                activityAdapter.removeItem(position)
            }
        })
        activityAdapter.addAllActivities(editViewModel.route.value?.activities as MutableList<Activity>)
    }
}