package com.daval.routebox.presentation.ui.route.write.tracking

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
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
import com.daval.routebox.presentation.ui.route.write.RouteWriteViewModel
import com.daval.routebox.presentation.utils.BindingAdapter
import com.daval.routebox.presentation.utils.CommonPopupDialog
import com.daval.routebox.presentation.utils.PopupDialogInterface
import com.daval.routebox.presentation.utils.SharedPreferencesHelper
import com.daval.routebox.presentation.utils.SharedPreferencesHelper.Companion.APP_PREF_KEY
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.LabelTextBuilder
import com.kakao.vectormap.label.LabelTextStyle
import com.kakao.vectormap.route.RouteLine
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle
import com.kakao.vectormap.route.RouteLineStyles
import com.kakao.vectormap.route.RouteLineStylesSet
import java.util.Arrays


@RequiresApi(Build.VERSION_CODES.O)
class RouteTrackingFragment: Fragment(), PopupDialogInterface {

    private lateinit var binding: FragmentRouteTrackingBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private val viewModel: RouteViewModel by activityViewModels()
    private val editViewModel: RouteEditViewModel by activityViewModels()
    private val writeViewModel: RouteWriteViewModel by activityViewModels()
    private lateinit var bottomSheetDialog: BottomSheetActivityBinding
    private var deleteId: Int = -1
    private var deleteActivityIndex: Int = -1
    private val activityAdapter = ActivityRVAdapter(true)
    private lateinit var kakaoMap: KakaoMap

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
        addBackgroundDots()
        initMapSetting()
        setInit()
        initClickListener()

        return binding.root
    }

    private fun initSharedPreferences() {
        var sharedPreferences = requireActivity().getSharedPreferences(APP_PREF_KEY, Context.MODE_PRIVATE)
        sharedPreferencesHelper = SharedPreferencesHelper(sharedPreferences)
        sharedPreferencesHelper.setIsBackground(false)
    }

    private fun initMapSetting() {
        binding.trackingMap.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                // 지도 API 가 정상적으로 종료될 때 호출
                Log.d("KakaoMap", "onMapDestroy: ")
            }
            override fun onMapError(error: Exception) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출
                Log.d("KakaoMap", "onMapError: $error")
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(kakaoMap: KakaoMap) {
                // 인증 후 API 가 정상적으로 실행될 때 호출됨
                Log.d("KakaoMap", "onMapReady: $kakaoMap")
                this@RouteTrackingFragment.kakaoMap = kakaoMap

                // 활동 마커 추가하기
                if (editViewModel.route.value?.routeActivities != null) {
                    for (i in 0 until editViewModel.route.value?.routeActivities!!.size) {
                        var activity = editViewModel.route.value?.routeActivities!![i]
                        addMarker(
                            activity.latitude.toDouble(), activity.longitude.toDouble(),
                            activity.category, (i + 1).toString()
                        )
                    }
                }

                initObserve()
                addBackgroundDots()
                drawRoutePath()
            }

            override fun getZoomLevel(): Int {
                return 17
            }
        })
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
            if (sharedPreferencesHelper.getRouteActivity() != null) {
                showCallActivityDataPopupDialog()
            } else {
                startActivity(Intent(requireActivity(), RouteActivityActivity::class.java).putExtra("routeId", writeViewModel.routeId.value))
            }
        }

        // 활동이 1개 이상일 때 나타나는 활동 추가 버튼
        bottomSheetDialog.activityAddBtn.setOnClickListener {
            if (sharedPreferencesHelper.getRouteActivity() != null) {
                showCallActivityDataPopupDialog()
            } else {
                startActivity(Intent(requireActivity(), RouteActivityActivity::class.java).putExtra("routeId", writeViewModel.routeId.value))
            }
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
                showDeletePopupDialog()
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
            if (writeViewModel.currentCoordinate.value != null) {
                val cameraUpdate = CameraUpdateFactory.newCenterPosition(writeViewModel.currentCoordinate.value)
                kakaoMap.moveCamera(cameraUpdate)

                addMarker(writeViewModel.currentCoordinate.value!!.latitude,
                    writeViewModel.currentCoordinate.value!!.longitude, "", "")
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

    private fun addBackgroundDots() {
        if (sharedPreferencesHelper.getBackgroundCoordinate() != null) {
            val backgroundDots = sharedPreferencesHelper.getBackgroundCoordinate()
            for (i in 0 until backgroundDots!!.size) {
                writeViewModel.addDot(backgroundDots[i]!!.latitude, backgroundDots[i]!!.longitude)
            }
            sharedPreferencesHelper.setBackgroundCoordinate(arrayListOf())
        }
    }

    // 크래시가 발생할 수도 있어 지도의 LifeCycle도 함께 관리 필요!
    override fun onResume() {
        super.onResume()
        binding.trackingMap.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.trackingMap.pause()
        sharedPreferencesHelper.setIsBackground(true)
    }

    // 마커 띄우기
    private fun addMarker(latitude: Double, longitude: Double, category: String, activityNumber: String) {
        var markerImg: Int
        if (category != "") {
            markerImg = returnActivityCategoryImg(category)
        } else {
            markerImg = R.drawable.ic_gps_marker
        }
        var styles = kakaoMap.labelManager?.addLabelStyles(
            LabelStyles.from(LabelStyle.from(markerImg).setTextStyles(
            LabelTextStyle.from(35, ContextCompat.getColor(requireActivity(), R.color.black)),
        )))
        val layer = kakaoMap.labelManager!!.layer
        val options = LabelOptions.from(LatLng.from(latitude, longitude)).setStyles(styles)
        if (category != "") {
            options.setTexts(LabelTextBuilder().setTexts(activityNumber))
        }
        val label = layer!!.addLabel(options)
        label.show()
    }

    private fun showDeletePopupDialog() {
        val dialog = CommonPopupDialog(this, DialogType.DELETE.id, String.format(resources.getString(R.string.activity_delete_popup)), null, null)
        dialog.isCancelable = false // 배경 클릭 막기
        dialog.show(requireActivity().supportFragmentManager, "PopupDialog")
    }

    private fun showCallActivityDataPopupDialog() {
        val dialog = CommonPopupDialog(this, DialogType.CALL_ACTIVITY_DATA.id, String.format(resources.getString(R.string.check_activity_data)), null, null)
        dialog.isCancelable = false // 배경 클릭 막기
        dialog.show(requireActivity().supportFragmentManager, "PopupDialog")
    }

    override fun onClickPositiveButton(id: Int) {
        if (id == DialogType.DELETE.id) {
            Toast.makeText(requireActivity(), "활동이 삭제되었습니다", Toast.LENGTH_SHORT).show()
            editViewModel.deleteActivity(deleteId)
        } else {
            writeViewModel.checkIsContinuedActivity.value = true
            Log.d("ROUTE-TEST", "trackingFragment writeViewModel.checkContinueActivity.value = ${writeViewModel.checkIsContinuedActivity.value}")
            startActivity(Intent(requireActivity(), RouteActivityActivity::class.java).putExtra("routeId", writeViewModel.routeId.value))
        }
    }

    override fun onClickNegativeButton(id: Int) {
        startActivity(Intent(requireActivity(), RouteActivityActivity::class.java).putExtra("routeId", writeViewModel.routeId.value))
    }

    private fun drawRoutePath() {
        // 기록된 점 조회를 위해 api 호출
        editViewModel.tryGetMyRouteDetail()

        var layer = kakaoMap.routeLineManager?.addLayer()
        val stylesSet = RouteLineStylesSet.from(
            "routePathStyle", RouteLineStyles.from(RouteLineStyle.from(8f, ContextCompat.getColor(requireActivity(), R.color.main)))
        )
        val segment: RouteLineSegment = RouteLineSegment.from(routePathToLatLng()).setStyles(stylesSet.getStyles(0))
        val options = RouteLineOptions.from(segment).setStylesSet(stylesSet)
        val routeLine: RouteLine = layer!!.addRouteLine(options)
        routeLine.show()

        routePathToLatLng()
    }

    private fun routePathToLatLng(): List<LatLng>? {
        val routePath = editViewModel.route.value?.routePath
        return routePath?.map {
            LatLng.from(it.latitude, it.longitude)
        }
    }

    private fun returnActivityCategoryImg(category: String): Int {
        return when (category) {
            "숙소" -> R.drawable.activity_accommodation
            "관광지" -> R.drawable.activity_tourist
            "음식점" -> R.drawable.activity_restaurant
            "카페" -> R.drawable.activity_cafe
            "SNS 스팟" -> R.drawable.activity_sns_spot
            "문화 공간" -> R.drawable.activity_culture
            "화장실" -> R.drawable.activity_toilet
            "주차장" -> R.drawable.activity_parking
            else -> R.drawable.activity_etc
        }
    }
}