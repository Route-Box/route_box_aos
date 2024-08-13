package com.example.routebox.presentation.ui.route.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.routebox.R
import com.example.routebox.databinding.FragmentRouteEditBinding

class RouteEditFragment : Fragment() {
    private lateinit var binding: FragmentRouteEditBinding

    private val viewModel: RouteEditViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRouteEditBinding.inflate(inflater, container, false)

        binding.apply {
            viewModel = this@RouteEditFragment.viewModel
            lifecycleOwner = this@RouteEditFragment
        }

        setInit()
        initClickListeners()
        initObserve()

        return binding.root
    }

    private fun setInit() {
        viewModel.setStepId(1)
    }

    private fun initClickListeners() {
        // 활동 수정 버튼
        binding.routeEditActivityEditBtn.setOnClickListener {
            // 활동 수정 화면으로 이동
            findNavController().navigate(R.id.action_routeEditFragment_to_routeEditActivityFragment)
        }
        // 완료 버튼
        binding.routeEditDoneBtn.setOnClickListener {
            //TODO: 루트 제목, 내용 저장 진행
        }
    }

    private fun initObserve() {
        viewModel.routeTitle.observe(viewLifecycleOwner) {
            viewModel.checkButtonEnable()
        }

        viewModel.routeContent.observe(viewLifecycleOwner) {
            viewModel.checkButtonEnable()
        }
    }
}