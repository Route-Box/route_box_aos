package com.daval.routebox.presentation.ui.route.write.convenience

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.daval.routebox.R
import com.daval.routebox.databinding.BottomSheetConveniencePlaceBinding
import com.daval.routebox.databinding.FragmentRouteConvenienceBinding
import com.daval.routebox.domain.model.CategoryGroupCode
import com.daval.routebox.domain.model.ConvenienceCategoryResult
import com.daval.routebox.domain.model.WeatherData
import com.daval.routebox.presentation.config.Constants.OPEN_API_BASE_URL
import com.daval.routebox.presentation.config.Constants.OPEN_API_SERVICE_KEY
import com.daval.routebox.presentation.ui.route.adapter.ConveniencePlaceRVAdapter
import com.daval.routebox.presentation.ui.route.write.MapCameraRadius
import com.daval.routebox.presentation.ui.route.write.RouteConvenienceViewModel
import com.daval.routebox.presentation.ui.route.write.RouteWriteViewModel
import com.daval.routebox.presentation.utils.WeatherCoordinatorConverter
import com.google.android.gms.location.LocationServices
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class RouteConvenienceFragment: Fragment(), CompoundButton.OnCheckedChangeListener {

    private lateinit var binding: FragmentRouteConvenienceBinding
    private lateinit var kakaoMap: KakaoMap
    private val writeViewModel: RouteWriteViewModel by activityViewModels()
    private val convenienceViewModel: RouteConvenienceViewModel by activityViewModels()
    private var categoryDotImg: Int = -1
    private lateinit var bottomSheetConvenienceDialog: BottomSheetConveniencePlaceBinding
    private var placeList = arrayListOf<ConvenienceCategoryResult>()
    private val placeRVAdapter = ConveniencePlaceRVAdapter(placeList)
    private lateinit var mainWeather: WeatherData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRouteConvenienceBinding.inflate(inflater, container, false)

        binding.apply {
            viewModel = this@RouteConvenienceFragment.writeViewModel
            convenienceViewModel = this@RouteConvenienceFragment.convenienceViewModel
            lifecycleOwner = this@RouteConvenienceFragment
        }

        setInit()
        initMapSetting()
        initClickListener()
        initRadioButton()
        setAdapter()

        return binding.root
    }

    private fun initMapSetting() {
        binding.convenienceMap.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                // 지도 API 가 정상적으로 종료될 때 호출
                Log.d("KakaoMap", "onMapDestroy: ")
            }
            override fun onMapError(error: Exception) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출
                Log.d("KakaoMap", "onMapError: $error")
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(kakaoMap: KakaoMap) {
                // 인증 후 API 가 정상적으로 실행될 때 호출됨
                Log.d("KakaoMap", "onMapReady: $kakaoMap")
                this@RouteConvenienceFragment.kakaoMap = kakaoMap

                if (writeViewModel.currentCoordinate.value != null) {
                    val cameraUpdate = CameraUpdateFactory.newCenterPosition(writeViewModel.currentCoordinate.value)
                    kakaoMap.moveCamera(cameraUpdate)
                }

                initObserve()
                callWeatherApi()
//                addCurrentLocationMarker()
            }

            override fun getZoomLevel(): Int {
                return 17
            }
        })
    }

    private fun initClickListener() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
        binding.weatherCv.setOnClickListener {
            val weatherBottomSheet = WeatherBottomSheet()
            val bundle = Bundle()
            bundle.putString("latitude", writeViewModel.currentCoordinate.value?.latitude.toString())
            bundle.putString("longitude", writeViewModel.currentCoordinate.value?.longitude.toString())
            weatherBottomSheet.arguments = bundle
            weatherBottomSheet.run {
                setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogStyle)
            }
            weatherBottomSheet.show(requireActivity().supportFragmentManager, weatherBottomSheet.tag)
        }
    }

    private fun initObserve() {
        writeViewModel.currentCoordinate.observe(viewLifecycleOwner) {
            if (writeViewModel.currentCoordinate.value != null) {
                val cameraUpdate = CameraUpdateFactory.newCenterPosition(writeViewModel.currentCoordinate.value)
                kakaoMap.moveCamera(cameraUpdate)

                // 현재 위치 마커 띄우기
                var styles = kakaoMap.labelManager?.addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.ic_gps_marker)))
                val options = LabelOptions.from(LatLng.from(writeViewModel.currentCoordinate.value!!.latitude,
                    writeViewModel.currentCoordinate.value!!.longitude
                )).setStyles(styles)
                val layer = kakaoMap.labelManager!!.layer
                val label = layer!!.addLabel(options)
                label.show()
            }
        }

        convenienceViewModel.isCategoryEndPage.observe(viewLifecycleOwner) {
            if (convenienceViewModel.isCategoryEndPage.value == true) {
                for (i in 0 until convenienceViewModel.placeCategoryResult.value!!.size) {
                    addMarker(convenienceViewModel.placeCategoryResult.value!![i].latitude.toDouble(), convenienceViewModel.placeCategoryResult.value!![i].longitude.toDouble(), categoryDotImg)
                }
            }

            if (convenienceViewModel.placeCategoryResult.value != null && convenienceViewModel.placeCategoryResult.value!!.size != 0) {
                placeRVAdapter.resetAllItems(convenienceViewModel.placeCategoryResult.value!!)
                binding.routeConvenienceBottomSheet.bottomSheetCl.visibility = View.VISIBLE
            }
        }
    }

    private fun setInit() {
        bottomSheetConvenienceDialog = binding.routeConvenienceBottomSheet
        bottomSheetConvenienceDialog.apply {
            this.viewModel = this@RouteConvenienceFragment.writeViewModel
            this.lifecycleOwner = this@RouteConvenienceFragment
        }
    }

    // 마커 띄우기
    private fun addMarker(latitude: Double, longitude: Double, markerImg: Int) {
        var styles = kakaoMap.labelManager?.addLabelStyles(LabelStyles.from(LabelStyle.from(markerImg)))
        val options = LabelOptions.from(LatLng.from(latitude, longitude)).setStyles(styles)
        val layer = kakaoMap.labelManager!!.layer
        val label = layer!!.addLabel(options)
        label.show()
    }

    private fun initRadioButton() {
        // 선택한 라디오 버튼 글씨 Bold 처리하기 위한 ChangedListener 부분
        binding.categoryStay.setOnCheckedChangeListener(this)
        binding.categoryTour.setOnCheckedChangeListener(this)
        binding.categoryFood.setOnCheckedChangeListener(this)
        binding.categoryCafe.setOnCheckedChangeListener(this)
        binding.categoryCulture.setOnCheckedChangeListener(this)
        binding.categoryParking.setOnCheckedChangeListener(this)

        binding.categoryRadiogroup.setOnCheckedChangeListener { _, buttonId ->
            convenienceViewModel.setCameraPosition(kakaoMap.cameraPosition!!.position)
            kakaoMap.labelManager?.removeAllLabelLayer()

            when (buttonId) {
                R.id.category_stay -> {
                    convenienceViewModel.setKakaoCategory(CategoryGroupCode.AD5)
                    categoryDotImg = R.drawable.ic_marker_stay
                }
                R.id.category_tour -> {
//                    convenienceViewModel.setTourCategory()
                    categoryDotImg = R.drawable.ic_marker_tour
                    callTourApi()
                }
                R.id.category_food -> {
                    convenienceViewModel.setKakaoCategory(CategoryGroupCode.FD6)
                    categoryDotImg = R.drawable.ic_marker_food
                }
                R.id.category_cafe -> {
                    convenienceViewModel.setKakaoCategory(CategoryGroupCode.CE7)
                    categoryDotImg = R.drawable.ic_marker_cafe
                }
                R.id.category_culture -> {
                    convenienceViewModel.setKakaoCategory(CategoryGroupCode.CT1)
                    categoryDotImg = R.drawable.ic_marker_culture
                }
                R.id.category_parking -> {
                    convenienceViewModel.setKakaoCategory(CategoryGroupCode.PK6)
                    categoryDotImg = R.drawable.ic_marker_parking
                }
            }
        }
    }

    private fun callTourApi() {
        val thread = NetworkThread()
        thread.start()
        thread.join()
    }

    // Open Api 결과를 받고, JSON을 파싱하기 위한 부분!!
    inner class NetworkThread: Thread() {
        override fun run() {
            val site = "${OPEN_API_BASE_URL}B551011/KorService1/locationBasedList1?numOfRows=300&MobileOS=AND&MobileApp=Route%20Box&_type=json&mapX=${convenienceViewModel.cameraPosition.value?.longitude}&mapY=${convenienceViewModel.cameraPosition.value?.latitude}&radius=$MapCameraRadius&contentTypeId=12&serviceKey=${OPEN_API_SERVICE_KEY}"
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

            var result = arrayListOf<ConvenienceCategoryResult>()
            for (i in 0 until item.length()) {
                var longitude = item.getJSONObject(i).getString("mapx")
                var latitude = item.getJSONObject(i).getString("mapy")
                addMarker(latitude.toDouble(), longitude.toDouble(), R.drawable.ic_marker_tour)
                result.add(ConvenienceCategoryResult(
                    item.getJSONObject(i).getString("title"),
                    item.getJSONObject(i).getString("firstimage"),
                    latitude, longitude
                ))
            }
            placeRVAdapter.resetAllItems(result)
            binding.routeConvenienceBottomSheet.bottomSheetCl.visibility = View.VISIBLE
        }
    }

    private fun setAdapter() {
        bottomSheetConvenienceDialog.placeRv.apply {
            this.adapter = placeRVAdapter
            this.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        }
        bottomSheetConvenienceDialog.placeRv.itemAnimator = null
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        buttonView.typeface = if (isChecked) resources.getFont(R.font.pretendard_bold) else resources.getFont(R.font.pretendard_regular)
    }

    private fun callWeatherApi() {
        val thread = WeatherThread()
        thread.start()
        thread.join()

        convenienceViewModel.setWeatherMainData(mainWeather)
    }

    // Open Api 결과를 받고, JSON을 파싱하기 위한 부분!!
    inner class WeatherThread: Thread() {
        override fun run() {
            // 현재 시각에 따라 넘겨야 하는 데이터들 개수
            var date = (LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt() - 1).toString()
            var time = LocalTime.now().toString().substring(0, 5)
            // TODO: 테스터용 지우기
            var latitude = "34"
            var longitude = "127"
            var skipRows = if (time.substring(0, 2).toInt() < 5) (time.substring(0, 2).toInt() + 24 - 5) else time.substring(0, 2).toInt() - 5
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
            var tempResult = arrayListOf<String>()
            for (i in (skipRows * 12) until (skipRows + 1) * 12) {
                var category = item.getJSONObject(i).getString("category")
                if (category == "TMP" || category == "SKY" || category == "PTY") {
                    tempResult.add(item.getJSONObject(i).getString("fcstValue"))
                }
            }

            var weatherType: WeatherType? = null
            if (tempResult[2] == "0") { // PTY(강수)가 없음이면 SKY(하늘 상태) 사용
                weatherType = returnWeatherType("SKY", tempResult[1].toInt())
            } else {
                weatherType = returnWeatherType("PTY", tempResult[2].toInt())
            }

            mainWeather = WeatherData(
                tempResult[0], tempResult[1], tempResult[2], null, null, weatherType
            )
        }
    }

    // 크래시가 발생할 수도 있어 지도의 LifeCycle도 함께 관리 필요!
    override fun onResume() {
        super.onResume()
        binding.convenienceMap.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.convenienceMap.pause()
    }

    override fun onStop() {
        super.onStop()
        convenienceViewModel.setPlaceCategoryResult()
        placeRVAdapter.removeAllItems()
        binding.routeConvenienceBottomSheet.bottomSheetCl.visibility = View.GONE
    }
}