package com.hmisdoctor.ui.dashboard.model

data class CovidNationalityResponseModel(
    val responseContents: List<NationalityresponseContent?>? = listOf(),
    val statusCode: Int? = 0
)