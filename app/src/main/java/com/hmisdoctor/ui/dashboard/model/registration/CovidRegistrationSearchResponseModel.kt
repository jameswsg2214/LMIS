package com.hmisdoctor.ui.dashboard.model.registration

data class CovidRegistrationSearchResponseModel(
    val message: String = "",
    val responseContents: List<CovidRegistrationSearchResponseContent> = listOf(),
    val statusCode: Int = 0,
    val totalRecords: Int = 0
)