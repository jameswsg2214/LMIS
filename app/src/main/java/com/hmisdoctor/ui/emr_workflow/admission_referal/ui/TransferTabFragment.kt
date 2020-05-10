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
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.FragmentFavouriteBinding
import com.hmisdoctor.databinding.FragmentTabAdmissionBinding
import com.hmisdoctor.databinding.FragmentTabTrasfereBinding
import com.hmisdoctor.ui.emr_workflow.admission_referal.model.AdmissionWardResponseModel
import com.hmisdoctor.ui.emr_workflow.admission_referal.model.AmissionWardResponseContent
import com.hmisdoctor.ui.emr_workflow.admission_referal.model.ReasonResponseContent
import com.hmisdoctor.ui.emr_workflow.admission_referal.model.ReasonResponseModel
import com.hmisdoctor.ui.emr_workflow.admission_referal.view_model.AdmissionViewModel
import com.hmisdoctor.ui.emr_workflow.chief_complaint.view_model.AdmissionViewModelFactory
import com.hmisdoctor.ui.emr_workflow.chief_complaint.view_model.LabViewModelFactory
import com.hmisdoctor.ui.emr_workflow.delete.model.DeleteResponseModel
import com.hmisdoctor.ui.emr_workflow.history.surgery.model.response.InstitutionresponseContent
import com.hmisdoctor.ui.emr_workflow.history.surgery.model.response.SurgeryInstitutionResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddAllDepatResponseContent
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddAllDepatResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.view_model.LabViewModel
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesResponseModel
import com.hmisdoctor.ui.emr_workflow.radiology.ui.RadiologyFavouriteFragment
import com.hmisdoctor.ui.emr_workflow.radiology.ui.RadiologyTempleteFragment
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import retrofit2.Response


class TransferTabFragment : Fragment(){

    private var customdialog: Dialog?=null
    private var typeDepartmentList = mutableMapOf<Int,String>()
    private var facility_UUID: Int?  =0
    @SuppressLint("ClickableViewAccessibility")
    var binding : FragmentTabTrasfereBinding ?=null
    private var department_uuid: Int? = null
    private var viewModel: AdmissionViewModel? = null
    private var utils: Utils? = null
    var appPreferences: AppPreferences? = null

    private var listAllAddDepartmentItems: List<FavAddAllDepatResponseContent?> = ArrayList()
    private var listAllinstituteItems: List<InstitutionresponseContent?> = ArrayList()
    private var listAllReasonItems: List<ReasonResponseContent?> = ArrayList()
    private var wardItems: List<AmissionWardResponseContent?> = ArrayList()
    private var wardResponseMap = mutableMapOf<Int, String>()




    private var favAddResponseMap = mutableMapOf<Int, String>()
    private var AddinstituteResponseMap = mutableMapOf<Int, String>()
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
     override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_tab_trasfere,
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
         userDetailsRoomRepository = UserDetailsRoomRepository(requireActivity().application)

         var userDataStoreBean = userDetailsRoomRepository?.getUserDetails()


         utils = Utils(requireContext())

         viewModel!!.getSurgeryInstitutionCallback(userDataStoreBean?.uuid!!,facility_UUID,surgeryInstitutionRetrofitCallback)
         viewModel?.getAllDepartment(facility_UUID,AddAllDepartmentCallBack)
         viewModel?.getReason(facility_UUID!!,ReasonRetrofitCallback)
         binding?.viewModel?.getWArdList(
             facility_UUID,department_uuid!!,WardCallBack

         )


         return binding!!.root
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
            binding?.institution!!.adapter = adapter


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
                binding?.institutionDepartment!!.adapter = adapter
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
            binding?.departmentReason!!.adapter = adapter
            binding?.institutionReason!!.adapter = adapter
            binding?.wardReason!!.adapter = adapter




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
    val WardCallBack =
        object : RetrofitCallback<AdmissionWardResponseModel> {
            @SuppressLint("NewApi")
            override fun onSuccessfulResponse(responseBody: Response<AdmissionWardResponseModel>?) {
                Log.i("", "" + responseBody?.body()?.responseContents);
                wardItems = responseBody?.body()?.responseContents!!
                wardResponseMap =
                    wardItems.map { it?.ward_uuid!! to it.ward_name }.toMap().toMutableMap()
                val adapter =
                    ArrayAdapter<String>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        wardResponseMap.values.toMutableList()
                    )

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding?.wardSpinner!!.adapter = adapter
            }

            override fun onBadRequest(errorBody: Response<AdmissionWardResponseModel>?) {
                val gson = GsonBuilder().create()
                val responseModel: AdmissionWardResponseModel
                try {
                    responseModel = gson.fromJson(
                        errorBody?.errorBody()!!.string(),
                        AdmissionWardResponseModel::class.java
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











}