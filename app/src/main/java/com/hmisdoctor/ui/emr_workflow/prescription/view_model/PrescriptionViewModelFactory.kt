package com.hmisdoctor.ui.emr_workflow.prescription.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
class PrescriptionViewModelFactory(private var application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PrescriptionViewModel(
            application
        ) as T
    }
}