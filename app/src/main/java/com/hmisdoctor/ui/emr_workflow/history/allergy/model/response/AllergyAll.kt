package com.hmisdoctor.ui.emr_workflow.history.allergy.model.response

data class AllergyAll(
    val allergy_adr_score_uuid: Int? = 0,
    val allergy_adr_status_uuid: Int? = 0,
    val allergy_master_uuid: Int? = 0,
    val allergy_masters: AllergyMasters? = AllergyMasters(),
    val allergy_severity: AllergySeverity? = AllergySeverity(),
    val allergy_severity_uuid: Int? = 0,
    val allergy_source: AllergySource? = AllergySource(),
    val allergy_source_uuid: Int? = 0,
    val allergy_type_uuid: Int? = 0,
    val comments: Any? = Any(),
    val consultation_uuid: Int? = 0,
    val created_by: Int? = 0,
    val created_date: String? = "",
    val duration: Int? = 0,
    val encounter: Encounter? = Encounter(),
    val encounter_uuid: Int? = 0,
    val end_date: String? = "",
    val is_active: Boolean? = false,
    val modified_by: Int? = 0,
    val modified_date: String? = "",
    val patient_allergy_status_uuid: Int? = 0,
    val patient_uuid: Int? = 0,
    val performed_by: Int? = 0,
    val performed_date: String? = "",
    val period_uuid: Int? = 0,
    val periods: Periods? = Periods(),
    val revision: Int? = 0,
    val start_date: String? = "",
    val status: Boolean? = false,
    val symptom: Any? = Any(),
    val uuid: Int? = 0
)