package com.hmisdoctor.ui.emr_workflow.op_notes.model

data class OpNotesResponsModel(
    val message: String = "",
    val req: String = "",
    val responseContents: List<OpNotesResponseContent> = listOf(),
    val statusCode: Int = 0,
    val totalRecords: Int = 0
)