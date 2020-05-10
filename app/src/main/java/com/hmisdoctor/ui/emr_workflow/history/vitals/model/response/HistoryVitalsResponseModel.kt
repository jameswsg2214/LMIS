package com.hmisdoctor.ui.emr_workflow.history.vitals.model.response

data class HistoryVitalsResponseModel(
    val code: Int? = 0,
    val message: String? = "",
    val responseContents: List<HistoryVitalResponseContent?>? = listOf()
)