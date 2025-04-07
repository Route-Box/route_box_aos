package com.daval.routebox.presentation.ui.route.write.convenience

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.daval.routebox.R
import com.daval.routebox.databinding.BottomSheetConveniencePlaceBinding

@RequiresApi(Build.VERSION_CODES.O)
class ConveniencePlaceBottomSheet(
    private val fragmentManager: FragmentManager,
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: RouteConvenienceViewModel
) {

    private lateinit var binding: BottomSheetConveniencePlaceBinding

    private val placeFragment = ConveniencePlaceFragment()
    private val placeDetailFragment = ConveniencePlaceDetailFragment()

    fun bind(binding: BottomSheetConveniencePlaceBinding) {
        this.binding = binding

        setup()
        observeViewModel()
    }

    private fun setup() {
        binding.apply {
            this.viewModel = this@ConveniencePlaceBottomSheet.viewModel
            this.lifecycleOwner = this@ConveniencePlaceBottomSheet.lifecycleOwner
        }

        placeFragment.viewModel = viewModel
        placeDetailFragment.viewModel = viewModel

        showFragment(placeFragment)
    }

    private fun observeViewModel() {
        viewModel.placeBottomSheetState.observe(lifecycleOwner) { state ->
            when (state) {
                PlaceSearchResultStatus.DEFAULT -> showFragment(placeFragment)
                PlaceSearchResultStatus.DETAIL -> showFragment(placeDetailFragment)
            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        fragmentManager.beginTransaction()
            .replace(R.id.convenience_content_frm, fragment)
            .commitAllowingStateLoss()
    }
}
