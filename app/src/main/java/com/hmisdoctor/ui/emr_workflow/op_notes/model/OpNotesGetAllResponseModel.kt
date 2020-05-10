package com.hmisdoctor.ui.emr_workflow.op_notes.model

data class OpNotesGetAllResponseModel(
    val responseContents: List<OpNotesGetAllresponseContent?>? = listOf(),
    val code: Int? = 0,
    val message: String? = ""
)