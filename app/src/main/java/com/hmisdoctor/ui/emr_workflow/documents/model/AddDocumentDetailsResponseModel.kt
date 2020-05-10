package com.hmisdoctor.ui.emr_workflow.documents.model

data class AddDocumentDetailsResponseModel(
    val req: String = "",
    val responseContents: AddDocumentDetailsResponseContents = AddDocumentDetailsResponseContents(),
    val statusCode: Int = 0
)