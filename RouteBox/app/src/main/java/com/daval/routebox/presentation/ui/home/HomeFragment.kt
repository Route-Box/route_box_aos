package com.daval.routebox.presentation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.daval.routebox.R
import com.daval.routebox.databinding.FragmentHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    // MEMO: 웹뷰에서 다른 프래그먼트로 이동을 위해 썼던 코드 / UI 제작 후 사용 안할 시 제거
    private fun selectBottomNavTab(tabId: Int) {
        val bottomNavView = requireActivity().findViewById<BottomNavigationView>(R.id.main_bottom_nav)
        bottomNavView.post {
            bottomNavView.menu.performIdentifierAction(tabId, 0)
        }
    }
}