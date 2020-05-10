package com.hmisdoctor.ui.dashboard.model

data class ChangePasswordOTPResponseModel(
    val msg: String? = "",
    val responseContents: Otp? = Otp(),
    val status: String? = ""
)