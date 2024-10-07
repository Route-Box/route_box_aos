package com.daval.routebox.presentation.ui.route.write.convenience

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.daval.routebox.databinding.BottomSheetConvenienceWeatherBinding
import com.daval.routebox.domain.model.WeatherData
import com.daval.routebox.presentation.config.Constants.OPEN_API_BASE_URL
import com.daval.routebox.presentation.config.Constants.OPEN_API_SERVICE_KEY
import com.daval.routebox.presentation.ui.route.adapter.WeatherRVAdapter
import com.daval.routebox.presentation.ui.route.write.RouteConvenienceViewModel
import com.daval.routebox.presentation.ui.route.write.RouteWriteViewModel
import com.daval.routebox.presentation.utils.WeatherCoordinatorConverter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.json.JSONArray
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
    private val writeViewModel : RouteWriteViewModel by activityViewModels()
    private lateinit var weatherRVAdapter: WeatherRVAdapter
    private var weatherList = arrayListOf<WeatherData>()
    private lateinit var latitude: String
    private lateinit var longitude: String
    private lateinit var viewTime: String
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

        latitude = writeViewModel.currentCoordinate.value?.latitude.toString()
        longitude = writeViewModel.currentCoordinate.value?.longitude.toString()
        viewTime = LocalTime.now().toString().substring(0, 5)
        viewDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))

        binding.apply {
            writeViewModel = this@WeatherBottomSheet.writeViewModel
            viewModel = this@WeatherBottomSheet.convenienceViewModel
            lifecycleOwner = this@WeatherBottomSheet
            date = this@WeatherBottomSheet.viewDate
            time = this@WeatherBottomSheet.viewTime
        }

        setAdapter()
        callWeatherApi()
        convenienceViewModel.getRegionCode(latitude, longitude)

        return binding.root
    }

    private fun callWeatherApi() {
        // 현재 시각에 따라 넘겨야 하는 데이터들 개수
        latitude = writeViewModel.currentCoordinate.value?.latitude.toString()
        longitude = writeViewModel.currentCoordinate.value?.longitude.toString()
        var xy = WeatherCoordinatorConverter.changeCoordinate(latitude.toDouble(), longitude.toDouble())
        val site = "${OPEN_API_BASE_URL}1360000/VilageFcstInfoService_2.0/getUltraSrtFcst?serviceKey=$OPEN_API_SERVICE_KEY&pageNo=1&numOfRows=${numOfRows}&dataType=JSON&base_date=${getDateAndTime().first}&base_time=${getDateAndTime().second}00&nx=${xy.first}&ny=${xy.second}"

        val thread = PublicApiThread(site)
        thread.start()
        thread.join()

        var weatherResult = thread.returnResult()
        if (weatherResult == null) {
            Toast.makeText(activity, "다시 시도해주세요", Toast.LENGTH_SHORT).show()
        } else {
            getWeatherList(weatherResult)
        }
    }

    // dataNumber는 이용할 데이터의 개수를 의미!
    private fun getWeatherList(returnResult: JSONArray) {
        var result = arrayListOf<WeatherData>()

        for (i in 0 until 6) {
            var weatherType: WeatherType? = null
            var mainPTY = returnResult.getJSONObject(PTY + i).getString("fcstValue")
            var mainSKY = returnResult.getJSONObject(SKY + i).getString("fcstValue")
            var mainT1H = returnResult.getJSONObject(T1H + i).getString("fcstValue")
            var mainDate = returnResult.getJSONObject(T1H + i).getString("fcstDate")
            var mainTime = returnResult.getJSONObject(T1H + i).getString("fcstTime")
            if (mainPTY == "0") { // PTY(강수)가 없음이면 SKY(하늘 상태) 사용
                weatherType = returnWeatherType("SKY", mainSKY.toInt())
            } else {
                weatherType = returnWeatherType("PTY", mainPTY.toInt())
            }

            result.add(WeatherData(
                mainT1H, mainSKY, mainPTY,
                mainDate, mainTime.substring(0, 2), weatherType
            ))
        }

        weatherRVAdapter.addAllItems(result)
    }

    private fun setAdapter() {
        weatherRVAdapter = WeatherRVAdapter(weatherList)
        binding.weatherRv.adapter = weatherRVAdapter
        binding.weatherRv.layoutManager = GridLayoutManager(requireContext(), 6)
    }

    private fun getDateAndTime(): Pair<String, String> {
        // 시간이 자정일 경우, 날짜는 하루 앞으로, 시간은 23시로 직접 설정!
        var time = LocalTime.now().toString().substring(0, 2)
        var date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt()
        if (time == "00") {
            time = "23"
            date -= 1
        } else time = (time.toInt()).toString()

        return Pair(date.toString(), time)
    }
}

// 받아오는 총 데이터가 60개로 고정되어 있음!
const val numOfRows = 60
// PTY (강수 형태), SKY (하늘 상태), T1H (기온)에 해당하는 데이터의 Index
const val PTY = 6
const val SKY = 18
const val T1H = 24