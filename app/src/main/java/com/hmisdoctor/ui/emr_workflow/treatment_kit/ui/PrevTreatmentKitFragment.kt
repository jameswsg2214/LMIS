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
import com.hmisdoctor.databinding.PrevTreatmentKitFragmentBinding
import com.hmisdoctor.ui.emr_workflow.treatment_kit.model.TreatmentKitPrevResponsModel
import com.hmisdoctor.ui.emr_workflow.treatment_kit.view_model.TreatmentKitViewModel
import com.hmisdoctor.ui.emr_workflow.treatment_kit.view_model.TreatmentKitViewModelFactory
import com.hmisdoctor.ui.emr_workflow.view.lab.model.PodArrResult
import com.hmisdoctor.ui.emr_workflow.view.lab.model.PrevLabResponseModel
import com.makkalnalam.ui.Expandable.PrevTreatmentParentParentAdapter
import retrofit2.Response


class PrevTreatmentKitFragment : Fragment() {
    private var binding: PrevTreatmentKitFragmentBinding? = null
    private var viewModel: TreatmentKitViewModel? = null
    private var utils: Utils? = null
    private var patientUuid:Int = 0
    private var mAdapter: PrevTreatmentParentParentAdapter? = null
    var appPreferences : AppPreferences?=null
    private var mainCallback:LabPrevClickedListener?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.prev_treatment_kit_fragment,
                container,
                false
            )

        viewModel = TreatmentKitViewModelFactory(
            requireActivity().application
        ).create(TreatmentKitViewModel::class.java)
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


        viewModel?.getPrevTreatment_kit_Records(patientID!!,facilityid,prevlabrecordsRetrofitCallback)
        viewModel!!.errorText.observe(viewLifecycleOwner,
            Observer { toastMessage ->
                utils!!.showToast(R.color.negativeToast, binding?.mainLayout!!, toastMessage)
            })
        preparePatientLIstData()

        mAdapter!!.setOnItemClickListener(object:PrevTreatmentParentParentAdapter.OnItemClickListener{

            override fun onItemClick(responseContent: List<PodArrResult>?) {


                Log.i("",""+responseContent)
                mainCallback!!.sendPrevtoChild(responseContent)


            }
        })

        return binding!!.root
    }


    val prevlabrecordsRetrofitCallback = object : RetrofitCallback<TreatmentKitPrevResponsModel> {
        override fun onSuccessfulResponse(response: Response<TreatmentKitPrevResponsModel>) {

            if (response.body()?.responseContents?.isNotEmpty()!!) {
                viewModel?.errorText?.value = 8.toString()
                mAdapter?.refreshList(response.body()?.responseContents!!)

            } else {
                viewModel?.errorText?.value = 0.toString()
            }

        }

        override fun onBadRequest(response: Response<TreatmentKitPrevResponsModel>) {
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
            viewModel!!.progress.value = 8
        }


    }
    private fun preparePatientLIstData() {
         mAdapter =PrevTreatmentParentParentAdapter((requireActivity()))
         binding?.previewRecyclerView!!.adapter = mAdapter


    }

    override fun onResume() {
        super.onResume()
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        val patientID = appPreferences?.getInt(AppConstants.PATIENT_UUID)
        val facilityid = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        viewModel?.getPrevTreatment_kit_Records(patientID!!,facilityid,prevlabrecordsRetrofitCallback)
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


