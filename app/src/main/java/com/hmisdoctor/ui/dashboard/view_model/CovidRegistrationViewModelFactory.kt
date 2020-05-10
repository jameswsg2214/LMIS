package com.hmisdoctor.ui.dashboard.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CovidRegistrationViewModelFactory (
    private  var application: Application?) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CovidRegistrationViewModel(
            application
        ) as T
    }
}
