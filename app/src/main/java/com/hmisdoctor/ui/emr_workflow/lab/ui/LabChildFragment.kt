package com.hmisdoctor.ui.emr_workflow.lab.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.BoringLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.FragmentBackClick
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.FragmentLabChildBinding
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.search_complaint.ComplaintSearchResponseModel
import com.hmisdoctor.ui.emr_workflow.chief_complaint.ui.Chief_Complaint_FavouritesAdapter
import com.hmisdoctor.ui.emr_workflow.chief_complaint.view_model.LabViewModelFactory
import com.hmisdoctor.ui.emr_workflow.lab.model.LabToLocationResponse
import com.hmisdoctor.ui.emr_workflow.lab.model.LabTypeResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.favresponse.FavSearchResponce
import com.hmisdoctor.ui.emr_workflow.lab.ui.saveTemplate.ManageLabSaveTemplateFragment
import com.hmisdoctor.ui.emr_workflow.lab.view_model.LabViewModel
import com.hmisdoctor.ui.emr_workflow.model.EMR_Request.Detail
import com.hmisdoctor.ui.emr_workflow.model.EMR_Request.EmrRequestModel
import com.hmisdoctor.ui.emr_workflow.model.EMR_Request.Header
import com.hmisdoctor.ui.emr_workflow.model.EMR_Response.EmrResponceModel
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import com.hmisdoctor.ui.emr_workflow.model.templete.LabDetail
import com.hmisdoctor.ui.emr_workflow.model.templete.TempDetails
import com.hmisdoctor.ui.emr_workflow.view.lab.model.PodArrResult

import com.hmisdoctor.ui.emr_workflow.view.lab.ui.PrevLabFragment

import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.CustomProgressDialog
import com.hmisdoctor.utils.Utils
import retrofit2.Response

class LabChildFragment : Fragment(), LabFavouriteFragment.FavClickedListener,
    LabTempleteFragment.TempleteClickedListener,PrevLabFragment.LabPrevClickedListener,ManageLabSaveTemplateFragment.LabChiefComplaintListener {

    private var arrayItemData: ArrayList<FavouritesModel?>? = null
    private var binding: FragmentLabChildBinding? = null
    lateinit var labFavouritesAdapter: LabFavouritesAdapter
    lateinit var drugNmae: TextView
    private var viewModel: LabViewModel? = null
    private var utils: Utils? = null
    private var customdialog: Dialog?=null
    private var responseContents: String? = ""
    lateinit var dropdownReferenceView: AppCompatAutoCompleteTextView
    var labAdapter: LabAdapter? = null
    private var fragmentBackClick: FragmentBackClick? = null
    var appPreferences: AppPreferences? = null
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null

    val detailsList = ArrayList<Detail>()

    var facility_id:Int=0

    var searchposition:Int=0


    val header: Header? = Header()

    private var customProgressDialog: CustomProgressDialog? = null

    val emrRequestModel: EmrRequestModel? = EmrRequestModel()
    var mCallbackLabFavFragment: ClearFavParticularPositionListener? = null
    var mCallbackLabTemplateFragment: ClearTemplateParticularPositionListener? = null


    var prevOrder:Boolean=false
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_lab_child,
                container,
                false
            )

        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        userDetailsRoomRepository = UserDetailsRoomRepository(requireActivity().application)
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        appPreferences?.saveInt(AppConstants?.LAB_MASTER_TYPE_ID,1)

        val labMasterTypeUUID = appPreferences?.getInt(AppConstants.LAB_MASTER_TYPE_ID)
        facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)!!
        val patient_UUID = appPreferences?.getInt(AppConstants?.PATIENT_UUID)
        val department_UUID = appPreferences?.getInt(AppConstants?.DEPARTMENT_UUID)
        val encounter_Type = appPreferences?.getInt(AppConstants?.ENCOUNTER_TYPE)
        val encounter_uuid = appPreferences?.getInt(AppConstants?.ENCOUNTER_UUID)
        binding?.viewpager?.offscreenPageLimit

        customProgressDialog = CustomProgressDialog(this.context)

        if (activity !is FragmentBackClick) {
            throw ClassCastException("Hosting activity must implement BackHandlerInterface")
        } else {
            fragmentBackClick = activity as FragmentBackClick?
        }


        viewModel = LabViewModelFactory(
            requireActivity().application
        )
            .create(LabViewModel::class.java)
        binding?.viewModel = viewModel
        binding!!.lifecycleOwner = this

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

        viewModel!!.progress.observe(
            this.viewLifecycleOwner,
            Observer { progress ->
                if (progress == View.VISIBLE) {
                    customProgressDialog!!.show()
                } else if (progress == View.GONE) {
                    customProgressDialog!!.dismiss()
                }
            })

        binding?.saveCardView!!.setOnClickListener {

            arrayItemData = labAdapter?.getItems()

            if(prevOrder){

                detailsList.clear()

                for (i in arrayItemData?.indices!!) {

                    val data=arrayItemData!!.size
                    if(i!=data-1) {
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

                viewModel?.labInsert(facility_id, emrRequestModel!!, emrpostRetrofitCallback)
            }
            else {
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
                        details.order_priority_uuid =
                            arrayItemData?.get(i)?.selectTypeUUID!!

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

                    viewModel?.labInsert(facility_id, emrRequestModel!!, emrpostRetrofitCallback)

                } else {
                    utils?.showToast(
                        R.color.positiveToast,
                        binding?.mainLayout!!,
                        "Please select any one item"
                    )
                }
            }




            // jehfjehdih
        }
        binding?.clearCardView?.setOnClickListener({
            mCallbackLabFavFragment?.ClearAllData()
            labAdapter?.clearall()
            labAdapter?.addRow(FavouritesModel())
            labAdapter?.addTempleteRow(TempDetails())
        })

        binding?.saveTemplateCardView!!.setOnClickListener {
            val arrayItemData=labAdapter!!.getAll()
           /* arrayItemData.add(FavouritesModel())

           if (arrayItemData?.size!! > 1) {
                arrayItemData!!.removeAt(arrayItemData!!.size - 1);
            }*/
            val ft = childFragmentManager.beginTransaction()
            val labtemplatedialog = ManageLabSaveTemplateFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(AppConstants.RESPONSECONTENT, arrayItemData)
            labtemplatedialog.setArguments(bundle)
            labtemplatedialog.show(ft, "Tag")


          /*  labAdapter =
                LabAdapter(
                    requireActivity(),
                    ArrayList(), ArrayList()
                )
            binding?.savelabRecyclerView?.adapter = labAdapter

            labAdapter?.addRow(FavouritesModel())
            labAdapter?.addTempleteRow(TempDetails())*/
        }
        return binding!!.root
    }
    private fun setupViewPager(viewPager: ViewPager) {

        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(LabFavouriteFragment(), "Favourite")
        adapter.addFragment(LabTempleteFragment(), "Templete")
        adapter.addFragment(PrevLabFragment(), "Prev.Lab")
        viewPager?.offscreenPageLimit =3
        viewPager.adapter = adapter

    }

    internal inner class ViewPagerAdapter(manager: FragmentManager) :

        FragmentStatePagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
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
        labAdapter =
            LabAdapter(
                requireActivity(),
                ArrayList(), ArrayList()
            )
        binding?.savelabRecyclerView?.adapter = labAdapter

        labAdapter?.addRow(FavouritesModel())
        labAdapter?.addTempleteRow(TempDetails())

        labAdapter?.setOnDeleteClickListener(object :
            LabAdapter.OnDeleteClickListener {
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
                drugNmae.text ="${drugNmae.text.toString()} '"+responseContent?.test_master_name+"' Record ?"
                val yesBtn = customdialog!! .findViewById(R.id.yes) as CardView
                val noBtn = customdialog!! .findViewById(R.id.no) as CardView
                yesBtn.setOnClickListener {
                    val check=labAdapter?.deleteRow(position)
                    if (responseContent?.viewLabstatus == 1) {
                        mCallbackLabFavFragment?.ClearFavParticularPosition(responseContent.isFavposition)

                    } else if (responseContent?.viewLabstatus == 2) {

                        if(check!!) {

                            mCallbackLabTemplateFragment?.ClearTemplateParticularPosition(
                                responseContent.isTemposition
                            )}
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
        labAdapter?.setOnSearchInitiatedListener(object :
            LabAdapter.OnSearchInitiatedListener {
            override fun onSearchInitiated(query: String, view: AppCompatAutoCompleteTextView, position: Int) {
                dropdownReferenceView = view
                searchposition=position
                viewModel?.getComplaintSearchResult(facility_id,query, getComplaintSearchRetrofitCallBack)
            }
        })


    }

    val emrpostRetrofitCallback = object : RetrofitCallback<EmrResponceModel> {
        override fun onSuccessfulResponse(responseBody: Response<EmrResponceModel>?) {

            Log.i("res", "" + responseBody?.body()?.message)
            mCallbackLabFavFragment?.ClearAllData()
            mCallbackLabTemplateFragment?.ClearAllData()
            utils?.showToast(
                R.color.positiveToast,
                binding?.mainLayout!!,
                responseBody?.body()?.message!!
            )
            labAdapter?.clearall()
            labAdapter?.addRow(FavouritesModel())
            labAdapter?.addTempleteRow(TempDetails())

            prevOrder=false

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

    val getComplaintSearchRetrofitCallBack =
        object : RetrofitCallback<FavSearchResponce> {
            override fun onSuccessfulResponse(response: Response<FavSearchResponce>) {
                responseContents = Gson().toJson(response.body()?.responseContents)
                if (response.body()?.responseContents?.isNotEmpty()!!) {
                    labAdapter?.setAdapter(
                        dropdownReferenceView,
                        response.body()?.responseContents!!,searchposition
                    )

                }
            }

            override fun onBadRequest(response: Response<FavSearchResponce>) {
                val gson = GsonBuilder().create()
                val responseModel: ComplaintSearchResponseModel
                try {

                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
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


    val getLabTypeRetrofitCallback = object : RetrofitCallback<LabTypeResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<LabTypeResponseModel>?) {
            labAdapter?.setadapterTypeValue(responseBody?.body()?.responseContents)
            viewModel?.getToLocation(getLabToLoctionRetrofitCallback, facility_id)
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

            viewModel!!.progress.value = 8

        }

    }
    val getLabToLoctionRetrofitCallback = object : RetrofitCallback<LabToLocationResponse> {

        override fun onSuccessfulResponse(responseBody: Response<LabToLocationResponse>?) {

            labAdapter?.setToLocationList(responseBody?.body()?.responseContents)

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

            viewModel!!.progress.value = 8
        }

    }

    override fun onStart() {
        super.onStart()
        fragmentBackClick!!.setSelectedFragment(this)
    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        if (childFragment is LabFavouriteFragment) {
            childFragment.setOnTextClickedListener(this)
        }
        if (childFragment is LabTempleteFragment) {
            childFragment.setOnTextClickedListener(this)
        }
        if (childFragment is ClearFavParticularPositionListener) {
            mCallbackLabFavFragment = childFragment as ClearFavParticularPositionListener
        }
        if (childFragment is ClearTemplateParticularPositionListener) {
            mCallbackLabTemplateFragment = childFragment as ClearTemplateParticularPositionListener
        }
        if (childFragment is ManageLabSaveTemplateFragment) {
            childFragment.setOnClickedListener(this)
        }
        if (childFragment is PrevLabFragment) {
            childFragment.setOnTextClickedListener(this)
        }
    }

    override fun sendFavAddInLab(favmodel: FavouritesModel?, position: Int, selected: Boolean) {
        favmodel?.viewLabstatus = 1
        favmodel?.isFavposition = position
        if (!selected) {
            labAdapter!!.addFavouritesInRow(favmodel)
        } else {
            labAdapter!!.deleteRowFromTemplate(favmodel?.test_master_id, 1)
        }
    }

    override fun sendTemplete(templeteDetails: List<LabDetail?>?, position: Int, selected: Boolean,id:Int) {
        if (!selected) {
            for (i in templeteDetails!!.indices) {
                val favmodel: FavouritesModel? = FavouritesModel()
                favmodel?.viewLabstatus = 2
                favmodel?.isTemposition = position
                favmodel?.test_master_name = templeteDetails[i]!!.lab_name
                favmodel?.test_master_id=templeteDetails[i]!!.lab_test_uuid
                favmodel?.test_master_code=templeteDetails[i]!!.lab_code
                favmodel?.template_id=id
                labAdapter!!.addFavouritesInRow(favmodel)

            }
        } else {
            for (i in templeteDetails!!.indices) {
                labAdapter!!.deleteRowFromTemplate(templeteDetails[i]!!.lab_test_uuid, 2)
            }

        }
    }

    override fun sendPrevtoChild(responseContent: List<PodArrResult>?) {

        prevOrder=true
        labAdapter!!.clearallAddone()
        for (i in responseContent!!.indices) {
            val favmodel: FavouritesModel? = FavouritesModel()
            favmodel!!.chief_complaint_name = responseContent[i].name
            favmodel.itemAppendString = responseContent[i].name
            favmodel.test_master_name = responseContent[i].name
            favmodel.test_master_id= responseContent[i].test_master_uuid
            favmodel.test_master_code= responseContent[i].code
            favmodel.selectTypeUUID=responseContent[i].order_priority_uuid
            favmodel.selectToLocationUUID=responseContent[i].order_to_location_uuid
            labAdapter!!.addFavouritesInRow(favmodel)

        }


    }

    override fun sendDataChiefComplaint() {
        mCallbackLabTemplateFragment?.GetTemplateDetails()
    }
}

