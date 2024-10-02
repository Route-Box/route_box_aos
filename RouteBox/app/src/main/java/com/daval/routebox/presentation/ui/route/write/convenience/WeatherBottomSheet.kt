package com.daval.routebox.presentation.ui.route.write.convenience

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.daval.routebox.databinding.BottomSheetConvenienceWeatherBinding
import com.daval.routebox.domain.model.WeatherData
import com.daval.routebox.presentation.config.Constants.OPEN_API_BASE_URL
import com.daval.routebox.presentation.config.Constants.OPEN_API_SERVICE_KEY
import com.daval.routebox.presentation.ui.route.adapter.WeatherRVAdapter
import com.daval.routebox.presentation.ui.route.write.RouteConvenienceViewModel
import com.daval.routebox.presentation.utils.WeatherCoordinatorConverter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class WeatherBottomSheet: BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetConvenienceWeatherBinding
    private val convenienceViewModel: RouteConvenienceViewModel by activityViewModels()
    private lateinit var weatherRVAdapter: WeatherRVAdapter
    private var weatherList = arrayListOf<WeatherData>()
    private lateinit var latitude: String
    private lateinit var longitude: String
    private lateinit var date: String
    private lateinit var time: String
    private lateinit var viewDate: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetConvenienceWeatherBinding.inflate(inflater, container, false)

//        latitude = arguments?.getString("latitude").toString()
//        longitude = arguments?.getString("longitude").toString()
        date = (LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt() - 1).toString()
        time = LocalTime.now().toString().substring(0, 5)
        viewDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))

        binding.apply {
            viewModel = this@WeatherBottomSheet.convenienceViewModel
            lifecycleOwner = this@WeatherBottomSheet
            date = this@WeatherBottomSheet.viewDate
            time = this@WeatherBottomSheet.time
        }

        // TODO: 테스터용 지우기
        latitude = "34"
        longitude = "127"

        setAdapter()
        callWeatherApi()
        initObserve()
        convenienceViewModel.getRegionCode(latitude, longitude)

        return binding.root
    }

    private fun callWeatherApi() {
        val thread = WeatherThread()
        thread.start()
        thread.join()
    }

    // Open Api 결과를 받고, JSON을 파싱하기 위한 부분!!
    inner class WeatherThread: Thread() {
        override fun run() {
            // 현재 시각에 따라 넘겨야 하는 데이터들 개수
            var skipRows = if (time.substring(0, 2).toInt() < 5) (time.substring(0, 2).toInt() + 24 - 5 + 1) else time.substring(0, 2).toInt() - 5 + 1
            var numOfRows = WeatherTypeNumber * (WeatherRequestTime + skipRows)// 24시간 동안의 결과만 받으면 되기 때문에 해당 개수로 지정
            var xy = WeatherCoordinatorConverter.changeCoordinate(latitude.toDouble(), longitude.toDouble())
            val site = "${OPEN_API_BASE_URL}1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey=$OPEN_API_SERVICE_KEY&pageNo=1&numOfRows=${numOfRows}&dataType=JSON&base_date=${date}&base_time=0500&nx=${xy.first}&ny=${xy.second}"
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

            // 하나로 되어있는 결과를 JSON 객체 형태로 가져와 데이터 파싱
            val root = JSONObject(buf.toString())
            val response = root.getJSONObject("response").getJSONObject("body").getJSONObject("items")
            val item = response.getJSONArray("item") // 객체 안에 있는 item이라는 이름의 리스트를 가져옴

            // TODO: 좌표는 처음 입력했을 때 기준으로 사용
            // TODO: 비동기 처리 필요
            var result = arrayListOf<WeatherData>()
            for (i in 0 until WeatherRequestTime) {
                var tempResult = arrayListOf<String>()
                for (j in (i + skipRows) * 12 until (i + skipRows + 1) * WeatherTypeNumber) {
                    var category = item.getJSONObject(j).getString("category")
                    if (category == "TMP" || category == "SKY" || category == "PTY") {
                        tempResult.add(item.getJSONObject(j).getString("fcstValue"))
                    }
                }

                var weatherType: WeatherType? = null
                if (tempResult[2] == "0") { // PTY(강수)가 없음이면 SKY(하늘 상태) 사용
                    weatherType = returnWeatherType("SKY", tempResult[1].toInt())
                } else {
                    weatherType = returnWeatherType("PTY", tempResult[2].toInt())
                }

                result.add(WeatherData(
                    tempResult[0], tempResult[1], tempResult[2],
                    item.getJSONObject((i + skipRows) * 12).getString("fcstDate"),
                    item.getJSONObject((i + skipRows) * 12).getString("fcstTime").toString().substring(0, 2), weatherType
                ))
            }

            weatherRVAdapter.addAllItems(result)
        }
    }

    private fun setAdapter() {
        weatherRVAdapter = WeatherRVAdapter(weatherList)
        binding.weatherRv.adapter = weatherRVAdapter
        binding.weatherRv.layoutManager = GridLayoutManager(requireContext(), 7)
    }

    private fun initObserve() {
        convenienceViewModel.weatherRegion.observe(viewLifecycleOwner) {
            binding.regionTv.text = convenienceViewModel.weatherRegion.value
        }
    }
}

// 날씨 API에서 한 시간 기준으로 받아오는 데이터 결과의 개수 ex) 온도, 시간, 위치, ...
const val WeatherTypeNumber = 12
// 화면에 나타내기 위해 데이터를 받아야 하는 시간
const val WeatherRequestTime = 7