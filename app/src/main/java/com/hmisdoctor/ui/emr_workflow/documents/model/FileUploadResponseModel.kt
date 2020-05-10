package com.hmisdoctor.ui.emr_workflow.documents.model

data class FileUploadResponseModel(
    val attachment : AttachmentData = AttachmentData(),
    val count: Int = 0,
    val files: List<File> = listOf(),
    val message: String = "",
    val status: Int = 0
)