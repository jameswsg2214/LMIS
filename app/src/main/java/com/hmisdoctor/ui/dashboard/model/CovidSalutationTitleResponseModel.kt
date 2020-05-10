package com.hmisdoctor.ui.dashboard.model

data class CovidSalutationTitleResponseModel(
    val responseContents: List<SalutationresponseContent?>? = listOf(),
    val req: String? = "",
    val statusCode: Int? = 0
)