package com.example.routebox.presentation.ui.route

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.example.routebox.R
import com.example.routebox.databinding.ActivityRouteActivityBinding
import com.example.routebox.domain.model.Category
import com.example.routebox.domain.model.SearchActivityResult
import com.example.routebox.presentation.ui.route.adapter.CategoryRVAdapter
import com.example.routebox.presentation.ui.route.adapter.KakaoPlaceRVAdapter
import com.example.routebox.presentation.ui.route.adapter.PictureRVAdapter
import com.example.routebox.presentation.ui.route.write.RoutePictureAlbumActivity
import com.example.routebox.presentation.ui.route.write.RoutePictureAlbumViewModel
import com.example.routebox.presentation.ui.route.write.RouteWriteViewModel
import com.example.routebox.presentation.utils.picker.CalendarBottomSheet
import com.example.routebox.presentation.utils.picker.DateClickListener
import com.example.routebox.presentation.utils.picker.TimeChangedListener
import com.example.routebox.presentation.utils.picker.TimePickerBottomSheet
import com.google.android.flexbox.FlexboxLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.util.Calendar


@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class RouteActivityActivity: AppCompatActivity(), DateClickListener, TimeChangedListener {

    private lateinit var binding: ActivityRouteActivityBinding

    private lateinit var placeRVAdapter: KakaoPlaceRVAdapter
    private var placeList: ArrayList<SearchActivityResult> = arrayListOf()
    private lateinit var categoryRVAdapter: CategoryRVAdapter
    private lateinit var imgRVAdapter: PictureRVAdapter
    private var imgList = arrayListOf<Uri?>(null)

    private val viewModel: RouteWriteViewModel by viewModels()
    private val albumViewModel: RoutePictureAlbumViewModel by viewModels()

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
            startActivity(Intent(this@RouteActivityActivity, RoutePictureAlbumActivity::class.java))
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
        }

        setAdapter()
        initClickListener()
        initEditTextListener()

        // 선택한 사진을 받기 위한 launcher
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == RESULT_OK) {
                for (i in 0 until result.data?.getStringArrayListExtra("album")!!.size) {
                    imgRVAdapter.addItem(Uri.parse(result.data?.getStringArrayListExtra("album")!![i]))
                }
            }
        }

//        if (viewModel.activity.value?.activityImages != null) {
//            val arrayList = viewModel.activity.value?.activityImages!!.toCollection(ArrayList<String>())
//            imgRVAdapter.addAllItems(arrayList)
//        }
//
//        if (albumViewModel.sendPictureList.value != null) {
//            imgRVAdapter.addAllItems(albumViewModel.sendPictureList.value!!)
//            viewModel.activity.value?.activityImages = albumViewModel.sendPictureList.value!!.toTypedArray()
//            albumViewModel.resetSendPictureList()
//        }
    }

    private fun setAdapter() {
        placeRVAdapter = KakaoPlaceRVAdapter(placeList)
        binding.placeRv.adapter = placeRVAdapter
        binding.placeRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        placeRVAdapter.setPlaceClickListener(object: KakaoPlaceRVAdapter.MyItemClickListener {
            override fun onItemClick(place: SearchActivityResult) {
                binding.searchEt.setText(place.place_name)
                viewModel.activity.value?.locationName = place.place_name
                viewModel.setPlaceSearchResult(arrayListOf())
                viewModel.setPlaceSearchMode(false)
                viewModel.activity.value?.address = place.address_name

                viewModel.checkBtnEnabled()
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
                    if (!viewModel.isEndPage.value!!) {
                        viewModel.setPlaceSearchPage(viewModel.placeSearchPage.value!! + 1)
                        viewModel.pagingPlace()
                    }
                }
            }
        })

        categoryRVAdapter = CategoryRVAdapter()
        binding.categoryRv.adapter = categoryRVAdapter
        binding.categoryRv.layoutManager = FlexboxLayoutManager(this)
        binding.categoryRv.itemAnimator = null
        categoryRVAdapter.setCategoryClickListener(object: CategoryRVAdapter.MyItemClickListener {
            override fun onItemClick(position: Int, isSelected: Boolean) {
                if (categoryRVAdapter.getItem(position) == Category.ETC) {
                    viewModel.setCategoryETC(true)
                    viewModel.activity.value?.category = binding.categoryEt.text.toString()
                } else {
                    binding.categoryEt.setText("")
                    viewModel.setCategoryETC(false)
                    viewModel.activity.value?.category = categoryRVAdapter.getItem(position).categoryName
                }
                categoryRVAdapter.setSelectedIndex(position)

                viewModel.checkBtnEnabled()
            }
        })
        if (viewModel.activity.value?.category != "") {
            categoryRVAdapter.setSelectedName(viewModel.activity.value?.category!!)
        }

        imgRVAdapter = PictureRVAdapter(imgList)
        binding.pictureRv.adapter = imgRVAdapter
        binding.pictureRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.pictureRv.itemAnimator = null
        imgRVAdapter.setPictureClickListener(object: PictureRVAdapter.MyItemClickListener {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onPlusItemClick(position: Int) {
                if (ContextCompat.checkSelfPermission(this@RouteActivityActivity, checkVersion()) == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(this@RouteActivityActivity, RoutePictureAlbumActivity::class.java)
                    resultLauncher.launch(intent)
//                    findNavController().navigate(R.id.action_routeActivityFragment_to_routePictureAlbumFragment)
//                    startActivity(Intent(this@RouteActivityActivity, RoutePictureAlbumActivity::class.java))
                } else {
                    galleryPermissionLauncher.launch(checkVersion())
                }
            }
            override fun onPictureItemClick(position: Int) {
                imgRVAdapter.removeItem(position)
            }
        })
    }

    private fun initClickListener() {
        binding.closeIv.setOnClickListener {
            viewModel.resetActivityResult()
            finish()
        }

        binding.dateContent.setOnClickListener {
            showCalendarBottomSheet(true, viewModel.date.value!!)
        }

        binding.startTimeTv.setOnClickListener {
            showTimePickerBottomSheet(true, viewModel.startTimePair.value)
        }

        binding.endTimeTv.setOnClickListener {
            showTimePickerBottomSheet(false, viewModel.endTimePair.value)
        }

        binding.categoryEraseIv.setOnClickListener {
            viewModel.activity.value?.category = ""
            binding.categoryEt.setText("")
        }

        binding.nextBtn.setOnClickListener {
            finish()
        }
    }

    private fun initEditTextListener() {
        binding.searchEt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.activity.value?.locationName = ""
                viewModel.setPlaceSearchKeyword(binding.searchEt.text.toString())
                viewModel.checkBtnEnabled()
            }
            override fun afterTextChanged(p0: Editable?) { }
        })
        binding.categoryEt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.activity.value?.category = binding.categoryEt.text.toString()
                viewModel.checkBtnEnabled()
            }
            override fun afterTextChanged(p0: Editable?) { }
        })
        binding.locationContentEt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.activity.value?.description = binding.locationContentEt.text.toString()
                binding.locationContentLength.text = binding.locationContentEt.text.length.toString()
                viewModel.checkBtnEnabled()
            }
            override fun afterTextChanged(p0: Editable?) { }
        })
    }

    private fun showCalendarBottomSheet(isStartDate: Boolean, date: LocalDate) {
        val calendarBottomSheet = CalendarBottomSheet(this, isStartDate, date)
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
}