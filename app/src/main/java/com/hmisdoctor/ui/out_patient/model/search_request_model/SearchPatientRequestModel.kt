package com.hmisdoctor.ui.out_patient.model.search_request_model

data class SearchPatientRequestModel(
    var mobile: String? = null,
    var pageNo: Int? = null,
    var paginationSize: Int? = null,
    var pin: String? = null,
    var sortField: String? = null,
    var sortOrder: String? = null
)