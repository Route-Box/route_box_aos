package com.example.routebox.presentation.ui.route.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.routebox.databinding.FragmentRouteEditBinding

class RouteEditFragment : Fragment() {
    private lateinit var binding: FragmentRouteEditBinding

    private val viewModel: RouteEditViewModel by viewModels()

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
        val args: RouteEditFragmentArgs by navArgs()
        args.route?.let {
            viewModel.setRoute(it)
            viewModel.initRouteTitleAndContent()
        }
    }

    private fun initClickListeners() {
        // 활동 수정 버튼
        binding.routeEditActivityEditBtn.setOnClickListener {
            // 활동 수정 화면으로 이동
            val action = RouteEditFragmentDirections.actionRouteEditFragmentToRouteEditActivityFragment(
                viewModel.route.value
            )
            findNavController().navigate(action)
        }
        // 완료 버튼
        binding.routeEditDoneBtn.setOnClickListener {

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