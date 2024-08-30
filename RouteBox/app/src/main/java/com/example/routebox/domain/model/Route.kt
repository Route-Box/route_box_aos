package com.example.routebox.domain.model

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.bumptech.glide.load.model.StringLoader
import java.io.File
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
    var routeId: Int
)

// 루트 점 기록
data class RoutePointRequest(
    var latitude: String,
    var longitude: String,
    var pointOrder: Int
)

data class RoutePointResult(
    var pointId: Int
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

// 활동 추가
data class Activity(
    var locationName: String = "",
    var address: String = "",
    var latitude: String = "",
    var longitude: String = "",
    var visitDate: String = "",
    var startTime: String = "",
    var endTime: String = "",
    var category: String = "", // 음식점, 관광명소 등
    var description: String? = null,
    var activityImages: ArrayList<File?> = arrayListOf(),
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
)

data class ActivityImage(
    var id: Int,
    var url: String
)

data class ActivityId(
    var activityId: Int
)

// 활동 수정
data class ActivityUpdateRequest(
    var locationName: String,
    var address: String,
    var latitude: String?,
    var longitude: String?,
    var visitDate: String,
    var startTime: String,
    var endTime: String,
    var category: String,
    var description: String?,
    var addedActivityImages: ArrayList<String>?,
    var deletedActivityImageIds: ArrayList<String>?
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
    val place_name: String, // 장소명, 업체명
    val category_name: String, // 카테고리 이름
    val category_group_code: String, // 중요 카테고리만 그룹핑한 카테고리 그룹 코드
    val category_group_name: String, // 중요 카테고리만 그룹핑한 카테고리 그룹명
    val phone: String, // 전화번호
    val address_name: String, // 전체 지번 주소
    val road_address_name: String, // 전체 도로명 주소
    val x: String, // X 좌표값 혹은 longitude
    val y: String, // Y 좌표값 혹은 latitude
    val place_url: String, // 장소 상세페이지 URL
    val distanc: String // 중심좌표까지의 거리. 단, x,y 파라미터를 준 경우에만 존재. 단위는 meter
)

data class ActivityPictureAlbum(
    val uri: Uri?,
    var selectedNumber: Int? = null
)

const val pictureImgType = 0
const val pictureAddType = 1