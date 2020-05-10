package com.hmisdoctor.ui.quick_reg.view


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.databinding.DataBindingUtil
import com.hmisdoctor.R
import com.hmisdoctor.config.AppConstants

import com.hmisdoctor.utils.Utils

import android.app.Dialog
import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.DialogSearchListBinding
import com.hmisdoctor.ui.emr_workflow.radiology.model.RecyclerDto
import com.hmisdoctor.ui.quick_reg.model.QuickSearchresponseContent
import com.hmisdoctor.ui.quick_reg.view_model.QuickRegistrationViewModel
import com.hmisdoctor.ui.quick_reg.view_model.QuickRegistrationViewModelFactory
import java.util.ArrayList


class SearchPatientDialogFragment : DialogFragment() {

    private var department_uuid: Int? = null
    private var facilitylevelID: Int? = null
    private var content: String? = null
    private var viewModel: QuickRegistrationViewModel? = null
    var binding: DialogSearchListBinding? = null

    private var mAdapter: SearchPatientAdapter? = null
    var dialogListener: DialogListener? = null
    var arrayListPatientList : ArrayList<QuickSearchresponseContent> = ArrayList()


    private var utils: Utils? = null
    var appPreferences: AppPreferences? = null
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
            DataBindingUtil.inflate(inflater, R.layout.dialog_search_list, container, false)
        viewModel = QuickRegistrationViewModelFactory(
            requireActivity().application

        )
            .create(QuickRegistrationViewModel::class.java)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this
        utils = Utils(requireContext())

        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        facilitylevelID = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        department_uuid = appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)

        binding?.closeImageView?.setOnClickListener {
            dialog?.dismiss()
        }

        val layoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        binding?.searchListrecycleview!!.layoutManager = layoutmanager
        mAdapter = SearchPatientAdapter(requireContext(), ArrayList())
        binding?.searchListrecycleview!!.adapter = mAdapter


        val args = arguments
        if (args == null) {

            //  Toast.makeText(activity, "arguments is null ", Toast.LENGTH_LONG).show()
        } else {

            val responseDataQuickSearch = args.getParcelableArrayList<QuickSearchresponseContent>(AppConstants.RESPONSECONTENT)

             binding?.patientCount?.setText("Patient(s) " + responseDataQuickSearch!!.size)
            if(responseDataQuickSearch!=null)
             {
                 arrayListPatientList = responseDataQuickSearch
             }
            mAdapter?.setData(arrayListPatientList)

        }

        mAdapter!!.setOnItemClickListener(object :
            SearchPatientAdapter.OnItemClickListener {
            override fun onItemClick(responseContent: QuickSearchresponseContent) {
                Log.i("",""+responseContent)
                val dialogListener = activity as DialogListener?
                dialogListener!!.onFinishEditDialog(responseContent)
                dismiss()
            }})
        return binding?.root
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(activity!!, theme) {
            override fun onBackPressed() {
            }
        }
    }
    interface DialogListener {
        fun onFinishEditDialog(responseData: QuickSearchresponseContent)
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

}

