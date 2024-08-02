package com.example.routebox.presentation.ui.seek

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routebox.R
import com.example.routebox.databinding.FragmentSeekBinding
import com.example.routebox.domain.model.RoutePreview
import com.example.routebox.presentation.ui.seek.adapter.SeekHomeRouteRVAdapter

class SeekFragment : Fragment() {

    private lateinit var binding: FragmentSeekBinding
    private lateinit var routeAdapter: SeekHomeRouteRVAdapter
    private var routeList = arrayListOf<RoutePreview>()

    private var checkInitial: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeekBinding.inflate(inflater, container, false)

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
        routeList.add( RoutePreview(null, "2", "2024-07-1",
            arrayListOf(
                "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg",
                "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg",
                "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg"
            ), "제목", "내용", 1, 1) )
        routeList.add( RoutePreview(null, "3", "2024-07-2",
            arrayListOf(
                "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg",
                "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg"
            ), "제목", "내용", 1, 1) )
        routeList.add( RoutePreview(null, "4", "2024-07-3",
            arrayListOf(
                "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg"
            ), "제목", "내용", 1, 1) )
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
        routeAdapter = SeekHomeRouteRVAdapter(routeList)
        binding.seekHomeRv.adapter = routeAdapter
        binding.seekHomeRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private fun initClickListener() {
        binding.topPointIv.setOnClickListener {
            startActivity(Intent(context, WalletActivity::class.java))
        }
    }

    private fun initScrollLoading() {
//        binding.seekHomeRv.setOnTouchListener(object: OnSwipeTouchListener(binding.root.context) {
//            @SuppressLint("ClickableViewAccessibility")
//            override fun onSwipeBottom() {
//                super.onSwipeBottom()
//                if (binding.seekHomeRv.canScrollVertically(1)) {
//                    Log.d("SWIPE-TEST","아래로")
//                    if (checkInitial) {
//                        checkInitial = !checkInitial
//                    } else {
//                        if (!routeAdapter.checkLoading()) routeAdapter.addLoading()
//                    }
//                } else {
//                    if (routeAdapter.checkLoading()) routeAdapter.deleteLoading()
//                }
//            }
//        })
//        binding.seekHomeRv.addOnScrollListener(object: RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//
//                // RecyclerView가 최상단에 도달했을 때
//                if (binding.seekHomeRv.canScrollVertically(1)) {
//                    if (checkInitial) {
//                        checkInitial = !checkInitial
//                    } else {
//                        if (!routeAdapter.checkLoading())
//                            routeAdapter.addLoading()
//                    }
//                } else {
//                    if (routeAdapter.checkLoading()) {
//                        routeAdapter.deleteLoading()
//                    }
//                }
//            }
//        })
//
//        // TODO: 로딩을 멈추기 위해 임시로 넣어둔 부분! 나중에 API와 연결 필요
//        binding.routeTitle.setOnClickListener {
//            routeAdapter.deleteLoading()
//        }
    }
}