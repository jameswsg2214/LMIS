package com.hmisdoctor.ui.emr_workflow.investigation.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.hmisdoctor.ui.emr_workflow.lab.view_model.LabViewModel
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesResponseModel
import com.hmisdoctor.ui.institute.model.DepartmentResponseModel

class InvestigationViewModelFactory(
    private var application: Application


) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return InvestigationViewModel(
            application
        ) as T
    }
}
