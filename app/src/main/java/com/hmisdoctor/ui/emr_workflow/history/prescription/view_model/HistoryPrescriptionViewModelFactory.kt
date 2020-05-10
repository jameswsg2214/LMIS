package com.hmisdoctor.ui.emr_workflow.history.prescription.view_model

import com.hmisdoctor.ui.emr_workflow.history.radiology.viewmodel.HistoryRadiologyViewModel


import android.app.Application
import androidx.lifecycle.ViewModel

import androidx.lifecycle.ViewModelProvider

class HistoryPrescriptionViewModelFactory (
    private  var application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HistoryPrescriptionViewModel(
            application
        ) as T
    }
}
