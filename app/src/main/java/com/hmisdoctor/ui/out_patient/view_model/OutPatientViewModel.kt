package com.hmisdoctor.ui.out_patient.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.hmisdoctor.R
import com.hmisdoctor.application.HmisApplication
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.callbacks.RetrofitMainCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppConstants.Companion.FACILITY_UUID
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.ui.out_patient.model.search_request_model.SearchPatientRequestModel
import com.hmisdoctor.ui.out_patient.search_response_model.SearchResponseModel
import com.hmisdoctor.utils.Utils
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject

class OutPatientViewModel(
    application: Application?
) : AndroidViewModel(
    application!!
) {
    var errorText = MutableLiveData<String>()
    var progressBar = MutableLiveData<Int>()
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    var appPreferences: AppPreferences? = null
    init {
        progressBar.value = 8
        userDetailsRoomRepository = UserDetailsRoomRepository(application!!)
        appPreferences = AppPreferences.getInstance(application, AppConstants.SHARE_PREFERENCE_NAME)

    }


    fun searchPatient(
        query: String,
        currentPage: Int,
        pageSize: Int,
        sortField: String,
        sortOrder: String,
        patientSearchRetrofitCallBack: RetrofitCallback<SearchResponseModel>
    ) {
        val searchPatientRequestModel = SearchPatientRequestModel()
        if (query.length > 10) {
            searchPatientRequestModel.mobile = ""
            searchPatientRequestModel.pin = query
        } else {
            searchPatientRequestModel.mobile = query
            searchPatientRequestModel.pin = ""
        }
        searchPatientRequestModel.pageNo = currentPage
        searchPatientRequestModel.paginationSize = pageSize
        searchPatientRequestModel.sortField = sortField
        searchPatientRequestModel.sortOrder = sortOrder

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progressBar.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        apiService?.searchOutPatient(
            AppConstants.ACCEPT_LANGUAGE_EN,
            AppConstants.BEARER_AUTH+userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!, appPreferences?.getInt(FACILITY_UUID)!!, searchPatientRequestModel)!!.enqueue(
            RetrofitMainCallback(patientSearchRetrofitCallBack)
        )
    }

    fun getPatientListNextPage(query: String,currentPage: Int, patientSearchNextRetrofitCallBack: RetrofitCallback<SearchResponseModel>) {

        val searchPatientRequestModel = SearchPatientRequestModel()
        if (query.length > 10) {
            searchPatientRequestModel.mobile = ""
            searchPatientRequestModel.pin = query
        } else {
            searchPatientRequestModel.mobile = query
            searchPatientRequestModel.pin = ""
        }
        searchPatientRequestModel.pageNo = currentPage
        searchPatientRequestModel.paginationSize = currentPage
        searchPatientRequestModel.sortField = "modified_date"
        searchPatientRequestModel.sortOrder = "DESC"

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progressBar.value = 0
        val hmisApplication = HmisApplication.get(getApplication())
        val apiService = hmisApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        apiService?.searchOutPatient(
            AppConstants.ACCEPT_LANGUAGE_EN,
            AppConstants.BEARER_AUTH+userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!, appPreferences?.getInt(FACILITY_UUID)!!, searchPatientRequestModel)!!.enqueue(
            RetrofitMainCallback(patientSearchNextRetrofitCallBack)
        )
    }

}