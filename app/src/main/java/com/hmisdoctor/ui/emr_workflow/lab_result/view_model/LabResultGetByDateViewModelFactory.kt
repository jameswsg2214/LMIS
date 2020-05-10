package com.hmisdoctor.ui.emr_workflow.chief_complaint.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hmisdoctor.ui.emr_workflow.lab_result.view_model.LabResultGetByDateViewModel
import com.hmisdoctor.ui.emr_workflow.lab_result.view_model.LabResultViewModel

class LabResultGetByDateViewModelFactory(private var application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LabResultGetByDateViewModel(
            application
        ) as T
    }
}
