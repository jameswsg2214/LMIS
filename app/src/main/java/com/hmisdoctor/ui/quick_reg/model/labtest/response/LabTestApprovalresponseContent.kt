package com.hmisdoctor.ui.quick_reg.model.labtest.response

data class LabTestApprovalresponseContent(
    val age: String? = "",
    val ageperiod: String? = "",
    val auth_status_name: String? = "",
    val auth_status_uuid: Int? = 0,
    val dispatch_nr: Any? = Any(),
    val dispatch_uuid: Any? = Any(),
    val dob: String? = "",
    val doctor_first_name: String? = "",
    val doctor_last_name: Any? = Any(),
    val doctor_title: String? = "",
    val doctor_uuid: Int? = 0,
    val first_name: String? = "",
    val from_department_code: String? = "",
    val from_department_name: String? = "",
    val from_department_uuid: Int? = 0,
    val from_facility_code: String? = "",
    val from_facility_name: String? = "",
    val from_facility_uuid: Int? = 0,
    val gender_name: String? = "",
    val gender_uuid: Int? = 0,
    val is_approval_requried: Boolean? = false,
    val is_profile: Boolean? = false,
    val last_name: String? = "",
    val location_code: String? = "",
    val location_name: String? = "",
    val middle_name: String? = "",
    val mobile: String? = "",
    val modified_date: String? = "",
    val order_number: Int? = 0,
    val order_priority_name: String? = "",
    val order_priority_uuid: Int? = 0,
    val order_request_date: String? = "",
    val order_status_name: String? = "",
    val order_status_uuid: Int? = 0,
    val patient_orders_uuid: Int? = 0,
    val patient_uuid: Int? = 0,
    val pattitle: Any? = Any(),
    val profile_code: Any? = Any(),
    val profile_name: Any? = Any(),
    val profile_uuid: Any? = Any(),
    val qualifier_uuid: Int = 0,
    val sample_identifier: String? = "",
    val sample_transport_details_uuid: Any? = Any(),
    val send_approver_uuid: Int? = 0,
    val specimen_type_code: String? = "",
    val specimen_type_name: String? = "",
    val specimen_type_uuid: Int? = 0,
    val st_from_facility_code: Any? = Any(),
    val st_from_facility_name: Any? = Any(),
    val st_from_facility_uuid: Any? = Any(),
    val test_code: String? = "",
    val test_master_uuid: Int? = 0,
    val test_method_code: String? = "",
    val test_method_name: String? = "",
    val test_method_uuid: Int? = 0,
    val test_name: String? = "",
    val to_department_code: String? = "",
    val to_department_name: String? = "",
    val to_department_uuid: Int? = 0,
    val to_facility_code: Any? = Any(),
    val to_facility_name: Any? = Any(),
    val to_facility_uuid: Any? = Any(),
    val to_location_uuid: Int? = 0,
    val to_sub_department_code: Any? = Any(),
    val to_sub_department_name: Any? = Any(),
    val to_sub_department_uuid: Int? = 0,
    val uhid: String? = "",
    val uuid: Int? = 0,
    val work_order_status_name: Any? = Any(),
    val work_order_status_uuid: Any? = Any(),
    var is_selected: Boolean? = false,
    var is_radio_selected : Boolean? = false,
    var checkboxdeclardtatus : Boolean ?=false,
    var radioselectName : Int? = 0
)