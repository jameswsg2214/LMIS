package com.hmisdoctor.ui.dashboard.model.registration

data class PatientDetail(
    val aadhaar_number: String = "",
    val address_line1: String = "",
    val address_line2: String = "",
    val address_line3: Any = Any(),
    val address_line4: Any = Any(),
    val address_line5: String = "",
    val airport_name: Any = Any(),
    val alternate_number:String ="",
    val attender_contact_number: Any = Any(),
    val attender_name: Any = Any(),
    val bill_class: Any = Any(),
    val city_uuid: Int = 0,
    val community_uuid: Int = 0,
    val complication_uuid: Int = 0,
    val contact_history: Boolean = false,
    val contact_history_details: Any = Any(),
    val country_uuid: Int = 0,
    val created_by: Int = 0,
    val created_date: String = "",
    val date_of_onset: Any = Any(),
    val death_approved_by: Int = 0,
    val death_coments: Any = Any(),
    val death_confirmed_by: Int = 0,
    val death_place: String = "",
    val death_updated_by: Int = 0,
    val death_updated_date: Any = Any(),
    val district_uuid: Int = 0,
    val email: String = "",
    val height: String = "",
    val income_uuid: Int = 0,
    val is_active: Boolean = false,
    val is_death_confirmed: Any = Any(),
    val is_email_communication_preference: Any = Any(),
    val is_sms_communication_preference: Any = Any(),
    val location_travelled: Any = Any(),
    val marital_uuid: Int = 0,
    val mobile: String = "",
    val modified_by: Int = 0,
    val modified_date: String = "",
    val nationality_type_uuid: Int = 0,
    val occupation_uuid: Int = 0,
    val op_status: Any = Any(),
    val other_proof_number: Any = Any(),
    val other_proof_uuid: Int = 0,
    val out_come_date: Any = Any(),
    val out_come_type_uuid: Int = 0,
    val para_1: String = "",
    val patient_uuid: Int = 0,
    val photo_path: String = "",
    val pin_status: Any = Any(),
    val pincode: String = "",
    val place_uuid: Int = 0,
    val quarantine_status_date: String = "",
    val quarentine_status: Any = Any(),
    val quarentine_status_type_uuid: Int = 0,
    val referred_doctor: Any = Any(),
    val relation_type_uuid: Int = 0,
    val religion_uuid: Int = 0,
    val repeat_test: Any = Any(),
    val repeat_test_date: Any = Any(),
    val repeat_test_type_uuid: Int = 0,
    val revision: Boolean = false,
    val smart_ration_number: String = "",
    val state_uuid: Int = 0,
    val symptom_duration: Any = Any(),
    val symptoms: Boolean = false,
    val taluk_uuid: Int = 0,
    val test_location: Any = Any(),
    val test_result: Boolean = false,
    val travel_history: Any = Any(),
    val travel_history_date: Any = Any(),
    val travel_history_to_date: Any = Any(),
    val treatment_category_uuid: Int = 0,
    val treatment_plan_uuid: Int = 0,
    val underline_medicine_condition_uuid: Int = 0,
    val underline_medicine_details: Any = Any(),
    val uuid: Int = 0,
    val village_uuid: Int = 0,
    val weight: String = ""
)