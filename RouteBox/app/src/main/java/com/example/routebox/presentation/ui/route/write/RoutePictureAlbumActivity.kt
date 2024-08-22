package com.example.routebox.presentation.ui.route.write

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.routebox.R
import com.example.routebox.databinding.ActivityRoutePictureAlbumBinding
import com.example.routebox.domain.model.ActivityPictureAlbum
import com.example.routebox.domain.model.DialogType
import com.example.routebox.presentation.ui.route.RouteActivityActivity
import com.example.routebox.presentation.ui.route.adapter.PictureAlbumRVAdapter
import com.example.routebox.presentation.utils.CommonPopupDialog
import com.example.routebox.presentation.utils.PopupDialogInterface

class RoutePictureAlbumActivity: AppCompatActivity(), PopupDialogInterface {

    private lateinit var binding: ActivityRoutePictureAlbumBinding
    private lateinit var albumRVAdapter: PictureAlbumRVAdapter
    private val viewModel: RoutePictureAlbumViewModel by viewModels()

    override fun onRestart() {
        super.onRestart()
        viewModel.resetActivityPictureAlbumList()
        viewModel.getActivityPictureAlbumList(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_route_picture_album)

        binding.apply {
            viewModel = this@RoutePictureAlbumActivity.viewModel
            lifecycleOwner = this@RoutePictureAlbumActivity
        }

        viewModel.getActivityPictureAlbumList(this)

        setAdapter()
        initObserve()
        initClickListener()
    }

    private fun initClickListener() {
        binding.backIv.setOnClickListener {
            finish()
        }

        binding.nextBtn.setOnClickListener {
            val intent = Intent(this, RouteActivityActivity::class.java)
            intent.putExtra("album", arrayListOf(
                viewModel.selectedPictureAlbumList.value!![0].uri.toString(),
                viewModel.selectedPictureAlbumList.value!![1].uri.toString(),
                viewModel.selectedPictureAlbumList.value!![2].uri.toString()
            ))
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun setAdapter() {
        albumRVAdapter = PictureAlbumRVAdapter()
        binding.pictureRv.adapter = albumRVAdapter
        binding.pictureRv.layoutManager = GridLayoutManager(this, 3)
        binding.pictureRv.itemAnimator = null
        albumRVAdapter.setPictureClickListener(object: PictureAlbumRVAdapter.MyItemClickListener {
            override fun onPictureItemClick(position: Int, data: ActivityPictureAlbum) {
                var selectedStatus = data.selectedNumber != null
                var selectedNumber: Int? = null
                
                if (selectedStatus) selectedNumber = null
                else selectedNumber = viewModel.selectedPictureAlbumList.value!!.size + 1

                // 선택된 상태인지 확인
                if (!selectedStatus) {
                    if (selectedNumber!! < 4) {
                        // 선택된 사진이 3개 미만이라면 추가
                        viewModel.selectedPictureAlbumList.value?.add(data)
                        changeSelectedPictureList(position, selectedStatus, selectedNumber)
                    } else {
                        // 사진 3개가 이미 선택된 상태라면
                        showPopupDialog()
                    }
                } else {
                    // 선택을 해제하는 경우
                    viewModel.selectedPictureAlbumList.value?.remove(data)
                    changeSelectedPictureList(position, selectedStatus, selectedNumber)

                    for (i in 0 until viewModel.selectedPictureAlbumList.value!!.size) {
                        albumRVAdapter.notifyItemChanged(albumRVAdapter.returnAllItems().indexOf(viewModel.selectedPictureAlbumList.value!![i]))
                    }
                }

                albumRVAdapter.notifyItemChanged(position)
            }
            // 카메라 실행
            override fun onCameraItemClick() {
                try {
                    val mInfo = packageManager.resolveActivity(Intent(MediaStore.ACTION_IMAGE_CAPTURE), 0)
                    val intent = Intent()
                    intent.setComponent(ComponentName(mInfo!!.activityInfo.packageName, mInfo.activityInfo.name))
                    intent.setAction(Intent.ACTION_MAIN)
                    intent.addCategory(Intent.CATEGORY_LAUNCHER)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.i("CAMERA-Exception", "Unable to launch camera: " + e)
                }
            }
        })
    }

    private fun initObserve() {
        viewModel.activityPictureAlbumList.observe(this) {
            if (viewModel.activityPictureAlbumList.value != null) {
                albumRVAdapter.addAllItems(viewModel.activityPictureAlbumList.value!!)
            }
        }
        viewModel.selectedPictureAlbumList.observe(this) { }
    }

    // 사진 선택 상태 변경
    private fun changeSelectedPictureList(position: Int, selectedStatus: Boolean, selectedNumber: Int?) {
        albumRVAdapter.changeStatus(position, selectedStatus, selectedNumber)
        viewModel.changeSelectedNumber()
        albumRVAdapter.notifyItemChanged(position)
    }

    private fun showPopupDialog() {
        val dialog = CommonPopupDialog(this@RoutePictureAlbumActivity, -1, String.format(resources.getString(R.string.route_max_picture)), null, getString(R.string.close))
        dialog.isCancelable = false // 배경 클릭 막기
        dialog.show(this.supportFragmentManager, "PopupDialog")
    }

    override fun onClickPositiveButton(id: Int) { }
}