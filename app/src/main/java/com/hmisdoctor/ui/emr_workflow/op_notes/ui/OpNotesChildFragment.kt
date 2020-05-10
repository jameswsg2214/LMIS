package com.hmisdoctor.ui.emr_workflow.op_notes.ui

import android.annotation.SuppressLint

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.FragmentBackClick
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.FragmentOpNotesChildBinding
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddResponseContent
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import com.hmisdoctor.ui.emr_workflow.model.EMR_Request.Detail
import com.hmisdoctor.ui.emr_workflow.model.EMR_Request.EmrRequestModel
import com.hmisdoctor.ui.emr_workflow.model.EMR_Request.Header
import com.hmisdoctor.ui.emr_workflow.op_notes.model.OpNotesExpandableResponseModel
import com.hmisdoctor.ui.emr_workflow.op_notes.model.OpNotesResponsModel
import com.hmisdoctor.ui.emr_workflow.op_notes.model.OpNotesResponseContent
import com.hmisdoctor.ui.emr_workflow.op_notes.view_model.OpNotesViewModel
import com.hmisdoctor.ui.emr_workflow.op_notes.view_model.OpNotesViewModelFactory
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils


import retrofit2.Response

class OpNotesChildFragment : Fragment() {
    lateinit var drugNmae: TextView
    private var facility_id: Int? = 0

    private var binding: FragmentOpNotesChildBinding? = null
    private var viewModel: OpNotesViewModel? = null
    private var utils: Utils? = null
    private var responseContents: String? = ""
    private var favmodel: FavouritesModel? = null
    private var listDepartmentItems: List<OpNotesResponseContent?> =ArrayList()
    private var favAddResponseMap = mutableMapOf<Int, String>()


    lateinit var dropdownReferenceView: AppCompatAutoCompleteTextView
    private var fragmentBackClick: FragmentBackClick? = null
    private var mAdapter: OpNotesParentAdapter? = null
    var appPreferences: AppPreferences? = null
    val detailsList = ArrayList<Detail>()
    val header: Header? = Header()
    val emrRequestModel: EmrRequestModel? = EmrRequestModel()
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_op_notes_child,
                container,
                false
            )
        viewModel = OpNotesViewModelFactory(
            requireActivity().application
        )
            .create(OpNotesViewModel::class.java)
        binding?.viewModel = viewModel
        binding!!.lifecycleOwner = this
        utils = Utils(requireContext())
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        userDetailsRoomRepository = UserDetailsRoomRepository(requireActivity().application)
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        appPreferences?.saveInt(AppConstants.LAB_MASTER_TYPE_ID, 2)
        facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        val patient_UUID = appPreferences?.getInt(AppConstants.PATIENT_UUID)
        val department_UUID = appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)
        val encounter_Type = appPreferences?.getInt(AppConstants.ENCOUNTER_TYPE)
        val encounter_uuid = appPreferences?.getInt(AppConstants.ENCOUNTER_UUID)
        binding?.opNotesSpinner?.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            viewModel?.getOpNotes(getIpNotesRetrofitCallBack, facility_id)

            false
        })




        viewModel!!.errorText.observe(viewLifecycleOwner,
            Observer { toastMessage ->
                utils!!.showToast(R.color.negativeToast, binding?.mainLayout!!, toastMessage)
            })
        preparePatientLIstData()


        return binding!!.root
    }

    val getIpNotesRetrofitCallBack =
        object : RetrofitCallback<OpNotesResponsModel> {
            override fun onSuccessfulResponse(response: Response<OpNotesResponsModel>) {
                Log.i("", "" + response?.body()?.responseContents);
                listDepartmentItems = response?.body()?.responseContents!!
                favAddResponseMap =
                    listDepartmentItems.map { it?.p_uuid!! to it.p_profile_name }.toMap().toMutableMap()

                adapterCall(response?.body()?.responseContents!!)

         }

            override fun onBadRequest(response: Response<OpNotesResponsModel>) {
                val gson = GsonBuilder().create()
                val responseModel: OpNotesResponsModel
                try {

                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        OpNotesResponsModel::class.java
                    )
                    utils?.showToast(
                        R.color.negativeToast,
                        binding?.mainLayout!!,
                        ""
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

    private fun adapterCall(responseContents: List<OpNotesResponseContent>) {

        val adapter = ArrayAdapter<String>(
            this!!.activity!!,
            android.R.layout.simple_spinner_item,
            favAddResponseMap.values.toMutableList()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.opNotesSpinner!!.adapter = adapter

        binding?.opNotesSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                val itemValue=parent!!.getItemAtPosition(0).toString()

                //val selectedPoi = parent!!.adapter.getItem(0) as OpNotesResponseContent?

                val responsedata=    favAddResponseMap.filterValues { it == itemValue }.keys.toList()[0]

                viewModel?.getOpExpandableList(facility_id, OpNotesExpandablesRetrofitCallback,responsedata)

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                pos: Int,
                id: Long) {


        //        val selectedPoi = parent!!.adapter.getItem(pos) as OpNotesResponseContent?

                val itemValue=parent!!.getItemAtPosition(pos).toString()


            val responsedata=    favAddResponseMap.filterValues { it == itemValue }.keys.toList()[0]


                viewModel?.getOpExpandableList(facility_id, OpNotesExpandablesRetrofitCallback,responsedata)

            }
        }


    }

    val OpNotesExpandablesRetrofitCallback =
        object : RetrofitCallback<OpNotesExpandableResponseModel> {
            override fun onSuccessfulResponse(response: Response<OpNotesExpandableResponseModel>) {

                if (response.body()?.responseContents?.isNotEmpty()!!) {
                    viewModel?.errorText?.value = 8.toString()
                    mAdapter?.refreshList(response.body()?.responseContents!!)

                } else {
                    viewModel?.errorText?.value = 0.toString()
                }

            }

            override fun onBadRequest(response: Response<OpNotesExpandableResponseModel>) {
                val gson = GsonBuilder().create()
                val responseModel: OpNotesExpandableResponseModel
                try {
                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        OpNotesExpandableResponseModel::class.java
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

        mAdapter =
            activity?.let {
                OpNotesParentAdapter((requireActivity())!!)
            }!!
        binding?.opNotesRecyclerView!!.adapter = mAdapter

    }
}

private fun <E> ArrayList<E>.add(element: List<OpNotesResponseContent>?) {

}


