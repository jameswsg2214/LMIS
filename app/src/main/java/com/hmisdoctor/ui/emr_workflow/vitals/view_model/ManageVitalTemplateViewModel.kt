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
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddAllDepatResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddTestNameResponse
import com.hmisdoctor.ui.emr_workflow.lab.model.LabFavManageResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.faveditresponse.FavEditResponse
import com.hmisdoctor.ui.emr_workflow.lab.model.favresponse.FavAddListResponse
import com.hmisdoctor.ui.emr_workflow.lab.model.request.RequestLabFavModel
import com.hmisdoctor.ui.emr_workflow.lab.model.template.request.RequestTemplateAddDetails
import com.hmisdoctor.ui.emr_workflow.lab.model.template.response.ReponseTemplateadd
import com.hmisdoctor.ui.emr_workflow.lab.model.updaterequest.UpdateRequestModule
import com.hmisdoctor.ui.emr_workflow.lab.model.updateresponse.UpdateResponse
import com.hmisdoctor.ui.emr_workflow.model.templete.TempleResponseModel
import com.hmisdoctor.ui.emr_workflow.vitals.model.VitalsListResponseModel
import com.hmisdoctor.ui.emr_workflow.vitals.model.VitalsSearchResponseModel
//import com.hmisdoctor.ui.emr_workflow.vitals.model.VitalsSearchResponseModel
import com.hmisdoctor.ui.emr_workflow.vitals.model.VitalsTemplateResponseModel
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject


class ManageVitalTemplateViewModel(
    application: Application?


) : AndroidViewModel(
    application!!
) {

    var errorText = MutableLiveData<String>()
    var progress = MutableLiveData<Int>()
    var appPreferences: AppPreferences? = null
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    var department_UUID : Int? = 0
    init {
        progress.value = 8
        userDetailsRoomRepository = UserDetailsRoomRepository(application!!)
        appPreferences = AppPreferences.getInstance(application, AppConstants.SHARE_PREFERENCE_NAME)
        department_UUID = appPreferences?.getInt(AppConstants?.DEPARTMENT_UUID)
    }
    var facilityID = appPreferences?.getInt(AppConstants?.FACILITY_UUID)

    fun getVitalsSearch(faciltyID: Int?, vitalsSearchRetrofitCallBack: RetrofitCallback<VitalsSearchResponseModel>) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        apiService?.getVitalSearch(
            AppConstants.BEARER_AUTH +userDataStoreBean?.access_token, userDataStoreBean?.uuid!!,
            faciltyID!!
        )?.enqueue(RetrofitMainCallback(vitalsSearchRetrofitCallBack))
        return

    }

    fun labTemplateDetails(facilityUuid: Int?, requestTemplateAddDetails: RequestTemplateAddDetails, emrlabTemplateAddRetrofitCallback: RetrofitCallback<ReponseTemplateadd>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        apiService?.createTemplate(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!, facilityUuid!!,
            requestTemplateAddDetails
        )?.enqueue(RetrofitMainCallback(emrlabTemplateAddRetrofitCallback))
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
            AppConstants.FAV_TYPE_ID_LAB
        )?.enqueue(RetrofitMainCallback(templeteRetrofitCallBack))
        return
    }

    fun labUpdateTemplateDetails(facilityUuid: Int?, requestTemplateUpdateDetails: UpdateRequestModule, UpdateemrlabTemplateAddRetrofitCallback: RetrofitCallback<UpdateResponse>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        apiService?.getTemplateUpdate(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!, facilityUuid!!,
            requestTemplateUpdateDetails
        )?.enqueue(RetrofitMainCallback(UpdateemrlabTemplateAddRetrofitCallback))
        return



    }



}