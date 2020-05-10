package com.hmisdoctor.ui.emr_workflow.view.lab.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.hmisdoctor.databinding.HistoryVitalsFragmentBinding
import com.hmisdoctor.ui.emr_workflow.history.vitals.model.response.HistoryVitalResponseContent
import com.hmisdoctor.ui.emr_workflow.history.vitals.model.response.HistoryVitalsResponseModel
import com.hmisdoctor.ui.emr_workflow.history.vitals.model.ui.HistoryVitalsAdapter
import com.hmisdoctor.ui.emr_workflow.history.vitals.model.viewmodel.HistoryVitalsViewModel
import com.hmisdoctor.ui.emr_workflow.history.vitals.model.viewmodel.HistoryVitalsViewModelFactory
import com.hmisdoctor.ui.emr_workflow.view.lab.model.PrevLabResponseModel
import retrofit2.Response


class HistoryVitalsChildFragment : Fragment() {
    private var binding: HistoryVitalsFragmentBinding? = null
    private var viewModel: HistoryVitalsViewModel? = null
    private var utils: Utils? = null
    private var historyVitalsAdapter: HistoryVitalsAdapter?=null
    private var historyVitalsArrayList: List<HistoryVitalResponseContent?>? = ArrayList()

    var appPreferences: AppPreferences? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.history_vitals_fragment,
                container,
                false
            )

        viewModel = HistoryVitalsViewModelFactory(
            requireActivity().application
        ).create(HistoryVitalsViewModel::class.java)
        binding?.viewModel = viewModel
        binding!!.lifecycleOwner = this
        utils = Utils(requireContext())
        viewModel!!.errorText.observe(viewLifecycleOwner,
            Observer { toastMessage ->
                utils!!.showToast(R.color.negativeToast, binding?.mainLayout!!, toastMessage)
            })
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        val patientID = appPreferences?.getInt(AppConstants.PATIENT_UUID)
        val facilityid = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        val departmentid = appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)


        viewModel!!.getHistoryVitalsList(facilityid!!,patientID!!,departmentid!!,historyVitalsRetrofitCallback)


        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.historyVitalsRecyclerView!!.layoutManager = linearLayoutManager
        historyVitalsAdapter = HistoryVitalsAdapter(
            activity!!,
            ArrayList()
        )
        binding?.historyVitalsRecyclerView!!.adapter = historyVitalsAdapter

        return binding!!.root
    }

    val historyVitalsRetrofitCallback = object : RetrofitCallback<HistoryVitalsResponseModel> {
        override fun onSuccessfulResponse(response: Response<HistoryVitalsResponseModel>) {

            //Log.e("HisVitalsData", response.body().toString())

            if(response.body()?.responseContents?.size == 0) {
                binding?.historyVitalsRecyclerView?.visibility = View.GONE
                binding?.hideLayout?.visibility = View.GONE
                binding?.noRecordsTextView?.visibility = View.VISIBLE
            }else{
                historyVitalsAdapter?.setData(response.body()?.responseContents)
                binding?.historyVitalsRecyclerView?.visibility = View.VISIBLE
                binding?.hideLayout?.visibility = View.VISIBLE
                binding?.noRecordsTextView?.visibility = View.GONE
            }


        }

        override fun onBadRequest(response: Response<HistoryVitalsResponseModel>) {

            Log.e("HisRadData", "badReq")
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
            Log.e("HisRadData", response.message())
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }

        override fun onUnAuthorized() {
            Log.e("HisRadData", "UnAuth")
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


