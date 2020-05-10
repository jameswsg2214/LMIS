package com.hmisdoctor.ui.emr_workflow.vitals.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.hmisdoctor.R
import com.hmisdoctor.application.HmisApplication
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.callbacks.RetrofitMainCallback
import com.hmisdoctor.config.AppConstants


import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.ui.emr_workflow.delete.model.DeleteResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.template.gettemplate.ResponseLabGetTemplateDetails
import com.hmisdoctor.ui.emr_workflow.model.templete.TempleResponseModel
import com.hmisdoctor.ui.emr_workflow.vitals.model.UomListResponceModel
import com.hmisdoctor.ui.emr_workflow.vitals.model.VitalSaveRequestModel
//import com.hmisdoctor.ui.emr_workflow.vitals.model.UOMListResponseModel
import com.hmisdoctor.ui.emr_workflow.vitals.model.VitalsListResponseModel
import com.hmisdoctor.ui.emr_workflow.vitals.model.VitalsTemplateResponseModel
import com.hmisdoctor.ui.emr_workflow.vitals.model.response.VitalSaveResponseModel
import com.hmisdoctor.ui.emr_workflow.vitals.model.response.VitalSearchListResponseModel
import com.hmisdoctor.ui.emr_workflow.vitals.model.responseedittemplatevitual.ResponseEditTemplate
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject

class VitalsViewModel(
    application: Application
) : AndroidViewModel(
    application
) {
    var errorText = MutableLiveData<String>()
    var progress = MutableLiveData<Int>()
    var appPreferences: AppPreferences? = null
    private var department_UUID: Int? = 0
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null

    init {
        userDetailsRoomRepository = UserDetailsRoomRepository(application)
        appPreferences = AppPreferences.getInstance(application, AppConstants.SHARE_PREFERENCE_NAME)
        department_UUID = appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)

    }

    fun getvitalsTemplate(
            faciltyID: Int?,
            departmentId: Int?,
            vitalsTemplateRetrofitCallBack: RetrofitCallback<VitalsTemplateResponseModel>
    ) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        apiService?.getVitalsTemplatet(
                AppConstants.BEARER_AUTH + userDataStoreBean?.access_token, userDataStoreBean?.uuid!!,
                faciltyID!!, departmentId!!, AppConstants.TEM_TYPE_ID_VITALS, userDataStoreBean?.uuid!!
        )?.enqueue(RetrofitMainCallback(vitalsTemplateRetrofitCallBack))
        return
    }

    fun getUmoList(faciltyID: Int?, umoListRetrofitCallback: RetrofitCallback<UomListResponceModel>) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        // api call for send otp

        val jsonBody = JSONObject()
        try {
            jsonBody.put("sortField", "name")
            jsonBody.put("sortOrder", "ASC")
            jsonBody.put("getAll", true)
            jsonBody.put("uomStatus", true)
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
        apiService?.gatUomVitalList(
                AppConstants.BEARER_AUTH + userDataStoreBean?.access_token, userDataStoreBean?.uuid!!,
                faciltyID!!, body
        )?.enqueue(RetrofitMainCallback(umoListRetrofitCallback))
        return

    }

    fun getVitalsName(faciltyID: Int?, vitalsNametRetrofitCallback: RetrofitCallback<VitalsListResponseModel>) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        // api call for send otp

        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        apiService?.getVitalsName(
                AppConstants.BEARER_AUTH + userDataStoreBean?.access_token, userDataStoreBean?.uuid!!,
                faciltyID!!
        )?.enqueue(RetrofitMainCallback(vitalsNametRetrofitCallback))
        return

    }

    fun deleteFavourite(facility_id: Int?, favouriteId: Int?, deleteRetrofitCallback: RetrofitCallback<DeleteResponseModel>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }

        val jsonBody = JSONObject()
        try {
            jsonBody.put("favouriteId", favouriteId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val body = RequestBody.create(
                okhttp3.MediaType.parse("application/json; charset=utf-8"),
                jsonBody.toString()
        )
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        apiService?.deleteRows(
                AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
                userDataStoreBean?.uuid!!, facility_id!!, body)?.enqueue(RetrofitMainCallback(deleteRetrofitCallback))
        return
    }

    fun vitalSave(faciltyID: Int?, vitalsSaveRetrofitCallback: RetrofitCallback<VitalSaveResponseModel>, saveData: ArrayList<VitalSaveRequestModel>) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }

        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        apiService?.saveVitals(
                AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
                userDataStoreBean?.uuid!!, faciltyID!!, saveData)?.enqueue(RetrofitMainCallback(vitalsSaveRetrofitCallback))
        return

    }

    fun searchList(vitalSearchRetrofitCallback: RetrofitCallback<VitalSearchListResponseModel>, faciltyID: Int?) {


        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }

        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        apiService?.getVitals(
                AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
                userDataStoreBean?.uuid!!, faciltyID!!)?.enqueue(RetrofitMainCallback(vitalSearchRetrofitCallback))
        return

    }

        fun getTemplateDetails(templateId: Int?, facilityUuid: Int?, departmentUuid: Int?, getTemplateRetrofitCallback: RetrofitCallback<ResponseEditTemplate>) {

            if (!Utils.isNetworkConnected(getApplication())) {
                errorText.value = getApplication<Application>().getString(R.string.no_internet)
                return
            }
            val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
            progress.value = 0
            val aiiceApplication = HmisApplication.get(getApplication())
            val apiService = aiiceApplication.getRetrofitService()
            apiService?.getLastVitualTemplate(
                    AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
                    userDataStoreBean?.uuid!!, facilityUuid!!, templateId!!, AppConstants.FAV_TYPE_ID_Vitual, departmentUuid!!)?.enqueue(RetrofitMainCallback(getTemplateRetrofitCallback))
            return
        }



    fun deleteTemplate(facility_id : Int?,template_uuid : Int?, deleteRetrofitCallback: RetrofitCallback<DeleteResponseModel>){
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val jsonBody = JSONObject()
        try {
            jsonBody.put("template_uuid", template_uuid)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            jsonBody.toString()
        )
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        apiService?.deleteTemplate(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!, facility_id!!,body)?.enqueue(RetrofitMainCallback(deleteRetrofitCallback))
        return
    }

    fun getTemplete(templeteRetrofitCallBack: RetrofitCallback<TempleResponseModel>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()


        apiService?.getTemplete(AppConstants.BEARER_AUTH + userDataStoreBean?.access_token, userDataStoreBean?.uuid!!, department_UUID!!,
            AppConstants.FAV_TYPE_ID_Vitual
        )?.enqueue(RetrofitMainCallback(templeteRetrofitCallBack))
        return
    }
}



