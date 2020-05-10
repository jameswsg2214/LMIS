package com.hmisdoctor.ui.institute.model

data class OfficeResponseModel(
    var responseContents: List<OfficeResponseContent?>? = listOf(),
    var req: Req? = Req(),
    var statusCode: Int? = 0
)