package com.hmisdoctor.ui.emr_workflow.prescription.view_model

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
import com.hmisdoctor.ui.emr_workflow.prescription.model.PrevPrescriptionModel
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject

class PrescriptionPreviewModel(
    application: Application
) : AndroidViewModel(application) {
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

    }
    fun getPrevPrescription_Records(
        patientId: Int?,
        facilityid: Int?,
        prevPrescriptionrecordsRetrofitCallback: RetrofitCallback<PrevPrescriptionModel>
    ) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        Log.i("",""+patientId)
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        val jsonBody = JSONObject()
        try {
            jsonBody.put("patient_uuid", patientId)

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
        apiService?.getPrevPrescription(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!, facilityid!!,body)?.enqueue(RetrofitMainCallback(prevPrescriptionrecordsRetrofitCallback))
    }


}