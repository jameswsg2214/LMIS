package com.hmisdoctor.ui.emr_workflow.history.surgery.model.response

data class InstitutionresponseContent(
    val facility: Facility? = Facility(),
    val facility_uuid: Int? = 0,
    val user_uuid: Int? = 0,
    val uuid: Int? = 0
)