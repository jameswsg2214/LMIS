package com.hmisdoctor.ui.emr_workflow.op_notes.model

data class Categories(
    val category_group_uuid: Int = 0,
    val category_type_uuid: Int = 0,
    val code: String = "",
    val description: String = "",
    val name: String = "",
    val uuid: Int = 0
)