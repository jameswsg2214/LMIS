package com.hmisdoctor.ui.emr_workflow.chief_complaint.ui.chiefcomplaintadddialog.model

data class chiefComplaintFavEditResponse(
    var requestContent: ChiefComplaintFavEditResponseList = ChiefComplaintFavEditResponseList(),
    var code: Int = 0,
    var message: String = ""
)