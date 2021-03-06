package com.hmisdoctor.ui.emr_workflow.lab.model.favresponse

data class FavSearch(
    val code: String = "",
    val department_uuid: Int = 0,
    val is_active: Boolean = false,
    val name: String = "",
    val status: Boolean = false,
    val sub_department_uuid: Int = 0,
    val type: String = "",
    val uuid: Int = 0
)