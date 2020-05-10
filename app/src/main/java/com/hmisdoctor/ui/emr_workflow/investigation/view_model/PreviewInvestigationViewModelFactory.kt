package com.hmisdoctor.ui.emr_workflow.view.lab.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.ui.emr_workflow.investigation.model.InvestigationPrevResponseModel
import com.hmisdoctor.ui.emr_workflow.view.lab.model.PrevLabResponseModel

class PreviewInvestigationViewModelFactory(
    private val mApplication: Application,    private val prevLabrecordsRetrofitCallback: RetrofitCallback<InvestigationPrevResponseModel>


) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PreviewInvestigationViewModel(mApplication,prevLabrecordsRetrofitCallback) as T
    }
}