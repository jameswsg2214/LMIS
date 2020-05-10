package com.hmisdoctor.ui.emr_workflow.chief_complaint.ui
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
import com.hmisdoctor.databinding.PrevChiefComplaintFragmentBinding
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.PrevChiefComplainResponseModel
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.search_complaint.PrevChiefComplaintResponseContent
import com.hmisdoctor.ui.emr_workflow.chief_complaint.view_model.ChiefComplaintViewModel
import com.hmisdoctor.ui.emr_workflow.chief_complaint.view_model.ChiefComplaintViewModelFactory
import retrofit2.Response


class PrevChiefComplaintFragment : Fragment() {
    private var binding: PrevChiefComplaintFragmentBinding? = null
    private var viewModel: ChiefComplaintViewModel? = null
    private var prevChiefResponse: ArrayList<PrevChiefComplaintResponseContent?>? = null

    private var patientID: Int? = 0

    private var utils: Utils? = null
    private var patientUuid:Int = 0
    private var mAdapter: PrevChiefComplaintAdapter? = null
    var appPreferences : AppPreferences?=null
    private var mainCallback: ChiefrevClickedListener?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.prev_chief_complaint_fragment,
                container,
                false
            )

        viewModel = ChiefComplaintViewModelFactory(
            requireActivity().application
        ).create(ChiefComplaintViewModel::class.java)
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

        val encounter_uuid = appPreferences?.getInt(AppConstants?.ENCOUNTER_TYPE)



        viewModel?.getPrevChiefComplaintList(patientID,facilityid,encounter_uuid,prevlabrecordsRetrofitCallback)
        viewModel!!.errorText.observe(viewLifecycleOwner,
            Observer { toastMessage ->
                utils!!.showToast(R.color.negativeToast, binding?.mainLayout!!, toastMessage)
            })
        preparePatientLIstData()
//        mainCallback!!.sendPrevtoChild(prevChiefResponse)



        return binding!!.root
    }


    val prevlabrecordsRetrofitCallback = object : RetrofitCallback<PrevChiefComplainResponseModel> {
        override fun onSuccessfulResponse(response: Response<PrevChiefComplainResponseModel>) {

            if (response.body()?.responseContents?.isNotEmpty()!!) {

                mAdapter?.refreshList((response.body()?.responseContents)!!)

            }

        }

        override fun onBadRequest(response: Response<PrevChiefComplainResponseModel>) {
            val gson = GsonBuilder().create()
            val responseModel: PrevChiefComplainResponseModel
            try {
                responseModel = gson.fromJson(
                    response.errorBody()!!.string(),
                    PrevChiefComplainResponseModel::class.java
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
         mAdapter = PrevChiefComplaintAdapter((requireActivity()))
         binding?.previewRecyclerView!!.adapter = mAdapter


    }

    override fun onResume() {
        super.onResume()
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        val patientID = appPreferences?.getInt(AppConstants.PATIENT_UUID)
        val facilityid = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        val encounter_uuid = appPreferences?.getInt(AppConstants?.ENCOUNTER_UUID)

        viewModel?.getPrevChiefComplaintList(patientID,facilityid,encounter_uuid,prevlabrecordsRetrofitCallback)
    }


    interface ChiefrevClickedListener {
        fun sendPrevtoChild(
            responseContent: ArrayList<PrevChiefComplaintResponseContent?>?
        )
    }

    fun setOnTextClickedListener(callback: ChiefrevClickedListener) {
        this.mainCallback = callback
    }

}


