package com.daval.routebox.presentation.ui.route.write.convenience

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daval.routebox.domain.model.CategoryGroupCode
import com.daval.routebox.domain.model.ConvenienceCategoryResult
import com.daval.routebox.domain.model.WeatherData
import com.daval.routebox.domain.repositories.OpenApiRepository
import com.daval.routebox.domain.repositories.RouteRepository
import com.daval.routebox.presentation.config.Constants.OPEN_API_BASE_URL
import com.daval.routebox.presentation.ui.route.write.MapCameraRadius
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class RouteConvenienceViewModel @Inject constructor(
    private val repository: RouteRepository,
    private val openApiRepository: OpenApiRepository
): ViewModel() {
    private val _cameraPosition = MutableLiveData<LatLng>()
    val cameraPosition: LiveData<LatLng> = _cameraPosition

    private val _placeCategoryResult = MutableLiveData<ArrayList<ConvenienceCategoryResult>>()
    val placeCategoryResult: LiveData<ArrayList<ConvenienceCategoryResult>> = _placeCategoryResult

    private val _placeCategory = MutableLiveData<CategoryGroupCode>()
    val placeCategory: LiveData<CategoryGroupCode> = _placeCategory

    private val _placeCategoryPage = MutableLiveData<Int>()
    val placeCategoryPage: LiveData<Int> = _placeCategoryPage

    private val _isCategoryEndPage = MutableLiveData<Boolean>()
    val isCategoryEndPage: LiveData<Boolean> = _isCategoryEndPage

    private val _weatherRegion = MutableLiveData<String>()
    val weatherRegion: LiveData<String> = _weatherRegion

    private val _weatherDepth3Region = MutableLiveData<String>()
    val weatherDepth3Region: LiveData<String> = _weatherDepth3Region

    private val _weatherMainData = MutableLiveData<WeatherData>()
    val weatherMainData: LiveData<WeatherData> = _weatherMainData


    init {
        _isCategoryEndPage.value = false
        _placeCategoryPage.value = 1
        _placeCategoryResult.value = arrayListOf()
    }

    fun setWeatherMainData(weatherData: WeatherData) {
        _weatherMainData.value = weatherData
    }

    fun setKakaoCategory(category: CategoryGroupCode) {
        _placeCategory.value = category
        _placeCategoryPage.value = 1
        _placeCategoryResult.value = arrayListOf()
        _isCategoryEndPage.value = false

        searchCategory()
    }

//    fun setTourCategory() {
//        getTourList()
//    }

    fun setCameraPosition(position: LatLng) {
        _cameraPosition.value = position
    }

    fun setPlaceCategoryResult(placeList: ArrayList<ConvenienceCategoryResult>) {
        _placeCategoryResult.value = placeList
    }

    // 카테고리 검색
    private fun searchCategory() {
        viewModelScope.launch {
            while (true) {
                if (cameraPosition.value != null && placeCategory.value != null) {
                    val response = repository.searchKakaoCategory(_placeCategory.value!!, cameraPosition.value!!.latitude.toString(), cameraPosition.value!!.longitude.toString(), placeCategoryPage.value!!, MapCameraRadius)
                    var result = response.documents.map {
//                        ConvenienceCategoryResult(it.place_name, null, it.y, it.x)
                    }
//                    _placeCategoryResult.value!!.addAll(result)
                    _isCategoryEndPage.value = response.meta.is_end
                    _placeCategoryPage.value = _placeCategoryPage.value!! + 1

                    if (_isCategoryEndPage.value == true) break
                }
            }
        }
    }

    // 장소 검색 페이징 처리
//    fun pagingSearchCategory() {
//        viewModelScope.launch {
//            val response = repository.searchKakaoCategory(
//                _placeCategory.value!!, cameraPosition.value!!.latitude.toString(), cameraPosition.value!!.longitude.toString()
//            )
//            _placeCategoryResult.value = response.documents as ArrayList
//            _isCategoryEndPage.value = response.meta.is_end
//        }
//    }

    // TODO: 나중에 아래 방식으로 수정
//    private fun getTourList() {
//        viewModelScope.launch {
//            val response = tourRepository.getTourList(
//                "AND", "Route Box", BuildConfig.OPEN_API_SERVICE_KEY,
//                mapX = cameraPosition.value!!.longitude.toString(), mapY = cameraPosition.value!!.latitude.toString(),
//                MapCameraRadius.toString(), "12", "json"
//            )
//            Log.d("ROUTE-TEST", "response = $response")
//        }
//    }

    @SuppressLint("DefaultLocale")
    fun getWeatherList() {
        viewModelScope.launch {
            val response = openApiRepository.getWeatherList(
                OPEN_API_BASE_URL, 1, 50, "JSON",
                "20240922", "0500",
                55,
                127
            )
        }
    }

    fun getRegionCode(latitude: String, longitude: String) {
        viewModelScope.launch {
            val response = repository.getKakaoRegionCode(
                latitude, longitude
            )
            _weatherRegion.value = "${response.documents[0].region_1depth_name} ${response.documents[0].region_2depth_name} ${response.documents[0].region_3depth_name}"
            _weatherDepth3Region.value = response.documents[0].region_3depth_name
        }
    }
}