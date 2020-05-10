package com.hmisdoctor.ui.emr_workflow.view.lab.ui

import android.os.Bundle
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
import com.hmisdoctor.databinding.HistoryLabFragmentBinding
import com.hmisdoctor.ui.emr_workflow.history.lab.ui.HistoryLabAdapter
import com.hmisdoctor.ui.emr_workflow.history.lab.viewmodel.HistoryLabViewModel
import com.hmisdoctor.ui.emr_workflow.history.lab.viewmodel.HistoryLabViewModelFactory

import com.hmisdoctor.ui.emr_workflow.view.lab.model.PrevLabResponseContent
import com.hmisdoctor.ui.emr_workflow.view.lab.model.PrevLabResponseModel
import retrofit2.Response


class HistoryLabChildFragment : Fragment() {
    private var historyLabAdapter: HistoryLabAdapter?=null
    private var binding: HistoryLabFragmentBinding? = null
    private var viewModel: HistoryLabViewModel? = null
    private var utils: Utils? = null

    private var historyRadiologyArrayList: List<PrevLabResponseContent?>? = ArrayList()

    var appPreferences: AppPreferences? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.history_lab_fragment,
                container,
                false
            )

        viewModel = HistoryLabViewModelFactory(
            requireActivity().application
        ).create(HistoryLabViewModel::class.java)
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


        viewModel!!.getHistoryLabCallback(patientID.toString(),facilityid,prevlabrecordsRetrofitCallback)

        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.historyLabRecyclerView!!.layoutManager = linearLayoutManager
        historyLabAdapter = HistoryLabAdapter(
            activity!!,
            ArrayList()
        )
        binding?.historyLabRecyclerView!!.adapter = historyLabAdapter



        return binding!!.root
    }

    val prevlabrecordsRetrofitCallback = object : RetrofitCallback<PrevLabResponseModel> {
        override fun onSuccessfulResponse(response: Response<PrevLabResponseModel>) {

            //Log.e("HisLabData", response.body()?.responseContents!!.size.toString())

            if(response.body()?.responseContents?.size == 0) {
                binding?.historyLabRecyclerView?.visibility = View.GONE
                binding?.hideLayout?.visibility = View.GONE
                binding?.noRecordsTextView?.visibility = View.VISIBLE
            }else{
                historyLabAdapter?.setData(response.body()?.responseContents)
                binding?.historyLabRecyclerView?.visibility = View.VISIBLE
                binding?.hideLayout?.visibility = View.VISIBLE
                binding?.noRecordsTextView?.visibility = View.GONE
            }
        }
        override fun onBadRequest(response: Response<PrevLabResponseModel>) {

            //Log.e("HisLabData", "badReq")
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
            //Log.e("HisLabData", response.message())
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }

        override fun onUnAuthorized() {
            //Log.e("HisRadData", "UnAuth")
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.unauthorized)
            )
        }

        override fun onForbidden() {
            //Log.e("HisRadData", "Forbidden")
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


