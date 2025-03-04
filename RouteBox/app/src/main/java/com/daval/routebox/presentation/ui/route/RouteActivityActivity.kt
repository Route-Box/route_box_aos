package com.daval.routebox.presentation.ui.route

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daval.routebox.R
import com.daval.routebox.databinding.ActivityRouteActivityBinding
import com.daval.routebox.domain.model.ActivityImage
import com.daval.routebox.domain.model.ActivityResult
import com.daval.routebox.domain.model.Category
import com.daval.routebox.domain.model.DialogType
import com.daval.routebox.domain.model.SearchActivityResult
import com.daval.routebox.presentation.ui.route.adapter.CategoryRVAdapter
import com.daval.routebox.presentation.ui.route.adapter.KakaoPlaceRVAdapter
import com.daval.routebox.presentation.ui.route.adapter.PictureRVAdapter
import com.daval.routebox.presentation.ui.route.write.tracking.RoutePictureAlbumActivity
import com.daval.routebox.presentation.ui.route.write.RouteWriteViewModel
import com.daval.routebox.presentation.utils.CommonPopupDialog
import com.daval.routebox.presentation.utils.PopupDialogInterface
import com.daval.routebox.presentation.utils.SharedPreferencesHelper
import com.daval.routebox.presentation.utils.SharedPreferencesHelper.Companion.APP_PREF_KEY
import com.daval.routebox.presentation.utils.picker.CalendarBottomSheet
import com.daval.routebox.presentation.utils.picker.DateClickListener
import com.daval.routebox.presentation.utils.picker.TimeChangedListener
import com.daval.routebox.presentation.utils.picker.TimePickerBottomSheet
import com.google.android.flexbox.FlexboxLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class RouteActivityActivity: AppCompatActivity(), DateClickListener, TimeChangedListener {

    private lateinit var binding: ActivityRouteActivityBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    private lateinit var placeRVAdapter: KakaoPlaceRVAdapter
    private var placeList: ArrayList<SearchActivityResult> = arrayListOf()
    private lateinit var categoryRVAdapter: CategoryRVAdapter
    private lateinit var imgRVAdapter: PictureRVAdapter
    private var imgList: ArrayList<String?> = arrayListOf(null)
    private var isEditMode: Boolean = false

    private val viewModel: RouteWriteViewModel by viewModels()

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    // 앨범 접근 권한 요청
    private fun checkVersion(): String {
        // SDK 버전에 따라 특정 버전 이상일 경우, READ_MEDIA_IMAGES, 아닐 경우, READ_EXTERNAL_STORAGE 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) return Manifest.permission.READ_MEDIA_IMAGES
        else return Manifest.permission.READ_EXTERNAL_STORAGE
    }

    // 권한 허용 요청
    private val galleryPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (ContextCompat.checkSelfPermission(this@RouteActivityActivity, it.toString()) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(this@RouteActivityActivity, RoutePictureAlbumActivity::class.java).putExtra("album", viewModel.activity.value?.activityImages?.toTypedArray())
            resultLauncher.launch(intent)
        } else {
            Log.d("ALBUM-PERMISSION", "권한 필요")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_route_activity)

        binding.apply {
            viewModel = this@RouteActivityActivity.viewModel
            lifecycleOwner = this@RouteActivityActivity
            method = this@RouteActivityActivity
        }

        sharedPreferencesHelper = SharedPreferencesHelper(getSharedPreferences(APP_PREF_KEY, Context.MODE_PRIVATE))

        initData()
        setAdapter()
        initClickListener()
        initEditTextListener()
        initObserve()

        // 선택한 사진을 받기 위한 launcher
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == RESULT_OK) {
                val imgList = result.data?.getStringArrayListExtra("album")

                for (i in 0 until imgList?.size!!) {
                    imgRVAdapter.addItem(imgList[i])
                    viewModel.activity.value?.activityImages?.add(imgList[i])
                    viewModel.imageList.add(ActivityImage(-1, imgList[i])) // 새로 추가한 이미지
                }
            }
        }
    }

    private fun initData() {
        viewModel.setRouteId(intent.getIntExtra("routeId", -1))
        isEditMode = intent.getBooleanExtra("isEdit", false)
        viewModel.setIsEditMode(isEditMode)
        if (isEditMode) { // 넘겨받은 활동 데이터 세팅
            viewModel.initActivityInEditAndSaveMode(intent.getSerializableExtra("activity") as ActivityResult)
        }

        if (intent.getBooleanExtra("checkIsContinuedActivity", false)) {
            if (sharedPreferencesHelper.getRouteActivity() != null) {
                viewModel.initActivityInEditAndSaveMode(sharedPreferencesHelper.getRouteActivity()!!)
            } else {
                viewModel.resetActivity()
            }
        }
    }

    private fun initObserve() {
        viewModel.activity.observe(this) {
            viewModel.checkBtnEnabled()
        }

        viewModel.isRequestSuccess.observe(this) {
            if (it == true) finish()
        }
    }

    private fun initEditTextListener() {
        binding.categoryEt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.checkBtnEnabled()
            }
            override fun afterTextChanged(p0: Editable?) { }
        })
    }

    private fun setAdapter() {
        setPlaceAdapter()
        setCategoryAdapter()
        setImageAdapter()
    }

    private fun setPlaceAdapter() {
        placeRVAdapter = KakaoPlaceRVAdapter(placeList)
        binding.placeRv.adapter = placeRVAdapter
        binding.placeRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        placeRVAdapter.setPlaceClickListener(object: KakaoPlaceRVAdapter.MyItemClickListener {
            override fun onItemClick(place: SearchActivityResult) {
                binding.searchEt.setText(place.place_name)
                viewModel.activity.value?.apply {
                    locationName = place.place_name
                    address = place.address_name
                    longitude = place.x
                    latitude = place.y
                }
                viewModel.setPlaceSearchResult(arrayListOf())
                viewModel.setPlaceSearchMode(false)
                binding.searchEt.clearFocus()
            }
        })
        // 페이징 처리를 통해 새로운 장소 15개를 얻으면 placeRVAdapter에 해당 내용을 전송 -> 뷰 업데이트
        viewModel.placeSearchResult.observe(this) {
            // 다시 검색했을 경우, RecyclerView 데이터 새로 추가
            if (viewModel.placeSearchPage.value == 1) placeRVAdapter.resetAllItems(viewModel.placeSearchResult.value!!)
            else {
                placeRVAdapter.addAllItems(viewModel.placeSearchResult.value!!)
            }
        }
        // Kakao 위치 검색의 경우, 15개씩 결과값 리턴
        // So, 만약 스크롤 최하단에 도착했다면, 새로운 15개의 값을 얻기 위한 API 호출
        binding.placeRv.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // 스크롤 최하단 확인
                if (!binding.placeRv.canScrollVertically(1)) {
                    if (!viewModel.isKeywordEndPage.value!!) {
                        viewModel.setPlaceSearchPage(viewModel.placeSearchPage.value!! + 1)
                        viewModel.pagingPlace()
                    }
                }
            }
        })
    }

    private fun setCategoryAdapter() {
        categoryRVAdapter = CategoryRVAdapter()
        binding.categoryRv.adapter = categoryRVAdapter
        binding.categoryRv.layoutManager = FlexboxLayoutManager(this)
        binding.categoryRv.itemAnimator = null
        categoryRVAdapter.setCategoryClickListener(object: CategoryRVAdapter.MyItemClickListener {
            override fun onItemClick(position: Int, isSelected: Boolean) { // 카테고리 선택
                viewModel.resetCategory()
                if (categoryRVAdapter.getItem(position) == Category.ETC) {
                    viewModel.setCategoryETC(true)
                } else {
                    viewModel.setCategoryETC(false)
                    viewModel.activity.value?.category = categoryRVAdapter.getItem(position).categoryName
                }
                categoryRVAdapter.setSelectedIndex(position)

                viewModel.checkBtnEnabled()
            }
        })
        if (!viewModel.activity.value?.category.isNullOrEmpty()) { // 카테고리 아이템 선택
            categoryRVAdapter.setSelectedName(viewModel.activity.value?.category!!)
        }
    }

    private fun setImageAdapter() {
        imgList.addAll(viewModel.imageList.map { it.url })
        imgRVAdapter = PictureRVAdapter(imgList)
        binding.pictureRv.adapter = imgRVAdapter
        binding.pictureRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.pictureRv.itemAnimator = null
        imgRVAdapter.setPictureClickListener(object: PictureRVAdapter.MyItemClickListener {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onPlusItemClick(position: Int) {
                if (ContextCompat.checkSelfPermission(this@RouteActivityActivity, checkVersion()) == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(this@RouteActivityActivity, RoutePictureAlbumActivity::class.java).putExtra("album", viewModel.activity.value?.activityImages?.toTypedArray())
                    resultLauncher.launch(intent)
                } else {
                    galleryPermissionLauncher.launch(checkVersion())
                }
            }
            override fun onPictureDeleteIconClick(position: Int) {
                imgRVAdapter.removeItem(position)
                viewModel.activity.value?.activityImages!!.removeAt(position - 1)
                val activityImage = viewModel.imageList[position - 1]
                if (activityImage.id > 0) { // 업로드가 이미 되어있는 사진
                    viewModel.deletedImageIds.add(activityImage.id)
                }
            }
        })
    }

    private fun initClickListener() {
        binding.closeIv.setOnClickListener {
            finish()
        }

        binding.dateContent.setOnClickListener {
            showCalendarBottomSheet(viewModel.date.value!!)
        }

        binding.startTimeTv.setOnClickListener {
            showTimePickerBottomSheet(true, viewModel.startTimePair.value)
        }

        binding.endTimeTv.setOnClickListener {
            showTimePickerBottomSheet(false, viewModel.endTimePair.value)
        }

        binding.categoryEraseIv.setOnClickListener { // 카테고리 EditText 비우기
            viewModel.resetCategory()
        }

        // 완료 버튼
        binding.doneBtn.setOnClickListener {
            if (viewModel.isEditMode.value!!) { // 수정 모드
                viewModel.editActivity(this)
            } else { // 생성 모드
                viewModel.addActivity(this)
                // TODO: 생성 성공했을 때, 임시저장 데이터 삭제
            }
        }

        // 키보드 검색 버튼
        binding.searchEt.setOnEditorActionListener { it, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) { // 검색 버튼 클릭
                searchPlace(it) // 장소 검색
                true
            } else {
                false
            }
        }
    }

    fun searchPlace(view: View) {
        viewModel.searchPlace() // 장소 검색 진행
        hideKeyboard() // 키보드 내리기
        view.clearFocus() // EditText 포커스 해제
    }

    private fun hideKeyboard() {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEt.windowToken, 0)
    }

    private fun showCalendarBottomSheet(date: LocalDate) {
        val calendarBottomSheet = CalendarBottomSheet(this,
            setPrevDateDisable = false,
            isStartDate = true,
            initialDate = date
        )
        calendarBottomSheet.run {
            setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogStyle)
        }
        calendarBottomSheet.show(this.supportFragmentManager, calendarBottomSheet.tag)
    }

    private fun showTimePickerBottomSheet(isStartTime: Boolean, initTime: Pair<Int, Int>?) {
        val pickerBottomSheet = TimePickerBottomSheet(
            this,
            isStartTime,
            initTime?.first ?: Calendar.getInstance().get(Calendar.HOUR_OF_DAY), // 선택한 시간 정보가 없다면 현재 hour로 피커 초기화
            initTime?.second ?: Calendar.getInstance().get(Calendar.MINUTE) // 선택한 시간 정보가 없다면 현재 minute로 피커 초기화
        )
        pickerBottomSheet.run {
            setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogStyle)
        }
        pickerBottomSheet.show(this.supportFragmentManager, pickerBottomSheet.tag)
    }

    override fun onDateReceived(isStartDate: Boolean, date: LocalDate) {
        viewModel.updateDate(date)
    }

    override fun onTimeSelected(isStartTime: Boolean, hour: Int, minute: Int) {
        viewModel.updateTime(isStartTime, Pair(hour, minute))
    }

    // 루트 활동 임시저장
    override fun onPause() {
        super.onPause()
        val sharedPreferencesHelper = SharedPreferencesHelper(getSharedPreferences(APP_PREF_KEY, MODE_PRIVATE))

        // 루트 활동 수정 모드가 아닐 경우, 작성한 데이터 임시저장
        if (!isEditMode) {
            // 입력된 데이터가 있을 때만 데이터 임시저장
            if (viewModel.activity.value?.locationName != ""
                || viewModel.activity.value?.startTime != "" || viewModel.activity.value?.endTime != ""
                || viewModel.activity.value?.category != "" || viewModel.activity.value?.description != "" || viewModel.activity.value?.activityImages?.size != 0) {

                // ActivityImage를 ArrayList<String>에서 ArrayList<ActivityImage>로 변경
                val activityTemp = viewModel.returnActivity()
                var activityImages = arrayListOf<ActivityImage>()
                if (viewModel.activity.value!!.activityImages.size > 0) {
                    for (i in 0 until viewModel.activity.value!!.activityImages.size) {
                        activityImages.add(ActivityImage(i, viewModel.activity.value!!.activityImages[i]))
                    }
                }
                activityTemp.activityImages = activityImages

                sharedPreferencesHelper.setRouteActivity(activityTemp)
            } else {
                sharedPreferencesHelper.setRouteActivity(null)
            }
        }
    }
}