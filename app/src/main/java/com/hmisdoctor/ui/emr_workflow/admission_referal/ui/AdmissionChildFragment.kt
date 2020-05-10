package com.hmisdoctor.ui.emr_workflow.admission_referal.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.FragmentBackClick
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.FragmentAdmissionChildBinding
import com.hmisdoctor.ui.emr_workflow.admission_referal.view_model.AdmissionViewModel
import com.hmisdoctor.ui.emr_workflow.chief_complaint.view_model.AdmissionViewModelFactory
import com.hmisdoctor.ui.emr_workflow.history.model.response.History
import com.hmisdoctor.utils.Utils


class AdmissionChildFragment : Fragment() {
    private var binding: FragmentAdmissionChildBinding? = null
    private var viewModel: AdmissionViewModel? = null
    private var responseContents: String? = ""
    private var fragmentBackClick: FragmentBackClick? = null
    private var appPreferences : AppPreferences?= null
    private var utils: Utils? = null
    private var tabsArrayList: List<History?>? = null
    private var viewpageradapter: ViewPagerAdapter? = null
    private var selectedFragment: Fragment? = null



    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admission_child, container, false)

        utils = Utils(this.context!!)
        if (activity !is FragmentBackClick) {
            throw ClassCastException("Hosting activity must implement BackHandlerInterface")
        } else {
            fragmentBackClick = activity as FragmentBackClick?
        }
        viewModel = AdmissionViewModelFactory(
            requireActivity().application
        ).create(AdmissionViewModel::class.java)

        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        val facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        setupViewPager(binding?.viewPager!!)
        binding?.tabLayout!!.setupWithViewPager(binding?.viewPager!!)


        return binding!!.root
    }

    private fun setupViewPager(viewPager: ViewPager) {
         viewpageradapter = ViewPagerAdapter(childFragmentManager)
        viewpageradapter!!.addFragment(AdmissionTabFragment(), "Admission")
        viewpageradapter!!.addFragment(ReferralTabFragment(), "Refferal")
        viewpageradapter!!.addFragment(TransferTabFragment(), "Transfer")
        viewpageradapter!!.addFragment(DischargeTabFragment(), "Discharge")
        viewPager?.offscreenPageLimit =3
        viewPager.adapter = viewpageradapter

    }


    internal inner class ViewPagerAdapter(manager: FragmentManager) :
        FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val mFragmentList = java.util.ArrayList<Fragment>()
        private val mFragmentTitleList = java.util.ArrayList<String>()

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

