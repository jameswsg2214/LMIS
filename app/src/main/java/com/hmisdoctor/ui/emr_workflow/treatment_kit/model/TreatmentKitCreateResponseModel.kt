package com.hmisdoctor.ui.emr_workflow.treatment_kit.model

data class TreatmentKitCreateResponseModel(
    val code: Int? = 0,
    val message: String? = "",
    val reqContents: ReqContents? = ReqContents()
)