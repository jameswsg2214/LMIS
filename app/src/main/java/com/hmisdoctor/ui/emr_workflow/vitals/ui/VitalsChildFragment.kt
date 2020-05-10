package com.hmisdoctor.ui.emr_workflow.vitals.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.FragmentVitalsChildBinding
import com.hmisdoctor.ui.emr_workflow.delete.model.DeleteResponseModel
import com.hmisdoctor.ui.emr_workflow.model.templete.TempleResponseModel
import com.hmisdoctor.ui.emr_workflow.vitals.model.*
import com.hmisdoctor.ui.emr_workflow.vitals.model.response.VitalSaveResponseModel
import com.hmisdoctor.ui.emr_workflow.vitals.model.response.VitalSearchListResponseModel
import com.hmisdoctor.ui.emr_workflow.vitals.model.responseedittemplatevitual.ResponseEditTemplate
import com.hmisdoctor.ui.emr_workflow.vitals.view_model.VitalsViewModel
import com.hmisdoctor.ui.emr_workflow.vitals.view_model.VitalsViewModelFactory
import com.hmisdoctor.utils.Utils
import retrofit2.Response
import kotlin.collections.ArrayList


class VitalsChildFragment : Fragment(),ManageVitalTemplateFragment.OnTemplateRefreshListener {
    private var FaciltyID: Int? = 0
    private var searchPosition: Int? = 0

    private var customdialog: Dialog?=null
    private var binding: FragmentVitalsChildBinding? = null
    lateinit var vitalsTemplatesAdapter: VitalsTemplateAdapter
    var vitalsAdapter: VitalsAdapter? = null
    private var responseContents: String? = ""
    private var viewModel: VitalsViewModel? = null
    private var utils: Utils? = null
    var appPreferences: AppPreferences? = null
    private var department_uuid: Int? = null

    private var patient_Uuid:Int?=null

    lateinit var autoCompleteTextView: AppCompatAutoCompleteTextView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_vitals_child,
                container,
                false
            )
        viewModel = VitalsViewModelFactory(
            requireActivity().application
        )
            .create(VitalsViewModel::class.java)
        binding?.viewModel = viewModel



        binding?.favouriteDrawerCardView?.setOnClickListener {
            binding?.drawerLayout!!.openDrawer(GravityCompat.END)
        }
        binding?.drawerLayout?.drawerElevation = 0f
        binding?.drawerLayout?.setScrimColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )

        binding?.saveCardView?.isEnabled = true
        binding?.saveCardView?.alpha = 0.5f
        binding?.clearCardView?.isEnabled = true
        binding?.clearCardView?.alpha = 0.5f

        utils = Utils(requireContext())
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        department_uuid = appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)
        FaciltyID = appPreferences?.getInt(AppConstants?.FACILITY_UUID)
        viewModel!!.getvitalsTemplate(
            FaciltyID,
            department_uuid,
            vitalsTemplateRetrofitCallBack
        )
        initFavouritesAdapter()
        setADapter()

        binding?.saveCardView?.setOnClickListener {

            val saveData=ArrayList<VitalSaveRequestModel>()

            saveData.clear()

            val allDAta=vitalsAdapter!!.getall()

            Log.i("",""+allDAta)

            Log.i("",""+allDAta)
            Log.i("",""+allDAta)



            for (i in allDAta.indices){
                val vitalSaveRequestModel=VitalSaveRequestModel()

                vitalSaveRequestModel.facility_uuid= FaciltyID.toString()
                vitalSaveRequestModel.department_uuid= department_uuid.toString()
                vitalSaveRequestModel.patient_uuid= patient_Uuid.toString()
                vitalSaveRequestModel.vital_group_uuid=1
                vitalSaveRequestModel.vital_type_uuid=1
                vitalSaveRequestModel.vital_qualifier_uuid=1
                vitalSaveRequestModel.patient_vital_status_uuid=1
                vitalSaveRequestModel.vital_value_type_uuid=allDAta[i].vital_master.vital_value_type_uuid
                vitalSaveRequestModel.vital_master_uuid=allDAta[i].vital_master_uuid
                vitalSaveRequestModel.vital_value=allDAta[i].vital_master.vitals_value
                vitalSaveRequestModel.vital_uom_uuid=allDAta[i].vital_master.uom_master_uuid


                saveData.add(vitalSaveRequestModel)

            }

            viewModel!!.vitalSave(FaciltyID,vitalsSaveRetrofitCallback,saveData)



        }

        viewModel!!.getUmoList(FaciltyID,getUmoListRetrofitCallback)
        viewModel!!.getVitalsName(FaciltyID,getVitalsListRetrofitCallback)
        binding?.manageFavouritesCardView?.setOnClickListener {
            val ft = childFragmentManager.beginTransaction()
            val dialog = ManageVitalTemplateFragment()
            dialog.show(ft, "Tag")
        }


        return binding!!.root
    }

    private fun setADapter() {
        vitalsAdapter = VitalsAdapter(requireActivity(), ArrayList())
        binding?.savevitalsRecyclerView?.adapter = vitalsAdapter

        //Delete

        vitalsAdapter!!.setOnDeleteClickListener(object :
            VitalsAdapter.OnDeleteClickListener {
            override fun onDeleteClick(templateDetail: TemplateMasterDetail, position: Int) {

                val check= vitalsAdapter?.deleteItem(templateDetail,position)

                if(check!!){
                    vitalsTemplatesAdapter.unselect(templateDetail)

                }
            }
        })


        vitalsAdapter!!.setOnSearchClickListener(object :VitalsAdapter.OnSearchClickListener{
            override fun onSearchClick(autoCompleteTextView1: AppCompatAutoCompleteTextView, position: Int) {

                autoCompleteTextView= autoCompleteTextView1

                searchPosition=position

                viewModel!!.searchList(vitalSearchRetrofitCallback,FaciltyID)

            }
        })
      }

    private fun initFavouritesAdapter() {
        vitalsTemplatesAdapter =
            VitalsTemplateAdapter(requireContext())
        val gridLayoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        binding?.vitalsrecyclerView?.layoutManager = gridLayoutManager
        binding?.vitalsrecyclerView?.adapter = vitalsTemplatesAdapter

        vitalsTemplatesAdapter.setOnItemViewClickListener(object :
            VitalsTemplateAdapter.OnItemViewClickListner {
            override fun onItemClick(responseContent: TemplateDetail?) {

                Log.i("",""+responseContent)
                Log.i("",""+responseContent)
                Log.i("",""+responseContent)
                Log.i("",""+responseContent)
                viewModel?.getTemplateDetails(responseContent?.uuid,FaciltyID,department_uuid,getTemplateRetrofitResponseCallback)
            }


        })

        vitalsTemplatesAdapter.setOnItemDeleteClickListener(object :
            VitalsTemplateAdapter.OnItemDeleteClickListner {
            override fun onItemClick(
                responseContent: TemplateDetail?
            ) {
                Log.i("",""+responseContent)
                customdialog = Dialog(requireContext())
                customdialog!! .requestWindowFeature(Window.FEATURE_NO_TITLE)
                customdialog!! .setCancelable(false)
                customdialog!! .setContentView(R.layout.delete_cutsom_layout)
                val closeImageView = customdialog!! .findViewById(R.id.closeImageView) as ImageView
                closeImageView.setOnClickListener {
                    customdialog!!.dismiss()
                }
                val drugNmae = customdialog!! .findViewById(R.id.addDeleteName) as TextView
                drugNmae.text ="${drugNmae.text.toString()} '"+responseContent?.name+"' Record ?"

                val yesBtn = customdialog!! .findViewById(R.id.yes) as CardView
                val noBtn = customdialog!! .findViewById(R.id.no) as CardView
                yesBtn.setOnClickListener {

                    viewModel!!.deleteTemplate(
                        FaciltyID,
                        responseContent?.uuid!!,
                        deleteRetrofitResponseCallback
                    )


                }
                noBtn.setOnClickListener {
                    customdialog!! .dismiss()
                }
                customdialog!! .show()
            }
        })

        vitalsTemplatesAdapter.setOnItemClickListener(object :
            VitalsTemplateAdapter.OnItemClickListener {
            override fun onItemClick(
                templateDetail: TemplateDetail,
                position: Int,
                selected: Boolean
            ) {
                vitalsTemplatesAdapter.updateSelectStatus(position, selected)
                if (!selected) {
                    vitalsAdapter!!.addFavouritesInRow(templateDetail.template_master_details as ArrayList<TemplateMasterDetail>)
                } else {
                    vitalsAdapter!!.deleteRowItem(templateDetail.template_master_details)
                 }
            }
        })
    }

    val vitalSearchRetrofitCallback=object :RetrofitCallback<VitalSearchListResponseModel>{

        override fun onSuccessfulResponse(response: Response<VitalSearchListResponseModel>) {
            responseContents = Gson().toJson(response.body()?.responseContents)

            Log.i("",""+response.body()?.message)
            Log.i("",""+responseContents)
            Log.i("",""+responseContents)

            vitalsAdapter!!.setAdapter(autoCompleteTextView,response.body()?.responseContents!!.getVitals,searchPosition)

            utils?.showToast(
                    R.color.positiveToast,
                    binding?.mainLayout!!, response.body()?.message!!
            )

        }

        override fun onBadRequest(response: Response<VitalSearchListResponseModel>) {
            val gson = GsonBuilder().create()
            val responseModel: VitalSearchListResponseModel
            try {
                responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        VitalSearchListResponseModel::class.java
                )
                utils?.showToast(
                        R.color.negativeToast,
                        binding?.mainLayout!!, ""
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

    val vitalsSaveRetrofitCallback=object :RetrofitCallback<VitalSaveResponseModel>{
        override fun onSuccessfulResponse(response: Response<VitalSaveResponseModel>) {
            responseContents = Gson().toJson(response.body()?.responseContents)

            Log.i("",""+response.body()?.message)
            Log.i("",""+responseContents)
            Log.i("",""+responseContents)

            utils?.showToast(
                    R.color.positiveToast,
                    binding?.mainLayout!!, response.body()?.message!!
            )

            vitalsAdapter!!.clearAll()

            vitalsAdapter!!.addTemplateRow()

            vitalsTemplatesAdapter.unSelectAll()

        }

        override fun onBadRequest(response: Response<VitalSaveResponseModel>) {
            val gson = GsonBuilder().create()
            val responseModel: VitalSaveResponseModel
            try {
                responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        VitalSaveResponseModel::class.java
                )
                utils?.showToast(
                        R.color.negativeToast,
                        binding?.mainLayout!!, ""
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

    val vitalsTemplateRetrofitCallBack =
        object : RetrofitCallback<VitalsTemplateResponseModel> {

            @SuppressLint("NewApi")
            override fun onSuccessfulResponse(response: Response<VitalsTemplateResponseModel>) {
                responseContents = Gson().toJson(response.body()?.responseContents)
                vitalsTemplatesAdapter.refreshList(response.body()?.responseContents?.templateDetails)

            }
            override fun onBadRequest(response: Response<VitalsTemplateResponseModel>) {
                val gson = GsonBuilder().create()
                val responseModel: VitalsTemplateResponseModel
                try {
                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        VitalsTemplateResponseModel::class.java
                    )
                    utils?.showToast(
                        R.color.negativeToast,
                        binding?.mainLayout!!, ""
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

    val getUmoListRetrofitCallback= object :RetrofitCallback<UomListResponceModel>{
        override fun onSuccessfulResponse(responseBody: Response<UomListResponceModel>?) {

            if(responseBody?.body()?.responseContents?.isNotEmpty()!!) {

                if (responseBody!!.body()!!.responseContents.size != 0) {

                    vitalsAdapter!!.setTypeValue(responseBody!!.body()!!.responseContents)

                    Log.i("", "" + responseBody!!.body())
                }
            }

        }

        override fun onBadRequest(response: Response<UomListResponceModel>) {
            val gson = GsonBuilder().create()
            val responseModel: VitalsTemplateResponseModel
            try {
                responseModel = gson.fromJson(
                    response.errorBody()!!.string(),
                    VitalsTemplateResponseModel::class.java
                )
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!, ""
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
    val getVitalsListRetrofitCallback= object :RetrofitCallback<VitalsListResponseModel>{
        override fun onSuccessfulResponse(responseBody: Response<VitalsListResponseModel>?) {

            if(responseBody?.body()?.responseContents?.getVitals?.isNotEmpty()!!) {

                if (responseBody!!.body()!!.responseContents.getVitals.size != 0) {

                    Log.i("", "" + responseBody!!.body())
                }
            }

        }

        override fun onBadRequest(response: Response<VitalsListResponseModel>) {
            val gson = GsonBuilder().create()
            val responseModel: VitalsListResponseModel
            try {
                responseModel = gson.fromJson(
                    response.errorBody()!!.string(),
                    VitalsListResponseModel::class.java
                )
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!, ""
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

    override fun onTemplateID(position: Int) {

    }


    override fun onRefreshList() {
        viewModel?.getvitalsTemplate(
            FaciltyID,
            department_uuid,
            vitalsTemplateRetrofitCallBack
        )
    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        if (childFragment is ManageVitalTemplateFragment) {
            childFragment.setOnTemplateRefreshListener(this)
        }
    }


    /*
    Get Template
   */
    var getTemplateRetrofitResponseCallback = object : RetrofitCallback<ResponseEditTemplate> {
        override fun onSuccessfulResponse(responseBody: Response<ResponseEditTemplate>?) {

            Log.i("",""+responseBody?.body()?.responseContent)

            val ft = childFragmentManager.beginTransaction()
            val labtemplatedialog = ManageVitalTemplateFragment()
            val bundle = Bundle()
            bundle.putParcelable(AppConstants.RESPONSECONTENT, responseBody?.body()?.responseContent)
            labtemplatedialog.setArguments(bundle)
            labtemplatedialog.show(ft, "Tag")
        }
        override fun onBadRequest(errorBody: Response<ResponseEditTemplate>?) {

        }

        override fun onServerError(response: Response<*>?) {

        }

        override fun onUnAuthorized() {

        }

        override fun onForbidden() {

        }

        override fun onFailure(s: String?) {

        }

        override fun onEverytime() {

        }

    }

    var deleteRetrofitResponseCallback = object : RetrofitCallback<DeleteResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<DeleteResponseModel>?) {
            viewModel!!.getvitalsTemplate(
                FaciltyID,
                department_uuid,
                vitalsTemplateRetrofitCallBack
            )

            customdialog!! .dismiss()

        }

        override fun onBadRequest(errorBody: Response<DeleteResponseModel>?) {

        }

        override fun onServerError(response: Response<*>?) {

        }

        override fun onUnAuthorized() {

        }

        override fun onForbidden() {

        }

        override fun onFailure(s: String?) {

        }

        override fun onEverytime() {

        }

    }

    val getTempleteRetrofitCallBack =
        object : RetrofitCallback<TempleResponseModel> {
            override fun onSuccessfulResponse(response: Response<TempleResponseModel>) {
                var responseContents = Gson().toJson(response.body()?.responseContents)
//                vitalsTemplatesAdapter.refreshList(response?.body()?.responseContents)
            }

            override fun onBadRequest(response: Response<TempleResponseModel>) {
                val gson = GsonBuilder().create()
                val responseModel: TempleResponseModel
                try {
                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        TempleResponseModel::class.java
                    )
                    utils?.showToast(
                        R.color.negativeToast,
                        binding?.mainLayout!!,
                        getString(R.string.bad)
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

    }



