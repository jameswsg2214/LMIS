package com.hmisdoctor.ui.emr_workflow.radiology_result.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.RadiologyResultChildFragmentBinding
import com.hmisdoctor.ui.emr_workflow.chief_complaint.view_model.RadiologyResultViewModelFactory
import com.hmisdoctor.ui.emr_workflow.lab_result.model.LabResultResponseContent
import com.hmisdoctor.ui.emr_workflow.lab_result.model.SampleParentResponse
import com.hmisdoctor.ui.emr_workflow.radiology_result.model.RadilogyResultResponseContent
import com.hmisdoctor.ui.emr_workflow.radiology_result.model.RadiologyResultResponseModel
import com.hmisdoctor.ui.emr_workflow.radiology_result.view_model.RadiologyResultViewModel
import com.hmisdoctor.utils.Utils
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class RadiologyResultListViewFragment : Fragment() {
    private var dateDialog: DatePickerDialog?=null
    private var Counter=1
    var startDate: String? = null
    var endDate: String? = null

    private var binding: RadiologyResultChildFragmentBinding? = null
    private var viewModel: RadiologyResultViewModel? = null
    var cal = Calendar.getInstance()
    private var utils: Utils? = null
    var appPreferences : AppPreferences?=null
    private var mAdapter: RadiologyResultParentAdapter? = null
    private var mChildAdapter: RadiologyResultChildAdapter? = null

    private val labResultLIst: ArrayList<SampleParentResponse> = ArrayList()
    private val RadiologyResultLIst: ArrayList<LabResultResponseContent> = ArrayList()

    private var fromCalenderDateSetListener: DatePickerDialog.OnDateSetListener? = null
    private var toCalenderDateSetListener: DatePickerDialog.OnDateSetListener? = null
    private val fromCalender = Calendar.getInstance()
    private val toCalender = Calendar.getInstance()




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.radiology_result_child_fragment,
                container,
                false
            )

        viewModel = RadiologyResultViewModelFactory(
            requireActivity().application
        ).create(RadiologyResultViewModel::class.java)
        binding?.viewModel = viewModel
        binding!!.lifecycleOwner = this
        utils = Utils(requireContext())

        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        val patientID = appPreferences?.getInt(AppConstants.PATIENT_UUID)
        val facilityid = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        viewModel?.getRadiology_Result(patientID,facilityid,radiologyResultRetrofitCallback)

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        binding?.LabResultRecyclerView!!.layoutManager = layoutManager
        mAdapter = RadiologyResultParentAdapter(requireContext(), ArrayList())
        binding?.LabResultRecyclerView!!.adapter = mAdapter
/*
        prepareChildData()
*/

        binding?.calendarEditText?.setOnClickListener {
            showFromDatePickerDialog()
        }



        fromCalenderDateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                fromCalender.set(Calendar.YEAR, year)
                fromCalender.set(Calendar.MONTH, monthOfYear)
                fromCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                setFromDate()
            }

        toCalenderDateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                toCalender.set(Calendar.YEAR, year)
                toCalender.set(Calendar.MONTH, monthOfYear)
                toCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                setToDate()
            }
        //adapter setting




        return binding!!.root
    }

    /**
     * Show From Date picker dialog
     */
    private fun showFromDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(), R.style.TimePickerTheme,
            fromCalenderDateSetListener, calendar[Calendar.YEAR], calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    /**
     * Show To Date picker dialog
     */
    private fun showToDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.TimePickerTheme,
            toCalenderDateSetListener,
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis
        datePickerDialog.show()
    }
    /**
     * Setting from date
     */
    private fun setFromDate() {
        val dateMonthAndYear =
            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        startDate = dateMonthAndYear.format(fromCalender.getTime())

        showToDatePickerDialog()
    }

    /**
     * Setting to date
     */
    private fun setToDate() {
        val dateMonthAndYear =
            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        endDate = dateMonthAndYear.format(toCalender.getTime())
        binding?.calendarEditText?.setText(startDate +"~"+endDate)
    }




    val radiologyResultRetrofitCallback = object : RetrofitCallback<RadiologyResultResponseModel> {
        override fun onSuccessfulResponse(response: Response<RadiologyResultResponseModel>) {

            if (response.body()?.responseContents?.isNotEmpty()!!) {
                viewModel?.errorTextVisibility?.value = 8
                mAdapter!!.setData(response.body()?.responseContents as ArrayList<RadilogyResultResponseContent>)

/*
                mAdapter?.refreshList(response.body()?.responseContents!!)
*/

            } else {
                viewModel?.errorTextVisibility?.value = 0
            }

        }

        override fun onBadRequest(response: Response<RadiologyResultResponseModel>) {
            val gson = GsonBuilder().create()
            val responseModel: RadiologyResultResponseModel
            try {
                responseModel = gson.fromJson(
                    response.errorBody()!!.string(),
                    RadiologyResultResponseModel::class.java
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
            viewModel!!.progress.value = 8
        }
    }






}






