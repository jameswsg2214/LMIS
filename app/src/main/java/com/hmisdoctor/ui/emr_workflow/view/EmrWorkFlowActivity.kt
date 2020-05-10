package com.hmisdoctor.ui.emr_workflow.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.FragmentBackClick
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppConstants.Companion.ACTIVITY_CODE_ADMISSION
import com.hmisdoctor.config.AppConstants.Companion.ACTIVITY_CODE_CERTIFICATE
import com.hmisdoctor.config.AppConstants.Companion.ACTIVITY_CODE_CHIEF_COMPLAINTS
import com.hmisdoctor.config.AppConstants.Companion.ACTIVITY_CODE_DIAGNOSIS
import com.hmisdoctor.config.AppConstants.Companion.ACTIVITY_CODE_DOCUMENT
import com.hmisdoctor.config.AppConstants.Companion.ACTIVITY_CODE_HISTORY
import com.hmisdoctor.config.AppConstants.Companion.ACTIVITY_CODE_INVESTIGATION
import com.hmisdoctor.config.AppConstants.Companion.ACTIVITY_CODE_INVESTIGATION_RESULT
import com.hmisdoctor.config.AppConstants.Companion.ACTIVITY_CODE_LAB
import com.hmisdoctor.config.AppConstants.Companion.ACTIVITY_CODE_LAB_RESULT
import com.hmisdoctor.config.AppConstants.Companion.ACTIVITY_CODE_OP_NOTES
import com.hmisdoctor.config.AppConstants.Companion.ACTIVITY_CODE_PRESCRIPTION
import com.hmisdoctor.config.AppConstants.Companion.ACTIVITY_CODE_RADIOLOGY
import com.hmisdoctor.config.AppConstants.Companion.ACTIVITY_CODE_RADIOLOGY_RESULT
import com.hmisdoctor.config.AppConstants.Companion.ACTIVITY_CODE_TREATMENT_KIT
import com.hmisdoctor.config.AppConstants.Companion.ACTIVITY_CODE_VITALS
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.ActivityEmrWorkflowBinding
import com.hmisdoctor.ui.emr_workflow.admission_referal.ui.AdmissionFragment
import com.hmisdoctor.ui.emr_workflow.certificate.ui.CertificateFragment
import com.hmisdoctor.ui.emr_workflow.blood_request.ui.BloodRequestFragment
import com.hmisdoctor.ui.emr_workflow.chief_complaint.ui.ChiefComplaintsFragment
import com.hmisdoctor.ui.emr_workflow.chief_complaint.ui.DocumentFragment
import com.hmisdoctor.ui.emr_workflow.chief_complaint.ui.LabFragment
import com.hmisdoctor.ui.emr_workflow.diagnosis.view.DiagnosisFragment
import com.hmisdoctor.ui.emr_workflow.history.ui.HistoryFragment
import com.hmisdoctor.ui.emr_workflow.investigation.ui.InvestigationFragment
import com.hmisdoctor.ui.emr_workflow.investigation_result.ui.InvestigationResultFragment
import com.hmisdoctor.ui.emr_workflow.lab_result.ui.LabResultFragment

import com.hmisdoctor.ui.emr_workflow.model.EmrWorkFlowResponseModel
import com.hmisdoctor.ui.emr_workflow.model.GetStoreMasterResponseModel
import com.hmisdoctor.ui.emr_workflow.model.ResponseContent
import com.hmisdoctor.ui.emr_workflow.model.StoreMasterresponseContent
import com.hmisdoctor.ui.emr_workflow.model.create_encounter_response.CreateEncounterResponseModel
import com.hmisdoctor.ui.emr_workflow.model.fetch_encounters_response.FectchEncounterResponseModel
import com.hmisdoctor.ui.emr_workflow.model.fetch_encounters_response.FetchEncounterResponseContent
import com.hmisdoctor.ui.emr_workflow.op_notes.ui.OpNotesChildFragment
import com.hmisdoctor.ui.emr_workflow.op_notes.ui.OpNotesFragment
import com.hmisdoctor.ui.emr_workflow.prescription.ui.PrescriptionFragment
import com.hmisdoctor.ui.emr_workflow.radiology.ui.RadiologyFragment
import com.hmisdoctor.ui.emr_workflow.radiology_result.ui.RadiologyResultFragment
import com.hmisdoctor.ui.emr_workflow.treatment_kit.ui.TreatmentKitFragment

import com.hmisdoctor.ui.emr_workflow.view_model.EmrViewModel
import com.hmisdoctor.ui.emr_workflow.view_model.EmrViewModelFactory
import com.hmisdoctor.ui.emr_workflow.vitals.ui.VitalsFragment
import com.hmisdoctor.utils.Utils
import retrofit2.Response

class EmrWorkFlowActivity : AppCompatActivity(), FragmentBackClick {
    private var encounter_uuid: Int? = 0
    private var encounter_doctor_uuid: Int? = 0
    private var viewpageradapter: ViewPagerAdapter? = null
    private var tabsArrayList: List<ResponseContent?>? = null
    private var binding: ActivityEmrWorkflowBinding? = null
    private var viewModel: EmrViewModel? = null
    private var utils: Utils? = null
    private var selectedFragment: Fragment? = null
    lateinit var encounterResponseContent: List<FetchEncounterResponseContent?>
    private lateinit var getStoreMasterId: List<StoreMasterresponseContent?>
    private var patientUuid: Int = 0
    private var encounterType: Int = 0
    private var facility_id: Int = 0
    private var department_uuid: Int = 0
    private var store_master_uuid: Int = 0

    var appPreferences: AppPreferences? = null

    override fun setSelectedFragment(fragment: Fragment?) {
        this.selectedFragment = fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_emr_workflow)
        viewModel = EmrViewModelFactory(
            application
        ).create(EmrViewModel::class.java)
        binding?.viewModel = viewModel
        binding!!.lifecycleOwner = this
        appPreferences = AppPreferences.getInstance(this, AppConstants.SHARE_PREFERENCE_NAME)
        patientUuid = intent.getIntExtra(AppConstants.PATIENT_UUID, 0)
        encounterType = intent.getIntExtra(AppConstants.ENCOUNTER_TYPE, 0)

        facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)!!
        department_uuid = appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)!!

        utils = Utils(this)
        binding?.viewPager?.offscreenPageLimit
        viewModel!!.errorText.observe(this,
            Observer { toastMessage ->
                utils!!.showToast(R.color.negativeToast, binding?.mainLayout!!, toastMessage)
            })

        binding?.contentLinearLayout?.visibility = View.INVISIBLE
        binding?.noDataFoundTextView?.visibility = View.INVISIBLE

        setSupportActionBar(binding!!.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        binding!!.toolbar.setNavigationOnClickListener {
            finish()
        }

        viewModel?.getEmrWorkFlow(emrWorkFlowRetrofitCallBack)
        viewModel?.getEncounter(patientUuid, encounterType, fetchEncounterRetrofitCallBack)
        viewModel?.getStoreMaster(facility_id,department_uuid,getStoreMasterRetrofitCallback)

       // viewModel?.getStore(facility_id!!,storeCallback)

    }

    private fun setupViewPager(tabsArrayList: List<ResponseContent?>) {
        viewpageradapter = ViewPagerAdapter(supportFragmentManager)
        for (i in tabsArrayList.indices) {
            if (tabsArrayList[i]?.activity_code == ACTIVITY_CODE_CHIEF_COMPLAINTS) {

                viewpageradapter!!.addFragment(ChiefComplaintsFragment(), "Chief Complaints")
            } else if (tabsArrayList[i]?.activity_code == ACTIVITY_CODE_LAB) {

                viewpageradapter!!.addFragment(LabFragment(), "Lab")
            } else if (tabsArrayList[i]!!.activity_code == ACTIVITY_CODE_RADIOLOGY) {

                viewpageradapter!!.addFragment(RadiologyFragment(), "Radiology")
            } else if (tabsArrayList[i]!!.activity_code == ACTIVITY_CODE_INVESTIGATION) {
                viewpageradapter!!.addFragment(InvestigationFragment(), "Investigation")
            }
            else if (tabsArrayList[i]!!.activity_code == ACTIVITY_CODE_DIAGNOSIS) {
                viewpageradapter!!.addFragment(DiagnosisFragment(), "Diagnosis")
            }

            else if(tabsArrayList[i]!!.activity_code == ACTIVITY_CODE_HISTORY){
                viewpageradapter!!.addFragment(HistoryFragment(),"History")
            }

            else if (tabsArrayList[i]!!.activity_code == ACTIVITY_CODE_PRESCRIPTION) {
                viewpageradapter!!.addFragment(PrescriptionFragment(), "Prescription")
            }
            else if (tabsArrayList[i]!!.activity_code == ACTIVITY_CODE_VITALS) {
                viewpageradapter!!.addFragment(VitalsFragment(), "Vitals")
            }
            else if (tabsArrayList[i]!!.activity_code == ACTIVITY_CODE_LAB_RESULT) {
                viewpageradapter!!.addFragment(LabResultFragment(), "Lab Result")
            }else if (tabsArrayList[i]!!.activity_code == ACTIVITY_CODE_RADIOLOGY_RESULT) {
                viewpageradapter!!.addFragment(RadiologyResultFragment(), "Radiology Result")
            }else if (tabsArrayList[i]!!.activity_code == ACTIVITY_CODE_TREATMENT_KIT) {
                viewpageradapter!!.addFragment(TreatmentKitFragment(), "Treatment Kit")
            }else if (tabsArrayList[i]!!.activity_code == ACTIVITY_CODE_OP_NOTES) {
                viewpageradapter!!.addFragment(OpNotesFragment(), "Op Notes")
            } else if (tabsArrayList[i]!!.activity_code == ACTIVITY_CODE_DOCUMENT) {
                viewpageradapter!!.addFragment(DocumentFragment(), "Documents")
            }else if (tabsArrayList[i]!!.activity_code == ACTIVITY_CODE_INVESTIGATION_RESULT) {
                viewpageradapter!!.addFragment(InvestigationResultFragment(), "Investigation Result")
            }else if (tabsArrayList[i]!!.activity_code == ACTIVITY_CODE_ADMISSION) {
                viewpageradapter!!.addFragment(AdmissionFragment(), "Admission/Refferal")
            }else if (tabsArrayList[i]!!.activity_code == ACTIVITY_CODE_CERTIFICATE) {
                viewpageradapter!!.addFragment(CertificateFragment(), "Certificate")
//            } else if(tabsArrayList[i]!!.activity_code == ACTIVITY_CODE_BLOOD_REQUEST) {  //Sri activity code is null
            } else if(tabsArrayList[i]!!.activity_id == 252) {
                viewpageradapter!!.addFragment(BloodRequestFragment(), "Blood Request")
            }
            else {
                viewpageradapter!!.addFragment(CertificateFragment(), "Certificate")
            }
        }
        binding?.viewPager?.adapter = viewpageradapter
        viewpageradapter?.notifyDataSetChanged()
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

    private val emrWorkFlowRetrofitCallBack = object : RetrofitCallback<EmrWorkFlowResponseModel> {
        override fun onSuccessfulResponse(response: Response<EmrWorkFlowResponseModel>) {
            if (response.body()?.responseContents?.isNotEmpty()!!) {
                binding?.contentLinearLayout?.visibility = View.VISIBLE
                binding?.noDataFoundTextView?.visibility = View.INVISIBLE
                tabsArrayList = response.body()?.responseContents!!
                setupViewPager(tabsArrayList!!)
                binding?.tabLayout!!.setupWithViewPager(binding?.viewPager!!)
                for (i in tabsArrayList!!.indices) {
                    val layoutInflater: View? =
                        LayoutInflater.from(this@EmrWorkFlowActivity)
                            .inflate(R.layout.treatment_custom_tab_row, null, false)
                    val tabImageView = layoutInflater?.findViewById(R.id.tabImageView) as ImageView
                    val tabTextView = layoutInflater.findViewById(R.id.tabTextView) as TextView
                    tabTextView.text = tabsArrayList!![i]?.activity_name
                    binding?.tabLayout?.getTabAt(i)!!.customView = layoutInflater
                }
            } else {
                binding?.contentLinearLayout?.visibility = View.INVISIBLE
                binding?.noDataFoundTextView?.visibility = View.VISIBLE
            }
        }

        override fun onBadRequest(response: Response<EmrWorkFlowResponseModel>) {
            val gson = GsonBuilder().create()
            val responseModel: EmrWorkFlowResponseModel
            try {
                responseModel = gson.fromJson(
                    response.errorBody()!!.string(),
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

        override fun onServerError(response: Response<*>) {
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }

        override fun onUnAuthorized() {
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.unauthorized)
            )
        }

        override fun onForbidden() {
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }

        override fun onFailure(failure: String) {
            utils?.showToast(R.color.negativeToast, binding?.mainLayout!!, failure)
        }

        override fun onEverytime() {
            viewModel!!.progressBar.value = 8
        }
    }
    private val fetchEncounterRetrofitCallBack =
        object : RetrofitCallback<FectchEncounterResponseModel> {
            override fun onSuccessfulResponse(response: Response<FectchEncounterResponseModel>) {
                if (response.body()?.responseContents?.isNotEmpty()!!) {
                    encounterResponseContent = response.body()?.responseContents!!
                    encounter_doctor_uuid = encounterResponseContent.get(0)?.encounter_doctors?.get(0)?.uuid
                    encounter_uuid = encounterResponseContent.get(0)?.uuid
                    appPreferences?.saveInt(AppConstants.ENCOUNTER_DOCTOR_UUID,encounter_doctor_uuid!!)
                    appPreferences?.saveInt(AppConstants.ENCOUNTER_UUID,encounter_uuid!!)


                } else {
                    viewModel?.createEncounter(
                        patientUuid,
                        encounterType,
                        createEncounterRetrofitCallback
                    )
                } }
            override fun onBadRequest(response: Response<FectchEncounterResponseModel>) {
                val gson = GsonBuilder().create()
                val responseModel: FectchEncounterResponseModel
                try {
                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        FectchEncounterResponseModel::class.java
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

            override fun onServerError(response: Response<*>) {
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    getString(R.string.something_went_wrong)
                )
            }

            override fun onUnAuthorized() {
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    getString(R.string.unauthorized)
                )
            }

            override fun onForbidden() {
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    getString(R.string.something_went_wrong)
                )
            }

            override fun onFailure(failure: String) {
                utils?.showToast(R.color.negativeToast, binding?.mainLayout!!, failure)
            }
            override fun onEverytime() {
                viewModel!!.progressBar.value = 8
            }
        }

    val createEncounterRetrofitCallback = object : RetrofitCallback<CreateEncounterResponseModel> {
        override fun onSuccessfulResponse(response: Response<CreateEncounterResponseModel>) {

            encounter_doctor_uuid = response?.body()?.responseContents?.encounterDoctor?.doctor_uuid!!.toInt()
            encounter_uuid = response?.body()?.responseContents?.encounterDoctor?.uuid!!.toInt()
            patientUuid= response?.body()?.responseContents?.encounterDoctor?.patient_uuid!!.toInt()
            appPreferences?.saveInt(AppConstants.ENCOUNTER_DOCTOR_UUID,encounter_doctor_uuid!!)
            appPreferences?.saveInt(AppConstants.ENCOUNTER_UUID,encounter_uuid!!)
            appPreferences?.saveInt(AppConstants.PATIENT_UUID,patientUuid!!)

        }

        override fun onBadRequest(response: Response<CreateEncounterResponseModel>) {
            val gson = GsonBuilder().create()
            val responseModel: CreateEncounterResponseModel
            try {
                responseModel = gson.fromJson(
                    response.errorBody()!!.string(),
                    CreateEncounterResponseModel::class.java
                )
             /*   utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    responseModel.message!!
                )*/
            } catch (e: Exception) {
             /*   utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    getString(R.string.something_went_wrong)
                )*/
                e.printStackTrace()
            }
        }

        override fun onServerError(response: Response<*>) {
           /* utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )*/
        }
        override fun onUnAuthorized() {
        /*    utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.unauthorized)
            )*/
        }

        override fun onForbidden() {
         /*   utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )*/
        }

        override fun onFailure(failure: String) {
//            utils?.showToast(R.color.negativeToast, binding?.mainLayout!!, failure)
        }

        override fun onEverytime() {
            viewModel!!.progressBar.value = 8
        }
    }
    val getStoreMasterRetrofitCallback = object :RetrofitCallback<GetStoreMasterResponseModel>{
        override fun onSuccessfulResponse(response: Response<GetStoreMasterResponseModel>) {

            if (response.body()?.responseContents?.isNotEmpty()!!)
            {
                getStoreMasterId = response.body()?.responseContents!!
                store_master_uuid = getStoreMasterId[getStoreMasterId.size - 1]?.store_master_uuid!!.toInt()
                appPreferences?.saveInt(AppConstants.STOREMASTER_UUID,store_master_uuid)
            }
        }

        override fun onBadRequest(response: Response<GetStoreMasterResponseModel>) {
            val gson = GsonBuilder().create()
            val responseModel: GetStoreMasterResponseModel
            try {
                responseModel = gson.fromJson(
                    response.errorBody()!!.string(),
                    GetStoreMasterResponseModel::class.java
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

        override fun onServerError(response: Response<*>) {
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }
        override fun onUnAuthorized() {
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.unauthorized)
            )
        }

        override fun onForbidden() {
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }

        override fun onFailure(failure: String) {
            utils?.showToast(R.color.negativeToast, binding?.mainLayout!!, failure)
        }

        override fun onEverytime() {
            viewModel!!.progressBar.value = 8
        }
    }

}