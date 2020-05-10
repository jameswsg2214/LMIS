package com.hmisdoctor.ui.emr_workflow.prescription.model

data class PrevPrescriptionResponseContents(
    var count: Int? = 0,
    var rows: List<PrevRow?>? = listOf()
)