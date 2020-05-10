package com.hmisdoctor.ui.emr_workflow.treatment_kit.view_model

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
import com.hmisdoctor.ui.emr_workflow.delete.model.DeleteResponseModel
import com.hmisdoctor.ui.emr_workflow.diagnosis.model.DiagnosisRequest
import com.hmisdoctor.ui.emr_workflow.diagnosis.model.DiagnosisResponseModel
import com.hmisdoctor.ui.emr_workflow.diagnosis.model.diagonosissearch.DiagonosisSearchResponse
import com.hmisdoctor.ui.emr_workflow.lab.model.LabToLocationResponse
import com.hmisdoctor.ui.emr_workflow.lab.model.LabTypeResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.favresponse.FavSearchResponce
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesResponseModel
import com.hmisdoctor.ui.emr_workflow.prescription.model.*
import com.hmisdoctor.ui.emr_workflow.treatment_kit.model.TreatmentKitPrevResponsModel
import com.hmisdoctor.ui.emr_workflow.treatment_kit.model.TreatmentkitSearchResponseModel
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject

class TreatmentKitViewModel (
    application: Application
) : AndroidViewModel(
    application
) {
    var errorText = MutableLiveData<String>()
    var progress = MutableLiveData<Int>()
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    var appPreferences : AppPreferences? = null
    var StoreMasterID : Int?=0



    init {
        userDetailsRoomRepository = UserDetailsRoomRepository(application)
        appPreferences = AppPreferences.getInstance(application, AppConstants.SHARE_PREFERENCE_NAME)
        StoreMasterID = appPreferences?.getInt(AppConstants.STOREMASTER_UUID)
    }
    fun getPrescriptionFavourites(  departmentID: Int?,emrWorkFlowRetrofitCallBack: RetrofitCallback<FavouritesResponseModel>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        apiService?.getFavourites(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token, userDataStoreBean?.uuid!!, departmentID!!,
            AppConstants.FAV_TYPE_ID_PRESCRIPTION
        )?.enqueue(RetrofitMainCallback(emrWorkFlowRetrofitCallBack))
        return
    }


    fun getdiagnosisFavourites(
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

    fun getDiagnosisComplaintSearchResult(
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
    fun getPrescriptionComplaintSearchResult(
        query: String,
        complaintSearchRetrofitCallBack: RetrofitCallback<PrescriptionSearchResponseModel>, facilityID: Int?
    ) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val jsonBody = JSONObject()
        try {
            jsonBody.put("itemname", query)
            jsonBody.put("store_master_uuid", StoreMasterID)

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

        apiService?.getPrescriptionsSearchResult(AppConstants.BEARER_AUTH + userDataStoreBean?.access_token, userDataStoreBean?.uuid!!, facilityID!!, body)?.enqueue(RetrofitMainCallback(complaintSearchRetrofitCallBack))
        return
    }

    fun getLabComplaintSearchResult(facility_id: Int?,
                                 query: String,
                                 complaintSearchRetrofitCallBack: RetrofitCallback<FavSearchResponce>
    ) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val jsonBody = JSONObject()
        try {
            jsonBody.put("sortField", "uuid")
            jsonBody.put("sortOrder", "DESC")
            jsonBody.put("search", query)
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
        apiService?.getLAbSearchResult(AppConstants.BEARER_AUTH + userDataStoreBean?.access_token, userDataStoreBean?.uuid!!,facility_id!!,body)?.enqueue(RetrofitMainCallback(complaintSearchRetrofitCallBack))
        return
    }
    fun getLabToLocation(
        labToRetrofitCallback: RetrofitCallback<LabToLocationResponse>,
        facilityID: Int?
    ) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }

        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        apiService?.getToLocation(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facilityID!!
        )?.enqueue(RetrofitMainCallback(labToRetrofitCallback))
        return

    }

    fun getToLocation(
        labToRetrofitCallback: RetrofitCallback<LabToLocationResponse>,
        facilityID: Int?
    ) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }

        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        apiService?.getToLocationRadiology(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facilityID!!
        )?.enqueue(RetrofitMainCallback(labToRetrofitCallback))
        return

    }

    fun getLabType(
        labTypeRetrofitCallback: RetrofitCallback<LabTypeResponseModel>,
        facilityID: Int?
    ) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val jsonBody = JSONObject()
        try {
            jsonBody.put("table_name", "order_priority")
            jsonBody.put("sortField", "uuid")
            jsonBody.put("sortOrder", "DESC")
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

        apiService?.getLabType(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facilityID!!,
            body
        )?.enqueue(RetrofitMainCallback(labTypeRetrofitCallback))
        return

    }
    fun getRadioSearchResult(facility_id: Int?,
                             query: String,
                             radioSearchRetrofitCallBack: RetrofitCallback<FavSearchResponce>
    ) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val jsonBody = JSONObject()
        try {
            jsonBody.put("sortField", "uuid")
            jsonBody.put("sortOrder", "DESC")
            jsonBody.put("search", query)
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

        apiService?.getRadioSearchResult(AppConstants.BEARER_AUTH + userDataStoreBean?.access_token, userDataStoreBean?.uuid!!, facility_id!!, body)?.enqueue(RetrofitMainCallback(radioSearchRetrofitCallBack))
        return
    }
    fun getInvestigationComplaintSearchResult(
        facility_id: Int?,
        query: String,
        complaintSearchRetrofitCallBack: RetrofitCallback<TreatmentkitSearchResponseModel>
    ) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }

        val jsonBody = JSONObject()
        try {
            jsonBody.put("search", query)
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

        apiService?.getTraetmentkitSearch(AppConstants.BEARER_AUTH + userDataStoreBean?.access_token, userDataStoreBean?.uuid!!,facility_id!!, body)?.enqueue(RetrofitMainCallback(complaintSearchRetrofitCallBack))
        return
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
    fun getRouteDetails(
        labTypeRetrofitCallback: RetrofitCallback<PrescriptionRoutResponseModel>,
        facilityID: Int?
    ) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val jsonBody = JSONObject()
        try {

            jsonBody.put("table_name", "drug_route")


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

        apiService?.getRouteDetails(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facilityID!!,
            body
        )?.enqueue(RetrofitMainCallback(labTypeRetrofitCallback))
        return

    }
    fun getInstructionDetails(
        instructionRetrofitCallback: RetrofitCallback<PresInstructionResponseModel>,
        facilityID: Int?
    ) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val jsonBody = JSONObject()
        try {

            jsonBody.put("table_name", "drug_instruction")


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

        apiService?.getInstruction(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facilityID!!,
            body
        )?.enqueue(RetrofitMainCallback(instructionRetrofitCallback))
        return

    }

    fun getFrequencyDetails(
        getFrequencyRetrofitCallback: RetrofitCallback<PresDrugFrequencyResponseModel>,
        facilityID: Int?
    ) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val jsonBody = JSONObject()
        try {
            jsonBody.put("table_name", "drug_frequency")

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
        apiService?.getFrequency(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facilityID!!,body
        )?.enqueue(RetrofitMainCallback(getFrequencyRetrofitCallback))
        return

    }
    fun getPrescriptionDuration(
        getPrescriptionDurationRetrofitCallBack: RetrofitCallback<PrescriptionDurationResponseModel>,
        facilityID: Int?
    ) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val jsonBody = JSONObject()
        try {
            jsonBody.put("table_name", "duration_period")

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
        apiService?.getPrescriptionDuration(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            facilityID!!,body
        )?.enqueue(RetrofitMainCallback(getPrescriptionDurationRetrofitCallBack))
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

/*
    fun getPrevTreatment_kit_Records(patientId: Int?, facilityid: Int?,prevLabrecordsRetrofitCallback: RetrofitCallback<PrevLabResponseModel>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }

        Log.i("",""+patientId)
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        val jsonBody = JSONObject()
        try {
            jsonBody.put("patient_id", patientId)
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
        apiService?.getPrevLab(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!, facilityid!!,body)?.enqueue(RetrofitMainCallback(prevLabrecordsRetrofitCallback))
    }
*/
    fun getPrevTreatment_kit_Records(
        patientUuid: Int,
        facility_id : Int?,
        fetchEncounterRetrofitCallBack: RetrofitCallback<TreatmentKitPrevResponsModel>
    ) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        apiService?.getPrevTreatmentKit(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,facility_id!!,
            174


        )!!.enqueue(
            RetrofitMainCallback(fetchEncounterRetrofitCallBack)
        )
    }



}