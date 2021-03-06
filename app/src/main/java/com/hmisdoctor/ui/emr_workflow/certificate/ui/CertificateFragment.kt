package com.hmisdoctor.ui.emr_workflow.certificate.ui
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.hmisdoctor.R
import com.hmisdoctor.databinding.FragmentCertificateBinding
import com.hmisdoctor.databinding.FragmentOpNotesBinding

import com.hmisdoctor.databinding.FragmentRadiologyBinding
import com.hmisdoctor.ui.emr_workflow.op_notes.ui.OpNotesChildFragment
import com.hmisdoctor.ui.emr_workflow.radiology.ui.RadiologyChildFragment


class CertificateFragment : Fragment() {
    private var binding: FragmentCertificateBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_certificate,
                container,
                false
            )
        replaceFragment(CertificateChildFragment())
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

