package com.daval.routebox.presentation.ui.route.write.convenience

import android.app.Service.MODE_PRIVATE
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
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
import com.daval.routebox.presentation.ui.route.write.RouteWriteActivity
import com.daval.routebox.presentation.ui.route.write.RouteWriteActivity.Companion.ROUTE_WRITE_DEFAULT_ZOOM_LEVEL
import com.daval.routebox.presentation.ui.route.write.RouteWriteViewModel
import com.daval.routebox.presentation.utils.SharedPreferencesHelper
import com.daval.routebox.presentation.utils.SharedPreferencesHelper.Companion.APP_PREF_KEY
import com.daval.routebox.presentation.utils.WeatherCoordinatorConverter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class RouteConvenienceFragment: Fragment(), CompoundButton.OnCheckedChangeListener,
    OnMapReadyCallback {

    private lateinit var binding: FragmentRouteConvenienceBinding
    private var googleMap: GoogleMap? = null
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
            Log.d("RouteConvenienceFrag", "latLng: $it")
            if (writeViewModel.currentCoordinate.value?.latitude != 0.0) {
                // 카메라 설정
                setMapCenterPoint()
                // 현재 위치 마커 띄우기
                setCurrentLocationMarker()
                
                // 지역 이름 받아오기
                convenienceViewModel.getRegionCode(writeViewModel.currentCoordinate.value?.latitude.toString(), writeViewModel.currentCoordinate.value?.longitude.toString())
                // 현재 위치 날씨 받아오기
                callWeatherApi()
            } else {
                var sharedPreferencesHelper = SharedPreferencesHelper(requireActivity().getSharedPreferences(APP_PREF_KEY, MODE_PRIVATE))
                writeViewModel.setCurrentCoordinate(LatLng(sharedPreferencesHelper.getLocationCoordinate()[0]!!, sharedPreferencesHelper.getLocationCoordinate()[1]!!))
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

    private fun initMapSetting() {
        // 맵 프래그먼트 초기화
        val mapFragment = childFragmentManager.findFragmentById(R.id.convenience_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setMapCenterPoint() {
        // 카메라 위치 설정 및 줌 레벨 조정
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(
            writeViewModel.currentCoordinate.value!!,
            ROUTE_WRITE_DEFAULT_ZOOM_LEVEL
        ))
    }

    private fun setCurrentLocationMarker() {
        val activity = requireActivity() as RouteWriteActivity

        // 마커 추가
        googleMap?.addMarker(
            MarkerOptions()
                .position(LatLng(
                    writeViewModel.currentCoordinate.value!!.latitude,
                    writeViewModel.currentCoordinate.value!!.longitude
                ))
                .icon(activity.getResizedMarker(iconName = R.drawable.ic_gps_marker))
                .zIndex(1f)
        )
    }

    // 마커 띄우기
    private fun addMarker(latitude: Double, longitude: Double, markerImg: Int) {
//        var styles = googleMap.labelManager?.addLabelStyles(LabelStyles.from(LabelStyle.from(markerImg)))
//        val options = LabelOptions.from(LatLng.from(latitude, longitude)).setStyles(styles)
//        val layer = googleMap.labelManager!!.layer
//        val label = layer!!.addLabel(options)
//        label.show()
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
//            convenienceViewModel.setCameraPosition(googleMap?.cameraPosition!!)
//            googleMap.labelManager?.removeAllLabelLayer()

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
        // 현재 시각에 따라 넘겨야 하는 데이터들 개수
        var dateAndTime = getDateAndTime()
        var latitude = writeViewModel.currentCoordinate.value?.latitude
        var longitude = writeViewModel.currentCoordinate.value?.longitude
        var xy = WeatherCoordinatorConverter.changeCoordinate(latitude!!.toDouble(), longitude!!.toDouble())
        val site = "${OPEN_API_BASE_URL}1360000/VilageFcstInfoService_2.0/getUltraSrtFcst?serviceKey=$OPEN_API_SERVICE_KEY&pageNo=1&numOfRows=${numOfRows}&dataType=JSON&base_date=${dateAndTime.first}&base_time=${dateAndTime.second}00&nx=${xy.first}&ny=${xy.second}"

        // Thread를 통해 Open Api 결과를 받고, JSON을 파싱하기 위한 부분!!
        val thread = PublicApiThread(site)
        thread.start()
        thread.join()

        var weatherResult = thread.returnResult()
        if (weatherResult == null) {
            Toast.makeText(activity, "다시 시도해주세요", Toast.LENGTH_SHORT).show()
        } else {
            getMainWeather(weatherResult)
        }

        convenienceViewModel.setWeatherMainData(mainWeather)
    }

    private fun getMainWeather(returnResult: JSONArray) {
        var weatherType: WeatherType? = null
        var mainPTY = returnResult.getJSONObject(PTY).getString("fcstValue")
        var mainSKY = returnResult.getJSONObject(SKY).getString("fcstValue")
        var mainT1H = returnResult.getJSONObject(T1H).getString("fcstValue")
        var mainDate = returnResult.getJSONObject(T1H).getString("fcstDate")
        var mainTime = returnResult.getJSONObject(T1H).getString("fcstTime")
        if (mainPTY == "0") { // PTY(강수)가 없음이면 SKY(하늘 상태) 사용
            weatherType = returnWeatherType("SKY", mainSKY.toInt())
        } else {
            weatherType = returnWeatherType("PTY", mainPTY.toInt())
        }

        mainWeather = WeatherData(
            mainT1H, mainSKY, mainPTY,
            mainDate, mainTime, weatherType
        )
    }

    override fun onStop() {
        super.onStop()
        convenienceViewModel.setPlaceCategoryResult()
        placeRVAdapter.removeAllItems()
        binding.routeConvenienceBottomSheet.bottomSheetCl.visibility = View.GONE
    }

    private fun getDateAndTime(): Pair<String, String> {
        // 시간이 자정일 경우, 날짜는 하루 앞으로, 시간은 23시로 직접 설정!
        var time = LocalTime.now().toString().substring(0, 2)
        var date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt()
        if (time == "00") {
            time = "23"
            date -= 1
        } else time = (time.toInt() - 1).toString()

        return Pair(date.toString(), time)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        initObserve()
    }
}