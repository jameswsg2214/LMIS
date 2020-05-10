package com.hmisdoctor.ui.emr_workflow.history.surgery.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.hmisdoctor.R
import com.hmisdoctor.databinding.FragmentAllergyBinding
import com.hmisdoctor.databinding.FragmentHistorySurgeryBinding
import com.hmisdoctor.databinding.FragmentRadiologyBinding
import com.hmisdoctor.ui.emr_workflow.view.lab.ui.HistoryRadiologyChildFragment
import com.hmisdoctor.ui.emr_workflow.view.lab.ui.HistorySurgeryChildFragment

class HistorySurgeryFragment : Fragment() {
    private var binding: FragmentHistorySurgeryBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_history_surgery,
                container,
                false
            )

        replaceFragment(HistorySurgeryChildFragment())
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

