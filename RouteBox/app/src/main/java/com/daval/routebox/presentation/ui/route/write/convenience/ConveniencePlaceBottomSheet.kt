package com.daval.routebox.presentation.ui.route.write.convenience

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.daval.routebox.R
import com.daval.routebox.databinding.BottomSheetConveniencePlaceBinding

class ConveniencePlaceBottomSheet(
    private val fragmentManager: FragmentManager,
    private val viewModel: RouteConvenienceViewModel
) {

    private lateinit var binding: BottomSheetConveniencePlaceBinding

    private val placeFragment = ConveniencePlaceFragment()

    fun bind(binding: BottomSheetConveniencePlaceBinding) {
        this.binding = binding
        setup()
    }

    private fun setup() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = binding.root.findViewTreeLifecycleOwner()

        placeFragment.viewModel = viewModel

        fragmentManager.beginTransaction()
            .replace(R.id.convenience_content_frm, placeFragment)
            .commitNowAllowingStateLoss()
    }
}
