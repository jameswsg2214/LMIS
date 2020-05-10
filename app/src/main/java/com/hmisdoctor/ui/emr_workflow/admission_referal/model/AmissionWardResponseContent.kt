package com.hmisdoctor.ui.emr_workflow.admission_referal.model

data class AmissionWardResponseContent(
    val department_name: String = "",
    val department_uuid: Int = 0,
    val facility_uuid: Int = 0,
    val ward_name: String = "",
    val ward_uuid: Int = 0
)