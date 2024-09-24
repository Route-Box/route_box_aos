package com.daval.routebox.presentation.ui.auth

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.daval.routebox.R
import com.daval.routebox.databinding.FragmentSignup4TermsBinding

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
        // TODO: 약관 관련 페이지 나올 경우, View로 만드는 것인지, WebView로 노션 연결하는지 확인 후 작업!
        binding.term1Arrow.setOnClickListener { }
        binding.term2Arrow.setOnClickListener { }

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