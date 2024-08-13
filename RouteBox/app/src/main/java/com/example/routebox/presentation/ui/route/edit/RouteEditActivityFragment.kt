package com.example.routebox.presentation.ui.route.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routebox.databinding.BottomSheetActivityBinding
import com.example.routebox.databinding.FragmentRouteEditActivityBinding
import com.example.routebox.presentation.ui.route.adapter.ActivityRVAdapter

class RouteEditActivityFragment : Fragment() {
    private lateinit var binding: FragmentRouteEditActivityBinding

    private val viewModel: RouteEditViewModel by viewModels()
    private lateinit var bottomSheetDialog: BottomSheetActivityBinding
    private val activityAdapter = ActivityRVAdapter(true)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRouteEditActivityBinding.inflate(inflater, container, false)

        binding.apply {
            viewModel = this@RouteEditActivityFragment.viewModel
            lifecycleOwner = this@RouteEditActivityFragment
        }

        setInit()
        initClickListeners()
        initObserve()

        return binding.root
    }

    private fun setInit() {
        bottomSheetDialog = binding.routeEditActivityBottomSheet
        bottomSheetDialog.apply {
            this.viewModel = viewModel
            this.lifecycleOwner = this@RouteEditActivityFragment
        }

        val args: RouteEditFragmentArgs by navArgs()
        args.route?.let {
            viewModel.setRoute(it)
        }
    }

    private fun initClickListeners() {
        // 활동 추가 버튼
        bottomSheetDialog.activityAddBtn.setOnClickListener {
            //TODO: 활동 추가 화면으로 이동
            Toast.makeText(requireContext(), "활동 추가 버튼 클릭", Toast.LENGTH_SHORT).show()
        }

        // 활동 아이템 클릭
        activityAdapter.setActivityClickListener(object : ActivityRVAdapter.MyItemClickListener {
            override fun onEditButtonClick(position: Int) {
                //TODO: 수정 화면으로 이동
                Toast.makeText(requireContext(), "활동 수정 버튼 클릭", Toast.LENGTH_SHORT).show()
            }

            override fun onDeleteButtonClick(position: Int) {
                // 활동 삭제 팝업 띄우기
                Toast.makeText(requireContext(), "활동 삭제 버튼 클릭", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setActivityAdapter() {
        bottomSheetDialog.activityRv.apply {
            this.adapter = activityAdapter
            this.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        }
        activityAdapter.addActivity(viewModel.route.value!!.activities)
    }

    private fun initObserve() {
        viewModel.route.observe(viewLifecycleOwner) { route ->
            if (route.activities.isNotEmpty()) {
                setActivityAdapter()
            }
        }
    }
}