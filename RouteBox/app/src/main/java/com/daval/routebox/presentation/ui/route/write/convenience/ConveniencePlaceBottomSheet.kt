package com.daval.routebox.presentation.ui.route.write.convenience

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.daval.routebox.BuildConfig
import com.daval.routebox.R
import com.daval.routebox.databinding.BottomSheetConveniencePlaceDetailBinding
import com.daval.routebox.domain.model.ConvenienceCategoryResult
import com.daval.routebox.presentation.ui.route.adapter.ConveniencePlaceImageRVAdapter
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture

class ConveniencePlaceBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetConveniencePlaceDetailBinding

    lateinit var viewModel: RouteConvenienceViewModel
    lateinit var placeInfo: ConvenienceCategoryResult

    private lateinit var placesClient: PlacesClient
    private lateinit var imageRVAdapter: ConveniencePlaceImageRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetConveniencePlaceDetailBinding.inflate(inflater, container, false)

        // placeInfo가 초기화된 후에 setInit() 호출
        if (::placeInfo.isInitialized) {
            setInit()
        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), R.style.TransparentBottomSheetDialogStyle)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        return dialog
    }

    private fun setInit() {
        Places.initialize(requireContext(), BuildConfig.GOOGLE_API_KEY)
        placesClient = Places.createClient(requireContext())

        binding.apply {
            placeInfo = this@ConveniencePlaceBottomSheet.placeInfo
            lifecycleOwner = this@ConveniencePlaceBottomSheet
        }

        Log.d("ConveniencePlaceBS", "photoMetadataSize: ${placeInfo.photoMetadataList?.size}")

        if (!placeInfo.photoMetadataList.isNullOrEmpty()) {
            setAdapter()
            fetchPlacePhotos(placeInfo.photoMetadataList!!.take(IMAGE_MAX_SIZE))
        }
    }

    private fun setAdapter() {
        imageRVAdapter = ConveniencePlaceImageRVAdapter()

        binding.placeImageRv.apply {
            this.adapter = imageRVAdapter
            this.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun fetchPlacePhotos(photoMetadataList: List<PhotoMetadata>) {
        val deferredResults = ArrayList<CompletableFuture<Bitmap>>()

        photoMetadataList.forEach { photoMetadata ->
            val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .setMaxWidth(500) // Optional
                .setMaxHeight(300) // Optional
                .build()

            val future = CompletableFuture<Bitmap>()
            placesClient.fetchPhoto(photoRequest)
                .addOnSuccessListener { response ->
                    val bitmap = response.bitmap
                    future.complete(bitmap)
                }

            deferredResults.add(future)
        }

        // 모든 요청이 완료될 때까지 대기
        CoroutineScope(Dispatchers.IO).launch {
            val results = deferredResults.map { it.get() }

            // null을 제거하고 리스트에 추가
            val filteredList = results.filterNotNull()

            withContext(Dispatchers.Main) {
                imageRVAdapter.updateImageList(filteredList)
            }
        }
    }


    companion object {
        private const val IMAGE_MAX_SIZE = 5
    }
}