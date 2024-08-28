package com.example.routebox.presentation.ui.seek

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routebox.R
import com.example.routebox.databinding.FragmentSeekBinding
import com.example.routebox.domain.model.FilterOption
import com.example.routebox.domain.model.RoutePreview
import com.example.routebox.presentation.ui.seek.adapter.SeekHomeRouteRVAdapter
import com.example.routebox.presentation.ui.seek.comment.CommentActivity
import com.example.routebox.presentation.ui.seek.wallet.WalletActivity

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
            RoutePreview(-1, -1, "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg", "닉네임닉네임", "강릉", "강릉 여행",
            arrayListOf(
                "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg", "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg",
                "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg", "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg"
            ), false, 20, 12, arrayListOf(FilterOption.MANY_TWO.optionName, FilterOption.TRANSPORTATION_PUBLIC_TRANSPORTATION.optionName,
                    FilterOption.WITH_CHILD.optionName, FilterOption.STYLE_ETC.optionName, FilterOption.MANY_FOUR.optionName, FilterOption.STYLE_HEALING.optionName),  FilterOption.WITH_CHILD.optionName, FilterOption.TRANSPORTATION_TAXI_CAR.optionName, 3, "3", "2024-08-27")
        )
        routeList.add(
            RoutePreview(-1, -1, "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg", "닉네임닉네임", "강릉", "강릉 여행",
                arrayListOf(
                    "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg", "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg",
                    "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg", "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg"
                ), false, 20, 12, arrayListOf(FilterOption.MANY_TWO.optionName, FilterOption.TRANSPORTATION_PUBLIC_TRANSPORTATION.optionName,
                    FilterOption.WITH_CHILD.optionName, FilterOption.STYLE_ETC.optionName, FilterOption.MANY_FOUR.optionName, FilterOption.STYLE_HEALING.optionName),  FilterOption.WITH_CHILD.optionName, FilterOption.TRANSPORTATION_TAXI_CAR.optionName, 3, "3", "2024-08-27")
        )
        routeList.add(
            RoutePreview(-1, -1, "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg", "닉네임닉네임", "강릉", "강릉 여행",
                arrayListOf(
                    "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg", "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg",
                    "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg", "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg"
                ), false, 20, 12, arrayListOf(FilterOption.MANY_TWO.optionName, FilterOption.TRANSPORTATION_PUBLIC_TRANSPORTATION.optionName,
                    FilterOption.WITH_CHILD.optionName, FilterOption.STYLE_ETC.optionName, FilterOption.MANY_FOUR.optionName, FilterOption.STYLE_HEALING.optionName),  FilterOption.WITH_CHILD.optionName, FilterOption.TRANSPORTATION_TAXI_CAR.optionName, 3, "3", "2024-08-27")
        )
        routeList.add(
            RoutePreview(-1, -1, "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg", "닉네임닉네임", "강릉", "강릉 여행",
                arrayListOf(
                    "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg", "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg",
                    "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg", "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg"
                ), false, 20, 12, arrayListOf(FilterOption.MANY_TWO.optionName, FilterOption.TRANSPORTATION_PUBLIC_TRANSPORTATION.optionName,
                    FilterOption.WITH_CHILD.optionName, FilterOption.STYLE_ETC.optionName, FilterOption.MANY_FOUR.optionName, FilterOption.STYLE_HEALING.optionName),  FilterOption.WITH_CHILD.optionName, FilterOption.TRANSPORTATION_TAXI_CAR.optionName, 3, "3", "2024-08-27")
        )
        routeList.add(
            RoutePreview(-1, -1, "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg", "닉네임닉네임", "강릉", "강릉 여행",
                arrayListOf(
                    "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg", "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg",
                    "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg", "https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg"
                ), false, 20, 12, arrayListOf(FilterOption.MANY_TWO.optionName, FilterOption.TRANSPORTATION_PUBLIC_TRANSPORTATION.optionName,
                    FilterOption.WITH_CHILD.optionName, FilterOption.STYLE_ETC.optionName, FilterOption.MANY_FOUR.optionName, FilterOption.STYLE_HEALING.optionName),  FilterOption.WITH_CHILD.optionName, FilterOption.TRANSPORTATION_TAXI_CAR.optionName, 3, "3", "2024-08-27")
        )

        setAdapter()
        initClickListener()
        initScrollLoading()

        return binding.root
    }

    private fun setAdapter() {
        routeAdapter = SeekHomeRouteRVAdapter(routeList)
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
                    Toast.makeText(activity, "신고하기 버튼 클릭", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> { false }
            }
        }
        popupMenu.show()
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