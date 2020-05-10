
package com.hmisdoctor.ui.emr_workflow.lab_result.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.hmisdoctor.R
import com.hmisdoctor.databinding.LabResultFragmentBinding

class LabResultFragment : Fragment() {
    private var binding: LabResultFragmentBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.lab_result_fragment,
                container,
                false
            )
        replaceFragment(LabResultChildFragment())
        return binding!!.root
    }
    private fun replaceFragment(
        fragment: Fragment
    ) {
        val fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.labRestultfragmentContainer, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}

