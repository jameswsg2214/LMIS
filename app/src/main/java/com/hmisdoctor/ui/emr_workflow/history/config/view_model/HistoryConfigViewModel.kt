package com.hmisdoctor.ui.emr_workflow.history.config.view_model

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
import com.hmisdoctor.ui.configuration.model.ConfigResponseModel
import com.hmisdoctor.ui.configuration.model.ConfigUpdateRequestModel
import com.hmisdoctor.ui.configuration.model.ConfigUpdateResponseModel
import com.hmisdoctor.ui.emr_workflow.model.EmrWorkFlowResponseModel
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject

class HistoryConfigViewModel(
    application: Application?,
    private var configRetrofitCallBack: RetrofitCallback<ConfigResponseModel>?
) : AndroidViewModel(
    application!!
) {

    var errorText = MutableLiveData<String>()
    var progress = MutableLiveData<Int>()
    var appPreferences: AppPreferences? = null
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    init {
        progress.value = 8
        userDetailsRoomRepository = UserDetailsRoomRepository(application!!)
        appPreferences = AppPreferences.getInstance(application, AppConstants.SHARE_PREFERENCE_NAME)
    }
    fun getConfigList() {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()

        val jsonBody = JSONObject()
        try {
            jsonBody.put("context_uuid", AppConstants.History)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            jsonBody.toString()
        )
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        apiService?.getConfigList(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            body
        )?.enqueue(RetrofitMainCallback(configRetrofitCallBack))
        return
    }

    fun postRequestParameter(facility_id : Int?,configRequestData: ArrayList<ConfigUpdateRequestModel?>, configFinalRetrofitCallBack: RetrofitCallback<ConfigUpdateResponseModel>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        apiService?.getHistoryConfigUpdate(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!, facility_id!!,
            configRequestData
        )?.enqueue(RetrofitMainCallback(configFinalRetrofitCallBack))
        return

    }


    fun getEmrWorkFlowFav(emrWorkFlowRetrofitCallBack: RetrofitCallback<EmrWorkFlowResponseModel>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        apiService?.getEmrWorkflow(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!
        )!!.enqueue(
            RetrofitMainCallback(emrWorkFlowRetrofitCallBack)
        )
    }

}