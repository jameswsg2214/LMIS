package com.hmisdoctor.ui.emr_workflow.lab.ui.saveTemplate

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.databinding.DataBindingUtil
import com.hmisdoctor.R
import com.hmisdoctor.config.AppConstants
import android.app.Dialog
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.DialogManageLabSaveTemplateBinding
import com.hmisdoctor.ui.emr_workflow.lab.model.*
import com.hmisdoctor.ui.emr_workflow.lab.model.favresponse.ResponseContentsfav
import com.hmisdoctor.ui.emr_workflow.lab.model.template.gettemplate.ResponseContentLabGetDetails
import com.hmisdoctor.ui.emr_workflow.lab.model.template.request.Detail
import com.hmisdoctor.ui.emr_workflow.lab.model.template.request.Headers
import com.hmisdoctor.ui.emr_workflow.lab.model.template.request.RequestTemplateAddDetails
import com.hmisdoctor.ui.emr_workflow.lab.model.template.response.ReponseTemplateadd
import com.hmisdoctor.ui.emr_workflow.lab.model.updaterequest.NewDetail
import com.hmisdoctor.ui.emr_workflow.lab.model.updaterequest.RemovedDetail
import com.hmisdoctor.ui.emr_workflow.lab.model.updaterequest.UpdateRequestModule
import com.hmisdoctor.ui.emr_workflow.lab.model.updateresponse.UpdateResponse
import com.hmisdoctor.ui.emr_workflow.lab.ui.LabFavouriteFragment
import com.hmisdoctor.ui.emr_workflow.lab.ui.LabTempleteFragment
import com.hmisdoctor.ui.emr_workflow.lab.view_model.ManageLabTemplateViewModel
import com.hmisdoctor.ui.emr_workflow.lab.view_model.ManageLabTemplateViewModelFactory
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import com.hmisdoctor.ui.emr_workflow.model.templete.LabDetail
import com.hmisdoctor.ui.emr_workflow.model.templete.TempleResponseModel
import com.hmisdoctor.ui.emr_workflow.prescription.model.PrescriptionDurationResponseModel
import com.hmisdoctor.ui.emr_workflow.radiology.model.RecyclerDto

import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import retrofit2.Response


class ManageLabSaveTemplateFragment : DialogFragment() {

    private var favouriteData: ArrayList<FavouritesModel>? = ArrayList()
    private var Itemname: String? = ""
    private var Itemdescription : String?=""
    private var arrayItemData: ArrayList<ResponseContentsfav?>? =null
    private var Str_auto_id: Int? = 0
    private var Str_auto_name: String? = ""
    private var Str_auto_code: String? = ""
    private var deparment_UUID: Int? = 0
    private var autocompleteTestResponse: List<FavAddTestNameResponseContent>? = null
    private var content: String? = null
    private var viewModel: ManageLabTemplateViewModel? = null
    var binding: DialogManageLabSaveTemplateBinding? = null
    private var facility_UUID: Int? = 0
    private var favAddResponseMap = mutableMapOf<Int, String>()
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    var dropdownReferenceView: AutoCompleteTextView? = null
    var mCallback: LabChiefComplaintListener? =null
    val detailsList: ArrayList<Detail> = ArrayList()
    private var listDepartmentItems: ArrayList<FavAddResponseContent?> = ArrayList()
    private var listAllAddDepartmentItems: List<FavAddAllDepatResponseContent?> = ArrayList()
    private var mAdapter: LabManageSaveTemplateAdapter? = null
    private val favList: MutableList<RecyclerDto> = java.util.ArrayList()
    var appPreferences: AppPreferences? = null
    private var utils: Utils? = null
    val header: Headers? = Headers()
    var RequestTemplateAddDetails : RequestTemplateAddDetails= RequestTemplateAddDetails()
    var callbacktemplate: OnSaveTemplateRefreshListener? = null
    var status : Boolean ?=false
    var rasponsecontentLabGetTemplateDetails : ResponseContentLabGetDetails = ResponseContentLabGetDetails()
    var arraylistresponse : ArrayList<ResponseContentsfav?> = ArrayList()
    var UpdateRequestModule : UpdateRequestModule = UpdateRequestModule()

    val removeList:  ArrayList<RemovedDetail> = ArrayList()

    private var customdialog: Dialog?=null

    var newDetail : NewDetail = NewDetail()
    var newDetailList : ArrayList<NewDetail> = ArrayList()

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
        binding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_manage_lab_save_template, container, false)
        viewModel = ManageLabTemplateViewModelFactory(
            requireActivity().application
        )
            .create(ManageLabTemplateViewModel::class.java)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this

//        val layoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(context)
//        binding?.labManageTemplateRecyclerView!!.layoutManager = layoutmanager
        mAdapter = LabManageSaveTemplateAdapter(requireContext(), ArrayList())
        binding?.labManageTemplateRecyclerView!!.adapter = mAdapter


        userDetailsRoomRepository = UserDetailsRoomRepository(requireActivity().application)
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        facility_UUID = appPreferences?.getInt(AppConstants?.FACILITY_UUID)
        deparment_UUID = appPreferences?.getInt(AppConstants?.DEPARTMENT_UUID)

        binding?.UserName?.setText(userDataStoreBean?.user_name)

//        departmentDropDownAdapter = FavouriteDropDownAdapter(context!!,ArrayList())
        binding?.viewModel?.getDepartmentList(facility_UUID, FavdepartmentRetrofitCallBack)


        binding?.closeImageView?.setOnClickListener {
            dialog?.dismiss()
        }
        binding?.spinnerdepartment?.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            binding?.viewModel?.getAllDepartment(facility_UUID, favAddAllDepartmentCallBack)
            false
        })
        val args = arguments
        if (args == null) {
            status = true
            //  Toast.makeText(activity, "arguments is null ", Toast.LENGTH_LONG).show()
        } else {
            // get value from bundle..
      //      rasponsecontentLabGetTemplateDetails = args.getParcelable(AppConstants.RESPONSECONTENT)!!
     //       Itemname = rasponsecontentLabGetTemplateDetails.temp_details?.template_name
      //      Itemdescription = rasponsecontentLabGetTemplateDetails.temp_details?.template_description


            binding?.UserName?.setText(userDataStoreBean?.user_name)

    //        binding?.editName?.setText(Itemname)
    //        binding?.editDescription?.setText(Itemdescription)
   //         binding?.displayorder?.setText(rasponsecontentLabGetTemplateDetails.temp_details?.template_displayorder?.toString())
            arraylistresponse.clear()

         /*   for(i in rasponsecontentLabGetTemplateDetails.lab_details?.indices!!)
            {
                val ResponseContantSave: ResponseContentsfav? = ResponseContentsfav()
                ResponseContantSave?.template_details_uuid = rasponsecontentLabGetTemplateDetails?.lab_details?.get(i)?.template_details_uuid
                ResponseContantSave?.template_id = rasponsecontentLabGetTemplateDetails?.temp_details?.template_id!!
                ResponseContantSave?.test_master_name = rasponsecontentLabGetTemplateDetails.lab_details?.get(i)?.lab_name
                ResponseContantSave?.test_master_id = rasponsecontentLabGetTemplateDetails.lab_details?.get(i)?.lab_test_uuid
                ResponseContantSave?.test_master_code = rasponsecontentLabGetTemplateDetails.lab_details?.get(i)?.lab_code
                arraylistresponse.add(ResponseContantSave)

            }
            mAdapter?.setFavAddItem(arraylistresponse)
*/
            favouriteData = args.getParcelableArrayList<FavouritesModel>(AppConstants.RESPONSECONTENT)

            for(i in favouriteData!!.indices){

                val ResponseContantSave: ResponseContentsfav? = ResponseContentsfav()
                ResponseContantSave?.test_master_name = favouriteData!![i].itemAppendString
                ResponseContantSave?.test_master_id = favouriteData!![i].test_master_id
                ResponseContantSave?.test_master_code = favouriteData!![i]?.test_master_code.toString()
                if (i != favouriteData?.size!!-1) {
                    arraylistresponse.add(ResponseContantSave)

                }
                else
                {

                }

            }
            mAdapter?.setFavAddItem(arraylistresponse)
        }
        mAdapter?.setOnDeleteClickListener(object : LabManageSaveTemplateAdapter.OnDeleteClickListener {
            override fun onDeleteClick(responseData : ResponseContentsfav?,position: Int) {

                customdialog = Dialog(requireContext())
                customdialog!! .requestWindowFeature(Window.FEATURE_NO_TITLE)
                customdialog!! .setCancelable(false)
                customdialog!! .setContentView(R.layout.delete_cutsom_layout)
                val closeImageView = customdialog!! .findViewById(R.id.closeImageView) as ImageView

                closeImageView.setOnClickListener {
                    customdialog?.dismiss()
                }
                val drugNmae = customdialog!! .findViewById(R.id.addDeleteName) as TextView
                drugNmae.text ="${drugNmae.text.toString()} '"+responseData?.test_master_name+"' Record ?"
                val yesBtn = customdialog!! .findViewById(R.id.yes) as CardView
                val noBtn = customdialog!! .findViewById(R.id.no) as CardView
                yesBtn.setOnClickListener {

                    mAdapter?.delete(position)

                    customdialog!!.dismiss()


                }
                noBtn.setOnClickListener {
                    customdialog!! .dismiss()
                }
                customdialog!! .show()


            }

        })



        binding?.save?.setOnClickListener {
      /*      if(status as Boolean)
            {*/
                /*
                Add Details
                 */



                val displayordervalue = binding?.displayorder?.text.toString()
                arrayItemData = mAdapter?.getItems()
                detailsList.clear()
                if (arrayItemData?.size!! > 0) {
                    for (i in arrayItemData?.indices!!) {
                        val details: Detail = Detail()
                        details.chief_complaint_uuid = 0
                        details.vital_master_uuid = 0
                        details.test_master_uuid = arrayItemData?.get(i)?.test_master_id
                        details.item_master_uuid = 0
                        details.drug_route_uuid = 0
                        details.drug_frequency_uuid = 0
                        details.duration = 0
                        details.duration_period_uuid = 0
                        details.drug_instruction_uuid = 0
                        details.revision = true
                        details.is_active = true
                        detailsList.add(details)
                    }

                    header?.name = binding?.editName?.text.toString()
                    header?.description =  binding?.editDescription?.text.toString()
                    header?.template_type_uuid = AppConstants.FAV_TYPE_ID_LAB
                    header?.diagnosis_uuid = 0
                    header?.is_public = "false"
                    header?.facility_uuid = facility_UUID?.toString()
                    header?.department_uuid = deparment_UUID?.toString()
                    header?.display_order = binding?.displayorder?.text?.toString()
                    header?.revision = true
                    header?.is_active = true

                    RequestTemplateAddDetails.headers = header!!
                    RequestTemplateAddDetails.details = this.detailsList

                    val request = Gson().toJson(RequestTemplateAddDetails)

                    Log.i("", "" + request)
                    viewModel?.labTemplateDetails(
                        facility_UUID,
                        RequestTemplateAddDetails!!,
                        emrlabtemplatepostRetrofitCallback
                    )
                }

        }

        binding?.addDetails?.setOnClickListener {

            Itemname = binding?.editName?.text.toString()
            Itemdescription = binding?.editDescription?.text.toString()


            if(!binding?.editName?.text.toString().isNullOrEmpty()) {


                if(!binding?.displayorder?.text.toString().isNullOrEmpty()) {


                    if(Str_auto_id!=0) {

                        val testmasterId = Str_auto_id

                        val responseContentsfav = ResponseContentsfav()

                        responseContentsfav.test_master_name = Str_auto_name
                        responseContentsfav.test_master_id = testmasterId
                        responseContentsfav.test_master_code = Str_auto_code

                        binding?.autoCompleteTextViewTestName?.setText("")

                        mAdapter?.setAddItem(responseContentsfav)

                        Str_auto_id=0
                    }

                    else{
                        binding?.autoCompleteTextViewTestName?.error="Test Name must be valid"

                    }
                }

                else{
                    binding?.displayorder?.error="Display order can't be empty"

                }
            }
            else{

                binding?.editName?.error="Template name must be valid"


            }


        }


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


        return binding?.root
    }
    val emrlabtemplatepostRetrofitCallback = object : RetrofitCallback<ReponseTemplateadd> {
        override fun onSuccessfulResponse(responseBody: Response<ReponseTemplateadd>?) {
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                responseBody?.body()?.message!!
            )
            mAdapter?.cleardata()


            dialog!!.dismiss()

            mCallback?.sendDataChiefComplaint()


        //    viewModel!!.getTemplete(getTempleteRetrofitCallBack)
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

/*
Update Response
 */


   /* val UpdateemrlabtemplatepostRetrofitCallback = object : RetrofitCallback<UpdateResponse> {
        override fun onSuccessfulResponse(responseBody: Response<UpdateResponse>?) {
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                responseBody?.body()?.message!!
            )

            Log.i("",""+responseBody?.body()?.responseContent)
            Log.i("",""+responseBody?.body()?.responseContent)
            Log.i("",""+responseBody?.body()?.responseContent)
            Log.i("",""+responseBody?.body()?.responseContent)
            mAdapter?.cleardata()



        }
        override fun onBadRequest(response: Response<UpdateResponse>) {
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

*/
    val FavdepartmentRetrofitCallBack =
        object : RetrofitCallback<FavAddResponseModel> {
            override fun onSuccessfulResponse(response: Response<FavAddResponseModel>) {
                Log.i("", "" + response.body()?.responseContent);
                Log.i("", "" + response.body()?.responseContent);
                viewModel?.getTestName(facility_UUID.toString(), favAddTestNameCallBack)
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
                binding?.spinnerdepartment!!.adapter = adapter

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

    val favAddAllDepartmentCallBack = object : RetrofitCallback<FavAddAllDepatResponseModel> {
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
            binding?.spinnerdepartment!!.adapter = adapter

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
/*    val getTempleteRetrofitCallBack =
        object : RetrofitCallback<TempleResponseModel> {
            override fun onSuccessfulResponse(response: Response<TempleResponseModel>) {
                var responseContents = Gson().toJson(response.body()?.responseContents)




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
        }*/

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(activity!!, theme) {
            override fun onBackPressed() {
            }
        }
    }

    fun setOnTemplateRefreshListener(callback: OnSaveTemplateRefreshListener) {
        this.callbacktemplate = callback
    }
    // This interface can be implemented by the Activity, parent Fragment,
    // or a separate test implementation.
    interface OnSaveTemplateRefreshListener {
        fun onTemplateID(position: Int)
        fun onTemplateRefreshList()
    }
    //defining Interface
    fun setOnClickedListener(callback: LabChiefComplaintListener) {
        this.mCallback = callback
    }
    interface LabChiefComplaintListener {
        fun sendDataChiefComplaint()
    }

}


