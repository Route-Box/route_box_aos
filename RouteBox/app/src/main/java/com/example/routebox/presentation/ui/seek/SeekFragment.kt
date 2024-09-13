package com.example.routebox.presentation.ui.seek

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.example.routebox.R
import com.example.routebox.databinding.FragmentSeekBinding
import com.example.routebox.presentation.ui.common.report.ReportFeedActivity
import com.example.routebox.presentation.ui.seek.adapter.SeekHomeRouteRVAdapter
import com.example.routebox.presentation.ui.seek.comment.CommentActivity
import com.example.routebox.presentation.ui.seek.wallet.WalletActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlin.Boolean
import kotlin.Int
import kotlin.apply
import kotlin.getValue

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class SeekFragment : Fragment() {

    private lateinit var binding: FragmentSeekBinding
    private val viewModel : SeekViewModel by viewModels()
    private lateinit var routeAdapter: SeekHomeRouteRVAdapter
    private var isEnd: Boolean = false
    private var isBottom: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeekBinding.inflate(inflater, container, false)

        binding.apply {
            viewModel = this@SeekFragment.viewModel
            lifecycleOwner = this@SeekFragment
        }

        isEnd = false
        isBottom = false

        setAdapter()
        initClickListener()
        initScrollLoading()
        initScrollListener()
        initObserve()

        viewModel.refresh()
        viewModel.getRouteList()

        return binding.root
    }

    private fun setAdapter() {
        routeAdapter = SeekHomeRouteRVAdapter()
        binding.seekHomeRv.adapter = routeAdapter
        binding.seekHomeRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        routeAdapter.setRouteCommentClickListener(object: SeekHomeRouteRVAdapter.MyItemClickListener {
            override fun commentItemClick(position: Int) {
                val intent = Intent(context, CommentActivity::class.java)
                // TODO: 루트 아이디 보내주기
                intent.putExtra("routeId", 0)
                startActivity(intent)
            }
            override fun moreItemClick(view: View, position: Int) {
                reportMenuShow(view!!)
            }
        })
        binding.seekHomeRv.itemAnimator = null
    }

    private fun initClickListener() {
        binding.topPointIv.setOnClickListener {
            startActivity(Intent(context, WalletActivity::class.java))
        }

        binding.topSearchIv.setOnClickListener {
            findNavController().navigate(R.id.action_seekFragment_to_searchFragment)
        }
    }

    private fun reportMenuShow(view: View) {
        val popupMenu = PopupMenu(activity, view)
        popupMenu.inflate(R.menu.report_menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_report -> {
                    // 신고하기 화면으로 이동
                    startActivity(Intent(requireActivity(), ReportFeedActivity::class.java))
                    true
                }
                else -> { false }
            }
        }
        popupMenu.show()
    }

    private fun initObserve() {
        viewModel.routeList.observe(viewLifecycleOwner) {
            if (viewModel.page.value == 0 && viewModel.routeList.value?.size != 0) {
                routeAdapter.addItems(viewModel.routeList.value!!)
                viewModel.setPage(viewModel.page.value!! + 1)

                // 만약 새로고침으로 인해 값이 변경되었다면, 새로고침을 안 보이게 처리
                if (binding.swipeLayout.isRefreshing) binding.swipeLayout.isRefreshing = false
            } else {
                if (isBottom) {
                    routeAdapter.addItems(viewModel.routeList.value!!)
                    isBottom = false
                    viewModel.setPage(viewModel.page.value!! + 1)
                }
            }
        }
    }

    private fun initScrollListener() {
        binding.nestedSv.setOnScrollChangeListener(object: View.OnScrollChangeListener {
            override fun onScrollChange(p0: View?, p1: Int, p2: Int, p3: Int, p4: Int) {
                if (!binding.nestedSv.canScrollVertically(1)) {
                    if (!isEnd) {
                        viewModel.getRouteList()
                    }
                    if (viewModel.routeList.value!!.size < 3) isEnd = true

                    isBottom = true
                }
            }
        })
    }

    private fun initScrollLoading() {
        binding.swipeLayout.setOnRefreshListener(OnRefreshListener {
            // 새로고침으로 인한 기존의 루트 삭제 및 초기화
            viewModel.refresh()
            routeAdapter.resetItems()
            viewModel.getRouteList()
        })
    }
}