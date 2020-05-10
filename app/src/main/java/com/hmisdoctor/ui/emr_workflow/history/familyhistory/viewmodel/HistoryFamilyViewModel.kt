package com.hmisdoctor.ui.emr_workflow.history.familyhistory.viewmodel

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
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.duration.DurationResponseModel
import com.hmisdoctor.ui.emr_workflow.history.allergy.model.response.EncounterAllergyTypeResponse
import com.hmisdoctor.ui.emr_workflow.history.familyhistory.model.CreateFamilyHistoryResponseModel
import com.hmisdoctor.ui.emr_workflow.history.familyhistory.model.FamilyHistoryResponseModel
import com.hmisdoctor.ui.emr_workflow.history.familyhistory.model.FamilyTypeSpinnerResponseModel
import com.hmisdoctor.ui.emr_workflow.history.familyhistory.model.FamilyUpdateResponseModel
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject


class HistoryFamilyViewModel(
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
    fun getDuration(getDurationRetrofitCallBack: RetrofitCallback<DurationResponseModel>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progressBar.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.getDuration(AppConstants.BEARER_AUTH +userDataStoreBean?.access_token, userDataStoreBean?.uuid!!)?.enqueue(
            RetrofitMainCallback(getDurationRetrofitCallBack)
        )
        return
    }
    fun getFamilyTypeList(facility_uuid:Int,patient_uuid:Int,getFamilyTypeRetrofitCallBack: RetrofitCallback<FamilyHistoryResponseModel>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progressBar.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.getFamilyAllType(
            AppConstants.BEARER_AUTH +userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facility_uuid,patient_uuid
        )?.enqueue(RetrofitMainCallback(getFamilyTypeRetrofitCallBack))
        return
    }


    fun getHistoryFamilyType(facility_uuid:Int,getallergyNameRetrofitCallBack: RetrofitCallback<FamilyTypeSpinnerResponseModel>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }

        val jsonBody = JSONObject()
        try {
            jsonBody.put("table_name", "family_relation_type")
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

        apiService?.getHistoryFamilyType(BEARER_AUTH+userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facility_uuid,body
        )?.enqueue(RetrofitMainCallback(getallergyNameRetrofitCallBack))
        return
    }


    fun createFamilyHistory(facility_uuid: Int, createFamily: RequestBody, createFamilyHistoryRetrofitCallBack: RetrofitCallback<CreateFamilyHistoryResponseModel>){

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }

        progressBar.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.createFamilyHistory(BEARER_AUTH+userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facility_uuid,createFamily
        )?.enqueue(RetrofitMainCallback(createFamilyHistoryRetrofitCallBack))
        return
    }


    fun updateFamilyData(facility_uuid: Int,uuid : Int, allergyRequest: RequestBody, updateFamilyRetrofitCallBack: RetrofitCallback<FamilyUpdateResponseModel>){

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }

        progressBar.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.updateFamilyHistory(BEARER_AUTH+userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facility_uuid,uuid,allergyRequest
        )?.enqueue(RetrofitMainCallback(updateFamilyRetrofitCallBack))
        return

    }


}