package com.hmisdoctor.ui.login.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.ui.dashboard.model.ChangePasswordOTPResponseModel
import com.hmisdoctor.ui.dashboard.model.PasswordChangeResponseModel
import com.hmisdoctor.ui.login.model.login_response_model.LoginResponseModel

class LoginViewModelFactory(
    private var application: Application?,
    private var loginRetrofitCallBack: RetrofitCallback<LoginResponseModel>,
    private var otpRetrofitCallBack: RetrofitCallback<ChangePasswordOTPResponseModel>,
    private var changePasswordRetrofitCallBack: RetrofitCallback<PasswordChangeResponseModel>
) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(
            application,
            loginRetrofitCallBack,
            otpRetrofitCallBack,
            changePasswordRetrofitCallBack

        ) as T
    }
}
