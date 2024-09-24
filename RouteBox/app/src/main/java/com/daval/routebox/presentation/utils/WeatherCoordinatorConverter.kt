package com.daval.routebox.presentation.utils

import android.util.Log
import kotlin.math.*

object WeatherCoordinatorConverter {
    const val NX = 149  // X축 격자점 수
    const val NY = 253  // Y축 격자점 수

    data class LamcParameter(
        var Re: Double = 6371.00877,   // 사용할 지구반경 [ km ]
        var grid: Double = 5.0,        // 격자간격 [ km ]
        var slat1: Double = 30.0,      // 표준위도 [degree]
        var slat2: Double = 60.0,      // 표준위도 [degree]
        var olon: Double = 126.0,      // 기준점의 경도 [degree]
        var olat: Double = 38.0,       // 기준점의 위도 [degree]
        var xo: Double = 210.0 / 5.0,     // 기준점의 X좌표 [격자거리]
        var yo: Double = 675.0 / 5,     // 기준점의 Y좌표 [격자거리]
        var first: Int = 0             // 시작여부 (0 = 시작)
    )

    var x: Double = 0.0
    var y: Double = 0.0
    var lon: Double = 0.0
    var lat: Double = 0.0

    fun changeCoordinate(version: Int, coordinate1: Double, coordinate2: Double) {
        val map = LamcParameter()

        when (version) {
            1 -> { // 격자 -> 위경도
                x = coordinate1.toDouble()
                y = coordinate2.toDouble()

                lon = 0.0
                lat = 0.0
                mapConv(1, map)
                Log.d("ROUTE-TEST", "X = ${x.toInt()}, Y = ${y.toInt()} ---> lon.= $lon, lat.= $lat")
            }
            0 -> { // 위경도 -> 격자
                lon = coordinate1.toDouble()
                lat = coordinate2.toDouble()

                x = 0.0
                y = 0.0
                mapConv(0, map)
                Log.d("ROUTE-TEST", "lon.= $lon, lat.= $lat ---> X = ${x.toInt()}, Y = ${y.toInt()}")
            }
        }
    }

    fun mapConv(code: Int, map: LamcParameter) {
        if (code == 0) { // 위경도 -> (X,Y)
            lamcProj(0, map)
            x += 1.5
            y += 1.5
        } else if (code == 1) { // (X,Y) -> 위경도
            x -= 1
            y -= 1
            lamcProj(1, map)
        }
    }

    fun lamcProj(code: Int, map: LamcParameter) {
        val PI = asin(1.0) * 2.0
        val DEGRAD = PI / 180.0
        val RADDEG = 180.0 / PI

        val re = map.Re / map.grid
        val slat1 = map.slat1 * DEGRAD
        val slat2 = map.slat2 * DEGRAD
        val olon = map.olon * DEGRAD
        val olat = map.olat * DEGRAD

        var sn = Math.log(cos(slat1) / cos(slat2)) / Math.log(Math.tan(PI * 0.25 + slat2 * 0.5) / Math.tan(PI * 0.25 + slat1 * 0.5))
        var sf = Math.pow(Math.tan(PI * 0.25 + slat1 * 0.5), sn) * Math.cos(slat1) / sn
        var ro = re * sf / Math.pow(Math.tan(PI * 0.25 + olat * 0.5), sn)
        map.first = 1

        when (code) {
            0 -> { // (lon, lat) -> (x, y)
                var ra = tan(PI * 0.25 + lat * DEGRAD * 0.5)
                ra = re * sf / Math.pow(ra, sn)
                var theta = lon * DEGRAD - olon
                theta = if (theta > PI) theta - 2.0 * PI else if (theta < -PI) theta + 2.0 * PI else theta
                theta *= sn
                x = (ra * sin(theta) + map.xo).toDouble()
                y = (ro - ra * cos(theta) + map.yo).toDouble()
            }
            1 -> { // (x, y) -> (lon, lat)
                var xn = x - map.xo
                var yn = ro - y + map.yo
                val ra = sqrt(xn * xn + yn * yn)
                var alat = Math.pow((re * sf / ra), (1.0 / sn))
                alat = 2.0 * Math.atan(alat) - PI * 0.5

                if (xn < 0) xn *= (-1)

                var theta = 0.0

                if (xn != 0.0) {
                    if (yn < 0) yn *= -1

                    if (yn <= 0.0) {
                        theta = PI * 0.5
                        if (xn < 0.0) theta = -theta
                    } else {
                        theta = atan2(xn, yn)
                    }
                }

                val alon = theta / sn + olon
                lon = (alon * RADDEG).toDouble()
                lat = (alat * RADDEG).toDouble()
            }
        }
    }
}