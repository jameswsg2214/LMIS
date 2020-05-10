package com.hmisdoctor.ui.emr_workflow.certificate.model

data class CertificateResponseModel(
    val code: Int = 0,
    val message: String = "",
    val responseContents: CertificateResponseContents = CertificateResponseContents()
)