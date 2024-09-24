package com.daval.routebox.presentation.utils

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.daval.routebox.databinding.DialogCommonPopupBinding

interface PopupDialogInterface {
    fun onClickPositiveButton(id: Int)
}

class CommonPopupDialog(
    confirmDialogInterface: PopupDialogInterface,
    id: Int,
    content: String?, negativeButtonText: String?, positiveButtonText: String?
) : DialogFragment() {

    // 뷰 바인딩 정의
    private var _binding: DialogCommonPopupBinding? = null
    private val binding get() = _binding!!

    private var confirmDialogInterface: PopupDialogInterface? = null

    private var content: String? = null
    private var negativeButtonText: String? = null
    private var positiveButtonText: String? = null
    private var id: Int? = null

    init {
        this.confirmDialogInterface = confirmDialogInterface
        this.id = id
        this.content = content
        this.negativeButtonText = negativeButtonText
        this.positiveButtonText = positiveButtonText
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCommonPopupBinding.inflate(inflater, container, false)
        val view = binding.root

        // 레이아웃 배경을 투명하게 해줌
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setInit()
        initClickListeners()
        hideNegativeButton()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setInit() {
        binding.apply {
            dialogContentTv.text = content // 내용
            negativeButtonText?.let {// 취소 버튼 텍스트
                dialogNegativeBtn.text = it
            }
            positiveButtonText?.let {// 확인 버튼 텍스트
                dialogPositiveBtn.text = it
            }
        }
    }

    private fun initClickListeners() {
        // 취소 버튼 클릭
        binding.dialogNegativeBtn.setOnClickListener {
            dismiss()
        }

        // 확인 버튼 클릭
        binding.dialogPositiveBtn.setOnClickListener {
            this.confirmDialogInterface?.onClickPositiveButton(id!!)
            dismiss()
        }
    }

    private fun hideNegativeButton() {
        // 취소 버튼이 없는 다이얼로그는 id를 -1로 넘겨줌
        if (id == -1) {
            // 취소 버튼을 보이지 않게 처리
            binding.dialogNegativeBtn.visibility = View.GONE
            binding.dialogButtonSeparateView.visibility = View.GONE
        }
    }
}