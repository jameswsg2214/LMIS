package com.hmisdoctor.ui.emr_workflow.treatment_kit.model

data class ReqContents(
    val treatment_kit: TreatmentKit? = TreatmentKit(),
    val treatment_kit_diagnosis: List<TreatmentKitDiagnosi?>? = listOf(),
    val treatment_kit_drug: List<Any?>? = listOf(),
    val treatment_kit_investigation: List<Any?>? = listOf(),
    val treatment_kit_lab: List<TreatmentKitLab?>? = listOf(),
    val treatment_kit_radiology: List<Any?>? = listOf()
)