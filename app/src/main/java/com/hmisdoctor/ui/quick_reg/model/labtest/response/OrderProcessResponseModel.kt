package com.hmisdoctor.ui.quick_reg.model.labtest.response

data class OrderProcessResponseModel(
    var responseContents: OrderProcess = OrderProcess(),
    var msg: String = "",
    var statusCode: Int = 0
)