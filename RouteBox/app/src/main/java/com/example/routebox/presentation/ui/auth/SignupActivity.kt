package com.example.routebox.presentation.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation.findNavController
import com.example.routebox.R
import com.example.routebox.databinding.ActivitySignupBinding
import com.example.routebox.presentation.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)

        initObserve()
        initClickListener()
        backPressed()
    }

    private fun initObserve() {
        // 회원가입 각 과정에서 값 입력 여부에 따라 다음 버튼 활성화
        binding.nextBtn.apply {
            viewModel.isAvailableNickname.observe(this@SignupActivity) { isAvailableNickname ->
               isEnabled = isAvailableNickname == true // 중복 확인이 끝났을 경우
            }
            viewModel.birth.observe(this@SignupActivity) {
                isEnabled = viewModel.birth.value != ""
            }
            viewModel.gender.observe(this@SignupActivity) {
                isEnabled = viewModel.birth.value != ""
            }
            viewModel.terms.observe(this@SignupActivity) {
                isEnabled = viewModel.terms.value != false
            }
        }
    }

    private fun initClickListener() {
        binding.signupBackIv.setOnClickListener {
            backStep()
        }

        binding.nextBtn.setOnClickListener {
            when (viewModel.step.value) {
                1 -> {
                    findNavController(binding.signupContainer).navigate(R.id.action_signup1NicknameFragment_to_signup2BirthFragment)
                }
                2 -> {
                    findNavController(binding.signupContainer).navigate(R.id.action_signup2BirthFragment_to_signup3GenderFragment)
                    binding.nextBtn.text = ContextCompat.getString(this, R.string.next_btn)
                }
                3 -> {
                    findNavController(binding.signupContainer).navigate(R.id.action_signup3GenderFragment_to_signup4TermsFragment)
                    binding.nextBtn.text = ContextCompat.getString(this, R.string.signup_complete_btn)
                }
                4 -> {
                    // TODO: 서버 회원가입 API와 연동!!
                    // 회원가입 API 성공했을 때 MainActivity로 이동!
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }
            }

            viewModel.setStep(viewModel.step.value!! + 1)
            binding.progressBar.progress = viewModel.step.value!!
            if (viewModel.step.value != 5) binding.nextBtn.isEnabled = false
        }
    }

    private fun backPressed() {
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backStep()
            }
        })
    }

    // 뒤로가기의 경우, 뒤로가기 아이콘을 눌렀을 때와 안드로이드 자체 뒤로가기 버튼을 눌렀을 때를 모두 고려해야 하기 때문에 편의를 위해 함수로 정의
    private fun backStep() {
        when (viewModel.step.value) {
            1 -> { finish() }
            2 -> {
                findNavController(binding.signupContainer).navigate(R.id.action_signup2BirthFragment_to_signup1NicknameFragment2)
                viewModel.setNickname("")
            }
            3 -> {
                findNavController(binding.signupContainer).navigate(R.id.action_signup3GenderFragment_to_signup2BirthFragment)
                viewModel.setBirth("")
            }
            4 -> {
                findNavController(binding.signupContainer).navigate(R.id.action_signup4TermsFragment_to_signup3GenderFragment)
                viewModel.setGender("")
                viewModel.setTerms(false)
                binding.nextBtn.text = ContextCompat.getString(this, R.string.next_btn)
            }
        }

        viewModel.setStep(viewModel.step.value!! - 1)
        if (viewModel.step.value != 0) binding.progressBar.progress = viewModel.step.value!!
        binding.nextBtn.isEnabled = false
    }
}