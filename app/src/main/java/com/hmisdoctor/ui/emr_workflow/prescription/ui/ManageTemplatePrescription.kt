package com.hmisdoctor.ui.emr_workflow.prescription.ui
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.hmisdoctor.R
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.DialogManageTempatePrescriptionBinding
import com.hmisdoctor.ui.emr_workflow.prescription.view_model.ManagePrescriptionTemplateViewModel
import com.hmisdoctor.ui.emr_workflow.prescription.view_model.ManagePrescriptionTemplateViewModelFactory
import com.hmisdoctor.utils.Utils


class ManageTemplatePrescription : DialogFragment() {

    private var viewModel: ManagePrescriptionTemplateViewModel? = null
    var binding: DialogManageTempatePrescriptionBinding? = null
    var appPreferences: AppPreferences? = null
    private var utils: Utils? = null
    private var facility_UUID: Int? = 0
    private var deparment_UUID: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            DataBindingUtil.inflate(inflater, R.layout.dialog_manage_tempate_prescription, container, false)
        viewModel = ManagePrescriptionTemplateViewModelFactory(
            requireActivity().application
        )
            .create(ManagePrescriptionTemplateViewModel::class.java)

        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this
        facility_UUID = appPreferences?.getInt(AppConstants?.FACILITY_UUID)
        deparment_UUID = appPreferences?.getInt(AppConstants?.DEPARTMENT_UUID)
        binding?.closeImageView?.setOnClickListener {
            dialog?.dismiss()
        }

        return binding!!.root
    }




}