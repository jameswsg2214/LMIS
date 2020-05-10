package com.hmisdoctor.ui.emr_workflow.history.config.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.ui.configuration.model.ConfigResponseModel

class HistoryConfigViewModelFactory (
    private  var application: Application?,
    private var configRetrofitCallBack: RetrofitCallback<ConfigResponseModel>) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HistoryConfigViewModel(
            application,
            configRetrofitCallBack

        ) as T
    }
}
