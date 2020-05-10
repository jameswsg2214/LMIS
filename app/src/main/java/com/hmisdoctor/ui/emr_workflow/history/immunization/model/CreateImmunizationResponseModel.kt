package com.hmisdoctor.ui.emr_workflow.history.immunization.model

data class CreateImmunizationResponseModel(
    val responseContents: CreateImmunizationresponseContents? = CreateImmunizationresponseContents(),
    val code: Int? = 0,
    val message: String? = ""
)