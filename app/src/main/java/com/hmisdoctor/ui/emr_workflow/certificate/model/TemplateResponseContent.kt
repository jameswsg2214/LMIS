package com.hmisdoctor.ui.emr_workflow.certificate.model

data class TemplateResponseContent(
    val code: String = "",
    val created_by: Int = 0,
    val created_date: String = "",
    val data_template: String = "",
    val department_uuid: Int = 0,
    val facility_uuid: Int = 0,
    val is_active: Boolean = false,
    val is_default: Boolean = false,
    val modified_by: Int = 0,
    val modified_date: String = "",
    val name: String = "",
    val note_template_type_uuid: Int = 0,
    val note_type_uuid: Int = 0,
    val revision: Int = 0,
    val status: Boolean = false,
    val uuid: Int = 0
)