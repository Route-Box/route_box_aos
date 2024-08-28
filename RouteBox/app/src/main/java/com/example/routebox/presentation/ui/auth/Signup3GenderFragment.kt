package com.example.routebox.presentation.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.routebox.R
import com.example.routebox.databinding.FragmentSignup3GenderBinding
import com.example.routebox.domain.model.Gender

class Signup3GenderFragment : Fragment() {

    private lateinit var binding: FragmentSignup3GenderBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignup3GenderBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)

        initRadioButton()

        return binding.root
    }

    private fun initRadioButton() {
        binding.genderRadiogroup.setOnCheckedChangeListener { _, buttonId ->
            when (buttonId) {
                R.id.gender_m -> viewModel.setGender(Gender.MALE.text) // 남자
                R.id.gender_f -> viewModel.setGender(Gender.FEMALE.text) // 여자
                else -> viewModel.setGender(Gender.PRIVATE.text) // 비공개
            }
        }
    }
}