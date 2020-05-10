package com.hmisdoctor.ui.emr_workflow.documents.model

data class DocumentTypeResponseModel(
    val req: String = "",
    val responseContents: List<DocumentTypeResponseContent> = listOf(),
    val statusCode: Int = 0
)