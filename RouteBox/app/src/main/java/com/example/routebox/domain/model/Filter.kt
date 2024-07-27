package com.example.routebox.domain.model

/** 필터 유형 */
enum class FilterType(val order: Int) {
    WITH_WHOM(0), // 누구와
    HOW_MANY(1), // 몇 명과
    HOW_LONG(2), // 며칠 동안
    ROUTE_STYLE(3), // 원하는 스타일
    MEANS_OF_TRANSPORTATION(4), // 이동 수단
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
    WITH_ETC(FilterType.WITH_WHOM, "기타"),
    // 몇 명과
    MANY_ALONE(FilterType.HOW_MANY, "혼자"),
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
    STYLE_ETC(FilterType.ROUTE_STYLE, "기타"),
    // 이동 수단
    TRANSPORTATION_FOOTSTEP(FilterType.MEANS_OF_TRANSPORTATION, "뚜벅뚜벅"),
    TRANSPORTATION_TAXI_CAR(FilterType.MEANS_OF_TRANSPORTATION, "택시/자동차"),
    TRANSPORTATION_PUBLIC_TRANSPORTATION(FilterType.MEANS_OF_TRANSPORTATION, "대중교통");

    companion object {
        // 필터 유형에 해당하는 선택지 리스트 반환
        fun findOptionsByFilterType(type: FilterType): List<FilterOption> {
            return entries.filter { it.filterType == type }
        }

        // 필터 유형 순서에 따라 정렬된 필터 옵션 리스트 반환
        fun getOptionsSortedByFilterType(): List<List<FilterOption>> {
            return FilterType.entries.sortedBy { it.order }.map { type ->
                findOptionsByFilterType(type)
            }
        }
    }
}