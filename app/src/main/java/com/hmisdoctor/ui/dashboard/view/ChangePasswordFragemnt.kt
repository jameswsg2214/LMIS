package com.hmisdoctor.ui.dashboard.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.DialogChangePasswordBinding
import com.hmisdoctor.ui.configuration.model.ConfigResponseModel
import com.hmisdoctor.ui.dashboard.model.ChangePasswordOTPResponseModel
import com.hmisdoctor.ui.dashboard.model.PasswordChangeResponseModel
import com.hmisdoctor.ui.dashboard.view_model.ChangePasswordViewModel
import com.hmisdoctor.ui.dashboard.view_model.ChangePasswordViewModelFactory
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import retrofit2.Response

class ChangePasswordFragemnt : DialogFragment() {

    private var content: String? = null
    var binding: DialogChangePasswordBinding? = null
    private var viewModel: ChangePasswordViewModel? = null
    var appPreferences: AppPreferences? = null
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    private var utils: Utils? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content = arguments?.getString(AppConstants.ALERTDIALOG)
        val style = STYLE_NO_FRAME
        val theme = R.style.DialogTheme
        setStyle(style, theme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        binding =
        DataBindingUtil.inflate(inflater, R.layout.dialog_change_password, container, false)
        viewModel = ChangePasswordViewModelFactory(
            requireActivity().application)
            .create(ChangePasswordViewModel::class.java)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner=this


        userDetailsRoomRepository = UserDetailsRoomRepository( requireActivity().application!!)
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        binding?.userNameEditText?.setOnKeyListener(null)
        binding?.userNameEditText?.setText(userDataStoreBean?.user_name)
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        val facility_uuid = appPreferences?.getInt(AppConstants.FACILITY_UUID)

        viewModel!!.errorText.observe(
            this.activity!!,
            Observer { toastMessage ->
                //utils!!.showToast(R.color.negativeToast, binding?.mainLayout!!, toastMessage)
                Toast.makeText(activity,toastMessage,Toast.LENGTH_LONG).show()
            })

        binding?.closeImageView?.setOnClickListener {
            dialog?.dismiss()
        }

        binding?.sendOTPButton?.setOnClickListener {
            viewModel?.getOtp(userDataStoreBean?.user_name.toString(), facility_uuid!!,otpRetrofitCallBack)
        }

        binding?.changePasswordButton?.setOnClickListener {



            viewModel?.onChangePassword(userDataStoreBean?.user_name.toString(),
                viewModel?.enterOTPEditText?.value!!,Utils.encrypt(viewModel?.enterNewPasswordEditText?.value.toString()).toString(),
                changePasswordRetrofitCallBack)


        }
        return binding?.root
    }



    val otpRetrofitCallBack = object : RetrofitCallback<ChangePasswordOTPResponseModel> {

        override fun onSuccessfulResponse(responseBody: Response<ChangePasswordOTPResponseModel>?) {

                //Log.e("GetOTP",responseBody?.body()?.toString()!!)
            if(responseBody?.body()?.status == "success"){
                viewModel?.enterOTPEditText!!.value = responseBody.body()?.responseContents?.otp
                Log.e("SendOTP",responseBody?.body()?.toString()!!)
                Toast.makeText(activity,responseBody?.body()?.msg,Toast.LENGTH_LONG).show()
        //        viewModel?.enterOTPEditText!!.value = responseBody.body()?.responseContents?.otp
                binding?.otpLayout!!.visibility = View.GONE
            }

        }
        override fun onBadRequest(response: Response<ChangePasswordOTPResponseModel>?) {
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }

        override fun onServerError(response: Response<*>?) {
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }

        override fun onUnAuthorized() {
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.unauthorized)
            )
        }

        override fun onForbidden() {
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }

        override fun onFailure(s: String?) {
            if (s != null) {
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    s
                )
            }
        }

        override fun onEverytime() {
            viewModel!!.progressBar.value = 8
        }
    }



    val changePasswordRetrofitCallBack = object : RetrofitCallback<PasswordChangeResponseModel> {

        override fun onSuccessfulResponse(responseBody: Response<PasswordChangeResponseModel>?) {
            utils?.showToast(
                R.color.positiveToast,
                binding?.mainLayout!!,
                responseBody?.body()?.msg!!)

            Toast.makeText(activity,responseBody?.body()?.msg!!,Toast.LENGTH_LONG).show()


        }
        override fun onBadRequest(response: Response<PasswordChangeResponseModel>?) {
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }

        override fun onServerError(response: Response<*>?) {
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }

        override fun onUnAuthorized() {
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.unauthorized)
            )
        }

        override fun onForbidden() {
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }

        override fun onFailure(s: String?) {
            if (s != null) {
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    s
                )
            }
        }

        override fun onEverytime() {
            viewModel!!.progressBar.value = 8
        }
    }


}