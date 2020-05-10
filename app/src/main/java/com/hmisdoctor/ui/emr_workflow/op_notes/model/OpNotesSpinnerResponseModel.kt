package com.hmisdoctor.ui.emr_workflow.op_notes.model

data class OpNotesSpinnerResponseModel(
    val responseContents: List<OpNotesSpinnerresponseContent?>? = listOf(),
    val message: String? = "",
    val req: String? = "",
    val statusCode: Int? = 0,
    val totalRecords: Int? = 0
)