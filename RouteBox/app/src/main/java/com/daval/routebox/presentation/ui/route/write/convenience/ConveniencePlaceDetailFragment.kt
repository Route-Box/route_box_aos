package com.daval.routebox.presentation.ui.route.write.convenience

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.daval.routebox.databinding.BottomSheetConveniencePlaceBinding
//import com.daval.routebox.databinding.FragmentConveniencePlaceDetailBinding
import com.daval.routebox.domain.model.ConvenienceCategoryResult

//class ConveniencePlaceDetailFragment: Fragment() {
//
//    private lateinit var binding: FragmentConveniencePlaceDetailBinding
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = BottomSheetConveniencePlaceBinding.inflate(inflater, container, false)
//
//        // 장소 정보를 받아와서 화면에 표시
//        val placeInfo = arguments?.getSerializable(PLACE_INFO_KEY) as ConvenienceCategoryResult
//        binding.placeInfo = placeInfo
//
//        return binding.root
//    }
//
//    companion object {
//        const val PLACE_INFO_KEY = "placeInfo"
//        fun newInstance(placeInfo: ConvenienceCategoryResult): ConveniencePlaceDetailFragment {
//            val fragment = ConveniencePlaceDetailFragment()
//            val args = Bundle()
//            args.putSerializable(PLACE_INFO_KEY, placeInfo)
//            fragment.arguments = args
//            return fragment
//        }
//    }
//}
