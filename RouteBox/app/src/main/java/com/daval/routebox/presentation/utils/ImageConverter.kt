package com.daval.routebox.presentation.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

/*
    ImageConverter를 사용하는 이유
    - 기존에는 File Uri를 그대로 노출해도 상관 없었지만, 안드로이드 7.0부터 정책 변경
    - So, File Uri를 그대로 사용하는 것이 아니라 이를 변환하여 파일 전달
 */
object ImageConverter {
    private suspend fun uriToFile(context: Context, uri: Uri): File? =
        withContext(Dispatchers.IO) {
            try {
                // context.cacheDir => 내부 저장소의 캐시 저장소
                // 다른 앱에서 접근할 수 없어 민감한 데이터를 저장할 때 사용!!
                val storage = context.cacheDir
                val fileName = "image_${UUID.randomUUID()}.jpg"
                val imgFile = File(storage, fileName)

                val bitmap = uriToBitmap(context, uri, imgFile)
                FileOutputStream(imgFile).use { outputStream ->
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
                return@withContext imgFile
            } catch (e: Exception) {
                Log.d("ImageConverter", "e = $e")
                return@withContext null
            }
        }

    private suspend fun uriToBitmap(context: Context, uri: Uri, file: File): Bitmap? =
        withContext(Dispatchers.IO) {
            var bitmap: Bitmap? = null
            try {
                // Android의 버전에 따른 코드 구분해주는 부분
                bitmap = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                    BitmapFactory.decodeFile(uri.toString())
                } else {
                    ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(context.contentResolver, uri)
                    )
                }

                // 메모리 부족 문제가 발생하지 않도록 이미지를 압축!
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))
                return@withContext bitmap
            } catch (e: IOException) {
                Log.d("ImageConverter", "e = $e")
                return@withContext null
            }
        }

    // bitmap
    private suspend fun fileToMultipartFile(context: Context, uri: Uri, partName: String): MultipartBody.Part? =
        withContext(Dispatchers.IO) {
            try {
                val file = uriToFile(context, uri)
                val requestFile = file?.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData(partName, file?.name, requestFile!!)
                return@withContext multipartBody
            } catch (e: Exception) {
                Log.d("ImageConverter", "e = $e")
                return@withContext null
            }
        }

    suspend fun getMultipartImgList(context: Context, imgList: MutableList<String>, partName: String): List<MultipartBody.Part?> =
        withContext(Dispatchers.IO) {
            // String List를 Uri List로 변환
            val list = imgList.map {
                fileToMultipartFile(context, Uri.parse(it), partName)
            }
            return@withContext list
        }
}