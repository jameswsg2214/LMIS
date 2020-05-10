package com.hmisdoctor.ui.emr_workflow.vitals.model

data class TemplateDetail(
    val active_from: String = "",
    val active_to: String = "",
    val code: Any = Any(),
    val comments: Any = Any(),
    val created_by: Int = 0,
    val created_date: String = "",
    val department_uuid: Int = 0,
    val description: String = "",
    val diagnosis_uuid: Int = 0,
    val display_order: Int = 0,
    val facility_uuid: Int = 0,
    val is_active: Boolean = false,
    val is_public: Boolean = false,
    val modified_by: Int = 0,
    val modified_date: String = "",
    var name: String = "",
    val revision: Int = 0,
    val status: Boolean = false,
    val template_master_details: List<TemplateMasterDetail> = listOf(),
    val template_type_uuid: Int = 0,
    val user_uuid: Int = 0,
    val uuid: Int = 0,
    var isSelected: Boolean? = false,
    var position: Int? = 0,
    var itemAppendString: String? = ""
)