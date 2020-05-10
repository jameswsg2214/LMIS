package com.hmisdoctor.ui.emr_workflow.diet.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.hmisdoctor.R
import com.hmisdoctor.databinding.FragmentDietChildBinding
import com.hmisdoctor.databinding.PrevDietFragmentBinding

class PrevDietFragment : Fragment() {

    private  var  binding: PrevDietFragmentBinding?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.prev_diet_fragment,
                container,
                false
            )

        return  this.binding!!.root

    }
}