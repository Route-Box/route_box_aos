package com.example.routebox.presentation.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.routebox.databinding.FragmentSignup1NicknameBinding
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

        return binding.root
    }

    private fun initObserve() {
        viewModel.nickname.observe(viewLifecycleOwner) {
            viewModel.setNicknameValidation()
        }
    }
}