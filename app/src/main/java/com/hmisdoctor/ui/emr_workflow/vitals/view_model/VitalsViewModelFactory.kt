package com.hmisdoctor.ui.emr_workflow.vitals.view_model
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hmisdoctor.ui.emr_workflow.lab.view_model.LabViewModel

class VitalsViewModelFactory(
    private var application: Application


) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VitalsViewModel(
            application
        ) as T
    }
}
