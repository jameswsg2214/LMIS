package com.hmisdoctor.ui.emr_workflow.diagnosis.view.snomed_dialog

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
import com.hmisdoctor.ui.emr_workflow.diagnosis.view.snomed_dialog.response.SnomedChildDataResponseModel
import com.hmisdoctor.ui.emr_workflow.diagnosis.view.snomed_dialog.response.SnomedDataResponseModel
import com.hmisdoctor.ui.emr_workflow.diagnosis.view.snomed_dialog.response.SnomedParentDataResponseModel
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject

class SnomedDialogviewModel (
        application: Application
) : AndroidViewModel(
        application
) {

    var errorText = MutableLiveData<String>()
    var progress = MutableLiveData<Int>()
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    var appPreferences: AppPreferences? = null

    var facilityID:Int?= 0


    init {
        userDetailsRoomRepository = UserDetailsRoomRepository(application)

        appPreferences = AppPreferences.getInstance(application, AppConstants.SHARE_PREFERENCE_NAME)

        facilityID = appPreferences?.getInt(AppConstants.FACILITY_UUID)
    }

    fun searchSnoomed(query: String, searchsn0omedRetrofitCallback: RetrofitCallback<SnomedDataResponseModel>) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()


        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()

        apiService?.getSnommed(
                AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
                userDataStoreBean?.uuid!!,facilityID!!,
                query
        )?.enqueue(RetrofitMainCallback(searchsn0omedRetrofitCallback))
        return

    }


    fun searchParentSnoomed(query: String, searchsn0omedRetrofitCallback: RetrofitCallback<SnomedParentDataResponseModel>) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()

        apiService?.getParentSnommed(
                AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
                userDataStoreBean?.uuid!!,facilityID!!,
                query
        )?.enqueue(RetrofitMainCallback(searchsn0omedRetrofitCallback))
        return

    }

    fun searchChildSnoomed(query: String, searchsn0omedRetrofitCallback: RetrofitCallback<SnomedChildDataResponseModel>) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()

        apiService?.getChildSnommed(
                AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
                userDataStoreBean?.uuid!!,facilityID!!,
                query
        )?.enqueue(RetrofitMainCallback(searchsn0omedRetrofitCallback))
        return

    }
}