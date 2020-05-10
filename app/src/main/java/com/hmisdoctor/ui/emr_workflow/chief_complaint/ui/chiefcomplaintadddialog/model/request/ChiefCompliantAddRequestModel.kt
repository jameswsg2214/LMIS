package com.hmisdoctor.ui.emr_workflow.chief_complaint.ui.chiefcomplaintadddialog.model.request

data class ChiefCompliantAddRequestModel(
    var details: List<Detail> = listOf(),
    var headers: Headers = Headers()
)