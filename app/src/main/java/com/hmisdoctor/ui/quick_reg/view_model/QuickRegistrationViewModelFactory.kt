package com.hmisdoctor.ui.quick_reg.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class QuickRegistrationViewModelFactory (
    private  var application: Application?) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return QuickRegistrationViewModel(
            application
        ) as T
    }
}
