package com.hmisdoctor.ui.emr_workflow.history.immunization.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.hmisdoctor.R
import com.hmisdoctor.application.HmisApplication
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.callbacks.RetrofitMainCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppConstants.Companion.BEARER_AUTH

import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.ui.emr_workflow.history.allergy.model.response.*
import com.hmisdoctor.ui.emr_workflow.history.immunization.model.CreateImmunizationResponseModel
import com.hmisdoctor.ui.emr_workflow.history.immunization.model.GetImmunizationResponseModel
import com.hmisdoctor.ui.emr_workflow.history.immunization.model.ImmunizationInstitutionResponseModel
import com.hmisdoctor.ui.emr_workflow.history.immunization.model.ImmunizationNameResponseModel
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject

class HistoryImmunizationViewModel(
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


    fun getImmunizationTypeList(facility_uuid:Int,getImmunizationTypeRetrofitCallBack: RetrofitCallback<EncounterAllergyTypeResponse>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progressBar.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.getEncounterAllergyType(BEARER_AUTH+userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facility_uuid
        )?.enqueue(RetrofitMainCallback(getImmunizationTypeRetrofitCallBack))
        return
    }


    fun getImmunizationNameList(facility_uuid:Int,getImmunizationNameRetrofitCallBack: RetrofitCallback<ImmunizationNameResponseModel>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progressBar.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.getImmunizationName(BEARER_AUTH+userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facility_uuid
        )?.enqueue(RetrofitMainCallback(getImmunizationNameRetrofitCallBack))
        return
    }


    fun getImmunizationAll(facility_uuid:Int,patient_uuid: Int,getImmunizationAllRetrofitCallBack: RetrofitCallback<GetImmunizationResponseModel>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progressBar.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.getImmunizationAll(BEARER_AUTH+userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facility_uuid, patient_uuid
        )?.enqueue(RetrofitMainCallback(getImmunizationAllRetrofitCallBack))
        return
    }

    fun createImmunization(facility_uuid: Int, createImmunization: RequestBody, createImmunizationHistoryRetrofitCallBack: RetrofitCallback<CreateImmunizationResponseModel>){

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }

        progressBar.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.createImmunization(BEARER_AUTH+userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facility_uuid,createImmunization
        )?.enqueue(RetrofitMainCallback(createImmunizationHistoryRetrofitCallBack))
        return
    }



    fun getInstitutionSearchResult(facility_id: Int?,
                                 query: String,
                                 complaintSearchRetrofitCallBack: RetrofitCallback<ImmunizationInstitutionResponseModel>
    ) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val jsonBody = JSONObject()
        try {
            jsonBody.put("pageNo", 0)
            jsonBody.put("paginationSize", 10)
            jsonBody.put("search", query)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            jsonBody.toString()
        )
        progressBar.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        apiService?.getImmunizationInstitution(AppConstants.BEARER_AUTH + userDataStoreBean?.access_token, userDataStoreBean?.uuid!!,facility_id!!,body)?.enqueue(RetrofitMainCallback(complaintSearchRetrofitCallBack))
        return
    }

}