package com.hmisdoctor.ui.emr_workflow.prescription.model

data class PrescriptionAllDataResponseContents(
    var prescription_details_result: List<PrescriptionDetailsResult?>? = listOf(),
    var prescription_result: PrescriptionResult? = PrescriptionResult()
)