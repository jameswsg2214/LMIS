package com.hmisdoctor.ui.dashboard.model

data class CovidQuarantineTypeResponseModel(
    val responseContents: List<QuarantinetyperesponseContent?>? = listOf(),
    val statusCode: Int? = 0
)