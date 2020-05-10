package com.hmisdoctor.ui.emr_workflow.treatment_kit.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.hmisdoctor.R
import com.hmisdoctor.application.HmisApplication
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.callbacks.RetrofitMainCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.search_complaint.ComplaintSearchResponseModel
import com.hmisdoctor.ui.emr_workflow.delete.model.DeleteResponseModel
import com.hmisdoctor.ui.emr_workflow.diagnosis.model.DiagnosisRequest
import com.hmisdoctor.ui.emr_workflow.diagnosis.model.DiagnosisResponseModel
import com.hmisdoctor.ui.emr_workflow.diagnosis.model.diagonosissearch.DiagonosisSearchResponse
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.LabToLocationResponse
import com.hmisdoctor.ui.emr_workflow.lab.model.LabTypeResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.favresponse.FavSearchResponce
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesResponseModel
import com.hmisdoctor.ui.emr_workflow.prescription.model.*
import com.hmisdoctor.ui.emr_workflow.treatment_kit.model.TreatmentKitCreateResponseModel
import com.hmisdoctor.ui.emr_workflow.treatment_kit.model.TreatmentKitPrevResponsModel
import com.hmisdoctor.ui.emr_workflow.treatment_kit.model.request.CreateTreatmentkitRequestModel
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject

class TreatmentSaveOrderKitViewModel (
    application: Application
) : AndroidViewModel(
    application
) {
    var errorText = MutableLiveData<String>()
    var progress = MutableLiveData<Int>()
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    var appPreferences : AppPreferences? = null
    var StoreMasterID : Int?=0
    var department_UUID : Int? = 0




    init {
        userDetailsRoomRepository = UserDetailsRoomRepository(application)
        appPreferences = AppPreferences.getInstance(application, AppConstants.SHARE_PREFERENCE_NAME)
        StoreMasterID = appPreferences?.getInt(AppConstants.STOREMASTER_UUID)
        department_UUID = appPreferences?.getInt(AppConstants?.DEPARTMENT_UUID)

    }
    fun getSaveOrderDepartmentList(
        facilityID: Int?,
        templateRadiodepartmentRetrofitCallBack: RetrofitCallback<FavAddResponseModel>
    ) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        val jsonBody = JSONObject()
        try {
            jsonBody.put("uuid", department_UUID)
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

        apiService?.getFavDepartmentList(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,facilityID!!,
            body
        )?.enqueue(RetrofitMainCallback(templateRadiodepartmentRetrofitCallBack))
        return
    }


    fun createTreatmentKit(
        facilityID: Int?,
        createTreatmentkitRequestModel: CreateTreatmentkitRequestModel,
        createTreatmentmentRetrofitCallBack: RetrofitCallback<TreatmentKitCreateResponseModel>
    ) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()

        apiService?.postTreatmentKit(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,facilityID!!,
            createTreatmentkitRequestModel
        )?.enqueue(RetrofitMainCallback(createTreatmentmentRetrofitCallBack))
        return
    }

}