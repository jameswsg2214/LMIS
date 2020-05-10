package com.hmisdoctor.ui.quick_reg.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson

import com.hmisdoctor.ui.dashboard.model.ResponseSpicemanType
import com.hmisdoctor.R
import com.hmisdoctor.application.HmisApplication
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.callbacks.RetrofitMainCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.ui.covid.addpatientrequest.*
import com.hmisdoctor.ui.covid.addpatientresponse.AddPatientResponse
import com.hmisdoctor.ui.dashboard.model.registration.DistrictListResponseModel
import com.hmisdoctor.ui.dashboard.model.registration.StateListResponseModel
import com.hmisdoctor.ui.dashboard.model.registration.TalukListResponseModel
import com.hmisdoctor.ui.dashboard.model.registration.VilliageListResponceModel
import com.hmisdoctor.ui.dashboard.model.*
import com.hmisdoctor.ui.dashboard.model.registration.*
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.ui.quick_reg.model.*
import com.hmisdoctor.ui.quick_reg.model.request.LabNameSearchResponseModel
import com.hmisdoctor.ui.out_patient.model.search_request_model.SearchPatientRequestModel
import com.hmisdoctor.ui.out_patient.search_response_model.SearchResponseModel
import com.hmisdoctor.ui.quick_reg.model.*
import com.hmisdoctor.ui.quick_reg.model.labapprovalresult.ApprovalRequestModel
import com.hmisdoctor.ui.quick_reg.model.labapprovalresult.LabApprovalResultResponse
import com.hmisdoctor.ui.quick_reg.model.labapprovalresult.LabApprovalSpinnerResponseModel
import com.hmisdoctor.ui.quick_reg.model.labapprovalresult.response.OrderApprovedResponseModel
import com.hmisdoctor.ui.quick_reg.model.labtest.request.LabTestApprovalRequestModel
import com.hmisdoctor.ui.quick_reg.model.labtest.request.LabTestRequestModel
import com.hmisdoctor.ui.quick_reg.model.labtest.request.LabTestSampleAcceptanceRequestModel
import com.hmisdoctor.ui.quick_reg.model.labtest.request.orderRequest.OrderProcessDetailsResponseModel
import com.hmisdoctor.ui.quick_reg.model.labtest.request.orderRequest.Req
import com.hmisdoctor.ui.quick_reg.model.labtest.response.LabAssignedToResponseModel
import com.hmisdoctor.ui.quick_reg.model.labtest.response.LabTestApprovalResponseModel
import com.hmisdoctor.ui.quick_reg.model.labtest.response.LabTestResponseModel
import com.hmisdoctor.ui.quick_reg.model.labtest.response.LabTestSpinnerResponseModel
import com.hmisdoctor.ui.quick_reg.model.request.QuickRegistrationRequestModel
import com.hmisdoctor.ui.quick_reg.model.request.QuickRegistrationUpdateRequestModel
import com.hmisdoctor.utils.Utils
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject


class LabTestApprovalViewModel(
    application: Application?
) : AndroidViewModel(
    application!!
) {



    var enterOTPEditText = MutableLiveData<String>()
    var enterNewPasswordEditText = MutableLiveData<String>()
    var enterConfirmPasswordEditText = MutableLiveData<String>()
    var progress = MutableLiveData<Int>()
    var errorText = MutableLiveData<String>()


    var userDetailsRoomRepository: UserDetailsRoomRepository? = null

    var facility_id:Int?=0

    var appPreferences: AppPreferences? = null



    init {

        userDetailsRoomRepository = UserDetailsRoomRepository(application!!)

        appPreferences = AppPreferences.getInstance(application, AppConstants.SHARE_PREFERENCE_NAME)

        //progress.value = 8
        facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)
    }



    fun getLabTestApprovalList(requestLabApprovalListRequest: LabTestApprovalRequestModel, GetLabTestApprovalListRetrofitCallback: RetrofitCallback<LabTestApprovalResponseModel>
    ) {
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()

        apiService?.getLabTestApproval(AppConstants.ACCEPT_LANGUAGE_EN,
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!, facility_id!!,true,
            requestLabApprovalListRequest
        )?.enqueue(RetrofitMainCallback(GetLabTestApprovalListRetrofitCallback))

    }

    fun getLabTestApprovalListSecond(labTestApprovalRequestModel: LabTestApprovalRequestModel, labTestApprovalResponseSecondRetrofitCallback: RetrofitCallback<LabTestApprovalResponseModel>) {

        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()

        apiService?.getLabTestApproval(AppConstants.ACCEPT_LANGUAGE_EN,
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!, facility_id!!,true,
            labTestApprovalRequestModel
        )?.enqueue(RetrofitMainCallback(labTestApprovalResponseSecondRetrofitCallback))


    }
    fun orderDetailsGet(req: com.hmisdoctor.ui.quick_reg.model.labapprovalresult.Req, GetLabTestSampleListRetrofitCallback: RetrofitCallback<LabApprovalResultResponse>
    ) {
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()

        apiService?.orderDetailsGetLabApproval(AppConstants.ACCEPT_LANGUAGE_EN,
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!, facility_id!!,
            req
        )?.enqueue(RetrofitMainCallback(GetLabTestSampleListRetrofitCallback))

    }
    fun getapprovalSpinner(
        labApprovalSpinnerRetrofitCallback: RetrofitCallback<LabApprovalSpinnerResponseModel>
    ) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val jsonBody = JSONObject()

        try {
            jsonBody.put("table_name", "auth_status")
            jsonBody.put("sortField", "display_order")
            jsonBody.put("sortOrder", "ASC")
            jsonBody.put("status",1)

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

        apiService?.getApprovalResultSpinner(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facility_id!!,AppConstants.ACCEPT_LANGUAGE_EN,
            body
        )?.enqueue(RetrofitMainCallback(labApprovalSpinnerRetrofitCallback))
        return

    }



    fun getTextMethod1(facilityId: Int, ResponseTestMethodRetrofitCallback: RetrofitCallback<LabTestSpinnerResponseModel>) {

        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        val jsonBody = JSONObject()

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }

        try {

            jsonBody.put("sortField", "uuid")
            jsonBody.put("sortOrder", "DESC")
            jsonBody.put("search", "")

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

        apiService?.getLabTestSpinner(AppConstants.ACCEPT_LANGUAGE_EN,
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!, facilityId!!, true,
            body
        )?.enqueue(RetrofitMainCallback(ResponseTestMethodRetrofitCallback))

    }

    fun getTextAssignedTo(facilityId: Int, ResponseTestAssignedToRetrofitCallback: RetrofitCallback<LabAssignedToResponseModel>) {

        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        val jsonBody = JSONObject()

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }

        try {

            jsonBody.put("sortOrder", "ASC")
            jsonBody.put("sortField", "uuid")
            jsonBody.put("status", "1")
            jsonBody.put("pageNo", "0")
            jsonBody.put("paginationSize", 100)
            jsonBody.put("search", "")



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

        apiService?.getLabAssignedToSpinner(AppConstants.ACCEPT_LANGUAGE_EN,
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!, facilityId, true,
            body
        )?.enqueue(RetrofitMainCallback(ResponseTestAssignedToRetrofitCallback))

    }

    fun orderApproved(request: ApprovalRequestModel,ResponseTestAssignedToRetrofitCallback: RetrofitCallback<OrderApprovedResponseModel>) {

        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()

        apiService?.orderApproved(AppConstants.ACCEPT_LANGUAGE_EN,
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!, facility_id!!,
            request
        )?.enqueue(RetrofitMainCallback(ResponseTestAssignedToRetrofitCallback))
    }

}