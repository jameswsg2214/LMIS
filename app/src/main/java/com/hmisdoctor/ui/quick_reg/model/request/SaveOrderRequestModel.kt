package com.hmisdoctor.ui.quick_reg.model.request

data class SaveOrderRequestModel(
    var details: ArrayList<Detail> = ArrayList(),
    var header: Header = Header()
)