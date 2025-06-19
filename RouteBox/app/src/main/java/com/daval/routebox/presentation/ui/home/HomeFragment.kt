package com.daval.routebox.presentation.ui.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.daval.routebox.R
import com.daval.routebox.databinding.FragmentHomeBinding
import com.daval.routebox.domain.model.PopularRoute
import com.daval.routebox.domain.model.RecommendRoute
import com.daval.routebox.presentation.ui.route.RouteDetailActivity
import com.daval.routebox.presentation.utils.RecyclerViewHorizontalDecoration
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private lateinit var recommendRouteRVAdapter: RecommendRouteRVAdapter
    private var recommendRouteList = arrayListOf<RecommendRoute>()
    private lateinit var popularRouteRVAdapter: PopularRouteRVAdapter
    private var popularRouteList = arrayListOf<PopularRoute>()
    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        setAdapter()

        return binding.root
    }

    private fun setAdapter() {
        recommendRouteRVAdapter = RecommendRouteRVAdapter(recommendRouteList)
        binding.recommendRv.apply {
            adapter = recommendRouteRVAdapter
            layoutManager = LinearLayoutManager(context)
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
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(RecyclerViewHorizontalDecoration("right", 13))
        }
        popularRouteRVAdapter.setRouteClickListener(object: PopularRouteRVAdapter.RouteItemClickListener {
            override fun onItemClick(routeId: Int) {
                // 루트 보기 화면으로 이동
                startActivity(Intent(requireActivity(), RouteDetailActivity::class.java).putExtra("routeId", routeId))
            }
        })
    }

    // MEMO: 웹뷰에서 다른 프래그먼트로 이동을 위해 썼던 코드 / UI 제작 후 사용 안할 시 제거
    private fun selectBottomNavTab(tabId: Int) {
        val bottomNavView = requireActivity().findViewById<BottomNavigationView>(R.id.main_bottom_nav)
        bottomNavView.post {
            bottomNavView.menu.performIdentifierAction(tabId, 0)
        }
    }
}