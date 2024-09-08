package com.example.routebox.presentation.ui.route.write

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import com.example.routebox.domain.model.ActivityResult
import com.example.routebox.domain.model.DialogType
import com.example.routebox.presentation.ui.route.RouteActivityActivity
import com.example.routebox.presentation.ui.route.RouteViewModel
import com.example.routebox.presentation.ui.route.adapter.ActivityRVAdapter
import com.example.routebox.presentation.ui.route.edit.RouteEditViewModel
import com.example.routebox.presentation.utils.CommonPopupDialog
import com.example.routebox.presentation.utils.PopupDialogInterface
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import dagger.hilt.android.AndroidEntryPoint


@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class RouteWriteActivity: AppCompatActivity(), PopupDialogInterface {

    private lateinit var binding: ActivityRouteWriteBinding
    private val viewModel: RouteViewModel by viewModels()
    private val editViewModel: RouteEditViewModel by viewModels()
    private lateinit var bottomSheetDialog: BottomSheetActivityBinding
    private val activityAdapter = ActivityRVAdapter(true)
    private var routeId: Int = -1
    private var deleteId: Int = -1
    private var deleteActivityIndex: Int = -1

    override fun onResume() {
        super.onResume()
        viewModel.tryGetMyRouteDetail(routeId)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_route_write)
        binding.apply {
            viewModel = this@RouteWriteActivity.viewModel
            lifecycleOwner = this@RouteWriteActivity
        }

        routeId = Integer.parseInt(intent.getStringExtra("routeId"))
        editViewModel.setRouteId(routeId)

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
            startActivity(Intent(this, RouteActivityActivity::class.java).putExtra("routeId", editViewModel.routeId.value.toString()))
        }

        // 활동이 1개 이상일 때 나타나는 활동 추가 버튼
        bottomSheetDialog.activityAddBtn.setOnClickListener {
            startActivity(Intent(this, RouteActivityActivity::class.java).putExtra("routeId", editViewModel.routeId.value.toString()))
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
        viewModel.route.observe(this) {
            if (viewModel.route.value?.routeActivities?.size != 0) {
                editViewModel.setRoute(viewModel.route.value!!)
                activityAdapter.addAllActivities(viewModel.route.value?.routeActivities!!)
            }
        }

        editViewModel.deleteActivityId.observe(this) {
            if (editViewModel.deleteActivityId.value == deleteId && deleteId != -1) {
                activityAdapter.removeItem(deleteActivityIndex)
                deleteId = -1
                editViewModel.setDeleteActivityId(-1)
            }
        }

        editViewModel.route.observe(this) { route ->
            if (route.routeActivities.isNotEmpty()) {
                setActivityAdapter()
            }
        }
    }

    private fun setActivityAdapter() {
        bottomSheetDialog.activityRv.apply {
            this.adapter = activityAdapter
            this.layoutManager = LinearLayoutManager(this@RouteWriteActivity, LinearLayoutManager.VERTICAL, false)
        }
        bottomSheetDialog.activityRv.itemAnimator = null
        activityAdapter.setActivityClickListener(object: ActivityRVAdapter.MyItemClickListener {
            override fun onEditButtonClick(position: Int, data: ActivityResult) {
                startActivity(Intent(this@RouteWriteActivity, RouteActivityActivity::class.java).putExtra("routeId", routeId))
            }
            override fun onDeleteButtonClick(position: Int) {
                deleteId = activityAdapter.returnActivityId(position)
                deleteActivityIndex = position
                // 활동 삭제 팝업 띄우기
                showPopupDialog()
            }
        })
        activityAdapter.addAllActivities(editViewModel.route.value?.routeActivities as MutableList<ActivityResult>)
    }

    private fun showPopupDialog() {
        val dialog = CommonPopupDialog(this, DialogType.DELETE.id, String.format(resources.getString(R.string.activity_delete_popup)), null, null)
        dialog.isCancelable = false // 배경 클릭 막기
        dialog.show(supportFragmentManager, "PopupDialog")
    }

    override fun onClickPositiveButton(id: Int) {
        Toast.makeText(this, "활동이 삭제되었습니다", Toast.LENGTH_SHORT).show()
        editViewModel.deleteActivity(deleteId)
    }
}