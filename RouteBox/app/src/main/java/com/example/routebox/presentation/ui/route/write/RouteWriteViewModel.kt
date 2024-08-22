package com.example.routebox.presentation.ui.route.write

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routebox.domain.model.Category
import com.example.routebox.domain.model.SearchActivityResult
import com.example.routebox.domain.repositories.RouteRepository
import com.example.routebox.presentation.ui.route.write.RouteCreateActivity.Companion.TODAY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class RouteWriteViewModel @Inject constructor(
    private val repository: RouteRepository
): ViewModel() {
    private val _placeSearchKeyword = MutableLiveData<String>()
    val placeSearchKeyword: LiveData<String> = _placeSearchKeyword

    private val _placeSearchMode = MutableLiveData<Boolean>()
    val placeSearchMode: LiveData<Boolean> = _placeSearchMode

    private val _placeName = MutableLiveData<String?>()
    val placeName: LiveData<String?> = _placeName

    private val _placeSearchResult = MutableLiveData<ArrayList<SearchActivityResult>>()
    val placeSearchResult: MutableLiveData<ArrayList<SearchActivityResult>> = _placeSearchResult

    private val _placeSearchPage = MutableLiveData<Int>()
    val placeSearchPage: MutableLiveData<Int> = _placeSearchPage

    private val _isEndPage = MutableLiveData<Boolean>()
    val isEndPage: MutableLiveData<Boolean> = _isEndPage

    private val _date = MutableLiveData<LocalDate>(TODAY)
    val date: LiveData<LocalDate> = _date

    private val _startTimePair = MutableLiveData<Pair<Int, Int>>()
    val startTimePair: LiveData<Pair<Int, Int>> = _startTimePair

    private val _endTimePair = MutableLiveData<Pair<Int, Int>>()
    val endTimePair: LiveData<Pair<Int, Int>> = _endTimePair

    private val _category = MutableLiveData<Category>()
    val category: LiveData<Category> = _category

    private val _categoryETC = MutableLiveData<String?>()
    val categoryETC: LiveData<String?> = _categoryETC

    private val _placeImage = MutableLiveData<ArrayList<String>>()
    val placeImage: LiveData<ArrayList<String>> = _placeImage

    private val _locationContent = MutableLiveData<String>()
    val locationContent: LiveData<String> = _locationContent

    private val _btnEnabled = MutableLiveData<Boolean>()
    val btnEnabled: LiveData<Boolean> = _btnEnabled

    init {
        _placeSearchKeyword.value = ""
        _placeSearchResult.value = arrayListOf()
        _placeSearchPage.value = 1
        _categoryETC.value = null
        _isEndPage.value = true
        _locationContent.value = ""
    }

    fun updateDate(date: LocalDate) {
        _placeSearchKeyword.value = ""
        _date.value = date
        checkBtnEnabled()
    }

    fun updateTime(isStartTime: Boolean, timePair: Pair<Int, Int>) {
        if (isStartTime) _startTimePair.value = timePair
        else _endTimePair.value = timePair
        checkBtnEnabled()
    }

    fun setPlaceSearchMode(mode: Boolean) {
        _placeSearchMode.value = mode
    }

    fun setPlaceName(placeName: String) {
        _placeName.value = placeName
        checkBtnEnabled()
    }

    fun setPlaceSearchKeyword(query: String) {
        _placeSearchKeyword.value = query
        checkBtnEnabled()
    }

    fun setCategory(category: Category) {
        _category.value = category
        checkBtnEnabled()
    }

    fun setCategoryETC(categoryETC: String?) {
        _categoryETC.value = categoryETC
        checkBtnEnabled()
    }

    fun setLocationContent(locationContent: String) {
        _locationContent.value = locationContent
    }

    fun searchPlace() {
        viewModelScope.launch {
            _placeSearchPage.value = 1

            val response = repository.searchKakaoPlace(_placeSearchKeyword.value.toString(), _placeSearchPage.value!!)
            _placeSearchResult.value = response.documents as ArrayList
            _isEndPage.value = response.meta.is_end
            _placeSearchMode.value = true
        }
    }

    fun pagingPlace() {
        viewModelScope.launch {
            val response = repository.searchKakaoPlace(_placeSearchKeyword.value.toString(), _placeSearchPage.value!!)
            _placeSearchResult.value = response.documents as ArrayList
            _isEndPage.value = response.meta.is_end
        }
    }

    private fun checkBtnEnabled() {
        // 카테고리가 기타가 선택됐을 때와 아닐 때로 구분
        if (_category.value == Category.ETC) {
            _btnEnabled.value = (_placeName.value != "" && _date.value != null && _startTimePair.value != null
                    && _endTimePair.value != null && _categoryETC.value!!.isNotEmpty())
        } else {
            _btnEnabled.value = (_placeName.value != "" && _date.value != null && _startTimePair.value != null
                    && _endTimePair.value != null && _category.value != null)
        }
    }
}