package com.example.routebox.presentation.ui.route.write

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routebox.domain.model.ActivityPictureAlbum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private const val INDEX_MEDIA_ID = MediaStore.MediaColumns._ID
private const val INDEX_MEDIA_URI = MediaStore.MediaColumns.DATA
//private const val INDEX_ALBUM_NAME = MediaStore.Images.Media.BUCKET_DISPLAY_NAME
private const val INDEX_DATE_ADDED = MediaStore.MediaColumns.DATE_ADDED

class RoutePictureAlbumViewModel: ViewModel() {
    private val _ActivityPictureAlbumList = MutableLiveData<ArrayList<ActivityPictureAlbum>>()
    val ActivityPictureAlbumList: LiveData<ArrayList<ActivityPictureAlbum>> = _ActivityPictureAlbumList

    @SuppressLint("Range")
    fun getActivityPictureAlbumList(context: Context) {
        // ContentProvider가 관리하는 URI
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        // 선택한 이미지의 아이디, Uri 등 정보들을 저장하기 위한 변수
        val projection = arrayOf(
            INDEX_MEDIA_ID,
            INDEX_MEDIA_URI,
//            INDEX_ALBUM_NAME,
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

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // cursor는 어떤 데이터를 가리키고 있는지를 나타내는 포인터!
                cursor?.let {
                    // 사진 선택 번호를 입력하기 위한 변수
                    var id = 0
                    // 사진이 최대 3장인 조건
                    while (id < 4) {
                        // 선택한 사진의 URI를 가져와 저장
                        val mediaPath = cursor.getString(cursor.getColumnIndex(INDEX_MEDIA_URI))
                        _ActivityPictureAlbumList.value!!.add(
                            ActivityPictureAlbum(Uri.fromFile(File(mediaPath)), id++)
                        )
                        Log.d("ALBUM-TEST", "_ActivityPictureAlbumList=${_ActivityPictureAlbumList.value}")
                    }
                }

                cursor?.close()
            }
        }
    }

//    fun getCheckedImageUriList(): MutableList<String> {
//        val checkedImageUriList = mutableListOf<String>()
//        ActivityPictureAlbumList.value?.let {
//            for(ActivityPictureAlbum in ActivityPictureAlbumList.value!!) {
//                if(ActivityPictureAlbumList.isChecked) checkedImageUriList.add(ActivityPictureAlbum.uri.toString())
//            }
//        }
//        return checkedImageUriList
//    }
}