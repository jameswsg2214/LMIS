package com.hmisdoctor.ui.emr_workflow.lab.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.cardview.widget.CardView
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
import com.hmisdoctor.databinding.DialogManageLabFavouriteBinding
import com.hmisdoctor.ui.emr_workflow.delete.model.DeleteResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.*
import com.hmisdoctor.ui.emr_workflow.lab.model.faveditresponse.FavEditResponse
import com.hmisdoctor.ui.emr_workflow.lab.model.favresponse.FavAddListResponse
import com.hmisdoctor.ui.emr_workflow.lab.model.favresponse.ResponseContentsfav
import com.hmisdoctor.ui.emr_workflow.lab.model.request.Detail
import com.hmisdoctor.ui.emr_workflow.lab.model.request.Headers
import com.hmisdoctor.ui.emr_workflow.lab.model.request.RequestLabFavModel
import com.hmisdoctor.ui.emr_workflow.lab.view_model.ManageLabFavourteViewModel
import com.hmisdoctor.ui.emr_workflow.lab.view_model.ManageLabFavourteViewModelFactory
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesResponseModel
import com.hmisdoctor.ui.emr_workflow.prescription.ui.ErrorAPIClass
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import retrofit2.Response
import java.io.IOException


class ManageLabFavouriteFragment : DialogFragment() {
    private var deletefavouriteID: Int? = 0
    private var customdialog: Dialog?=null
    private var ResponseContantSave: ResponseContentsfav? = ResponseContentsfav()
    private var favouriteData: FavouritesModel?=null
    private var is_active: Boolean = true
    lateinit var drugNmae: TextView
    private var Str_auto_id: Int? = 0
    private var content: String? = null
    private var viewModel: ManageLabFavourteViewModel? = null
    var binding: DialogManageLabFavouriteBinding? = null
    var callbackfavourite: OnFavRefreshListener? = null
    var appPreferences: AppPreferences? = null
    private var utils: Utils? = null
    private var facility_UUID: Int? = 0
    private var deparment_UUID: Int? = 0
    private var listDepartmentItems: ArrayList<FavAddResponseContent?> = ArrayList()
    private var listAllAddDepartmentItems: List<FavAddAllDepatResponseContent?> = ArrayList()
    private var favAddResponseMap = mutableMapOf<Int, String>()
    private var autocompleteTestResponse: List<FavAddTestNameResponseContent>? = null
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    var status : Boolean ?=false

    val header: Headers? = Headers()
    val detailsList : ArrayList<Detail> = ArrayList()
    var labManageFavAdapter: LabManageFavAdapter? = null
    val emrFavRequestModel: RequestLabFavModel? = RequestLabFavModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        content = arguments?.getString(AppConstants.ALERTDIALOG)
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
            DataBindingUtil.inflate(inflater, R.layout.dialog_manage_lab_favourite, container, false)
        viewModel = ManageLabFavourteViewModelFactory(
            requireActivity().application
        )
         .create(ManageLabFavourteViewModel::class.java)

        hideSoftKeyboard(view)

        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this

        userDetailsRoomRepository = UserDetailsRoomRepository(requireActivity().application)
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        facility_UUID = appPreferences?.getInt(AppConstants?.FACILITY_UUID)
        deparment_UUID = appPreferences?.getInt(AppConstants?.DEPARTMENT_UUID)

        binding?.viewModel?.getDepartmentList(facility_UUID, FavLabdepartmentRetrofitCallBack)
        viewModel?.getTestName(facility_UUID.toString(), favAddTestNameCallBack)
        binding?.userNameTextView?.setText(userDataStoreBean?.user_name)

        labManageFavAdapter =
            LabManageFavAdapter(
                requireActivity(),
                ArrayList()
            )

        val linearLayoutManager =  LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false)
        binding?.listdatarecycleview?.layoutManager = linearLayoutManager
        binding?.listdatarecycleview?.adapter = labManageFavAdapter
     //   binding?.listDataRecycleview?.adapter = labManageFavAdapter

        val args = arguments
        if (args == null) {
            status = true
          //  Toast.makeText(activity, "arguments is null ", Toast.LENGTH_LONG).show()
        } else {
            // get value from bundle..
            favouriteData = args.getParcelable(AppConstants.RESPONSECONTENT)
            Log.i("",""+favouriteData)
            ResponseContantSave?.favourite_id = favouriteData?.favourite_id
            ResponseContantSave?.favourite_display_order = favouriteData!!.favourite_display_order
            ResponseContantSave?.test_master_name = favouriteData!!.test_master_name
            binding?.buttonstatus?.text = "Update"

            binding?.editTextDisplayOrder?.setText(favouriteData!!.favourite_display_order?.toString())
//            binding?.displayOrderTextView?.text = favouriteData!!.favourite_display_order.toString()
            binding?.autoCompleteTextViewTestName!!.setText(favouriteData?.test_master_name);
            binding?.autoCompleteTextViewTestName!!.isFocusable = false
            labManageFavAdapter?.setFavAddItem(ResponseContantSave)
        }

        labManageFavAdapter?.setOnDeleteClickListener(object : LabManageFavAdapter.OnDeleteClickListener{
            override fun onDeleteClick(favouritesID: Int?, testMasterName: String?) {
                deletefavouriteID = favouritesID
                customdialog = Dialog(requireContext())
                customdialog!! .requestWindowFeature(Window.FEATURE_NO_TITLE)
                customdialog!! .setCancelable(false)
                customdialog!! .setContentView(R.layout.delete_cutsom_layout)
                val closeImageView = customdialog!! .findViewById(R.id.closeImageView) as ImageView

                closeImageView.setOnClickListener {
                    customdialog?.dismiss()
                }
                drugNmae = customdialog!! .findViewById(R.id.addDeleteName) as TextView

                drugNmae.text ="${drugNmae.text.toString()} '"+testMasterName+"' Record ?"
                val yesBtn = customdialog!! .findViewById(R.id.yes) as CardView
                val noBtn = customdialog!! .findViewById(R.id.no) as CardView
                yesBtn.setOnClickListener {
                    viewModel!!.deleteFavourite(
                        facility_UUID,
                        favouritesID,deleteRetrofitResponseCallback
                    )
                    customdialog!!.dismiss()
                }
                noBtn.setOnClickListener {
                    customdialog!! .dismiss()
                }
                customdialog!! .show()

            }

        })

        binding?.switchCheck!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            // do something, the isChecked will be
            // true if the switch is in the On position
            if(isChecked)
            {
                is_active = true
            }
            else{
                is_active = false
            }})

        binding?.closeImageView?.setOnClickListener {
            dialog?.dismiss()
        }
        binding?.spinnerFavLabdepartment?.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            binding?.viewModel?.getAllDepartment(facility_UUID, favLabAddAllDepartmentCallBack)
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
             Str_auto_id = autocompleteTestResponse?.get(position)?.uuid
             Log.i("", "" + autocompleteTestResponse!!.get(position).name)

        }
        binding?.addFav?.setOnClickListener({
            val Str_DisplayOrder = binding?.editTextDisplayOrder?.text.toString()
            if(status as Boolean)
            {
                detailsList.clear()

                header?.is_public = false
                header?.facility_uuid = facility_UUID?.toString()!!
                header?.favourite_type_uuid= AppConstants.FAV_TYPE_ID_LAB
                header?.department_uuid = deparment_UUID?.toString()!!
                header?.user_uuid = userDataStoreBean?.uuid?.toString()!!
                header?.display_order = Str_DisplayOrder
                header?.revision = true
                header?.is_active = is_active

                val details: Detail = Detail()
                details.test_master_uuid=Str_auto_id!!.toInt()
                details.test_master_type_uuid = AppConstants.LAB_TESTMASTER_UUID
                details.item_master_uuid =0
                details.chief_complaint_uuid=0
                details.vital_master_uuid=0
                details.drug_route_uuid=0
                details.drug_frequency_uuid=0
                details.duration=0
                details.duration_period_uuid=0
                details.drug_instruction_uuid=0
                details.display_order=Str_DisplayOrder
                details.revision=true
                details.is_active= is_active
                details.diagnosis_uuid=0
                detailsList.add(details)

                emrFavRequestModel?.headers = this.header!!
                emrFavRequestModel?.details = this.detailsList

                val jsonrequest = Gson().toJson(emrFavRequestModel)

                Log.i("",""+jsonrequest)

                viewModel?.getADDFavourite(facility_UUID, emrFavRequestModel!!, emrposFavtRetrofitCallback)
            }
            else
            {
                //False
                viewModel?.getEditFavourite(facility_UUID,favouriteData?.test_master_name,
                    favouriteData?.favourite_id,deparment_UUID,Str_DisplayOrder,is_active,emrposListDataFavtEditRetrofitCallback)


            }



        })
        binding?.clearCardview?.setOnClickListener({
            binding?.editTextDisplayOrder?.setText("")

        })

        return binding!!.root
    }

    private fun hideSoftKeyboard(view: View?) {
        if (view != null) {
            val inputManager = view.context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    val FavLabdepartmentRetrofitCallBack =
        object : RetrofitCallback<FavAddResponseModel> {
            override fun onSuccessfulResponse(response: Response<FavAddResponseModel>) {
                Log.i("", "" + response.body()?.responseContent);
                Log.i("", "" + response.body()?.responseContent);
                if(response.body()?.responseContent?.uuid!=null)
                {
                    listDepartmentItems.add(response.body()?.responseContent)

                    favAddResponseMap =
                        listDepartmentItems.map { it?.uuid!! to it.name!! }!!.toMap().toMutableMap()

                    try
                    {
                        val adapter =
                            ArrayAdapter<String>(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                favAddResponseMap.values.toMutableList()
                            )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding?.spinnerFavLabdepartment!!.adapter = adapter
                    }catch (e:Exception)
                    {

                    }



                }

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

    val favLabAddAllDepartmentCallBack = object : RetrofitCallback<FavAddAllDepatResponseModel> {
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
            binding?.spinnerFavLabdepartment!!.adapter = adapter

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
        }}

    val favAddTestNameCallBack = object : RetrofitCallback<FavAddTestNameResponse> {
        override fun onSuccessfulResponse(responseBody: Response<FavAddTestNameResponse>?) {
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
        }}

    val emrposFavtRetrofitCallback = object : RetrofitCallback<LabFavManageResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<LabFavManageResponseModel>?) {
            Log.i("", "" + responseBody?.body()?.responseContents?.details?.get(0)?.favourite_master_uuid);
            viewModel?.getAddListFav(facility_UUID,responseBody?.body()?.responseContents?.details?.get(0)?.favourite_master_uuid,emrposListDataFavtRetrofitCallback)

            utils?.showToast(
                R.color.positiveToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }
        override fun onBadRequest(response: Response<LabFavManageResponseModel>?) {
            val gson = GsonBuilder().create()
            val responseModel: LabFavManageResponseModel
            if (response!!.code() == 400) {
                val gson = GsonBuilder().create()
                var mError = ErrorAPIClass()
                try {
                    mError =
                        gson.fromJson(response.errorBody()!!.string(), ErrorAPIClass::class.java)
                    Toast.makeText(
                        context,
                        mError.message,
                        Toast.LENGTH_LONG
                    ).show()
                } catch (e: IOException) { // handle failure to read error
                }
            }
            try {

                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    response?.body()?.message!!
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
    val emrposListDataFavtRetrofitCallback = object : RetrofitCallback<FavAddListResponse> {
        override fun onSuccessfulResponse(responseBody: Response<FavAddListResponse>?) {
            Log.i("", "" + responseBody?.body()?.responseContents);
            labManageFavAdapter?.setFavAddItem(responseBody?.body()?.responseContents)
            utils?.showToast(
                R.color.positiveToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
            callbackfavourite?.onFavID(responseBody?.body()?.responseContents?.favourite_id!!)
        }
        override fun onBadRequest(response: Response<FavAddListResponse>?) {
            val gson = GsonBuilder().create()
            val responseModel: FavAddListResponse
            try {

                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    response?.body()?.message!!
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

    val emrposListDataFavtEditRetrofitCallback = object : RetrofitCallback<FavEditResponse> {
        override fun onSuccessfulResponse(responseBody: Response<FavEditResponse>?) {
            Log.i("", "" + responseBody?.body()?.requestContent);
            ResponseContantSave?.favourite_id = responseBody?.body()?.requestContent?.favourite_id
            ResponseContantSave?.favourite_display_order = responseBody?.body()?.requestContent?.favourite_display_order
            ResponseContantSave?.test_master_name = responseBody?.body()?.requestContent?.testname
            labManageFavAdapter?.clearadapter()

            labManageFavAdapter?.setFavAddItem(ResponseContantSave)
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                responseBody?.body()?.message!!
            )

            callbackfavourite?.onFavID(responseBody?.body()?.requestContent?.favourite_id!!)

        }
        override fun onBadRequest(response: Response<FavEditResponse>?) {
            val gson = GsonBuilder().create()
            val responseModel: FavEditResponse
            try {

                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    response?.body()?.message!!
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

    var deleteRetrofitResponseCallback = object : RetrofitCallback<DeleteResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<DeleteResponseModel>?) {
            labManageFavAdapter?.adapterrefresh(deletefavouriteID)
            callbackfavourite?.onRefreshList()

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

    val getFavouritesRetrofitCallBack =
        object : RetrofitCallback<FavouritesResponseModel> {
            override fun onSuccessfulResponse(response: Response<FavouritesResponseModel>) {
                var responseContents = Gson().toJson(response.body()?.responseContents)
                if (response.body()?.responseContents?.isNotEmpty()!!) {

//                    favouriteData.refreshList(response.body()?.responseContents!!)
                }
            }

            override fun onBadRequest(response: Response<FavouritesResponseModel>) {
                val gson = GsonBuilder().create()
                val responseModel: FavouritesResponseModel
                try {
                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        FavouritesResponseModel::class.java
                    )
                    utils?.showToast(
                        R.color.negativeToast,
                        binding?.mainLayout!!,
                        responseModel.message!!
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

    fun setOnFavRefreshListener(callback: OnFavRefreshListener) {
        this.callbackfavourite = callback
    }
    // This interface can be implemented by the Activity, parent Fragment,
    // or a separate test implementation.
    interface OnFavRefreshListener {
        fun onFavID(position: Int)
        fun onRefreshList()
    }



}