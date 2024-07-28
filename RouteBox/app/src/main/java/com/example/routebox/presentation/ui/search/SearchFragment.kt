package com.example.routebox.presentation.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.routebox.databinding.FragmentSearchBinding
import com.example.routebox.domain.model.RoutePreview
import com.example.routebox.presentation.ui.search.adapter.RouteImageVPAdapter
import com.example.routebox.presentation.ui.search.adapter.SearchHomeRouteRVAdapter

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var routeAdapter: SearchHomeRouteRVAdapter
    private var routeList = arrayListOf<RoutePreview>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        // 더미데이터
        routeList.add(
            RoutePreview("https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg",
            "1", "2024-07-23",
            arrayListOf(
                "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg",
                "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg",
                "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg",
                "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg"
            ), "제목", "내용", 1, 1)
        )
        routeList.add( RoutePreview(null, "2", "2024-07-1", null, "제목", "내용", 1, 1) )
        routeList.add( RoutePreview(null, "3", "2024-07-2", null, "제목", "내용", 1, 1) )
        routeList.add( RoutePreview(null, "4", "2024-07-3", null, "제목", "내용", 1, 1) )
        routeList.add( RoutePreview(null, "5", "2024-07-4", null, "제목", "내용", 1, 1) )
        routeList.add( RoutePreview(null, "6", "2024-07-5", null, "제목", "내용", 1, 1) )
        routeList.add( RoutePreview(null, "7", "2024-07-6", null, "제목", "내용", 1, 1) )
        routeList.add( RoutePreview(null, "8", "2024-07-7", null, "제목", "내용", 1, 1) )
        routeList.add( RoutePreview(null, "9", "2024-07-8", null, "제목", "내용", 1, 1) )
        routeList.add( RoutePreview(null, "10", "2024-07-9", null, "제목", "내용", 1, 1) )

        setAdapter()
        initClickListener()
        initScrollLoading()

        return binding.root
    }

    private fun setAdapter() {
        routeAdapter = SearchHomeRouteRVAdapter(routeList)
        binding.searchHomeRv.adapter = routeAdapter
        binding.searchHomeRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private fun initClickListener() {
        binding.topPointIv.setOnClickListener {
            startActivity(Intent(context, WalletActivity::class.java))
        }
    }

    private fun initScrollLoading() {
        binding.searchHomeRv.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // RecyclerView가 최상단에 도달했을 때
                if (binding.searchHomeRv.canScrollVertically(-1)) {

                }
            }
        })
    }
}