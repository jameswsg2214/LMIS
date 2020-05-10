package com.hmisdoctor.ui.emr_workflow.blood_request.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.FragmentBloodRequestChildBinding
import com.hmisdoctor.ui.emr_workflow.blood_request.model.*
import com.hmisdoctor.ui.emr_workflow.blood_request.view_model.BloodRequestViewModel
import com.hmisdoctor.utils.Utils
import retrofit2.Response

class BloodRequestChildFragment : Fragment() {

    private val TAG = BloodRequestChildFragment::class.java.simpleName

    private var viewModel: BloodRequestViewModel? = null
    private var binding: FragmentBloodRequestChildBinding? = null
    private var utils: Utils? = null

    private var appPreferences: AppPreferences? = null
    private var facility_id: Int? = null
    private var patient_id: Int? = null
    private var department_uuid: Int? = null

    private val bloodRequestList = ArrayList<BloodRequestModel>()
    private var bloodRequestAdapter: BloodRequestAdapter? = null

    private val prevBloodRequestList = ArrayList<ResponseContentXX>()
    private var prevBloodRequestAdapter: PrevBloodRequestAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_blood_request_child,
            container,
            false
        )
        utils = Utils(activity!!)

        viewModel = ViewModelProvider(this)[BloodRequestViewModel::class.java]

        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        patient_id = appPreferences?.getInt(AppConstants.PATIENT_UUID)
        department_uuid = appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)
        val encounter_uuid = appPreferences?.getInt(AppConstants.ENCOUNTER_UUID)

        initViews()
        listeners()

        getAllBloodGroup()
        getAllPurpose()
        getPreviousBloodRequest()

        return binding?.root
    }

    private fun initViews() {
        binding?.recyclerViewBloodRequest?.layoutManager = GridLayoutManager(activity!!, 2)
        bloodRequestAdapter = BloodRequestAdapter(bloodRequestList)
        binding?.recyclerViewBloodRequest?.adapter = bloodRequestAdapter

        binding?.recyclerViewPrevBloodRequest?.layoutManager = LinearLayoutManager(activity!!)
        prevBloodRequestAdapter = PrevBloodRequestAdapter(prevBloodRequestList)
        binding?.recyclerViewPrevBloodRequest?.adapter = prevBloodRequestAdapter

        populateRecyclerView()
    }

    private fun listeners() {
        binding?.favouriteDrawerCardView?.setOnClickListener {
            binding?.drawerLayout?.openDrawer(GravityCompat.END)
        }
    }

    private fun populateRecyclerView() {
        bloodRequestList.clear()
        bloodRequestList.add(BloodRequestModel(false, "White Human Blood"))
        bloodRequestList.add(BloodRequestModel(false, "CPP-Cryo Poor Plasma"))
        bloodRequestList.add(BloodRequestModel(false, "Red Cells (in Additive Solution)"))
        bloodRequestList.add(BloodRequestModel(false, "Apheresis Platelets (>3x1011)"))
        bloodRequestList.add(BloodRequestModel(false, "Packed Red Cells"))
        bloodRequestList.add(BloodRequestModel(false, "Saline Washed Red Cells"))
        bloodRequestList.add(BloodRequestModel(false, "Platelets Concentrate (>0.5x1011)"))
        bloodRequestList.add(BloodRequestModel(false, "Single Donor Platelets"))
        bloodRequestList.add(BloodRequestModel(false, "Fresh Frozen Plasma"))
        bloodRequestList.add(BloodRequestModel(false, "Cryo Prepcipitate"))
        binding?.recyclerViewBloodRequest?.adapter?.notifyDataSetChanged()
    }

    private fun getAllBloodGroup() {
        val body = GetAllBloodGroupReq("blood_groups")
        viewModel?.getAllBloodGroup(facility_id!!, body, allBloodGroupRetrofitCallback)
    }

    private fun getAllPurpose() {
        val body = GetAllPurposeReq("name", "blood_request_purpose")
        viewModel?.getAllPurpose(facility_id!!, body, allPurposeRetrofitCallback)
    }

    private fun getPreviousBloodRequest() {
        val body = GetPreviousBloodReq(patient_id.toString())
        viewModel?.getPreviousBloodRequest(
            facility_id!!,
            body,
            getPreviousBloodRespRetrofitCallback
        )
    }

    val allBloodGroupRetrofitCallback = object : RetrofitCallback<GetAllBloodGroupResp> {
        override fun onSuccessfulResponse(responseBody: Response<GetAllBloodGroupResp>?) {
            responseBody?.body()?.let { getAllBloodGroupResp ->
                val bloodGroupList = ArrayList<String>()
                getAllBloodGroupResp.responseContents?.forEach { responseContent: ResponseContent ->
                    responseContent.name?.let { bloodGroupList.add(it) }
                }

                val adapter =
                    ArrayAdapter<String>(
                        context!!,
                        android.R.layout.simple_spinner_item,
                        bloodGroupList
                    )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding?.spinnerBloodGroup?.adapter = adapter
            }
        }

        override fun onBadRequest(errorBody: Response<GetAllBloodGroupResp>?) {
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }

        override fun onServerError(response: Response<*>?) {
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

        override fun onFailure(s: String?) {
            if (s != null) {
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    s
                )
            }
        }

        override fun onEverytime() {
            viewModel!!.progress.value = 8
        }
    }

    val allPurposeRetrofitCallback = object : RetrofitCallback<GetAllPurposeResp> {
        override fun onSuccessfulResponse(responseBody: Response<GetAllPurposeResp>?) {
            responseBody?.body()?.let { getAllBloodGroupResp ->
                val purposeList = ArrayList<String>()
                getAllBloodGroupResp.responseContents?.forEach { responseContent ->
                    responseContent.name?.let { purposeList.add(it) }
                }

                val adapter =
                    ArrayAdapter<String>(
                        context!!,
                        android.R.layout.simple_spinner_item,
                        purposeList
                    )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding?.spinnerPurpose?.adapter = adapter
            }
        }

        override fun onBadRequest(errorBody: Response<GetAllPurposeResp>?) {
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }

        override fun onServerError(response: Response<*>?) {
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

        override fun onFailure(s: String?) {
            if (s != null) {
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    s
                )
            }
        }

        override fun onEverytime() {
            viewModel!!.progress.value = 8
        }
    }

    val getPreviousBloodRespRetrofitCallback = object : RetrofitCallback<GetPreviousBloodResp> {
        override fun onSuccessfulResponse(responseBody: Response<GetPreviousBloodResp>?) {
            responseBody?.body()?.let { getPreviousBloodResp ->
                if (getPreviousBloodResp.statusCode == 200) {
                    getPreviousBloodResp.responseContents?.let { list ->
                        prevBloodRequestList.addAll(list)
                        prevBloodRequestAdapter?.notifyDataSetChanged()
                    }
                }
            }
        }

        override fun onBadRequest(errorBody: Response<GetPreviousBloodResp>?) {
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }

        override fun onServerError(response: Response<*>?) {
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

        override fun onFailure(s: String?) {
            if (s != null) {
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    s
                )
            }
        }

        override fun onEverytime() {
            viewModel!!.progress.value = 8
        }
    }
}
