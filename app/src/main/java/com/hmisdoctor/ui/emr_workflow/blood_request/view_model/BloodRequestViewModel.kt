package com.hmisdoctor.ui.emr_workflow.blood_request.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.hmisdoctor.R
import com.hmisdoctor.application.HmisApplication
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.callbacks.RetrofitMainCallback
import com.hmisdoctor.config.AppConstants.Companion.BEARER_AUTH
import com.hmisdoctor.ui.emr_workflow.blood_request.model.*
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils

class BloodRequestViewModel(application: Application) : AndroidViewModel(application) {

    var errorText = MutableLiveData<String>()
    var progress = MutableLiveData<Int>()
    var userDetailsRoomRepository: UserDetailsRoomRepository = UserDetailsRoomRepository(application)

    fun getAllBloodGroup(
        facility_uuid: Int,
        getAllBloodGroupReq: GetAllBloodGroupReq,
        getAllBloodGroupRespCallback: RetrofitCallback<GetAllBloodGroupResp>
    ) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }

        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository.getUserDetails()

        apiService?.getAllBloodGroup(
            "accept",
            BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facility_uuid,
            getAllBloodGroupReq
        )?.enqueue(RetrofitMainCallback(getAllBloodGroupRespCallback))
    }

    fun getAllPurpose(
        facility_uuid: Int,
        getAllPurposeReq: GetAllPurposeReq,
        getAllPurposeRespCallback: RetrofitCallback<GetAllPurposeResp>
    ) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }

        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository.getUserDetails()

        apiService?.getAllPurpose(
            "accept",
            BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facility_uuid,
            getAllPurposeReq
        )?.enqueue(RetrofitMainCallback(getAllPurposeRespCallback))
    }

    fun getPreviousBloodRequest(
        facility_uuid: Int,
        getPreviousBloodReq: GetPreviousBloodReq,
        getPreviousBloodRespRetrofitCallback: RetrofitCallback<GetPreviousBloodResp>
    ) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }

        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository.getUserDetails()

        apiService?.getPreviousBlood(
            "accept",
            BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facility_uuid,
            getPreviousBloodReq
        )?.enqueue(RetrofitMainCallback(getPreviousBloodRespRetrofitCallback))
    }
}