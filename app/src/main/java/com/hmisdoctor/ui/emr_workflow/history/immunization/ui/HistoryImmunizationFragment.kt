package com.hmisdoctor.ui.emr_workflow.history.immunization.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.hmisdoctor.R
import com.hmisdoctor.databinding.FragmentAllergyBinding
import com.hmisdoctor.databinding.FragmentImmunizationBinding
import com.hmisdoctor.databinding.FragmentLabBinding
import com.hmisdoctor.ui.emr_workflow.view.lab.ui.HistoryImmunizationChildFragment
import com.hmisdoctor.ui.emr_workflow.view.lab.ui.HistoryLabChildFragment
import com.hmisdoctor.ui.emr_workflow.view.lab.ui.HistoryRadiologyChildFragment

class HistoryImmunizationFragment : Fragment() {
    private var binding: FragmentImmunizationBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_immunization,
                container,
                false
            )

        replaceFragment(HistoryImmunizationChildFragment())
        return binding!!.root
    }
    private fun replaceFragment(
        fragment: Fragment
    ) {
        val fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}

