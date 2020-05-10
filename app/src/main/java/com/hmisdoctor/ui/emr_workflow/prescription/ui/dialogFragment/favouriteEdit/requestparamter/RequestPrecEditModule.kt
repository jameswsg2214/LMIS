package com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.favouriteEdit.requestparamter

data class RequestPrecEditModule(
    var details: List<Detail?>? = listOf(),
    var headers: Headers? = Headers()
)