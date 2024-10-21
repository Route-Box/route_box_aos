package com.daval.routebox.domain.model

import android.util.Log

/** 필터 유형 */
enum class FilterType(val order: Int, val maxSelectionCount: Int) {
    WITH_WHOM(0, 0), // 누구와
    HOW_MANY(1, 0), // 몇 명과
    HOW_LONG(2, 0), // 며칠 동안
    ROUTE_STYLE(3, 2), // 원하는 스타일
    MEANS_OF_TRANSPORTATION(4, 0), // 이동 수단
}

/** 필터 옵션 */
enum class FilterOption(val filterType: FilterType, val optionName: String) {
    // 누구와
    WITH_ALONE(FilterType.WITH_WHOM, "혼자"),
    WITH_FRIEND(FilterType.WITH_WHOM, "친구와"),
    WITH_LOVER(FilterType.WITH_WHOM, "연인과"),
    WITH_PARTNER(FilterType.WITH_WHOM, "배우자와"),
    WITH_CHILD(FilterType.WITH_WHOM, "아이와"),
    WITH_PARENT(FilterType.WITH_WHOM, "부모님과"),
    WITH_ETC(FilterType.WITH_WHOM, "누군가와"),
    // 몇 명과
    MANY_TWO(FilterType.HOW_MANY, "2명"),
    MANY_THREE(FilterType.HOW_MANY, "3명"),
    MANY_FOUR(FilterType.HOW_MANY, "4명"),
    MANY_OVER_FIVE(FilterType.HOW_MANY, "5명 이상"),
    // 며칠 동안
    LONG_THE_DAY(FilterType.HOW_LONG, "당일"),
    LONG_ONE_NIGHT_TWO_DAYS(FilterType.HOW_LONG, "1박 2일"),
    LONG_TWO_NIGHTS_THREE_DAYS(FilterType.HOW_LONG, "2박 3일"),
    LONG_THREE_NIGHTS_FOUR_DAYS(FilterType.HOW_LONG, "3박 4일"),
    LONG_FOUR_NIGHTS_FIVE_DAYS(FilterType.HOW_LONG, "4박 5일"),
    LONG_FIVE_NIGHTS_SIX_DAYS(FilterType.HOW_LONG, "5박 6일"),
    LONG_UNKNOWN(FilterType.HOW_LONG, "6박 7일 이상"),
    // 루트 스타일
    STYLE_HEALING(FilterType.ROUTE_STYLE, "힐링 \uD83D\uDE0C"),
    STYLE_NATURE(FilterType.ROUTE_STYLE, "자연 만끽 \uD83C\uDF3F"),
    STYLE_TOURIST_SPOT(FilterType.ROUTE_STYLE, "관광지 필수 \uD83C\uDFA1"),
    STYLE_SNS_SPOT(FilterType.ROUTE_STYLE, "SNS 스팟 \uD83D\uDCF8"),
    STYLE_ACTIVITY(FilterType.ROUTE_STYLE, "체험, 엑티비티 \uD83D\uDEFC"),
    STYLE_SHOPPING(FilterType.ROUTE_STYLE, "쇼핑 \uD83D\uDECD\uFE0F"),
    STYLE_EATING(FilterType.ROUTE_STYLE, "먹방 \uD83C\uDF7D\uFE0F"),
    STYLE_HISTORY(FilterType.ROUTE_STYLE, "역사 탐방 \uD83D\uDD0D"),
    STYLE_CULTURE(FilterType.ROUTE_STYLE, "전시, 문화 \uD83D\uDCD6"),
    STYLE_ETC(FilterType.ROUTE_STYLE, "내 맘대로"),
    // 이동 수단
    TRANSPORTATION_FOOTSTEP(FilterType.MEANS_OF_TRANSPORTATION, "뚜벅뚜벅 \uD83D\uDC63"),
    TRANSPORTATION_TAXI_CAR(FilterType.MEANS_OF_TRANSPORTATION, "택시/자동차 \uD83D\uDE97"),
    TRANSPORTATION_PUBLIC_TRANSPORTATION(FilterType.MEANS_OF_TRANSPORTATION, "대중교통 \uD83D\uDE8C");

    companion object {
        // 필터 유형에 해당하는 선택지 리스트 반환
        fun findOptionsByFilterType(type: FilterType): List<FilterOption> {
            return entries.filter { it.filterType == type }
        }

        // 필터 이름 리스트에 해당하는 선택지 리스트 반환
        fun findOptionsByNames(names: List<String>): List<FilterOption> {
            return entries.filter { entry ->
                val cleanOptionName = entry.optionName.removeEmojis() // FilterOption의 이모티콘 제거
                names.any { name ->
                    name.contains(cleanOptionName) // 부분 일치 확인
                }
            }
        }

        // 리스트를 FilterType 별로 그룹핑
        fun groupingTagListWithFilterType(tagList: List<String>): Map<FilterType, Set<FilterOption>> {
            return tagList
                .mapNotNull { optionName -> FilterOption.findOptionsByNames(listOf(optionName)).firstOrNull() }
                .groupBy { it.filterType }
                .mapValues { entry -> entry.value.toSet() } // Set<FilterOption>으로 변환
        }

        // 적용한 옵션들을 필터 유형에 맞게 변환 - 필터 데이터 서버 전송을 위함
        fun getOptionNamesByTypeAndNames(names: List<String>, type: FilterType): List<Any>? {
            // type에 해당하는 FilterOption들 중에서 names에 속하는 옵션들을 반환
            val filteredOptions = entries
                .filter { it.filterType == type && it.optionName in names }

            // 필터링 결과가 없으면 null 반환
            if (filteredOptions.isEmpty()) return null

            return if (type == FilterType.HOW_MANY) {
                // HOW_MANY일 경우 인원수(Int)로 변환하여 반환
                filteredOptions.mapNotNull { getPersonCountIfHowMany(it) }
            } else {
                // 다른 타입의 경우 optionName(String)으로 반환
                filteredOptions.map { it.optionName }
            }
        }

        // type이 '몇 명과'라면 예외적으로 정수 인원수 반환
        private fun getPersonCountIfHowMany(option: FilterOption): Int? {
            return if (option.filterType == FilterType.HOW_MANY) {
                // optionName에서 첫 번째 문자가 숫자인 경우 변환
                option.optionName.firstOrNull()?.digitToIntOrNull()
            } else {
                null
            }
        }

        // 필터 유형 순서에 따라 정렬된 필터 옵션 리스트 반환
        fun getOptionsSortedByFilterType(): List<List<FilterOption>> {
            return FilterType.entries.sortedBy { it.order }.map { type ->
                findOptionsByFilterType(type)
            }
        }

        // int 형태의 numberOfPeople를 명 수 텍스트로 변환
        fun getNumberOfPeopleText(numberOfPeople: Int?): String {
            return when (numberOfPeople) {
                in 2..4 -> "${numberOfPeople}명"
                else -> "5명 이상"
            }
        }

        // 이모티콘 제거 함수
        private fun String.removeEmojis(): String {
            return this.replace(Regex("[\\p{So}\\p{Cn}]+"), "") // 유니코드 Symbol, Other 및 이모티콘 범위를 제거
        }
    }
}