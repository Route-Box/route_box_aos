package com.daval.routebox.presentation.utils

object WeatherCoordinatorConverter {

    var Re: Float = 6371.00877f            // 사용할 지구 반경 [ km ]
    var grid: Float = 5.0f                 // 격자간격 [ km ]
    var slat1: Float = 30.0f               // 표준위도 [degree]
    var slat2: Float = 60.0f               // 표준위도 [degree]
    var olon: Float = 126.0f               // 기준점의 경도 [degree]
    var olat: Float = 38.0f                // 기준점의 위도 [degree]
    var xo: Float = (210 / 5).toFloat()    // 기준점의 X좌표 [격자거리]
    var yo: Float = (675 / 5).toFloat()    // 기준점의 Y좌표 [격자거리]

    fun changeCoordinate(latitude: Double, longitude: Double): Pair<Int, Int> {
        val PI = Math.asin(1.0) * 2.0
        val DEGRAD = PI / 180.0

        val re = Re.toDouble() / grid.toDouble()
        val slat1 = slat1 * DEGRAD
        val slat2 = slat2 * DEGRAD
        val olon = olon * DEGRAD
        val olat = olat * DEGRAD

        var sn = Math.tan(PI * 0.25 + slat2 * 0.5) / Math.tan(PI * 0.25 + slat1 * 0.5)
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn)
        var sf = Math.tan(PI * 0.25 + slat1 * 0.5)
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn
        var ro = Math.tan(PI * 0.25 + olat * 0.5)
        ro = re * sf / Math.pow(ro, sn)

        var ra = Math.tan(PI * 0.25 + latitude * DEGRAD * 0.5)
        ra = re * sf / Math.pow(ra, sn)
        var theta = longitude * DEGRAD - olon

        if (theta > PI) {
            theta -= 2.0 * PI
        }
        if (theta < -PI) {
            theta += 2.0 * PI
        }
        theta *= sn

        val x = ((ra * Math.sin(theta)).toFloat() + xo + 1.5).toInt()
        val y = ((ro - ra * Math.cos(theta)).toFloat() + yo + 1.5).toInt()

        return Pair(x, y)
    }
}