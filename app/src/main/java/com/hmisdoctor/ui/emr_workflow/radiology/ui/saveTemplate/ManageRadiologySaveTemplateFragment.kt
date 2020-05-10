package com.hmisdoctor.ui.emr_workflow.radiology.ui.saveTemplate

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.DialogManageRadiologyTemplateBinding
import com.hmisdoctor.ui.emr_workflow.lab.model.*
import com.hmisdoctor.ui.emr_workflow.lab.model.favresponse.ResponseContentsfav
import com.hmisdoctor.ui.emr_workflow.lab.model.template.gettemplate.ResponseContentLabGetDetails
import com.hmisdoctor.ui.emr_workflow.lab.model.template.request.Detail
import com.hmisdoctor.ui.emr_workflow.lab.model.template.request.Headers
import com.hmisdoctor.ui.emr_workflow.lab.model.template.request.RequestTemplateAddDetails
import com.hmisdoctor.ui.emr_workflow.lab.model.template.response.ReponseTemplateadd
import com.hmisdoctor.ui.emr_workflow.lab.ui.ManageLabTemplateFragment
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import com.hmisdoctor.ui.emr_workflow.model.templete.TempleResponseModel
import com.hmisdoctor.ui.emr_workflow.prescription.model.PrescriptionDurationResponseModel
import com.hmisdoctor.ui.emr_workflow.radiology.model.ManageTemplateModel
import com.hmisdoctor.ui.emr_workflow.radiology.ui.RadiologyManageTemplateAdapter


import com.hmisdoctor.ui.emr_workflow.radiology.view_model.ManageRadiologyTemplateViewModel
import com.hmisdoctor.ui.emr_workflow.radiology.view_model.ManageRadiologyTemplateViewModelFactory
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import retrofit2.Response

class ManageRadiologySaveTemplateFragment : DialogFragment() {

    private var Itemdescription: String?=null
    private var Itemname: String?=""
    private var deparment_UUID: Int? = 0
    private var content: String? = null
    private var autocompleteTestResponse: List<FavAddTestNameResponseContent>? = null
    private var viewModel: ManageRadiologyTemplateViewModel? = null
    var binding: DialogManageRadiologyTemplateBinding? = null
    private var facility_UUID: Int? = 0
    private var favAddResponseMap = mutableMapOf<Int, String>()
    var dropdownReferenceView: AutoCompleteTextView? = null
    private var listDepartmentItems: ArrayList<FavAddResponseContent?> = ArrayList()
    private var listAllAddDepartmentItems: List<FavAddAllDepatResponseContent?> = ArrayList()
    private var mAdapter: RadiologyManageTemplateAdapter? = null
    private val favList: MutableList<ManageTemplateModel> = java.util.ArrayList()
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    private var arrayItemData: ArrayList<ResponseContentsfav?>? =null
    private var Str_auto_id: Int? = 0
    private var Str_auto_name: String? = ""
    private var Str_auto_code: String? = ""
    val detailsList: ArrayList<Detail> = ArrayList()
    var appPreferences: AppPreferences? = null
    private var utils: Utils? = null
    val header: Headers? = Headers()
    var callbacktemplate: OnTemplateRefreshListener? = null
    var RequestTemplateAddDetails : RequestTemplateAddDetails = RequestTemplateAddDetails()
    var arraylistresponse : ArrayList<ResponseContentsfav?> = ArrayList()
    var rasponsecontentLabGetTemplateDetails : ResponseContentLabGetDetails = ResponseContentLabGetDetails()


    var responsecontentLabGetTemplateDetails : ArrayList<FavouritesModel> = ArrayList()

    var status : Boolean ?=false
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
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_manage_radiology_template, container, false)
        viewModel = ManageRadiologyTemplateViewModelFactory(
            requireActivity().application
        ).create(ManageRadiologyTemplateViewModel::class.java)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this
        val layoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        binding?.templateRadiologyRecyclerView!!.layoutManager = layoutmanager
        mAdapter = RadiologyManageTemplateAdapter(requireContext(), ArrayList())
        binding?.templateRadiologyRecyclerView!!.adapter = mAdapter

        userDetailsRoomRepository = UserDetailsRoomRepository(requireActivity().application)
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        facility_UUID = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        deparment_UUID = appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)
        binding?.UserName?.setText(userDataStoreBean?.user_name)

        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        facility_UUID = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        deparment_UUID = appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)

        binding?.viewModel?.getRadiologyDepartmentList(
            facility_UUID,
            templateRadiodepartmentRetrofitCallBack
        )

        binding?.addDetails?.setOnClickListener {

            Itemname = binding?.editName?.text.toString()
            Itemdescription = binding?.editDescription?.text.toString()

            val testmasterId = Str_auto_id
            val responseContentsfav = ResponseContentsfav()

            responseContentsfav.test_master_name = Str_auto_name
            responseContentsfav.test_master_id = testmasterId
            responseContentsfav.test_master_code = Str_auto_code
            binding?.autoCompleteTextViewTestName?.setText("")
            arraylistresponse.add(responseContentsfav)

            mAdapter?.setFavAddItem(arraylistresponse)
        }

        mAdapter!!.setOnItemClickListener(object :RadiologyManageTemplateAdapter.OnItemClickListener{
            override fun onItemClick(responseContent: ResponseContentsfav?, position: Int) {

                binding!!.displayorder!!.setText(responseContent!!.favourite_display_order!!.toString())

                binding!!.autoCompleteTextViewTestName.setText(responseContent!!.test_master_name!!.toString())

            }
        })


        val args = arguments
        if (args == null) {
            status = true
            //  Toast.makeText(activity, "arguments is null ", Toast.LENGTH_LONG).show()
        } else {

            val favouriteData = args.getParcelableArrayList<FavouritesModel>(AppConstants.RESPONSECONTENT)

            val dataSize=favouriteData!!.size
            for(i in 0..dataSize-2){

                val ResponseContantSave: ResponseContentsfav? = ResponseContentsfav()

                ResponseContantSave?.test_master_name = favouriteData[i].itemAppendString
                ResponseContantSave?.test_master_id = favouriteData[i].test_master_id
                ResponseContantSave?.test_master_code = favouriteData[i]?.test_master_code.toString()
                arraylistresponse.add(ResponseContantSave)

            }


            mAdapter?.setFavAddItem(arraylistresponse)


        /*
            Itemname = rasponsecontentLabGetTemplateDetails.temp_details?.template_name
            Itemdescription = rasponsecontentLabGetTemplateDetails.temp_details?.template_description
            binding?.UserName?.setText(userDataStoreBean?.user_name)
            binding?.editName?.setText(Itemname)
            binding?.editDescription?.setText(Itemdescription)
            binding?.displayorder?.setText(rasponsecontentLabGetTemplateDetails.temp_details?.template_displayorder?.toString())
            arraylistresponse.clear()
            for(i in rasponsecontentLabGetTemplateDetails.lab_details?.indices!!)
            {
                val ResponseContantSave: ResponseContentsfav? = ResponseContentsfav()

                ResponseContantSave?.test_master_name = rasponsecontentLabGetTemplateDetails.lab_details?.get(i)?.lab_name
                ResponseContantSave?.test_master_id = rasponsecontentLabGetTemplateDetails.lab_details?.get(i)?.lab_test_uuid
                ResponseContantSave?.test_master_code = rasponsecontentLabGetTemplateDetails.lab_details?.get(i)?.lab_code
                arraylistresponse.add(ResponseContantSave)

            }
            mAdapter?.setFavAddItem(arraylistresponse)*/

        }

        binding?.save?.setOnClickListener {
            val displayordervalue = binding?.displayorder?.text.toString()
            arrayItemData = mAdapter?.getItems()
            detailsList.clear()
            if (arrayItemData?.size!! > 0) {
                for (i in arrayItemData?.indices!!) {
                    val details: Detail = Detail()
                    details.chief_complaint_uuid=0
                    details.vital_master_uuid=0
                    details.test_master_uuid=arrayItemData?.get(i)?.test_master_id
                    details.item_master_uuid = 0
                    details.drug_route_uuid=0
                    details.drug_frequency_uuid=0
                    details.duration=0
                    details.duration_period_uuid=0
                    details.drug_instruction_uuid=0
                    details.revision=true
                    details.is_active=true
                    detailsList.add(details)
                }
                header?.name = Itemname.toString()
                header?.description = Itemdescription
                header?.template_type_uuid = AppConstants.FAV_TYPE_ID_RADIOLOGY
                header?.diagnosis_uuid =0
                header?.is_public="false"
                header?.facility_uuid = facility_UUID?.toString()
                header?.department_uuid = deparment_UUID?.toString()
                header?.display_order = displayordervalue
                header?.revision = true
                header?.is_active = true

                RequestTemplateAddDetails.headers = this.header!!
                RequestTemplateAddDetails.details = this.detailsList

                val request =  Gson().toJson(RequestTemplateAddDetails)
                Log.i("",""+request)
                viewModel?.radiologyTemplateDetails(facility_UUID, RequestTemplateAddDetails!!, emrradilogytemplatepostRetrofitCallback)
            }
        }



        binding?.closeImageView?.setOnClickListener {
            dialog?.dismiss()
        }
        binding?.spinnerRadiologydepartment?.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            binding?.viewModel?.getRadiologyAllDepartment(
                facility_UUID,
                tempRadiologyAddAllDepartmentCallBack
            )
            false
        })

        binding?.autoCompleteTextViewTestName?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun afterTextChanged(s: Editable) {
                if (s.length > 2) {

                    viewModel?.getTestName(s.toString(), favAddTestNameCallBack)

                }
            }
        })
        binding?.autoCompleteTextViewTestName!!.setOnItemClickListener { parent, _, position, id ->
            binding?.autoCompleteTextViewTestName?.setText(autocompleteTestResponse?.get(position)?.name)

            Log.i("", "" + autocompleteTestResponse!!.get(position).name)
            Str_auto_code = autocompleteTestResponse?.get(position)?.code
            Str_auto_name = autocompleteTestResponse?.get(position)?.name

            Str_auto_id = autocompleteTestResponse?.get(position)?.uuid


        }



        return binding!!.root
    }

/*
Response
 */

    val emrradilogytemplatepostRetrofitCallback = object : RetrofitCallback<ReponseTemplateadd> {
        override fun onSuccessfulResponse(responseBody: Response<ReponseTemplateadd>?) {
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                responseBody?.body()?.message!!
            )
            mAdapter?.cleardata()
            viewModel!!.getTemplete(getTempleteRetrofitCallBack)
        }
        override fun onBadRequest(response: Response<ReponseTemplateadd>) {
            val gson = GsonBuilder().create()
            val responseModel: PrescriptionDurationResponseModel
            try {
                responseModel = gson.fromJson(
                    response.errorBody()!!.string(),
                    PrescriptionDurationResponseModel::class.java
                )
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    getString(R.string.something_went_wrong)
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




    val templateRadiodepartmentRetrofitCallBack =
        object : RetrofitCallback<FavAddResponseModel> {
            override fun onSuccessfulResponse(response: Response<FavAddResponseModel>) {
                Log.i("", "" + response.body()?.responseContent);
                Log.i("", "" + response.body()?.responseContent);
                listDepartmentItems.add(response.body()?.responseContent)
                favAddResponseMap =
                    listDepartmentItems.map { it?.uuid!! to it.name!! }!!.toMap().toMutableMap()
                val adapter =
                    ArrayAdapter<String>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        favAddResponseMap.values.toMutableList()
                    )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding?.spinnerRadiologydepartment!!.adapter = adapter
            }

            override fun onBadRequest(response: Response<FavAddResponseModel>) {
                val gson = GsonBuilder().create()
                val responseModel: FavAddResponseModel
                try {
                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        FavAddResponseModel::class.java
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

    val tempRadiologyAddAllDepartmentCallBack =
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
                binding?.spinnerRadiologydepartment!!.adapter = adapter
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
    val getTempleteRetrofitCallBack =
        object : RetrofitCallback<TempleResponseModel> {
            override fun onSuccessfulResponse(response: Response<TempleResponseModel>) {
                var responseContents = Gson().toJson(response.body()?.responseContents)
                callbacktemplate?.onRefreshList()

                /*     if (response.body()?.responseContents?.templates_lab_list?.isNotEmpty()!!) {
                         templeteAdapter.refreshList(response.body()?.responseContents?.templates_lab_list)
                     }*/
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
    val favAddTestNameCallBack = object : RetrofitCallback<FavAddTestNameResponse> {
        override fun onSuccessfulResponse(responseBody: Response<FavAddTestNameResponse>?) {
            Log.i("", "" + responseBody?.body()?.responseContents);

            autocompleteTestResponse = responseBody?.body()?.responseContents
            val responseContentAdapter = FavTestNameSearchResultAdapter(
                context!!,
                R.layout.row_chief_complaint_search_result,
                responseBody?.body()?.responseContents!!
            )
            binding?.autoCompleteTextViewTestName?.threshold = 1
            binding?.autoCompleteTextViewTestName?.setAdapter(responseContentAdapter)

        }

        override fun onBadRequest(errorBody: Response<FavAddTestNameResponse>?) {
            val gson = GsonBuilder().create()
            val responseModel: FavAddTestNameResponse
            try {
                responseModel = gson.fromJson(
                    errorBody?.errorBody()!!.string(),
                    FavAddTestNameResponse::class.java
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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(activity!!, theme) {
            override fun onBackPressed() {
            }
        }
    }

    fun setOnTemplateRefreshListener(callback: OnTemplateRefreshListener) {
        this.callbacktemplate = callback
    }
    // This interface can be implemented by the Activity, parent Fragment,
    // or a separate test implementation.
    interface OnTemplateRefreshListener {
        fun onRefreshList()
    }


}