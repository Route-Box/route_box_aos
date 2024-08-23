package com.example.routebox.presentation.ui.route.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routebox.R
import com.example.routebox.databinding.BottomSheetActivityBinding
import com.example.routebox.databinding.FragmentRouteEditActivityBinding
import com.example.routebox.domain.model.DialogType
import com.example.routebox.presentation.ui.route.adapter.ActivityRVAdapter
import com.example.routebox.presentation.utils.CommonPopupDialog
import com.example.routebox.presentation.utils.PopupDialogInterface

class RouteEditActivityFragment : Fragment(), PopupDialogInterface {
    private lateinit var binding: FragmentRouteEditActivityBinding

    private val viewModel: RouteEditViewModel by activityViewModels()
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
            this.viewModel = this@RouteEditActivityFragment.viewModel
            this.lifecycleOwner = this@RouteEditActivityFragment
        }

        viewModel.setStepId(2)
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
                showPopupDialog()
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

    private fun showPopupDialog() {
        val dialog = CommonPopupDialog(this@RouteEditActivityFragment, DialogType.DELETE.id, String.format(resources.getString(R.string.activity_delete_popup)), null, null)
        dialog.isCancelable = false // 배경 클릭 막기
        activity?.let { dialog.show(it.supportFragmentManager, "PopupDialog") }
    }

    override fun onClickPositiveButton(id: Int) {
        //TODO: 활동 삭제 진행
        Toast.makeText(requireContext(), "활동이 삭제되었습니다", Toast.LENGTH_SHORT).show()
    }
}