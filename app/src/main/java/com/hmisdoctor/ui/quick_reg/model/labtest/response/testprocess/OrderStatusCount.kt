package com.hmisdoctor.ui.quick_reg.model.labtest.response.testprocess

data class OrderStatusCount(
    val order_count: Int? = 0,
    val order_status_name: String? = "",
    val order_status_uuid: Int? = 0
)