package com.daval.routebox.presentation.ui.route.write

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
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

@RequiresApi(Build.VERSION_CODES.O)
class RouteConvenienceFragment: Fragment(), CompoundButton.OnCheckedChangeListener {

    private lateinit var binding: FragmentRouteConvenienceBinding
    private lateinit var kakaoMap: KakaoMap
    private val writeViewModel: RouteWriteViewModel by activityViewModels()
    private var categoryDotImg: Int = -1
    private lateinit var bottomSheetDialog: BottomSheetConveniencePlaceBinding
    private var placeList = arrayListOf<ConvenienceCategoryResult>()
    private val placeRVAdapter = ConveniencePlaceRVAdapter(placeList)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRouteConvenienceBinding.inflate(inflater, container, false)

        binding.apply {
            viewModel = this@RouteConvenienceFragment.writeViewModel
        }

        setInit()
        initMapSetting()
        initClickListener()
        initRadioButton()
        setAdapter()
        initObserve()

        // writeViewModel.getWeatherList()

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

                val cameraUpdate = CameraUpdateFactory.newCenterPosition(writeViewModel.currentCoordinate.value)
                kakaoMap.moveCamera(cameraUpdate)

                initObserve()
                callWeatherApi()
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

        writeViewModel.isCategoryEndPage.observe(viewLifecycleOwner) {
            if (writeViewModel.isCategoryEndPage.value == true) {
                for (i in 0 until writeViewModel.placeCategoryResult.value!!.size) {
                    addMarker(writeViewModel.placeCategoryResult.value!![i].latitude.toDouble(), writeViewModel.placeCategoryResult.value!![i].longitude.toDouble(), categoryDotImg)
                }
            }

            if (writeViewModel.placeCategoryResult.value != null && writeViewModel.placeCategoryResult.value!!.size != 0) {
                placeRVAdapter.resetAllItems(writeViewModel.placeCategoryResult.value!!)
                binding.routeConvenienceBottomSheet.bottomSheetCl.visibility = View.VISIBLE
            }
        }

//        writeViewModel.getWeatherList()
    }

    private fun setInit() {
        bottomSheetDialog = binding.routeConvenienceBottomSheet
        bottomSheetDialog.apply {
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

    // TODO: 누른 카테고리 장소 띄워주기
    private fun initRadioButton() {
        // 선택한 라디오 버튼 글씨 Bold 처리하기 위한 ChangedListener 부분
        binding.categoryStay.setOnCheckedChangeListener(this)
        binding.categoryTour.setOnCheckedChangeListener(this)
        binding.categoryFood.setOnCheckedChangeListener(this)
        binding.categoryCafe.setOnCheckedChangeListener(this)
        binding.categoryCulture.setOnCheckedChangeListener(this)
        binding.categoryParking.setOnCheckedChangeListener(this)

        binding.categoryRadiogroup.setOnCheckedChangeListener { _, buttonId ->
            writeViewModel.setCameraPosition(kakaoMap.cameraPosition!!.position)
            kakaoMap.labelManager?.removeAllLabelLayer()

            when (buttonId) {
                R.id.category_stay -> {
                    writeViewModel.setKakaoCategory(CategoryGroupCode.AD5)
                    categoryDotImg = R.drawable.ic_marker_stay
                }
                R.id.category_tour -> {
//                    writeViewModel.setTourCategory()
                    categoryDotImg = R.drawable.ic_marker_tour
                    callTourApi()
                }
                R.id.category_food -> {
                    writeViewModel.setKakaoCategory(CategoryGroupCode.FD6)
                    categoryDotImg = R.drawable.ic_marker_food
                }
                R.id.category_cafe -> {
                    writeViewModel.setKakaoCategory(CategoryGroupCode.CE7)
                    categoryDotImg = R.drawable.ic_marker_cafe
                }
                R.id.category_culture -> {
                    writeViewModel.setKakaoCategory(CategoryGroupCode.CT1)
                    categoryDotImg = R.drawable.ic_marker_culture
                }
                R.id.category_parking -> {
                    writeViewModel.setKakaoCategory(CategoryGroupCode.PK6)
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
            val site = "${OPEN_API_BASE_URL}B551011/KorService1/locationBasedList1?numOfRows=300&MobileOS=AND&MobileApp=Route%20Box&_type=json&mapX=${writeViewModel.cameraPosition.value?.longitude}&mapY=${writeViewModel.cameraPosition.value?.latitude}&radius=${MapCameraRadius}&contentTypeId=12&serviceKey=${OPEN_API_SERVICE_KEY}"
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

    private fun callWeatherApi() {
        val thread = WeatherThread()
        thread.start()
        thread.join()
    }

    // TODO: RecyclerView 연결
    // Open Api 결과를 받고, JSON을 파싱하기 위한 부분!!
    inner class WeatherThread: Thread() {
        override fun run() {
            var numOfRows = 289 // 24시간 동안의 결과만 받으면 되기 때문에 해당 개수로 지정
            // TODO: 어제 날짜을 base date로 가져오고, 현재 시간에 맞춰 24시간 기준 가져오기 수정
            val site = "${OPEN_API_BASE_URL}1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey=${OPEN_API_SERVICE_KEY}&pageNo=1&numOfRows=${numOfRows}&dataType=JSON&base_date=20240922&base_time=0500&nx=55&ny=127"
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
            for (i in 0 until 24) {
                var tempResult = arrayListOf<String>()
                for (j in i * 12 until (i + 1) * 12) {
                    var category = item.getJSONObject(j).getString("category")
                    if (category == "TMP" || category == "SKY" || category == "PTY") {
                        tempResult.add(item.getJSONObject(j).getString("fcstValue"))
                    }
                }
                result.add(WeatherData(
                    tempResult[0], tempResult[1], tempResult[2], "latitude", "longitude",
                    item.getJSONObject(i * 12).getString("fcstDate"),
                    item.getJSONObject(i * 12).getString("fcstTime"))
                )
            }

            Log.d("ROUTE-TEST", "result = $result")
        }
    }

    private fun setAdapter() {
        bottomSheetDialog.placeRv.apply {
            this.adapter = placeRVAdapter
            this.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        }
        bottomSheetDialog.placeRv.itemAnimator = null
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        buttonView.typeface = if (isChecked) resources.getFont(R.font.pretendard_bold) else resources.getFont(R.font.pretendard_regular)
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
        writeViewModel.setPlaceCategoryResult()
        placeRVAdapter.removeAllItems()
        binding.routeConvenienceBottomSheet.bottomSheetCl.visibility = View.GONE
    }
}