package com.hmisdoctor.ui.emr_workflow.treatment_kit.model

data class Diagnosi(
    val diagnosis_code: String = "",
    val diagnosis_description: String = "",
    val diagnosis_id: Int = 0,
    val diagnosis_name: String = "",
    val order_id: Int = 0,
    val uuid: Int = 0
)