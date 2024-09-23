package com.daval.routebox.presentation.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.daval.routebox.databinding.FragmentSignup2BirthBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar

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

    private fun isBirthValid()  {
        val dateFormat = SimpleDateFormat(BIRTH_FORMAT)
        val thisYear = dateFormat.format(Calendar.getInstance().time).substring(0, 4)
        val date = "${binding.birthYearEt.text}-${binding.birthMonthEt.text}-${binding.birthDayEt.text}"

        if (Integer.parseInt(binding.birthYearEt.text.toString()) <= Integer.parseInt(thisYear)) {
            try {
                dateFormat.isLenient = false
                dateFormat.parse(date)
                viewModel.setBirth(date)
            } catch (_: ParseException) { }
        }
    }

    companion object {
        val BIRTH_FORMAT = "yyyy-MM-dd"
    }
}