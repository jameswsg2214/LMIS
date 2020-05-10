package com.hmisdoctor.ui.emr_workflow.prescription.model

data class PresDrugFrequencyResponseModel(
    val message: String = "",
    val responseContents: List<PresDrugFrequencyResponseContent> = listOf(),
    val statusCode: Int = 0,
    val totalRecords: Int = 0
)