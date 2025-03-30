package com.daval.routebox.presentation.ui.route

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.daval.routebox.R
import com.daval.routebox.databinding.FragmentRouteBinding
import com.daval.routebox.domain.model.DialogType
import com.daval.routebox.presentation.ui.route.adapter.MyRouteRVAdapter
import com.daval.routebox.presentation.ui.route.edit.RouteEditBaseActivity
import com.daval.routebox.presentation.ui.route.write.RouteCreateActivity
import com.daval.routebox.presentation.ui.route.write.RouteWriteActivity
import com.daval.routebox.presentation.ui.seek.comment.CommentActivity
import com.daval.routebox.presentation.utils.CommonPopupDialog
import com.daval.routebox.presentation.utils.PopupDialogInterface
import com.daval.routebox.presentation.utils.SharedPreferencesHelper
import com.daval.routebox.presentation.utils.SharedPreferencesHelper.Companion.APP_PREF_KEY
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class RouteFragment : Fragment(), PopupDialogInterface {
    private lateinit var binding: FragmentRouteBinding

    private lateinit var myRouteAdapter: MyRouteRVAdapter
    private val viewModel: RouteViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        binding = FragmentRouteBinding.inflate(inflater, container, false)

        binding.apply {
            viewModel = this@RouteFragment.viewModel
            lifecycleOwner = this@RouteFragment
        }

        setAdapter()
        initClickListeners()
        initObserve()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setInit()
    }

    private fun setInit() {
        viewModel.tryGetMyRouteList() // 내 루트 목록 조회 API 호출
        viewModel.tryGetIsRouteRecording() // 기록 진행 중인 루트 여부 조회 API 호출
    }

    private fun initClickListeners() {
        // 인사이트 버튼
        binding.routeStatisticsIv.setOnClickListener {
            // 인사이트 화면으로 이동
            findNavController().navigate(R.id.action_routeFragment_to_routeInsightFragment)
        }

        // 루트 시작하기 버튼
        binding.routeRecordStartBtn.setOnClickListener {
            // 루트 시작하기 화면으로 이동
            startActivity(Intent(requireActivity(), RouteCreateActivity::class.java))
        }

        // 기록중인 루트 보러가기 버튼
        binding.routeSeeTrackingBtn.setOnClickListener {
            if (checkRouteEnd()) { // 루트 기록 종료 시간이 지났다면
                startActivity(Intent(requireActivity(), RouteWriteCompleteActivity::class.java).putExtra("routeId", viewModel.recordingRouteId))
            } else { // 루트 기록 종료 시간이 지나지 않았다면
                startActivity(Intent(requireActivity(), RouteWriteActivity::class.java).putExtra("routeId", viewModel.recordingRouteId.toString()))
            }
        }
    }

    private fun setAdapter() {
        myRouteAdapter = MyRouteRVAdapter()
        binding.routeMyRv.apply {
            adapter = myRouteAdapter
            layoutManager = LinearLayoutManager(context)
        }
        myRouteAdapter.setRouteClickListener(object: MyRouteRVAdapter.MyItemClickListener {
            override fun onMoreButtonClick(view: View?, routeId: Int, isPublic: Boolean) { // 더보기 버튼 클릭
                viewModel.selectedRouteId = routeId // 선택 id 업데이트
                viewModel.isPublic = isPublic
                showMenu(view!!) // 옵션 메뉴 띄우기
            }

            override fun onCommentButtonClick(position: Int) { // 댓글 아이콘 클릭
                // 댓글 화면으로 이동
                val intent = Intent(requireActivity(), CommentActivity::class.java)
                //TODO: 댓글 화면에서 필요한 정보 넘기기 (routeId 등)
                intent.putExtra("routeName", viewModel.routeList.value!![position].routeName)
                    .putExtra("routeId", viewModel.routeList.value!![position].routeId)
                startActivity(intent)
            }

            override fun onItemClick(routeId: Int) { // 아이템 전체 클릭
                // 루트 보기 화면으로 이동
                startActivity(Intent(requireActivity(), RouteDetailActivity::class.java).putExtra("routeId", routeId))
            }
        })
    }

    private fun initObserve() {
        // routeList를 관찰하여 리사이클러뷰 아이템에 추가
        viewModel.routeList.observe(viewLifecycleOwner) { routeList ->
            Log.d("RouteFragment", "routeList: $routeList")
            if (!routeList.isNullOrEmpty()) {
                myRouteAdapter.addRoute(routeList)
            }
        }

        viewModel.isGetRouteDetailSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                // 루트 수정 화면으로 이동
                val intent = Intent(requireActivity(), RouteEditBaseActivity::class.java)
                intent.apply {
                    putExtra("route", Gson().toJson(viewModel.route.value))
                    putExtra("isEditMode", true)
                    putExtra("routeId", viewModel.route.value?.routeId)
                }
                startActivity(intent)
            }
        }

        // 삭제 성공 유무를 관측하여 삭제 시 routeList 업데이트
        viewModel.isDeleteRouteSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                viewModel.tryGetMyRouteList()
            }
        }
    }

    private fun showMenu(view: View) {
        val popupMenu = PopupMenu(requireActivity(), view)
        popupMenu.inflate(R.menu.route_my_menu)
        // 공개 여부에 따라 메뉴 아이템의 텍스트 변경
        val changeShowMenuItem = popupMenu.menu.findItem(R.id.menu_make_public_or_private)
        if (viewModel.isPublic) {
            changeShowMenuItem.setTitle(R.string.route_my_make_private)
        } else {
            changeShowMenuItem.setTitle(R.string.route_my_make_public)
        }
        // 메뉴 노출
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_edit -> { // 수정하기
                    // 루트 상세조회 API 호출 후 화면 이동
                    viewModel.tryGetMyRouteDetail(viewModel.selectedRouteId)
                    true
                }
                R.id.menu_make_public_or_private -> { // 공개/비공개 전환
                    showChangePublicPopupDialog() // 확인 다이얼로그 노출
                    true
                }
                R.id.menu_delete -> { // 삭제하기
                    showDeletePopupDialog() // 확인 다이얼로그 노출
                    true
                }
                else -> { false }
            }
        }
        popupMenu.show()
    }

    private fun showChangePublicPopupDialog() {
        val popupContent = if (viewModel.isPublic) R.string.route_my_change_to_private_popup_content else R.string.route_my_change_to_public_popup_content
        val dialog = CommonPopupDialog(this, DialogType.CHANGE_PUBLIC.id, String.format(resources.getString(popupContent)), null, null)
        dialog.isCancelable = false // 배경 클릭 막기
        dialog.show(parentFragmentManager, "PopupDialog")
    }

    private fun showDeletePopupDialog() {
        val dialog = CommonPopupDialog(this, DialogType.DELETE.id, String.format(resources.getString(R.string.activity_delete_popup)), null, null)
        dialog.isCancelable = false // 배경 클릭 막기
        dialog.show(parentFragmentManager, "PopupDialog")
    }

    override fun onClickPositiveButton(id: Int) {
        if (DialogType.getDialogTypeById(id) == DialogType.CHANGE_PUBLIC) { // 공개 여부 전환 확인
            // 공개 상태라면 비공개 전환, 비공개 상태라면 공개 전환
            viewModel.tryChangePublic()
        } else { // 삭제 확인
            viewModel.tryDeleteRoute()
        }
    }

    override fun onClickNegativeButton(id: Int) { }

    private fun checkRouteEnd(): Boolean {
        val sharedPreferencesHelper = SharedPreferencesHelper(requireActivity().getSharedPreferences(APP_PREF_KEY, Context.MODE_PRIVATE))
        val endTime = sharedPreferencesHelper.getEndTime().toString()
        val nowTime = LocalDateTime.now().toString().split(".")[0]

        // endTime이 지난 경우
        if (nowTime.compareTo(endTime) >= 0) {
            return true
        } else { // endTime이 지나기 전인 경우
            return false
        }
    }
}