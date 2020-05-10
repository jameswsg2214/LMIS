package com.hmisdoctor.ui.emr_workflow.view.lab.model

data class PrevLabResponseContent(
    val created_date: String = "",
    val department_name: Any = Any(),
    val department_uuid: Int = 0,
    val doctor_name: String = "",
    val doctor_uuid: Int = 0,
    val order_status: String = "",
    val pod_arr_result: List<PodArrResult> = listOf()
)