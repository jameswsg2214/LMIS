package com.hmisdoctor.ui.emr_workflow.diagnosis.model.diagonosissearch

data class DiagonosisSearchResponse(
    val code: Int = 0,
    val message: String = "",
    val responseContents: List<ResponseContentsdiagonosissearch> = listOf()
)