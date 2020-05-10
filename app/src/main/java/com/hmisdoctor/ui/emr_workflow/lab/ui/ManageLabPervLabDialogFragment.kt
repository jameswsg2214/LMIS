package com.hmisdoctor.ui.emr_workflow.lab.ui
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager

import com.hmisdoctor.R

import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.CommentDialogFragmentBinding
import com.hmisdoctor.databinding.DialogManageLabPrevlabBinding
import com.hmisdoctor.ui.emr_workflow.lab.view_model.ManageLabPervLabViewModelFactory
import com.hmisdoctor.ui.emr_workflow.lab.view_model.ManageLabPrevLabViewModel
import com.hmisdoctor.ui.emr_workflow.prescription.model.*
import com.hmisdoctor.ui.emr_workflow.prescription.view_model.CommentViewModel
import com.hmisdoctor.ui.emr_workflow.prescription.view_model.CommentViewModelFactory
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils



class ManageLabPervLabDialogFragment : DialogFragment() {
    private var content: String? = null
    private var viewModel: ManageLabPrevLabViewModel? = null
    private var managePrevLabAdapter: ManageLabPrevLabAdapter?=null
    var binding: DialogManageLabPrevlabBinding? = null
    var appPreferences: AppPreferences? = null
    private var utils: Utils? = null
    private var facility_UUID: Int? = 0
    private var deparment_UUID: Int? = 0
    private var facility_id: Int? = 0
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null

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
            DataBindingUtil.inflate(inflater, R.layout.dialog_manage_lab_prevlab, container, false)
        viewModel = ManageLabPervLabViewModelFactory(
            requireActivity().application
        )
            .create(ManageLabPrevLabViewModel::class.java)

        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        userDetailsRoomRepository = UserDetailsRoomRepository(requireActivity().application)
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)

        binding?.closeImageView?.setOnClickListener {
            dialog?.dismiss()
        }

        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.labManagePrevLabRecyclerView!!.layoutManager = linearLayoutManager
        managePrevLabAdapter = ManageLabPrevLabAdapter(
            activity!!
        )
        binding?.labManagePrevLabRecyclerView!!.adapter = managePrevLabAdapter

        return binding!!.root
    }


}