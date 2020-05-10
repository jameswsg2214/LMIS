package com.hmisdoctor.ui.emr_workflow.vitals.model.responseedittemplatevitual

data class ResponseEditTemplate(
    var req: String? = "",
    var responseContent: ResponseContentedittemplatevitual? = ResponseContentedittemplatevitual(),
    var statusCode: Int? = 0
)