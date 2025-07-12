package com.daval.routebox.presentation.ui.user

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.daval.routebox.R
import com.daval.routebox.databinding.FragmentInquiryBinding
import com.daval.routebox.presentation.ui.route.edit.RouteEditBaseActivity
import com.daval.routebox.presentation.ui.route.write.tracking.RoutePictureAlbumActivity
import com.daval.routebox.presentation.ui.user.adapter.InquiryPictureRVAdapter
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InquiryFragment : Fragment() {
    private lateinit var binding: FragmentInquiryBinding

    private val viewModel: InquiryViewModel by activityViewModels()
    private lateinit var imgRVAdapter: InquiryPictureRVAdapter
    private var imgList: ArrayList<String?> = arrayListOf(null)

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
        if (ContextCompat.checkSelfPermission(requireActivity(), it.toString()) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(requireActivity(), RoutePictureAlbumActivity::class.java)
            // TODO: API 나오면 수정
                // .putExtra("album", viewModel.activity.value?.activityImages?.toTypedArray())
            resultLauncher.launch(intent)
        } else {
            Log.d("ALBUM-PERMISSION", "권한 필요")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInquiryBinding.inflate(inflater, container, false)

        binding.apply {
            viewModel = this@InquiryFragment.viewModel
            lifecycleOwner = this@InquiryFragment
        }

        initClickListener()
        setAdapter()
        initObserve()

        return binding.root
    }

    private fun initClickListener() {
        binding.inquiryStatusIv.setOnClickListener {
            showMenu(it)
        }
    }

    private fun setAdapter() {
        imgRVAdapter = InquiryPictureRVAdapter(imgList)
        binding.pictureRv.adapter = imgRVAdapter
        binding.pictureRv.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.pictureRv.itemAnimator = null
        imgRVAdapter.setPictureClickListener(object: InquiryPictureRVAdapter.MyItemClickListener {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onPlusItemClick(position: Int) {
                if (ContextCompat.checkSelfPermission(requireActivity(), checkVersion()) == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(requireActivity(), RoutePictureAlbumActivity::class.java)
                    // TODO: API 나오면 수정
                        // .putExtra("album", viewModel.activity.value?.activityImages?.toTypedArray())
                    resultLauncher.launch(intent)
                } else {
                    galleryPermissionLauncher.launch(checkVersion())
                }
            }
            override fun onPictureDeleteIconClick(position: Int) {
                imgRVAdapter.removeItem(position)
                // TODO: API 나오면 수정
//                viewModel.activity.value?.activityImages!!.removeAt(position - 1)
//                val activityImage = viewModel.imageList[position - 1]
//                if (activityImage.id > 0) { // 업로드가 이미 되어있는 사진
//                    viewModel.deletedImageIds.add(activityImage.id)
//                }
            }
        })
    }

    private fun initObserve() {

    }

    private fun showMenu(view: View) {
        val popupMenu = PopupMenu(activity, view)
        popupMenu.inflate(R.menu.inquiry_option_menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_error -> { // 오류
                    viewModel.inquiryStatus.value = resources.getString(R.string.error)
                    true
                }
                R.id.menu_user_info -> { // 회원정보
                    viewModel.inquiryStatus.value = resources.getString(R.string.inquiry_user_info)
                    true
                }
                R.id.menu_etc -> { // 기타
                    viewModel.inquiryStatus.value = resources.getString(R.string.report_reason_etc)
                    true
                }
                else -> { false }
            }
        }
        popupMenu.show()
    }
}