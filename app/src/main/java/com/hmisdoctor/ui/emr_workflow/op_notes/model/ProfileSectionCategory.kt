package com.hmisdoctor.ui.emr_workflow.op_notes.model

data class ProfileSectionCategory(
    val categories: Categories = Categories(),
    val category_uuid: Int = 0,
    val display_order: Int = 0,
    val profile_section_category_concepts: List<ProfileSectionCategoryConcept> = listOf(),
    val profile_section_uuid: Int = 0,
    val uuid: Int = 0
)