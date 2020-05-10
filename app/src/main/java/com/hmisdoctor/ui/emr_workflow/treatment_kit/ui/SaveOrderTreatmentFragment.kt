package com.hmisdoctor.ui.emr_workflow.treatment_kit.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
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
import android.app.TimePickerDialog
import android.text.Editable
import android.text.TextWatcher
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.DialogSaveOrderTreatmentBinding
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
import com.hmisdoctor.ui.emr_workflow.lab.ui.LabManageTemplateAdapter
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import com.hmisdoctor.ui.emr_workflow.model.templete.TempleResponseModel
import com.hmisdoctor.ui.emr_workflow.prescription.model.PrescriptionDurationResponseModel
import com.hmisdoctor.ui.emr_workflow.radiology.model.RecyclerDto
import com.hmisdoctor.ui.emr_workflow.treatment_kit.model.TreatmentKitCreateResponseModel
import com.hmisdoctor.ui.emr_workflow.treatment_kit.model.request.*
import com.hmisdoctor.ui.emr_workflow.treatment_kit.view_model.TreatmentKitSaveOrderViewModelFactory
import com.hmisdoctor.ui.emr_workflow.treatment_kit.view_model.TreatmentSaveOrderKitViewModel

import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SaveOrderTreatmentFragment : DialogFragment() {

    private var deparment_UUID: Int? = 0
    private var content: String? = null
    private var utils: Utils? = null
    private var viewModel: TreatmentSaveOrderKitViewModel? = null
    var binding: DialogSaveOrderTreatmentBinding? = null
    private var facility_UUID: Int? = 0
    private var favAddResponseMap = mutableMapOf<Int, String>()
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    var dropdownReferenceView: AutoCompleteTextView? = null
    private var listDepartmentItems: ArrayList<FavAddResponseContent?> = ArrayList()

    private var selectDepartmentId : Int? = 0
    private var selectStatus : Boolean? = false
    var treatmentCreate : CreateTreatmentkitRequestModel = CreateTreatmentkitRequestModel()

    var appPreferences: AppPreferences? = null

    var mcallback:refreshClickedListener?=null


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
            DataBindingUtil.inflate(inflater, R.layout.dialog_save_order_treatment, container, false)
        viewModel = TreatmentKitSaveOrderViewModelFactory(
            requireActivity().application
        )
            .create(TreatmentSaveOrderKitViewModel::class.java)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this



        userDetailsRoomRepository = UserDetailsRoomRepository(requireActivity().application)
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        facility_UUID = appPreferences?.getInt(AppConstants?.FACILITY_UUID)
        deparment_UUID = appPreferences?.getInt(AppConstants?.DEPARTMENT_UUID)


        binding?.viewModel?.getSaveOrderDepartmentList(
            facility_UUID,
            templateRadiodepartmentRetrofitCallBack
        )

        binding?.closeImageView?.setOnClickListener {
            dialog?.dismiss()
        }

        binding?.actvieFromDateEditText!!.setOnClickListener {
            val c: Calendar = Calendar.getInstance()
            val mYear = c.get(Calendar.YEAR)
            val mMonth = c.get(Calendar.MONTH)
            val mDay = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                activity!!,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                    val c = Calendar.getInstance()
                    val mHour = c[Calendar.HOUR_OF_DAY]
                    val mMinute = c[Calendar.MINUTE]
                    val mSeconds = c[Calendar.SECOND]

                    val timePickerDialog = TimePickerDialog(
                        this.activity,
                        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->

                            val myFormat = "dd-MM-yyyy"
                            val sdf = SimpleDateFormat(myFormat, Locale.US)

                            binding?.actvieFromDateEditText?.setText( String.format(
                                "%02d",
                                dayOfMonth
                            ) + "-" + String.format("%02d", monthOfYear + 1) + "-" + year + " " + hourOfDay + ":" + minute)
                        },
                        mHour!!,
                        mMinute!!,
                        false
                    )
                    timePickerDialog.show()
                }, mYear, mMonth, mDay
            )
            //datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.show()
        }

        binding?.actvieTo!!.setOnClickListener {
            val c: Calendar = Calendar.getInstance()
            val mYear = c.get(Calendar.YEAR)
            val mMonth = c.get(Calendar.MONTH)
            val mDay = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                activity!!,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                    val c = Calendar.getInstance()
                    val mHour = c[Calendar.HOUR_OF_DAY]
                    val mMinute = c[Calendar.MINUTE]
                    val mSeconds = c[Calendar.SECOND]

                    val timePickerDialog = TimePickerDialog(
                        this.activity,
                        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->

                            val myFormat = "dd-MM-yyyy"
                            val sdf = SimpleDateFormat(myFormat, Locale.US)

                            binding?.actvieTo?.setText( String.format(
                                "%02d",
                                dayOfMonth
                            ) + "-" + String.format("%02d", monthOfYear + 1) + "-" + year + " " + hourOfDay + ":" + minute)
                        },
                        mHour!!,
                        mMinute!!,
                        false
                    )
                    timePickerDialog.show()
                }, mYear, mMonth, mDay
            )
            //datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.show()
        }

        binding?.spinnerInvestdepartment!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                val itemValue = parent!!.getItemAtPosition(0).toString()

                selectDepartmentId =
                    favAddResponseMap.filterValues { it == itemValue }.keys.toList()[0]
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long) {
                val itemValue = parent!!.getItemAtPosition(position).toString()

                selectDepartmentId =
                    favAddResponseMap.filterValues { it == itemValue }.keys.toList()[0]
            }
        }

        binding?.switchStatus!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                selectStatus = isChecked
            }else{
                selectStatus = isChecked
            }

        }

        binding?.saveTreatement?.setOnClickListener {

            if(binding?.treatmentCode!!.text.trim().toString().isEmpty()){
                binding?.treatmentCode!!.setError("Enter Treatment Code")
            } else if(binding?.treatmentName!!.text.trim().toString()?.isEmpty()){
                binding?.treatmentName!!.setError("Enter Treatment Name")
            }else if (binding?.actvieFromDateEditText!!.text.toString().isEmpty()){
                binding?.actvieFromDateEditText!!.setError("Select Date")
            }else if(selectDepartmentId == 0){
                Toast.makeText(activity,"Select Department",Toast.LENGTH_LONG).show()
            }else {
                val args = arguments
                if (args != null) {
                    val favouriteDataLab =
                        args.getParcelableArrayList<FavouritesModel>(AppConstants.RESPONSECONTENT)
                    val favouriteDataDiagnosis =
                        args.getParcelableArrayList<FavouritesModel>(AppConstants.DIAGNISISRESPONSECONTENT)
                    val favouriteDataPrescription =
                        args.getParcelableArrayList<FavouritesModel>(AppConstants.PRESCRIPTIONRESPONSECONTENT)
                    val favouriteDataRadiology =
                        args.getParcelableArrayList<FavouritesModel>(AppConstants.RADIOLOGYRESPONSECONTENT)
                    val favouriteDataInvestigation =
                        args.getParcelableArrayList<FavouritesModel>(AppConstants.INVESTIGATIONRESPONSECONTENT)

                    treatmentCreate.treatment_kit!!.name =
                        binding?.treatmentName!!.text.trim().toString()
                    treatmentCreate.treatment_kit!!.code =
                        binding?.treatmentCode!!.text.trim().toString()
                    treatmentCreate.treatment_kit!!.treatment_kit_type_uuid = selectDepartmentId
                    treatmentCreate.treatment_kit!!.department_uuid = deparment_UUID.toString()
                    treatmentCreate.treatment_kit!!.is_public = selectStatus.toString()
                    treatmentCreate.treatment_kit!!.activefrom = binding?.actvieFromDateEditText!!.text.trim().toString()
                    treatmentCreate.treatment_kit!!.is_active = "true"
                    treatmentCreate.treatment_kit!!.user_uuid = userDataStoreBean?.uuid.toString()
                    treatmentCreate.treatment_kit!!.facility_uuid = facility_UUID?.toString()


                    val readtreatmentket: ArrayList<TreatmentKitLab?> = ArrayList()
                    readtreatmentket.clear()
                    for (i in favouriteDataLab!!.indices) {
                        val treatmentKitLab: TreatmentKitLab = TreatmentKitLab()
                        treatmentKitLab.test_master_uuid = favouriteDataLab[i].test_master_id
                        readtreatmentket.add(treatmentKitLab)
                    }

                    treatmentCreate.treatment_kit_lab = readtreatmentket


                    val readTreatmentkitRadiology: ArrayList<TreatmentKitRadiology> = ArrayList()
                    readTreatmentkitRadiology.clear()
                    for(i in favouriteDataRadiology!!.indices){
                        val treatmentRadiology: TreatmentKitRadiology = TreatmentKitRadiology()
                        treatmentRadiology.test_master_uuid = favouriteDataRadiology[i].test_master_id
                        readTreatmentkitRadiology.add(treatmentRadiology)
                    }
                    treatmentCreate.treatment_kit_radiology = readTreatmentkitRadiology


                    val readDiagnosis: ArrayList<TreatmentKitDiagnosis?> = ArrayList()
                    readDiagnosis.clear()
                    for (i in favouriteDataDiagnosis!!.indices) {
                        val treatmentKitDiagnosis: TreatmentKitDiagnosis = TreatmentKitDiagnosis()
                        treatmentKitDiagnosis.diagnosis_uuid =
                            favouriteDataDiagnosis[i].diagnosis_id!!.toInt()
                        readDiagnosis.add(treatmentKitDiagnosis)
                    }

                    treatmentCreate.treatment_kit_diagnosis = readDiagnosis


                    val readInvestigation: ArrayList<TreatmentKitInvestigation?> = ArrayList()
                    readInvestigation.clear()
                    for(i in favouriteDataInvestigation!!.indices){
                        val treatmentkitInvestigationn: TreatmentKitInvestigation = TreatmentKitInvestigation()
                        treatmentkitInvestigationn.test_master_uuid =
                            favouriteDataInvestigation[i].test_master_id
                        readInvestigation.add(treatmentkitInvestigationn)
                    }

                    treatmentCreate.treatment_kit_investigation = readInvestigation


                    val readPrescription: ArrayList<TreatmentKitPrescription?> = ArrayList()
                    readPrescription.clear()
                    for(i in favouriteDataPrescription!!.indices){
                        val treatmentkitPrescription: TreatmentKitPrescription = TreatmentKitPrescription()
                        treatmentkitPrescription.item_master_uuid =
                            favouriteDataPrescription[i].drug_id
                        treatmentkitPrescription.drug_route_uuid =
                            favouriteDataPrescription[i].selectRouteID?.toString()
                        treatmentkitPrescription.drug_frequency_uuid =
                            favouriteDataPrescription[i].selecteFrequencyID.toString()

                        treatmentkitPrescription.duration =
                            favouriteDataPrescription[i].duration

                        treatmentkitPrescription.duration_period_uuid =
                            favouriteDataPrescription[i].PrescriptiondurationPeriodId.toString()

                        treatmentkitPrescription.drug_instruction_uuid =
                            favouriteDataPrescription[i].selectInvestID.toString()

                        treatmentkitPrescription.quantity =
                            favouriteDataPrescription[i].perdayquantityPrescription

                        readPrescription.add(treatmentkitPrescription)
                    }

                    treatmentCreate.treatment_kit_drug = readPrescription


                    Log.e("AddedData", treatmentCreate.toString())

                    viewModel?.createTreatmentKit(
                        facility_UUID,
                        treatmentCreate,
                        createTreatmentRetrofitCallback
                    )

                }
            }
        }

        return binding?.root
    }



    interface refreshClickedListener {
        fun refresh(
        )
    }

    fun setOnRefreshClickedListener(callback: refreshClickedListener) {
        this.mcallback = callback
    }
    val templateRadiodepartmentRetrofitCallBack =
        object : RetrofitCallback<FavAddResponseModel> {
            override fun onSuccessfulResponse(response: Response<FavAddResponseModel>) {
                Log.i("", "" + response.body()?.responseContent);
                Log.i("", "" + response.body()?.responseContent);
                listDepartmentItems.add(response.body()?.responseContent)
                favAddResponseMap = listDepartmentItems.map { it?.uuid!! to it.name!! }!!.toMap().toMutableMap()



                val adapter =
                    ArrayAdapter<String>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        favAddResponseMap.values.toMutableList()
                    )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding?.spinnerInvestdepartment!!.adapter = adapter
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

    val createTreatmentRetrofitCallback = object : RetrofitCallback<TreatmentKitCreateResponseModel>{
        override fun onSuccessfulResponse(responseBody: Response<TreatmentKitCreateResponseModel>?) {


            if(responseBody?.body()?.code == 200){

                //Log.e("CreateTreatment",responseBody?.body()?.message)
                Toast.makeText(activity,responseBody?.body()?.message,Toast.LENGTH_LONG).show()

                mcallback!!.refresh()
                dialog?.dismiss()



            }


        }

        override fun onBadRequest(response: Response<TreatmentKitCreateResponseModel>) {
            val gson = GsonBuilder().create()
            val responseModel: TreatmentKitCreateResponseModel
            try {
                responseModel = gson.fromJson(
                    response.errorBody()!!.string(),
                    TreatmentKitCreateResponseModel::class.java
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


