package com.daval.routebox.presentation.ui.common.routePreview

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daval.routebox.domain.model.RoutePreview
import com.daval.routebox.domain.repositories.RouteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutePreviewViewModel @Inject constructor(
    val repository: RouteRepository
): ViewModel() {

    private val _routePreviewDetail = MutableLiveData<RoutePreview>()
    val routePreviewDetail: LiveData<RoutePreview> = _routePreviewDetail

    var routeId: Int = 0


    /** 루트 조회 */
    fun getRoutePreviewData() {
        Log.d("RoutePreviewVM", "routeId: $routeId")
        viewModelScope.launch {
            _routePreviewDetail.value = repository.getRouteDetailPreview(routeId)
        }
    }

    fun getImageUrlList() = _routePreviewDetail.value!!.routeImageUrls

    fun getTagList() = _routePreviewDetail.value!!.routeStyles
}