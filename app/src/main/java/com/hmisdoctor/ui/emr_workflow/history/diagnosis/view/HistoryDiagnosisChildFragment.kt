package com.hmisdoctor.ui.emr_workflow.history.diagnosis.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.callbacks.RetrofitMainCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.HistoryDiagnosisChildfragmentBinding
import com.hmisdoctor.ui.emr_workflow.history.diagnosis.model.*
import com.hmisdoctor.ui.emr_workflow.history.diagnosis.viewModel.HistoryDiagnosisViewModel
import com.hmisdoctor.ui.emr_workflow.history.diagnosis.viewModel.HistoryDiagnosisViewModelFactory
import com.hmisdoctor.utils.Utils
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class HistoryDiagnosisChildFragment :Fragment(){


    var binding:HistoryDiagnosisChildfragmentBinding?= null

    var viewModel:HistoryDiagnosisViewModel?=null
    private var appPreferences : AppPreferences?= null
    private var facility_id:Int?=null
    private var patient_id:Int?=null
    private var department_uuid:Int?=null
    private  var utils:Utils?=null
    private  var historyDiagnosisAdapter:HistoryDiagnosisAdapter?=null
    private var mYear: Int? = null
    private var mMonth: Int? = null
    private var mDay: Int? = null
    private var mHour: Int? = null
    private var mMinute: Int? = null
    private var mSeconds: Int? = null
    private var diagSearchList = mutableMapOf<Int, String>()
    private var selectSearchUuid: Int = 0
    private var updateId: Int = 0
    private var patient_diagnosis_id: Int = 0
    private var reqDate : String = ""
    private var reqTime: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.history_diagnosis_childfragment, container, false)


        viewModel=HistoryDiagnosisViewModelFactory(
            requireActivity().application
        ).create(HistoryDiagnosisViewModel::class.java)

        binding!!.viewModel=viewModel

        binding!!.lifecycleOwner=this

        utils= Utils(this.context!!)

        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        patient_id = appPreferences?.getInt(AppConstants.PATIENT_UUID)
        department_uuid = appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)
        val encounter_uuid = appPreferences?.getInt(AppConstants.ENCOUNTER_UUID)


        historyDiagnosisAdapter=HistoryDiagnosisAdapter(activity!!,
            ArrayList()
        )

        binding?.diagnosisRecyclerView!!.layoutManager = LinearLayoutManager(context);

        binding?.diagnosisRecyclerView!!.setHasFixedSize(true)

        binding!!.diagnosisRecyclerView.adapter=historyDiagnosisAdapter

        viewModel!!.getlist(facility_id,patient_id,department_uuid,getallListRetrofitCallback)

        binding?.etDiagnosisDateTime?.setOnClickListener {

            val c: Calendar = Calendar.getInstance()
            mYear = c.get(Calendar.YEAR)
            mMonth = c.get(Calendar.MONTH)
            mDay = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this!!.activity!!,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                    val c1 = Calendar.getInstance()
                    mHour = c1[Calendar.HOUR_OF_DAY]
                    mMinute = c1[Calendar.MINUTE]
                    mSeconds = c1[Calendar.SECOND]

                    val timePickerDialog = TimePickerDialog(
                        this!!.activity,
                        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->

                            reqDate = (year.toString()+"-" +String.format("%02d", monthOfYear + 1) + "-" +String.format(
                                "%02d",
                                dayOfMonth)+"T"+String.format("%02d",hourOfDay)+":"+String.format("%02d",minute)+":"+String.format("%02d",mSeconds)+".000Z")

                            Log.e("formDate",reqDate)

                            binding?.etDiagnosisDateTime?.setText(String.format(
                                "%02d",
                                dayOfMonth
                            ) + "-" +String.format("%02d", monthOfYear + 1) + "-" + year + " " + String.format(
                                "%02d",hourOfDay)+ ":" + String.format(
                                "%02d",minute) + ":"+String.format(
                                    "%02d",mSeconds))
                        },
                        mHour!!,
                        mMinute!!,
                        false
                    )
                    timePickerDialog.show()
                }, mYear!!, mMonth!!, mDay!!
            )
            datePickerDialog.show()
        }

        binding!!.etDiagnosisName.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.length > 2 && s.length<5) {

                    viewModel?.getComplaintSearchResult(facility_id,s.toString(), getDignosisSearchRetrofitCallBack)

                }
            }
        })

        binding?.etDiagnosisName!!.onItemClickListener = object  : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val itemValue = parent!!.getItemAtPosition(position).toString()

                selectSearchUuid = diagSearchList.filterValues { it == itemValue }.keys.toList()[0]

                Log.e("selectSearchUuid",selectSearchUuid.toString())
            }

        }

        binding?.addNewDiagnosisIconView!!.setOnClickListener {
            if(binding?.etDiagnosisDateTime!!.text?.trim()!!.isEmpty() || binding?.etDiagnosisName!!.text.trim().isEmpty() || binding?.etDiagnosisRemarks!!.text.trim().isEmpty()){
                Toast.makeText(activity, "Enter all fields", Toast.LENGTH_LONG).show()
            }else{

                if(updateId == 0) {
                    val jsonBody = JSONObject()
                    val jsonArray = JSONArray()
                    try {
                        jsonBody.put("facility_uuid", facility_id.toString())
                        jsonBody.put("department_uuid", department_uuid.toString())
                        jsonBody.put("patient_uuid", patient_id.toString())
                        jsonBody.put("encounter_uuid", encounter_uuid)
                        jsonBody.put("encounter_type_uuid", "1")
                        jsonBody.put("consultation_uuid", 0)
                        jsonBody.put("diagnosis_uuid", selectSearchUuid)
                        jsonBody.put("condition_type_uuid", 0)
                        jsonBody.put("condition_status_uuid", 0)
                        jsonBody.put("category_uuid", 0)
                        jsonBody.put("type_uuid", 0)
                        jsonBody.put("grade_uuid", 0)
                        jsonBody.put("body_site_uuid", 0)
                        jsonBody.put("prescription_uuid", 0)
                        jsonBody.put("patient_treatment_uuid", 0)
                        jsonBody.put("comments", binding?.etDiagnosisRemarks!!.text.trim().toString())
                        jsonBody.put("tat_start_time", reqDate)
                        jsonBody.put("tat_end_time", reqDate)

                        jsonArray.put(jsonBody)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    val body = RequestBody.create(
                        okhttp3.MediaType.parse("application/json; charset=utf-8"),
                        jsonArray.toString()
                    )

                    Log.e("createDiagnosisData", jsonArray.toString())

                    viewModel?.postDiagnosisData(
                        this.facility_id!!,
                        body, createDiagnosisHistoryRestrofitCallback
                    )
                }else{

                    val jsonUpdateArray = JSONObject()

                    jsonUpdateArray.put("encounter_uuid","6674")
                    jsonUpdateArray.put("encounter_type_uuid","1")
                    jsonUpdateArray.put("consultation_uuid","0")
                    jsonUpdateArray.put("diagnosis_uuid",selectSearchUuid)
                    jsonUpdateArray.put("condition_type_uuid",0)
                    jsonUpdateArray.put("condition_status_uuid",0)
                    jsonUpdateArray.put("category_uuid",0)
                    jsonUpdateArray.put("type_uuid",0)
                    jsonUpdateArray.put("grade_uuid",0)
                    jsonUpdateArray.put("prescription_uuid",0)
                    jsonUpdateArray.put("patient_treatment_uuid",0)
                    jsonUpdateArray.put("comments", binding?.etDiagnosisRemarks!!.text.trim().toString())
                    jsonUpdateArray.put("tat_start_time", reqDate)
                    jsonUpdateArray.put("tat_end_time", reqDate)

                    val updateBody = RequestBody.create(
                        okhttp3.MediaType.parse("application/json; charset=utf-8"),
                        jsonUpdateArray.toString()
                    )

                    Log.e("UpdateDiagnosisData", jsonUpdateArray.toString())

                    viewModel?.updateDiagnosisData(facility_id!!,patient_diagnosis_id,patient_id,
                    department_uuid,updateBody,updateDiagnosisRetrofitCallBack)

                }
            }
        }

        historyDiagnosisAdapter?.setOnClickListener(object : HistoryDiagnosisAdapter.OnClickListener{
            override fun OnClick(responseContent: HistoryDiagnosisresponseContent?, position: Int) {

                binding?.etDiagnosisDateTime!!.setText(responseContent!!.diagnosis_created_date)
                binding?.etDiagnosisRemarks!!.setText(responseContent.diagnosis_comments)
                binding?.etDiagnosisName!!.setText(responseContent.diagnosis_name)
                updateId = 1
                selectSearchUuid = responseContent.diagnosis_uuid!!
                patient_diagnosis_id = responseContent.patient_diagnosis_id!!

                binding?.addNewDiagnosisIconView!!.setImageResource(R.drawable.ic_update_black_24dp)
            }

        })

        return binding!!.root
    }

    val getallListRetrofitCallback= object:RetrofitCallback<HistoryDiagnosisResponseModel>{
        override fun onSuccessfulResponse(responseBody: Response<HistoryDiagnosisResponseModel>?) {
            historyDiagnosisAdapter!!.pushAll(responseBody!!.body()!!.responseContents as ArrayList<HistoryDiagnosisresponseContent>?)
        }

        override fun onBadRequest(errorBody: Response<HistoryDiagnosisResponseModel>?) {
            //Log.e("DataHistory", "badRequest")
            val gson = GsonBuilder().create()
            val responseModel: HistoryDiagnosisResponseModel
            try {
                responseModel = gson.fromJson(
                    errorBody!!.errorBody()!!.string(),
                    HistoryDiagnosisResponseModel::class.java
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

        override fun onServerError(response: Response<*>?) {
            // Log.e("DataHistory", "servererr")
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }

        override fun onUnAuthorized() {
            //Log.e("DataHistory", "unAuth")
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.unauthorized)
            )
        }

        override fun onForbidden() {
            //Log.e("DataHistory", "forbidden")
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }

        override fun onFailure(failure: String?) {
            //Log.e("DataHistory", "failure")
            utils?.showToast(R.color.negativeToast, binding?.mainLayout!!, failure!!)
        }

        override fun onEverytime() {
            //Log.e("DataHistory", "everytime")
            viewModel!!.progress.value = 8
        }
    }


    fun setAdapter(responseContents: List<DiagresponseContent?>?) {

        diagSearchList = responseContents!!.map { it?.uuid!! to it.name!! }!!.toMap().toMutableMap()

        val responseContentAdapter = ArrayAdapter(
            this.context!!,
            android.R.layout.simple_spinner_dropdown_item,
            diagSearchList.values.toMutableList()
        )
        binding!!.etDiagnosisName.threshold = 1
        binding!!.etDiagnosisName.setAdapter(responseContentAdapter)
        /*binding!!.etDiagnosisName.setOnItemClickListener { parent, _, position, id ->
            val selectedPoi = parent.adapter.getItem(position) as DiagnosisresponseContent?
            binding!!.etDiagnosisName.setText(selectedPoi?.name)
        }*/
    }


    var getDignosisSearchRetrofitCallBack=object :RetrofitCallback<DiagnosisSearchResponseModel>{
        override fun onSuccessfulResponse(response: Response<DiagnosisSearchResponseModel>?) {

            if (response?.body()?.responseContents?.isNotEmpty()!!) {
                setAdapter(response.body()?.responseContents)
            }

        }

        override fun onBadRequest(response: Response<DiagnosisSearchResponseModel>) {
            val gson = GsonBuilder().create()
            val responseModel: DiagnosisSearchResponseModel
            try {
                responseModel = gson.fromJson(
                    response.errorBody()!!.string(),
                    DiagnosisSearchResponseModel::class.java
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

    val createDiagnosisHistoryRestrofitCallback =
        object : RetrofitCallback<HistoryDiagnosisCreateResponseModel> {
            override fun onSuccessfulResponse(responseBody: Response<HistoryDiagnosisCreateResponseModel>?) {

                Log.e("CreateDiagnosisResponse", responseBody?.body().toString())

                Toast.makeText(activity,responseBody!!.body()?.message,Toast.LENGTH_LONG).show()
                binding?.etDiagnosisDateTime!!.setText("")
                binding?.etDiagnosisRemarks!!.setText("")
                binding?.etDiagnosisName!!.setText("")

                viewModel!!.getlist(facility_id,patient_id,department_uuid,getallListRetrofitCallback)

            }

            override fun onBadRequest(errorBody: Response<HistoryDiagnosisCreateResponseModel>?) {
                //Log.e("DataHistory", "badRequest")
                val gson = GsonBuilder().create()
                val responseModel: HistoryDiagnosisCreateResponseModel
                try {
                    responseModel = gson.fromJson(
                        errorBody!!.errorBody()!!.string(),
                        HistoryDiagnosisCreateResponseModel::class.java
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

            override fun onServerError(response: Response<*>?) {
                //Log.e("DataHistory", "servererr")
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    getString(R.string.something_went_wrong)
                )
            }

            override fun onUnAuthorized() {
                //Log.e("DataHistory", "unAuth")
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    getString(R.string.unauthorized)
                )
            }

            override fun onForbidden() {
                //Log.e("DataHistory", "forbidden")
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    getString(R.string.something_went_wrong)
                )
            }

            override fun onFailure(failure: String?) {
                //Log.e("DataHistory", "failure")
                utils?.showToast(R.color.negativeToast, binding?.mainLayout!!, failure!!)
            }

            override fun onEverytime() {
                // Log.e("DataHistory", "everytime")
                viewModel!!.progress.value = 8
            }

        }

    val updateDiagnosisRetrofitCallBack = object : RetrofitCallback<HistoryDiagnosisUpdateResponseModel>{
        override fun onSuccessfulResponse(responseBody: Response<HistoryDiagnosisUpdateResponseModel>?) {


            if(responseBody?.body()?.code == 200) {
                Toast.makeText(activity, "Diagnosis Updated Successfully", Toast.LENGTH_LONG).show()
                viewModel!!.getlist(facility_id,patient_id,department_uuid,getallListRetrofitCallback)
            }

        }

        override fun onBadRequest(errorBody: Response<HistoryDiagnosisUpdateResponseModel>?) {
            //Log.e("DataHistory", "badRequest")
            val gson = GsonBuilder().create()
            val responseModel: HistoryDiagnosisUpdateResponseModel
            try {
                responseModel = gson.fromJson(
                    errorBody!!.errorBody()!!.string(),
                    HistoryDiagnosisUpdateResponseModel::class.java
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

        override fun onServerError(response: Response<*>?) {
            //Log.e("DataHistory", "servererr")
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }

        override fun onUnAuthorized() {
            //Log.e("DataHistory", "unAuth")
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.unauthorized)
            )
        }

        override fun onForbidden() {
            //Log.e("DataHistory", "forbidden")
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }

        override fun onFailure(failure: String?) {
            //Log.e("DataHistory", "failure")
            utils?.showToast(R.color.negativeToast, binding?.mainLayout!!, failure!!)
        }

        override fun onEverytime() {
            // Log.e("DataHistory", "everytime")
            viewModel!!.progress.value = 8
        }

    }
}