package com.daval.routebox.presentation.ui.route.write.tracking

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.daval.routebox.domain.model.ActivityPictureAlbum
import java.io.File

private const val INDEX_MEDIA_ID = MediaStore.MediaColumns._ID
private const val INDEX_MEDIA_URI = MediaStore.MediaColumns.DATA
private const val INDEX_ALBUM_NAME = MediaStore.Images.Media.BUCKET_DISPLAY_NAME
private const val INDEX_DATE_ADDED = MediaStore.MediaColumns.DATE_ADDED

class RoutePictureAlbumViewModel: ViewModel() {
    private val _activityPictureAlbumList = MutableLiveData<ArrayList<ActivityPictureAlbum>>()
    val activityPictureAlbumList: LiveData<ArrayList<ActivityPictureAlbum>> = _activityPictureAlbumList

    private val _selectedPictureAlbumList = MutableLiveData<ArrayList<ActivityPictureAlbum>>()
    val selectedPictureAlbumList: LiveData<ArrayList<ActivityPictureAlbum>> = _selectedPictureAlbumList

    // 활동 추가 화면으로 데이터를 전송하기 위한 데이터
    private val _sendPictureList = MutableLiveData<ArrayList<String>>()
    val sendPictureList: LiveData<ArrayList<String>> = _sendPictureList

    init {
        _activityPictureAlbumList.value = arrayListOf(ActivityPictureAlbum(null, null))
        _selectedPictureAlbumList.value = arrayListOf()
        _sendPictureList.value = arrayListOf()
    }

    @SuppressLint("Range")
    fun getActivityPictureAlbumList(context: Context) {
        // ContentProvider가 관리하는 URI
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        // 선택한 이미지의 아이디, Uri 등 정보들을 저장하기 위한 변수
        val projection = arrayOf(
            INDEX_MEDIA_ID,
            INDEX_MEDIA_URI,
            INDEX_ALBUM_NAME,
            INDEX_DATE_ADDED
        )
        // 가져올 사진을 분류하기 위한 조건
        val selection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.Images.Media.SIZE + " > 0"
            else null
        // 사진을 선택한 순서를 유지하기 위함!
        val sortOrder = "$INDEX_DATE_ADDED DESC"
        // ContentResolver는 ContentProvider의 데이터에 접근할 수 있도록 돕는 Object!
        // ContentResolver가 ContentProvider에게 데이터를 요청 -> 응답하는 방식
        val cursor = context.contentResolver.query(uri, projection, selection, null, sortOrder)

        cursor?.let {
            while(cursor.moveToNext()) {
                val mediaPath = cursor.getString(cursor.getColumnIndex(INDEX_MEDIA_URI))
                _activityPictureAlbumList.value!!.add(
                    ActivityPictureAlbum(Uri.fromFile(File(mediaPath)), null)
                )
            }
        }

        cursor?.close()
    }

    fun resetActivityPictureAlbumList() {
        _activityPictureAlbumList.value = arrayListOf()
    }

    // 사진 선택 번호 당기기 위한 코드
    fun changeSelectedNumber() {
        for (i in 0 until _selectedPictureAlbumList.value!!.size) {
            selectedPictureAlbumList.value!![i].selectedNumber = i + 1
        }

        _selectedPictureAlbumList.value = selectedPictureAlbumList.value
    }

    fun setSendPictureList() {
        if (_selectedPictureAlbumList.value?.size != 0) {
            for (i in 0 until _selectedPictureAlbumList.value!!.size) {
                _sendPictureList.value!!.add(_selectedPictureAlbumList.value!![i].uri.toString())
            }
        }
    }

    fun resetSendPictureList() {
        _sendPictureList.value = arrayListOf()
    }
}