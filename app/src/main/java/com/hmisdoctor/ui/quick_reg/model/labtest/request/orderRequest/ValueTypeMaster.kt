package com.hmisdoctor.ui.quick_reg.model.labtest.request.orderRequest

data class ValueTypeMaster(
    var code: String = "",
    var created_by: Int = 0,
    var created_date: String = "",
    var is_active: Boolean = false,
    var modified_by: Int = 0,
    var modified_date: String = "",
    var name: String = "",
    var revision: Int = 0,
    var status: Boolean = false,
    var uuid: Int = 0
)