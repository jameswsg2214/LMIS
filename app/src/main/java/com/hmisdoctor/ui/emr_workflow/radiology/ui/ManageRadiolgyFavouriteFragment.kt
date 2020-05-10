package com.hmisdoctor.ui.emr_workflow.radiology.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
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
import com.hmisdoctor.databinding.DialogManageRadiologyFavouritesBinding
import com.hmisdoctor.ui.emr_workflow.delete.model.DeleteResponseModel


import com.hmisdoctor.ui.emr_workflow.lab.model.*
import com.hmisdoctor.ui.emr_workflow.lab.model.faveditresponse.FavEditResponse
import com.hmisdoctor.ui.emr_workflow.lab.model.favresponse.FavAddListResponse
import com.hmisdoctor.ui.emr_workflow.lab.model.favresponse.ResponseContentsfav
import com.hmisdoctor.ui.emr_workflow.lab.model.request.Detail
import com.hmisdoctor.ui.emr_workflow.lab.model.request.Headers
import com.hmisdoctor.ui.emr_workflow.lab.model.request.RequestLabFavModel
import com.hmisdoctor.ui.emr_workflow.lab.ui.LabManageFavAdapter
import com.hmisdoctor.ui.emr_workflow.lab.ui.ManageLabFavouriteFragment
import com.hmisdoctor.ui.emr_workflow.lab.ui.RadiologyManageFavAdapter
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import com.hmisdoctor.ui.emr_workflow.radiology.model.RecyclerDto
import com.hmisdoctor.ui.emr_workflow.radiology.view_model.ManageRadiologyFavourteViewModel
import com.hmisdoctor.ui.emr_workflow.radiology.view_model.ManageRadiologyFavourteViewModelFactory
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import retrofit2.Response

class ManageRadiolgyFavouriteFragment : DialogFragment() {
    private var Str_auto_id: Int? =0
    private var content: String? = null
    private var deletefavouriteID: Int? = 0
    lateinit var drugNmae: TextView
    private var viewModel: ManageRadiologyFavourteViewModel? = null
    var binding: DialogManageRadiologyFavouritesBinding? = null
    var appPreferences: AppPreferences? = null
    private var ResponseContantSave: ResponseContentsfav? = ResponseContentsfav()
    private var utils: Utils? = null

    private var favouriteData: FavouritesModel?=null
    private var facility_UUID: Int? = 0
    private var customdialog: Dialog?=null
    private var deparment_UUID: Int? = 0
    private var listDepartmentItems: ArrayList<FavAddResponseContent?> = ArrayList()
    private var listAllAddDepartmentItems: List<FavAddAllDepatResponseContent?> = ArrayList()
    private var favAddResponseMap = mutableMapOf<Int, String>()
    private var autocompleteTestResponse: List<FavAddTestNameResponseContent>? = null
    private var mAdapter: RadiologyManageFavAdapter? = null
    private val favList: MutableList<RecyclerDto> = java.util.ArrayList()
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    var status : Boolean ?=false
    val header: Headers? = Headers()
    val detailsList : ArrayList<Detail> = ArrayList()
    private var is_active: Boolean = true
    var callbackRadiology: OnFavRadiologyRefreshListener? = null
    val emrFavRequestModel: RequestLabFavModel? = RequestLabFavModel()

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
            DataBindingUtil.inflate(
                inflater,
                R.layout.dialog_manage_radiology_favourites,
                container,
                false
            )
        viewModel = ManageRadiologyFavourteViewModelFactory(
            requireActivity().application
        )
            .create(ManageRadiologyFavourteViewModel::class.java)

        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this
        userDetailsRoomRepository = UserDetailsRoomRepository(requireActivity().application)
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        facility_UUID = appPreferences?.getInt(AppConstants?.FACILITY_UUID)
        deparment_UUID = appPreferences?.getInt(AppConstants?.DEPARTMENT_UUID)
        binding?.username?.setText(userDataStoreBean?.user_name)
        binding?.viewModel?.getDepartmentList(facility_UUID, FavLabdepartmentRetrofitCallBack)

        mAdapter =
            RadiologyManageFavAdapter(
                requireActivity(),
                ArrayList()
            )

        val linearLayoutManager =  LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false)
        binding?.favManageRecyclerview?.layoutManager = linearLayoutManager
        binding?.favManageRecyclerview?.adapter = mAdapter

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

            binding?.editdisplayorder?.setText(favouriteData!!.favourite_display_order?.toString())
//            binding?.displayOrderTextView?.text = favouriteData!!.favourite_display_order.toString()
            binding?.autoCompleteTextViewTestName!!.setText(favouriteData?.test_master_name);
            binding?.autoCompleteTextViewTestName!!.isFocusable = false
            mAdapter?.setFavAddItem(ResponseContantSave)
        }
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
        binding?.clearCardview?.setOnClickListener({
            binding?.editdisplayorder?.setText("")
            binding?.autoCompleteTextViewTestName?.setText("")
        })
        binding?.spinnerFavRadiodepartment?.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
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

        mAdapter?.setOnDeleteClickListener(object : RadiologyManageFavAdapter.OnDeleteClickListener{
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
        binding?.autoCompleteTextViewTestName!!.setOnItemClickListener { parent, _, position, id ->
            binding?.autoCompleteTextViewTestName?.setText(autocompleteTestResponse?.get(position)?.name)
            Str_auto_id = autocompleteTestResponse?.get(position)?.uuid
        }
        binding?.addbutton?.setOnClickListener({
            val Str_DisplayOrder = binding?.editdisplayorder?.text.toString()
            if(status as Boolean)
            {
                detailsList.clear()
                header?.is_public = false
                header?.facility_uuid = facility_UUID?.toString()!!
                header?.favourite_type_uuid= AppConstants.FAV_TYPE_ID_RADIOLOGY
                header?.department_uuid = deparment_UUID?.toString()!!
                header?.user_uuid = userDataStoreBean?.uuid?.toString()!!
                header?.display_order = Str_DisplayOrder
                header?.revision = true
                header?.is_active = is_active

                val details: Detail = Detail()
                details.test_master_uuid= Str_auto_id!!.toInt()
                details.test_master_type_uuid = AppConstants.LAB_MASTER_TYPE_ID_RADIOLOGY
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
                viewModel?.getADDFavourite(facility_UUID, emrFavRequestModel!!, emrposFavtRetrofitCallback)
            }
            else
            {
                 //False
                  viewModel?.getEditFavourite(facility_UUID,favouriteData!!.test_master_name,
                    favouriteData?.favourite_id,deparment_UUID,Str_DisplayOrder,is_active,emrposListDataFavtEditRetrofitCallback)

            }
        })



        return binding!!.root
    }

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
            mAdapter?.setFavAddItem(responseBody?.body()?.responseContents)
            utils?.showToast(
                R.color.positiveToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
            callbackRadiology?.onFavRadiologyID(responseBody?.body()?.responseContents?.favourite_id!!)
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
    val FavLabdepartmentRetrofitCallBack =
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
                binding?.spinnerFavRadiodepartment!!.adapter = adapter

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
            binding?.spinnerFavRadiodepartment!!.adapter = adapter

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
    var deleteRetrofitResponseCallback = object : RetrofitCallback<DeleteResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<DeleteResponseModel>?) {
            mAdapter?.adapterrefresh(deletefavouriteID)
            callbackRadiology?.onRefreshList()

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

    val emrposListDataFavtEditRetrofitCallback = object : RetrofitCallback<FavEditResponse> {
        override fun onSuccessfulResponse(responseBody: Response<FavEditResponse>?) {
            Log.i("", "" + responseBody?.body()?.requestContent);
            ResponseContantSave?.favourite_id = responseBody?.body()?.requestContent?.favourite_id
            ResponseContantSave?.favourite_display_order = responseBody?.body()?.requestContent?.favourite_display_order
            ResponseContantSave?.test_master_name = responseBody?.body()?.requestContent?.testname
            mAdapter?.clearadapter()

            mAdapter?.setFavAddItem(ResponseContantSave)
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                responseBody?.body()?.message!!
            )

            callbackRadiology?.onFavRadiologyID(responseBody?.body()?.requestContent?.favourite_id!!)

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

//    var deleteRetrofitResponseCallback = object : RetrofitCallback<DeleteResponseModel> {
//        override fun onSuccessfulResponse(responseBody: Response<DeleteResponseModel>?) {
//            labManageFavAdapter?.adapterrefresh(deletefavouriteID)
//            callbackfavourite?.onRefreshList()
//
//        }
//
//        override fun onBadRequest(errorBody: Response<DeleteResponseModel>?) {
//
//        }
//
//        override fun onServerError(response: Response<*>?) {
//
//        }
//
//        override fun onUnAuthorized() {
//
//        }
//
//        override fun onForbidden() {
//
//        }
//
//        override fun onFailure(s: String?) {
//
//        }
//
//        override fun onEverytime() {
//
//        }
//
//    }

    fun setOnFavRefreshListener(callback: OnFavRadiologyRefreshListener) {
        this.callbackRadiology = callback
    }
    // This interface can be implemented by the Activity, parent Fragment,
    // or a separate test implementation.
    interface OnFavRadiologyRefreshListener {
        fun onFavRadiologyID(position: Int)
        fun onRefreshList()
    }



}