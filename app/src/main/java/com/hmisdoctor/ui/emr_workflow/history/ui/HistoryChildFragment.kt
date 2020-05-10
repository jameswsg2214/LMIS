package com.hmisdoctor.ui.emr_workflow.history.ui

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
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.FragmentBackClick
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.FragmentHistoryChildBinding
import com.hmisdoctor.ui.emr_workflow.history.allergy.ui.AllergyFragment
import com.hmisdoctor.ui.emr_workflow.history.config.view.ConfigHistoryFragment
import com.hmisdoctor.ui.emr_workflow.history.diagnosis.view.HistoryDiagnosisFragment
import com.hmisdoctor.ui.emr_workflow.history.familyhistory.ui.HistoryFamilyFragment
import com.hmisdoctor.ui.emr_workflow.history.immunization.ui.HistoryImmunizationFragment
import com.hmisdoctor.ui.emr_workflow.history.lab.ui.HistoryLabFragment
import com.hmisdoctor.ui.emr_workflow.history.model.response.History
import com.hmisdoctor.ui.emr_workflow.history.model.response.HistoryResponce
import com.hmisdoctor.ui.emr_workflow.history.prescription.ui.HistoryPrescriptionFragment
import com.hmisdoctor.ui.emr_workflow.history.radiology.ui.HistoryRadiologFragment
import com.hmisdoctor.ui.emr_workflow.history.surgery.ui.HistorySurgeryFragment
import com.hmisdoctor.ui.emr_workflow.history.viewmodel.HistoryViewModel
import com.hmisdoctor.ui.emr_workflow.history.viewmodel.HistoryViewModelFactory
import com.hmisdoctor.ui.emr_workflow.history.vitals.model.ui.HistoryVitalsFragment
import com.hmisdoctor.ui.emr_workflow.model.EmrWorkFlowResponseModel
import com.hmisdoctor.ui.emr_workflow.view.NoRecordsFragment
import com.hmisdoctor.utils.Utils
import retrofit2.Response


class HistoryChildFragment : Fragment() {
    private var binding: FragmentHistoryChildBinding? = null
    private var viewModel: HistoryViewModel? = null
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history_child, container, false)

        utils = Utils(this.context!!)
        if (activity !is FragmentBackClick) {
            throw ClassCastException("Hosting activity must implement BackHandlerInterface")
        } else {
            fragmentBackClick = activity as FragmentBackClick?
        }
        viewModel = HistoryViewModelFactory(
            requireActivity().application
        ).create(HistoryViewModel::class.java)

        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        val facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        viewModel?.getHistoryList(facility_id!!,getHistoryAllResponseCallback)
        binding?.config?.setOnClickListener {
            val ft = requireActivity().supportFragmentManager.beginTransaction()
            val dialog =
                ConfigHistoryFragment()
            dialog.show(ft, "Tag")
        }
        return binding!!.root
    }

    private fun setupViewPager(tabsArrayList: List<History?>) {
        viewpageradapter = ViewPagerAdapter(childFragmentManager)
        for (i in tabsArrayList.indices) {
            if (tabsArrayList[i]?.activity_code == "Allergy") {
                viewpageradapter!!.addFragment(AllergyFragment(), "Allergy")
            } else if (tabsArrayList[i]?.activity_code == AppConstants.ACTIVITY_CODE_RADIOLOGY) {

                viewpageradapter!!.addFragment(HistoryRadiologFragment(), "Radiology")
            }else if (tabsArrayList[i]?.activity_code == AppConstants.ACTIVITY_CODE_PRESCRIPTION) {

                viewpageradapter!!.addFragment(HistoryPrescriptionFragment(), "Prescription")
            }  else if (tabsArrayList[i]!!.activity_code == AppConstants.ACTIVITY_CODE_FAMILY_HISTORY) {
                viewpageradapter!!.addFragment(HistoryFamilyFragment(), "Family History")
            }
            else if (tabsArrayList[i]!!.activity_code == AppConstants.ACTIVITY_CODE_LAB) {
                viewpageradapter!!.addFragment(HistoryLabFragment(), "Lab")
            } else if (tabsArrayList[i]!!.activity_code == AppConstants.ACTIVITY_CODE_VITALS) {

                viewpageradapter!!.addFragment(HistoryVitalsFragment(), "Vitals")
            } else if (tabsArrayList[i]!!.activity_code == "Surgury") {
                viewpageradapter!!.addFragment(HistorySurgeryFragment(), "Surgury")
            }
           else if (tabsArrayList[i]!!.activity_code == "IMM") {
                viewpageradapter!!.addFragment(HistoryImmunizationFragment(), "Immunization")
            } else if (tabsArrayList[i]!!.activity_code == AppConstants.ACTIVITY_CODE_DIAGNOSIS) {
                viewpageradapter!!.addFragment(HistoryDiagnosisFragment(), "Diagnosis")
            }
            /*else if(tabsArrayList[i]!!.activity_code == AppConstants.ACTIVITY_CODE_HISTORY){
               viewpageradapter!!.addFragment(HistoryFragment(),"Diagnosis")
           }*/
            /*else if(tabsArrayList[i]!!.activity_code == AppConstants.ACTIVITY_CODE_HISTORY){
                viewpageradapter!!.addFragment(HistoryFragment(),"Diagnosis")
            }*/
            else {
                viewpageradapter!!.addFragment(NoRecordsFragment(), "NoRecord")
            }
        }
        binding?.viewPager?.adapter = viewpageradapter
        viewpageradapter?.notifyDataSetChanged()
    }

    val getHistoryAllResponseCallback = object : RetrofitCallback<HistoryResponce> {
        override fun onSuccessfulResponse(response: Response<HistoryResponce>?) {
            //Log.e("DataHistory", responseBody.toString())
            if (response!!.body()?.responseContents?.isNotEmpty()!!) {
                binding?.contentLinearLayout?.visibility = View.VISIBLE
                //binding?.noDataFoundTextView?.visibility = View.INVISIBLE
                tabsArrayList = response.body()?.responseContents!!
                setupViewPager(tabsArrayList!!)
                binding?.tabLayout!!.setupWithViewPager(binding?.viewPager!!)
                for (i in tabsArrayList!!.indices) { val layoutInflater: View? = LayoutInflater.from(activity).inflate(R.layout.history_custom_tab_row, null, false)
                    //val tabImageView = layoutInflater?.findViewById(R.id.tabImageView) as ImageView
                    val tabTextView = layoutInflater!!.findViewById(R.id.tabTextView) as TextView
                    tabTextView.text = tabsArrayList!![i]?.activity_name
                    binding?.tabLayout?.getTabAt(i)!!.customView = layoutInflater
                }
            } else {
                binding?.contentLinearLayout?.visibility = View.INVISIBLE
                //binding?.noDataFoundTextView?.visibility = View.VISIBLE
            }
        }

        override fun onBadRequest(errorBody: Response<HistoryResponce>?) {
            //Log.e("DataHistory", "badRequest")
            val gson = GsonBuilder().create()
            val responseModel: EmrWorkFlowResponseModel
            try {
                responseModel = gson.fromJson(
                    errorBody!!.errorBody()!!.string(),
                    EmrWorkFlowResponseModel::class.java
                )
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    responseModel.message!!
                )
            } catch (e: Exception) {
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    getString(R.string.something_went_wrong)
                )
                e.printStackTrace()
            }

        }

        override fun onServerError(response: Response<*>?) {
            // Log.e("DataHistory", "servererr")
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }

        override fun onUnAuthorized() {
            //Log.e("DataHistory", "unAuth")
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.unauthorized)
            )
        }

        override fun onForbidden() {
            //Log.e("DataHistory", "forbidden")
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }

        override fun onFailure(failure: String?) {
            //Log.e("DataHistory", "failure")
            utils?.showToast(R.color.negativeToast, binding?.mainLayout!!, failure!!)
        }

        override fun onEverytime() {
            //Log.e("DataHistory", "everytime")
            viewModel!!.progress.value = 8
        }

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

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            return super.instantiateItem(container, position)

        }
    }



}

