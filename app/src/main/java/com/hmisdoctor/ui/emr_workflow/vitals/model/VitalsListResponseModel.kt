package com.hmisdoctor.ui.emr_workflow.vitals.model

data class VitalsListResponseModel(
    val message: String = "",
    val responseContents: VitalsListResponseContents = VitalsListResponseContents(),
    val statusCode: Int = 0
)