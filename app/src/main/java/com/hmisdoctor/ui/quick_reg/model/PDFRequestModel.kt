package com.hmisdoctor.ui.quick_reg.model

data class PDFRequestModel(
    var addressDetails: AddressDetails? = AddressDetails(),
    var age: Int? = 0,
    var componentName: String? = "",
    var facilityName: String? = "",
    var firstName: String? = "",
    var gender: String? = "",
    var ili: Boolean? = false,
    var mobile: String? = "",
    var noSymptom: Boolean? = false,
    var period: String? = "",
    var registered_date: String? = "",
    var sari: Boolean? = false,
    var testMethod: String? = "",
    var uhid: String? = "",
    var uuid: Int? = 0
)