package com.hmisdoctor.ui.emr_workflow.history.prescription.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences

import com.hmisdoctor.databinding.HistoryPrescriptionChildFragmentBinding
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.duration.DurationResponseContent
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.duration.DurationResponseModel

import com.hmisdoctor.ui.emr_workflow.history.prescription.model.PrescriptionHistoryResponseModel
import com.hmisdoctor.ui.emr_workflow.history.prescription.view_model.HistoryPrescriptionViewModel
import com.hmisdoctor.ui.emr_workflow.history.prescription.view_model.HistoryPrescriptionViewModelFactory
import com.hmisdoctor.ui.emr_workflow.prescription.model.PrevPrescriptionModel
import com.hmisdoctor.utils.Utils
import retrofit2.Response
import java.util.ArrayList


class HistoryPrescriptionChildFragment : Fragment() {
    private var binding: HistoryPrescriptionChildFragmentBinding? = null
    private var viewModel: HistoryPrescriptionViewModel? = null
    private var utils: Utils? = null
    private var patientID: Int? = 0
    private var facilityid: Int? = 0
    var appPreferences: AppPreferences? = null

    private var mAdapter: HistoryPrescriptionParentAdapter? = null
    private var durationArrayList: ArrayList<DurationResponseContent?>? = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.history_prescription_child_fragment,
                container,
                false
            )
        viewModel = HistoryPrescriptionViewModelFactory(
            requireActivity().application
        ).create(HistoryPrescriptionViewModel::class.java)
        binding!!.viewModel = viewModel
        binding!!.lifecycleOwner = this
        utils = Utils(requireContext())
        viewModel!!.errorText.observe(viewLifecycleOwner,
            Observer { toastMessage ->
                utils!!.showToast(R.color.negativeToast, binding!!.mainLayout!!, toastMessage)
            })
        mAdapter = HistoryPrescriptionParentAdapter((requireActivity()))
        binding!!.historyPrescriptionRecyclerView!!.adapter = mAdapter
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        patientID = appPreferences?.getInt(AppConstants.PATIENT_UUID)
        facilityid = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        viewModel?.getPrescription_History(
            patientID,
            facilityid,
            historyPrescriptionrecordsRetrofitCallback
        )
/*
        viewModel?.getDuration(getDurationRetrofitCallBack)
*/




        return binding!!.root
    }

    val historyPrescriptionrecordsRetrofitCallback = object :
        RetrofitCallback<PrescriptionHistoryResponseModel> {
        override fun onSuccessfulResponse(response: Response<PrescriptionHistoryResponseModel>) {

            if (response.body()?.responseContents?.rows!!.isNotEmpty()!!) {
                viewModel?.errorTextVisibility?.value = 8
                mAdapter?.refreshList(response.body()?.responseContents?.rows)
            } else {
                viewModel?.errorTextVisibility?.value = 0
            }

        }

        override fun onBadRequest(response: Response<PrescriptionHistoryResponseModel>) {
            val gson = GsonBuilder().create()
            val responseModel: PrevPrescriptionModel
            try {
                responseModel = gson.fromJson(
                    response.errorBody()!!.string(),
                    PrevPrescriptionModel::class.java
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



/*
    val getDurationRetrofitCallBack =
        object : RetrofitCallback<DurationResponseModel> {
            override fun onSuccessfulResponse(response: Response<DurationResponseModel>) {
                if (response.body()?.responseContents?.isNotEmpty()!!) {
                    if (response.body()?.responseContents?.isNotEmpty()!!) {
*/
/*
                        mAdapter.setDuration(response.body()?.responseContents!!)
*//*


                    }

                }
            }

            override fun onBadRequest(response: Response<DurationResponseModel>) {
                val gson = GsonBuilder().create()
                val responseModel: DurationResponseModel
                try {

                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        DurationResponseModel::class.java
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
                viewModel!!.progressBar.value = 8
            }
        }
*/


}