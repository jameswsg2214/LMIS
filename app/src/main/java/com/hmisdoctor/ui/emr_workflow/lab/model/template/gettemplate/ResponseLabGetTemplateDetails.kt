package com.hmisdoctor.ui.emr_workflow.lab.model.template.gettemplate

data class ResponseLabGetTemplateDetails(
    var req: String? = "",
    var responseContent: ResponseContentLabGetDetails? = ResponseContentLabGetDetails(),
    var statusCode: Int? = 0
)