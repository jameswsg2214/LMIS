package com.hmisdoctor.ui.emr_workflow.admission_referal.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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
import com.hmisdoctor.databinding.FragmentFavouriteBinding
import com.hmisdoctor.databinding.FragmentTabAdmissionBinding
import com.hmisdoctor.databinding.FragmentTabDischargeBinding
import com.hmisdoctor.ui.emr_workflow.admission_referal.view_model.AdmissionViewModel
import com.hmisdoctor.ui.emr_workflow.chief_complaint.view_model.AdmissionViewModelFactory
import com.hmisdoctor.ui.emr_workflow.chief_complaint.view_model.LabViewModelFactory
import com.hmisdoctor.ui.emr_workflow.delete.model.DeleteResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.view_model.LabViewModel
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesResponseModel
import com.hmisdoctor.ui.emr_workflow.radiology.ui.RadiologyFavouriteFragment
import com.hmisdoctor.ui.emr_workflow.radiology.ui.RadiologyTempleteFragment
import com.hmisdoctor.utils.Utils
import retrofit2.Response


class DischargeTabFragment : Fragment(){

    private var customdialog: Dialog?=null
    private var typeDepartmentList = mutableMapOf<Int,String>()
    private var facility_UUID: Int?  =0
    @SuppressLint("ClickableViewAccessibility")
    var binding : FragmentTabDischargeBinding ?=null
    private var department_uuid: Int? = null
    private var viewModel: AdmissionViewModel? = null


    private var utils: Utils? = null
    var appPreferences: AppPreferences? = null
     override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_tab_discharge,
                container,
                false
            )

        viewModel = AdmissionViewModelFactory(
            activity!!.application
        ).create(AdmissionViewModel::class.java)
        binding?.viewModel = viewModel
        binding!!.lifecycleOwner = this
         appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
         facility_UUID = appPreferences?.getInt(AppConstants.FACILITY_UUID)
         department_uuid = appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)

         utils = Utils(requireContext())

        val tf = ResourcesCompat.getFont(requireContext(), R.font.poppins)

         return binding!!.root
    }
    //defining Interface







}