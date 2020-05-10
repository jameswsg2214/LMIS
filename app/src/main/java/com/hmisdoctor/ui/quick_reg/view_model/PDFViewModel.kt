package com.hmisdoctor.ui.quick_reg.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

import com.hmisdoctor.R
import com.hmisdoctor.application.HmisApplication
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.callbacks.RetrofitMainCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.ui.quick_reg.model.*
import com.hmisdoctor.utils.Utils
import okhttp3.ResponseBody


class PDFViewModel(
    application: Application?
) : AndroidViewModel(
    application!!
) {

    var progress = MutableLiveData<Int>()
    var errorText = MutableLiveData<String>()
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    var facility_id:Int?=0
    var appPreferences: AppPreferences? = null

    init {
        userDetailsRoomRepository = UserDetailsRoomRepository(application!!)
        appPreferences = AppPreferences.getInstance(application, AppConstants.SHARE_PREFERENCE_NAME)
        progress.value = 8
        facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)
    }
    fun GetPDFf(requestPDF: PDFRequestModel, GetPDFRetrofitCallback: RetrofitCallback<ResponseBody>) {
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()

        apiService?.getPDF(AppConstants.ACCEPT_LANGUAGE_EN,
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!, facility_id!!,
            requestPDF
        )?.enqueue(RetrofitMainCallback(GetPDFRetrofitCallback))

    }}