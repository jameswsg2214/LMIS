package com.hmisdoctor.ui.configuration.view

import com.hmisdoctor.ui.configuration.model.ConfigResponseContent

interface ConfigFinalData {
    fun onConfigFinalData(configResponseContent: ConfigResponseContent?)
}
