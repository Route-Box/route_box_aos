package com.example.routebox.presentation.ui.seek.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routebox.R
import com.example.routebox.databinding.FragmentSearchBinding
import com.example.routebox.presentation.ui.RoutePreviewDetailActivity
import com.example.routebox.presentation.ui.seek.search.adapter.RecentSearchWordRVAdapter
import com.example.routebox.presentation.ui.seek.search.adapter.SearchResultRVAdapter
import com.example.routebox.presentation.utils.SharedPreferencesHelper
import com.example.routebox.presentation.utils.SharedPreferencesHelper.Companion.APP_PREF_KEY
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager

class SearchFragment: Fragment() {
    private lateinit var binding: FragmentSearchBinding

    private lateinit var searchWordAdapter: RecentSearchWordRVAdapter
    private lateinit var searchResultAdapter: SearchResultRVAdapter

    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        binding.apply {
            viewModel = this@SearchFragment.viewModel
            lifecycleOwner = this@SearchFragment
        }

        initObserve()
        initRecentSearchWords()
        initClickListeners()
        setSearchResultAdapter()
        return binding.root
    }

    private fun initClickListeners() {
        binding.searchBackIv.setOnClickListener {
            findNavController().popBackStack()
        }

        // 정렬 기준
        binding.searchOrderOptionTv.setOnClickListener {
            showOrderingSearchResultMenu(it)
        }

        // 필터 버튼 클릭
        binding.searchFilterIv.setOnClickListener {
            //필터 화면으로 이동
            startActivity(Intent(requireActivity(), FilterActivity::class.java))
        }

        // 최근 검색어 모두 지우기
        binding.searchClearAllRecentSearchwordTv.setOnClickListener {
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
        binding.searchRecentSearchwordRv.apply {
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
        binding.searchResultRv.apply {
            adapter = searchResultAdapter
            layoutManager = LinearLayoutManager(context)
        }
        searchResultAdapter.addRoute(arrayListOf("1", "2", "3", "4", "5")) //TODO: 서버의 루트 데이터로 변경
        searchResultAdapter.setRouteClickListener(object: SearchResultRVAdapter.MyItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(activity, RoutePreviewDetailActivity::class.java)
                startActivity(intent)
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

    private fun showOrderingSearchResultMenu(view: View) {
        val popupMenu = PopupMenu(requireActivity(), view)
        popupMenu.inflate(R.menu.search_order_menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_order_recent -> { // 최신 순
                    viewModel.updateSelectedOrderOptionMenuId(0)
                    true
                }
                R.id.menu_order_old -> { // 오래된 순
                    viewModel.updateSelectedOrderOptionMenuId(1)
                    true
                }
                R.id.menu_order_popularity -> { // 인기 순
                    viewModel.updateSelectedOrderOptionMenuId(2)
                    true
                }
                R.id.menu_order_many_comment -> { // 댓글 많은 순
                    viewModel.updateSelectedOrderOptionMenuId(3)
                    true
                }
                else -> { false }
            }
        }
        popupMenu.show()
    }
}