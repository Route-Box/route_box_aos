package com.daval.routebox.presentation.config

import com.daval.routebox.BuildConfig

object Constants {
    const val BASE_URL = BuildConfig.BASE_URL
    const val KAKAO_BASE_URL = "https://dapi.kakao.com/v2/local/"
    const val OPEN_API_BASE_URL = "https://apis.data.go.kr/"
    const val OPEN_API_SERVICE_KEY = BuildConfig.OPEN_API_SERVICE_KEY
    const val WEB_BASE_URL = BuildConfig.WEB_BASE_URL // 웹 주소

    // 웹 endPoint
    const val ENDPOINT_HOME = "/" // 홈
    const val ENDPOINT_MY = "/my-page" // 마이페이지

    // 약관 관련
    const val STANDARD_TERM_URL = "https://trapezoidal-success-ff6.notion.site/10845c063330803d8e96e6d58739a77b" // 필수 이용 약관
    const val POLICY_TERM_URL = "https://trapezoidal-success-ff6.notion.site/10845c063330800488e9e377850fe8e0" // 개인정보 수집 이용 동의
    const val LOCATION_TERM_URL = "https://trapezoidal-success-ff6.notion.site/f9b6a0a31d8b4334900137aa9cb023f3" // 위치 기반 서비스 이용 약관
    const val ALERT_TERM_URL = "https://trapezoidal-success-ff6.notion.site/10e45c0633308051a8e2f742f563ad13" // 알림 약관 동의
}