package com.hmisdoctor.ui.emr_workflow.lab_result.ui

import android.annotation.SuppressLint
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
import com.hmisdoctor.databinding.LabResultTabFragmentBinding
import com.hmisdoctor.ui.emr_workflow.chief_complaint.view_model.LabResultGetByDateViewModelFactory
import com.hmisdoctor.ui.emr_workflow.lab_result.model.LabResultGetByDataResponseContent
import com.hmisdoctor.ui.emr_workflow.lab_result.model.LabResultGetByDataResponseModel
import com.hmisdoctor.ui.emr_workflow.lab_result.view_model.LabResultGetByDateViewModel
import com.hmisdoctor.utils.Utils
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class LabResultGetDataFragment : Fragment() {

    private var binding: LabResultTabFragmentBinding? = null
    private var viewModel: LabResultGetByDateViewModel? = null
    var cal = Calendar.getInstance()
    private var utils: Utils? = null
    var appPreferences: AppPreferences? = null
    private var mAdapter: LabResultByDateAdapter? = null
    private val labResultLIst: ArrayList<LabResultGetByDataResponseContent> = ArrayList()


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.lab_result_tab_fragment,
                container,
                false
            )

        viewModel = LabResultGetByDateViewModelFactory(
            requireActivity().application
        ).create(LabResultGetByDateViewModel::class.java)
        binding?.viewModel = viewModel
        binding!!.lifecycleOwner = this
        utils = Utils(requireContext())

        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        val patientID = appPreferences?.getInt(AppConstants.PATIENT_UUID)
        val facilityid = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        viewModel?.getLab_Result_by_date(patientID, facilityid, labResultgetByDateRetrofitCallback)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        binding?.LabResultByDataRecyclerView!!.layoutManager = layoutManager
        mAdapter = LabResultByDateAdapter(requireContext(), labResultLIst)
        binding?.LabResultByDataRecyclerView!!.adapter = mAdapter


        return binding!!.root
    }
    val labResultgetByDateRetrofitCallback = object : RetrofitCallback<LabResultGetByDataResponseModel> {
        override fun onSuccessfulResponse(response: Response<LabResultGetByDataResponseModel>) {

            if (response.body()?.responseContents?.isNotEmpty()!!) {
                viewModel?.errorTextVisibility?.value = 8
                mAdapter!!.setData(response.body()?.responseContents as ArrayList<LabResultGetByDataResponseContent>)

            } else {
                viewModel?.errorTextVisibility?.value = 0
            }

        }

        override fun onBadRequest(response: Response<LabResultGetByDataResponseModel>) {
            val gson = GsonBuilder().create()
            val responseModel: LabResultGetByDataResponseModel
            try {
                responseModel = gson.fromJson(
                    response.errorBody()!!.string(),
                    LabResultGetByDataResponseModel::class.java
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
        val fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.labResultTabframeLayout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


}






