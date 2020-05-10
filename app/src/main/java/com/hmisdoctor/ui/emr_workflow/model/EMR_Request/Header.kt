package com.hmisdoctor.ui.emr_workflow.model.EMR_Request

data class Header(
    var consultation_uuid: Int = 0,
    var department_uuid: String = "",
    var doctor_uuid: String = "",
    var encounter_type_uuid: Int = 0,
    var encounter_uuid: Int = 0,
    var facility_uuid: String = "",
    var lab_master_type_uuid: Int = 0,
    var order_status_uuid: Int = 0,
    var order_to_location_uuid: Int = 0,
    var patient_treatment_uuid: Int = 0,
    var patient_uuid: String = "",
    var sub_department_uuid: Int = 0,
    var treatment_plan_uuid: Int = 0
)