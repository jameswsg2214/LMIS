package com.hmisdoctor.ui.emr_workflow.lab_result.ui


import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
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
import com.hmisdoctor.databinding.LabResultChildFragmentBinding
import com.hmisdoctor.ui.emr_workflow.chief_complaint.view_model.LabResultViewModelFactory
import com.hmisdoctor.ui.emr_workflow.lab_result.model.LabResultResponseContent
import com.hmisdoctor.ui.emr_workflow.lab_result.model.LabResultResponseModel
import com.hmisdoctor.ui.emr_workflow.lab_result.view_model.LabResultViewModel
import com.hmisdoctor.utils.Utils
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class LabResultChildFragment : Fragment() {
    private var Counter = 1
    var startDate: String? = null
    var endDate: String? = null
    private var binding: LabResultChildFragmentBinding? = null
    private var viewModel: LabResultViewModel? = null
    var cal = Calendar.getInstance()
    private var utils: Utils? = null
    var appPreferences: AppPreferences? = null
    private var mAdapter: LabResultParentAdapter? = null
    private val labResultLIst: ArrayList<LabResultResponseContent> = ArrayList()
    private var fromCalenderDateSetListener: OnDateSetListener? = null
    private var toCalenderDateSetListener: OnDateSetListener? = null
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
                R.layout.lab_result_child_fragment,
                container,
                false
            )

        viewModel = LabResultViewModelFactory(
            requireActivity().application
        ).create(LabResultViewModel::class.java)
        binding?.viewModel = viewModel
        binding!!.lifecycleOwner = this
        utils = Utils(requireContext())

        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        val patientID = appPreferences?.getInt(AppConstants.PATIENT_UUID)
        val facilityid = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        viewModel?.getLab_Result(253, facilityid, labResultRetrofitCallback)

        binding?.calendarEditText?.setOnClickListener {
            showFromDatePickerDialog()
        }

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        binding?.LabResultRecyclerView!!.layoutManager = layoutManager
        mAdapter = LabResultParentAdapter(requireContext(), labResultLIst)
        binding?.LabResultRecyclerView!!.adapter = mAdapter


        fromCalenderDateSetListener =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                fromCalender.set(Calendar.YEAR, year)
                fromCalender.set(Calendar.MONTH, monthOfYear)
                fromCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                setFromDate()
            }

        toCalenderDateSetListener =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                toCalender.set(Calendar.YEAR, year)
                toCalender.set(Calendar.MONTH, monthOfYear)
                toCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                setToDate()
            }

        binding?.nextCardView?.setOnClickListener {
            replaceFragment(LabResultTabFragment())
              viewModel?.getLab_Result(patientID, facilityid, labResultRetrofitCallback)

        }
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
        binding?.calendarEditText?.setText(startDate + "~" + endDate)
    }


    val labResultRetrofitCallback = object : RetrofitCallback<LabResultResponseModel> {
        override fun onSuccessfulResponse(response: Response<LabResultResponseModel>) {

            if (response.body()?.responseContents?.isNotEmpty()!!) {
                viewModel?.errorTextVisibility?.value = 8
                mAdapter!!.setData(response.body()?.responseContents as ArrayList<LabResultResponseContent>)

            } else {
                viewModel?.errorTextVisibility?.value = 0
            }

        }

        override fun onBadRequest(response: Response<LabResultResponseModel>) {
            val gson = GsonBuilder().create()
            val responseModel: LabResultResponseModel
            try {
                responseModel = gson.fromJson(
                    response.errorBody()!!.string(),
                    LabResultResponseModel::class.java
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

    private fun replaceFragment(
        fragment: Fragment
    ) {
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.labRestultfragmentContainer, fragment)
        fragmentTransaction?.addToBackStack(null)
        fragmentTransaction?.commit()
    }
}






