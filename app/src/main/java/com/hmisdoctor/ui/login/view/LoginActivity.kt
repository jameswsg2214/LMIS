package com.hmisdoctor.ui.login.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppConstants.Companion.ROLE_EMR
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.ActivityLoginBinding
import com.hmisdoctor.ui.dashboard.model.ChangePasswordOTPResponseModel
import com.hmisdoctor.ui.dashboard.model.PasswordChangeResponseModel
import com.hmisdoctor.ui.dashboard.view.DashBoardActivity
import com.hmisdoctor.ui.emr_workflow.history.surgery.model.response.SurgeryInstitutionResponseModel
import com.hmisdoctor.ui.institute.model.DepartmentResponseModel
import com.hmisdoctor.ui.institute.view.InstituteDialogFragment
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.ui.login.model.login_response_model.LoginResponseModel
import com.hmisdoctor.ui.login.model.login_response_model.ModuleDetail
import com.hmisdoctor.ui.login.view_model.LoginViewModel
import com.hmisdoctor.ui.login.view_model.LoginViewModelFactory
import com.hmisdoctor.utils.CustomProgressDialog
import com.hmisdoctor.utils.Utils
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    private var facilityUserID: Int? =0
    private var departmentID: Int?=0
    private var facilitylevelID: Int? =0
    private var module_details: List<ModuleDetail?>? = ArrayList()
    private var binding: ActivityLoginBinding? = null
    private var viewModel: LoginViewModel? = null
    private var utils: Utils? = null
    var action: Boolean? = null
    var enableeye : Boolean?=true
    private var customProgressDialog: CustomProgressDialog? = null
    var appPreferences: AppPreferences? = null
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        viewModel = LoginViewModelFactory(
            application,
            loginRetrofitCallBack,
            otpRetrofitCallBack,
            changePasswordRetrofitCallBack
        ).create(LoginViewModel::class.java)

        binding!!.lifecycleOwner = this
        binding?.viewModel = viewModel
        utils = Utils(this)
        customProgressDialog = CustomProgressDialog(this)
        appPreferences = AppPreferences.getInstance(this, AppConstants.SHARE_PREFERENCE_NAME)
        viewModel!!.errorText.observe(this,
            Observer { toastMessage ->
                utils!!.showToast(R.color.negativeToast, binding?.mainLayout!!, toastMessage)
            })

        viewModel!!.progress.observe(this,
            Observer { progress ->
                if (progress == View.VISIBLE) {
                    customProgressDialog!!.show()
                } else if (progress == View.GONE) {
                    customProgressDialog!!.dismiss()
                }
            })
        binding?.click?.setOnClickListener {
            val encryptvalue = Utils.encrypt(viewModel?.password?.value)
            viewModel?.onLoginClicked(encryptvalue)
        }
        binding?.termsAndCndition?.setOnClickListener {
            val ft = (this)!!.supportFragmentManager.beginTransaction()
            val dialog = TremsAndConditionActivity()
            dialog.show(ft, "Tag")

        }
        binding?.termsAndCndition1?.setOnClickListener {
            val ft = (this)!!.supportFragmentManager.beginTransaction()
            val dialog = TremsAndConditionActivity()
            dialog.show(ft, "Tag")

        }
        binding?.termsAndCndition2?.setOnClickListener {
            val ft = (this)!!.supportFragmentManager.beginTransaction()
                   val dialog = TremsAndConditionActivity()
                   dialog.show(ft, "Tag")

        }
      binding?.passwordEdittext?.setOnTouchListener(OnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding?.passwordEdittext?.right!! - binding?.passwordEdittext?.getCompoundDrawables()?.get(DRAWABLE_RIGHT)?.getBounds()?.width()!!
                ) {

                    if(this.enableeye!!)
                    {
                        binding?.passwordEdittext?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye__open, 0);
                         enableeye = false
                        binding?.passwordEdittext?.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                    }
                    else{
                        binding?.passwordEdittext?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_close, 0);
                        enableeye = true
                        binding?.passwordEdittext?.setInputType(
                            InputType.TYPE_CLASS_TEXT or
                                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }

                    return@OnTouchListener true
                }
            }
            false
        })

        binding?.newPasswordEditText?.setOnTouchListener(OnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding?.newPasswordEditText?.right!! - binding?.newPasswordEditText?.getCompoundDrawables()?.get(DRAWABLE_RIGHT)?.getBounds()?.width()!!
                ) {

                    if(this.enableeye!!)
                    {
                        binding?.newPasswordEditText?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye__open, 0);
                        enableeye = false
                        binding?.newPasswordEditText?.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                    }
                    else{
                        binding?.newPasswordEditText?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_close, 0);
                        enableeye = true
                        binding?.newPasswordEditText?.setInputType(
                            InputType.TYPE_CLASS_TEXT or
                                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }

                    return@OnTouchListener true
                }
            }
            false
        })





    }
    val loginRetrofitCallBack = object : RetrofitCallback<LoginResponseModel> {
        override fun onSuccessfulResponse(response: Response<LoginResponseModel>?) {
            if(response!!.body()!!.msg!=""){
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    response.body()!!.msg.toString()
                )
            }

            //appPreferences?.saveInt(AppConstants.ROLEUUID,  response.body()?.responseContents?.activityDetails?.get(0)?.uuid!!)

            module_details = response.body()?.responseContents?.moduleDetails

            val checkEMR= response?.body()?.responseContents?.moduleDetails!!.any{ it!!.code == AppConstants.ROLE_EMR}

            appPreferences?.saveBoolean(AppConstants.EMRCHECK,false)

            val checkLIMS= response?.body()?.responseContents?.moduleDetails!!.any{ it!!.code == AppConstants.ROLE_LMIS}

            appPreferences?.saveBoolean(AppConstants.LMISCHECK,checkLIMS)

            val checkREGISTER= response?.body()?.responseContents?.moduleDetails!!.any{ it!!.code == AppConstants.ROLE_Registration}

            appPreferences?.saveBoolean(AppConstants.REGISTRATIONCHECK,checkREGISTER)


            val userDetailsRoomRepository = UserDetailsRoomRepository(application)
            userDetailsRoomRepository.insertData(response?.body()!!.responseContents?.userDetails!!)

            /* facilty check
 */
            if((checkEMR || checkLIMS)|| checkREGISTER) {
                if (module_details?.size!! > 1) {
                    viewModel!!.getfacilityCallback(
                        response.body()!!.responseContents?.userDetails?.uuid,
                        facilitycallbackRetrofitCallback
                    )
                }
            }
            else{

                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    "Invalid User"
                )

            }

        }
        override fun onBadRequest(response: Response<LoginResponseModel>?) {
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
            viewModel!!.progress.value = 8
        }
    }

    val otpRetrofitCallBack = object : RetrofitCallback<ChangePasswordOTPResponseModel> {

        override fun onSuccessfulResponse(responseBody: Response<ChangePasswordOTPResponseModel>?) {

            if(responseBody?.body()?.status == "success"){

                Log.e("SendOTP",responseBody?.body()?.toString()!!)
                viewModel!!.forgetUsernemeLayout.value=8
                viewModel!!.changePasswordLayout.value=0

/*                viewModel!!.otp.value = responseBody?.body()?.responseContents?.otp

                viewModel!!.otp.value = responseBody?.body()?.responseContents?.otp*/

                Toast.makeText(this@LoginActivity,responseBody?.body()?.msg, Toast.LENGTH_LONG).show()
                //binding?.otpLayout!!.visibility = View.GONE
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
            viewModel!!.progress.value = 8
        }
    }

    val changePasswordRetrofitCallBack = object : RetrofitCallback<PasswordChangeResponseModel> {

        override fun onSuccessfulResponse(responseBody: Response<PasswordChangeResponseModel>?) {
            utils?.showToast(
                R.color.positiveToast,
                binding?.mainLayout!!,
                responseBody?.body()?.msg!!)
            viewModel!!.visisbleLogin()

            Toast.makeText(this@LoginActivity,responseBody?.body()?.msg!!,Toast.LENGTH_LONG).show()


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
            viewModel!!.progress.value = 8
        }
    }



    val facilitycallbackRetrofitCallback = object : RetrofitCallback<SurgeryInstitutionResponseModel> {

        override fun onSuccessfulResponse(responseBody: Response<SurgeryInstitutionResponseModel>?) {

            Log.i("",""+responseBody?.body()?.responseContents)
            Log.i("",""+responseBody?.body()?.responseContents)
            Log.i("",""+responseBody?.body()?.responseContents)


            if(responseBody?.body()?.responseContents?.size!! >1)
            {
                 val ft = supportFragmentManager.beginTransaction()
                      val dialog = InstituteDialogFragment()
                      dialog.show(ft, "Tag")
            }

            else{
                facilitylevelID = responseBody?.body()?.responseContents?.get(0)?.facility_uuid
                facilityUserID = responseBody?.body()?.responseContents?.get(0)!!.user_uuid
                appPreferences?.saveString(AppConstants.INSTITUTION_NAME, responseBody?.body()?.responseContents?.get(0)!!.facility?.name)
                viewModel?.getDepartmentList(facilitylevelID,facilityUserID,depatmentCallback)
            }
        }
        override fun onBadRequest(response: Response<SurgeryInstitutionResponseModel>?) {
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
            viewModel!!.progress.value = 8
        }
    }


    /*
    Department
     */

    val depatmentCallback = object : RetrofitCallback<DepartmentResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<DepartmentResponseModel>?) {
            Log.i("",""+responseBody?.body()?.responseContents)
            var jsonresponse = Gson().toJson(responseBody?.body()?.responseContents)
            Log.i("",""+jsonresponse)
            Log.i("",""+jsonresponse)
            Log.i("",""+jsonresponse)
            Log.i("",""+jsonresponse)
            if(responseBody?.body()?.responseContents?.size!! >1)
            {
                val ft = supportFragmentManager.beginTransaction()
                val dialog = InstituteDialogFragment()
                dialog.show(ft, "Tag")
            }
            else{
                facilitylevelID = responseBody.body()?.responseContents?.get(0)?.facility_uuid
                departmentID = responseBody.body()?.responseContents?.get(0)?.department_uuid
                appPreferences?.saveInt(AppConstants.FACILITY_UUID, facilitylevelID!!)
                appPreferences?.saveInt(AppConstants.DEPARTMENT_UUID,departmentID!!)
                startActivity(Intent(this@LoginActivity, DashBoardActivity::class.java))
            }
        }
        override fun onBadRequest(response: Response<DepartmentResponseModel>?) {
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
            viewModel!!.progress.value = 8
        }
    }



}