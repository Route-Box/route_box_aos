package com.daval.routebox.presentation.ui.user

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.daval.routebox.databinding.FragmentInquiryHistoryBinding
import com.daval.routebox.domain.model.Inquiry
import com.daval.routebox.presentation.ui.user.adapter.InquiryHistoryRVAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InquiryHistoryFragment : Fragment() {
    private lateinit var binding: FragmentInquiryHistoryBinding

    private val viewModel: UserViewModel by activityViewModels()
    private lateinit var inquiryHistoryRVAdapter: InquiryHistoryRVAdapter
    // TODO: API 연동 후 삭제
    private var inquiryHistoryList = arrayListOf(
        Inquiry(-1, "문의 1", "답변 대기"),
        Inquiry(-1, "문의 1문의 1", "답변 완료"),
        Inquiry(-1, "문의 1문의 1문의 1", "답변 완료"),
        Inquiry(-1, "문의 1문의 1문의 1문의 1", "답변 대기"),
        Inquiry(-1, "문의 1문의 1문의 1문의 1문의 1문의 1문의 1문의 1문의 1", "답변 대기"),
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        binding = FragmentInquiryHistoryBinding.inflate(inflater, container, false)

        binding.apply {
            lifecycleOwner = this@InquiryHistoryFragment
        }

        initClickListener()
        setAdapter()

        return binding.root
    }

    private fun initClickListener() {

    }

    private fun setAdapter() {
        inquiryHistoryRVAdapter = InquiryHistoryRVAdapter(inquiryHistoryList)
        binding.inquiryHistoryRv.apply {
            adapter = inquiryHistoryRVAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        inquiryHistoryRVAdapter.setInquiryClickListener(object: InquiryHistoryRVAdapter.InquiryItemClickListener {
            override fun onItemClick(inquiry: Inquiry) {
                startActivity(Intent(requireActivity(), MyInquiryActivity::class.java).putExtra("inquiryId", inquiry.inquiryId))
            }
        })
    }
}