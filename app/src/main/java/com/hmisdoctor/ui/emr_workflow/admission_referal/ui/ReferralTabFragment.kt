package com.hmisdoctor.ui.emr_workflow.admission_referal.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.FragmentTabRefferalBinding
import com.hmisdoctor.ui.emr_workflow.admission_referal.model.ReasonResponseContent
import com.hmisdoctor.ui.emr_workflow.admission_referal.model.ReasonResponseModel
import com.hmisdoctor.ui.emr_workflow.admission_referal.model.RefferaNextResponseModel
import com.hmisdoctor.ui.emr_workflow.admission_referal.model.RefferalNextRequestModel
import com.hmisdoctor.ui.emr_workflow.admission_referal.view_model.AdmissionViewModel
import com.hmisdoctor.ui.emr_workflow.chief_complaint.view_model.AdmissionViewModelFactory
import com.hmisdoctor.ui.emr_workflow.chief_complaint.view_model.LabViewModelFactory
import com.hmisdoctor.ui.emr_workflow.delete.model.DeleteResponseModel
import com.hmisdoctor.ui.emr_workflow.history.surgery.model.response.InstitutionresponseContent
import com.hmisdoctor.ui.emr_workflow.history.surgery.model.response.SurgeryInstitutionResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddAllDepatResponseContent
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddAllDepatResponseModel
import com.hmisdoctor.ui.emr_workflow.prescription.ui.ErrorAPIClass

import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import kotlinx.android.synthetic.main.activity_quick_registration.*
import retrofit2.Response
import java.io.IOException


class ReferralTabFragment : Fragment(){

    private var customdialog: Dialog?=null
    private var typeDepartmentList = mutableMapOf<Int,String>()
    private var facility_UUID: Int?  =0
    @SuppressLint("ClickableViewAccessibility")
    var binding : FragmentTabRefferalBinding ?=null
    private var department_uuid: Int? = null
    var encounter_id:Int?=0
    private var patientUUId: Int? = 0;





    private var viewModel: AdmissionViewModel? = null
    private var listAllAddDepartmentItems: List<FavAddAllDepatResponseContent?> = ArrayList()
    private var listAllinstituteItems: List<InstitutionresponseContent?> = ArrayList()
    private var listAllReasonItems: List<ReasonResponseContent?> = ArrayList()


    private var favAddResponseMap = mutableMapOf<Int, String>()
    private var AddinstituteResponseMap = mutableMapOf<Int, String>()
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null






    private var utils: Utils? = null
    var appPreferences: AppPreferences? = null
     override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_tab_refferal,
                container,
                false
            )

        viewModel = AdmissionViewModelFactory(
            activity!!.application
        ).create(AdmissionViewModel::class.java)
        binding?.viewModel = viewModel
        binding!!.lifecycleOwner = this
         appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
         facility_UUID = appPreferences?.getInt(AppConstants.FACILITY_UUID)
         department_uuid = appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)
          patientUUId = appPreferences?.getInt(AppConstants.PATIENT_UUID)
          encounter_id = appPreferences?.getInt(AppConstants.ENCOUNTER_UUID)


         userDetailsRoomRepository = UserDetailsRoomRepository(requireActivity().application)

         var userDataStoreBean = userDetailsRoomRepository?.getUserDetails()


         utils = Utils(requireContext())

         viewModel!!.getSurgeryInstitutionCallback(userDataStoreBean?.uuid!!,facility_UUID,surgeryInstitutionRetrofitCallback)
         viewModel?.getAllDepartment(facility_UUID,AddAllDepartmentCallBack)
         viewModel?.getReason(facility_UUID!!,ReasonRetrofitCallback)

         binding?.nextCardView?.setOnClickListener{
             val refferalNextRequestModel: RefferalNextRequestModel = RefferalNextRequestModel()
             refferalNextRequestModel.patient_uuid= patientUUId!!.toString()
             refferalNextRequestModel.encounter_uuid= encounter_id!!
             refferalNextRequestModel.encounter_type_uuid=1
             refferalNextRequestModel.doctor_uuid=  userDataStoreBean?.uuid!!.toString()
             refferalNextRequestModel.facility_uuid= facility_UUID.toString()
             refferalNextRequestModel.department_uuid= department_uuid.toString()
             refferalNextRequestModel.referred_date="2020-05-08T09:19:34.442Z"
             refferalNextRequestModel.referral_type_uuid=1
             refferalNextRequestModel.referral_facility_uuid="1"
             refferalNextRequestModel.referral_deptartment_uuid="88"
             refferalNextRequestModel.referral_comments="vccx"
             refferalNextRequestModel.referal_reason_uuid="1"

             viewModel!!.getSaveNext(refferalNextRequestModel,NextOrderRegistrationRetrofitCallback)


         }

         return binding!!.root
    }
    val AddAllDepartmentCallBack =
        object : RetrofitCallback<FavAddAllDepatResponseModel> {
            @SuppressLint("NewApi")
            override fun onSuccessfulResponse(responseBody: Response<FavAddAllDepatResponseModel>?) {
                Log.i("", "" + responseBody?.body()?.responseContents);
                listAllAddDepartmentItems = responseBody?.body()?.responseContents!!
                favAddResponseMap =
                    listAllAddDepartmentItems.map { it?.uuid!! to it.name }.toMap().toMutableMap()
                val adapter =
                    ArrayAdapter<String>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        favAddResponseMap.values.toMutableList()
                    )

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding?.spinnerDepartment!!.adapter = adapter
                binding?.department!!.adapter = adapter


            }

            override fun onBadRequest(errorBody: Response<FavAddAllDepatResponseModel>?) {
                val gson = GsonBuilder().create()
                val responseModel: FavAddAllDepatResponseModel
                try {
                    responseModel = gson.fromJson(
                        errorBody?.errorBody()!!.string(),
                        FavAddAllDepatResponseModel::class.java
                    )
                    utils?.showToast(
                        R.color.negativeToast,
                        binding?.mainLayout!!,
                        ""
                    )
                } catch (e: Exception) {
                    utils?.showToast(
                        R.color.negativeToast,
                        binding?.mainLayout!!,
                        getString(R.string.something_went_wrong)
                    )
                    e.printStackTrace()
                }
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

            override fun onFailure(failure: String) {
                utils?.showToast(R.color.negativeToast, binding?.mainLayout!!, failure)
            }

            override fun onEverytime() {
                viewModel!!.progress.value = 8
            }

        }
    val surgeryInstitutionRetrofitCallback = object : RetrofitCallback<SurgeryInstitutionResponseModel> {
        override fun onSuccessfulResponse(response: Response<SurgeryInstitutionResponseModel>) {

            Log.i("", "" + response?.body()?.responseContents);
            listAllinstituteItems = response?.body()?.responseContents!!
            AddinstituteResponseMap  =
                listAllinstituteItems.map { it?.uuid!! to it.facility?.name!! }.toMap().toMutableMap()
            val adapter =
                ArrayAdapter<String>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    AddinstituteResponseMap.values.toMutableList()
                )

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding?.institutionSpinner!!.adapter = adapter


        }

        override fun onBadRequest(response: Response<SurgeryInstitutionResponseModel>) {

            //Log.e("InsData", "badReq")
            val gson = GsonBuilder().create()
            val responseModel: SurgeryInstitutionResponseModel
            try {
                responseModel = gson.fromJson(
                    response.errorBody()!!.string(),
                    SurgeryInstitutionResponseModel::class.java
                )
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    response.message()
                )
            } catch (e: Exception) {
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    getString(R.string.something_went_wrong)
                )
                e.printStackTrace()
            }
        }

        override fun onServerError(response: Response<*>) {

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

        override fun onFailure(failure: String) {
            utils?.showToast(R.color.negativeToast, binding?.mainLayout!!, failure)
        }

        override fun onEverytime() {
            viewModel!!.progress.value = 8
        }
    }
    val ReasonRetrofitCallback = object : RetrofitCallback<ReasonResponseModel> {
        override fun onSuccessfulResponse(response: Response<ReasonResponseModel>) {

            Log.i("", "" + response?.body()?.responseContent);
            listAllReasonItems = response?.body()?.responseContent!!
            AddinstituteResponseMap  =
                listAllReasonItems.map { it?.uuid!! to it.name!! }.toMap().toMutableMap()
            val adapter =
                ArrayAdapter<String>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    AddinstituteResponseMap.values.toMutableList()
                )

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding?.reason!!.adapter = adapter
            binding?.reasonSpinner!!.adapter = adapter



        }

        override fun onBadRequest(response: Response<ReasonResponseModel>) {

            //Log.e("InsData", "badReq")
            val gson = GsonBuilder().create()
            val responseModel: ReasonResponseModel
            try {
                responseModel = gson.fromJson(
                    response.errorBody()!!.string(),
                    ReasonResponseModel::class.java
                )
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    response.message()
                )
            } catch (e: Exception) {
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    getString(R.string.something_went_wrong)
                )
                e.printStackTrace()
            }
        }

        override fun onServerError(response: Response<*>) {

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

        override fun onFailure(failure: String) {
            utils?.showToast(R.color.negativeToast, binding?.mainLayout!!, failure)
        }

        override fun onEverytime() {
            viewModel!!.progress.value = 8
        }
    }

    var NextOrderRegistrationRetrofitCallback = object :
        RetrofitCallback<RefferaNextResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<RefferaNextResponseModel>?) {
            Log.i("", "" + responseBody?.body()?.message);

            utils?.showToast(
                R.color.positiveToast,
                mainLayout!!,
                "Register Success"

            )



        }

        override fun onBadRequest(response: Response<RefferaNextResponseModel>) {
            Log.e("badreq",response.toString())
            val gson = GsonBuilder().create()
            val responseModel: RefferaNextResponseModel
            var mError = ErrorAPIClass()
            try {
                mError = gson.fromJson(response.errorBody()!!.string(), ErrorAPIClass::class.java)

            } catch (e: IOException) { // handle failure to read error
            }
        }
        override fun onServerError(response: Response<*>) {
            utils?.showToast(
                R.color.negativeToast,
                mainLayout!!,
                "serverError"
            )

        }
        override fun onUnAuthorized() {
            utils?.showToast(
                R.color.negativeToast,
                mainLayout,
                getString(R.string.unauthorized)
            )
        }
        override fun onForbidden() {
            utils?.showToast(
                R.color.negativeToast,
                mainLayout!!,
                "Forbidden"
            )

        }
        override fun onFailure(failure: String) {
            utils?.showToast(
                R.color.negativeToast,
                mainLayout!!,
                failure
            )
        }
        override fun onEverytime() {
            viewModel!!.progress.value = 8
        }

    }











}