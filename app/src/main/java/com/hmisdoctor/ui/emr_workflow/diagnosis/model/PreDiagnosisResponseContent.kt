package com.hmisdoctor.ui.emr_workflow.diagnosis.model

data class PreDiagnosisResponseContent(
    val diagnosis: Diagnosis = Diagnosis(),
    val diagnosis_uuid: Int = 0,
    val encounter_type_uuid: Int = 0,
    val is_snomed: IsSnomed = IsSnomed(),
    val other_diagnosis: String = "",
    val patient_uuid: Int = 0,
    val uuid: Int = 0
)