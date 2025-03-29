package com.daval.routebox.domain.model

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.daval.routebox.presentation.ui.route.write.convenience.WeatherType
import com.daval.routebox.presentation.utils.DateConverter
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.PhotoMetadata
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

data class RoutePreviewResult(
    var result: ArrayList<RoutePreview> = arrayListOf()
)

// 루트 미리보기
data class RoutePreview(
    var routeId: Int = -1,
    var userId: Int = -1,
    var profileImageUrl: String = "",
    var nickname: String = "",
    var routeName: String = "",
    var routeDescription: String = "",
    var routeImageUrls: ArrayList<String>? = arrayListOf(),
    var isPurchased: Boolean = false,
    var purchaseCount: Int = -1,
    var commentCount: Int = -1,
    var routeStyles: ArrayList<String> = arrayListOf(),
    var whoWith: String = "",
    var transportation: String = "",
    var numberOfPeople: Int = -1,
    var numberOfDays: String = "",
    var createdAt: String = ""
)

// 루트 구매 후 상세보기
@RequiresApi(Build.VERSION_CODES.O)
data class RouteDetail (
    var routeId: Int = -1,
    var userId: Int = -1,
    var profileImageUrl: String = "",
    var nickname: String = "",
    var routeName: String = "",
    var routeDescription: String = "",
    var startTime: String = "",
    var endTime: String = "",
    var whoWith: String = "",
    var routeStyles: ArrayList<String> = arrayListOf(),
    var numberOfPeople: Int = -1,
    var numberOfDays: String = "",
    var transportation: String = "",
    var createdAt: String = LocalDateTime.now().toString(),
    var routePath: ArrayList<RoutePath> = arrayListOf(),
    var routeActivities: ArrayList<ActivityResult> = arrayListOf(),
    var isPublic: Boolean = false
)

data class RoutePath(
    var latitude: Double,
    var longitude: Double
)

// 인사이트
data class Insight(
    var routeCount: Int,
    var purchaseCount: Int,
    var commentCount: Int
)

// 내 루트
data class MyRouteResponse(
    var result: ArrayList<MyRoute>
)

data class MyRoute(
    var routeId: Int = -1,
    var routeName: String = "",
    var routeDescription: String = "",
    var routeImageUrl: String = "",
    var isPublic: Boolean = false,
    var purchaseCount: Int = 0,
    var commentCount: Int = 0,
    var createdAt: String = ""
)

// 내 루트 공개/비공개
data class RoutePublicRequest(
    var isPublic: Boolean = false
)

data class RoutePublicResult(
    var routeId: Int,
    var isPublic: Boolean
)

// 루트 기록 시작 등록
data class RouteWriteTime(
    var startTime: String,
    var endTime: String
)

data class RouteId(
    var routeId: Int?
)

// 루트 점 기록
data class RoutePoint(
    var points: ArrayList<RoutePointRequest?>? = arrayListOf()
)

data class RoutePointRequest(
    var latitude: String,
    var longitude: String,
    var recordAt: String
)

// 내 루트 수정
data class RouteUpdateRequest(
    var routeName: String?,
    var routeDescription: String?,
    var whoWith: String?,
    var numberOfPeople: Int?,
    var numberOfDays: String?,
    var routeStyles: List<String>?,
    var transportation: String?
)

data class RouteUpdateResult(
    var routeId: Int,
    var routeName: String,
    var routeDescription: String,
    var whoWith: String,
    var numberOfPeople: Int,
    var numberOfDays: String,
    var routeStyles: ArrayList<String>,
    var transportation: String
)

// 루트 마무리하기
data class RouteFinishRequest(
    var routeName: String,
    var routeDescription: String
)

data class RouteFinishResult(
    var routeId: Int,
    var routeName: String,
    var routeDescription: String,
    var recordFinishedAt: String
)

// 활동 추가
data class Activity(
    var locationName: String = "",
    var address: String = "",
    var latitude: String = "",
    var longitude: String = "",
    var visitDate: String = DateConverter.getAPIFormattedDate(LocalDate.now()),
    var startTime: String = "",
    var endTime: String = "",
    var category: String = "", // 음식점, 관광명소 등
    var description: String = "",
    var activityImages: ArrayList<String> = arrayListOf(),
)

data class ActivityResult(
    var activityId: Int = -1,
    var locationName: String = "",
    var address: String = "",
    var latitude: String = "",
    var longitude: String = "",
    var visitDate: String = "",
    var startTime: String = "",
    var endTime: String = "",
    var category: String = "",
    var description: String = "",
    var activityImages: ArrayList<ActivityImage> = arrayListOf()
): Serializable {
    fun convertToActivity(): Activity {
        return Activity(
            locationName, address, latitude, longitude,
            visitDate, startTime, endTime,
            category, description, activityImages.map { it.url } as ArrayList<String>
        )
    }
}

data class ActivityImage(
    var id: Int,
    var url: String
): Serializable

data class ActivityId(
    var activityId: Int
)

// 카카오 장소 검색
data class KakaoSearchResult(
    val meta: PlaceMeta, // 장소 메타데이터
    val documents: List<SearchActivityResult> // 검색 결과
)

data class PlaceMeta(
    val total_count: Int, // 검색어에 검색된 문서 수
    val pageable_count: Int, // total_count 중 노출 가능 문서 수, 최대 45 (API에서 최대 45개 정보만 제공)
    val is_end: Boolean, // 현재 페이지가 마지막 페이지인지 여부, 값이 false면 page를 증가시켜 다음 페이지를 요청할 수 있음
    val same_name: RegionInfo // 질의어의 지역 및 키워드 분석 정보
)

data class RegionInfo(
    val region: List<String>, // 질의어에서 인식된 지역의 리스트, ex) '중앙로 맛집' 에서 중앙로에 해당하는 지역 리스트
    val query: String, // 질의어에서 지역 정보를 제외한 키워드, ex) '중앙로 맛집' 에서 '맛집'
    val selected_region: String // 인식된 지역 리스트 중, 현재 검색에 사용된 지역 정보
)

data class SearchActivityResult(
    val id: String, // 장소 ID
    val placeName: String, // 장소명, 업체명
    val latLng: LatLng,
    val addressName: String
)

data class ActivityPictureAlbum(
    val uri: Uri?,
    var selectedNumber: Int? = null
)

// 편의기능
enum class CategoryGroupCode {
    AD5,
    FD6,
    CE7,
    CT1,
    PK6
}

data class TourApiResult(
    val response: TourApiResponse
)

data class TourApiResponse(
    val header: TourApiHeader,
    val body: TourApiBody
)

data class TourApiHeader(
    val resultCode: String,
    val resultMsg: String
)

data class TourApiBody(
    val items: TourApiItems,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)

data class TourApiItems(
    val item: List<TourApiItem>
)

data class TourApiItem(
    val contentid: String,
    val addr1: String,
    val addr2: String,
    val areacode: String,
    val booktour: String,
    val cat1: String,
    val cat2: String,
    val cat3: String,
    val contenttypeid: String,
    val createdtime: String,
    val dist: String,
    val firstimage: String,
    val firstimage2: String,
    val cpyrhtDivCd: String,
    val mapx: String,
    val mapy: String,
    val mlevel: String,
    val modifiedtime: String,
    val sigungucode: String,
    val tel: String,
    val title: String
)

// 편의기능 결과
data class ConvenienceCategoryResult(
    val placeId: String? = "",
    val placeName: String? = "", // 이름
    val photoMetadataList: List<PhotoMetadata>? = arrayListOf(), // 사진 메타데이터 리스트
    val rating: Double? = 0.0, // 평점
    val latitude: LatLng? = LatLng(0.0, 0.0),
    val isOpen: Boolean? = false,
): Serializable

data class WeatherApiResult(
    val response: WeatherApiResponse
)

data class WeatherApiResponse(
    val header: WeatherApiHeader,
    val body: WeatherApiBody
)

data class WeatherApiHeader(
    val resultCode: String,
    val resultMsg: String
)

data class WeatherApiBody(
    val dataType: String,
    val items: WeatherApiItems
)

data class WeatherApiItems(
    val item: List<WeatherApiItem>
)

data class WeatherApiItem(
    val baseData: String,
    val baseTime: String,
    val category: String,
    val fcstDate : String,
    val fcstTime : String,
    val fcstValue : String,
    val nx : Int,
    val ny : Int
)

data class WeatherData(
    val tmp: String,
    val sky: String,
    val pty: String,
    val fcstDate: String?,
    val fcstTime: String?,
    val weatherType: WeatherType
)

data class WeatherRegionResponse(
    val meta: WeatherRegionMeta,
    val documents: List<WeatherRegionResult>
)

data class WeatherRegionMeta(
    val total_count: Int
)

data class WeatherRegionDocuments(
    val list: List<WeatherRegionResult>
)

data class WeatherRegionResult(
    val region_type: String,
    val address_name: String,
    val region_1depth_name: String,
    val region_2depth_name: String,
    val region_3depth_name: String,
    val region_4depth_name: String,
    val code: String,
    val x: Double,
    val y: Double
)

// TODO: Enum으로 변경
data class BuyRouteRequest(
    val paymentMethod: String
)

const val pictureImgType = 0
const val pictureAddType = 1