package com.daval.routebox.presentation.ui.route.write.convenience

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
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
import com.daval.routebox.presentation.ui.route.write.convenience.RouteConvenienceFragment.Companion.SEARCH_RADIUS
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.IsOpenRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
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

    private val _selectedConvenience = MutableLiveData<Convenience?>(null)
    val selectedConvenience: LiveData<Convenience?> = _selectedConvenience


    init {
        _placeCategoryResult.value = arrayListOf()
    }

    fun setWeatherMainData(weatherData: WeatherData) {
        _weatherMainData.value = weatherData
    }

    fun setPlaceCategoryResult(placeList: ArrayList<ConvenienceCategoryResult>) {
        _placeCategoryResult.value = placeList
    }

    fun getNearbySearchPlaceResult(placesClient: PlacesClient, currentLocation: LatLng) {
        val placeList: ArrayList<ConvenienceCategoryResult> = arrayListOf() // 임시 저장

        // 응답에 포함할 필드 설정
        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.DISPLAY_NAME,
            Place.Field.LOCATION,
            Place.Field.RATING,
            Place.Field.OPENING_HOURS,
            Place.Field.CURRENT_OPENING_HOURS,
            Place.Field.PHOTO_METADATAS
        )

        val circle = CircularBounds.newInstance(currentLocation, SEARCH_RADIUS) // 검색 기준, 범위 설정

        val request = SearchNearbyRequest.builder(circle, placeFields)
            .setIncludedTypes(_selectedConvenience.value!!.typeList)
            .setMaxResultCount(20)
            .build()

        viewModelScope.launch {
            try {
                val response = placesClient.searchNearby(request).await()

                val deferredResults = response.places.map { place ->
                    async {
                        placeList.add(getPlaceWithIsOpen(placesClient, place)) // 비동기적으로 장소 추가
                    }
                }
                deferredResults.awaitAll() // 모든 비동기 작업 완료 대기
            } catch (e: Exception) {
                Log.e("RouteConvenienceVM", "검색 실패: ${e.message}")
            } finally {
                setPlaceCategoryResult(placeList) // UI 업데이트
            }
        }
    }

    private suspend fun getPlaceWithIsOpen(placesClient: PlacesClient, place: Place): ConvenienceCategoryResult {
        place.id?.let { placeId ->
            val isOpen = getIsOpenStatus(placesClient, placeId) // 가게 영업 여부 확인

            val newPlace = ConvenienceCategoryResult(
                placeId = place.id,
                placeName = place.displayName,
                photoMetadataList = place.photoMetadatas,
                rating = place.rating,
                latitude = place.location,
                isOpen = isOpen
            )

            return newPlace
        }
        return ConvenienceCategoryResult()
    }

    // 가게 영업 여부 확인
    private suspend fun getIsOpenStatus(placesClient: PlacesClient, placeId: String): Boolean? {
        val isOpenCalendar: Calendar = Calendar.getInstance()
        val request = IsOpenRequest.newInstance(placeId, isOpenCalendar.timeInMillis)

        return try {
            placesClient.isOpen(request).await().isOpen
        } catch (e: Exception) {
            Log.e("RouteConvenienceVM", "isOpen 확인 실패: ${e.message}")
            null
        }
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

    fun selectConvenienceChip(selectedConvenience: Convenience?) {
        _selectedConvenience.value = selectedConvenience
    }
}