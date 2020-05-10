package com.hmisdoctor.ui.emr_workflow.radiology.ui
import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.cardview.widget.CardView
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

import com.hmisdoctor.databinding.FragmentRadiologyChildBinding
import com.hmisdoctor.ui.emr_workflow.lab.model.LabToLocationResponse
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import com.hmisdoctor.ui.emr_workflow.model.templete.TempDetails

import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.search_complaint.ComplaintSearchResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.LabTypeResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.favresponse.FavSearchResponce
import com.hmisdoctor.ui.emr_workflow.lab.ui.ClearFavParticularPositionListener
import com.hmisdoctor.ui.emr_workflow.lab.ui.ClearTemplateParticularPositionListener
import com.hmisdoctor.ui.emr_workflow.lab.ui.TypeDropDownAdapter
import com.hmisdoctor.ui.emr_workflow.model.EMR_Request.Detail
import com.hmisdoctor.ui.emr_workflow.model.EMR_Request.EmrRequestModel
import com.hmisdoctor.ui.emr_workflow.model.EMR_Request.Header
import com.hmisdoctor.ui.emr_workflow.model.EMR_Response.EmrResponceModel
import com.hmisdoctor.ui.emr_workflow.model.fetch_encounters_response.FectchEncounterResponseModel
import com.hmisdoctor.ui.emr_workflow.model.templete.LabDetail
import com.hmisdoctor.ui.emr_workflow.radiology.ui.saveTemplate.ManageRadiologySaveTemplateFragment
import com.hmisdoctor.ui.emr_workflow.radiology.view_model.RadiologyViewModel
import com.hmisdoctor.ui.emr_workflow.radiology.view_model.RadiologyViewModelFactory
import com.hmisdoctor.ui.emr_workflow.view.lab.model.PodArrResult
import com.hmisdoctor.ui.emr_workflow.view.lab.ui.PrevRadiologyFragment
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository

import com.hmisdoctor.utils.Utils
import retrofit2.Response

class RadiologyChildFragment : Fragment(), RadiologyFavouriteFragment.RadiologyFavClickedListener,
    RadiologyTempleteFragment.RadiologyTempleteClickedListener,PrevRadiologyFragment.RadiologyPrevClickedListener {

    private var customdialog: Dialog?=null
    lateinit var drugNmae: TextView
    private var facility_id: Int? = 0

    private var searchPosition: Int? = 0

    private var arrayItemData: ArrayList<FavouritesModel?>? = null
    private var labtypeDropDownAdapter: TypeDropDownAdapter? = null
    private var binding: FragmentRadiologyChildBinding? = null
    private var viewModel: RadiologyViewModel? = null
    private var utils: Utils? = null
    private var responseContents: String? = ""
    private var favmodel: FavouritesModel? = null
    lateinit var dropdownReferenceView: AppCompatAutoCompleteTextView
    var radiologyAdapter: RadiologyAdapter? = null
    private var fragmentBackClick: FragmentBackClick? = null
    var appPreferences: AppPreferences? = null
    var arrayList : ArrayList<FavouritesModel>?=null
    var mCallbackRadiologyFavFragment: ClearFavParticularPositionListener? = null
    var mCallbackRadiologyTemplateFragment: ClearTemplateParticularPositionListener? = null
    val detailsList = ArrayList<Detail>()
    val header: Header? = Header()
    val emrRequestModel: EmrRequestModel? = EmrRequestModel()
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_radiology_child,
                container,
                false
            )


        if (activity !is FragmentBackClick) {
            throw ClassCastException("Hosting activity must implement BackHandlerInterface")
        } else {
            fragmentBackClick = activity as FragmentBackClick?
        }
        viewModel = RadiologyViewModelFactory(
            requireActivity().application
        ) .create(RadiologyViewModel::class.java)
        binding?.viewModel = viewModel
        binding!!.lifecycleOwner = this
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        userDetailsRoomRepository = UserDetailsRoomRepository(requireActivity().application)
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        appPreferences?.saveInt(AppConstants.LAB_MASTER_TYPE_ID,2)
        val labMasterTypeUUID = appPreferences?.getInt(AppConstants.LAB_MASTER_TYPE_ID)
         facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        val patient_UUID = appPreferences?.getInt(AppConstants.PATIENT_UUID)
        val department_UUID = appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)
        val encounter_Type = appPreferences?.getInt(AppConstants.ENCOUNTER_TYPE)
        val encounter_uuid = appPreferences?.getInt(AppConstants.ENCOUNTER_UUID)
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

        setAdapter()
        setupViewPager(binding?.viewpager!!)
        binding?.tabs!!.setupWithViewPager(binding?.viewpager!!)

        viewModel?.getLabType(getLabTypeRetrofitCallback, facility_id)
        viewModel?.getToLocation(getLabToLoctionRetrofitCallback, facility_id)



        binding!!.saveTemplate!!.setOnClickListener{

            val arrayItemData=radiologyAdapter!!.getAll()

    /*        if (arrayItemData?.size!! > 0) {
                arrayItemData!!.removeAt(arrayItemData!!.size - 1);
            }*/

            val ft = childFragmentManager.beginTransaction()

            val labtemplatedialog = ManageRadiologySaveTemplateFragment()

            val bundle = Bundle()

            bundle.putParcelableArrayList(AppConstants.RESPONSECONTENT, arrayItemData)

            labtemplatedialog.setArguments(bundle)

            labtemplatedialog.show(ft, "Tag")

        }

        binding?.saveCardView!!.setOnClickListener{

            arrayItemData =  radiologyAdapter?.getItems()

            if (arrayItemData?.size!! > 0) {
                arrayItemData!!.removeAt(arrayItemData!!.size - 1);
            }
            detailsList.clear()

            if (arrayItemData?.size!! > 0) {
                for (i in arrayItemData?.indices!!) {
                    val details: Detail = Detail()
                    details.encounter_type_uuid = encounter_Type!!.toInt()
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
                header?.department_uuid = userDataStoreBean?.department_uuid?.toString()!!
                header?.doctor_uuid = userDataStoreBean.uuid.toString()
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

                val requestmodel = Gson().toJson(emrRequestModel)

                Log.i("",""+requestmodel)

                viewModel?.RadiologyInsert(facility_id, emrRequestModel!!, emrpostRadiologyRetrofitCallback)
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


        return binding!!.root
    }

    private fun setupViewPager(viewPager: ViewPager) {

        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(RadiologyFavouriteFragment(), "  Favourite")
        adapter.addFragment(RadiologyTempleteFragment(), "  Templete")
        adapter.addFragment(PrevRadiologyFragment(), "Prev.Radiology")
        viewPager.offscreenPageLimit =2
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
        radiologyAdapter =
            RadiologyAdapter(
                requireActivity(),
                ArrayList(), ArrayList()
            )
        binding?.savelabRecyclerView?.adapter = radiologyAdapter

        radiologyAdapter?.addRow(FavouritesModel())
        radiologyAdapter?.addTempleteRow(TempDetails())



        radiologyAdapter?.setOnDeleteClickListener(object :
            RadiologyAdapter.OnDeleteClickListener {
            override fun onDeleteClick(responseContent: FavouritesModel?, position: Int) {

                Log.i("", "" + responseContent)
                customdialog = Dialog(requireContext())
                customdialog!! .requestWindowFeature(Window.FEATURE_NO_TITLE)
                customdialog!! .setCancelable(false)
                customdialog!! .setContentView(R.layout.delete_cutsom_layout)
                val closeImageView = customdialog!! .findViewById(R.id.closeImageView) as ImageView

                closeImageView.setOnClickListener {
                    customdialog?.dismiss()
                }
                drugNmae = customdialog!! .findViewById(R.id.addDeleteName) as TextView
                drugNmae.text ="${drugNmae.text.toString()} '"+responseContent!!.test_master_name+"' Record ?"
                val yesBtn = customdialog!! .findViewById(R.id.yes) as CardView
                val noBtn = customdialog!! .findViewById(R.id.no) as CardView
                yesBtn.setOnClickListener {

                    val check=radiologyAdapter?.deleteRow(position)

                    if (responseContent?.viewRadiologystatus == 1) {
                        mCallbackRadiologyFavFragment?.ClearFavParticularPosition(responseContent.isFavposition)

                    } else if (responseContent?.viewRadiologystatus == 2) {

                        if(check!!) {

                            mCallbackRadiologyTemplateFragment?.ClearTemplateParticularPosition(
                                responseContent.isTemposition
                            )

                        }
                        //template_id
                    }
                    customdialog!!.dismiss()


                }
                noBtn.setOnClickListener {
                    customdialog!! .dismiss()
                }
                customdialog!! .show()
            }
        })
        radiologyAdapter?.setOnSearchInitiatedListener(object :
            RadiologyAdapter.OnSearchInitiatedListener {
            override fun onSearchInitiated(query: String, view: AppCompatAutoCompleteTextView, position: Int) {
                dropdownReferenceView = view

                searchPosition=position
                viewModel?.getRadioSearchResult(facility_id,query, getComplaintSearchRetrofitCallBack)
            }
        })
    }
    val getComplaintSearchRetrofitCallBack =
        object : RetrofitCallback<FavSearchResponce> {
            override fun onSuccessfulResponse(response: Response<FavSearchResponce>) {
                responseContents = Gson().toJson(response.body()?.responseContents)
                if (response.body()?.responseContents?.isNotEmpty()!!) {
                    radiologyAdapter?.setAdapter(
                        dropdownReferenceView,
                        response.body()?.responseContents!!,searchPosition
                    )}
            }
            override fun onBadRequest(response: Response<FavSearchResponce>) {
                val gson = GsonBuilder().create()
                val responseModel: FavAddResponseModel
                try {

                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        FavAddResponseModel::class.java
                    )
                    utils?.showToast(
                        R.color.negativeToast,
                        binding?.mainLayout!!,
                        ""
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
            radiologyAdapter?.setadapterTypeValue(responseBody?.body()?.responseContents)
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

            radiologyAdapter?.setToLocationList(responseBody?.body()?.responseContents)
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
        if (childFragment is RadiologyFavouriteFragment) {
            childFragment.setOnTextClickedListener(this)
        }
        if (childFragment is RadiologyTempleteFragment) {
            childFragment.setOnTextClickedListener(this)
        }
        if (childFragment is PrevRadiologyFragment) {
            childFragment.setOnTextClickedListener(this)
        }
        if (childFragment is ClearFavParticularPositionListener) {
            mCallbackRadiologyFavFragment = childFragment as ClearFavParticularPositionListener
        }
        if (childFragment is ClearTemplateParticularPositionListener) {
            mCallbackRadiologyTemplateFragment = childFragment as ClearTemplateParticularPositionListener
        }
    }

    override fun sendFavAddInLab(favmodel: FavouritesModel?, position: Int, selected: Boolean) {
        favmodel?.viewRadiologystatus = 1
        favmodel?.isFavposition = position
        if (!selected) {
            radiologyAdapter!!.addFavouritesInRow(favmodel)
        } else {
            radiologyAdapter!!.deleteRowFromTemplate(favmodel?.test_master_id, position)
        }
    }

    override fun sendTemplete(
        templeteDetails: List<LabDetail?>?,
        position: Int,
        selected: Boolean
    ) {
        if (!selected) {

            for (i in templeteDetails!!.indices) {
                val favmodel: FavouritesModel? = FavouritesModel()
                favmodel?.viewRadiologystatus = 2
                favmodel?.isTemposition = position
                favmodel?.test_master_name = templeteDetails[i]!!.lab_name
                favmodel?.test_master_id=templeteDetails[i]!!.lab_test_uuid!!
                favmodel?.test_master_code=templeteDetails[i]!!.lab_code
                favmodel?.template_id=templeteDetails[i]!!.template_details_uuid!!
                radiologyAdapter!!.addFavouritesInRow(favmodel)
            }
        } else {
            for (i in templeteDetails!!.indices) {
                radiologyAdapter!!.deleteRowFromTemplate(templeteDetails[i]!!.lab_test_uuid, position)

            }


        }
    }

    val emrpostRadiologyRetrofitCallback = object : RetrofitCallback<EmrResponceModel> {
        override fun onSuccessfulResponse(responseBody: Response<EmrResponceModel>?) {

            Log.i("radioPost", "" + responseBody?.body()?.message)
            mCallbackRadiologyFavFragment?.ClearAllData()
            utils?.showToast(
                R.color.positiveToast,
                binding?.mainLayout!!,
                responseBody?.body()?.message!!
            )
            radiologyAdapter?.clearall()
            radiologyAdapter?.addRow(FavouritesModel())
            radiologyAdapter?.addTempleteRow(TempDetails())

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

            Log.i("ServerErr",response.message())

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

    override fun sendPrevtoChild(responseContent: List<PodArrResult>?) {


      //  drawer_layout.closeDrawer(GravityCompat.START)

        for (i in responseContent!!.indices) {
            val favmodel: FavouritesModel? = FavouritesModel()

            favmodel!!.test_master_name = responseContent[i]?.name
            favmodel!!.itemAppendString = responseContent[i]?.name
            favmodel!!.test_master_id=responseContent[i]?.test_master_uuid
            favmodel!!.test_master_code=responseContent[i]?.code
            favmodel.selectTypeUUID=responseContent[i].order_priority_uuid
            favmodel.selectToLocationUUID=responseContent[i].order_to_location_uuid
            radiologyAdapter!!.addFavouritesInRow(favmodel)

        }

    }



}


