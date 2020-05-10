package com.hmisdoctor.ui.emr_workflow.view.lab.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.hmisdoctor.R
import com.hmisdoctor.utils.Utils

import com.google.gson.GsonBuilder
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.HistoryRadiologyFragmentBinding
import com.hmisdoctor.ui.emr_workflow.history.radiology.ui.HistoryRadiologyAdapter
import com.hmisdoctor.ui.emr_workflow.history.radiology.viewmodel.HistoryRadiologyViewModel
import com.hmisdoctor.ui.emr_workflow.history.radiology.viewmodel.HistoryRadiologyViewModelFactory
import com.hmisdoctor.ui.emr_workflow.view.lab.model.PrevLabResponseContent
import com.hmisdoctor.ui.emr_workflow.view.lab.model.PrevLabResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.ui.PrevLabParentAdapter
import retrofit2.Response


class HistoryRadiologyChildFragment : Fragment() {
    private var binding: HistoryRadiologyFragmentBinding? = null
    private var viewModel: HistoryRadiologyViewModel? = null
    private var utils: Utils? = null
    private var patientUuid: Int = 0
    private var mAdapter: PrevLabParentAdapter? = null

    private var historyRadiologyAdapter: HistoryRadiologyAdapter?=null

    private var historyRadiologyArrayList: List<PrevLabResponseContent?>? = ArrayList()

    var appPreferences: AppPreferences? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.history_radiology_fragment,
                container,
                false
            )

        viewModel = HistoryRadiologyViewModelFactory(
            requireActivity().application
        ).create(HistoryRadiologyViewModel::class.java)
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


        viewModel!!.getHistoryRadiologyCallback(patientID,facilityid,prevlabrecordsRetrofitCallback)


        historyRadiologyAdapter = HistoryRadiologyAdapter(
            activity!!,
            ArrayList()
        )
        binding?.historyRadilogyRecyclerView!!.adapter = historyRadiologyAdapter

        return binding!!.root
    }

    val prevlabrecordsRetrofitCallback = object : RetrofitCallback<PrevLabResponseModel> {
        override fun onSuccessfulResponse(response: Response<PrevLabResponseModel>) {

            //Log.e("HisRadData", response.body()?.toString())



            if(response.body()?.responseContents?.size == 0) {
                binding?.historyRadilogyRecyclerView?.visibility = View.GONE
                binding?.hideLayout?.visibility = View.GONE
                binding?.noRecordsTextView?.visibility = View.VISIBLE
            }else{
                historyRadiologyAdapter?.setData(response.body()?.responseContents!!)
                binding?.historyRadilogyRecyclerView?.visibility = View.VISIBLE
                binding?.hideLayout?.visibility = View.VISIBLE
                binding?.noRecordsTextView?.visibility = View.GONE
            }
        }

        override fun onBadRequest(response: Response<PrevLabResponseModel>) {

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
            viewModel!!.progressBar.value = 8
        }
    }

}


