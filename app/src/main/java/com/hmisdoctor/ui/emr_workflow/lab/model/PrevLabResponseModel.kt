package com.hmisdoctor.ui.emr_workflow.view.lab.model

data class PrevLabResponseModel(
    val message: String = "",
    val responseContents: List<PrevLabResponseContent> = listOf(),
    val statusCode: Int = 0
)