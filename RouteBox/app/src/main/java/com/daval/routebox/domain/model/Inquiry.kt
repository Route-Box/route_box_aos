package com.daval.routebox.domain.model

data class InquiryList(
    val inquiries: List<Inquiry>
)

data class Inquiry(
    val inquiryId: Int,
    var content: String,
    var status: String
)

data class InquiryDetail(
    val inquiryId: Int,
    val userId: Int,
    val type: String,
    val content: String,
    val status: String,
    val reply: String,
    val imageUrls: List<String>
)