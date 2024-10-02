package com.daval.routebox.presentation.ui.seek.search

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.daval.routebox.R
import com.daval.routebox.databinding.FragmentSearchBinding
import com.daval.routebox.domain.model.SearchRoute
import com.daval.routebox.presentation.ui.common.routePreview.RoutePreviewDetailActivity
import com.daval.routebox.presentation.ui.seek.search.adapter.RecentSearchWordRVAdapter
import com.daval.routebox.presentation.ui.seek.search.adapter.SearchResultRVAdapter
import com.daval.routebox.presentation.utils.SharedPreferencesHelper
import com.daval.routebox.presentation.utils.SharedPreferencesHelper.Companion.APP_PREF_KEY
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment: Fragment() {
    private lateinit var binding: FragmentSearchBinding

    private lateinit var searchWordAdapter: RecentSearchWordRVAdapter
    private lateinit var searchResultAdapter: SearchResultRVAdapter

    private val viewModel: SearchViewModel by viewModels()

    private val getResultText = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val returnTag = result.data?.getStringArrayListExtra(TAG_KEY)
            Log.e("SearchFrag", "return tagList: $returnTag")
            if (returnTag == null) return@registerForActivityResult
            viewModel.updateSelectedTagList(returnTag.toList())
            // 필터링 적용하고 돌아오면 다시 검색 진행
            searchRoute()
        }
    }

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
        // 안드로이드 기본 뒤로가기
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        // 뒤로가기
        binding.searchBackIv.setOnClickListener {
            findNavController().popBackStack()
        }

        // 키보드 검색 버튼
        binding.searchEt.setOnEditorActionListener { it, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) { // 검색 버튼 클릭
                searchRoute() // 루트 검색 진행
                it.clearFocus() // EditText 포커스 해제
                true
            } else {
                false
            }
        }

        // 검색 아이콘
        binding.searchSearchIv.setOnClickListener {
            searchRoute() // 루트 검색 진행
        }

        // 정렬 기준
        binding.searchOrderOptionTv.setOnClickListener {
            showOrderingSearchResultMenu(it)
        }

        // 필터 버튼 클릭
        binding.searchFilterIv.setOnClickListener {
            // 필터 화면으로 이동
            val intent = Intent(requireActivity(), FilterActivity::class.java)
            intent.apply {
                putExtra("searchResultNum", viewModel.searchResultRoutes.value!!.size) // 검색 결과 개수
                putExtra("searchWord", viewModel.searchWord.value) // 입력한 검색어
                viewModel.selectedFilterTagList.value?.let { tagList ->
                    val tagArrayList = if (tagList is ArrayList) {
                        tagList
                    } else {
                        ArrayList(tagList) // List를 ArrayList로 변환
                    }
                    Log.e("SearchFrag", "send tagList: ${viewModel.selectedFilterTagList.value}")
                    putExtra("tagList", tagArrayList) // 적용된 필터 (태그 리스트)
                }
            }
            getResultText.launch(intent) // 돌아오면서 선택한 태그 리스트 받기
        }

        // 최근 검색어 모두 지우기
        binding.searchClearAllRecentSearchwordTv.setOnClickListener {
            searchWordAdapter.deleteAllWords()
            viewModel.clearAllRecentSearchWords()
        }
    }

    // 저장소로부터 최근 검색어 불러오기
    private fun initRecentSearchWords() {
        val sharedPreferencesHelper = SharedPreferencesHelper(requireActivity().getSharedPreferences(APP_PREF_KEY, Context.MODE_PRIVATE))
        Log.d("SearchDetailFrag", "최근 검색어 가져오기: ${sharedPreferencesHelper.getRecentSearchWords()}")
        viewModel.setRecentSearchWordSet(sharedPreferencesHelper.getRecentSearchWords())
    }

    // 저장소에 변경된 최근 검색어 저장하기
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
                viewModel.setCurrentSearchWord(word) // 검색 제목 설정
                searchRoute() // 검색 API 호출

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
        searchResultAdapter.setRouteClickListener(object: SearchResultRVAdapter.MyItemClickListener {
            override fun onItemClick(position: Int) {
                // 루트 상세보기 화면으로 이동
                startActivity(Intent(activity, RoutePreviewDetailActivity::class.java)
                    .putExtra("routeId", viewModel.searchResultRoutes.value!![position].routeId)
                )
            }
        })
    }

    private fun searchRoute() {
        if (viewModel.searchWord.value.isNullOrBlank()) { // 검색 결과가 없을 경우 리턴
            Toast.makeText(requireActivity(), "검색어를 입력해 주세요", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.inputRouteSearchWord() // 루트 검색 진행
        hideKeyboard() // 키보드 내리기
    }

    private fun initObserve() {
        // 검색 결과 관측
        viewModel.searchResultRoutes.observe(viewLifecycleOwner) { searchList ->
            searchResultAdapter.addRoute(searchList as ArrayList<SearchRoute>)
        }

        // 최근 검색어 관측
        viewModel.resentSearchWordSet.observe(viewLifecycleOwner) { set ->
            Log.d("SearchDetailFrag", "최근 검색어 관측: $set")
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
                    viewModel.updateSelectedOrderOption(OrderOptionType.ORDER_RECENT)
                    searchRoute()
                    true
                }
                R.id.menu_order_old -> { // 오래된 순
                    viewModel.updateSelectedOrderOption(OrderOptionType.ORDER_OLD)
                    searchRoute()
                    true
                }
                R.id.menu_order_popularity -> { // 인기 순
                    viewModel.updateSelectedOrderOption(OrderOptionType.ORDER_POPULARITY)
                    searchRoute()
                    true
                }
                R.id.menu_order_many_comment -> { // 댓글 많은 순
                    viewModel.updateSelectedOrderOption(OrderOptionType.ORDER_COMMENT)
                    searchRoute()
                    true
                }
                else -> { false }
            }
        }
        popupMenu.show()
    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEt.windowToken, 0)
    }

    companion object { const val TAG_KEY = "tag_key" }
}