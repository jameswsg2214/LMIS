package com.hmisdoctor.ui.emr_workflow.prescription.model

data class PrescriptionPostAllDataResponseModel(
    var msg: String? = "",
    var req: PrescriptionResponseReq? = PrescriptionResponseReq(),
    var responseContents: PrescriptionAllDataResponseContents? = PrescriptionAllDataResponseContents(),
    var statusCode: Int? = 0
)