package com.hmisdoctor.ui.emr_workflow.lab.model.template.request

import com.hmisdoctor.ui.emr_workflow.lab.model.template.request.Detail
import com.hmisdoctor.ui.emr_workflow.lab.model.template.request.Headers

data class RequestTemplateAddDetails(

    var details: List<Detail?>? = listOf(),
    var headers: Headers? = Headers()
)