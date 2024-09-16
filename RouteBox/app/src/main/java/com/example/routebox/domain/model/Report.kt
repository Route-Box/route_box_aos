package com.example.routebox.domain.model

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.example.routebox.R

data class ReportId(
    var reportId: Int
)

data class RouteReportId(
    var routeReportId: Int
)

// 사용자 신고
data class ReportUser(
    var userId: Int,
    var content: String
)

// 루트 신고
data class ReportRoute(
    var routeId: Int,
    var reasonTypes: List<String>,
    var reasonDetail: String?
)

// 루트 신고 사유
enum class RouteReportReason(val type: String, val description: Int) {
    IRRELEVANT_CONTENT("IRRELEVANT_CONTENT", R.string.report_reason_not_trip),
    ADVERTISEMENT("ADVERTISEMENT", R.string.report_reason_advertising),
    INAPPROPRIATE_OR_HATEFUL_CONTENT("INAPPROPRIATE_OR_HATEFUL_CONTENT", R.string.report_reason_lascivious),
    COPYRIGHT_INFRINGEMENT_OR_IMPERSONATION("COPYRIGHT_INFRINGEMENT_OR_IMPERSONATION", R.string.report_reason_unauthorized_use),
    PRIVACY_VIOLATION("PRIVACY_VIOLATION", R.string.report_reason_personal_information),
    ETC("ETC", R.string.report_reason_etc);

    companion object {
        fun convertReportTypesByDescriptions(context: Context, description: List<String>): List<String> {
            return entries.filter { getString(context, it.description) in description }.map {
                it.type
            }
        }
    }
}