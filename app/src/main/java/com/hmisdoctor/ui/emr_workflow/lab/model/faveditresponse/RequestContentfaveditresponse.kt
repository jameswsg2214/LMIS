package com.hmisdoctor.ui.emr_workflow.lab.model.faveditresponse

data class RequestContentfaveditresponse(
    var departmentId: String? = "",
    var favourite_display_order: Int? = 0,
    var favourite_id: Int? = 0,
    var is_active: Boolean? = false,
    var testname: String? = ""
)