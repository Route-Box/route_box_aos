package com.example.routebox.presentation.ui.route.write

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routebox.domain.model.SearchActivityResult
import com.example.routebox.domain.repositories.RouteRepository
import com.example.routebox.presentation.ui.route.write.RouteCreateActivity.Companion.TODAY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale.Category
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class RouteWriteViewModel @Inject constructor(
    private val repository: RouteRepository
): ViewModel() {
    private val _placeSearchKeyword = MutableLiveData<String>()
    val placeSearchKeyword: LiveData<String> = _placeSearchKeyword

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

    init {
        _placeSearchKeyword.value = ""
        _placeSearchResult.value = arrayListOf()
        _placeSearchPage.value = 1
        _isEndPage.value = true
    }

    fun updateDate(date: LocalDate) {
        _placeSearchKeyword.value = ""
        _date.value = date
    }

    fun updateTime(isStartTime: Boolean, timePair: Pair<Int, Int>) {
        if (isStartTime) _startTimePair.value = timePair
        else _endTimePair.value = timePair
    }

    fun setPlaceSearchKeyword(query: String) {
        _placeSearchKeyword.value = query
    }

    fun setCategoryETC(category: String?) {
        _categoryETC.value = category
    }

    fun searchPlace() {
        viewModelScope.launch {
            _placeSearchPage.value = 1

            val response = repository.searchKakaoPlace(_placeSearchKeyword.value.toString(), _placeSearchPage.value!!)
            _placeSearchResult.value = response.documents as ArrayList
            _isEndPage.value = response.meta.is_end
        }
    }

    fun pagingPlace() {
        viewModelScope.launch {
            val response = repository.searchKakaoPlace(_placeSearchKeyword.value.toString(), _placeSearchPage.value!!)
            _placeSearchResult.value = response.documents as ArrayList
            _isEndPage.value = response.meta.is_end
        }
    }
}