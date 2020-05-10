package com.hmisdoctor.ui.emr_workflow.admission_referal.model

data class ReasonResponseModel(
    val code: Int = 0,
    val message: String = "",
    val responseContent: List<ReasonResponseContent> = listOf()
)