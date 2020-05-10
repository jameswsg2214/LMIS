package com.hmisdoctor.ui.emr_workflow.radiology_result.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.hmisdoctor.R
import com.hmisdoctor.application.HmisApplication
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.callbacks.RetrofitMainCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.ui.emr_workflow.radiology_result.model.RadiologyResultResponseModel
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject

class RadiologyResultViewModel(
    application: Application
) : AndroidViewModel(
    application
) {
    private var department_UUID: Int? = 0
    var errorText = MutableLiveData<String>()
    var progress = MutableLiveData<Int>()
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    var errorTextVisibility = MutableLiveData<Int>()

    var appPreferences: AppPreferences? = null



    init {
        errorTextVisibility.value = 8
        progress.value = 8
        userDetailsRoomRepository = UserDetailsRoomRepository(application)

    }
    fun getRadiology_Result(patientId: Int?, facilityid: Int?, LabResultRetrofitCallback: RetrofitCallback<RadiologyResultResponseModel>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }

        Log.i("",""+patientId)
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        val jsonBody = JSONObject()
        try {
          /*  jsonBody.put("patient_id", patientId)
            jsonBody.put("from_date", "Invalid date")*/

            jsonBody.put("Id", userDataStoreBean?.uuid!!)


        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            jsonBody.toString()
        )
        progress.value = 0
        val hmisApplication = HmisApplication.get(getApplication())
        val apiService = hmisApplication.getRetrofitService()
        apiService?.getRadiologyResult(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!, facilityid!!,body)?.enqueue(RetrofitMainCallback(LabResultRetrofitCallback))
    }





}