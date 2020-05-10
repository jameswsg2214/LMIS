package com.hmisdoctor.ui.emr_workflow.diagnosis.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hmisdoctor.ui.emr_workflow.chief_complaint.ui.chiefcomplaintadddialog.viewmodel.ChiefcomplaintDialogViewModel
import com.hmisdoctor.ui.emr_workflow.lab.view_model.ManageLabFavourteViewModel

class DiagonosisDialogViewModelFactory(
    private  var application: Application?) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DiagonosisDialogViewModel(
            application
        ) as T
    }
}