package com.hmisdoctor.ui.emr_workflow.op_notes.model

data class ProfileSectionCategoryConcept(
    val code: String = "",
    val description: String = "",
    val display_order: Int = 0,
    val is_mandatory: Boolean = false,
    val is_multiple: Boolean = false,
    val name: String = "",
    val profile_section_category_concept_values: List<ProfileSectionCategoryConceptValue> = listOf(),
    val profile_section_category_uuid: Int = 0,
    val uuid: Int = 0,
    val value_type_uuid: Int = 0,
    val value_types: ValueTypes = ValueTypes()
)