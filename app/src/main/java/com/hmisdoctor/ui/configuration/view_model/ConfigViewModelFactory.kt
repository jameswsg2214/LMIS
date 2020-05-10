package com.hmisdoctor.ui.configuration.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.ui.configuration.model.ConfigResponseModel

class ConfigViewModelFactory (
    private  var application: Application?,
    private var configRetrofitCallBack: RetrofitCallback<ConfigResponseModel>) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ConfigViewModel(
            application,
            configRetrofitCallBack

        ) as T
    }
}
