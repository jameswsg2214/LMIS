package com.hmisdoctor.ui.emr_workflow.chief_complaint.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.hmisdoctor.R
import com.hmisdoctor.application.HmisApplication
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.callbacks.RetrofitMainCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppConstants.Companion.BEARER_AUTH

import com.hmisdoctor.config.AppConstants.Companion.FAV_TYPE_ID_CHIEF_COMPLAINTS
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.PrevChiefComplainResponseModel
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesResponseModel
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.duration.DurationResponseModel
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.request.ChiefComplaintRequestModel
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.response.ChiefComplaintResponse
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.search_complaint.ComplaintSearchResponseModel
import com.hmisdoctor.ui.emr_workflow.delete.model.DeleteResponseModel
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject

class ChiefComplaintViewModel(
    application: Application) : AndroidViewModel(
    application
) {
    var errorText = MutableLiveData<String>()
    var progress = MutableLiveData<Int>()
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    var department_UUID : Int? = 0
    var appPreferences: AppPreferences? = null


    init {
        userDetailsRoomRepository = UserDetailsRoomRepository(application!!)
        appPreferences = AppPreferences.getInstance(application, AppConstants.SHARE_PREFERENCE_NAME)
        department_UUID = appPreferences?.getInt(AppConstants?.DEPARTMENT_UUID)
    }

    fun getFavourites(
        departmentID: Int?,
        emrWorkFlowRetrofitCallBack: RetrofitCallback<FavouritesResponseModel>
    ) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.getFavourites(BEARER_AUTH+userDataStoreBean?.access_token, userDataStoreBean?.uuid!!, departmentID!!, FAV_TYPE_ID_CHIEF_COMPLAINTS)?.enqueue(RetrofitMainCallback(emrWorkFlowRetrofitCallBack))
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


    fun getComplaintSearchResult(
        query: String,
        complaintSearchRetrofitCallBack: RetrofitCallback<ComplaintSearchResponseModel>
    ) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.getChiefComplaintsSearchResult(BEARER_AUTH+userDataStoreBean?.access_token, userDataStoreBean?.uuid!!, "filterbythree", query)?.enqueue(RetrofitMainCallback(complaintSearchRetrofitCallBack))
        return
    }

    fun InsertCheifComplant(facility_id : Int?,ChiefComplaintArray:ArrayList<ChiefComplaintRequestModel> , configFinalRetrofitCallBack: RetrofitCallback<ChiefComplaintResponse>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        apiService?.insertChiefComplaint(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facility_id!!,
            ChiefComplaintArray
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

    fun searchFavChiefComplaint(query: String, complaintSearchFavRetrofitCallBack: RetrofitCallback<ComplaintSearchResponseModel>) {


        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.getChiefComplaintsSearchResult(BEARER_AUTH+userDataStoreBean?.access_token, userDataStoreBean?.uuid!!, "filterbythree", query)?.enqueue(RetrofitMainCallback(complaintSearchFavRetrofitCallBack))
        return

    }

    fun getPrevChiefComplaintList(
        patientId: Int?,
        facilityid: Int?,
        encounterId: Int?,
        dignosisSearchRetrofitCallBack1: RetrofitCallback<PrevChiefComplainResponseModel>
    ) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        apiService?.getPrevChiefComplaint(
            AppConstants.BEARER_AUTH +userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facilityid!!,
            "1", patientId.toString(),"5"
        )?.enqueue(RetrofitMainCallback(dignosisSearchRetrofitCallBack1))
    }






}