package com.example.routebox.presentation.ui.search.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routebox.databinding.FragmentSearchDetailBinding

class SearchDetailFragment: Fragment() {
    private lateinit var binding: FragmentSearchDetailBinding

    private lateinit var routeAdapter: SearchResultRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchDetailBinding.inflate(inflater, container, false)

        initClickListeners()
        setAdapter()
        return binding.root
    }

    private fun initClickListeners() {
        binding.searchDetailSearchIv.setOnClickListener {
            Toast.makeText(requireContext(), "검색 버튼 클릭", Toast.LENGTH_SHORT).show()
        }

        binding.searchDetailFilterIv.setOnClickListener {
            //TODO: 필터 화면으로 이동
        }
    }

    private fun setAdapter() {
        routeAdapter = SearchResultRVAdapter()
        binding.searchDetailResultRv.apply {
            adapter = routeAdapter
            layoutManager = LinearLayoutManager(context)
        }
        routeAdapter.addRoute(arrayListOf("1", "2", "3", "4", "5")) //TODO: 서버의 루트 데이터로 변경
        routeAdapter.setRouteClickListener(object: SearchResultRVAdapter.MyItemClickListener {
            override fun onMoreButtonClick(position: Int) {
                //TODO: 클릭 동작 정의
            }
        })
    }
}