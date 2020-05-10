package com.hmisdoctor.ui.emr_workflow.certificate.model

data class TemplateResponseModel(
    val message: String = "",
    val responseContents: List<TemplateResponseContent> = listOf(),
    val statusCode: Int = 0,
    val totalRecords: Int = 0
)