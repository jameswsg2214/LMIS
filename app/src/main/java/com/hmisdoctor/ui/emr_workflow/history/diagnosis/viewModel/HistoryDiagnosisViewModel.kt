package com.hmisdoctor.ui.emr_workflow.history.diagnosis.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.hmisdoctor.R
import com.hmisdoctor.application.HmisApplication
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.callbacks.RetrofitMainCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppConstants.Companion.BEARER_AUTH
import com.hmisdoctor.ui.emr_workflow.history.diagnosis.model.DiagnosisSearchResponseModel
import com.hmisdoctor.ui.emr_workflow.history.diagnosis.model.HistoryDiagnosisCreateResponseModel
import com.hmisdoctor.ui.emr_workflow.history.diagnosis.model.HistoryDiagnosisResponseModel
import com.hmisdoctor.ui.emr_workflow.history.diagnosis.model.HistoryDiagnosisUpdateResponseModel
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import okhttp3.RequestBody

class HistoryDiagnosisViewModel(application: Application) : AndroidViewModel(application) {

    var errorText = MutableLiveData<String>()

    var progress = MutableLiveData<Int>()

    var userDetailsRoomRepository: UserDetailsRoomRepository? = null

    init {

        userDetailsRoomRepository = UserDetailsRoomRepository(application)

    }



    fun getlist(facilityId: Int?,patient_id:Int?, department_uuid : Int?, getallListRetrofitCallback: RetrofitCallback<HistoryDiagnosisResponseModel>) {


        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        if (facilityId != null) {
            if (patient_id != null) {
                apiService?.getDiagnosisList(BEARER_AUTH+userDataStoreBean?.access_token,
                    userDataStoreBean?.uuid!!,facilityId,"getLatestDiagnosis",10,
                    patient_id,department_uuid!!)?.enqueue(
                    RetrofitMainCallback(getallListRetrofitCallback)
                )
            }
        }
        return
    }


    fun getComplaintSearchResult(
        facility_uuid: Int?,
        query: String,
        dignosisSearchRetrofitCallBack1: RetrofitCallback<DiagnosisSearchResponseModel>
    ) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        apiService?.searchDiagnosis(
            AppConstants.BEARER_AUTH +userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facility_uuid!!,
            "filterbythree",query
        )?.enqueue(RetrofitMainCallback(dignosisSearchRetrofitCallBack1))
    }


    fun postDiagnosisData(facility_uuid: Int, diagnosisRequest: RequestBody, postAllergyRetrofitCallBack: RetrofitCallback<HistoryDiagnosisCreateResponseModel>){

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }

        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.createDiagnosis(BEARER_AUTH+userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facility_uuid,diagnosisRequest
        )?.enqueue(RetrofitMainCallback(postAllergyRetrofitCallBack))
        return
    }



    fun updateDiagnosisData(facility_uuid: Int,patient_diagnosis_id: Int?,
                            patient_uuid: Int?,
                            department_uuid: Int?,
                            diagnosisupdateRequest: RequestBody,
                            updateDiagnosisRetrofitCallBack: RetrofitCallback<HistoryDiagnosisUpdateResponseModel>){

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }

        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.updateDiagnosis(BEARER_AUTH+userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facility_uuid,patient_diagnosis_id!!,patient_uuid!!,department_uuid!!,diagnosisupdateRequest
        )?.enqueue(RetrofitMainCallback(updateDiagnosisRetrofitCallBack))
        return
    }
}