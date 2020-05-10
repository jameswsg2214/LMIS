package com.hmisdoctor.ui.emr_workflow.prescription.ui

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
import com.hmisdoctor.databinding.PrevPrescriptionFragmentBinding
import com.hmisdoctor.ui.emr_workflow.prescription.model.PrescriptionDetail
import com.hmisdoctor.ui.emr_workflow.prescription.model.PrevPrescriptionModel

import com.hmisdoctor.ui.emr_workflow.prescription.view_model.PrescriptionPreviewModel
import com.hmisdoctor.ui.emr_workflow.prescription.view_model.PrescriptionPreviewModelFactory
import com.hmisdoctor.utils.Utils
import retrofit2.Response

class PrevPrescriptionFragment : Fragment() {
    private var patientID: Int? = 0
    private var facilityid: Int? = 0
    private var binding: PrevPrescriptionFragmentBinding?=null
    private var viewModel: PrescriptionPreviewModel? = null
    private var utils: Utils? = null
    private var mAdapter: PrevPrescriptionParentAdapter? = null
    var appPreferences: AppPreferences? = null

    private var mainCallback:PrevClickedListener?=null

     override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.prev_prescription_fragment, container, false)
        viewModel = PrescriptionPreviewModelFactory(
            requireActivity().application
        ).create(PrescriptionPreviewModel::class.java)
        binding!!.viewModel = viewModel
        binding!!.lifecycleOwner = this
        utils = Utils(requireContext())
        viewModel!!.errorText.observe(viewLifecycleOwner,
            Observer { toastMessage ->
                utils!!.showToast(R.color.negativeToast, binding!!.mainLayout!!, toastMessage)
            })
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        patientID = appPreferences?.getInt(AppConstants.PATIENT_UUID)
        facilityid = appPreferences?.getInt(AppConstants.FACILITY_UUID)

        /*viewModel!!.errorText.observe(viewLifecycleOwner,
            Observer { toastMessage ->
                utils!!.showToast(R.color.negativeToast, binding?.mainLayout!!, toastMessage)
            })*/
        mAdapter = PrevPrescriptionParentAdapter((requireActivity()))
        binding!!.previewRecyclerView!!.adapter = mAdapter



        mAdapter!!.setOnItemClickListener(object :PrevPrescriptionParentAdapter.OnItemClickListener{
            override fun onItemClick(responseContent: List<PrescriptionDetail?>?) {


                mainCallback!!.sendPrevInPres(responseContent)

            }

        })
        viewModel?.getPrevPrescription_Records(patientID,facilityid,prevPrescriptionrecordsRetrofitCallback)

        return binding!!.root
    }
    val prevPrescriptionrecordsRetrofitCallback = object : RetrofitCallback<PrevPrescriptionModel> {
        override fun onSuccessfulResponse(response: Response<PrevPrescriptionModel>) {

            if (response.body()?.responseContents?.rows!!.isNotEmpty()!!) {
                viewModel?.errorTextVisibility?.value = 8
              mAdapter?.refreshList(response.body()?.responseContents?.rows)
            } else {
                viewModel?.errorTextVisibility?.value = 0
            }

        }

        override fun onBadRequest(response: Response<PrevPrescriptionModel>) {
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
    override fun onResume() {
        super.onResume()

        viewModel?.getPrevPrescription_Records(patientID,facilityid,prevPrescriptionrecordsRetrofitCallback)

    }

    interface PrevClickedListener {
        fun sendPrevInPres(
                responseContent: List<PrescriptionDetail?>?
        )
    }
    fun setOnTextClickedListener(callback: PrevClickedListener) {
        this.mainCallback = callback
    }

   /* override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        if (childFragment is ManageLabFavouriteFragment) {
            childFragment.setOnFavRefreshListener(this)
        }
    }*/
}