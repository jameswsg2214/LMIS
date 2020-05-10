package com.hmisdoctor.ui.emr_workflow.lab_result.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.FragmentBackClick
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.LabResultTabLayoutBinding
import com.hmisdoctor.ui.emr_workflow.chief_complaint.view_model.LabResultViewModelFactory
import com.hmisdoctor.ui.emr_workflow.lab_result.view_model.LabResultViewModel
import com.hmisdoctor.utils.Utils
import java.util.ArrayList


@Suppress("UNREACHABLE_CODE")
class LabResultTabFragment : Fragment() {
    private var binding: LabResultTabLayoutBinding? = null
    private var viewModel: LabResultViewModel? = null
    private var responseContents: String? = ""
    private var fragmentBackClick: FragmentBackClick? = null
    private var appPreferences : AppPreferences?= null
    private var utils: Utils? = null



    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.lab_result_tab_layout, container, false)

        utils = Utils(this.context!!)
        if (activity !is FragmentBackClick) {
            throw ClassCastException("Hosting activity must implement BackHandlerInterface")
        } else {
            fragmentBackClick = activity as FragmentBackClick?
        }
        viewModel = LabResultViewModelFactory(
            requireActivity().application
        ).create(LabResultViewModel::class.java)

        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        val facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        setupViewPager(binding?.viewPagerLabResult!!)
        binding?.tabLayout!!.setupWithViewPager(binding?.viewPagerLabResult!!)


        return binding!!.root

    }

private fun setupViewPager(viewPager: ViewPager?) {
    val adapter = ViewPagerAdapter(childFragmentManager)
    adapter.addFragment(LabResultGetDataFragment(), "List View")
    adapter.addFragment(LabResultGrapicalFragment(), "Grapical View")
    viewPager!!.adapter = adapter
}

    internal inner class ViewPagerAdapter(manager: FragmentManager?) : FragmentPagerAdapter(manager!!) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

}

