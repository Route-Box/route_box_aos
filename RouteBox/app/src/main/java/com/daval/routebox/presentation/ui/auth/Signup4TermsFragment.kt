package com.daval.routebox.presentation.ui.auth

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
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

        binding.term3Check.setOnClickListener {
            checkGPSPermission()
        }

        binding.term4Check.setOnClickListener {
            // SDK 32 이하에서는 자동으로 알림 권한이 활성화! So, SDK 33 이상일 경우에만 권한 요청
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) checkNotificationPermission()
        }
    }

    private fun checkNotificationPermission() {
        // 권한을 구분하기 위한 NOTIFICATION_PERMISSION_REQUEST_CODE 필요!
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            NOTIFICATION_PERMISSION_REQUEST_CODE
        )
    }

    // Background GPS 권한 허용을 위한 부분
    // Android 11 이상부터는 Background에서 접근하는 권한이 처음 권한 확인 문구에 뜨지 않는다 -> So, 추가로 권한을 한번 더 확인하여 Background에서 동작이 가능하도록 구성
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 첫 권한 확인이 완료 되었는지 확인
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // SDK 29 이상일 때는 한번 더 권한 요청
                // SDK 29 미만일 경우, 항상 허용 옵션이 첫 권한 요청 화면에 뜨기 때문에 추가로 요청 X
                // 만약 첫 권한을 허용했다면, Background에서 작동하도록 "항상 허용" 권한 요청
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                    && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(requireActivity(), ContextCompat.getString(requireActivity(), R.string.gps_always_grant), Toast.LENGTH_SHORT).show()
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), LOCATION_BACKGROUND_PERMISSION_REQUEST_CODE)
                }
            } else {
                // 권한이 거부되었을 경우, 아래 문구 띄우기
                Toast.makeText(requireActivity(), ContextCompat.getString(requireActivity(), R.string.gps_deny), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkGPSPermission() {
        // 권한을 구분하기 위한 LOCATION_PERMISSION_REQUEST_CODE 필요!
        requestPermissions(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
            , Manifest.permission.ACCESS_COARSE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1
        private const val LOCATION_PERMISSION_REQUEST_CODE = 2
        private const val LOCATION_BACKGROUND_PERMISSION_REQUEST_CODE = 3
    }
}