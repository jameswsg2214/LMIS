package com.hmisdoctor.ui.emr_workflow.certificate.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.hmisdoctor.R
import com.hmisdoctor.application.HmisApplication
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.callbacks.RetrofitMainCallback
import com.hmisdoctor.config.AppConstants

import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.ui.emr_workflow.admission_referal.model.ReasonResponseModel
import com.hmisdoctor.ui.emr_workflow.admission_referal.model.RefferaNextResponseModel
import com.hmisdoctor.ui.emr_workflow.admission_referal.model.RefferalNextRequestModel
import com.hmisdoctor.ui.emr_workflow.certificate.model.*
import com.hmisdoctor.ui.emr_workflow.documents.model.Attachment
import com.hmisdoctor.ui.emr_workflow.documents.model.DeleteDocumentResponseModel
import com.hmisdoctor.ui.emr_workflow.model.fetch_encounters_response.FectchEncounterResponseModel
import com.hmisdoctor.ui.emr_workflow.op_notes.model.OpNotesExpandableResponseModel
import com.hmisdoctor.ui.emr_workflow.op_notes.model.OpNotesResponsModel
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject

class CertificateViewModel(
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

    fun getNoteTemplate(facilityId: Int, addDocumentTypeResponseCallback: RetrofitCallback<TemplateResponseModel>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.getNoteTEmplate(
            AppConstants.ACCEPT_LANGUAGE_EN,
            AppConstants.BEARER_AUTH +userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facilityId
        )?.enqueue(RetrofitMainCallback(addDocumentTypeResponseCallback))
        return
    }

    fun getItemTemplate( facilityId: Int?, downloadfile: RetrofitCallback<TemplateItemResponseModel>, s: Int) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        val jsonBody = JSONObject()
        try {
            jsonBody.put("Note_temp_id",s)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            jsonBody.toString()
        )
        progress.value = 0
        val hmisApplication = HmisApplication.get(getApplication())
        val apiService = hmisApplication.getRetrofitService()
        apiService?.getItemTemplate( AppConstants.ACCEPT_LANGUAGE_EN,
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,facilityId!!, body)?.enqueue(RetrofitMainCallback(downloadfile))

    }

    fun getSaveNext(request: CertificateRequestModel, PatientNameRetrofitCallback: RetrofitCallback<CertificateResponseModel>) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()

        apiService?.saveCertificate( AppConstants.ACCEPT_LANGUAGE_EN,
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!, facility_id!!,
            request
        )?.enqueue(RetrofitMainCallback(PatientNameRetrofitCallback))

    }

    fun getCertificateAll(
        facilityId: Int,
        patientUuid: Int,
        fetchEncounterRetrofitCallBack: RetrofitCallback<GetCertificateResponseModel>
    ) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        apiService?.getCertificateAll(AppConstants.ACCEPT_LANGUAGE_EN,
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!, facilityId, patientUuid

        )!!.enqueue(
            RetrofitMainCallback(fetchEncounterRetrofitCallBack)
        )
    }








}