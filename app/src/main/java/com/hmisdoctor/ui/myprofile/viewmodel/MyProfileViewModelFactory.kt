package com.hmisdoctor.ui.login.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.ui.myprofile.model.MyProfileResponseModel
import com.hmisdoctor.ui.myprofile.viewmodel.MyProfileViewModel

class MyProfileViewModelFactory (
    private  var application: Application?) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MyProfileViewModel(
            application

        ) as T
    }
}
