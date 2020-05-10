package com.hmisdoctor.ui.emr_workflow.model.templete

data class TemplatesLab(
    var lab_details: List<LabDetail?>? = listOf(),
    var temp_details: TempDetails? = TempDetails()
)