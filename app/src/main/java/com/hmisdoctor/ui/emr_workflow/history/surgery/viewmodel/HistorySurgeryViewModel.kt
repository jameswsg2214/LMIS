package com.hmisdoctor.ui.emr_workflow.history.surgery.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.hmisdoctor.R
import com.hmisdoctor.application.HmisApplication
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.callbacks.RetrofitMainCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppConstants.Companion.BEARER_AUTH

import com.hmisdoctor.config.AppConstants.Companion.FAV_TYPE_ID_CHIEF_COMPLAINTS
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesResponseModel
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.duration.DurationResponseModel
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.request.ChiefComplaintRequestModel
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.response.ChiefComplaintResponse
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.search_complaint.ComplaintSearchResponseModel
import com.hmisdoctor.ui.emr_workflow.history.allergy.model.Request.AllergySourceRequestModel
import com.hmisdoctor.ui.emr_workflow.history.allergy.model.response.*
import com.hmisdoctor.ui.emr_workflow.history.model.response.HistoryResponce
import com.hmisdoctor.ui.emr_workflow.history.surgery.model.response.*
import com.hmisdoctor.ui.emr_workflow.view.lab.model.PrevLabResponseModel
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject

class HistorySurgeryViewModel(
    application: Application) : AndroidViewModel(
    application
) {
    var progressBar = MutableLiveData<Int>()
    var errorText = MutableLiveData<String>()
    private var noInternetLayout: MutableLiveData<Int>? = null
    var errorTextVisibility = MutableLiveData<Int>()

    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    var appPreferences: AppPreferences? = null



    init {
        errorTextVisibility.value = 8
        progressBar.value = 8
        userDetailsRoomRepository = UserDetailsRoomRepository(application)
        appPreferences = AppPreferences.getInstance(application, AppConstants.SHARE_PREFERENCE_NAME)
    }


    fun getSurgeryInstitutionCallback(userId: Int?, facilityid: Int?, SurgeryInstitutionCallback: RetrofitCallback<SurgeryInstitutionResponseModel>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        val jsonBody = JSONObject()
        try {
            jsonBody.put("userId", userId.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            jsonBody.toString()
        )
        progressBar.value = 0
        val hmisApplication = HmisApplication.get(getApplication())
        val apiService = hmisApplication.getRetrofitService()
        apiService?.getInstitutions(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!, facilityid!!,body)?.enqueue(RetrofitMainCallback(SurgeryInstitutionCallback))
    }

    fun getSurgeryCallback(patientId: Int?, facilityid: Int?, surgeryCallback: RetrofitCallback<HistorySurgeryResponseModel>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        progressBar.value = 0
        val hmisApplication = HmisApplication.get(getApplication())
        val apiService = hmisApplication.getRetrofitService()
        apiService?.getSurgery(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!, facilityid!!,1,patientId!!)?.enqueue(RetrofitMainCallback(surgeryCallback))
    }

    fun getSurgeryNameCallback(facilityid: Int?, SurgeryNameCallback: RetrofitCallback<SurgeryNameResponseModel>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        val labMasterTypeUUID = appPreferences?.getInt(AppConstants?.LAB_MASTER_TYPE_ID)
        val jsonBody = JSONObject()
        try {
            jsonBody.put("table_name", "procedures")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            jsonBody.toString()
        )
        progressBar.value = 0
        val hmisApplication = HmisApplication.get(getApplication())
        val apiService = hmisApplication.getRetrofitService()
        apiService?.getName(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!, facilityid!!,body)?.enqueue(RetrofitMainCallback(SurgeryNameCallback))
    }


    fun createSurgery(facility_uuid: Int, createSurgery: RequestBody, createSurgeryHistoryRetrofitCallBack: RetrofitCallback<CreateSurgeryResponseModel>){

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }

        progressBar.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.createSurgery(BEARER_AUTH+userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facility_uuid,createSurgery
        )?.enqueue(RetrofitMainCallback(createSurgeryHistoryRetrofitCallBack))
        return
    }


    fun updateSurgery(facility_uuid: Int,uuid : Int, surgeryRequest: RequestBody, updateSurgeryRetrofitCallBack: RetrofitCallback<SurgeryUpdateResponseModel>){

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }

        progressBar.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.updateSurgery(BEARER_AUTH+userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facility_uuid,uuid,surgeryRequest
        )?.enqueue(RetrofitMainCallback(updateSurgeryRetrofitCallBack))
        return
    }


}