package com.hmisdoctor.ui.quick_reg.model.request

data class LabNameSearchResponseModel(
    var responseContents: ArrayList<LabName> = ArrayList(),
    var status: String = "",
    var statusCode: Int = 0
)