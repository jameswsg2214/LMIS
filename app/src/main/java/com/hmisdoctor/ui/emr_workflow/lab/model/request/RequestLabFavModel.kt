package com.hmisdoctor.ui.emr_workflow.lab.model.request

data class RequestLabFavModel(
    var details: List<Detail> = listOf(),
    var headers: Headers = Headers()
)