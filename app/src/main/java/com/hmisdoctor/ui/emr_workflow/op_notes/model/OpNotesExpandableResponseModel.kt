package com.hmisdoctor.ui.emr_workflow.op_notes.model

data class OpNotesExpandableResponseModel(
    val code: Int = 0,
    val message: String = "",
    val responseContents: List<OpNotesExpandableResponseContent> = listOf()
)