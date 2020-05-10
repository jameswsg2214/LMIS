package com.hmisdoctor.ui.emr_workflow.history.diagnosis.model

data class HistoryDiagnosisCreateResponseModel(
    val responseContents: List<HistoryDiagnosisCreateresponseContent?>? = listOf(),
    val code: Int? = 0,
    val message: String? = ""
)