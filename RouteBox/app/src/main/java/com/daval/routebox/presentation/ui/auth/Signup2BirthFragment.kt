package com.daval.routebox.presentation.ui.auth

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.daval.routebox.databinding.FragmentSignup2BirthBinding
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar

@SuppressLint("SimpleDateFormat")
@RequiresApi(Build.VERSION_CODES.O)
class Signup2BirthFragment : Fragment() {

    private lateinit var binding: FragmentSignup2BirthBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignup2BirthBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)

        initClickListener()
        initEditTextListener()

        return binding.root
    }

    private fun initClickListener() {

    }

    private fun initEditTextListener() {
        binding.birthYearEt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                isBirthValid()
            }
            override fun afterTextChanged(p0: Editable?) { }
        })
        binding.birthMonthEt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                isBirthValid()
            }
            override fun afterTextChanged(p0: Editable?) { }
        })
        binding.birthDayEt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                isBirthValid()
            }
            override fun afterTextChanged(p0: Editable?) { }
        })
    }

    // 입력한 날짜가 오늘 날짜 전 날짜인지, 올바른 날짜인지 확인
    private fun isBirthValid()  {
        // 기존에 저장했던 Birth 초기화
        viewModel.setBirth("")
        
        val decimalYearFormat = DecimalFormat("0000")
        val decimalMonthDayFormat = DecimalFormat("00")
        val today = LocalDate.now().toString().replace("-", "")

        // 모든 값이 채워져 있을 때만 유효성 확인
        if (binding.birthYearEt.text.isNotEmpty() && binding.birthMonthEt.text.isNotEmpty() && binding.birthDayEt.text.isNotEmpty()) {
            val date = "${decimalYearFormat.format(binding.birthYearEt.text.toString().toInt())}-${decimalMonthDayFormat.format(binding.birthMonthEt.text.toString().toInt())}-${decimalMonthDayFormat.format(binding.birthDayEt.text.toString().toInt())}"
            // 입력한 날짜가 오늘 이전 날짜인지 확인
            if (date.replace("-", "").toInt() < today.toInt()) {
                if (validationDate(date)) {
                    viewModel.setBirth(date)
                }
            }
        }
    }

    // 실제로 존재하는 날짜인지 체크
    private fun validationDate(checkDate: String): Boolean {
        try {
            var dateFormat = SimpleDateFormat("yyyy-MM-dd")
            dateFormat.isLenient = false
            dateFormat.parse(checkDate)
            return true
        } catch (e: ParseException){
            return false
        }
    }
}