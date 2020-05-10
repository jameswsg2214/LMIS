package com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.templeteEdit

import android.os.Binder
import android.os.Bundle
import android.text.BoringLayout
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.DialogTemplateEditPrescriptionBinding
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddAllDepatResponseContent
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddResponseContent
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddTestNameResponseContent
import com.hmisdoctor.ui.emr_workflow.lab.model.favresponse.ResponseContentsfav
import com.hmisdoctor.ui.emr_workflow.lab.model.request.Headers
import com.hmisdoctor.ui.emr_workflow.lab.model.request.RequestLabFavModel
import com.hmisdoctor.ui.emr_workflow.lab.ui.LabManageFavAdapter
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import com.hmisdoctor.ui.emr_workflow.prescription.model.*
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.savetemplate.request.Detail
import com.hmisdoctor.ui.emr_workflow.prescription.ui.PrescriptionSearchAdapter
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.savetemplate.SaveTemplateAdapter
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.savetemplate.request.SaveTemplateRequestModel
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.savetemplate.request.SearchPrescription
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.savetemplate.request.SearchPrescriptionResponseModel
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.savetemplate.response.SaveTemplateResponseModel
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.templeteEdit.request.ExistingDetail
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.templeteEdit.request.NewDetail
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.templeteEdit.request.RemovedDetail
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.templeteEdit.request.UpdateRequestModel
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.templeteEdit.response.UpdateResponseModel
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import kotlinx.android.synthetic.main.activity_profiledetails.*
import retrofit2.Response

class ManagePreTempAddEdit: DialogFragment() {
    private var ResponseContantSave: ResponseContentsfav? = ResponseContentsfav()
    private var favouriteData: Templates? = null
    private var is_active: Boolean = true
    private var Str_auto_id: Int? = 0
    private var content: String? = null
    private var viewModel: ManagePreTempAddEditviewModel? = null
    var binding: DialogTemplateEditPrescriptionBinding? = null
    var appPreferences: AppPreferences? = null
    private var utils: Utils? = null
    private var facility_UUID: Int? = 0

    private var deparment_UUID: Int? = 0
    private var facility_id: Int? = 0
    private var user_uuid: Int? = 0

    var status : Boolean ?=true

    var exitingStatus : Boolean ?=false

    private var test_uuid: Int? = 0

    private var routeSpinnerList = mutableMapOf<Int, String>()
    private var frequencySpinnerList = mutableMapOf<Int, String>()
    private var instructionSpinnerList = mutableMapOf<Int, String>()
    private var durationSpinnerList = mutableMapOf<Int, String>()


    private val hashfrequencySpinnerList: HashMap<Int,Int> = HashMap()
    private val hashrouteSpinnerList: HashMap<Int,Int> = HashMap()
    private val hashinstructionSpinnerList: HashMap<Int,Int> = HashMap()
    private val hashdurationSpinnerList: HashMap<Int,Int> = HashMap()

    private  var selectRouteUuid:Int=0
    private  var selectFrequencyUuid:Int=0
    private  var selectInstructionUuid:Int=0
    private  var selectDurationUuid:Int=0

    private  var exitiingPisition:Int=0


    var saveTemplateAdapter:ManagePreTempAdapter?=null

    var userDetailsRoomRepository: UserDetailsRoomRepository? = null

    val header: Headers? = Headers()
    val detailsList: ArrayList<Detail> = ArrayList()

    var bundle: Bundle = Bundle()

    var addnewstatus:Boolean?=false

    var removed_details: ArrayList<RemovedDetail> = ArrayList()

    var new_details: ArrayList<NewDetail> = ArrayList()

    var existing_details: ArrayList<ExistingDetail> = ArrayList()

    var callbacktemplate: OnTemplateRefreshListener? = null

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
                DataBindingUtil.inflate(inflater, R.layout.dialog_template_edit_prescription, container, false)
        viewModel = ManagePreTempAddEditviewModelFactory(
                requireActivity().application
        )
                .create(ManagePreTempAddEditviewModel::class.java)

        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this

        userDetailsRoomRepository = UserDetailsRoomRepository(requireActivity().application)
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        facility_id = appPreferences?.getInt(AppConstants?.FACILITY_UUID)
        deparment_UUID = appPreferences?.getInt(AppConstants?.DEPARTMENT_UUID)

        removed_details.clear()

        new_details.clear()

        existing_details.clear()

        val args = arguments
        if (args == null) {

            Toast.makeText(activity, "arguments is null ", Toast.LENGTH_LONG).show()

            saveTemplateAdapter= ManagePreTempAdapter(ArrayList<DrugDetail>(),
                    activity!!
            )

            binding?.zeroStockRecyclerView!!.adapter = saveTemplateAdapter

            binding!!.save.visibility=View.VISIBLE

            binding!!.updateTemp.visibility=View.GONE


        } else {


            status=false

            favouriteData = args.getParcelable(AppConstants.RESPONSECONTENT)

            Log.i("",""+favouriteData)


            binding!!.edtName.setText(favouriteData!!.temp_details.template_name)

            binding!!.autoCompleteTextViewDispalyName.setText(favouriteData!!.temp_details.display_order.toString())

            saveTemplateAdapter = ManagePreTempAdapter(favouriteData!!.drug_details!!,
                    activity!!
            )

            binding?.zeroStockRecyclerView!!.adapter = saveTemplateAdapter


            binding!!.save.visibility=View.GONE

            binding!!.updateTemp.visibility=View.VISIBLE


        }

        binding?.closeImageView?.setOnClickListener {

            dialog?.dismiss()

        }

        binding!!.cancel.setOnClickListener {

            dialog?.dismiss()

        }


        viewModel?.getRouteDetails(getRouteRetrofitCallback, facility_id)


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


        binding!!.addTemp.setOnClickListener {

            val send_data=DrugDetail()

            binding!!.autoCompleteTextViewDrugName.isEnabled=true

            if(status!!) {

                if (test_uuid != 0) {

                    send_data.drug_id = test_uuid!!

                    send_data.drug_name = binding!!.autoCompleteTextViewDrugName.text.toString()

                    send_data.drug_route_id = selectRouteUuid

                    send_data.drug_frequency_id = selectFrequencyUuid

                    send_data.drug_duration = binding!!.autoCompleteTextViewDuration.text.toString().toInt()

                    send_data.drug_period_id = selectDurationUuid

                    send_data.drug_instruction_id = selectInstructionUuid

                    saveTemplateAdapter!!.addRow(send_data)



                } else {

                    Toast.makeText(this.context, "Please select Valid Drug Name", Toast.LENGTH_SHORT).show()

                }
            }
            else{

                if (test_uuid != 0) {


                    if(exitingStatus!!){


                        send_data.drug_id = test_uuid!!

                        send_data.drug_name = binding!!.autoCompleteTextViewDrugName.text.toString()

                        send_data.drug_route_id = selectRouteUuid

                        send_data.drug_frequency_id = selectFrequencyUuid

                        send_data.drug_duration = binding!!.autoCompleteTextViewDuration.text.toString().toInt()

                        send_data.drug_period_id = selectDurationUuid

                        send_data.drug_instruction_id = selectInstructionUuid

                        saveTemplateAdapter!!.updateRow(send_data,exitiingPisition)

                        val existingDetail=ExistingDetail()

                        existingDetail.template_master_uuid = favouriteData!!.temp_details.template_id
                        existingDetail.drug_id = test_uuid!!
                        existingDetail.drug_route_uuid = selectRouteUuid.toString()
                        existingDetail.drug_frequency_uuid = selectFrequencyUuid
                        existingDetail.drug_duration = binding!!.autoCompleteTextViewDuration.text.toString().toInt()
                        existingDetail.drug_period_uuid = selectDurationUuid
                        existingDetail.drug_instruction_uuid = selectInstructionUuid
                        existingDetail.display_order = binding!!.autoCompleteTextViewDispalyName.text.toString().toInt()
                        existingDetail.quantity = 14

                        existing_details.add(existingDetail)

                        exitiingPisition=0
                        exitingStatus=false

                        binding!!.autoCompleteTextViewDrugName.text.clear()

                        binding!!.autoCompleteTextViewDuration.text.clear()

                        test_uuid = 0

                        viewModel?.getRouteDetails(getRouteRetrofitCallback, facility_id)
                    }
                    else {
                        send_data.drug_id = test_uuid!!

                        send_data.drug_name = binding!!.autoCompleteTextViewDrugName.text.toString()

                        send_data.drug_route_id = selectRouteUuid

                        send_data.drug_frequency_id = selectFrequencyUuid

                        send_data.drug_duration = binding!!.autoCompleteTextViewDuration.text.toString().toInt()

                        send_data.drug_period_id = selectDurationUuid

                        send_data.drug_instruction_id = selectInstructionUuid

                        saveTemplateAdapter!!.addRow(send_data)

                        // for api

                        val newDetail = NewDetail()
                        newDetail.template_master_uuid = favouriteData!!.temp_details.template_id
                        newDetail.drug_id = test_uuid!!
                        newDetail.drug_route_uuid = selectRouteUuid.toString()
                        newDetail.drug_frequency_uuid = selectFrequencyUuid.toString()
                        newDetail.drug_duration = binding!!.autoCompleteTextViewDuration.text.toString()
                        newDetail.drug_period_uuid = selectDurationUuid.toString()
                        newDetail.drug_instruction_uuid = selectInstructionUuid.toString()
                        newDetail.display_order = binding!!.autoCompleteTextViewDispalyName.text.toString().toInt()
                        newDetail.quantity = 14

                        new_details.add(newDetail)

                        binding!!.autoCompleteTextViewDrugName.text.clear()

                        binding!!.autoCompleteTextViewDuration.text.clear()

                        test_uuid = 0

                        viewModel?.getRouteDetails(getRouteRetrofitCallback, facility_id)
                    }


                    binding!!.autoCompleteTextViewDrugName.text.clear()

                    binding!!.autoCompleteTextViewDuration.text.clear()

                    test_uuid = 0

                    viewModel?.getRouteDetails(getRouteRetrofitCallback, facility_id)

                } else {


                    Toast.makeText(this.context, "Please select Valid Drug Name", Toast.LENGTH_SHORT).show()

                }



            }

        }

        binding!!.save.setOnClickListener {


            val get_all_data=saveTemplateAdapter!!.getAll()

            if(get_all_data.size!=null) {

                val saveTemplateRequestModel = SaveTemplateRequestModel()

                val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

                saveTemplateRequestModel.headers.name = binding!!.edtName.text.toString()
                saveTemplateRequestModel.headers.description = binding!!.edtDescription.text.toString()
                saveTemplateRequestModel.headers.display_order = binding!!.autoCompleteTextViewDispalyName.text.toString()
                saveTemplateRequestModel.headers.template_type_uuid = 1
                saveTemplateRequestModel.headers.user_uuid = userDataStoreBean?.uuid!!.toString()
                saveTemplateRequestModel.headers.facility_uuid = "$facility_id"
                saveTemplateRequestModel.headers.department_uuid = "$deparment_UUID"

                detailsList.clear()

                for (i in get_all_data.indices) {

                    val details: Detail = Detail()

                    details.item_master_uuid = get_all_data[i].drug_id!!
                    details.drug_route_uuid = get_all_data[i].drug_route_id
                    details.drug_frequency_uuid = get_all_data[i].drug_frequency_id
                    details.duration = get_all_data[i].drug_duration
                    details.duration_period_uuid = get_all_data[i].drug_period_id
                    details.drug_instruction_uuid = get_all_data[i].drug_instruction_id!!
                    details.quantity = 1

                    detailsList.add(details)

                }

                saveTemplateRequestModel.details=detailsList


                viewModel?.saveTemplate(saveTemplateRetrofitCallback,saveTemplateRequestModel)

            }


        }

        binding!!.updateTemp.setOnClickListener {

            val updateRequestModel=UpdateRequestModel()


            updateRequestModel.headers.template_id=favouriteData!!.temp_details.template_id


            updateRequestModel.headers.name = binding!!.edtName.text.toString()
            updateRequestModel.headers.description = binding!!.edtDescription.text.toString()
            updateRequestModel.headers.display_order = binding!!.autoCompleteTextViewDispalyName.text.toString().toInt()

            updateRequestModel.headers.facility_uuid=facility_id.toString()

            updateRequestModel.existing_details=existing_details

            updateRequestModel.new_details=new_details

            updateRequestModel.removed_details=removed_details


            viewModel!!.updateTemplate(updateTemplateRetrofitCallback,updateRequestModel)



        }


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
                    }
                }

        saveTemplateAdapter!!.setOnDeleteClickListener(object :ManagePreTempAdapter.OnDeleteClickListener{
            override fun onDeleteClick(responseContent: DrugDetail?, position: Int) {

                if(status!!){

                    saveTemplateAdapter!!.deleteRow(position)

                }
                else{

                    val removedDetail =RemovedDetail()

                    removedDetail.template_uuid=favouriteData!!.temp_details.template_id

                    removedDetail.template_details_uuid=responseContent!!.drug_id

                    removed_details.add(removedDetail)

                    saveTemplateAdapter!!.deleteRow(position)


                }


            }
        })

        saveTemplateAdapter!!.setOnViewClickListener(object :ManagePreTempAdapter.OnViewClickListener{

            override fun onViewClick(responseContent: DrugDetail?, position: Int) {


                if(!status!!){

                    exitingStatus=true

                    exitiingPisition=position

                    binding!!.autoCompleteTextViewDrugName.setText(responseContent!!.drug_name)

                    binding!!.autoCompleteTextViewDrugName.isEnabled=false

                    test_uuid=responseContent.drug_id

                    binding!!.autoCompleteTextViewDuration.setText(responseContent.drug_duration.toString())

                    selectRouteUuid=responseContent.drug_route_id

                    binding!!.routeSpinner.setSelection(hashrouteSpinnerList[selectRouteUuid]!!)

                    selectFrequencyUuid=responseContent.drug_frequency_id

                    binding!!.frequencySpinner.setSelection(hashfrequencySpinnerList[selectFrequencyUuid]!!)

                    selectDurationUuid= responseContent.drug_period_id

                    binding!!.PeriodSpinner.setSelection(hashdurationSpinnerList[selectDurationUuid]!!)

                    selectInstructionUuid =responseContent.drug_instruction_id

                    binding!!.instructionSpinner.setSelection(hashinstructionSpinnerList[selectInstructionUuid]!!)



                        }



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
        binding!!.autoCompleteTextViewDrugName.threshold = 1
        binding!!.autoCompleteTextViewDrugName.setAdapter(responseContentAdapter)
        binding!!.autoCompleteTextViewDrugName.showDropDown()

    }

    fun allData(){

        val args = arguments
        if (args == null) {
            status = true


        } else {



        }



    }


    val saveTemplateRetrofitCallback=object :RetrofitCallback<SaveTemplateResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<SaveTemplateResponseModel>?) {


            Log.i("",""+responseBody!!.body()!!.message)

            Log.i("",""+responseBody!!.body()!!.message)

            saveTemplateAdapter!!.clearAll()

            binding!!.edtName.setText("")

            binding!!.edtDescription.setText("")

            binding!!.autoCompleteTextViewDispalyName.text.clear()

            binding!!.autoCompleteTextViewDrugName.text.clear()

            binding!!.autoCompleteTextViewDuration.text.clear()

            Log.i("",""+responseBody!!.body()!!.message)

            dialog!!.dismiss()

        }

        override fun onBadRequest(errorBody: Response<SaveTemplateResponseModel>?) {

            val gson = GsonBuilder().create()
            val responseModel: PrescriptionSearchResponseModel
            try {

                responseModel = gson.fromJson(
                        errorBody!!.errorBody()!!.string(),
                        PrescriptionSearchResponseModel::class.java
                )


                Log.i("","BADREQ"+responseModel.message)
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
            Log.i("","server")
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


    val updateTemplateRetrofitCallback=object :RetrofitCallback<UpdateResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<UpdateResponseModel>?) {


            Log.i("",""+responseBody!!.body()!!.message)

            Log.i("",""+responseBody!!.body()!!.message)

            saveTemplateAdapter!!.clearAll()

            binding!!.edtName.setText("")

            binding!!.edtDescription.setText("")

            binding!!.autoCompleteTextViewDispalyName.text.clear()

            binding!!.autoCompleteTextViewDrugName.text.clear()

            binding!!.autoCompleteTextViewDuration.text.clear()

            Log.i("",""+responseBody!!.body()!!.message)

        }

        override fun onBadRequest(errorBody: Response<UpdateResponseModel>?) {

            val gson = GsonBuilder().create()
            val responseModel: UpdateResponseModel
            try {

                responseModel = gson.fromJson(
                        errorBody!!.errorBody()!!.string(),
                        UpdateResponseModel::class.java
                )


                Log.i("","BADREQ"+responseModel.message)
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
            Log.i("","server")
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
    val getFrequencyRetrofitCallback = object : RetrofitCallback<PresDrugFrequencyResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<PresDrugFrequencyResponseModel>?) {
            Log.i("res", "" + responseBody?.body()?.message)


            //   viewModel?.getInstructionDetails(getInstructionRetrofitCallback, facility_id)

            setFrequencySpinnerValue(responseBody?.body()?.responseContents)
            viewModel?.getPrescriptionDuration(getSaveDurationRetrofitCallBack,facility_id)





        }
        override fun onBadRequest(errorBody: Response<PresDrugFrequencyResponseModel>?) {
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

            allData()
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
/*
    private fun setInsttructionSpinnerValue(responseContents: List<PresInstructionResponseContent>?) {

        routeNamesMap = responseContents?.map { it?.uuid!! to it.name }!!.toMap().toMutableMap()


    }*/


    fun setRouteSpinnerValue(responseContents: List<PrescriptionRouteResponseContent?>?) {


        routeSpinnerList = responseContents?.map { it?.uuid!! to it.name!! }!!.toMap().toMutableMap()


        hashrouteSpinnerList.clear()

        for(i in responseContents.indices){

            hashrouteSpinnerList[responseContents[i]!!.uuid] = i
        }

        saveTemplateAdapter!!.setRote(routeSpinnerList)

        val adapter = ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_item, routeSpinnerList.values.toMutableList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.routeSpinner!!.adapter = adapter


    }

    fun setFrequencySpinnerValue(responseContents: List<PresDrugFrequencyResponseContent?>?) {

        frequencySpinnerList = responseContents?.map { it?.uuid!! to it.name!! }!!.toMap().toMutableMap()


        hashfrequencySpinnerList.clear()

        for(i in responseContents.indices){

            hashfrequencySpinnerList[responseContents[i]!!.uuid] = i
        }

        saveTemplateAdapter!!.setfrequencySpinnerList(frequencySpinnerList)

        val adapter = ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_item, frequencySpinnerList.values.toMutableList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.frequencySpinner!!.adapter = adapter

    }

    fun setInsttructionSpinnerValue(responseContents: List<PresInstructionResponseContent?>?) {

        instructionSpinnerList =
                responseContents?.map { it?.uuid!! to it.name!! }!!.toMap().toMutableMap()

        hashinstructionSpinnerList.clear()
        for(i in responseContents.indices){

            hashinstructionSpinnerList[responseContents[i]!!.uuid] = i
        }

        saveTemplateAdapter!!.setinstructionSpinnerList(instructionSpinnerList)
        val adapter = ArrayAdapter<String>(
                activity!!,
                android.R.layout.simple_spinner_item,
                instructionSpinnerList.values.toMutableList()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.instructionSpinner!!.adapter = adapter

    }

    fun setdurationSpinnerValue(responseContents: List<PrescriptionDurationResponseContent?>?) {

        durationSpinnerList =
                responseContents?.map { it?.uuid!! to it.name!! }!!.toMap().toMutableMap()

        hashdurationSpinnerList.clear()

        for(i in responseContents.indices){

            hashdurationSpinnerList[responseContents[i]!!.uuid] = i
        }

        saveTemplateAdapter!!.setdurationSpinnerList(durationSpinnerList)
        val adapter = ArrayAdapter<String>(
                activity!!,
                android.R.layout.simple_spinner_item,
                durationSpinnerList.values.toMutableList()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.PeriodSpinner!!.adapter = adapter

    }


    fun setOnTemplateRefreshListener(callback: OnTemplateRefreshListener) {
        this.callbacktemplate = callback
    }
    // This interface can be implemented by the Activity, parent Fragment,
    // or a separate test implementation.
    interface OnTemplateRefreshListener {
        fun onTemplateID(position: Int)
        fun onRefreshList()
    }

}