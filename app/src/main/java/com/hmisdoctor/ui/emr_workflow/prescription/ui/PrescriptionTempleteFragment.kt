package com.hmisdoctor.ui.emr_workflow.prescription.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.PrescriptionTemplateFragmentBinding
import com.hmisdoctor.ui.emr_workflow.delete.model.DeleteResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.ui.ClearTemplateParticularPositionListener
import com.hmisdoctor.ui.emr_workflow.prescription.model.DrugDetail
import com.hmisdoctor.ui.emr_workflow.prescription.model.PrescriptionTempDetails
import com.hmisdoctor.ui.emr_workflow.prescription.model.PrescriptionTemplateResponseModel
import com.hmisdoctor.ui.emr_workflow.prescription.model.Templates
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.templeteEdit.ManagePreTempAddEdit
import com.hmisdoctor.ui.emr_workflow.prescription.view_model.PrescriptionViewModel
import com.hmisdoctor.ui.emr_workflow.prescription.view_model.PrescriptionViewModelFactory
import com.hmisdoctor.utils.Utils
import retrofit2.Response

class PrescriptionTempleteFragment :Fragment(),ClearTemplateParticularPositionListener,ManagePreTempAddEdit.OnTemplateRefreshListener {
    private var customdialog: Dialog?=null
    private var binding: PrescriptionTemplateFragmentBinding? = null
    private var viewModel: PrescriptionViewModel? = null
    lateinit var prescriptionTempleteAdapter: PrescriptionTemplateAdapter
    var mCallback: TempleteClickedListener? =null
    private var utils: Utils? = null
    private var facility_UUID: Int? = 0

    var appPreferences: AppPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
        DataBindingUtil.inflate(
            inflater,
            R.layout.prescription_template_fragment,
            container,
            false)
        viewModel = PrescriptionViewModelFactory(
            activity!!.application
        ).create(PrescriptionViewModel::class.java)
        binding?.viewModel = viewModel
        binding!!.lifecycleOwner = this

        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        facility_UUID = appPreferences?.getInt(AppConstants.FACILITY_UUID)

        utils = Utils(requireContext())
        val searchText =
            binding?.searchView?.findViewById(R.id.search_src_text) as TextView
        val tf = ResourcesCompat.getFont(requireContext(), R.font.poppins)
        searchText.typeface = tf
        binding?.searchView?.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            @SuppressLint("RestrictedApi")
            override fun onQueryTextSubmit(query: String): Boolean {
                callSearch(query)
                binding?.searchView?.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                callSearch(newText)
                return true
            }

            fun callSearch(query: String) {
                prescriptionTempleteAdapter.getFilter().filter(query)
            }

        })
        initTempleteAdapter()
        viewModel!!.getTemplete(getTempleteRetrofitCallBack)
        binding?.manageFavouritesCardView?.setOnClickListener {


            val ft = requireActivity().supportFragmentManager.beginTransaction()

           val dialog = ManagePreTempAddEdit()


            dialog.show(ft, "Tag")


        }



        return binding!!.root
    }

    private fun initTempleteAdapter() {
        prescriptionTempleteAdapter =
            PrescriptionTemplateAdapter(requireContext())
        val gridLayoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        binding?.recyclerView?.layoutManager = gridLayoutManager
        binding?.recyclerView?.adapter = prescriptionTempleteAdapter
        /*prescriptionTempleteAdapter.setOnItemClickListener(object :
            PrescriptionTemplateAdapter.OnItemClickListener {
            override fun onItemClick(
                responseContent: List<DrugDetail>?,
                position: Int,
                selected: Boolean,
                templateID : Int
            ) {
                prescriptionTempleteAdapter.updateSelectStatus(position, selected)
                mCallback?.sendTemplete(responseContent,position,selected,templateID)
            }

        })*/

        prescriptionTempleteAdapter.setOnItemClickListener(object :
                PrescriptionTemplateAdapter.OnItemClickListener {
            override fun onItemClick(
                    responseContent: List<DrugDetail>?,
                    position: Int,
                    selected: Boolean,
                    templateID : Int
            ) {
                prescriptionTempleteAdapter.updateSelectStatus(position, selected)
                mCallback?.sendTemplete(responseContent,position,selected,templateID)
            }

        })


        prescriptionTempleteAdapter.setOnItemViewClickListener(object :PrescriptionTemplateAdapter.OnItemViewClickListner{

            override fun onItemClick(responseContent: Templates?) {

                val ft = childFragmentManager.beginTransaction()
                //val dialog = ManageLabFavouriteFragment()

                val dialog = ManagePreTempAddEdit()

                val bundle = Bundle()

                bundle.putParcelable(AppConstants.RESPONSECONTENT, responseContent)

                dialog.arguments = bundle

                dialog.show(ft, "Tag")

            }

        })






        prescriptionTempleteAdapter.setOnItemDeleteClickListener(object :
            PrescriptionTemplateAdapter.OnItemDeleteClickListner {
            override fun onItemClick(
                responseContent: PrescriptionTempDetails?
            ) {
                customdialog = Dialog(requireContext())
                customdialog!! .requestWindowFeature(Window.FEATURE_NO_TITLE)
                customdialog!! .setCancelable(false)
                customdialog!! .setContentView(R.layout.delete_cutsom_layout)
                val closeImageView = customdialog!! .findViewById(R.id.closeImageView) as ImageView

                closeImageView.setOnClickListener {
                    customdialog!!.dismiss()
                }
                val drugNmae = customdialog!! .findViewById(R.id.addDeleteName) as TextView

                drugNmae.text ="${drugNmae.text.toString()} '"+responseContent?.template_name+"' Record ?"
                val yesBtn = customdialog!! .findViewById(R.id.yes) as CardView
                val noBtn = customdialog!! .findViewById(R.id.no) as CardView
                yesBtn.setOnClickListener {
                    viewModel!!.deleteFavourite(
                        facility_UUID,
                        responseContent?.template_id!!,
                        deleteRetrofitResponseCallback
                    )                }
                noBtn.setOnClickListener {
                    customdialog !!.dismiss()
                }
                customdialog !!.show()
            }

        })
    }


    override fun ClearAllData() {
        prescriptionTempleteAdapter.refreshAllData()
    }

    override fun GetTemplateDetails() {

    }

    val getTempleteRetrofitCallBack =
        object : RetrofitCallback<PrescriptionTemplateResponseModel> {
            override fun onSuccessfulResponse(response: Response<PrescriptionTemplateResponseModel>) {
                var responseContents = Gson().toJson(response.body()?.responseContents)
                if (response.body()?.responseContents?.templates_list?.isNotEmpty()!!) {
                    prescriptionTempleteAdapter.refreshList(response.body()?.responseContents?.templates_list)
                }
            }

            override fun onBadRequest(response: Response<PrescriptionTemplateResponseModel>) {
                val gson = GsonBuilder().create()
                val responseModel: PrescriptionTemplateResponseModel
                try {
                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        PrescriptionTemplateResponseModel::class.java
                    )
                    utils?.showToast(
                        R.color.negativeToast,
                        binding?.mainLayout!!,
                        getString(R.string.bad)
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
    fun setOnTextClickedListener(callback: TempleteClickedListener) {
        this.mCallback = callback
    }
    override fun ClearTemplateParticularPosition(position: Int) {
        prescriptionTempleteAdapter.refreshParticularData(position)
    }




    //defining Interface
    interface TempleteClickedListener {
        fun sendTemplete(
            templeteDetails: List<DrugDetail>?,
            position: Int,
            selected: Boolean,
            templateID: Int
        )
    }


    var deleteRetrofitResponseCallback = object : RetrofitCallback<DeleteResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<DeleteResponseModel>?) {
            viewModel!!.getTemplete(getTempleteRetrofitCallBack)
            customdialog!!.dismiss()

            Log.e("DeleteResponse", responseBody?.body().toString())
        }

        override fun onBadRequest(errorBody: Response<DeleteResponseModel>?) {

        }

        override fun onServerError(response: Response<*>?) {

        }

        override fun onUnAuthorized() {

        }

        override fun onForbidden() {

        }

        override fun onFailure(s: String?) {

        }

        override fun onEverytime() {

        }

    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)

        if (childFragment is ManagePreTempAddEdit) {
            childFragment.setOnTemplateRefreshListener(this)
        }
    }

    override fun onTemplateID(position: Int) {

    }

    override fun onRefreshList() {
        Log.i("call","callback")
        Log.i("call","callback")
        Log.i("call","callback")
    }

/*
    override fun onRefreshList() {
        viewModel!!.getTemplete(getTempleteRetrofitCallBack)
    }*/
}