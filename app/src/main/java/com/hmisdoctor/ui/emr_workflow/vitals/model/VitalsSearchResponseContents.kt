package com.hmisdoctor.ui.emr_workflow.vitals.model

import com.hmisdoctor.ui.emr_workflow.vitals.model.response.GetVital

data class VitalsSearchResponseContents(
    val getVitals: List<GetVital> = listOf()
)