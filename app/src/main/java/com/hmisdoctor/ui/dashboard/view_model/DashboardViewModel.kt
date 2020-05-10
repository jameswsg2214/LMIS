package com.hmisdoctor.ui.dashboard.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.hmisdoctor.R
import com.hmisdoctor.application.HmisApplication
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.callbacks.RetrofitMainCallback
import com.hmisdoctor.config.AppConstants.Companion.BEARER_AUTH
import com.hmisdoctor.ui.dashboard.model.DashBoardResponse
import com.hmisdoctor.ui.emr_workflow.model.EmrWorkFlowResponseModel
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils

class DashboardViewModel(
    application: Application?
) : AndroidViewModel(
    application!!
) {
    var errorText = MutableLiveData<String>()
    var progressBar = MutableLiveData<Int>()
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null

    init {
        progressBar.value = 8
        userDetailsRoomRepository = UserDetailsRoomRepository(application!!)
    }
    fun getEmrWorkFlow(emrWorkFlowRetrofitCallBack: RetrofitCallback<EmrWorkFlowResponseModel>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progressBar.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        apiService?.getEmrWorkflow(
            BEARER_AUTH+userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!)!!.enqueue(
            RetrofitMainCallback(emrWorkFlowRetrofitCallBack)
        )
    }

    fun getPatientsDetails(dashBoardDetailRetrofitCallBack: RetrofitCallback<DashBoardResponse>){
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progressBar.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        apiService?.getDashBoardResponse(
            BEARER_AUTH+userDataStoreBean?.access_token,
            27343,88,"2020-04-01","2020-04-24")!!.enqueue(
            RetrofitMainCallback(dashBoardDetailRetrofitCallBack)
        )
    }
}