package com.daval.routebox.presentation.ui.auth

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.daval.routebox.databinding.FragmentSignup1NicknameBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Signup1NicknameFragment : Fragment() {

    private lateinit var binding: FragmentSignup1NicknameBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignup1NicknameBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]

        binding.apply {
            viewModel = this@Signup1NicknameFragment.viewModel
            lifecycleOwner = this@Signup1NicknameFragment
        }

        initObserve()
        initClickListener()

        return binding.root
    }

    private fun initObserve() {
        viewModel.nickname.observe(viewLifecycleOwner) {
            viewModel.setNicknameValidation()
        }
    }

    private fun initClickListener() {
        binding.nicknameCl.setOnClickListener {
            hideKeyboard()
        }
    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.nicknameEt.windowToken, 0)
    }
}