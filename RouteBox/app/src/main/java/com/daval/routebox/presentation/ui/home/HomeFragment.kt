package com.daval.routebox.presentation.ui.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.daval.routebox.R
import com.daval.routebox.databinding.FragmentHomeBinding
import com.daval.routebox.domain.model.PopularRoute
import com.daval.routebox.domain.model.RecommendRoute
import com.daval.routebox.presentation.ui.route.RouteDetailActivity
import com.daval.routebox.presentation.ui.seek.wallet.WalletActivity
import com.daval.routebox.presentation.utils.RecyclerViewHorizontalDecoration
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private lateinit var recommendRouteRVAdapter: RecommendRouteRVAdapter
    // TODO: API 연동 후 더미데이터 삭제
    private var recommendRouteList = arrayListOf<RecommendRoute>(
        RecommendRoute(1, "ROUTE1", "ROUTE-1", "https://imagescdn.gettyimagesbank.com/500/201907/jv11447791.jpg"),
        RecommendRoute(2, "ROUTE2", "ROUTE-2", "https://imagescdn.gettyimagesbank.com/500/201907/jv11447309.jpg"),
        RecommendRoute(3, "ROUTE3", "ROUTE-3", "https://imagescdn.gettyimagesbank.com/500/201907/jv11447791.jpg"),
        RecommendRoute(4, "ROUTE4", "ROUTE-4", "https://imagescdn.gettyimagesbank.com/500/201907/jv11447309.jpg"),
        RecommendRoute(5, "ROUTE5", "ROUTE-5", "https://imagescdn.gettyimagesbank.com/500/201907/jv11447791.jpg"),
        RecommendRoute(6, "ROUTE6", "ROUTE-6", "https://imagescdn.gettyimagesbank.com/500/201907/jv11447309.jpg"),
    )
    private lateinit var popularRouteRVAdapter: PopularRouteRVAdapter
    private var popularRouteList = arrayListOf<PopularRoute>(
        PopularRoute(1, "루트 1"),
        PopularRoute(1, "루트 2"),
        PopularRoute(1, "루트 3"),
        PopularRoute(1, "루트 4"),
        PopularRoute(1, "루트 5"),
        PopularRoute(1, "루트 6")
    )
    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.apply {
            viewModel = this@HomeFragment.viewModel
            lifecycleOwner = this@HomeFragment
        }

        setAdapter()
        initClickListener()

        return binding.root
    }

    private fun setAdapter() {
        recommendRouteRVAdapter = RecommendRouteRVAdapter(recommendRouteList)
        binding.recommendRv.apply {
            adapter = recommendRouteRVAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(RecyclerViewHorizontalDecoration("right", 12))
        }
        recommendRouteRVAdapter.setRouteClickListener(object: RecommendRouteRVAdapter.RouteItemClickListener {
            override fun onItemClick(routeId: Int) {
                // 루트 보기 화면으로 이동
                startActivity(Intent(requireActivity(), RouteDetailActivity::class.java).putExtra("routeId", routeId))
            }
        })

        popularRouteRVAdapter = PopularRouteRVAdapter(popularRouteList)
        binding.popularRv.apply {
            adapter = popularRouteRVAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(RecyclerViewHorizontalDecoration("right", 13))
        }
        popularRouteRVAdapter.setRouteClickListener(object: PopularRouteRVAdapter.RouteItemClickListener {
            override fun onItemClick(routeId: Int) {
                // 루트 보기 화면으로 이동
                startActivity(Intent(requireActivity(), RouteDetailActivity::class.java).putExtra("routeId", routeId))
            }
        })
    }

    private fun initClickListener() {
        binding.topPointIv.setOnClickListener {
            startActivity(Intent(requireActivity(), WalletActivity::class.java))
        }
    }

    // MEMO: 웹뷰에서 다른 프래그먼트로 이동을 위해 썼던 코드 / UI 제작 후 사용 안할 시 제거
    private fun selectBottomNavTab(tabId: Int) {
        val bottomNavView = requireActivity().findViewById<BottomNavigationView>(R.id.main_bottom_nav)
        bottomNavView.post {
            bottomNavView.menu.performIdentifierAction(tabId, 0)
        }
    }
}