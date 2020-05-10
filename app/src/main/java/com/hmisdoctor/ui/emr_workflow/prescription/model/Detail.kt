package com.hmisdoctor.ui.emr_workflow.prescription.model

data class Detail(
    var comments: String? = "",
    var drug_frequency_uuid: String? = "",
    var drug_instruction_uuid: String? = "",
    var drug_route_uuid: String? = "",
    var duration: String? = "",
    var duration_period_uuid: String? = "",
    var is_emar: Boolean? = false,
    var item_master_uuid: Int? = 0,
    var prescribed_quantity: Int? = 0
)