package com.hmisdoctor.ui.dashboard.model.registration

data class RepeatedIntervalReponseModel(
    val responseContents: List<RepeatedIntervalresponseContent?>? = listOf(),
    val statusCode: Int? = 0
)