package com.daval.routebox.presentation.utils

import com.daval.routebox.domain.model.BaseResponse
import org.json.JSONObject
import retrofit2.HttpException

object ErrorHandler {
    // Throwable의 확장 함수로 에러 메시지 처리
    fun Throwable.handleError(): BaseResponse {
        return when (this) {
            is HttpException -> {
                val errorBody = this.response()?.errorBody()?.string()
                val errorCode = try {
                    JSONObject(errorBody).getString("code")
                } catch (e: Exception) {
                    "Unknown HTTP error"
                }
                val errorMessage = try {
                    JSONObject(errorBody).getString("message")
                } catch (e: Exception) {
                    "Unknown HTTP error"
                }
                BaseResponse(
                    code = errorCode.toInt(),
                    message = errorMessage,
                    isSuccess = false
                )
            }
            else -> {
                BaseResponse(
                    code = -1,
                    message = message ?: "Unknown error",
                    isSuccess = false
                )
            }
        }
    }
}