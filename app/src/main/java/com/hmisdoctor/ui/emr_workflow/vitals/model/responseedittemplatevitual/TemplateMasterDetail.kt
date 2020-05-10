package com.hmisdoctor.ui.emr_workflow.vitals.model.responseedittemplatevitual

data class TemplateMasterDetail(
    var chief_complaint_uuid: Int? = 0,
    var comments: Any? = Any(),
    var created_by: Int? = 0,
    var created_date: String? = "",
    var diet_category_uuid: Int? = 0,
    var diet_frequency_uuid: Int? = 0,
    var diet_master_uuid: Int? = 0,
    var display_order: Int? = 0,
    var drug_frequency_uuid: Int? = 0,
    var drug_instruction_uuid: Int? = 0,
    var drug_route_uuid: Int? = 0,
    var duration: Int? = 0,
    var duration_period_uuid: Int? = 0,
    var is_active: Boolean? = false,
    var item_master_uuid: Int? = 0,
    var modified_by: Int? = 0,
    var modified_date: String? = "",
    var quantity: Int? = 0,
    var revision: Int? = 0,
    var status: Boolean? = false,
    var template_master_uuid: Int? = 0,
    var test_master_uuid: Int? = 0,
    var uuid: Int? = 0,
    var vital_master: VitalMaster? = VitalMaster(),
    var vital_master_uuid: Int? = 0
)