package com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.savetemplate.request

data class SaveTemplateRequestModel(
        var details: ArrayList<Detail> = ArrayList(),
        var headers: Headers = Headers()
)