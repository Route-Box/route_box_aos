package com.daval.routebox.presentation.utils

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.lifecycle.LiveData
import com.daval.routebox.R

class ClearableEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private lateinit var editText: EditText
    private lateinit var clearButton: ImageView

    init {
        setLayout()

        // XML 속성 처리
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ClearableEditText,
            0, 0
        ).apply {
            try {
                val maxLines = getInteger(R.styleable.ClearableEditText_maxLines, 10)
                val inputType = getInteger(R.styleable.ClearableEditText_inputType, InputType.TYPE_CLASS_TEXT)

                setInputType(inputType)
                setMaxLines(maxLines)
            } finally {
                recycle()
            }
        }
    }

    private fun setLayout() {
        inflater.inflate(R.layout.clearable_edit_text, this, true)
        editText = findViewById(R.id.clearable_et)
        clearButton = findViewById(R.id.clearable_clear_iv)
        clearButton.visibility = INVISIBLE
        clearText()

        // 포커스 변경 리스너 설정
        editText.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            checkClearButtonVisibility(clearButton, editText.text, hasFocus)
        }
    }

    private fun setMaxLines(maxLines: Int) {
        editText.maxLines = maxLines
        // maxLines 속성에 따라 EditText의 동작을 설정
        if (maxLines == 1) {
            editText.imeOptions = EditorInfo.IME_ACTION_DONE
            editText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) { // MaxLines가 1이라면 엔터키 클릭 시 포커스 해제 및 키보드 내리기
                    editText.clearFocus()
                    hideKeyboard()
                    true
                } else {
                    false
                }
            }
        } else { // 줄바꿈 가능하도록 설정
            editText.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
            editText.isSingleLine = false
        }
    }

    private fun setInputType(inputType: Int) {
        editText.inputType = inputType
    }

    private fun hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    private fun clearText() {
        clearButton.setOnClickListener {
            editText.text = null
            clearButton.visibility = INVISIBLE
        }
    }

    companion object {
        // X 버튼 노출 여부 확인 (포커스 상태와 텍스트 길이 모두 체크)
        fun checkClearButtonVisibility(clearButton: ImageView, text: CharSequence?, hasFocus: Boolean) {
            clearButton.visibility = if (hasFocus && (text?.length ?: 0) > 0) VISIBLE else INVISIBLE
        }

        @JvmStatic
        @BindingAdapter("app:bindText")
        fun bindText(view: ClearableEditText, text: LiveData<String>?) {
            text?.let {
                if (view.editText.text.toString() != text.value) { // 함수 호출시 무한루프를 방지하기 위해 기존 텍스트와의 동일 여부를 검사
                    view.editText.setText(it.value)
                }
                // 관찰자 등록
                text.observeForever { newValue ->
                    if (view.editText.text?.toString() != newValue) {
                        view.editText.setText(newValue)
                    }
                }
            }
        }

        @JvmStatic
        @InverseBindingAdapter(attribute = "app:bindText", event = "bindTextAttrChanged")
        fun getText(view: ClearableEditText): String {
            return view.editText.text?.toString() ?: ""
        }

        @JvmStatic
        @BindingAdapter("bindTextAttrChanged")
        fun setTextWatcher(view: ClearableEditText, listener: InverseBindingListener?) {
            view.editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    listener?.onChange()
                    checkClearButtonVisibility(view.clearButton, s, view.editText.isFocused)
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }
}