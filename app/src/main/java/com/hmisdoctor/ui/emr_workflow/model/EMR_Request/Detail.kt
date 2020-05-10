package com.hmisdoctor.ui.emr_workflow.model.EMR_Request

data class Detail(
    var encounter_type_uuid: Int = 0,
    var group_uuid: Int = 0,
    var is_profile: Boolean = false,
    var lab_master_type_uuid: Int = 0,
    var order_priority_uuid: Int = 0,
    var details_comments : String ="",
    var profile_uuid: String = "",
    var test_master_uuid: Int = 0,
    var to_department_uuid: Int = 0,
    var to_location_uuid: String = "",
    var to_sub_department_uuid: Int = 0
)