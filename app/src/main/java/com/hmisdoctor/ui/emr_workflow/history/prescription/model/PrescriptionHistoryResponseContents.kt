package com.hmisdoctor.ui.emr_workflow.history.prescription.model

data class PrescriptionHistoryResponseContents(
    val count: Int = 0,
    val rows: List<HistoryPresRow> = listOf()
)