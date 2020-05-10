package com.hmisdoctor.ui.emr_workflow.op_notes.model

data class ProfileSection(
    val activity_uuid: Int = 0,
    val display_order: Int = 0,
    val profile_section_categories: List<ProfileSectionCategory> = listOf(),
    val profile_uuid: Int = 0,
    val section_uuid: Int = 0,
    val sections: Sections = Sections(),
    val uuid: Int = 0
)