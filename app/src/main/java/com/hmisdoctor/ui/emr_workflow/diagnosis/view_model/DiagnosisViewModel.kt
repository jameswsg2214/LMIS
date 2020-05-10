package com.hmisdoctor.ui.emr_workflow.diagnosis.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.hmisdoctor.R
import com.hmisdoctor.application.HmisApplication
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.callbacks.RetrofitMainCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.PrevChiefComplainResponseModel
import com.hmisdoctor.ui.emr_workflow.delete.model.DeleteResponseModel
import com.hmisdoctor.ui.emr_workflow.diagnosis.model.DiagnosisRequest
import com.hmisdoctor.ui.emr_workflow.diagnosis.model.DiagnosisResponseModel
import com.hmisdoctor.ui.emr_workflow.diagnosis.model.PreDiagnosisResponseModel
import com.hmisdoctor.ui.emr_workflow.diagnosis.model.diagonosissearch.DiagonosisSearchResponse
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesResponseModel
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject

class DiagnosisViewModel (
    application: Application
) : AndroidViewModel(
    application
) {
    var errorText = MutableLiveData<String>()
    var progress = MutableLiveData<Int>()
    var errorTextVisibility = MutableLiveData<Int>()

    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    init {
        errorTextVisibility.value = 8
        progress.value = 8
        userDetailsRoomRepository = UserDetailsRoomRepository(application)

    }

    fun getFavourites(
        DEPT_ID: Int?,
        favouritesRetrofitCallBack: RetrofitCallback<FavouritesResponseModel>
    ) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.getFavourites(
            AppConstants.BEARER_AUTH +userDataStoreBean?.access_token, userDataStoreBean?.uuid!!,
            DEPT_ID!!,
            AppConstants.FAV_TYPE_ID_DIAGNOSIS
        )?.enqueue(RetrofitMainCallback(favouritesRetrofitCallBack))
        return
    }

    fun getComplaintSearchResult(
        facilityUuid: Int?,
        query: String,
        dignosisSearchRetrofitCallBack1: RetrofitCallback<DiagonosisSearchResponse>
    ) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        apiService?.getDiagonosisName(AppConstants.BEARER_AUTH +userDataStoreBean?.access_token, userDataStoreBean?.uuid!!,
            facilityUuid!!,
           "filterbythree",query
            )?.enqueue(RetrofitMainCallback(dignosisSearchRetrofitCallBack1))
    }
    fun getdiagnosisSearchResult(
        query: String,
        facilityUuid: Int?,
        complaintSearchRetrofitCallBack: RetrofitCallback<DiagonosisSearchResponse>
    ) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.getDiagonosisName(AppConstants.BEARER_AUTH +userDataStoreBean?.access_token, userDataStoreBean?.uuid!!,
            facilityUuid!!,"filterbythree", query)?.enqueue(
            RetrofitMainCallback(complaintSearchRetrofitCallBack)
        )
        return
    }


    fun InsertDiagnosis(
        diagnosisRequestArrayList: ArrayList<DiagnosisRequest>,
        insertDiagnoisisRetrofitCallback: RetrofitCallback<DiagnosisResponseModel>,
        facilityId: Int
    ) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        apiService?.insertDiagnosis(AppConstants.BEARER_AUTH +userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,facilityId,
            diagnosisRequestArrayList!!
        )?.enqueue(RetrofitMainCallback(insertDiagnoisisRetrofitCallback))
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

    fun getComplaintSearchResult(
        query: String,
        facilityUuid: Int?,
        complaintSearchRetrofitCallBack: RetrofitCallback<DiagonosisSearchResponse>
    ) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        apiService?.getDiagonosisName(AppConstants.BEARER_AUTH +userDataStoreBean?.access_token, userDataStoreBean?.uuid!!,
            facilityUuid!!,"filterbythree", query)?.enqueue(
            RetrofitMainCallback(complaintSearchRetrofitCallBack)
        )
        return
    }
    fun getPrevDiagnosisList(
        patientId: Int?,
        facilityid: Int?,
        encounterId: Int?,
        dignosisSearchRetrofitCallBack1: RetrofitCallback<PreDiagnosisResponseModel>
    ) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        apiService?.getPrevDiagnosis(
            AppConstants.BEARER_AUTH +userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facilityid!!,
            encounterId!!.toString(), patientId!!.toString()
        )?.enqueue(RetrofitMainCallback(dignosisSearchRetrofitCallBack1))
    }


}