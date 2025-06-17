package com.daval.routebox.presentation.ui.my

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.daval.routebox.databinding.FragmentMyBinding
import com.daval.routebox.presentation.ui.auth.LoginActivity

class MyFragment : Fragment() {
    private lateinit var binding: FragmentMyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        binding = FragmentMyBinding.inflate(inflater, container, false)

        return binding.root
    }

    // MEMO: 웹뷰에서 로그인 액티비티로 이동을 위해 썼던 코드 / UI 제작 후 사용 안할 시 제거
    private fun moveToLoginActivity() {
        requireActivity().startActivity(Intent(requireActivity(), LoginActivity::class.java))
        requireActivity().finish()
    }
}