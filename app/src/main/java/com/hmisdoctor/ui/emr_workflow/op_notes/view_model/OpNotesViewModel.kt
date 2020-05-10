package com.hmisdoctor.ui.emr_workflow.op_notes.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.hmisdoctor.R
import com.hmisdoctor.application.HmisApplication
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.callbacks.RetrofitMainCallback
import com.hmisdoctor.config.AppConstants

import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.ui.emr_workflow.op_notes.model.OpNotesExpandableResponseModel
import com.hmisdoctor.ui.emr_workflow.op_notes.model.OpNotesResponsModel
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import org.json.JSONObject

class OpNotesViewModel(
    application: Application
) : AndroidViewModel(
    application
) {
    var errorText = MutableLiveData<String>()

    var progress = MutableLiveData<Int>()
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    private var department_UUID: Int? = 0
    private var facility_id: Int? = 0

    var appPreferences: AppPreferences? = null

    init {
        userDetailsRoomRepository = UserDetailsRoomRepository(application)
        appPreferences = AppPreferences.getInstance(application, AppConstants.SHARE_PREFERENCE_NAME)
        department_UUID = appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)
        facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)

    }

    fun getOpNotes(
        labTypeRetrofitCallback: RetrofitCallback<OpNotesResponsModel>,
        facilityID: Int?
    ) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val jsonBody = JSONObject()
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.getOpNotes(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facility_id!!
        )?.enqueue(RetrofitMainCallback(labTypeRetrofitCallback))
        return

    }

    fun getOpExpandableList(
        faciltyID: Int?,
        opNotesExpandableListRetrofitCallBack: RetrofitCallback<OpNotesExpandableResponseModel>,
        s: Int
    ) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        apiService?.getOpExpandableList(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token, userDataStoreBean?.uuid!!,
            facility_id!!, s.toInt()
        )?.enqueue(RetrofitMainCallback(opNotesExpandableListRetrofitCallBack))
        return
    }

}