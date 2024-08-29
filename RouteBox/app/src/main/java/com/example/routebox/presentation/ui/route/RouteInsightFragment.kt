package com.example.routebox.presentation.ui.route

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routebox.R
import com.example.routebox.databinding.FragmentRouteInsightBinding
import com.example.routebox.domain.model.DialogType
import com.example.routebox.presentation.ui.route.adapter.MyRouteRVAdapter
import com.example.routebox.presentation.ui.route.edit.RouteEditBaseActivity
import com.example.routebox.presentation.ui.seek.comment.CommentActivity
import com.example.routebox.presentation.utils.CommonPopupDialog
import com.example.routebox.presentation.utils.PopupDialogInterface
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class RouteInsightFragment : Fragment(), PopupDialogInterface {
    private lateinit var binding: FragmentRouteInsightBinding

    private lateinit var myRouteAdapter: MyRouteRVAdapter

    private val viewModel: RouteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        binding = FragmentRouteInsightBinding.inflate(inflater, container, false)

        binding.apply {
            viewModel = this@RouteInsightFragment.viewModel
            lifecycleOwner = this@RouteInsightFragment
        }

        setInit()
        initClickListeners()
        initObserve()

        return binding.root
    }

    private fun setInit() {
        setAdapter()
        viewModel.tryGetInsight() // 인사이트 조회 API 호출
        viewModel.tryGetMyRouteList() // 내 루트 목록 조회 API 호출
    }

    private fun initClickListeners() {
        // 안드로이드 기본 뒤로가기 버튼 클릭
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        binding.insightBackIv.setOnClickListener {
            findNavController().popBackStack() // 뒤로가기
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
                showMenu(view!!)  // 옵션 메뉴 띄우기
            }

            override fun onCommentButtonClick(position: Int) { // 댓글 아이콘 클릭
                // 댓글 화면으로 이동
                val intent = Intent(requireActivity(), CommentActivity::class.java)
                //TODO: 댓글 화면에서 필요한 정보 넘기기 (routeId 등)
                intent.putExtra("comment", viewModel.routeList.value!![position].routeName)
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
                    // 루트 수정 화면으로 이동
                    val intent = Intent(requireActivity(), RouteEditBaseActivity::class.java)
                    intent.apply {
                        putExtra("route", Gson().toJson(viewModel.routeList.value!![viewModel.selectedRouteId]))
                        putExtra("isEditMode", true)
                    }
                    startActivity(intent)
                    true
                }
                R.id.menu_make_public_or_private -> { // 공개/비공개 전환
                    showChangePublicPopupDialog()
                    true
                }
                R.id.menu_delete -> { // 삭제하기
                    showDeletePopupDialog()
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
}