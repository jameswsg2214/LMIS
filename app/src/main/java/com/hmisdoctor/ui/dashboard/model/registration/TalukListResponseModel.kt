package com.hmisdoctor.ui.dashboard.model.registration

data class TalukListResponseModel(
    var responseContents: ArrayList<Taluk> = ArrayList(),
    var req: String = "",
    var statusCode: Int = 0
)