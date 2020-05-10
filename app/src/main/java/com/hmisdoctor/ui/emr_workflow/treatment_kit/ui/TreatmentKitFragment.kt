package com.hmisdoctor.ui.emr_workflow.treatment_kit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.hmisdoctor.R
import com.hmisdoctor.databinding.FragmentTreatmentKitBinding

class TreatmentKitFragment:Fragment() {

    var binding:FragmentTreatmentKitBinding?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_treatment_kit,container,false)

        replaceFragment(TreatmentKitChildFragment())

        return binding!!.root
    }

    private fun replaceFragment(fragment: Fragment) {

        val fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.treatment_kit_fragment_Container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()


    }
}