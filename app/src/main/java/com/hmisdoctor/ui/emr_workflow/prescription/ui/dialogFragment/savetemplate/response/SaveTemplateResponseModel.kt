package com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.savetemplate.response

data class SaveTemplateResponseModel(
    var code: Int = 0,
    var message: String = "",
    var saveTemplateResponse: SaveTemplateResponse = SaveTemplateResponse()
)