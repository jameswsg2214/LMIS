package com.hmisdoctor.ui.emr_workflow.vitals.model

import com.hmisdoctor.ui.emr_workflow.vitals.model.response.GetVital

data class VitalsListResponseContents(
    val getVitals: List<GetVital> = listOf()
)