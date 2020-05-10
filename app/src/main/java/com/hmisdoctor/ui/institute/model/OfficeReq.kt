package com.hmisdoctor.ui.institute.model

data class OfficeReq(
    var pageNo: Int? = 0,
    var paginationSize: Int? = 0,
    var search: String? = "",
    var sortField: String? = "",
    var sortOrder: String? = "",
    var user_uuid: String? = ""
)