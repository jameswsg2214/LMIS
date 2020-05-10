package com.hmisdoctor.ui.emr_workflow.history.immunization.model

data class GetImmunizationresponseContent(
    val et_name: String? = "",
    val f_name: String? = "",
    val i_name: String? = "",
    val pis_comments: String? = "",
    val pis_immunization_date: String? = "",
    val pis_uuid: Int? = 0
)