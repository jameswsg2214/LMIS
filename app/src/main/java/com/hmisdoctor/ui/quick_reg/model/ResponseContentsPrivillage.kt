package com.hmisdoctor.ui.quick_reg.model

data class ResponseContentsPrivillage(
    var allow: Int? = 0,
    var code: String? = "",
    var display_name: String? = "",
    var name: String? = "",
    var uuid: Int? = 0
)