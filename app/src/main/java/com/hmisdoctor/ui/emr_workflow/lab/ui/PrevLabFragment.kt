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
import com.hmisdoctor.databinding.PrevLabFragmentBinding
import com.hmisdoctor.ui.emr_workflow.view.lab.view_model.PreviewViewModel
import com.hmisdoctor.ui.emr_workflow.view.lab.view_model.PreviewViewModelFactory
import com.hmisdoctor.utils.Utils

import com.google.gson.GsonBuilder
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.ui.emr_workflow.view.lab.model.PodArrResult
import com.hmisdoctor.ui.emr_workflow.view.lab.model.PrevLabResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.ui.PrevLabParentAdapter
import retrofit2.Response


class PrevLabFragment : Fragment() {
    private var binding: PrevLabFragmentBinding? = null
    private var viewModel: PreviewViewModel? = null
    private var utils: Utils? = null
    private var patientUuid:Int = 0
    private var mAdapter: PrevLabParentAdapter? = null
    var appPreferences : AppPreferences?=null
    private var mainCallback:LabPrevClickedListener?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.prev_lab_fragment,
                container,
                false
            )

        viewModel = PreviewViewModelFactory(
            requireActivity().application, prevlabrecordsRetrofitCallback
        ).create(PreviewViewModel::class.java)
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


        viewModel?.getPrevLab_Records(patientID,facilityid)
        viewModel!!.errorText.observe(viewLifecycleOwner,
            Observer { toastMessage ->
                utils!!.showToast(R.color.negativeToast, binding?.mainLayout!!, toastMessage)
            })
        preparePatientLIstData()

        mAdapter!!.setOnItemClickListener(object: PrevLabParentAdapter.OnItemClickListener{


            override fun onItemClick(responseContent: List<PodArrResult>?) {
                Log.i("",""+responseContent)
                mainCallback!!.sendPrevtoChild(responseContent)

            }
        })

        return binding!!.root
    }


    val prevlabrecordsRetrofitCallback = object : RetrofitCallback<PrevLabResponseModel> {
        override fun onSuccessfulResponse(response: Response<PrevLabResponseModel>) {

            if (response.body()?.responseContents?.isNotEmpty()!!) {
                viewModel?.errorTextVisibility?.value = 8
                mAdapter?.refreshList(response.body()?.responseContents!!)

            } else {
                viewModel?.errorTextVisibility?.value = 0
            }

        }

        override fun onBadRequest(response: Response<PrevLabResponseModel>) {
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
    private fun preparePatientLIstData() {
         mAdapter = PrevLabParentAdapter((requireActivity()))
         binding?.previewRecyclerView!!.adapter = mAdapter


    }

    override fun onResume() {
        super.onResume()
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        val patientID = appPreferences?.getInt(AppConstants.PATIENT_UUID)
        val facilityid = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        viewModel?.getPrevLab_Records(patientID,facilityid)
    }


    interface LabPrevClickedListener {
        fun sendPrevtoChild(
                responseContent: List<PodArrResult>?
        )
    }

    fun setOnTextClickedListener(callback: LabPrevClickedListener) {
        this.mainCallback = callback
    }

}


