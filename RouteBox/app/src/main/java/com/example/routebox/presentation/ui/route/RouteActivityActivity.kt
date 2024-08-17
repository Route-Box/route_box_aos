package com.example.routebox.presentation.ui.route

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
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
import java.io.File
import java.time.LocalDate
import java.util.Calendar


@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class RouteActivityActivity: AppCompatActivity(), DateClickListener, TimeChangedListener {

    private lateinit var binding: ActivityRouteActivityBinding

    private lateinit var placeRVAdapter: KakaoPlaceRVAdapter
    private var placeList: ArrayList<SearchActivityResult> = arrayListOf<SearchActivityResult>()
    private lateinit var categoryRVAdapter: CategoryRVAdapter
    private lateinit var imgRVAdapter: PictureRVAdapter
    private var imgList = arrayListOf<String?>(null)

    private lateinit var viewModel: RouteWriteViewModel
    private lateinit var albumViewModel: RoutePictureAlbumViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_route_activity)
        viewModel = ViewModelProvider(this).get(RouteWriteViewModel::class.java)
        albumViewModel = ViewModelProvider(this).get(RoutePictureAlbumViewModel::class.java)

        binding.apply {
            binding.viewModel = this@RouteActivityActivity.viewModel
            lifecycleOwner = this@RouteActivityActivity
        }

        setAdapter()
        initClickListener()
        initEditTextListener()
        setAlbumPermission()
    }

    // 앨범 접근 권한 체크 (허용 여부 체크)
    private fun checkAlbumPermission(): Int {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
    }

    lateinit var imageLauncher: ActivityResultLauncher<Intent>
    lateinit var galleryPermissionLauncher: ActivityResultLauncher<String>
    lateinit var version: String

    private fun setAlbumPermission() {
        // SDK 버전에 따라 특정 버전 이상일 경우, READ_MEDIA_IMAGES, 아닐 경우, READ_EXTERNAL_STORAGE 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            version = Manifest.permission.READ_MEDIA_IMAGES
        else version = Manifest.permission.READ_EXTERNAL_STORAGE

        // 앨범 접근 권한 요청
        var imageFile = File("")
        imageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri = result.data?.data
                imageUri?.let {
                    imageFile = File(it.toString())
                    Log.d("ALBUM-PERMISSION", imageFile.toString())
                }
            }
        }
        galleryPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            Log.d("ALBUM-PERMISSION", "it = $it")
            if (it) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.setDataAndType(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*"
                )
                imageLauncher.launch(intent)
            } else Log.d("ALBUM-PERMISSION", "deny")
        }
    }

    private fun setAdapter() {
        placeRVAdapter = KakaoPlaceRVAdapter(placeList)
        binding.placeRv.adapter = placeRVAdapter
        binding.placeRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        placeRVAdapter.setPlaceClickListener(object: KakaoPlaceRVAdapter.MyItemClickListener {
            override fun onItemClick(place: SearchActivityResult) {
                binding.searchEt.setText(place.place_name)
                binding.searchEt.setTypeface(Typeface.DEFAULT_BOLD)

                viewModel.placeSearchResult.value = arrayListOf()
            }
        })
        // 페이징 처리를 통해 새로운 장소 15개를 얻으면 placeRVAdapter에 해당 내용을 전송 -> 뷰 업데이트
        viewModel.placeSearchResult.observe(this) {
            // 다시 검색했을 경우, RecyclerView 데이터 새로 추가
            if (viewModel.placeSearchPage.value == 1) placeRVAdapter.resetAllItems(viewModel.placeSearchResult.value!!)
            else placeRVAdapter.addAllItems(viewModel.placeSearchResult.value!!)
        }
        // Kakao 위치 검색의 경우, 15개씩 결과값 리턴
        // So, 만약 스크롤 최하단에 도착했다면, 새로운 15개의 값을 얻기 위한 API 호출
        binding.placeRv.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // 스크롤 최하단 확인
                if (!binding.placeRv.canScrollVertically(1)) {
                    if (!viewModel.isEndPage.value!!) {
                        viewModel.placeSearchPage.value = viewModel.placeSearchPage.value!! + 1
                        viewModel.pagingPlace()
                    }
                }
            }
        })

        categoryRVAdapter = CategoryRVAdapter()
        binding.categoryRv.adapter = categoryRVAdapter
        binding.categoryRv.layoutManager = FlexboxLayoutManager(this)
        categoryRVAdapter.setCategoryClickListener(object: CategoryRVAdapter.MyItemClickListener {
            override fun onItemClick(position: Int, isSelected: Boolean) {
                // TOOD: 선택되어 있는 카테고리의 경우, 해제
                if (categoryRVAdapter.getItem(position) == Category.ETC) {
                    viewModel.setCategoryETC("")
                } else viewModel.setCategoryETC(null)
            }
        })

        imgRVAdapter = PictureRVAdapter(imgList)
        binding.pictureRv.adapter = imgRVAdapter
        binding.pictureRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imgRVAdapter.setPictureClickListener(object: PictureRVAdapter.MyItemClickListener {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onPlusItemClick(position: Int) {
//                imgRVAdapter.addItem("https://blog.kakaocdn.net/dn/YyLsE/btqEtFpJtdS/yAW5hkfVkg9YnYrNCzTKDk/img.jpg")
//                albumViewModel.getActivityPictureAlbumList(this@RouteActivityActivity)

                galleryPermissionLauncher.launch(version)

//                when {
//                    ContextCompat.checkSelfPermission(this@RouteActivityActivity, version) == PackageManager.PERMISSION_GRANTED -> {
//                        val startCustomAlbum = Intent(this, RoutePictureAlbumActivity::class.java)
//                        activityForResult.launch(startCustomAlbum)
////                    startActivity(startCustomAlbum)
//                        //스토리지 읽기 권한이 허용이면 커스텀 앨범 띄워주기
//                        //권한 있을 경우 : PERMISSION_GRANTED
//                        //권한 없을 경우 : PERMISSION_DENIED
//                    }
//
//                    shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
//                        //다이얼로그를 띄워 권한팝업을 허용하여야 접근 가능하다는 사실을 알려줌
//                        showPermissionAlertDialog()
//                    }
//
//                    else -> {
//                        //권한 요청
//                        requestPermissions(
//                            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
//                            PERMISSION_CODE
//                        )
//                    }
//                }
            }
            override fun onPictureItemClick(position: Int) {
//                imgRVAdapter.removeItem(position)
            }
        })
    }

    private fun initClickListener() {
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
            binding.categoryEt.setText("")
        }
    }

    private fun initEditTextListener() {
        binding.searchEt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.searchEt.setTypeface(Typeface.DEFAULT)
                viewModel.setPlaceSearchKeyword(binding.searchEt.text.toString())
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