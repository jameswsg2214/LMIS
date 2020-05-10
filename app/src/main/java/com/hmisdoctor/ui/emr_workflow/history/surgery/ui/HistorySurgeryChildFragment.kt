package com.hmisdoctor.ui.emr_workflow.view.lab.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.hmisdoctor.R
import com.hmisdoctor.utils.Utils

import com.google.gson.GsonBuilder
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.HistorySurguryFragmentBinding
import com.hmisdoctor.ui.emr_workflow.history.surgery.model.response.*
import com.hmisdoctor.ui.emr_workflow.history.surgery.ui.HistorySurgeryAdapter
import com.hmisdoctor.ui.emr_workflow.history.surgery.viewmodel.HistorySurgeryViewModel
import com.hmisdoctor.ui.emr_workflow.history.surgery.viewmodel.HistorySurgeryViewModelFactory
import com.hmisdoctor.ui.emr_workflow.view.lab.model.PrevLabResponseModel
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HistorySurgeryChildFragment : Fragment() {
    private var historySurgeryAdapter: HistorySurgeryAdapter?=null
    private var binding: HistorySurguryFragmentBinding? = null
    private var viewModel: HistorySurgeryViewModel? = null
    private var utils: Utils? = null
    private var institutionNamesList = mutableMapOf<Int, String>()
    private var NamesList = mutableMapOf<Int, String>()
    private  var selectitemid:Int=0
    private  var selectNameitemid:Int=0
    private var patientId: Int = 0
    private var facilityid: Int = 0
    private var mYear: Int? = null
    private var mMonth: Int? = null
    private var mDay: Int? = null
    private var mHour: Int? = null
    private var mMinute: Int? = null
    private val hashNameSpinnerList: HashMap<Int,Int> = HashMap()
    private val hashInstitutionSpinnerList: HashMap<Int,Int> = HashMap()
    private var updateId: Int = 0
    private var uuid: Int = 0

    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    var appPreferences: AppPreferences? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.history_surgury_fragment,
                container,
                false
            )

        viewModel = HistorySurgeryViewModelFactory(
            requireActivity().application
        ).create(HistorySurgeryViewModel::class.java)
        binding?.viewModel = viewModel
        binding!!.lifecycleOwner = this
        utils = Utils(requireContext())
        viewModel!!.errorText.observe(viewLifecycleOwner,
            Observer { toastMessage ->
                utils!!.showToast(R.color.negativeToast, binding?.mainLayout!!, toastMessage)
            })

        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        facilityid = appPreferences?.getInt(AppConstants.FACILITY_UUID)!!
        patientId = appPreferences?.getInt(AppConstants.PATIENT_UUID)!!
        userDetailsRoomRepository = UserDetailsRoomRepository(requireActivity().application)
        var userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        binding?.etFamilyDateTime?.setOnClickListener {

            val c: Calendar = Calendar.getInstance()
            mYear = c.get(Calendar.YEAR)
            mMonth = c.get(Calendar.MONTH)
            mDay = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this!!.activity!!,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                    val c = Calendar.getInstance()
                    mHour = c[Calendar.HOUR_OF_DAY]
                    mMinute = c[Calendar.MINUTE]

                    val timePickerDialog = TimePickerDialog(
                        this!!.activity,
                        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->

                            binding?.etFamilyDateTime?.setText(String.format(
                                "%02d",
                                dayOfMonth
                            ) + "-" +String.format("%02d", monthOfYear + 1) + "-" + year + " " + String.format(
                                "%02d",hourOfDay)+ ":" + String.format(
                                "%02d",minute))
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

        binding?.surguryInstitutionSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                val itemValue=parent!!.getItemAtPosition(0).toString()
                selectitemid=institutionNamesList.filterValues { it == itemValue }.keys.toList()[0]
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                pos: Int,
                id: Long
            ) {

                val itemValue=parent!!.getItemAtPosition(pos).toString()

                selectitemid=institutionNamesList.filterValues { it == itemValue }.keys.toList()[0]

                //Log.e("",""+selectitemid)
            }
        }

        binding?.surguryNameSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                val itemValue=parent!!.getItemAtPosition(0).toString()
                selectNameitemid=NamesList.filterValues { it == itemValue }.keys.toList()[0]
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                pos: Int,
                id: Long
            ) {

                val itemValue=parent!!.getItemAtPosition(pos).toString()

                selectNameitemid=NamesList.filterValues { it == itemValue }.keys.toList()[0]

            }
        }




        binding?.addNewSurgeryIconView!!.setOnClickListener {
            if (binding?.etFamilyDateTime?.text?.trim()!!.isEmpty() || binding?.etRemarks!!.text.trim().isEmpty()
                || selectitemid.equals(0) || selectNameitemid.equals(0)) {
                Toast.makeText(activity, "Enter all fields", Toast.LENGTH_LONG).show()
            } else {

                val jsonBody = JSONObject()
                try {
                    jsonBody.put("facility_uuid", selectitemid.toString())
                    jsonBody.put("patient_uuid", patientId)
                    jsonBody.put("encounter_uuid", 1)
                    jsonBody.put("consultation_uuid", 1)
                    jsonBody.put("procedure_uuid", selectNameitemid.toString())
                    jsonBody.put("surgery_name", "SurgeryMobileApp")
                    jsonBody.put("surgery_description", "SurgeryMobileApp")
                    jsonBody.put("performed_date", "2020-03-20T12:18:45.063Z")
                    jsonBody.put("comments", binding?.etRemarks!!.text.toString())

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                val body = RequestBody.create(
                    okhttp3.MediaType.parse("application/json; charset=utf-8"),
                    jsonBody.toString()
                )

                if(updateId == 0) {
                    viewModel?.createSurgery(
                        this.facilityid!!,
                        body, createSurgeryRetrofitCallback
                    )
                }else{

                    val jsonBody1 = JSONObject()
                    try {
                        jsonBody1.put("facility_uuid", selectitemid.toString())
                        jsonBody1.put("procedure_uuid", selectNameitemid.toString())
                        jsonBody1.put("comments", binding?.etRemarks!!.text.toString())

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    val body1 = RequestBody.create(
                        okhttp3.MediaType.parse("application/json; charset=utf-8"),
                        jsonBody1.toString()
                    )

                    viewModel?.updateSurgery(facilityid,uuid,body1,updateSurgeryRetrofitCallback)
                }

            }
        }

        viewModel!!.getSurgeryInstitutionCallback(userDataStoreBean?.uuid!!,facilityid,surgeryInstitutionRetrofitCallback)

        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.historySurguryRecyclerView!!.layoutManager = linearLayoutManager
        historySurgeryAdapter = HistorySurgeryAdapter(
            activity!!,
            ArrayList()
        )
        binding?.historySurguryRecyclerView!!.adapter = historySurgeryAdapter


        historySurgeryAdapter!!.setOnClickListener(object : HistorySurgeryAdapter.OnClickListener{
            override fun OnClick(responseContent: SurgeryresponseContent?, position: Int) {
                Log.e("SelectSurgeryData",responseContent!!.toString())

                try {
                    binding?.etFamilyDateTime!!.setText(responseContent?.ps_performed_date)
                    binding?.etRemarks!!.setText(responseContent?.ps_comments)

                    /*if (responseContent!!.institution_uuid != null) {

                        val checkperiod = hashInstitutionSpinnerList.any { it!!.key == responseContent!!.institution_uuid }

                        if (checkperiod) {
                            binding?.surguryInstitutionSpinner!!.setSelection(hashInstitutionSpinnerList.get(responseContent!!.institution_uuid)!!)
                        } else {
                            binding?.surguryInstitutionSpinner!!.setSelection(0)
                        }
                    }*/

                   /* binding?.surguryNameSpinner!!.setSelection(
                        hashNameSpinnerList.get(responseContent.ps_patient_uuid!!)!!)*/
                    binding?.addNewSurgeryIconView!!.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            getResources(),
                            R.drawable.ic_update_black_24dp,
                            null
                        )
                    )
                    updateId = 1
                    uuid = responseContent.ps_uuid!!
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

        })

        return binding!!.root
    }

    val surgeryInstitutionRetrofitCallback = object : RetrofitCallback<SurgeryInstitutionResponseModel> {
        override fun onSuccessfulResponse(response: Response<SurgeryInstitutionResponseModel>) {

            //Log.e("InsData", response.body()?.toString())

            setInstitutionValue(response.body()?.responseContents)

            viewModel?.getSurgeryNameCallback(facilityid,surgeryNameRetrofitCallback)

        }

        override fun onBadRequest(response: Response<SurgeryInstitutionResponseModel>) {

            //Log.e("InsData", "badReq")
            val gson = GsonBuilder().create()
            val responseModel: PrevLabResponseModel
            try {
                responseModel = gson.fromJson(
                    response.errorBody()!!.string(),
                    PrevLabResponseModel::class.java
                )
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    response.message()
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
            viewModel!!.progressBar.value = 8
        }
    }

    fun setInstitutionValue(responseContents: List<InstitutionresponseContent?>?) {

        institutionNamesList = responseContents?.map { it?.facility?.uuid!! to it.facility.name!! }!!.toMap().toMutableMap()

        hashInstitutionSpinnerList.clear()

        for(i in responseContents.indices){

            hashInstitutionSpinnerList[responseContents[i]!!.facility!!.uuid!!]=i
        }

        Log.e("Ins",hashInstitutionSpinnerList.toString())

        val adapter = ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_item, institutionNamesList.values.toMutableList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.surguryInstitutionSpinner!!.adapter = adapter

    }

    val surgeryRetrofitCallback = object : RetrofitCallback<HistorySurgeryResponseModel> {
        override fun onSuccessfulResponse(response: Response<HistorySurgeryResponseModel>) {

            //Log.e("SurData", response.body()?.toString())
            if(response.body()?.responseContent?.size == 0) {
                binding?.historySurguryRecyclerView?.visibility = View.GONE
               /* binding?.hideLayout?.visibility = View.GONE
                binding?.hideLayoutSub?.visibility = View.GONE*/
                binding?.noRecordsTextView?.visibility = View.VISIBLE
            }else{
                historySurgeryAdapter?.setData(response?.body()?.responseContent)
                binding?.historySurguryRecyclerView?.visibility = View.VISIBLE
               /* binding?.hideLayout?.visibility = View.VISIBLE
                binding?.hideLayoutSub?.visibility = View.VISIBLE*/
                binding?.noRecordsTextView?.visibility = View.GONE
            }

        }

        override fun onBadRequest(response: Response<HistorySurgeryResponseModel>) {

            //Log.e("SurData", "badReq")
            val gson = GsonBuilder().create()
            val responseModel: PrevLabResponseModel
            try {
                responseModel = gson.fromJson(
                    response.errorBody()!!.string(),
                    PrevLabResponseModel::class.java
                )
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    response.message()
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
            //Log.e("InsData", response.message())
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }

        override fun onUnAuthorized() {
            //Log.e("InsData", "UnAuth")
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
            viewModel!!.progressBar.value = 8
        }
    }



    val surgeryNameRetrofitCallback = object : RetrofitCallback<SurgeryNameResponseModel> {
        override fun onSuccessfulResponse(response: Response<SurgeryNameResponseModel>) {

            //Log.e("nameData", response.body()?.toString())

            setNameValue(response.body()?.responseContents)

            viewModel?.getSurgeryCallback(patientId,facilityid,surgeryRetrofitCallback)

        }

        override fun onBadRequest(response: Response<SurgeryNameResponseModel>) {

           // Log.e("nameData", "badReq")
            val gson = GsonBuilder().create()
            val responseModel: PrevLabResponseModel
            try {
                responseModel = gson.fromJson(
                    response.errorBody()!!.string(),
                    PrevLabResponseModel::class.java
                )
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    response.message()
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
            viewModel!!.progressBar.value = 8
        }
    }


    fun setNameValue(responseContents: List<NameresponseContent?>?) {

        NamesList = responseContents?.map { it!!.uuid!! to it.name!! }!!.toMap().toMutableMap()

        hashNameSpinnerList.clear()

        for(i in responseContents.indices){

            hashNameSpinnerList[responseContents[i]!!.uuid!!]=i
        }

        //Log.e("Name",hashNameSpinnerList.toString())

        val adapter = ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_item, NamesList.values.toMutableList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.surguryNameSpinner!!.adapter = adapter


    }


    val createSurgeryRetrofitCallback = object : RetrofitCallback<CreateSurgeryResponseModel> {
        override fun onSuccessfulResponse(response: Response<CreateSurgeryResponseModel>) {

            //Log.e("createSurData", response.body()?.toString())

            Toast.makeText(activity,response!!.body()?.message,Toast.LENGTH_LONG).show()
            binding?.etFamilyDateTime!!.setText("")
            binding?.etRemarks!!.setText("")

            if(response?.body()?.code == 200) {
                viewModel?.getSurgeryCallback(patientId, facilityid, surgeryRetrofitCallback)
            }

        }

        override fun onBadRequest(response: Response<CreateSurgeryResponseModel>) {

            //Log.e("createSurData", "badReq")
            val gson = GsonBuilder().create()
            val responseModel: PrevLabResponseModel
            try {
                responseModel = gson.fromJson(
                    response.errorBody()!!.string(),
                    PrevLabResponseModel::class.java
                )
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    response.message()
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
            viewModel!!.progressBar.value = 8
        }
    }

    val updateSurgeryRetrofitCallback = object  : RetrofitCallback<SurgeryUpdateResponseModel>{
        override fun onSuccessfulResponse(response: Response<SurgeryUpdateResponseModel>) {

            //Log.e("updateSurgData", response.body()?.toString())

            Toast.makeText(activity,response!!.body()?.message,Toast.LENGTH_LONG).show()
            binding?.etFamilyDateTime!!.setText("")
            binding?.etRemarks!!.setText("")
            if(response?.body()?.code == 200) {
                viewModel?.getSurgeryCallback(patientId, facilityid, surgeryRetrofitCallback)
            }

        }

        override fun onBadRequest(response: Response<SurgeryUpdateResponseModel>) {

            val gson = GsonBuilder().create()
            val responseModel: SurgeryUpdateResponseModel
            try {
                responseModel = gson.fromJson(
                    response.errorBody()!!.string(),
                    SurgeryUpdateResponseModel::class.java
                )
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    response.message()
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
            viewModel!!.progressBar.value = 8
        }
    }

}


