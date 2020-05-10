package com.hmisdoctor.ui.emr_workflow.investigation.view_model
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.hmisdoctor.R
import com.hmisdoctor.application.HmisApplication
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.callbacks.RetrofitMainCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.ui.emr_workflow.investigation.model.InvestigationSearchResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddAllDepatResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddTestNameResponse
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject


class ManageInvestFavourteViewModel(
    application: Application?


) : AndroidViewModel(
    application!!
) {

    var errorText = MutableLiveData<String>()
    var progress = MutableLiveData<Int>()
    var appPreferences: AppPreferences? = null
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    var department_UUID : Int? = 0
    init {
        progress.value = 8
        userDetailsRoomRepository = UserDetailsRoomRepository(application!!)
        appPreferences = AppPreferences.getInstance(application, AppConstants.SHARE_PREFERENCE_NAME)
        department_UUID = appPreferences?.getInt(AppConstants?.DEPARTMENT_UUID)
    }




    var facilityID = appPreferences?.getInt(AppConstants?.FACILITY_UUID)

    fun getDepartmentList(
        facilityID: Int?,
        FavdepartmentRetrofitCallBack: RetrofitCallback<FavAddResponseModel>
    ) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        val jsonBody = JSONObject()
        try {
            jsonBody.put("uuid", department_UUID)
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

        apiService?.getFavDepartmentList(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,facilityID!!,
            body
        )?.enqueue(RetrofitMainCallback(FavdepartmentRetrofitCallBack))
        return
    }

    fun getAllDepartment( facilityID: Int?,
                          favAddAllDepartmentCallBack: RetrofitCallback<FavAddAllDepatResponseModel>){
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        val jsonBody = JSONObject()

        try {
            jsonBody.put("facilityBased", true)
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

        apiService?.getFavddAllADepartmentList(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,facilityID!!,
            body
        )?.enqueue(RetrofitMainCallback(favAddAllDepartmentCallBack))
        return

    }

    fun getTestName(name: String, favAddTestNameCallBack: RetrofitCallback<InvestigationSearchResponseModel>) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        val jsonBody = JSONObject()

        try {
            jsonBody.put("search", name)
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

        apiService?.getInvestigationSearch(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,facilityID!!,
            body
        )?.enqueue(RetrofitMainCallback(favAddTestNameCallBack))
        return
    }


}