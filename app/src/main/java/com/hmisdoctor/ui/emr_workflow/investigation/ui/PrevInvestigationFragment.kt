package com.hmisdoctor.ui.emr_workflow.view.lab.ui

import android.os.Bundle
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
import com.hmisdoctor.databinding.PrevInvestigationFragmentBinding
import com.hmisdoctor.ui.emr_workflow.investigation.model.InvestigationPrevResponseModel
import com.hmisdoctor.ui.emr_workflow.view.lab.view_model.PreviewInvestigationViewModel
import com.hmisdoctor.ui.emr_workflow.view.lab.view_model.PreviewInvestigationViewModelFactory
import com.makkalnalam.ui.Expandable.PrevInvestigationParentAdapter
import retrofit2.Response


class PrevInvestigationFragment : Fragment() {
    private var binding: PrevInvestigationFragmentBinding? = null
    private var viewModel: PreviewInvestigationViewModel? = null
    private var utils: Utils? = null
    private var patientUuid: Int = 0
    private var mAdapter: PrevInvestigationParentAdapter? = null

    var appPreferences: AppPreferences? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.prev_investigation_fragment,
                container,
                false
            )

        viewModel = PreviewInvestigationViewModelFactory(
            requireActivity().application, prevInvestigationrecordsRetrofitCallback
        ).create(PreviewInvestigationViewModel::class.java)
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


        viewModel?.getPrevLab_Records(patientID, facilityid)
        viewModel!!.errorText.observe(viewLifecycleOwner,
            Observer { toastMessage ->
                utils!!.showToast(R.color.negativeToast, binding?.mainLayout!!, toastMessage)
            })
        preparePatientLIstData()

        return binding!!.root
    }


    val prevInvestigationrecordsRetrofitCallback = object : RetrofitCallback<InvestigationPrevResponseModel> {
        override fun onSuccessfulResponse(response: Response<InvestigationPrevResponseModel>) {

            if (response.body()?.responseContents?.isNotEmpty()!!) {
                viewModel?.errorTextVisibility?.value = 8
                mAdapter?.refreshList(response.body()?.responseContents!!)

            } else {
                viewModel?.errorTextVisibility?.value = 0
            }

        }

        override fun onBadRequest(response: Response<InvestigationPrevResponseModel>) {
            val gson = GsonBuilder().create()
            val responseModel: InvestigationPrevResponseModel
            try {
                responseModel = gson.fromJson(
                    response.errorBody()!!.string(),
                    InvestigationPrevResponseModel::class.java
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


    private fun preparePatientLIstData() {

        mAdapter =
            activity?.let {
                PrevInvestigationParentAdapter((requireActivity())!!)
            }!!
        binding?.previewRecyclerView!!.adapter = mAdapter

    }


}


