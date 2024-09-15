package com.example.routebox.presentation.ui.route.write

import android.Manifest
import android.Manifest.permission
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routebox.R
import com.example.routebox.databinding.ActivityRouteWriteBinding
import com.example.routebox.databinding.BottomSheetActivityBinding
import com.example.routebox.domain.model.ActivityResult
import com.example.routebox.domain.model.DialogType
import com.example.routebox.presentation.ui.route.GPSBackgroundService
import com.example.routebox.presentation.ui.route.RouteActivityActivity
import com.example.routebox.presentation.ui.route.RouteViewModel
import com.example.routebox.presentation.ui.route.adapter.ActivityRVAdapter
import com.example.routebox.presentation.ui.route.edit.RouteEditViewModel
import com.example.routebox.presentation.utils.CommonPopupDialog
import com.example.routebox.presentation.utils.PopupDialogInterface
import com.example.routebox.presentation.utils.SharedPreferencesHelper
import com.example.routebox.presentation.utils.SharedPreferencesHelper.Companion.APP_PREF_KEY
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import dagger.hilt.android.AndroidEntryPoint


/**
 * TODO: 1분 간격으로 실시간 위치 API 전송
 * TODO: 지도에 위치 띄우기
 * TODO: 기록 중이라면 기록 화면 왔을 때 진행 중 아이콘으로 띄우기!!! -> ViewModel 이용!
 */

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class RouteWriteActivity: AppCompatActivity(), PopupDialogInterface {

    private lateinit var binding: ActivityRouteWriteBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private var firstObserve: Boolean = true
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
        sharedPreferencesHelper = SharedPreferencesHelper(getSharedPreferences(APP_PREF_KEY, Context.MODE_PRIVATE))
        viewModel.getIsTracking(sharedPreferencesHelper.getRouteTracking())

        checkGPSPermission()
        initObserve()
        setTabLayout()
        setInit()
        initClickListener()

        routeId = Integer.parseInt(intent.getStringExtra("routeId"))
        editViewModel.setRouteId(routeId)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 2
        private const val LOCATION_BACKGROUND_PERMISSION_REQUEST_CODE = 3
    }

    // Background GPS 권한 허용을 위한 부분
    // Android 11 이상부터는 Background에서 접근하는 권한이 처음 권한 확인 문구에 뜨지 않는다 -> So, 추가로 권한을 한번 더 확인하여 Background에서 동작이 가능하도록 구성
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 첫 권한 확인이 완료 되었는지 확인
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // SDK 29 이상일 때는 한번 더 권한 요청
                // SDK 29 미만일 경우, 항상 허용 옵션이 첫 권한 요청 화면에 뜨기 때문에 추가로 요청 X
                // 만약 첫 권한을 허용했다면, Background에서 작동하도록 "항상 허용" 권한 요청
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this@RouteWriteActivity, ContextCompat.getString(this@RouteWriteActivity, R.string.gps_always_grant), Toast.LENGTH_SHORT).show()
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), LOCATION_BACKGROUND_PERMISSION_REQUEST_CODE)
                }
            } else {
                // 권한이 거부되었을 경우, 아래 문구 띄우기
                Toast.makeText(this@RouteWriteActivity, ContextCompat.getString(this@RouteWriteActivity, R.string.gps_deny), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkGPSPermission() {
        // 권한을 구분하기 위한 LOCATION_PERMISSION_REQUEST_CODE 필요!
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION
                ,Manifest.permission.ACCESS_COARSE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
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

        binding.trackingCv.setOnClickListener {
            viewModel.setIsTracking(this)
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

        viewModel.isTracking.observe(this) {
            if (firstObserve) sharedPreferencesHelper.setRouteTracking(sharedPreferencesHelper.getRouteTracking())
            else sharedPreferencesHelper.setRouteTracking(!sharedPreferencesHelper.getRouteTracking())

            firstObserve = false

            if (viewModel.isTracking.value == true) {
                Intent(applicationContext, GPSBackgroundService::class.java).apply {
                    action = GPSBackgroundService.SERVICE_START
                    startService(this)
                }
            } else {
                Intent(applicationContext, GPSBackgroundService::class.java).apply {
                    action = GPSBackgroundService.SERVICE_STOP
                    stopService(this)
                }
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