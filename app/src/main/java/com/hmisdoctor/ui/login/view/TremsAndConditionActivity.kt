package com.hmisdoctor.ui.login.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hmisdoctor.R
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.ActivityTremsAndConditionBinding
import com.hmisdoctor.ui.emr_workflow.lab.ui.ManageLabPrevLabAdapter
import com.hmisdoctor.ui.emr_workflow.lab.view_model.ManageLabPervLabViewModelFactory
import com.hmisdoctor.ui.emr_workflow.lab.view_model.ManageLabPrevLabViewModel
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository

import com.hmisdoctor.ui.login.view_model.TermsAndConditionFactory
import com.hmisdoctor.ui.login.view_model.TermsAndConditionViewModel


class TremsAndConditionActivity : DialogFragment()  {
    private var content: String? = null
    private var binding: ActivityTremsAndConditionBinding? = null
    private var viewModel: TermsAndConditionViewModel? = null

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
            DataBindingUtil.inflate(inflater, R.layout.activity_trems_and_condition, container, false)
        viewModel = TermsAndConditionFactory(
            requireActivity().application
        )
            .create(TermsAndConditionViewModel::class.java)

        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this


        binding?.closeImageView?.setOnClickListener {
            dialog?.dismiss()
        }

        binding?.okOrder?.setOnClickListener {
            dialog?.dismiss()
        }

        return binding!!.root
    }



/*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trems_and_condition)

        viewModel = TermsAndConditionFactory(
            application

        ).create(TermsAndConditionViewModel::class.java)

        binding!!.lifecycleOwner = this
        binding?.viewModel = viewModel

    }
*/
}
