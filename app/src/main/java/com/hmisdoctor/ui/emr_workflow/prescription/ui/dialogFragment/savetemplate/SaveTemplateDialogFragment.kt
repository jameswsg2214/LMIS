package com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.savetemplate

import android.os.Bundle
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
import com.hmisdoctor.databinding.SaveTemplateDialogFragmentBinding
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.savetemplate.request.Detail
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import com.hmisdoctor.ui.emr_workflow.prescription.model.*
import com.hmisdoctor.ui.emr_workflow.prescription.ui.PrescriptionSearchAdapter
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.savetemplate.request.SaveTemplateRequestModel
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.savetemplate.request.SearchPrescription
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.savetemplate.request.SearchPrescriptionResponseModel
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.savetemplate.response.SaveTemplateResponseModel
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import retrofit2.Response


class SaveTemplateDialogFragment : DialogFragment() {
    private var content: String? = null

    private var routeSpinnerList = mutableMapOf<Int, String>()
    private var frequencySpinnerList = mutableMapOf<Int, String>()
    private var instructionSpinnerList = mutableMapOf<Int, String>()
    private var durationSpinnerList = mutableMapOf<Int, String>()

    private  var selectRouteUuid:Int=0
    private  var selectFrequencyUuid:Int=0
    private  var selectInstructionUuid:Int=0
    private  var selectDurationUuid:Int=0

    var saveTemplateAdapter:SaveTemplateAdapter?=null
    private var routeNamesMap = mutableMapOf<Int, String>()



    private var viewModel: SaveTemplateViewModel? = null
    var binding: SaveTemplateDialogFragmentBinding? = null
    var appPreferences: AppPreferences? = null
    private var utils: Utils? = null
    private var facility_UUID: Int? = 0
    private var deparment_UUID: Int? = 0
    private var facility_id: Int? = 0
    private var user_uuid: Int? = 0

    private var test_uuid: Int? = 0
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null

    var detailsList=ArrayList<Detail>()


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

        binding = DataBindingUtil.inflate(inflater, R.layout.save_template_dialog_fragment, container, false)
        viewModel = SaveTemplateViewModelFactory(
                requireActivity().application
        )
            .create(SaveTemplateViewModel::class.java)

        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        userDetailsRoomRepository = UserDetailsRoomRepository(requireActivity().application)
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)

        deparment_UUID = appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)


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

            var send_data=FavouritesModel()

            if(test_uuid!=0) {

                send_data.test_master_id = test_uuid
                send_data.itemAppendString = binding!!.autoCompleteTextViewDrugName.text.toString()
                send_data.selectRouteID = selectRouteUuid
                send_data.selecteFrequencyID = selectFrequencyUuid
                send_data.duration = binding!!.autoCompleteTextViewDuration.text.toString()
                send_data.PrescriptiondurationPeriodId = selectDurationUuid
                send_data.selectInvestID = selectInstructionUuid

                saveTemplateAdapter!!.addRow(send_data)

                binding!!.autoCompleteTextViewDrugName.text.clear()

                binding!!.autoCompleteTextViewDuration.text.clear()

                test_uuid=0

                viewModel?.getRouteDetails(getRouteRetrofitCallback, facility_id)
            }

            else{

                Toast.makeText(this.context,"Please select Valid Drug Name",Toast.LENGTH_SHORT).show()

            }
        }

        binding!!.save.setOnClickListener {

            val get_all_data=saveTemplateAdapter!!.getAll()

            if(get_all_data.size!=null) {

                val saveTemplateRequestModel = SaveTemplateRequestModel()

                val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

                saveTemplateRequestModel.headers.name = binding!!.autoCompleteTextViewTestName.text.toString()
                saveTemplateRequestModel.headers.description = binding!!.autoCompleteTextViewDescription.text.toString()
                saveTemplateRequestModel.headers.display_order = binding!!.autoCompleteTextViewDispalyName.text.toString()
                saveTemplateRequestModel.headers.template_type_uuid = 1
                saveTemplateRequestModel.headers.user_uuid = userDataStoreBean?.uuid!!.toString()
                saveTemplateRequestModel.headers.facility_uuid = "$facility_id"
                saveTemplateRequestModel.headers.department_uuid = "$deparment_UUID"




                detailsList.clear()

                for (i in get_all_data.indices) {

                    val details:Detail=Detail()

                    details.item_master_uuid = get_all_data[i].test_master_id!!
                    details.drug_route_uuid = get_all_data[i].selectRouteID
                    details.drug_frequency_uuid = get_all_data[i].selecteFrequencyID
                    details.duration = get_all_data[i].duration!!.toInt()
                    details.duration_period_uuid = get_all_data[i].PrescriptiondurationPeriodId
                    details.drug_instruction_uuid = get_all_data[i].selectInvestID!!
                    details.quantity = 1

                    detailsList.add(details)

                }

                saveTemplateRequestModel.details=detailsList


                viewModel?.saveTemplate(saveTemplateRetrofitCallback,saveTemplateRequestModel)

            }


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


        saveTemplateAdapter = SaveTemplateAdapter(
            ArrayList(),
            activity!!,frequencySpinnerList,routeSpinnerList,instructionSpinnerList,durationSpinnerList
        )

        binding?.zeroStockRecyclerView!!.adapter = saveTemplateAdapter
        if (args == null) {
            status = true

        } else {
            // get value from bundle..
            val favouriteData = args.getParcelableArrayList<FavouritesModel>(AppConstants.RESPONSECONTENT)

            Log.i("",""+favouriteData)

            for(i in favouriteData!!.indices){

                if (i != favouriteData?.size!!-1) {
                    saveTemplateAdapter!!.addRow(favouriteData[i])

                }
                else
                {

                }

            }




        }



    }


    val saveTemplateRetrofitCallback=object :RetrofitCallback<SaveTemplateResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<SaveTemplateResponseModel>?) {

            Log.i("",""+responseBody!!.body()!!.message)

            Log.i("",""+responseBody!!.body()!!.message)

            saveTemplateAdapter!!.clearAll()

            binding!!.autoCompleteTextViewTestName.text.clear()

            binding!!.autoCompleteTextViewDescription.text.clear()

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
        val adapter = ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_item, routeSpinnerList.values.toMutableList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.routeSpinner!!.adapter = adapter


    }

    fun setFrequencySpinnerValue(responseContents: List<PresDrugFrequencyResponseContent?>?) {

        frequencySpinnerList = responseContents?.map { it?.uuid!! to it.name!! }!!.toMap().toMutableMap()
        val adapter = ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_item, frequencySpinnerList.values.toMutableList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.frequencySpinner!!.adapter = adapter

    }

    fun setInsttructionSpinnerValue(responseContents: List<PresInstructionResponseContent?>?) {

        instructionSpinnerList =
            responseContents?.map { it?.uuid!! to it.name!! }!!.toMap().toMutableMap()
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
        val adapter = ArrayAdapter<String>(
                activity!!,
                android.R.layout.simple_spinner_item,
                durationSpinnerList.values.toMutableList()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.PeriodSpinner!!.adapter = adapter

    }

}