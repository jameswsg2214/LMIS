package com.hmisdoctor.ui.emr_workflow.op_notes.model

data class OpNotesGetAllresponseContent(
    val department_uuid: Int? = 0,
    val profile_code: String? = "",
    val profile_description: String? = "",
    val profile_name: String? = "",
    val profile_sections: List<ProfileSection?>? = listOf(),
    val profile_type_uuid: Int? = 0,
    val uuid: Int? = 0
)