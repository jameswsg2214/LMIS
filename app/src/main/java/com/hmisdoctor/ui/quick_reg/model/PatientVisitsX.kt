package com.hmisdoctor.ui.quick_reg.model

data class PatientVisitsX(
    val department_uuid: String? = "",
    val facility_uuid: String? = "",
    val modified_by: String? = "",
    val modified_date: String? = "",
    val session_uuid: Int? = 0
)