package com.hmisdoctor.ui.institute.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.ui.institute.model.OfficeResponseModel

class InstituteViewModelFactory (
    private  var application: Application?,
    private var officeRetrofitCallBack: RetrofitCallback<OfficeResponseModel>) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return InstituteViewModel(
            application,
            officeRetrofitCallBack
        ) as T
    }
}
