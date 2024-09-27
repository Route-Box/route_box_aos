package com.daval.routebox.presentation.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.daval.routebox.R
import com.daval.routebox.databinding.FragmentSignup4TermsBinding
import com.daval.routebox.presentation.config.Constants

class Signup4TermsFragment : Fragment() {

    private lateinit var binding: FragmentSignup4TermsBinding
    private lateinit var viewModel: AuthViewModel
    private var term1: Boolean = false
    private var term2: Boolean = false

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignup4TermsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)
        binding.termTitle.text = String.format(resources.getString(R.string.signup_complete), viewModel.nickname.value)

        initClickListener()

        return binding.root
    }

    private fun initClickListener() {
        /* 약관 링크 연결 */
        binding.term1Tv.setOnClickListener { // 이용 약관
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constants.STANDARD_TERM_URL)))
        }
        binding.term2Tv.setOnClickListener { // 개인 정보 수집
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constants.POLICY_TERM_URL)))
        }
        binding.term3Tv.setOnClickListener { // 위치 기반 서비스 약관
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constants.LOCATION_TERM_URL)))
        }
        binding.term4Tv.setOnClickListener { // 알림 약관
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constants.ALERT_TERM_URL)))
        }

        // 필수 체크박스 선택 확인
        binding.term1Check.setOnClickListener {
            term1 = !term1
            viewModel.setTerms(term1 && term2)
        }

        binding.term2Check.setOnClickListener {
            term2 = !term2
            viewModel.setTerms(term1 && term2)
        }
    }
}