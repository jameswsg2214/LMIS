package com.hmisdoctor.ui.emr_workflow.op_notes.model

data class Sections(
    val code: Any = Any(),
    val description: String = "",
    val display_order: Int = 0,
    val name: String = "",
    val section_note_type_uuid: Int = 0,
    val section_type_uuid: Int = 0,
    val sref: String = "",
    val uuid: Int = 0
)