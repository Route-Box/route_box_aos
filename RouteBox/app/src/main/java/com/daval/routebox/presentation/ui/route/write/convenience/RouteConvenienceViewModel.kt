package com.daval.routebox.presentation.ui.route.write.convenience

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daval.routebox.domain.model.Convenience
import com.daval.routebox.domain.model.ConvenienceCategoryResult
import com.daval.routebox.domain.model.WeatherData
import com.daval.routebox.domain.repositories.OpenApiRepository
import com.daval.routebox.domain.repositories.RouteRepository
import com.daval.routebox.presentation.config.Constants.OPEN_API_BASE_URL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class RouteConvenienceViewModel @Inject constructor(
    private val repository: RouteRepository,
    private val openApiRepository: OpenApiRepository
): ViewModel() {
    private val _placeCategoryResult = MutableLiveData<ArrayList<ConvenienceCategoryResult>>(arrayListOf())
    val placeCategoryResult: LiveData<ArrayList<ConvenienceCategoryResult>> = _placeCategoryResult

    private val _weatherRegion = MutableLiveData<String>()
    val weatherRegion: LiveData<String> = _weatherRegion

    private val _weatherDepth3Region = MutableLiveData<String>()
    val weatherDepth3Region: LiveData<String> = _weatherDepth3Region

    private val _weatherMainData = MutableLiveData<WeatherData>()
    val weatherMainData: LiveData<WeatherData> = _weatherMainData

    var selectedConvenience: Convenience? = null


    init {
        _placeCategoryResult.value = arrayListOf()
    }

    fun setWeatherMainData(weatherData: WeatherData) {
        _weatherMainData.value = weatherData
    }

    fun setPlaceCategoryResult(placeList: ArrayList<ConvenienceCategoryResult>) {
        _placeCategoryResult.value = placeList
    }

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