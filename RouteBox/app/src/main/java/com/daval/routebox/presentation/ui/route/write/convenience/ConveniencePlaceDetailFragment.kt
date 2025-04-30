package com.daval.routebox.presentation.ui.route.write.convenience

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.daval.routebox.BuildConfig
import com.daval.routebox.databinding.FragmentConveniencePlaceDetailBinding
import com.daval.routebox.domain.model.ConvenienceCategoryResult
import com.daval.routebox.presentation.ui.route.adapter.ConveniencePlaceImageRVAdapter
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture

@RequiresApi(Build.VERSION_CODES.O)
class ConveniencePlaceDetailFragment : Fragment() {

    private lateinit var binding: FragmentConveniencePlaceDetailBinding

    lateinit var viewModel: RouteConvenienceViewModel

    private lateinit var placesClient: PlacesClient
    private lateinit var imageRVAdapter: ConveniencePlaceImageRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConveniencePlaceDetailBinding.inflate(inflater, container, false)

        initObserve()
        return binding.root
    }

    private fun setInit(placeInfo: ConvenienceCategoryResult) {
        Places.initialize(requireContext(), BuildConfig.GOOGLE_API_KEY)
        placesClient = Places.createClient(requireContext())

        binding.apply {
            this.placeInfo = placeInfo
            this.lifecycleOwner = this@ConveniencePlaceDetailFragment
        }

        Log.d("ConveniencePlaceBS", "place: ${placeInfo.placeName}, photoMetadataSize: ${placeInfo.photoMetadataList?.size}")

        if (!placeInfo.photoMetadataList.isNullOrEmpty()) {
            setAdapter()
            fetchPlacePhotos(placeInfo.photoMetadataList.take(IMAGE_MAX_SIZE))
        }
    }

    private fun setAdapter() {
        imageRVAdapter = ConveniencePlaceImageRVAdapter()
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
                if (filteredList.isNotEmpty()) {
                    imageRVAdapter.updateImageList(filteredList)
                }
            }
        }
    }

    private fun initObserve() {
        viewModel.selectedPlaceInfo.observe(viewLifecycleOwner) {
            if (it != null) {
                setInit(it)
            }
        }
    }

    companion object {
        private const val IMAGE_MAX_SIZE = 5
    }
}