package com.daval.routebox.presentation.ui.route.edit

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.daval.routebox.R
import com.daval.routebox.databinding.BottomSheetActivityBinding
import com.daval.routebox.databinding.FragmentRouteEditActivityBinding
import com.daval.routebox.domain.model.ActivityResult
import com.daval.routebox.domain.model.DialogType
import com.daval.routebox.presentation.ui.route.RouteActivityActivity
import com.daval.routebox.presentation.ui.route.adapter.ActivityRVAdapter
import com.daval.routebox.presentation.utils.CommonPopupDialog
import com.daval.routebox.presentation.utils.PopupDialogInterface
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.MapLifeCycleCallback

@RequiresApi(Build.VERSION_CODES.O)
class RouteEditActivityFragment : Fragment(), PopupDialogInterface {
    private lateinit var binding: FragmentRouteEditActivityBinding

    private val viewModel: RouteEditViewModel by activityViewModels()
    private lateinit var bottomSheetDialog: BottomSheetActivityBinding
    private lateinit var kakaoMap: KakaoMap
    private val activityAdapter = ActivityRVAdapter(true)

    private var deleteId: Int = -1
    private var deleteActivityIndex: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRouteEditActivityBinding.inflate(inflater, container, false)

        binding.apply {
            viewModel = this@RouteEditActivityFragment.viewModel
            lifecycleOwner = this@RouteEditActivityFragment
        }

        initMapSetting()
        setInit()
        setActivityAdapter()
        initClickListeners()
        initObserve()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.tryGetMyRouteDetail() // 루트 상세조회 API 호출
    }

    private fun initMapSetting() {
        binding.kakaoMap.start(object : MapLifeCycleCallback() {
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
                this@RouteEditActivityFragment.kakaoMap = kakaoMap
            }
        })
    }

    private fun setInit() {
        bottomSheetDialog = binding.routeEditActivityBottomSheet
        bottomSheetDialog.apply {
            this.viewModel = this@RouteEditActivityFragment.viewModel
            this.lifecycleOwner = this@RouteEditActivityFragment
        }

        viewModel.setStepId(2)
    }

    private fun initClickListeners() {
        // 활동 추가 버튼
        bottomSheetDialog.activityAddBtn.setOnClickListener {
            startActivity(Intent(activity, RouteActivityActivity::class.java)
                .putExtra("routeId", viewModel.routeId.value)
            )
        }

        // 활동 아이템 클릭
        activityAdapter.setActivityClickListener(object : ActivityRVAdapter.MyItemClickListener {
            override fun onEditButtonClick(position: Int, data: ActivityResult) { // 활동 수정
                startActivity(Intent(activity, RouteActivityActivity::class.java).apply {
                    putExtra("routeId", viewModel.routeId.value)
                    putExtra("activity", viewModel.route.value!!.routeActivities[position])
                    putExtra("isEdit", true)
                })
            }

            override fun onDeleteButtonClick(position: Int) { // 활동 삭제
                deleteId = activityAdapter.returnActivityId(position)
                deleteActivityIndex = position
                // 활동 삭제 팝업 띄우기
                showPopupDialog()
            }
        })
    }

    private fun setActivityAdapter() {
        bottomSheetDialog.activityRv.apply {
            this.adapter = activityAdapter
            this.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun initObserve() {
        viewModel.route.observe(viewLifecycleOwner) { route ->
            if (route.routeActivities.isNotEmpty()) {
                activityAdapter.addAllActivities(route.routeActivities as MutableList<ActivityResult>)
            }
        }
    }

    private fun showPopupDialog() {
        val dialog = CommonPopupDialog(this@RouteEditActivityFragment, DialogType.DELETE.id, String.format(resources.getString(R.string.activity_delete_popup)), null, null)
        dialog.isCancelable = false // 배경 클릭 막기
        activity?.let { dialog.show(it.supportFragmentManager, "PopupDialog") }
    }

    override fun onClickPositiveButton(id: Int) {
        Toast.makeText(requireContext(), "활동이 삭제되었습니다", Toast.LENGTH_SHORT).show()
        viewModel.deleteActivity(deleteId)
        activityAdapter.removeItem(deleteActivityIndex)
    }
}