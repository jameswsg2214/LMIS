package com.hmisdoctor.ui.quick_reg.model.labapprovalresult.response

data class OrderApprovedResponseModel(
    var msg: String = "",
    var responseContents: List<ResponseContent> = listOf(),
    var statusCode: Int = 0
)