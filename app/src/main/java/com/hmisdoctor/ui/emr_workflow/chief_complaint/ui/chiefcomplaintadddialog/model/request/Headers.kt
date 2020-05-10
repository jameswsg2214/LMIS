package com.hmisdoctor.ui.emr_workflow.chief_complaint.ui.chiefcomplaintadddialog.model.request

data class Headers(
    var department_uuid: String = "",
    var display_order: String = "",
    var facility_uuid: String = "",
    var favourite_type_uuid: Int = 0,
    var is_active: String = "",
    var is_public: Boolean = false,
    var revision: Boolean = false,
    var ticksheet_type_uuid: Int = 0,
    var user_uuid: Int = 0
)