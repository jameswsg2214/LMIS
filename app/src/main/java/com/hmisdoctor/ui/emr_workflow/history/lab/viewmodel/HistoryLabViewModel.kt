package com.hmisdoctor.ui.emr_workflow.history.lab.viewmodel

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
import com.hmisdoctor.ui.emr_workflow.view.lab.model.PrevLabResponseModel
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject

class HistoryLabViewModel(
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


    fun getHistoryLabCallback(patientId: String?, facilityid: Int?, historyRadiologyRetrofitCallback: RetrofitCallback<PrevLabResponseModel>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        //Log.i("",""+patientId)
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        val jsonBody = JSONObject()
        try {
            jsonBody.put("patient_id", patientId)
            jsonBody.put("lab_master_type_uuid", AppConstants?.LAB_TESTMASTER_UUID)
            jsonBody.put("limit",10)
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
        apiService?.getPrevLab(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!, facilityid!!,body)?.enqueue(RetrofitMainCallback(historyRadiologyRetrofitCallback))
    }


}