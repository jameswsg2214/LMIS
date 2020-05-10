package com.hmisdoctor.ui.emr_workflow.prescription.model

data class Header(
    var department_uuid: String? = "",
    var dispense_status_uuid: Int? = 0,
    var doctor_uuid: String? = "",
    var encounter_type_uuid: Int? = 0,
    var encounter_uuid: Int? = 0,
    var is_digitally_signed: Boolean? = false,
    var notes: String? = "",
    var patient_uuid: String? = "",
    var prescription_status_uuid: Int? = 0,
    var store_master_uuid: Int? = 0
)