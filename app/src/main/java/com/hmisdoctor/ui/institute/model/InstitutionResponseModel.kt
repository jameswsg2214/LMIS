package com.hmisdoctor.ui.institute.model

data class InstitutionResponseModel(
    var req: Req? = Req(),
    var responseContents: ArrayList<InstitutionResponseContent?>? = ArrayList(),
    var statusCode: Int? = 0
)