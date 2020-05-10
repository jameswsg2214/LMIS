package com.hmisdoctor.ui.emr_workflow.model

data class EmrWorkFlowResponseModel(
    var code: Int? = null,
    var message: String? = null,
    var responseContents: List<ResponseContent?>? = null
)