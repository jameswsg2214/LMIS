package com.hmisdoctor.ui.quick_reg.model

data class QuickSearchResponseModel(
    val responseContents: List<QuickSearchresponseContent?>? = listOf(),
    val message: String? = "",
    val statusCode: Int? = 0,
    val totalRecords: Int? = 0
)