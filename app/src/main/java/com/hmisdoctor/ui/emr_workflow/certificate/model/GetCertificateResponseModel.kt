package com.hmisdoctor.ui.emr_workflow.certificate.model

data class GetCertificateResponseModel(
    val code: Int = 0,
    val responseContent: List<GetCertificateResponseContent> = listOf()
)