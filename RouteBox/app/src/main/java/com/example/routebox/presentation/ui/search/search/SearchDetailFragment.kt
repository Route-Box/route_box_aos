package com.example.routebox.presentation.ui.search.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routebox.databinding.FragmentSearchDetailBinding
import com.example.routebox.presentation.utils.SharedPreferencesHelper
import com.example.routebox.presentation.utils.SharedPreferencesHelper.Companion.APP_PREF_KEY
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager

class SearchDetailFragment: Fragment() {
    private lateinit var binding: FragmentSearchDetailBinding

    private lateinit var searchWordAdapter: RecentSearchWordRVAdapter
    private lateinit var searchResultAdapter: SearchResultRVAdapter

    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchDetailBinding.inflate(inflater, container, false)

        binding.apply {
            viewModel = this@SearchDetailFragment.viewModel
            lifecycleOwner = this@SearchDetailFragment
        }

        initObserve()
        initRecentSearchWords()
        initClickListeners()
        setSearchResultAdapter()
        return binding.root
    }

    private fun initClickListeners() {
        // 필터 버튼 클릭
        binding.searchDetailFilterIv.setOnClickListener {
            //필터 화면으로 이동
            startActivity(Intent(requireActivity(), FilterActivity::class.java))
        }

        // 최근 검색어 모두 지우기
        binding.searchDetailClearAllRecentSearchwordTv.setOnClickListener {
            searchWordAdapter.deleteAllWords()
            viewModel.clearAllRecentSearchWords()
        }
    }

    private fun initRecentSearchWords() {
        val sharedPreferencesHelper = SharedPreferencesHelper(requireActivity().getSharedPreferences(APP_PREF_KEY, Context.MODE_PRIVATE))
        Log.d("SearchDetailFrag", "최근 검색어 가져오기: ${sharedPreferencesHelper.getRecentSearchWords()}")
        viewModel.setRecentSearchWordSet(sharedPreferencesHelper.getRecentSearchWords())
    }

    private fun saveRecentSearchWords() {
        // sharedPreferences에 최근 검색어 저장
        val sharedPreferencesHelper = SharedPreferencesHelper(requireActivity().getSharedPreferences(APP_PREF_KEY, Context.MODE_PRIVATE))
        sharedPreferencesHelper.setRecentSearchWords(viewModel.resentSearchWordSet.value)
    }

    private fun setSearchWordAdapter() {
        searchWordAdapter = RecentSearchWordRVAdapter()
        binding.searchDetailRecentSearchwordRv.apply {
            adapter = searchWordAdapter
            layoutManager = FlexboxLayoutManager(context).apply {
                flexWrap = FlexWrap.WRAP
                flexDirection = FlexDirection.ROW
            }
        }
        searchWordAdapter.setRecentSearchWordClickListener(object : RecentSearchWordRVAdapter.MyItemClickListener {
            override fun onItemClick(position: Int, word: String) {
                // 검색어로 다시 검색
                viewModel.setCurrentSearchWord(word)
                // 해당 검색어를 가장 최근 검색어로 이동
                viewModel.updateRecentSearchWord(word, SearchType.REBROWSING)
            }

            override fun onDeleteWord(position: Int, word: String) {
                // 검색어를 set에서 삭제
                viewModel.updateRecentSearchWord(word, SearchType.DELETE)
            }
        })
    }

    private fun setSearchResultAdapter() {
        searchResultAdapter = SearchResultRVAdapter()
        binding.searchDetailResultRv.apply {
            adapter = searchResultAdapter
            layoutManager = LinearLayoutManager(context)
        }
        searchResultAdapter.addRoute(arrayListOf("1", "2", "3", "4", "5")) //TODO: 서버의 루트 데이터로 변경
        searchResultAdapter.setRouteClickListener(object: SearchResultRVAdapter.MyItemClickListener {
            override fun onItemClick(position: Int) {
                //TODO: 피드 상세 화면으로 이동
            }
        })
    }

    private fun initObserve() {
        // 최근 검색어 관측
        viewModel.resentSearchWordSet.observe(viewLifecycleOwner) { set ->
            Log.d("SearchDetailFrag", "최근 검색어: $set")
            if (!set.isNullOrEmpty()) {
                setSearchWordAdapter()
                // 어댑터에 최근 검색어 추가
                searchWordAdapter.addSearchWord(set.toList())
            }
            saveRecentSearchWords()
        }
    }
}