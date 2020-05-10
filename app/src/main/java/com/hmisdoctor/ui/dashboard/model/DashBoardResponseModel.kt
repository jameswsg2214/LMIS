package com.hmisdoctor.ui.dashboard.model


data class DashBoardResponse(
    val responseContents: DashBoardContent? = null,
    val code: Int? = null,
    val message: String? = null
)
    data class DashBoardContent(
        val cieif_complaints: ArrayList<ChiefComplients>? = null,
        val cons_graph: HashMap<String?, Int?>? = null,
        val consulted: ArrayList<Consulted>? = null,
        val diagnosis: ArrayList<Diagnosis>? = null,
        val orders: Orders? = null,
        val orders_graph: HashMap<String?, Int?>?  = null,
        val prescription: ArrayList<Prescription>? = null
    )
        class ConsGraph(
        )

        data class Consulted(
            val F_Count: Int? = null,
            val M_Count: Int? = null,
            val T_Count: Int? = null,
            val Tot_Count: Int? = null)

        data class Orders(
            val inv_count: Int? = null,
            val lab_count: Int? = null,
            val rad_count: Int? = null,
            val total_count: Int? = null
        )

    data class ChiefComplients(val pcc_chief_complaint_uuid : Int? = null,
                           val cc_name: String? = null,
                           val  pcc_performed_date: String? = null,
                           val Count: Int? = null
)

      data class Diagnosis(val pd_diagnosis_uuid: Int ? = null,
                     val d_code: String? = null,
                     val d_name: String? = null,
                     val pd_performed_date: String? = null,
                     val Count: Int? = null )

         class OrdersGraph(
        )

        data class Prescription(
            val F_Count: Int? = null,
            val M_Count: Int? = null,
            val T_Count: Int? = null,
            val Tot_Count: Int? = null
        )

      data class CommonCount(val F_Count: String,
                             val M_Count: String,
                             val T_Count: String,
                             val Tot_Count: String,
                             val title: String,
                             val bgColor: Int)

