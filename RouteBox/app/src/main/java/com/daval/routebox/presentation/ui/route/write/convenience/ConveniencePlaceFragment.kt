package com.daval.routebox.presentation.ui.route.write.convenience

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.daval.routebox.databinding.FragmentConveniencePlaceBinding
import com.daval.routebox.domain.model.ConvenienceCategoryResult
import com.daval.routebox.presentation.ui.route.adapter.ConveniencePlaceRVAdapter

@RequiresApi(Build.VERSION_CODES.O)
class ConveniencePlaceFragment : Fragment() {

    private lateinit var binding: FragmentConveniencePlaceBinding

    lateinit var viewModel: RouteConvenienceViewModel

    private var placeList = arrayListOf<ConvenienceCategoryResult>()
    private val placeRVAdapter = ConveniencePlaceRVAdapter(placeList)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConveniencePlaceBinding.inflate(inflater, container, false)

        setInit()
        return binding.root
    }

    private fun setInit() {
        if (::viewModel.isInitialized) {

            binding.apply {
                viewModel = this@ConveniencePlaceFragment.viewModel
                lifecycleOwner = this@ConveniencePlaceFragment
            }

            setAdapter()
        }
    }

    private fun setAdapter() {
        binding.placeRv.apply {
            this.adapter = placeRVAdapter
            this.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        }
        binding.placeRv.itemAnimator = null

        initObserve()

        placeRVAdapter.setItemClickListener(object : ConveniencePlaceRVAdapter.MyItemClickListener {
            override fun onItemClick(placeInfo: ConvenienceCategoryResult) {
                viewModel.setPlaceInfo(placeInfo) // 장소 상세 정보 설정
                viewModel.setPlaceDetailFragment() // 장소 상세 화면
            }
        })
    }

    private fun initObserve() {
        viewModel.placeCategoryResult.observe(viewLifecycleOwner) {
            placeRVAdapter.addAllItems(it)
        }
    }
}