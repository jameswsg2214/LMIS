package com.hmisdoctor.ui.quick_reg.model

data class QuickRegistrationUpdateResponseModel(
    val responseContent: QuickRegistrationUpdateresponseContent = QuickRegistrationUpdateresponseContent(),
    val message: String? = "",
    val statusCode: Int? = 0
)