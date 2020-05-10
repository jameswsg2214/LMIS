package com.hmisdoctor.ui.emr_workflow.treatment_kit.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class TreatmentKitViewModelFactory (
    private  var application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TreatmentKitViewModel(
            application
        ) as T
    }
}
