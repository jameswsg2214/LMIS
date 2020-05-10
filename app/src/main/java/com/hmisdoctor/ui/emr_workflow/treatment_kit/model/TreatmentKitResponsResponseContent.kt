package com.hmisdoctor.ui.emr_workflow.treatment_kit.model

data class TreatmentKitResponsResponseContent(
    val department_id: Int = 0,
    val department_name: String = "",
    val diagnosis: List<Diagnosi> = listOf(),
    val doctor_id: Int = 0,
    val doctor_name: String = "",
    val encounter_type: String = "",
    val encounter_type_uuid: Int = 0,
    val labDetails: List<LabDetail> = listOf(),
    val order_id: Int = 0,
    val ordered_date: String = "",
    val patient_id: Int = 0,
    val radilogyDetails: List<RadilogyDetail> = listOf(),
    val treatment_kit_name: Any = Any(),
    val treatment_kit_uuid: Any = Any()
)