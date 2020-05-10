package com.hmisdoctor.ui.emr_workflow.radiology.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.hmisdoctor.R
import com.hmisdoctor.application.HmisApplication
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.callbacks.RetrofitMainCallback
import com.hmisdoctor.config.AppConstants

import com.hmisdoctor.config.AppConstants.Companion.FAV_TYPE_ID_RADIOLOGY
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.ui.emr_workflow.lab.model.LabToLocationResponse
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesResponseModel
import com.hmisdoctor.ui.emr_workflow.model.templete.TempleResponseModel
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.duration.DurationResponseModel
import com.hmisdoctor.ui.emr_workflow.delete.model.DeleteResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.LabTypeResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.favresponse.FavSearchResponce
import com.hmisdoctor.ui.emr_workflow.lab.model.template.gettemplate.ResponseLabGetTemplateDetails
import com.hmisdoctor.ui.emr_workflow.model.EMR_Request.EmrRequestModel
import com.hmisdoctor.ui.emr_workflow.model.EMR_Response.EmrResponceModel
import com.hmisdoctor.ui.emr_workflow.model.fetch_encounters_response.FectchEncounterResponseModel
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject

class RadiologyViewModel(
    application: Application
) : AndroidViewModel(
    application
) {
    var errorText = MutableLiveData<String>()
    var progress = MutableLiveData<Int>()
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    private var department_UUID: Int? = 0
    var appPreferences : AppPreferences? = null
    init {
        userDetailsRoomRepository = UserDetailsRoomRepository(application)
        appPreferences = AppPreferences.getInstance(application, AppConstants.SHARE_PREFERENCE_NAME)
        department_UUID = appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)
    }

    fun getFavourites(
        emrWorkFlowRetrofitCallBack: RetrofitCallback<FavouritesResponseModel>,
        departmentID: Int?
    ) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.getFavourites(AppConstants.BEARER_AUTH + userDataStoreBean?.access_token, userDataStoreBean?.uuid!!, departmentID!!, FAV_TYPE_ID_RADIOLOGY)?.enqueue(RetrofitMainCallback(emrWorkFlowRetrofitCallBack))
        return
    }

    /*
    Templete
     */

    fun getTemplete(templeteRetrofitCallBack: RetrofitCallback<TempleResponseModel>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()


        apiService?.getTemplete(AppConstants.BEARER_AUTH + userDataStoreBean?.access_token, userDataStoreBean?.uuid!!, department_UUID!!, FAV_TYPE_ID_RADIOLOGY)?.enqueue(RetrofitMainCallback(templeteRetrofitCallBack))
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

        apiService?.getDuration(AppConstants.BEARER_AUTH + userDataStoreBean?.access_token, userDataStoreBean?.uuid!!)?.enqueue(RetrofitMainCallback(getDurationRetrofitCallBack))
        return
    }


    fun getRadioSearchResult(facility_id: Int?,
        query: String,
        radioSearchRetrofitCallBack: RetrofitCallback<FavSearchResponce>
    ) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val jsonBody = JSONObject()
        try {
            jsonBody.put("sortField", "uuid")
            jsonBody.put("sortOrder", "DESC")
            jsonBody.put("search", query)
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

        apiService?.getRadioSearchResult(AppConstants.BEARER_AUTH + userDataStoreBean?.access_token, userDataStoreBean?.uuid!!, facility_id!!, body)?.enqueue(RetrofitMainCallback(radioSearchRetrofitCallBack))
        return
    }

    fun getLabType(
        labTypeRetrofitCallback: RetrofitCallback<LabTypeResponseModel>,
        facilityID: Int?
    ) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val jsonBody = JSONObject()
        try {
            jsonBody.put("table_name", "order_priority")
            jsonBody.put("sortField", "uuid")
            jsonBody.put("sortOrder", "DESC")
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

        apiService?.getLabType(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facilityID!!,
            body
        )?.enqueue(RetrofitMainCallback(labTypeRetrofitCallback))
        return

    }



    fun getToLocation(
        labToRetrofitCallback: RetrofitCallback<LabToLocationResponse>,
        facilityID: Int?
    ) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }

        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        apiService?.getToLocationRadiology(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facilityID!!
        )?.enqueue(RetrofitMainCallback(labToRetrofitCallback))
        return

    }

    fun RadiologyInsert(facility_id : Int?, EmrRequestData: EmrRequestModel, configFinalRetrofitCallBack: RetrofitCallback<EmrResponceModel>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        apiService?.EmrRadiologypost(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!, facility_id!!,
            EmrRequestData
        )?.enqueue(RetrofitMainCallback(configFinalRetrofitCallBack))
        return

    }

    fun deleteFavourite(facility_id : Int?,favouriteId : Int?, deleteRetrofitCallback: RetrofitCallback<DeleteResponseModel>){
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
            userDataStoreBean?.uuid!!, facility_id!!,body)?.enqueue(RetrofitMainCallback(deleteRetrofitCallback))
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

    fun getTemplateDetails(templateId: Int?, facilityUuid: Int?, departmentUuid: Int?,getTemplateRetrofitCallback: RetrofitCallback<ResponseLabGetTemplateDetails>) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        apiService?.getLastTemplate(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!, facilityUuid!!,templateId!!,AppConstants.FAV_TYPE_ID_RADIOLOGY,department_UUID!!)?.enqueue(RetrofitMainCallback(getTemplateRetrofitCallback))
        return
    }

    fun getEncounter(
        patientUuid: Int,
        encounterType: Int,
        fetchEncounterRetrofitCallBack: RetrofitCallback<FectchEncounterResponseModel>
    ) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        apiService?.getEncounters(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            patientUuid,
            userDataStoreBean.uuid,
            appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)!!,
            encounterType

        )!!.enqueue(
            RetrofitMainCallback(fetchEncounterRetrofitCallBack)
        )
    }


}