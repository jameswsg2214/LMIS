
package com.hmisdoctor.ui.emr_workflow.lab_result.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.hmisdoctor.R
import com.hmisdoctor.databinding.LabResultGrapicalFragmentBinding
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries


class LabResultGrapicalFragment : Fragment() {
    private var binding: LabResultGrapicalFragmentBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.lab_result_grapical_fragment,
                container,
                false
            )
/*
        replaceFragment(LabResultTabFragment())
*/


        val series: LineGraphSeries<DataPoint> = LineGraphSeries(getDataPoint())
         binding?.graphView?.addSeries(series)

        return binding!!.root
    }

    private fun getDataPoint(): Array<DataPoint>? {
        return arrayOf<DataPoint>(
            DataPoint(0.0, 1.0),
            DataPoint(2.0, 5.0),
            DataPoint(3.0, 1.0),
            DataPoint(5.0, 6.0),
            DataPoint(8.0, 3.0)
        )
    }
    private fun replaceFragment(
        fragment: Fragment
    ) {
        val fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.labResultGrapicalframeLayout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}

