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
        viewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)

        initClickListener()
        initEditTextListener()

        return binding.root
    }

    private fun initClickListener() {
        binding.nicknameCheckBtn.setOnClickListener {
            // MEMO: 사용 가능한 닉네임
            nicknameConditionText(View.GONE, View.VISIBLE, View.GONE)
            viewModel.setNickname(binding.nicknameEt.text.toString())

            // MEMO: 사용 불가한 닉네임
            // nicknameConditionText(View.GONE, View.GONE, View.VISIBLE)
        }
    }

    private fun initEditTextListener() {
        binding.nicknameEt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun afterTextChanged(p0: Editable?) {
                viewModel.setNickname("")
                nicknameConditionText(View.VISIBLE, View.GONE, View.GONE)
                binding.nicknameCheckBtn.isEnabled = binding.nicknameEt.text.isNotEmpty()
            }
        })
    }

    private fun nicknameConditionText(condition: Int, conditionO: Int, conditionX: Int) {
        binding.nicknameCondition.visibility = condition
        binding.nicknameConditionO.visibility = conditionO
        binding.nicknameConditionX.visibility = conditionX
    }
}