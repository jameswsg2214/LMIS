package com.hmisdoctor.ui.emr_workflow.prescription.model

data class PrescriptionTemplateResponseModel(
    val req: String = "",
    val responseContents: PrescriptionTemplateResponseContents = PrescriptionTemplateResponseContents(),
    val statusCode: Int = 0
)