package com.example.routebox.presentation.utils

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import com.example.routebox.R

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

                setMaxLines(maxLines)
//                setInputType(inputType)
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
        showClearButton()
    }

    private fun setMaxLines(maxLines: Int) {
        Log.d("ClearableEditText", "maxLines: $maxLines")
        editText.maxLines = maxLines
        // maxLines 속성에 따라 EditText의 동작을 설정
        if (maxLines == 1) {
            editText.inputType = InputType.TYPE_CLASS_TEXT
            editText.imeOptions = EditorInfo.IME_ACTION_DONE
            editText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    editText.clearFocus()
                    hideKeyboard()
                    true
                } else {
                    false
                }
            }
        }
        else { // 줄바꿈 가능하도록 설정
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

    private fun showClearButton() {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if ((s?.length ?: 0) > 0) VISIBLE else INVISIBLE // 입력 내용이 있을 경우 표시
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun clearText() {
        clearButton.setOnClickListener {
            editText.text = null
        }
    }

    fun setText(text: CharSequence?) {
        editText.setText(text)
    }

    fun getText(): Editable? {
        return editText.text
    }
}