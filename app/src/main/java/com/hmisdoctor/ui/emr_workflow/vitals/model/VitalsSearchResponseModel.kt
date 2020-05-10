package com.hmisdoctor.ui.emr_workflow.vitals.model

data class VitalsSearchResponseModel(
    val message: String = "",
    val responseContents: VitalsSearchResponseContents = VitalsSearchResponseContents(),
    val statusCode: Int = 0
)