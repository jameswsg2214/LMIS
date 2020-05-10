package com.hmisdoctor.ui.emr_workflow.history.allergy.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.hmisdoctor.R
import com.hmisdoctor.application.HmisApplication
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.callbacks.RetrofitMainCallback
import com.hmisdoctor.config.AppConstants.Companion.BEARER_AUTH
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.duration.DurationResponseModel
import com.hmisdoctor.ui.emr_workflow.history.allergy.model.response.*
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject


class AllergyViewModel(
    application: Application) : AndroidViewModel(
    application
) {
    var errorText = MutableLiveData<String>()
    var progress = MutableLiveData<Int>()
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    init {
        userDetailsRoomRepository = UserDetailsRoomRepository(application)
    }


    fun getAllergyTypeList(facility_uuid:Int,getallergyTypeRetrofitCallBack: RetrofitCallback<EncounterAllergyTypeResponse>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.getEncounterAllergyType(BEARER_AUTH+userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facility_uuid
        )?.enqueue(RetrofitMainCallback(getallergyTypeRetrofitCallBack))
        return
    }

    fun getDuration(getDurationRetrofitCallBack: RetrofitCallback<DurationResponseModel>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.getDuration(BEARER_AUTH+userDataStoreBean?.access_token, userDataStoreBean?.uuid!!)?.enqueue(RetrofitMainCallback(getDurationRetrofitCallBack))
        return
    }

    fun getAllergyNamesList(facility_uuid:Int,getallergyNameRetrofitCallBack: RetrofitCallback<AllergyNameResponse>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.getAllergyName(BEARER_AUTH+userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facility_uuid
        )?.enqueue(RetrofitMainCallback(getallergyNameRetrofitCallBack))
        return
    }

    fun getAllergySourceList(facility_uuid:Int,getallergyNameRetrofitCallBack: RetrofitCallback<AllergySourceResponse>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }

        val jsonBody = JSONObject()
        try {
            jsonBody.put("table_name", "allergy_source")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            jsonBody.toString()
        )
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.getAllergySource(BEARER_AUTH+userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facility_uuid,body
        )?.enqueue(RetrofitMainCallback(getallergyNameRetrofitCallBack))
        return
    }

    fun getAllergySeverityList(facility_uuid:Int,getallergyNameRetrofitCallBack: RetrofitCallback<AllergySeverityResponse>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }

        val jsonBody = JSONObject()
        try {
            jsonBody.put("table_name", "allergy_severity")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            jsonBody.toString()
        )
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.getAllergySeverity(BEARER_AUTH+userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facility_uuid,body
        )?.enqueue(RetrofitMainCallback(getallergyNameRetrofitCallBack))
        return
    }


    fun getAllergyAll(facility_uuid:Int,patient_uuid:Int,getAllergyAllRetrofitCallBack: RetrofitCallback<AllergyAllGetResponseModel>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.getAllergyAll(BEARER_AUTH+userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,facility_uuid,patient_uuid)?.enqueue(RetrofitMainCallback(getAllergyAllRetrofitCallBack))
        return
    }


    fun postAllergyData(facility_uuid: Int, allergyRequest: RequestBody, postAllergyRetrofitCallBack: RetrofitCallback<AllergyCreateResponseModel>){

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }

        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.createAllergy(BEARER_AUTH+userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facility_uuid,allergyRequest
        )?.enqueue(RetrofitMainCallback(postAllergyRetrofitCallBack))
        return
    }



    fun updateAllergyData(facility_uuid: Int,uuid : Int, allergyRequest: RequestBody, updateAllergyRetrofitCallBack: RetrofitCallback<AllergyUpdateResponseModel>){

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }

        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.updateAllergy(BEARER_AUTH+userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facility_uuid,uuid,allergyRequest
        )?.enqueue(RetrofitMainCallback(updateAllergyRetrofitCallBack))
        return
    }

}