package com.daval.routebox.presentation.ui.route.write.tracking

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.daval.routebox.R
import com.daval.routebox.databinding.BottomSheetActivityBinding
import com.daval.routebox.databinding.FragmentRouteTrackingBinding
import com.daval.routebox.domain.model.ActivityResult
import com.daval.routebox.domain.model.Category
import com.daval.routebox.domain.model.DialogType
import com.daval.routebox.presentation.ui.route.RouteActivityActivity
import com.daval.routebox.presentation.ui.route.RouteViewModel
import com.daval.routebox.presentation.ui.route.adapter.ActivityRVAdapter
import com.daval.routebox.presentation.ui.route.edit.RouteEditViewModel
import com.daval.routebox.presentation.ui.route.write.RouteWriteActivity
import com.daval.routebox.presentation.ui.route.write.RouteWriteActivity.Companion.ROUTE_WRITE_DEFAULT_ZOOM_LEVEL
import com.daval.routebox.presentation.ui.route.write.RouteWriteViewModel
import com.daval.routebox.presentation.utils.CommonPopupDialog
import com.daval.routebox.presentation.utils.MapUtil
import com.daval.routebox.presentation.utils.PopupDialogInterface
import com.daval.routebox.presentation.utils.SharedPreferencesHelper
import com.daval.routebox.presentation.utils.SharedPreferencesHelper.Companion.APP_PREF_KEY
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


@RequiresApi(Build.VERSION_CODES.O)
class RouteTrackingFragment: Fragment(), PopupDialogInterface, OnMapReadyCallback {

    private lateinit var binding: FragmentRouteTrackingBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private val viewModel: RouteViewModel by activityViewModels()
    private val editViewModel: RouteEditViewModel by activityViewModels()
    private val writeViewModel: RouteWriteViewModel by activityViewModels()
    private lateinit var bottomSheetDialog: BottomSheetActivityBinding
    private var deleteId: Int = -1
    private var deleteActivityIndex: Int = -1
    private val activityAdapter = ActivityRVAdapter(true)
    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRouteTrackingBinding.inflate(layoutInflater, container, false)

        binding.apply {
            viewModel = this@RouteTrackingFragment.viewModel
            editViewModel = this@RouteTrackingFragment.editViewModel
        }

        initSharedPreferences()
        initMapSetting()
        addBackgroundDots()
        setInit()
        initObserve()
        initClickListener()

        return binding.root
    }

    private fun initSharedPreferences() {
        var sharedPreferences = requireActivity().getSharedPreferences(APP_PREF_KEY, Context.MODE_PRIVATE)
        sharedPreferencesHelper = SharedPreferencesHelper(sharedPreferences)
        sharedPreferencesHelper.setIsBackground(false)
    }

    private fun initMapSetting() {
        // 맵 프래그먼트 초기화
        val mapFragment = childFragmentManager.findFragmentById(R.id.tracking_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setInit() {
        bottomSheetDialog = binding.routeWriteActivityBottomSheet
        bottomSheetDialog.apply {
            this.viewModel = this@RouteTrackingFragment.editViewModel
            this.lifecycleOwner = this@RouteTrackingFragment
        }
    }

    private fun initClickListener() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        // 활동이 없을 때 나타나는 활동 추가 버튼
        binding.addCv.setOnClickListener {
            startActivity(Intent(requireActivity(), RouteActivityActivity::class.java).putExtra("routeId", editViewModel.routeId.value.toString()).putExtra("routeId", writeViewModel.routeId.value))
        }

        // 활동이 1개 이상일 때 나타나는 활동 추가 버튼
        bottomSheetDialog.activityAddBtn.setOnClickListener {
            startActivity(Intent(requireActivity(), RouteActivityActivity::class.java).putExtra("routeId", editViewModel.routeId.value.toString()).putExtra("routeId", writeViewModel.routeId.value))
        }
    }

    private fun setActivityAdapter() {
        bottomSheetDialog.activityRv.apply {
            this.adapter = activityAdapter
            this.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        }
        bottomSheetDialog.activityRv.itemAnimator = null
        activityAdapter.setActivityClickListener(object: ActivityRVAdapter.MyItemClickListener {
            override fun onEditButtonClick(position: Int, data: ActivityResult) {
                startActivity(Intent(requireActivity(), RouteActivityActivity::class.java).apply {
                    putExtra("routeId", editViewModel.routeId.value)
                    putExtra("activity", editViewModel.route.value!!.routeActivities[position])
                    putExtra("isEdit", true)
                })
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

    private fun initObserve() {
        viewModel.route.observe(viewLifecycleOwner) {
            if (viewModel.route.value?.routeActivities?.size != 0) {
                activityAdapter.addAllActivities(viewModel.route.value?.routeActivities!!)
            }
        }

        writeViewModel.currentCoordinate.observe(viewLifecycleOwner) {
            setMapCenterPoint()
            if (writeViewModel.currentCoordinate.value != null) {
                setCurrentLocationMarker()
            }
        }

        editViewModel.deleteActivityId.observe(viewLifecycleOwner) {
            if (editViewModel.deleteActivityId.value == deleteId && deleteId != -1) {
                activityAdapter.removeItem(deleteActivityIndex)
                deleteId = -1
                editViewModel.setDeleteActivityId(-1)
            }
        }

        editViewModel.route.observe(viewLifecycleOwner) { route ->
            if (route.routeActivities.isNotEmpty()) {
                setActivityAdapter()
            }
        }
    }

    private fun setCurrentLocationMarker() {
        val activity = requireActivity() as RouteWriteActivity

        // 마커 추가
        googleMap?.addMarker(
            MarkerOptions()
                .position(LatLng(
                    writeViewModel.currentCoordinate.value!!.latitude,
                    writeViewModel.currentCoordinate.value!!.longitude
                ))
                .icon(activity.getResizedMarker(iconName = R.drawable.ic_gps_marker))
                .zIndex(1f)
        )
    }

    private fun setMapCenterPoint() {
        // 카메라 위치 설정 및 줌 레벨 조정
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(
            writeViewModel.currentCoordinate.value!!,
            ROUTE_WRITE_DEFAULT_ZOOM_LEVEL
        ))
    }

    private fun addBackgroundDots() {
        if (sharedPreferencesHelper.getBackgroundCoordinate() != null) {
            val backgroundDots = sharedPreferencesHelper.getBackgroundCoordinate()
            for (i in 0 until backgroundDots!!.size) {
                writeViewModel.addDot(backgroundDots[i]!!.latitude, backgroundDots[i]!!.longitude)
            }
            sharedPreferencesHelper.setBackgroundCoordinate(arrayListOf())
        }
    }

    override fun onPause() {
        super.onPause()
        sharedPreferencesHelper.setIsBackground(true)
    }

    // 마커 띄우기
    private fun addActivityMarker(latLng: LatLng, categoryName: String, activityNumber: Int) {
        // 지도에 마커 표시
        val markerIcon = MapUtil.createMarkerBitmap(requireContext(), Category.getCategoryByName(categoryName), activityNumber)
        // 마커 추가
        googleMap?.addMarker(
            MarkerOptions()
                .position(latLng)
                .icon(markerIcon)
                .zIndex(1f)
        )
    }

    private fun showPopupDialog() {
        val dialog = CommonPopupDialog(this, DialogType.DELETE.id, String.format(resources.getString(R.string.activity_delete_popup)), null, null)
        dialog.isCancelable = false // 배경 클릭 막기
        dialog.show(requireActivity().supportFragmentManager, "PopupDialog")
    }

    override fun onClickPositiveButton(id: Int) {
        Toast.makeText(requireActivity(), "활동이 삭제되었습니다", Toast.LENGTH_SHORT).show()
        editViewModel.deleteActivity(deleteId)
    }

    private fun drawRoutePath() {
        val routePath = getRoutePathToLatLng() ?: return

        // 기록된 점 조회를 위해 api 호출
        editViewModel.tryGetMyRouteDetail()

        // 이동 경로 선으로 연결
        val polylineOptions = MapUtil.getRoutePathPolylineOptions(requireContext(), routePath)
        googleMap?.addPolyline(polylineOptions)
    }

    private fun getRoutePathToLatLng(): List<LatLng>? {
        val routePath = editViewModel.route.value?.routePath
        return routePath?.map {
            LatLng(it.latitude, it.longitude)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        setMapCenterPoint()
        setCurrentLocationMarker()
        addBackgroundDots()
        drawRoutePath()
        // 활동 마커 추가하기
        if (editViewModel.route.value?.routeActivities != null) {
            for (i in 0 until editViewModel.route.value?.routeActivities!!.size) {
                var activity = editViewModel.route.value?.routeActivities!![i]
                addActivityMarker(
                    LatLng(activity.latitude.toDouble(), activity.longitude.toDouble()),
                    activity.category, i + 1
                )
            }
        }
    }
}