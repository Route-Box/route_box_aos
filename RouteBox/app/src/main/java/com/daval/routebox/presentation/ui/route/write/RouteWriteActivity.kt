package com.daval.routebox.presentation.ui.route.write

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
import com.daval.routebox.R
import com.daval.routebox.databinding.ActivityRouteWriteBinding
import com.daval.routebox.presentation.ui.route.GPSBackgroundService
import com.daval.routebox.presentation.ui.route.RouteViewModel
import com.daval.routebox.presentation.ui.route.edit.RouteEditViewModel
import com.daval.routebox.presentation.utils.SharedPreferencesHelper
import com.daval.routebox.presentation.utils.SharedPreferencesHelper.Companion.APP_PREF_KEY
import com.daval.routebox.presentation.utils.SharedPreferencesHelper.Companion.TRACKING_COORDINATE
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.kakao.vectormap.LatLng
import dagger.hilt.android.AndroidEntryPoint

/**
 * TODO: 카카오맵 트래킹 추가
 * TODO: 처음에 좌표 못 받아와서 위치 안 뜨는 오류 수정
 * TODO: 점 잇기
 * TODO: 뒤로가기 오류 수정
 */

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class RouteWriteActivity: AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener { // , PopupDialogInterface

    private lateinit var binding: ActivityRouteWriteBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private var firstObserve: Boolean = true
    private val viewModel: RouteViewModel by viewModels()
    private val editViewModel: RouteEditViewModel by viewModels()
    private val writeViewModel: RouteWriteViewModel by viewModels()
    private var routeId: Int = -1

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
        sharedPreferencesHelper = SharedPreferencesHelper(sharedPreferences)
        viewModel.getIsLiveTracking(sharedPreferencesHelper.getRouteTracking())

        checkGPSPermission()
        initObserve()
        setTabLayout()
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
    }

    override fun onSharedPreferenceChanged(spf: SharedPreferences?, key: String?) {
        if (key == TRACKING_COORDINATE) {
            var coordinate = sharedPreferencesHelper.getLocationCoordinate()
            var latitude = coordinate[0]
            var longitude = coordinate[1]

            if (latitude != null && longitude != null) {
                writeViewModel.setCurrentCoordinate(LatLng.from(latitude, longitude))
                writeViewModel.addDot(latitude, longitude)
            }
        }
    }
}