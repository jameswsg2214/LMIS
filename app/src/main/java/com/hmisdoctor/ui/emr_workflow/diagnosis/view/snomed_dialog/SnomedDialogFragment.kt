package com.hmisdoctor.ui.emr_workflow.diagnosis.view.snomed_dialog

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.databinding.SnomedDialogLayoutBinding
import com.hmisdoctor.ui.emr_workflow.diagnosis.view.snomed_dialog.response.SnomedChildDataResponseModel
import com.hmisdoctor.ui.emr_workflow.diagnosis.view.snomed_dialog.response.SnomedData
import com.hmisdoctor.ui.emr_workflow.diagnosis.view.snomed_dialog.response.SnomedDataResponseModel
import com.hmisdoctor.ui.emr_workflow.diagnosis.view.snomed_dialog.response.SnomedParentDataResponseModel
import com.hmisdoctor.ui.emr_workflow.prescription.model.PrescriptionDurationResponseModel
import com.hmisdoctor.utils.CustomProgressDialog
import com.hmisdoctor.utils.Utils
import retrofit2.Response

class SnomedDialogFragment:DialogFragment() {


    private var content: String? = null

    private var binding:SnomedDialogLayoutBinding?=null

    private var viewModel:SnomedDialogviewModel?=null
    private var customProgressDialog: CustomProgressDialog? = null


    private var utils: Utils? = null

    private var snomedAdapter:SnomedAdapter?=null

    private var snomedChildAdapter:SnomedAdapter?=null

    private var snomedParentAdapter:SnomedAdapter?=null

    private var callbackSnomed:OnSnomedListener?=null

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
                DataBindingUtil.inflate(inflater, R.layout.snomed_dialog_layout, container, false)
        viewModel = SnomedDialogviewModelFactory(
                requireActivity().application
        )
                .create(SnomedDialogviewModel::class.java)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this

        utils = Utils(requireContext())
        customProgressDialog = CustomProgressDialog(context)



        binding?.closeImageView!!.setOnClickListener {

            dialog?.dismiss()
        }

        //utils= Utils(context)

        binding?.searchView?.setOnQueryTextListener(object :
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
            @SuppressLint("RestrictedApi")
            override fun onQueryTextSubmit(query: String): Boolean {
                callSearch(query)
                binding?.searchView?.clearFocus()
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
            fun callSearch(query: String) {
                viewModel!!.progress.observe(requireActivity(),
                    Observer { progress ->
                        if (progress == View.VISIBLE) {
                            customProgressDialog!!.show()
                        } else if (progress == View.GONE) {
                            customProgressDialog!!.dismiss()
                        }
                    })



                viewModel?.searchSnoomed(
                        query,
                        searchsn0omedRetrofitCallback
                )
            }

        })

        snomedAdapter=SnomedAdapter(this.activity!!, ArrayList())

        val linearLayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)

        binding?.mainList?.layoutManager = linearLayoutManager

        binding?.mainList?.adapter = snomedAdapter

        snomedAdapter!!.setOnViewClickListener(object :SnomedAdapter.OnViewClickListener{
            override fun onViewClick(data: SnomedData?) {


                Log.i("click data",""+data)
                viewModel!!.searchParentSnoomed(data!!.ConceptId,searchParentRetrofitCallback)

                viewModel!!.searchChildSnoomed(data!!.ConceptId,searchChildRetrofitCallback)


            }
        })

        val linearLayoutManager2 = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)


        snomedParentAdapter=SnomedAdapter(this.activity!!, ArrayList())

        binding?.parentList?.layoutManager = linearLayoutManager2

        binding?.parentList?.adapter = snomedParentAdapter

        snomedParentAdapter!!.setOnViewClickListener(object :SnomedAdapter.OnViewClickListener{

            override fun onViewClick(data: SnomedData?) {

                viewModel!!.searchParentSnoomed(data!!.ConceptId,searchParentRetrofitCallback)

                viewModel!!.searchChildSnoomed(data!!.ConceptId,searchChildRetrofitCallback)


            }
        })


        val linearLayoutManager3 = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)

        snomedChildAdapter=SnomedAdapter(this.activity!!, ArrayList())

        binding?.childList?.layoutManager = linearLayoutManager3

        binding?.childList?.adapter = snomedChildAdapter

        snomedChildAdapter!!.setOnViewClickListener(object :SnomedAdapter.OnViewClickListener{

            override fun onViewClick(data: SnomedData?) {

                viewModel!!.searchParentSnoomed(data!!.ConceptId,searchParentRetrofitCallback)

                viewModel!!.searchChildSnoomed(data!!.ConceptId,searchChildRetrofitCallback)


            }
        })


        binding!!.save.setOnClickListener {

            val getData=snomedParentAdapter!!.getFirstData()

            callbackSnomed!!.onSnomeddata(getData)

            dialog?.dismiss()

        }


        return binding?.root
    }

    val searchsn0omedRetrofitCallback = object : RetrofitCallback<SnomedDataResponseModel> {

        override fun onSuccessfulResponse(responseBody: Response<SnomedDataResponseModel>?) {

            Log.i("response",""+responseBody!!.body()!!.Snomed_data)

            snomedAdapter!!.setData(responseBody!!.body()!!.Snomed_data)


        }
        override fun onBadRequest(response: Response<SnomedDataResponseModel>) {
            val gson = GsonBuilder().create()
            val responseModel: PrescriptionDurationResponseModel
            try {
                responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        PrescriptionDurationResponseModel::class.java
                )
                utils?.showToast(
                        R.color.negativeToast,
                        binding?.mainLayout!!,
                        getString(R.string.something_went_wrong)
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

    val searchParentRetrofitCallback = object : RetrofitCallback<SnomedParentDataResponseModel> {

        override fun onSuccessfulResponse(responseBody: Response<SnomedParentDataResponseModel>?) {

            Log.i("responsePar",""+responseBody!!.body())

            snomedParentAdapter!!.setData(responseBody.body()!!.Snomed_Parent_data)


        }
        override fun onBadRequest(response: Response<SnomedParentDataResponseModel>) {
            val gson = GsonBuilder().create()
            val responseModel: SnomedParentDataResponseModel
            try {
                responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        SnomedParentDataResponseModel::class.java
                )
                utils?.showToast(
                        R.color.negativeToast,
                        binding?.mainLayout!!,
                        getString(R.string.something_went_wrong)
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


    val searchChildRetrofitCallback = object : RetrofitCallback<SnomedChildDataResponseModel> {

        override fun onSuccessfulResponse(responseBody: Response<SnomedChildDataResponseModel>?) {

            Log.i("response",""+responseBody!!.body()!!.Snomed_Children_data)

            snomedChildAdapter!!.setData(responseBody!!.body()!!.Snomed_Children_data)


        }
        override fun onBadRequest(response: Response<SnomedChildDataResponseModel>) {
            val gson = GsonBuilder().create()
            val responseModel: SnomedChildDataResponseModel
            try {
                responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        SnomedChildDataResponseModel::class.java
                )
                utils?.showToast(
                        R.color.negativeToast,
                        binding?.mainLayout!!,
                        getString(R.string.something_went_wrong)
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

    fun setOnSnomedRefreshListener(callback: OnSnomedListener) {
        this.callbackSnomed = callback
    }
    // This interface can be implemented by the Activity, parent Fragment,
    // or a separate test implementation.
    interface OnSnomedListener {
        fun onSnomeddata(position: SnomedData)
    }
}