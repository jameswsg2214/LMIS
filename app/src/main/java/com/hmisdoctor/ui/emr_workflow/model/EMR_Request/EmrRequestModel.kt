package com.hmisdoctor.ui.emr_workflow.model.EMR_Request

data class EmrRequestModel(
    var details: ArrayList<Detail> = ArrayList(),
    var header: Header = Header()
)