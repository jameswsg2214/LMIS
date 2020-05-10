package com.hmisdoctor.ui.emr_workflow.model

data class ResponseContent(
    var activity_code: String? = null,
    var activity_icon: String? = null,
    var activity_id: Int? = null,
    var activity_name: String? = null,
    var activity_route_url: String? = null,
    var context_activity_map_uuid: Int? = null,
    var context_id: Int? = null,
    var emr_worflow_settings_id: Int? = null,
    var ews_is_active: Boolean? = null,
    var facility_uuid: Int? = null,
    var role_uuid: Int? = null,
    var user_uuid: Int? = null,
    var work_flow_order: Int? = null
)