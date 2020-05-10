package com.hmisdoctor.ui.quick_reg.view


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.fragment.app.DialogFragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.hmisdoctor.R
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.utils.Utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.DialogAssignListBinding
import com.hmisdoctor.ui.emr_workflow.radiology.model.RecyclerDto
import com.hmisdoctor.ui.quick_reg.model.LocationMaster
import com.hmisdoctor.ui.quick_reg.model.LocationMasterResponseModel
import com.hmisdoctor.ui.quick_reg.model.labtest.request.Assigntoother
import com.hmisdoctor.ui.quick_reg.model.labtest.request.AssigntootherRequest
import com.hmisdoctor.ui.quick_reg.model.labtest.request.SendIdList
import com.hmisdoctor.ui.quick_reg.model.labtest.response.SimpleResponseModel
import com.hmisdoctor.ui.quick_reg.model.labtest.response.UserProfileResponseModel
import com.hmisdoctor.ui.quick_reg.model.request.LabNameSearchResponseModel
import com.hmisdoctor.ui.quick_reg.view_model.LabTestViewModel
import com.hmisdoctor.ui.quick_reg.view_model.LabTestViewModelFactory
import com.hmisdoctor.utils.CustomProgressDialog
import retrofit2.Response


class AssignToOtherDialogFragment : DialogFragment() {

    private var department_uuid: Int? = null
    private var facilitylevelID: Int? = null
    private var content: String? = null
    private var viewModel: LabTestViewModel? = null
    var binding: DialogAssignListBinding? = null
    private val labTestList: MutableList<RecyclerDto> = ArrayList()
    private val searchList: MutableList<RecyclerDto> = ArrayList()
    private var mAdapter: AssignToAdapter? = null
    var dialogListener: DialogListener? = null
    private var customProgressDialog: CustomProgressDialog? = null

    var currentPosition:Int=0

    private var instutionView: AppCompatAutoCompleteTextView?=null

    private var labView: AppCompatAutoCompleteTextView?=null

    private var utils: Utils? = null
    var appPreferences: AppPreferences? = null

    private  var favouriteData:ArrayList<SendIdList> =ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content = arguments?.getString(AppConstants.ALERTDIALOG)
        val style = STYLE_NO_FRAME
        val theme = R.style.DialogTheme
        setStyle(style, theme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_assign_list, container, false)
        viewModel = LabTestViewModelFactory(
            requireActivity().application

        )
            .create(LabTestViewModel::class.java)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this
        utils = Utils(requireContext())
        customProgressDialog = CustomProgressDialog(requireContext())
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        facilitylevelID = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        department_uuid = appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)

        viewModel!!.progress.observe(requireActivity(), Observer {
                progress ->
            if (progress == View.VISIBLE) {
                customProgressDialog!!.show()
            } else if (progress == View.GONE) {
                customProgressDialog!!.dismiss()
            }

        })

        binding?.closeImageView?.setOnClickListener {

            dialog?.dismiss()
        }

        binding?.cancelCardView?.setOnClickListener {

            dialog?.dismiss()
        }
        val layoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        binding?.assignListrecycleview!!.layoutManager = layoutmanager
        mAdapter = AssignToAdapter(requireContext(), ArrayList())


        binding?.assignListrecycleview !!.adapter = mAdapter

        mAdapter!!.setOnSearchInitiatedListener(object :AssignToAdapter.OnSearchInitiatedListener{
            override fun onSearchInitiated(
                query: String,
                view: AppCompatAutoCompleteTextView,
                position: Int,
                lab: AppCompatAutoCompleteTextView
            ) {

                instutionView=view

                labView=lab

                currentPosition=position

                viewModel!!.getLabName(query,LabnameResponseCallback)


            }

        })

        binding!!.saveCardView.setOnClickListener {

            val data=mAdapter!!.getAll()

            val check= data.any{ it!!.to_facility == 0}

            val check2= data.any{ it!!.to_location_uuid == ""}

            if(check||check2){

                Toast.makeText(this.context,"Please Fill all field",Toast.LENGTH_SHORT).show()
            }
            else {

                Log.e("Save","save")

                val request: AssigntootherRequest = AssigntootherRequest()

                request.details = data

                viewModel!!.assigntoOther(request, saveRetrofitCallback)

            }
        }

        mAdapter!!.setOnSearch(object :AssignToAdapter.OnSearch{
            override fun onSearchFunction(
                data: Int,
                dropdownReferenceView: AppCompatAutoCompleteTextView,
                searchposition: Int
            ) {

                labView=dropdownReferenceView

                currentPosition=searchposition

                viewModel!!.getLocationMaster(data,LocationMasterResponseCallback)

            }
        })

        val args = arguments

        if (args == null) {

        } else {
            // get value from bundle..
            favouriteData = args.getParcelableArrayList<SendIdList>(AppConstants.RESPONSECONTENT)!!

            val list:ArrayList<Assigntoother> = ArrayList()

            for(i in favouriteData.indices){

                val data: Assigntoother=Assigntoother()

                data.id=favouriteData[i].Id

                data.testname=favouriteData[i].name

                list.add(data)

            }

            mAdapter!!.setData(list)

        }


        return binding?.root
    }

    interface DialogListener {
        fun onAssignTolabtestactivity()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try { /* this line is main difference for fragment to fragment communication & fragment to activity communication
            fragment to fragment: onInputListener = (OnInputListener) getTargetFragment();
            fragment to activity: onInputListener = (OnInputListener) getActivity();
             */
            dialogListener = targetFragment as DialogListener?
        } catch (e: ClassCastException) {
        }
    }

    val saveRetrofitCallback = object : RetrofitCallback<SimpleResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<SimpleResponseModel>?) {

            val dialogListener = activity as DialogListener?
            dialogListener!!.onAssignTolabtestactivity()
            dialog!!.dismiss()


        }

        override fun onBadRequest(errorBody: Response<SimpleResponseModel>?) {
            val gson = GsonBuilder().create()
            val responseModel: UserProfileResponseModel
            try {
                responseModel = gson.fromJson(
                    errorBody!!.errorBody()!!.string(),
                    UserProfileResponseModel::class.java
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

    val LocationMasterResponseCallback = object : RetrofitCallback<LocationMasterResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<LocationMasterResponseModel>?) {
            Log.i("","locationdata"+responseBody!!.body()!!.responseContents)
            val data=responseBody!!.body()!!.responseContents

            if(data.isNotEmpty()) {

                mAdapter!!.setlabAdapter(labView!!,data as ArrayList<LocationMaster>,currentPosition)
            }

            //    labnameAdapter(responseBody!!.body()!!.responseContents)
        }
        override fun onBadRequest(errorBody: Response<LocationMasterResponseModel>?) {
            val gson = GsonBuilder().create()
            val responseModel: LocationMasterResponseModel
            try {
                responseModel = gson.fromJson(
                    errorBody!!.errorBody()!!.string(),
                    LocationMasterResponseModel::class.java
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

        override fun onFailure(failure: String?) {
            utils?.showToast(R.color.negativeToast, binding?.mainLayout!!, failure!!)
        }

        override fun onEverytime() {

            viewModel!!.progress.value = 8


        }
    }

    val LabnameResponseCallback = object : RetrofitCallback<LabNameSearchResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<LabNameSearchResponseModel>?) {

            mAdapter!!.setAdapter(instutionView!!,responseBody!!.body()!!.responseContents,currentPosition,labView!!)

        }

        override fun onBadRequest(errorBody: Response<LabNameSearchResponseModel>?) {
            val gson = GsonBuilder().create()
            val responseModel: LabNameSearchResponseModel
            try {
                responseModel = gson.fromJson(
                    errorBody!!.errorBody()!!.string(),
                    LabNameSearchResponseModel::class.java
                )
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    responseModel.status!!
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

        override fun onFailure(failure: String?) {
            utils?.showToast(R.color.negativeToast, binding?.mainLayout!!, failure!!)
        }

        override fun onEverytime() {
            viewModel!!.progress.value = 8
        }
    }

}

