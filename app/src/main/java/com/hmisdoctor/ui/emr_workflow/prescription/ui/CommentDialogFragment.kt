package com.hmisdoctor.ui.emr_workflow.prescription.ui
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment

import com.hmisdoctor.R

import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.CommentDialogFragmentBinding
import com.hmisdoctor.ui.emr_workflow.prescription.model.*
import com.hmisdoctor.ui.emr_workflow.prescription.view_model.CommentViewModel
import com.hmisdoctor.ui.emr_workflow.prescription.view_model.CommentViewModelFactory
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import java.text.FieldPosition


class CommentDialogFragment : DialogFragment() {
    private var content: String? = null
    private var routeSpinnerList = mutableMapOf<Int, String>()
    private var frequencySpinnerList = mutableMapOf<Int, String>()
    private var instructionSpinnerList = mutableMapOf<Int, String>()
    private var viewModel: CommentViewModel? = null
    private var prescriptionInfoData: PrescriptionInfoResponsModel?=null

    private var mainCallback:CommandClickedListener?=null

    var binding: CommentDialogFragmentBinding? = null
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
            DataBindingUtil.inflate(inflater, R.layout.comment_dialog_fragment, container, false)
        viewModel = CommentViewModelFactory(
            requireActivity().application
        )
            .create(CommentViewModel::class.java)

        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        userDetailsRoomRepository = UserDetailsRoomRepository(requireActivity().application)
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
//
        facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)

        val args = arguments

        var getPosition=args!!.getInt("position")

        binding?.saveCardView?.setOnClickListener {

            Log.i("get",""+getPosition)

            mainCallback!!.sendCommandPosData(getPosition,binding?.supplierCodeTxt?.text.toString().trim())

            dialog?.dismiss()

        }

        binding?.closeImageView?.setOnClickListener {
            dialog?.dismiss()
        }

        return binding!!.root
    }

    interface CommandClickedListener {
        fun sendCommandPosData(
                position: Int,
                command:String
        )
    }
    fun setOnTextClickedListener(callback: CommandClickedListener) {
        this.mainCallback = callback
    }




}