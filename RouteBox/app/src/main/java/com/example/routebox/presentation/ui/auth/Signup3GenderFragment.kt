package com.example.routebox.presentation.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.routebox.R
import com.example.routebox.databinding.FragmentSignup3GenderBinding

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
            if (buttonId == R.id.gender_m) viewModel.setGender(binding.genderM.text.toString())
            else if (buttonId == R.id.gender_f) viewModel.setGender(binding.genderF.text.toString())
            // TODO: 비공개의 경우, 서버 처리 확인하고 수정!
            else viewModel.setGender("secret")
        }
    }
}