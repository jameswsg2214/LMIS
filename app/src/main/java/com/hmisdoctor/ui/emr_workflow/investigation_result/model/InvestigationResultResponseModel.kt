package com.hmisdoctor.ui.emr_workflow.investigation_result.model

data class InvestigationResultResponseModel(
    val message: String = "",
    val responseContents: List<Any> = listOf(),
    val statusCode: Int = 0,
    val totalRecords: Int = 0
)