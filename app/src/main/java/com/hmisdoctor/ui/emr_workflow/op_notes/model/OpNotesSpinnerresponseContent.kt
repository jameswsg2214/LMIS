package com.hmisdoctor.ui.emr_workflow.op_notes.model

data class OpNotesSpinnerresponseContent(
    val d_is_active: Boolean? = false,
    val d_name: String? = "",
    val d_status: Boolean? = false,
    val d_uuid: Int? = 0,
    val p_department_uuid: Int? = 0,
    val p_facility_uuid: Int? = 0,
    val p_is_active: Boolean? = false,
    val p_profile_code: String? = "",
    val p_profile_description: String? = "",
    val p_profile_name: String? = "",
    val p_profile_type_uuid: Int? = 0,
    val p_status: Boolean? = false,
    val p_uuid: Int? = 0
)