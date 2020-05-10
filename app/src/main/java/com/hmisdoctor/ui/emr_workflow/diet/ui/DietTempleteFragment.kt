package com.hmisdoctor.ui.emr_workflow.diet.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.hmisdoctor.R
import com.hmisdoctor.databinding.FragmentDietTempleteBinding

class DietTempleteFragment : Fragment() {

    private var binding:FragmentDietTempleteBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_diet_templete,container,false)
        //return super.onCreateView(inflater, container, savedInstanceState)

        return binding!!.root
    }
}