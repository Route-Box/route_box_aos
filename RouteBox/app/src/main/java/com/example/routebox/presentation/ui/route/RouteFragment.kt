package com.example.routebox.presentation.ui.route

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routebox.R
import com.example.routebox.databinding.FragmentRouteBinding
import com.example.routebox.presentation.ui.route.adapter.MyRouteRVAdapter
import com.example.routebox.presentation.ui.route.edit.RouteEditActivity
import com.example.routebox.presentation.ui.route.record.RouteCreateActivity
import com.example.routebox.presentation.ui.seek.comment.CommentActivity
import com.google.gson.Gson

class RouteFragment : Fragment() {
    private lateinit var binding: FragmentRouteBinding

    private lateinit var myRouteAdapter: MyRouteRVAdapter

    private val viewModel: RouteViewModel by viewModels()

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

        initClickListeners()
        initObserve()

        return binding.root
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
    }

    private fun setAdapter() {
        myRouteAdapter = MyRouteRVAdapter()
        binding.routeMyRv.apply {
            adapter = myRouteAdapter
            layoutManager = LinearLayoutManager(context)
        }
        myRouteAdapter.setRouteClickListener(object: MyRouteRVAdapter.MyItemClickListener {
            override fun onMoreButtonClick(view: View?, position: Int, isPrivate: Boolean) { // 더보기 버튼 클릭
                // 옵션 메뉴 띄우기
                showMenu(view!!, isPrivate)
            }

            override fun onCommentButtonClick(position: Int) { // 댓글 아이콘 클릭
                // 댓글 화면으로 이동
                val intent = Intent(requireActivity(), CommentActivity::class.java)
                //TODO: 댓글 화면에서 필요한 정보 넘기기 (routeId 등)
                intent.putExtra("comment", viewModel.routeList.value!![position].title)
                startActivity(intent)
            }

            override fun onItemClick(position: Int) { // 아이템 전체 클릭
                // 루트 보기 화면으로 이동
                val intent = Intent(requireActivity(), RouteDetailActivity::class.java)
                val routeJson = Gson().toJson(viewModel.routeList.value!![position])
                intent.putExtra("route", routeJson)
                startActivity(intent)
            }
        })
    }

    private fun initObserve() {
        // routeList를 관찰하여 리사이클러뷰 아이템에 추가
        viewModel.routeList.observe(viewLifecycleOwner) { routeList ->
            Log.d("RouteFragment", "routeList: $routeList")
            if (!routeList.isNullOrEmpty()) {
                setAdapter()
                myRouteAdapter.addRoute(routeList)
            }
        }
    }

    private fun showMenu(view: View, isPrivate: Boolean) {
        val popupMenu = PopupMenu(requireActivity(), view)
        popupMenu.inflate(R.menu.route_my_menu)
        // 공개 여부에 따라 메뉴 아이템의 텍스트 변경
        val changeShowMenuItem = popupMenu.menu.findItem(R.id.menu_make_public_or_private)
        if (isPrivate) {
            changeShowMenuItem.setTitle(R.string.route_my_make_public)
        } else {
            changeShowMenuItem.setTitle(R.string.route_my_make_private)
        }
        // 메뉴 노출
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_edit -> {
                    // 루트 수정 화면으로 이동
                    startActivity(Intent(requireActivity(), RouteEditActivity::class.java))
                    true
                }
                R.id.menu_make_public_or_private -> {
                    Toast.makeText(requireContext(), "공개/비공개 전환 메뉴 클릭", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_delete -> {
                    Toast.makeText(requireContext(), "삭제하기 메뉴 클릭", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> { false }
            }
        }
        popupMenu.show()
    }
}