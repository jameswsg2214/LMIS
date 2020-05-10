package com.hmisdoctor.ui.emr_workflow.view
import com.hmisdoctor.ui.emr_workflow.history.prescription.ui.HistoryPrescriptionChildFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.hmisdoctor.R
import com.hmisdoctor.databinding.FragmentNoRecordsBinding


class NoRecordsFragment : Fragment() {
    private var binding: FragmentNoRecordsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_no_records,
                container,
                false
            )

        return binding!!.root
    }

}