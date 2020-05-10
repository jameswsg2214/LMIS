package com.hmisdoctor.ui.emr_workflow.op_notes.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class OpNotesViewModelFactory(
    private var application: Application


) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OpNotesViewModel(
            application
        ) as T
    }
}
