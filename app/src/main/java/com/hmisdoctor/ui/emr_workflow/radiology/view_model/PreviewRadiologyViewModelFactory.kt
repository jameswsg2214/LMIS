package com.hmisdoctor.ui.emr_workflow.view.lab.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.ui.emr_workflow.view.lab.model.PrevLabResponseModel

class PreviewRadiologyViewModelFactory(
    private val mApplication: Application,    private val prevLabrecordsRetrofitCallback: RetrofitCallback<PrevLabResponseModel>


) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PreviewRadiologyViewModel(mApplication,prevLabrecordsRetrofitCallback) as T
    }
}