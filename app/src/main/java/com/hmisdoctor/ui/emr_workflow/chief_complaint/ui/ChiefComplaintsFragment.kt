package com.hmisdoctor.ui.emr_workflow.chief_complaint.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.hmisdoctor.R
import com.hmisdoctor.databinding.FragmentChiefComplaintsBinding
import com.hmisdoctor.ui.emr_workflow.lab.ui.ClearFavParticularPositionListener

class ChiefComplaintsFragment : Fragment()  {
    private var binding: FragmentChiefComplaintsBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_chief_complaints,
                container,
                false
            )

        replaceFragment(ChiefComplaintChildFragment())
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

