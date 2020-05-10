package com.hmisdoctor.ui.login.view_model

import android.app.Application
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.hmisdoctor.R
import com.hmisdoctor.application.HmisApplication
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.callbacks.RetrofitMainCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.ui.dashboard.model.ChangePasswordOTPResponseModel
import com.hmisdoctor.ui.dashboard.model.PasswordChangeResponseModel
import com.hmisdoctor.ui.emr_workflow.history.surgery.model.response.SurgeryInstitutionResponseModel
import com.hmisdoctor.ui.institute.model.DepartmentResponseModel
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.ui.login.model.login_response_model.LoginResponseModel
import com.hmisdoctor.utils.Utils
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject

class LoginViewModel(
    application: Application?,
    private var loginRetrofitCallBack: RetrofitCallback<LoginResponseModel>?,
    private var otpRetrofitCallBack: RetrofitCallback<ChangePasswordOTPResponseModel>?,
    private var changePasswordRetrofitCallBack:  RetrofitCallback<PasswordChangeResponseModel>?
) : AndroidViewModel(
    application!!
) {
    var username = MutableLiveData<String>()
    var password = MutableLiveData<String>()
    var errorText = MutableLiveData<String>()
    var progress = MutableLiveData<Int>()

    var loginLayout = MutableLiveData<Int>()
    var sendOptLayout=MutableLiveData<Int>()
    var forgetUsernemeLayout=MutableLiveData<Int>()
    var changePasswordLayout=MutableLiveData<Int>()

    var forgotpasswordusername = MutableLiveData<String>()
    var otp = MutableLiveData<String>()
    var changePassword = MutableLiveData<String>()
    var confirmPassword = MutableLiveData<String>()
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    init {
        username.value = ""
        password.value = ""
       /* username.value = "hmisqa"
        password.value = "oasyshmis"*/
        forgotpasswordusername.value = ""
        otp.value= ""
        changePassword.value = ""
        confirmPassword.value = ""
        loginLayout.value=0
        sendOptLayout.value=8
        forgetUsernemeLayout.value=0
        changePasswordLayout.value=8
        userDetailsRoomRepository = UserDetailsRoomRepository(application!!)
      //  utils=Utils(this)
    }
    fun onLoginClicked(passwordEncryptValue: Any) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        if(username.value == "")
        {
            errorText.value = "Please Enter username"
            return
        }
        if(password.value=="")
        {
            errorText.value = "Please Enter password"
            return
        }
        progress.value = 0
        val aiiceApplication = HmisApplication.get(getApplication())
        val apiService = aiiceApplication.getRetrofitService()
        apiService?.getLoginDetails(username.value,passwordEncryptValue.toString())?.enqueue(RetrofitMainCallback(loginRetrofitCallBack))
        return
    }
    fun visisbleSendOTp(){
        loginLayout.value=8
        sendOptLayout.value=0
    }
    fun visisbleLogin(){
        loginLayout.value=0
        sendOptLayout.value=8
        forgetUsernemeLayout.value=0
        changePasswordLayout.value=8
        forgotpasswordusername.value = ""
        otp.value= ""
        changePassword.value = ""
        confirmPassword.value = ""
    }
    fun validateSendOTp(){
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        if(forgotpasswordusername.value!!.trim().isEmpty())
        {   errorText.value = "Please Enter username/Mobile number"
            return }
        // api call for send otp
        val jsonBody = JSONObject()
        try {
            jsonBody.put("username", forgotpasswordusername.value!!.trim())
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
        apiService?.getOtpForPasswordChange(body)!!.enqueue(
            RetrofitMainCallback(otpRetrofitCallBack)
        )
        //responce success visiblity change
        return
    }
    fun validateChangePassword(){
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        if(otp.value!!.trim().isEmpty())
        {
            errorText.value = "Please Enter Otp"
            return
        }
        if(changePassword.value!!.trim().isEmpty())
        {
            errorText.value = "Please Enter Password"
            return
        }
        if(confirmPassword.value!!.trim().isEmpty())
        {
            errorText.value = "Please Enter Confirm Password"
            return
        }
        if(changePassword.value!!.trim()!=confirmPassword.value!!.trim())
        {
            errorText.value = "Please Check Change Password & Confirm Password Mismatched"
            return
        }
        // api call for change password & call login view again
        val jsonBody = JSONObject()
        try {
            jsonBody.put("username", forgotpasswordusername.value!!.trim())
            jsonBody.put("otp", otp.value!!.trim())
            jsonBody.put("password", Utils.encrypt(changePassword.value!!.trim()).toString())
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
        apiService?.getPasswordChanged(body)?.enqueue(RetrofitMainCallback(changePasswordRetrofitCallBack))

        return
    }
    fun getfacilityCallback(userId: Int?,  facilityCallback: RetrofitCallback<SurgeryInstitutionResponseModel>) {
        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        val jsonBody = JSONObject()
        try {
            jsonBody.put("userId", userId.toString())
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
        apiService?.getFaciltyCheck(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userId!!, body)?.enqueue(RetrofitMainCallback(facilityCallback))
    }
    fun getDepartmentList(facilitylevelID: Int?, facilityUserID : Int?, depatmentCallback: RetrofitCallback<DepartmentResponseModel>) {

        if (!Utils.isNetworkConnected(getApplication())) {
            errorText.value = getApplication<Application>().getString(R.string.no_internet)
            return
        }
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        val jsonBody = JSONObject()
        try {
            jsonBody.put("facility_uuid", facilitylevelID)
            jsonBody.put("Id", facilityUserID)
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

        apiService?.getDepartmentList(
            AppConstants.BEARER_AUTH + userDataStoreBean?.access_token,
            userDataStoreBean?.uuid!!,
            body
        )?.enqueue(RetrofitMainCallback(depatmentCallback))
        return
    }

}