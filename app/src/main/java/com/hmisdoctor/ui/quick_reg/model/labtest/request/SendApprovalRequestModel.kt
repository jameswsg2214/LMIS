package com.hmisdoctor.ui.quick_reg.model.labtest.request

data class SendApprovalRequestModel(
    var Id: ArrayList<Int> = ArrayList(),
    var comments: String = "",
    var doctor_Id: Int = 0
)