package com.daval.routebox.presentation.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
        binding.termTitle.text = "${viewModel.nickname.value}${ContextCompat.getString(requireActivity(), R.string.signup_complete)}"

        initClickListener()

        return binding.root
    }

    private fun initClickListener() {
        // 약관 링크 연결
        binding.term1Tv.setOnClickListener { // 이용 약관
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constants.TERM_URL)))
        }
        binding.term2Tv.setOnClickListener { // 개인 정보 수집
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constants.POLICY_URL)))
        }

        binding.term1Check.setOnClickListener {
            if (term1) {
                binding.term1Check.setImageResource(R.drawable.ic_check_term_x)
            } else binding.term1Check.setImageResource(R.drawable.ic_check_term_o)

            term1 = !term1
            viewModel.setTerms(term1 && term2)
        }

        binding.term2Check.setOnClickListener {
            if (term2) {
                binding.term2Check.setImageResource(R.drawable.ic_check_term_x)
            } else binding.term2Check.setImageResource(R.drawable.ic_check_term_o)

            term2 = !term2
            viewModel.setTerms(term1 && term2)
        }
    }
}