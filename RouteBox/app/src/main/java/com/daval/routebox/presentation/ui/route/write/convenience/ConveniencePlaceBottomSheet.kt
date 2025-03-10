package com.daval.routebox.presentation.ui.route.write.convenience

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daval.routebox.R
import com.daval.routebox.databinding.BottomSheetConveniencePlaceDetailBinding
import com.daval.routebox.domain.model.ConvenienceCategoryResult
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ConveniencePlaceBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetConveniencePlaceDetailBinding

    lateinit var viewModel: RouteConvenienceViewModel
    lateinit var placeInfo: ConvenienceCategoryResult

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
        binding.apply {
            placeInfo = this@ConveniencePlaceBottomSheet.placeInfo // 직접 할당
            lifecycleOwner = this@ConveniencePlaceBottomSheet
        }
    }
}