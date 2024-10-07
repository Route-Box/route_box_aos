package com.daval.routebox.presentation.ui.route.write.convenience

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class PublicApiThread(
    var site: String
): Thread() {
    var result: JSONArray? = null

    override fun run() {
        val conn = URL(site).openConnection()
        // 데이터가 들어오는 통로 역할
        val input = conn.getInputStream()
        val br = BufferedReader(InputStreamReader(input))

        // Json 문서는 문자열로 데이터를 모두 읽어온 후, Json에 관련된 객체를 만들어서 데이터를 가져옴
        var str: String? = null
        // 버퍼는 일시적으로 데이터를 저장하는 메모리!
        val buf = StringBuffer()

        // 들어오는 값이 없을 때까지 받아오는 과정
        do {
            str = br.readLine()
            if (str != null) buf.append(str)
        } while (str != null)

        try {
            // 하나로 되어있는 결과를 JSON 객체 형태로 가져와 데이터 파싱
            val root = JSONObject(buf.toString())
            val response = root.getJSONObject("response").getJSONObject("body").getJSONObject("items")
            result = response.getJSONArray("item") // 객체 안에 있는 item이라는 이름의 리스트를 가져옴
        } catch (e: Exception) {
            Log.e("ThreadResult", "e = $e")
        }
    }

    fun returnResult(): JSONArray? {
        return result
    }

    fun getDateAndTime(): Pair<String, String> {
        // 시간이 자정일 경우, 날짜는 하루 앞으로, 시간은 23시로 직접 설정!
        var time = LocalTime.now().toString().substring(0, 2)
        var date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt()
        if (time == "00") {
            time = "23"
            date -= 1
        } else time = (time.toInt() - 1).toString()

        return Pair(date.toString(), time)
    }
}