package com.example.routebox.presentation.ui.route.write

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routebox.R
import com.example.routebox.databinding.ActivityRouteWriteBinding
import com.example.routebox.databinding.BottomSheetActivityBinding
import com.example.routebox.domain.model.ActivityResult
import com.example.routebox.domain.model.DialogType
import com.example.routebox.domain.model.RoutePointRequest
import com.example.routebox.presentation.ui.route.GPSBackgroundService
import com.example.routebox.presentation.ui.route.RouteActivityActivity
import com.example.routebox.presentation.ui.route.RouteViewModel
import com.example.routebox.presentation.ui.route.adapter.ActivityRVAdapter
import com.example.routebox.presentation.ui.route.edit.RouteEditViewModel
import com.example.routebox.presentation.utils.CommonPopupDialog
import com.example.routebox.presentation.utils.PopupDialogInterface
import com.example.routebox.presentation.utils.SharedPreferencesHelper
import com.example.routebox.presentation.utils.SharedPreferencesHelper.Companion.APP_PREF_KEY
import com.example.routebox.presentation.utils.SharedPreferencesHelper.Companion.TRACKING_COORDINATE
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.kakao.vectormap.LatLng
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime

/**
 * TODO: 현재 위치 마커 띄우기
 * TODO: 활동 마커 띄우기
 */

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class RouteWriteActivity: AppCompatActivity(), PopupDialogInterface, SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var binding: ActivityRouteWriteBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private var firstObserve: Boolean = true
    private val viewModel: RouteViewModel by viewModels()
    private val editViewModel: RouteEditViewModel by viewModels()
    private val writeViewModel: RouteWriteViewModel by viewModels()
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

        var sharedPreferences = getSharedPreferences(APP_PREF_KEY, Context.MODE_PRIVATE)
//        sharedPreferences.registerOnSharedPreferenceChangeListener(prefListener)
        sharedPreferencesHelper = SharedPreferencesHelper(sharedPreferences)
        viewModel.getIsLiveTracking(sharedPreferencesHelper.getRouteTracking())

        checkGPSPermission()
        initObserve()
        setTabLayout()
        setInit()
        initClickListener()

        routeId = Integer.parseInt(intent.getStringExtra("routeId"))
        writeViewModel.setRouteId(routeId)
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
            viewModel.setIsLiveTracking(this)
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

        viewModel.isLiveTracking.observe(this) {
            if (firstObserve) sharedPreferencesHelper.setRouteTracking(sharedPreferencesHelper.getRouteTracking())
            else sharedPreferencesHelper.setRouteTracking(!sharedPreferencesHelper.getRouteTracking())

            firstObserve = false

            if (viewModel.isLiveTracking.value == true) {
                Intent(applicationContext, GPSBackgroundService::class.java).apply {
                    action = GPSBackgroundService.SERVICE_START
                    startService(this)
                    sharedPreferencesHelper.registerPreferences(this@RouteWriteActivity)
                }
            } else {
                Intent(applicationContext, GPSBackgroundService::class.java).apply {
                    action = GPSBackgroundService.SERVICE_STOP
                    startService(this)
                    sharedPreferencesHelper.unregisterPreferences(this@RouteWriteActivity)
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

    override fun onSharedPreferenceChanged(spf: SharedPreferences?, key: String?) {
        if (key == TRACKING_COORDINATE) {
            if (sharedPreferencesHelper.getLocationCoordinate() != null) {
                var coordinate = sharedPreferencesHelper.getLocationCoordinate()!!.toList()
                var latitude: Double
                var longitude: Double
                if (coordinate[0].split(" ")[0] == "lat") {
                    latitude = coordinate[0].split(" ")[1].toDouble()
                    longitude = coordinate[1].split(" ")[1].toDouble()
                } else {
                    latitude = coordinate[1].split(" ")[1].toDouble()
                    longitude = coordinate[0].split(" ")[1].toDouble()
                }
                Log.d("LOCATION_SERVICE", "onSharedPreferenceChanged = ${latitude} / ${longitude}")

                writeViewModel.setCurrentCoordinate(LatLng.from(latitude, longitude))
                writeViewModel.addDot()
            }
        }
    }
}