package com.hmisdoctor.ui.emr_workflow.investigation.ui
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer

import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.FragmentBackClick
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.FragmentInvestigationChildBinding

import com.hmisdoctor.ui.emr_workflow.investigation.view_model.InvestigationViewModel
import com.hmisdoctor.ui.emr_workflow.investigation.view_model.InvestigationViewModelFactory
import com.hmisdoctor.ui.emr_workflow.lab.model.LabToLocationResponse
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import com.hmisdoctor.ui.emr_workflow.model.templete.TempDetails

import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.search_complaint.ComplaintSearchResponseModel

import com.hmisdoctor.ui.emr_workflow.investigation.model.InvestigationSearchResponseContent
import com.hmisdoctor.ui.emr_workflow.investigation.model.InvestigationSearchResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.LabTypeResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.ui.ClearFavParticularPositionListener

import com.hmisdoctor.ui.emr_workflow.lab.ui.TypeDropDownAdapter
import com.hmisdoctor.ui.emr_workflow.model.EMR_Request.Detail
import com.hmisdoctor.ui.emr_workflow.model.EMR_Request.EmrRequestModel
import com.hmisdoctor.ui.emr_workflow.model.EMR_Request.Header
import com.hmisdoctor.ui.emr_workflow.model.EMR_Response.EmrResponceModel
import com.hmisdoctor.ui.emr_workflow.view.lab.ui.PrevInvestigationFragment
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.CustomProgressDialog

import com.hmisdoctor.utils.Utils
import retrofit2.Response

class InvestigationChildFragment : Fragment(), InvestigationFavouriteFragment.FavClickedListener{


    private var arrayItemData: ArrayList<FavouritesModel?>? = null
    private var labtypeDropDownAdapter: TypeDropDownAdapter? = null
    private var binding: FragmentInvestigationChildBinding? = null
    private var viewModel: InvestigationViewModel? = null
    private var utils: Utils? = null
    private var responseContents: String? = ""
    private var favmodel: FavouritesModel? = null
    lateinit var dropdownReferenceView: AppCompatAutoCompleteTextView
    var investigationAdapter: InvestigationAdapter? = null
    private var fragmentBackClick: FragmentBackClick? = null
    var appPreferences: AppPreferences? = null
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    var mCallbackLabFavFragment: ClearFavParticularPositionListener? = null
    var arrayList : ArrayList<FavouritesModel>?=null
    private var facility_id: Int? = 0


    val detailsList= ArrayList<Detail>()

    val header: Header?= Header()

    private var customProgressDialog: CustomProgressDialog? = null

    val emrRequestModel: EmrRequestModel?= EmrRequestModel()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_investigation_child,
                container,
                false
            )

        if (activity !is FragmentBackClick) {
            throw ClassCastException("Hosting activity must implement BackHandlerInterface")
        } else {
            fragmentBackClick = activity as FragmentBackClick?
        }
        viewModel = InvestigationViewModelFactory(
            requireActivity().application
        )
            .create(InvestigationViewModel::class.java)
        binding?.viewModel = viewModel
        binding!!.lifecycleOwner = this
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        var facilityID = appPreferences?.getInt(AppConstants?.FACILITY_UUID)
        userDetailsRoomRepository = UserDetailsRoomRepository(requireActivity().application)
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        val labMasterTypeUUID = appPreferences?.getInt(AppConstants?.LAB_MASTER_TYPE_ID)
         facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        val patient_UUID = appPreferences?.getInt(AppConstants?.PATIENT_UUID)
        val encounter_Type = appPreferences?.getInt(AppConstants?.ENCOUNTER_TYPE)
        val department_UUID = appPreferences?.getInt(AppConstants?.DEPARTMENT_UUID)
        val encounter_uuid = appPreferences?.getInt(AppConstants?.ENCOUNTER_UUID)



        appPreferences?.saveInt(AppConstants.LAB_MASTER_TYPE_ID,3)
        utils = Utils(requireContext())

        viewModel!!.errorText.observe(viewLifecycleOwner,
            Observer { toastMessage ->
                utils!!.showToast(R.color.negativeToast, binding?.mainLayout!!, toastMessage)
            })

        binding?.favouriteDrawerCardView?.setOnClickListener {
            binding?.drawerLayout!!.openDrawer(GravityCompat.END)
        }

        binding?.drawerLayout?.drawerElevation = 0f
        binding?.drawerLayout?.setScrimColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )

        binding?.saveCardView?.isEnabled = true
        binding?.saveCardView?.alpha = 0.5f
        binding?.clearCardView?.isEnabled = true
        binding?.clearCardView?.alpha = 0.5f

        setAdapter()
        setupViewPager(binding?.viewpager!!)
        binding?.tabs!!.setupWithViewPager(binding?.viewpager!!)

        viewModel?.getLabType(getLabTypeRetrofitCallback, facilityID)
        viewModel?.getToLocation(getLabToLoctionRetrofitCallback, facilityID)
        binding?.saveCardView!!.setOnClickListener {

            arrayItemData = investigationAdapter?.getItems()
            if (arrayItemData?.size!! > 0) {
                arrayItemData!!.removeAt(arrayItemData!!.size - 1);
            }
            detailsList.clear()
            if (arrayItemData?.size!! > 0) {
                for (i in arrayItemData?.indices!!) {
                    val details: Detail = Detail()
                    details.encounter_type_uuid = encounter_Type!!.toInt()
                    details.encounter_type_uuid = 1
                    details.group_uuid = 0
                    details.is_profile = false
                    details.lab_master_type_uuid = labMasterTypeUUID!!.toInt()
                    details.order_priority_uuid = arrayItemData?.get(i)?.selectTypeUUID!!

                    details.profile_uuid = ""
                    details.test_master_uuid = arrayItemData?.get(i)?.test_master_id!!
                    details.to_department_uuid = department_UUID!!.toInt()
                    details.to_location_uuid =
                        arrayItemData?.get(i)?.selectToLocationUUID.toString()
                    details.to_sub_department_uuid = 0

                    detailsList.add(details)
                }

                header?.consultation_uuid = 0
                header?.department_uuid = department_UUID.toString()
                header?.doctor_uuid = userDataStoreBean?.uuid.toString()
                header?.encounter_type_uuid = encounter_Type!!.toInt()
                header?.encounter_uuid = encounter_uuid!!.toInt()
                header?.facility_uuid = facility_id.toString()
                header?.lab_master_type_uuid = labMasterTypeUUID!!.toInt()
                header?.order_status_uuid = 0
                header?.order_to_location_uuid = 1
                header?.patient_treatment_uuid = 0
                header?.patient_uuid = patient_UUID.toString()

                header?.sub_department_uuid = 0
                header?.treatment_plan_uuid = 0
                emrRequestModel?.header = this.header!!
                emrRequestModel?.details = this.detailsList

                viewModel?.investigationInsert(facility_id, emrRequestModel!!, emrpostRetrofitCallback)

            }

            else
            {
                utils?.showToast(
                    R.color.positiveToast,
                    binding?.mainLayout!!,
                    "Please select any one item"
                )
            }
        }



        binding?.clearCardView?.setOnClickListener({
            mCallbackLabFavFragment?.ClearAllData()
            investigationAdapter?.clearall()
            investigationAdapter?.addRow(FavouritesModel())
            investigationAdapter?.addTempleteRow(TempDetails())
        })

        return binding!!.root
    }

    private fun setupViewPager(viewPager: ViewPager) {

        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(InvestigationFavouriteFragment(), "  Favourite")
/*
        adapter.addFragment(InvestigationTempleteFragment(), "  Templete")
*/
        adapter.addFragment(PrevInvestigationFragment(), "Prev.Investigation")
        viewPager.offscreenPageLimit = 2

        viewPager.adapter = adapter

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

    private fun setAdapter() {
        investigationAdapter =
            InvestigationAdapter(
                requireActivity(),
                ArrayList(), ArrayList()
            )
        binding?.savelabRecyclerView?.adapter = investigationAdapter

        investigationAdapter?.addRow(FavouritesModel())
        investigationAdapter?.addTempleteRow(TempDetails())



        investigationAdapter?.setOnDeleteClickListener(object :
            InvestigationAdapter.OnDeleteClickListener {
            override fun onDeleteClick(responseContent: FavouritesModel?, position: Int) {
                investigationAdapter?.deleteRow(position)
            }
        })
        investigationAdapter?.setOnSearchInitiatedListener(object :
            InvestigationAdapter.OnSearchInitiatedListener {
            override fun onSearchInitiated(query: String, view: AppCompatAutoCompleteTextView) {
                dropdownReferenceView = view
                viewModel?.getInvestigationSearchResult(facility_id,query, getComplaintSearchRetrofitCallBack)
            }
        })
    }
    val getComplaintSearchRetrofitCallBack =
        object : RetrofitCallback<InvestigationSearchResponseModel> {
            override fun onSuccessfulResponse(response: Response<InvestigationSearchResponseModel>) {
                responseContents = Gson().toJson(response.body()?.responseContents)
                if (response.body()?.responseContents?.isNotEmpty()!!) {
                    investigationAdapter?.setAdapter(
                        dropdownReferenceView,
                        (response.body()?.responseContents as ArrayList<InvestigationSearchResponseContent>?)!!
                    )

                }
            }

            override fun onBadRequest(response: Response<InvestigationSearchResponseModel>) {
                val gson = GsonBuilder().create()
                val responseModel: InvestigationSearchResponseModel
                try {

                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        InvestigationSearchResponseModel::class.java
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
                viewModel!!.progress.value = 8
            }
        }


    val getLabTypeRetrofitCallback = object : RetrofitCallback<LabTypeResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<LabTypeResponseModel>?) {


            investigationAdapter?.setadapterTypeValue(responseBody?.body()?.responseContents)


        }

        override fun onBadRequest(errorBody: Response<LabTypeResponseModel>?) {

        }

        override fun onServerError(response: Response<*>?) {
        }

        override fun onUnAuthorized() {

        }

        override fun onForbidden() {

        }

        override fun onFailure(s: String?) {

        }

        override fun onEverytime() {

        }

    }


    val getLabToLoctionRetrofitCallback = object : RetrofitCallback<LabToLocationResponse> {
        override fun onSuccessfulResponse(responseBody: Response<LabToLocationResponse>?) {

            investigationAdapter?.setToLocationList(responseBody?.body()?.responseContents)
        }

        override fun onBadRequest(errorBody: Response<LabToLocationResponse>?) {
        }

        override fun onServerError(response: Response<*>?) {
        }

        override fun onUnAuthorized() {

        }

        override fun onForbidden() {
        }

        override fun onFailure(s: String?) {
        }

        override fun onEverytime() {
        }

    }




    override fun onStart() {
        super.onStart()
        fragmentBackClick!!.setSelectedFragment(this)
    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        if (childFragment is InvestigationFavouriteFragment) {
            childFragment.setOnTextClickedListener(this)
        }
        if (childFragment is ClearFavParticularPositionListener) {
            mCallbackLabFavFragment = childFragment as ClearFavParticularPositionListener
        }

/*
        if (childFragment is InvestigationTempleteFragment) {
            childFragment.setOnTextClickedListener(this)
        }
*/
    }

    override fun sendData(favmodel: FavouritesModel?, position: Int, selected: Boolean) {
        if (!selected) {
            investigationAdapter!!.addFavouritesInRow(favmodel)
        } else {
            investigationAdapter!!.deleteRow(position)

        }
    }

/*
    override fun sendTemplete(templeteDetails: TempDetails?, position: Int, selected: Boolean) {
        if (!selected) {

            val favmodel: FavouritesModel? = FavouritesModel()
            favmodel?.test_master_name = templeteDetails?.template_name
            investigationAdapter!!.addFavouritesInRow(favmodel)
        } else {
            investigationAdapter!!.deleteRow(position)

        }
    }
*/



    val emrpostRetrofitCallback = object : RetrofitCallback<EmrResponceModel> {
        override fun onSuccessfulResponse(responseBody: Response<EmrResponceModel>?) {

            Log.i("res", "" + responseBody?.body()?.message)
            mCallbackLabFavFragment?.ClearAllData()
            utils?.showToast(
                R.color.positiveToast,
                binding?.mainLayout!!,
                responseBody?.body()?.message!!
            )
            investigationAdapter?.clearall()
            investigationAdapter?.addRow(FavouritesModel())
            investigationAdapter?.addTempleteRow(TempDetails())

            //setAdapter()
        }

        override fun onBadRequest(response: Response<EmrResponceModel>?) {
            val gson = GsonBuilder().create()
            val responseModel: ComplaintSearchResponseModel
            try {

                responseModel = gson.fromJson(
                    response!!.errorBody()!!.string(),
                    ComplaintSearchResponseModel::class.java
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
            viewModel!!.progress.value = 8
        }


    }

    private val investigationInsertRetrofitCallback=object :RetrofitCallback<EmrResponceModel>{
        override fun onSuccessfulResponse(responseBody: Response<EmrResponceModel>?) {

            Log.i("res",""+responseBody?.body()?.message)
            utils?.showToast(
                R.color.positiveToast,
                binding?.mainLayout!!,
                responseBody?.body()?.message!!
            )
            investigationAdapter?.clearall()
            investigationAdapter?.addRow(FavouritesModel())
            investigationAdapter?.addTempleteRow(TempDetails())

        }

        override fun onBadRequest(response: Response<EmrResponceModel>?) {
            val gson = GsonBuilder().create()
            val responseModel: EmrResponceModel
            try {

                responseModel = gson.fromJson(
                    response!!.errorBody()!!.string(),
                    EmrResponceModel::class.java
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
            viewModel!!.progress.value = 8
        }


    }
}


