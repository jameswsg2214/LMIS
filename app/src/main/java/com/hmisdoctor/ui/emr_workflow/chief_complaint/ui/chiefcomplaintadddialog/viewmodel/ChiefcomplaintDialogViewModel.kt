package com.hmisdoctor.ui.emr_workflow.chief_complaint.ui.chiefcomplaintadddialog.viewmodel

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
import com.hmisdoctor.ui.emr_workflow.chief_complaint.ui.chiefcomplaintadddialog.model.ChiefComplaintFavAddresponseModel
import com.hmisdoctor.ui.emr_workflow.chief_complaint.ui.chiefcomplaintadddialog.model.request.ChiefCompliantAddRequestModel
import com.hmisdoctor.ui.emr_workflow.delete.model.DeleteResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.faveditresponse.FavEditResponse
import com.hmisdoctor.ui.emr_workflow.lab.model.favresponse.FavAddListResponse
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesResponseModel
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject

class ChiefcomplaintDialogViewModel (
application: Application?
) : AndroidViewModel(
application!!
) {

    var errorText = MutableLiveData<String>()
    var progress = MutableLiveData<Int>()
    var appPreferences: AppPreferences? = null
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    var department_UUID: Int? = 0

    init {
        progress.value = 8
        userDetailsRoomRepository = UserDetailsRoomRepository(application!!)
        appPreferences = AppPreferences.getInstance(application, AppConstants.SHARE_PREFERENCE_NAME)
        department_UUID = appPreferences?.getInt(AppConstants?.DEPARTMENT_UUID)
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

        apiService?.getChiefComplaintsSearchResult(AppConstants.BEARER_AUTH +userDataStoreBean?.access_token, userDataStoreBean?.uuid!!, "filterbythree", query)?.enqueue(
            RetrofitMainCallback(complaintSearchRetrofitCallBack)
        )
        return
    }

    fun AddFavourite(facilityUuid: Int, emrFavRequestModel: ChiefCompliantAddRequestModel, addFavRetrofitCallBack: RetrofitCallback<ChiefComplaintFavAddresponseModel>) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0

        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.AddFavChiefComplaint(AppConstants.BEARER_AUTH +userDataStoreBean?.access_token, userDataStoreBean?.uuid!!, facilityUuid, "chiefComplaints",emrFavRequestModel)?.enqueue(
            RetrofitMainCallback(addFavRetrofitCallBack)
        )
        return


    }

    fun getFavourites(departmentID: Int?, favouritesRetrofitCallBack: RetrofitCallback<FavouritesResponseModel>) {


        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.getFavourites(
            AppConstants.BEARER_AUTH +userDataStoreBean?.access_token, userDataStoreBean?.uuid!!, departmentID!!,
            AppConstants.FAV_TYPE_ID_CHIEF_COMPLAINTS
        )?.enqueue(RetrofitMainCallback(favouritesRetrofitCallBack))
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

    fun getEditFavourite(
        facilityUuid: Int?,
        chiefcompliantID: Int?,
        favouriteId: Int?,
        favouriteDisplayOrder: String?,
        isactive: Boolean,
        emrposListDataFavtEditRetrofitCallback: RetrofitCallback<FavEditResponse>
    ) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        val jsonBody = JSONObject()

        try {
            jsonBody.put("chief_complaint_id", chiefcompliantID)
            jsonBody.put("favourite_display_order", favouriteDisplayOrder)
            jsonBody.put("favourite_id", favouriteId)
            jsonBody.put("is_active", isactive)
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

        apiService?.labEditFav(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,facilityUuid!!, body
        )?.enqueue(RetrofitMainCallback(emrposListDataFavtEditRetrofitCallback))
        return



    }

    fun getAddListFav(
        facilityUuid: Int?,
        favouriteMasterID : Int?,
        emrposListDataFavtRetrofitCallback: RetrofitCallback<FavAddListResponse>
    ) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()

        apiService?.getFavddAllList(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,facilityUuid!!, favouriteMasterID!!
        )?.enqueue(RetrofitMainCallback(emrposListDataFavtRetrofitCallback))
        return

    }


}