package com.hmisdoctor.ui.emr_workflow.lab.model

data class FavAddResponseModel(
    val responseContent: FavAddResponseContent = FavAddResponseContent(),
    val statusCode: Int = 0
)