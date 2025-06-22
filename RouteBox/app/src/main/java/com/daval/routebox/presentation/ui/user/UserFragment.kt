package com.daval.routebox.presentation.ui.user

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.daval.routebox.databinding.FragmentUserBinding
import com.daval.routebox.domain.model.UserRoute
import com.daval.routebox.presentation.ui.auth.LoginActivity
import com.daval.routebox.presentation.ui.home.UserRouteRVAdapter
import com.daval.routebox.presentation.ui.route.RouteDetailActivity
import com.daval.routebox.presentation.utils.RecyclerViewHorizontalDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserFragment : Fragment() {
    private lateinit var binding: FragmentUserBinding

    private lateinit var userRouteRVAdapter: UserRouteRVAdapter
    // TODO: API 연동 후 더미데이터 삭제
    private var userRouteList = arrayListOf<UserRoute>(
        UserRoute(1, "route1", "route설명1", "https://imagescdn.gettyimagesbank.com/500/201907/jv11447791.jpg", 1, 1, "2020-01-01"),
        UserRoute(2, "route2", "route설명2", "https://imagescdn.gettyimagesbank.com/500/201907/jv11447309.jpg", 1, 1, "2020-01-01"),
        UserRoute(3, "route3", "route설명3", "https://imagescdn.gettyimagesbank.com/500/201907/jv11447791.jpg", 1, 1, "2020-01-01"),
        UserRoute(4, "route4", "route설명4", "https://imagescdn.gettyimagesbank.com/500/201907/jv11447309.jpg", 1, 1, "2020-01-01"),
        UserRoute(5, "route5", "route설명5", "https://imagescdn.gettyimagesbank.com/500/201907/jv11447309.jpg", 1, 1, "2020-01-01"),
        UserRoute(6, "route6", "route설명6", "https://imagescdn.gettyimagesbank.com/500/201907/jv11447791.jpg", 1, 1, "2020-01-01"),
        UserRoute(7, "route7", "route설명7", "https://imagescdn.gettyimagesbank.com/500/201907/jv11447309.jpg", 1, 1, "2020-01-01"),
    )
    private val viewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        binding = FragmentUserBinding.inflate(inflater, container, false)

        binding.apply {
            viewModel = this@UserFragment.viewModel
            lifecycleOwner = this@UserFragment
        }

        setAdapter()
        initClickListener()

        return binding.root
    }

    private fun setAdapter() {
        userRouteRVAdapter = UserRouteRVAdapter(userRouteList)
        binding.userRouteRv.apply {
            adapter = userRouteRVAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(RecyclerViewHorizontalDecoration("bottom", 24))
        }
        userRouteRVAdapter.setRouteClickListener(object: UserRouteRVAdapter.RouteItemClickListener {
            override fun onItemClick(routeId: Int) {
                // 루트 보기 화면으로 이동
                startActivity(Intent(requireActivity(), RouteDetailActivity::class.java).putExtra("routeId", routeId))
            }
            override fun onOptionClick(routeId: Int) {

            }
        })
    }

    private fun initClickListener() {
        binding.pencilIv.setOnClickListener {
            startActivity(Intent(requireActivity(), IntroductionActivity::class.java))
        }

        binding.menuIv.setOnClickListener {
            startActivity(Intent(requireActivity(), SettingActivity::class.java))
        }
    }

    // MEMO: 웹뷰에서 로그인 액티비티로 이동을 위해 썼던 코드 / UI 제작 후 사용 안할 시 제거
    private fun moveToLoginActivity() {
        requireActivity().startActivity(Intent(requireActivity(), LoginActivity::class.java))
        requireActivity().finish()
    }
}