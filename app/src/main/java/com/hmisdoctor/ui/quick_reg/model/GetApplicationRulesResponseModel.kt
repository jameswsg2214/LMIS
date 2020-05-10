package com.hmisdoctor.ui.quick_reg.model

data class GetApplicationRulesResponseModel(
    var responseContents: ArrayList<GetApplicationRules> = ArrayList(),
    var currentDateTime: String = "",
    var req: String = "",
    var statusCode: Int = 0
)