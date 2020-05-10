package com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.favouriteEdit

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.DialogManageFavouritePrescriptionBinding
import com.hmisdoctor.ui.emr_workflow.delete.model.DeleteResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddResponseContent
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.favresponse.FavAddListResponse
import com.hmisdoctor.ui.emr_workflow.lab.model.favresponse.ResponseContentsfav
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import com.hmisdoctor.ui.emr_workflow.prescription.model.*
import com.hmisdoctor.ui.emr_workflow.prescription.ui.ErrorAPIClass
import com.hmisdoctor.ui.emr_workflow.prescription.ui.PrescriptionSearchAdapter
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.favouriteEdit.requestparamter.Detail
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.favouriteEdit.requestparamter.Headers
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.favouriteEdit.requestparamter.RequestPrecEditModule
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.favouriteEdit.updaterequest.UpdatePrescriptionRequest
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.favouriteEdit.updateresponse.ResponsePreFavEdit
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.savetemplate.request.SearchPrescription
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.savetemplate.request.SearchPrescriptionResponseModel
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.templeteEdit.ManagePreFavAddEditviewModelFactory
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.templeteEdit.request.ExistingDetail
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.templeteEdit.request.NewDetail
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.templeteEdit.request.RemovedDetail
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import retrofit2.Response
import java.io.IOException


class ManagePreFavEdit: DialogFragment() {
    private var selectRouteName: String?=""
    private var selectFreqValue: String?=""
    private var selectPeriodName: String?=""
    private var selectinstructionName: String?=""
    lateinit var drugNmae: TextView
    private var ResponseContantSave: ResponseContentsfav? = ResponseContentsfav()

    private var is_active: Int? = 0
    private var deletefavouriteID: Int? = 0
    private var Str_auto_id: Int? = 0
    private var content: String? = null
    private var viewModel: ManagePreFAvAddEditviewModel? = null
    var binding: DialogManageFavouritePrescriptionBinding? = null
    var appPreferences: AppPreferences? = null
    private var utils: Utils? = null
    private var facility_UUID: Int? = 0
    val emrFavRequestModel: RequestPrecEditModule? = RequestPrecEditModule()
    private var deparment_UUID: Int? = 0
    private var facility_id: Int? = 0
    private var user_uuid: Int? = 0
    private var customdialog: Dialog?=null
    var status : Boolean ?=false
    lateinit var drugName: TextView
    var exitingStatus : Boolean ?=false

    private var test_uuid: Int? = 0

    private var routeSpinnerList = mutableMapOf<Int, String>()
    private var frequencySpinnerList = mutableMapOf<Int, String>()
    private var instructionSpinnerList = mutableMapOf<Int, String>()
    private var durationSpinnerList = mutableMapOf<Int, String>()

    private var favouriteData: FavouritesModel?=null
    private val hashfrequencySpinnerList: HashMap<Int,Int> = HashMap()
    private val hashrouteSpinnerList: HashMap<Int,Int> = HashMap()
    private val hashinstructionSpinnerList: HashMap<Int,Int> = HashMap()
    private val hashdurationSpinnerList: HashMap<Int,Int> = HashMap()

    private  var selectRouteUuid:Int=0
    private  var selectFrequencyUuid:Int=0
    private  var selectInstructionUuid:Int=0
    private  var selectDurationUuid:Int=0

    private  var exitiingPisition:Int=0

    var callbackfavourite: OnFavRefreshListener? = null
    var saveFavAdapter:PresManageFavAdapter?=null

    var userDetailsRoomRepository: UserDetailsRoomRepository? = null

    val header: Headers? = Headers()
    val detailsList: ArrayList<Detail> = ArrayList()

    var bundle: Bundle = Bundle()

    var addnewstatus:Boolean?=false

    var removed_details: ArrayList<RemovedDetail> = ArrayList()

    var new_details: ArrayList<NewDetail> = ArrayList()

    var existing_details: ArrayList<ExistingDetail> = ArrayList()

    private var listDepartmentItems: ArrayList<FavAddResponseContent?> = ArrayList()
    private var favAddResponseMap = mutableMapOf<Int, String>()

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
                DataBindingUtil.inflate(inflater, R.layout.dialog_manage_favourite_prescription, container, false)
        viewModel = ManagePreFavAddEditviewModelFactory(
                requireActivity().application
        )
                .create(ManagePreFAvAddEditviewModel::class.java)

        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this

        userDetailsRoomRepository = UserDetailsRoomRepository(requireActivity().application)
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        deparment_UUID = appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)
        binding?.autoCompleteTextViewTestName?.setText(userDataStoreBean?.user_name)
        removed_details.clear()
        new_details.clear()
        existing_details.clear()
        saveFavAdapter = PresManageFavAdapter(
            activity!!,ArrayList()
        )
        binding!!.favEditRecyclerView!!.adapter = saveFavAdapter
        binding!!.closeImageView.setOnClickListener {
            dialog?.dismiss()
        }
        binding!!.cancel.setOnClickListener {
            dialog?.dismiss()
        }

        viewModel?.getDepartmentList(facility_UUID, favPresDepartmentCallBack)

        //viewModel?.getD

        binding?.switchCheck!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            // do something, the isChecked will be
            // true if the switch is in the On position
            if(isChecked)
            {
                is_active = 1
            }
            else{
                is_active = 0
            }})

        val args = arguments
        if (args == null) {
            status = true
            //  Toast.makeText(activity, "arguments is null ", Toast.LENGTH_LONG).show()
        }
        else
        {
            // get value from bundle..
            favouriteData = args.getParcelable(AppConstants.RESPONSECONTENT)
            Log.i("",""+favouriteData)
            ResponseContantSave?.favourite_id = favouriteData?.favourite_id
            ResponseContantSave?.favourite_display_order = favouriteData!!.favourite_display_order
            ResponseContantSave?.drug_name = favouriteData!!.drug_name
            ResponseContantSave?.drug_period_name = favouriteData!!.drug_period_name
            ResponseContantSave?.drug_route_name = favouriteData!!.drug_route_name
            ResponseContantSave?.drug_instruction_name = favouriteData!!.drug_instruction_name
            ResponseContantSave?.drug_frequency_name = favouriteData!!.drug_frequency_name

            ResponseContantSave?.drug_duration = favouriteData?.drug_duration
            binding?.buttonstatus?.text = "Update"
            binding?.autoCompleteTextViewDuration
            binding?.autoCompleteTextViewDuration?.setText(favouriteData?.drug_duration?.toString())
            binding?.autoCompleteTextViewDispalyName?.setText(favouriteData?.favourite_display_order?.toString())
            binding?.autoCompleteTextViewDispalyName?.setFocusable(false);
            binding?.autoCompleteTextViewDispalyName?.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
            binding?.autoCompleteTextViewDispalyName?.setClickable(false); //

            binding?.autoCompleteTextViewDispalyName?.setText(favouriteData!!.favourite_display_order?.toString())
//            binding?.displayOrderTextView?.text = favouriteData!!.favourite_display_order.toString()

            binding?.autoCompleteTextViewDrugName!!.setText(favouriteData?.drug_name)
            saveFavAdapter?.setFavAddItem(ResponseContantSave)
        }

        binding?.addDetailsItem?.setOnClickListener({
            val Str_DisplayOrder = binding?.autoCompleteTextViewDispalyName?.text.toString()
            if(status as Boolean)
            {

                detailsList.clear()
                header?.ticksheet_type_uuid=1
                header?.is_public = false
                header?.facility_uuid = facility_id?.toString()!!
                header?.favourite_type_uuid= AppConstants.FAV_TYPE_ID_PRESCRIPTION
                header?.department_uuid = deparment_UUID?.toString()!!
                header?.user_uuid = userDataStoreBean?.uuid?.toString()!!
                header?.display_order = Str_DisplayOrder
                header?.revision = true
                header?.is_active = "true"

                val details: Detail= Detail()
                details.test_master_uuid=Str_auto_id!!.toInt()
                details.test_master_type_uuid = 0
                details.item_master_uuid =test_uuid
                details.chief_complaint_uuid=0
                details.vital_master_uuid=0
                details.drug_route_uuid=selectRouteUuid.toString()
                details.drug_frequency_uuid=selectFrequencyUuid?.toString()
                details.duration=binding?.autoCompleteTextViewDuration?.text?.toString()
                details.duration_period_uuid=selectDurationUuid?.toString()
                details.drug_instruction_uuid=selectInstructionUuid?.toString()
                details.display_order=Str_DisplayOrder
                details.revision=true
                details.is_active= "true"
                details.diagnosis_uuid=0
                detailsList.add(details)

                emrFavRequestModel?.headers = this.header!!
                emrFavRequestModel?.details = this.detailsList

                val jsonrequest = Gson().toJson(emrFavRequestModel)

                Log.i("",""+jsonrequest)
                viewModel?.getADDFavourite(facility_id, emrFavRequestModel!!, emrposFavtRetrofitCallback)
            }
            else
            {

                val duration = binding?.autoCompleteTextViewDuration?.text?.toString()?.trim()

                val updatePrescriptionRequest : UpdatePrescriptionRequest = UpdatePrescriptionRequest()
                updatePrescriptionRequest.chief_complaint_id=""
                updatePrescriptionRequest.vital_master_id=""
                updatePrescriptionRequest.department_id = deparment_UUID.toString()
                updatePrescriptionRequest.favourite_display_order=Str_DisplayOrder?.toInt()
                updatePrescriptionRequest.drug_route_id = selectRouteUuid?.toString()
                updatePrescriptionRequest.drug_frequency_id = selectFrequencyUuid
                updatePrescriptionRequest.drug_duration = duration?.toInt()
                updatePrescriptionRequest.drug_period_id =selectDurationUuid
                updatePrescriptionRequest.drug_instruction_id = selectInstructionUuid
                updatePrescriptionRequest.favourite_id = favouriteData?.favourite_id

               //False

                viewModel?.getEditFavourite(deparment_UUID,facility_id,updatePrescriptionRequest,emrposListDataFavtEditRetrofitCallback)
            }


        })



        binding?.autoCompleteTextViewDrugName?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun afterTextChanged(s: Editable) {

                Log.i("textsixe",""+s.length)

                if (s.length > 2 && s.length<5) {

                    Log.i("textsixe",""+s.length)
                    viewModel?.getSearchDetails(getSearchRetrofitCallback, facility_id,s.toString())

                }}
        })


        binding?.routeSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                val itemValue=parent!!.getItemAtPosition(0).toString()
                selectRouteUuid=routeSpinnerList.filterValues { it == itemValue }.keys.toList()[0]
            }
            override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    pos: Int,
                    id: Long) {

                val itemValue=parent!!.getItemAtPosition(pos).toString()
                selectRouteUuid=routeSpinnerList.filterValues { it == itemValue }.keys.toList()[0]
                selectRouteName = itemValue
                Log.i("","seleceted"+selectRouteUuid)
            }
        }
        binding?.frequencySpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                val itemValue=parent!!.getItemAtPosition(0).toString()
                selectFrequencyUuid=frequencySpinnerList.filterValues { it == itemValue }.keys.toList()[0]
            }

            override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    pos: Int,
                    id: Long) {

                val itemValue=parent!!.getItemAtPosition(pos).toString()
                selectFrequencyUuid=frequencySpinnerList.filterValues { it == itemValue }.keys.toList()[0]
                selectFreqValue = itemValue

                Log.i("","seleceted"+selectFrequencyUuid)
            }
        }



        binding?.instructionSpinner!!.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        val itemValue = parent!!.getItemAtPosition(0).toString()
                        selectInstructionUuid =
                                instructionSpinnerList.filterValues { it == itemValue }.keys.toList()[0]
                    }

                    override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            pos: Int,
                            id: Long
                    ) {

                        val itemValue = parent!!.getItemAtPosition(pos).toString()
                        selectInstructionUuid =
                                instructionSpinnerList.filterValues { it == itemValue }.keys.toList()[0]
                        selectinstructionName = itemValue
                        Log.i("","seleceted"+selectInstructionUuid)
                    }
                }

        binding!!.autoCompleteTextViewDrugName.setOnItemClickListener { parent, _, position, id ->
            val selectedPoi = parent.adapter.getItem(position) as SearchPrescription?
            binding!!.autoCompleteTextViewDrugName.setText(selectedPoi?.name)


            addnewstatus=true

            test_uuid=selectedPoi!!.uuid

        }

        binding?.PeriodSpinner!!.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        val itemValue = parent!!.getItemAtPosition(0).toString()
                        selectDurationUuid =
                                durationSpinnerList.filterValues { it == itemValue }.keys.toList()[0]
                    }

                    override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            pos: Int,
                            id: Long
                    ) {

                        val itemValue = parent!!.getItemAtPosition(pos).toString()
                        selectDurationUuid =
                                durationSpinnerList.filterValues { it == itemValue }.keys.toList()[0]
                        selectPeriodName = itemValue
                    }
                }

        saveFavAdapter?.setOnDeleteClickListener(object : PresManageFavAdapter.OnDeleteClickListener{
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
                        facility_id,
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

        return binding!!.root
    }

    fun setAdapter(
            responseContents: ArrayList<SearchPrescription>
    ) {
        val responseContentAdapter = PrescriptionSearchAdapter(
                this!!.context!!,
                R.layout.row_chief_complaint_search_result,
                responseContents
        )
       //Log.i("",""+responseContents?.get(0)?.strength)
        binding!!.autoCompleteTextViewDrugName.threshold = 1
        binding!!.autoCompleteTextViewDrugName.setAdapter(responseContentAdapter)
        binding!!.autoCompleteTextViewDrugName.showDropDown()

    }
/*
Edit Response
 */
    val emrposFavtRetrofitCallback = object : RetrofitCallback<ResponsePrescriptionFav> {
        override fun onSuccessfulResponse(responseBody: Response<ResponsePrescriptionFav>?) {
            Log.i("",""+responseBody?.body()?.responseContents)
            Log.i("",""+responseBody?.body()?.responseContents)
            Log.i("",""+responseBody?.body()?.responseContents)
//            Log.i("", "" + responseBody?.body()?.responseContents?.details?.get(0)?.favourite_master_uuid);
            viewModel?.getAddListFav(facility_UUID,responseBody?.body()?.responseContents?.details?.get(0)?.favourite_master_uuid,emrposListDataFavtRetrofitCallback)

            utils?.showToast(
                R.color.positiveToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }
        override fun onBadRequest(response: Response<ResponsePrescriptionFav>?) {
            val gson = GsonBuilder().create()
            val responseModel: ResponsePrescriptionFav

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
            saveFavAdapter?.setFavAddItem(responseBody?.body()?.responseContents)
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

    val getSearchRetrofitCallback = object : RetrofitCallback<SearchPrescriptionResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<SearchPrescriptionResponseModel>?) {
            Log.i("res", "" + responseBody?.body()?.message)

            Log.i("res", "" + responseBody?.body()?.responseContents)
            setAdapter(responseBody?.body()?.responseContents!!)
        }

        override fun onBadRequest(errorBody: Response<SearchPrescriptionResponseModel>?) {

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

            viewModel!!.progress.value = 8

        }

    }

    val getRouteRetrofitCallback = object : RetrofitCallback<PrescriptionRoutResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<PrescriptionRoutResponseModel>?) {
            Log.i("res", "" + responseBody?.body()?.message)


            setRouteSpinnerValue(responseBody?.body()?.responseContents)

            viewModel?.getFrequencyDetails(getFrequencyRetrofitCallback, facility_id)

        }


        override fun onBadRequest(errorBody: Response<PrescriptionRoutResponseModel>?) {

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

            viewModel!!.progress.value = 8

        }

    }
    val getFrequencyRetrofitCallback = object : RetrofitCallback<PrescriptionFrequencyResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<PrescriptionFrequencyResponseModel>?) {
            Log.i("res", "" + responseBody?.body()?.message)


            //   viewModel?.getInstructionDetails(getInstructionRetrofitCallback, facility_id)

            setFrequencySpinnerValue(responseBody?.body()?.responseContents)
            viewModel?.getPrescriptionDuration(getSaveDurationRetrofitCallBack,facility_id)





        }
        override fun onBadRequest(errorBody: Response<PrescriptionFrequencyResponseModel>?) {
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

            viewModel!!.progress.value = 8
        }

    }
    val getSaveDurationRetrofitCallBack =
            object : RetrofitCallback<PrescriptionDurationResponseModel> {
                override fun onSuccessfulResponse(response: Response<PrescriptionDurationResponseModel>) {
                    Log.i("res", "" + response.body()?.message)

                    setdurationSpinnerValue(response.body()!!.responseContents)

                    viewModel?.getInstructionDetails(getInstructionRetrofitCallback, facility_id)

                }

                override fun onBadRequest(response: Response<PrescriptionDurationResponseModel>) {
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
                                responseModel.message
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
    val getInstructionRetrofitCallback = object : RetrofitCallback<PresInstructionResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<PresInstructionResponseModel>?) {
            Log.i("res", "" + responseBody?.body()?.message)

            setInsttructionSpinnerValue(responseBody?.body()?.responseContents)

        }
        override fun onBadRequest(errorBody: Response<PresInstructionResponseModel>?) {
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

            viewModel!!.progress.value = 8
        }

    }

    fun setRouteSpinnerValue(responseContents: List<PrescriptionRouteResponseContent?>?) {

        routeSpinnerList = responseContents?.map { it?.uuid!! to it.name!! }!!.toMap().toMutableMap()
        hashrouteSpinnerList.clear()

        for(i in responseContents.indices){

            hashrouteSpinnerList[responseContents[i]!!.uuid] = i
        }
        saveFavAdapter!!.setRote(routeSpinnerList)
        val adapter = ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_item, routeSpinnerList.values.toMutableList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.routeSpinner!!.adapter = adapter

        try {
            if (hashrouteSpinnerList.size != 0) {
                binding?.routeSpinner!!.setSelection(hashrouteSpinnerList.get(favouriteData?.drug_route_id!!.toInt())!!)
            }
        }catch (e : Exception){

        }

    }

    fun setFrequencySpinnerValue(responseContents: List<PrescriptionFrequencyresponseContent?>?) {

        frequencySpinnerList = responseContents?.map { it?.uuid!! to it.name!! }!!.toMap().toMutableMap()


        hashfrequencySpinnerList.clear()

        for(i in responseContents.indices){

            hashfrequencySpinnerList[responseContents[i]!!.uuid!!] = i
        }

        saveFavAdapter!!.setfrequencySpinnerList(frequencySpinnerList)

        val adapter = ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_item, frequencySpinnerList.values.toMutableList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.frequencySpinner!!.adapter = adapter

        try {
        if(hashfrequencySpinnerList.size != 0) {
            binding?.frequencySpinner!!.setSelection(hashfrequencySpinnerList.get(favouriteData?.drug_frequency_id!!.toInt())!!)
        }
        }catch (e : Exception){

        }
    }

    fun setInsttructionSpinnerValue(responseContents: List<PresInstructionResponseContent?>?) {

        instructionSpinnerList =
                responseContents?.map { it?.uuid!! to it.name!! }!!.toMap().toMutableMap()

        hashinstructionSpinnerList.clear()
        for(i in responseContents.indices){

            hashinstructionSpinnerList[responseContents[i]!!.uuid] = i
        }

        saveFavAdapter!!.setinstructionSpinnerList(instructionSpinnerList)
        val adapter = ArrayAdapter<String>(
                activity!!,
                android.R.layout.simple_spinner_item,
                instructionSpinnerList.values.toMutableList()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.instructionSpinner!!.adapter = adapter

        try{
        if(hashinstructionSpinnerList.size != 0) {
            binding?.instructionSpinner!!.setSelection(hashinstructionSpinnerList.get(favouriteData?.drug_instruction_id!!.toInt())!!)
        }
    }catch (e : Exception){

    }
    }

    fun setdurationSpinnerValue(responseContents: List<PrescriptionDurationResponseContent?>?) {

        durationSpinnerList =
                responseContents?.map { it?.uuid!! to it.name!! }!!.toMap().toMutableMap()

        hashdurationSpinnerList.clear()

        for(i in responseContents.indices){

            hashdurationSpinnerList[responseContents[i]!!.uuid] = i
        }

        saveFavAdapter!!.setdurationSpinnerList(durationSpinnerList)
        val adapter = ArrayAdapter<String>(
                activity!!,
                android.R.layout.simple_spinner_item,
                durationSpinnerList.values.toMutableList()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.PeriodSpinner!!.adapter = adapter

        /*if(hashdurationSpinnerList.size != 0) {
            binding?.PeriodSpinner!!.setSelection(hashdurationSpinnerList.get(favouriteData?.durationPeriodId!!.toInt())!!)
        }*/
    }

    var deleteRetrofitResponseCallback = object : RetrofitCallback<DeleteResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<DeleteResponseModel>?) {
            saveFavAdapter?.adapterrefresh(deletefavouriteID)
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


    /*
    Updated Response NEw
     */

    val emrposListDataFavtEditRetrofitCallback = object : RetrofitCallback<ResponsePreFavEdit> {
        override fun onSuccessfulResponse(responseBody: Response<ResponsePreFavEdit>?) {
            Log.i("", "" + responseBody?.body()?.requestContent);
            ResponseContantSave?.drug_route_id = responseBody?.body()?.requestContent?.drug_route_id
            ResponseContantSave?.drug_route_name = selectRouteName

            ResponseContantSave?.drug_frequency_id = responseBody?.body()?.requestContent?.drug_frequency_id?.toString()
            ResponseContantSave?.drug_frequency_name = selectFreqValue

            ResponseContantSave?.drug_instruction_id = responseBody?.body()?.requestContent?.drug_instruction_id?.toString()
            ResponseContantSave?.drug_instruction_name = selectinstructionName
            ResponseContantSave?.drug_duration = responseBody?.body()?.requestContent?.drug_duration
            ResponseContantSave?.favourite_id = responseBody?.body()?.requestContent?.favourite_id
            ResponseContantSave?.favourite_display_order = responseBody?.body()?.requestContent?.favourite_display_order
//            ResponseContantSave?.test_master_name = responseBody?.body()?.requestContent?.testname
            saveFavAdapter!!.clearadapter()

            saveFavAdapter!!.setFavAddItem(ResponseContantSave)
            callbackfavourite?.onFavID(responseBody?.body()?.requestContent?.favourite_id!!)

           Toast.makeText(activity,responseBody?.body()?.message,Toast.LENGTH_LONG).show()
            //dialog!!.dismiss()

        }
        override fun onBadRequest(response: Response<ResponsePreFavEdit>?) {
            val gson = GsonBuilder().create()
            val responseModel: ResponsePreFavEdit
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




    fun setOnFavRefreshListener(callback: OnFavRefreshListener) {
        callbackfavourite = callback
    }
    // This interface can be implemented by the Activity, parent Fragment,
    // or a separate test implementation.
    interface OnFavRefreshListener {
        fun onFavID(position: Int)
        fun onRefreshList()
    }


    val favPresDepartmentCallBack =
        object : RetrofitCallback<FavAddResponseModel> {
            override fun onSuccessfulResponse(response: Response<FavAddResponseModel>) {

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

                viewModel?.getRouteDetails(getRouteRetrofitCallback, facility_id)

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
}