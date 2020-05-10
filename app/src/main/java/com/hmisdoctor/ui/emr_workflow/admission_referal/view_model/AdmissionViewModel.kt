package com.hmisdoctor.ui.emr_workflow.admission_referal.view_model

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
import com.hmisdoctor.ui.emr_workflow.admission_referal.model.AdmissionWardResponseModel
import com.hmisdoctor.ui.emr_workflow.admission_referal.model.ReasonResponseModel
import com.hmisdoctor.ui.emr_workflow.admission_referal.model.RefferaNextResponseModel
import com.hmisdoctor.ui.emr_workflow.admission_referal.model.RefferalNextRequestModel
import com.hmisdoctor.ui.emr_workflow.documents.model.AddDocumentDetailsResponseModel
import com.hmisdoctor.ui.emr_workflow.history.surgery.model.response.SurgeryInstitutionResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddAllDepatResponseModel
import com.hmisdoctor.ui.emr_workflow.radiology_result.model.RadiologyResultResponseModel
import com.hmisdoctor.ui.institute.model.DepartmentResponseModel
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.ui.quick_reg.model.SaveOrderResponseModel
import com.hmisdoctor.ui.quick_reg.model.request.SaveOrderRequestModel
import com.hmisdoctor.utils.Utils
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject

class AdmissionViewModel(
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
    var facility_id:Int?=0




    init {
        errorTextVisibility.value = 8
        progress.value = 8
        userDetailsRoomRepository = UserDetailsRoomRepository(application)

        appPreferences = AppPreferences.getInstance(application, AppConstants.SHARE_PREFERENCE_NAME)

        facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)


    }
    fun getAllDepartment( facilityID: Int?,
                          favAddAllDepartmentCallBack: RetrofitCallback<FavAddAllDepatResponseModel>){
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        val jsonBody = JSONObject()

        try {
            jsonBody.put("facilityBased", true)

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

        apiService?.getFavddAllADepartmentList(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,facilityID!!,
            body
        )?.enqueue(RetrofitMainCallback(favAddAllDepartmentCallBack))
        return

    }

    fun getWArdList( facilityID: Int?,department_uuid : Int,
                          favAddAllDepartmentCallBack: RetrofitCallback<AdmissionWardResponseModel>){
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        val jsonBody = JSONObject()

        try {
            jsonBody.put("facility_uuid", 1)
            jsonBody.put("department_uuid", department_uuid)

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

        apiService?.getAdmissionWardList(AppConstants.ACCEPT_LANGUAGE_EN,
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,facilityID!!,
            body
        )?.enqueue(RetrofitMainCallback(favAddAllDepartmentCallBack))
        return

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
        progress.value = 0
        val hmisApplication = HmisApplication.get(getApplication())
        val apiService = hmisApplication.getRetrofitService()
        apiService?.getInstitutions(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!, facilityid!!,body)?.enqueue(RetrofitMainCallback(SurgeryInstitutionCallback))
    }
    fun getReason(facilityId: Int, addDocumentTypeResponseCallback: RetrofitCallback<ReasonResponseModel>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.getReason(
            AppConstants.ACCEPT_LANGUAGE_EN,
            AppConstants.BEARER_AUTH +userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facilityId
        )?.enqueue(RetrofitMainCallback(addDocumentTypeResponseCallback))
        return
    }
    fun getSaveNext(request: RefferalNextRequestModel, PatientNameRetrofitCallback: RetrofitCallback<RefferaNextResponseModel>) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()

        apiService?.nextOrder(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!, facility_id!!,
            request
        )?.enqueue(RetrofitMainCallback(PatientNameRetrofitCallback))

    }









}