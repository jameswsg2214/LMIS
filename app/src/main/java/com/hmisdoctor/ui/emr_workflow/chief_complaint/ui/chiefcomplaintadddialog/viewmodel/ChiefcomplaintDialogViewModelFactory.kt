package com.hmisdoctor.ui.emr_workflow.chief_complaint.ui.chiefcomplaintadddialog.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hmisdoctor.ui.emr_workflow.lab.view_model.ManageLabFavourteViewModel

class ChiefcomplaintDialogViewModelFactory(
    private  var application: Application?) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChiefcomplaintDialogViewModel(
            application
        ) as T
    }
}