package com.hmisdoctor.ui.emr_workflow.admission_referal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.hmisdoctor.R
import com.hmisdoctor.databinding.FragmentAdmissionBinding
import com.hmisdoctor.databinding.FragmentChiefComplaintsBinding
import com.hmisdoctor.databinding.FragmentHistoryBinding
import com.hmisdoctor.ui.emr_workflow.history.ui.HistoryChildFragment

class AdmissionFragment : Fragment() {
    private var binding: FragmentAdmissionBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_admission,
                container,
                false
            )
        replaceFragment(AdmissionChildFragment())


        return binding!!.root
    }
    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}

