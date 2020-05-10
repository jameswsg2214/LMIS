package com.hmisdoctor.ui.quick_reg.model

data class SampleErrorResponse(
    var statusCode: Int? = 0,
    var printValidationError: PrintValidation? = PrintValidation()

)