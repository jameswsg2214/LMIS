package com.hmisdoctor.ui.emr_workflow.prescription.model

data class PrescriptionFrequencyresponseContent(
    val code: String? = "",
    val comments: String? = "",
    val created_by: Int? = 0,
    val created_date: String? = "",
    val description: String? = "",
    val display: String? = "",
    val facility_uuid: Int? = 0,
    val is_active: Boolean? = false,
    val modified_by: Int? = 0,
    val modified_date: String? = "",
    val name: String? = "",
    val nooftimes: Int? = 0,
    val perdayquantity: Int? = 0,
    val product_type: ProductType? = ProductType(),
    val product_type_uuid: Int? = 0,
    val revision: Int? = 0,
    val status: Boolean? = false,
    val uuid: Int? = 0
)