package com.hmisdoctor.ui.quick_reg.model.request

data class QuickRegistrationUpdateRequestModel(
    var address_line1: String? = "",
    var age: Int? = 0,
    var block_uuid: Int? = 0,
    var country_uuid: Int? = 0,
    var department_uuid: String? = "",
    var district_uuid: Int? = 0,
    var dob: String? = "",
    var first_name: String? = "",
    var gender_uuid: Int? = 0,
    var ili: Boolean? = false,
    var is_adult: Int? = 0,
    var is_dob_auto_calculate: Int? = 0,
    var is_rapid_test: Boolean? = false,
    var mobile: String? = "",
    var no_symptom: Boolean? = false,
    var period_uuid: Int? = 0,
    var pincode: String? = "",
    var registred_facility_uuid: String? = "",
    var sample_type_uuid: Int? = 0,
    var sari: Boolean? = false,
    var saveExists: Boolean? = false,
    var session_uuid: Int? = 0,
    var state_uuid: Int? = 0,
    var isDrMobileApi: Int? = 0,

    var uuid: Int? = 0,
    var lab_to_facility_uuid:Int?=0
)