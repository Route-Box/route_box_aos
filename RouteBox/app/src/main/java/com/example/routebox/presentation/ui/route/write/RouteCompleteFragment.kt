package com.example.routebox.presentation.ui.route.write

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.routebox.R
import com.example.routebox.databinding.FragmentRouteCompleteBinding
import com.example.routebox.databinding.FragmentRouteEditBinding
import com.example.routebox.domain.model.FilterOption
import com.example.routebox.presentation.ui.common.routeStyle.FilterOptionClickListener
import com.example.routebox.presentation.ui.common.routeStyle.RouteStyleFragment
import com.example.routebox.presentation.ui.route.RouteCompleteActivity
import com.example.routebox.presentation.ui.route.edit.RouteEditViewModel

class RouteCompleteFragment : Fragment(), FilterOptionClickListener {

    private lateinit var binding: FragmentRouteCompleteBinding

    private val viewModel: RouteCompleteViewModel by activityViewModels()
    private val editViewModel: RouteEditViewModel by activityViewModels()

    private lateinit var routeStyleFragment: RouteStyleFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRouteCompleteBinding.inflate(inflater, container, false)

        binding.apply {
            viewModel = this@RouteCompleteFragment.viewModel
            lifecycleOwner = this@RouteCompleteFragment
        }

        initClickListeners()
        initObserve()

        editViewModel.setStepId(1)

        return binding.root
    }

    private fun initClickListeners() {
        // 활동 수정 버튼
        binding.routeCompleteActivityEditBtn.setOnClickListener {
            // 활동 수정 화면으로 이동
            findNavController().navigate(R.id.action_routeCompleteFragment_to_routeEditActivityFragment)
        }
        // 완료 버튼
        binding.routeCompleteDoneBtn.setOnClickListener {
            //TODO: 루트 제목, 내용 저장 진행
            startActivity(Intent(requireActivity(), RouteCompleteActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun initObserve() {
        viewModel.routeTitle.observe(viewLifecycleOwner) {
            viewModel.checkContentButtonEnable()
        }

        viewModel.routeContent.observe(viewLifecycleOwner) {
            viewModel.checkContentButtonEnable()
        }
    }

    override fun onOptionItemClick(option: FilterOption, isSelected: Boolean) {
        viewModel.updateSelectedOption(option, isSelected)
    }
}