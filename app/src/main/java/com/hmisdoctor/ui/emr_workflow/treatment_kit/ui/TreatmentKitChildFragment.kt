package com.hmisdoctor.ui.emr_workflow.treatment_kit.ui

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.TreatmentKitChildFragmentBinding
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.search_complaint.ComplaintSearchResponseModel
import com.hmisdoctor.ui.emr_workflow.diagnosis.model.DiagnosisRequest
import com.hmisdoctor.ui.emr_workflow.diagnosis.model.diagonosissearch.DiagonosisSearchResponse
import com.hmisdoctor.ui.emr_workflow.diagnosis.model.diagonosissearch.ResponseContentsdiagonosissearch
import com.hmisdoctor.ui.emr_workflow.diagnosis.view.DianosisSearchResultAdapter
import com.hmisdoctor.ui.emr_workflow.diagnosis.view.snomed_dialog.SnomedDialogFragment
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.LabToLocationResponse
import com.hmisdoctor.ui.emr_workflow.lab.model.LabTypeResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.favresponse.FavSearchResponce
import com.hmisdoctor.ui.emr_workflow.lab.ui.ClearFavParticularPositionListener
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesResponseModel
import com.hmisdoctor.ui.emr_workflow.model.templete.TempDetails
import com.hmisdoctor.ui.emr_workflow.prescription.model.*
import com.hmisdoctor.ui.emr_workflow.prescription.ui.PrescriptionAdapter
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.savetemplate.SaveTemplateDialogFragment
import com.hmisdoctor.ui.emr_workflow.radiology.ui.RadiologyAdapter
import com.hmisdoctor.ui.emr_workflow.treatment_kit.model.TreatmentkitSearchResponseModel
import com.hmisdoctor.ui.emr_workflow.treatment_kit.model.TreatmentkitSearchresponseContent
import com.hmisdoctor.ui.emr_workflow.treatment_kit.model.request.CreateTreatmentkitRequestModel
import com.hmisdoctor.ui.emr_workflow.treatment_kit.view_model.TreatmentKitViewModel
import com.hmisdoctor.ui.emr_workflow.treatment_kit.view_model.TreatmentKitViewModelFactory
import com.hmisdoctor.ui.emr_workflow.view.lab.model.PodArrResult
import com.hmisdoctor.ui.emr_workflow.view.lab.ui.PrevLabFragment
import com.hmisdoctor.ui.emr_workflow.view.lab.ui.PrevTreatmentKitFragment
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import org.json.JSONObject
import retrofit2.Response


class TreatmentKitChildFragment : Fragment(), TreatmentKitFavouriteFragment.FavClickedListener,
    PrevLabFragment.LabPrevClickedListener,SaveOrderTreatmentFragment.refreshClickedListener {
    private var customdialog: Dialog? = null
    lateinit var drugNmae: TextView
    private var jsonObj_: JSONObject? = null
    private var gsonObject: JsonObject? = null
    private var facility_id: Int = 0
    private var Str_auto_id: Int? = 0
    var binding: TreatmentKitChildFragmentBinding? = null
    private var departmentID: Int? = 0
    private var searchPosition: Int? = 0
    var mCallbacktreatmentKitFavFragment: ClearFavParticularPositionListener? = null
    private var utils: Utils? = null
    private var responseContents: String? = ""
    var searchposition:Int=0
    private var viewModel: TreatmentKitViewModel? = null
    private var treatmentDiagnosisAdapter: TreatmentKitDiagnosisAdapter? = null
    var treatmentprescriptionAdapter: TreatmentKitPrescriptionAdapter? = null
    var labAdapter: TreatmentKitLabAdapter? = null
    var treatmentRadiologyAdapter: TreatmentKitRadiologyAdapter? = null
    var treatmentKitInvestigationAdapter: TreatmentKitInvestigationAdapter? = null
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    private var selectedSearchPosition = -1
    lateinit var dropdownReferenceView: AppCompatAutoCompleteTextView
    private var appPreferences: AppPreferences? = null
    private var deparment_UUID: Int? = 0;
    var gson: Gson? = Gson()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.treatment_kit_child_fragment,
            container,
            false
        )

        viewModel = TreatmentKitViewModelFactory(
            requireActivity().application
        ).create(TreatmentKitViewModel::class.java)

        userDetailsRoomRepository = UserDetailsRoomRepository(requireActivity().application)
        var userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)

        deparment_UUID = appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)!!
        facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)!!

        setDiagnosisAdapter()
        setPrescriptionADapter()
        setLabAdapter()
        setRadiologyAdapter()
        setInvestigationAdapter()
        setupViewPager(binding?.viewpager!!)
        binding?.tabs!!.setupWithViewPager(binding?.viewpager!!)

        binding?.favouriteDrawerCardView!!.setOnClickListener {

            binding?.drawerLayout!!.openDrawer(GravityCompat.END)

        }

        binding?.drawerLayout?.drawerElevation = 0f

        binding?.drawerLayout?.setScrimColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )

        utils = Utils(requireContext())
        facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)!!


        viewModel?.getFrequencyDetails(getFrequencyRetrofitCallback, facility_id)
        viewModel?.getPrescriptionDuration(getPrescriptionDurationRetrofitCallBack,facility_id)
        viewModel?.getLabType(getLabTypeRetrofitCallback, facility_id)
        viewModel?.getLabType(getRadiologyTypeRetrofitCallback, facility_id)
        viewModel?.getLabType(getInvestigationTypeRetrofitCallback, facility_id)
        viewModel?.getLabToLocation(getInvestigationToLoctionRetrofitCallback, facility_id)



        binding?.saveTreatmentKit!!.setOnClickListener {


            /* Log.e("LabSize",labAdapter!!.getItems().size.toString())
            Log.e("RadiologySize",treatmentRadiologyAdapter!!.getAll().size.toString())
            Log.e("DiagnosisSize",treatmentDiagnosisAdapter!!.getall().size.toString())
            Log.e("PrescSize",treatmentprescriptionAdapter!!.getItems().size.toString())
            Log.e("InvestigationSize",treatmentKitInvestigationAdapter!!.getItems().size.toString())*/

            if (labAdapter!!.getItems().size > 1 || treatmentRadiologyAdapter!!.getAll().size > 1 || treatmentDiagnosisAdapter!!.getall().size > 1
                || treatmentprescriptionAdapter!!.getItems().size > 1 || treatmentKitInvestigationAdapter!!.getItems().size > 1
            ) {
                val dialog = SaveOrderTreatmentFragment()
                val ft = childFragmentManager.beginTransaction()
                val bundle = Bundle()
                val saveArrayLab = labAdapter!!.getItems()
                if (saveArrayLab.size > 0) {
                    saveArrayLab.removeAt(saveArrayLab.size - 1);
                }
                bundle.putParcelableArrayList(AppConstants.RESPONSECONTENT, saveArrayLab)

                val saveArrayDiagnosis = treatmentDiagnosisAdapter!!.getall()
                if (saveArrayDiagnosis.size > 0) {
                    saveArrayDiagnosis.removeAt(saveArrayDiagnosis.size - 1);
                }
                bundle.putParcelableArrayList(
                    AppConstants.DIAGNISISRESPONSECONTENT,
                    saveArrayDiagnosis
                )


                val savePrescription = treatmentprescriptionAdapter!!.getItems()
                if (savePrescription.size > 0) {
                    savePrescription.removeAt(savePrescription.size - 1);
                }
                bundle.putParcelableArrayList(
                    AppConstants.PRESCRIPTIONRESPONSECONTENT,
                    savePrescription
                )


                val saveRadiology = treatmentRadiologyAdapter!!.getAll()
                if (saveRadiology.size > 0) {
                    saveRadiology.removeAt(saveRadiology.size - 1)
                }
                bundle.putParcelableArrayList(AppConstants.RADIOLOGYRESPONSECONTENT, saveRadiology)

                val saveInvestigation = treatmentKitInvestigationAdapter!!.getItems()
                if (saveInvestigation.size > 0) {
                    saveInvestigation.removeAt(saveInvestigation.size - 1)
                }
                bundle.putParcelableArrayList(
                    AppConstants.INVESTIGATIONRESPONSECONTENT,
                    saveInvestigation
                )



                dialog.arguments = bundle
                dialog.show(ft, "Tag")

            }else{
                Toast.makeText(activity,"Enter any one widget details",Toast.LENGTH_LONG).show()
            }
        }



        return binding!!.root
    }

    private fun setupViewPager(viewPager: ViewPager) {

        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(TreatmentKitFavouriteFragment(), "Favourite")
        adapter.addFragment(PrevTreatmentKitFragment(), "Prev.Treatment Kit")
        viewPager.offscreenPageLimit = 3
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


     fun setDiagnosisAdapter() {

        treatmentDiagnosisAdapter =
            TreatmentKitDiagnosisAdapter(
                requireActivity(),
                ArrayList()
            )
        binding?.saveDiagonosisRecyclerView?.adapter = treatmentDiagnosisAdapter
        treatmentDiagnosisAdapter?.addRow(FavouritesModel())

        treatmentDiagnosisAdapter!!.setOnDeleteClickListener(object : TreatmentKitDiagnosisAdapter.OnDeleteClickListener{
            override fun onDeleteClick(favouritesModel: FavouritesModel?, position: Int) {
                customdialog = Dialog(requireContext())
                customdialog!! .requestWindowFeature(Window.FEATURE_NO_TITLE)
                customdialog!! .setCancelable(false)
                customdialog!! .setContentView(R.layout.delete_cutsom_layout)
                val closeImageView = customdialog!! .findViewById(R.id.closeImageView) as ImageView

                closeImageView.setOnClickListener {
                    customdialog?.dismiss()
                }
                drugNmae = customdialog!! .findViewById(R.id.addDeleteName) as TextView
                drugNmae.text ="${drugNmae.text.toString()} '"+favouritesModel?.diagnosis_name+"' Record ?"
                val yesBtn = customdialog!! .findViewById(R.id.yes) as CardView
                val noBtn = customdialog!! .findViewById(R.id.no) as CardView
                yesBtn.setOnClickListener {
                    treatmentDiagnosisAdapter!!.deleteRow(favouritesModel, position)

                    if(favouritesModel?.viewDiagnosisstatus==1) {
                        favouritesModel.isSelected = false
                        mCallbacktreatmentKitFavFragment?.ClearFavParticularPosition(favouritesModel.isFavposition)
                    }

                    customdialog!!.dismiss()


                }
                noBtn.setOnClickListener {
                    customdialog!! .dismiss()
                }
                customdialog!! .show()


            }
        })

        treatmentDiagnosisAdapter!!.setOnSearchInitiatedListener(object :
            TreatmentKitDiagnosisAdapter.OnSearchDignisisInitiatedListener {
            override fun onSearchInitiated(
                query: String,
                view: AppCompatAutoCompleteTextView,
                position: Int
            ) {
                selectedSearchPosition = position
                dropdownReferenceView = view
                viewModel?.getDiagnosisComplaintSearchResult(
                    facility_id,
                    query,
                    getDignosisSearchRetrofitCallBack
                )
            }
        })
    }

     fun setPrescriptionADapter() {
        treatmentprescriptionAdapter = TreatmentKitPrescriptionAdapter(requireActivity(), ArrayList(), ArrayList())
        binding?.savePrescriptionRecyclerView?.adapter = treatmentprescriptionAdapter
        treatmentprescriptionAdapter?.addRow(FavouritesModel())
        treatmentprescriptionAdapter?.addTempleteRow(TempDetails())
        treatmentprescriptionAdapter?.setOnDeleteClickListener(object :
            TreatmentKitPrescriptionAdapter.OnDeleteClickListener {
            override fun onDeleteClick(responseContent: FavouritesModel?, position: Int) {
                val check=treatmentprescriptionAdapter?.deleteRow(position)
//                val check=labAdapter?.checkData(responseContent!!)
                if (responseContent?.viewPrescriptionstatus == 1) {
                    mCallbacktreatmentKitFavFragment?.ClearFavParticularPosition(responseContent.isFavposition)
                }
            }
        })
        treatmentprescriptionAdapter?.setOnSearchInitiatedListener(object :
            TreatmentKitPrescriptionAdapter.OnSearchInitiatedListener {
            override fun onSearchInitiated(
                query: String,
                view: AppCompatAutoCompleteTextView,
                position: Int
            ) {
                dropdownReferenceView = view
                selectedSearchPosition = position
                viewModel?.getPrescriptionComplaintSearchResult(query, getPresriptionComplaintSearchRetrofitCallBack,facility_id)
            }
        })

        setupViewPager(binding?.viewpager!!)
        binding?.tabs!!.setupWithViewPager(binding?.viewpager!!)
        viewModel?.getPrescriptionDuration(getPrescriptionDurationRetrofitCallBack,facility_id)

    }
     fun setLabAdapter() {
        labAdapter =
            TreatmentKitLabAdapter(
                requireActivity(),
                ArrayList(), ArrayList()
            )
        binding?.savelabRecyclerView?.adapter = labAdapter

        labAdapter?.addRow(FavouritesModel())
        labAdapter?.addTempleteRow(TempDetails())

        labAdapter?.setOnDeleteClickListener(object :
            TreatmentKitLabAdapter.OnDeleteClickListener {
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
                        mCallbacktreatmentKitFavFragment?.ClearFavParticularPosition(responseContent.isFavposition)

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
            TreatmentKitLabAdapter.OnSearchInitiatedListener {
            override fun onSearchInitiated(query: String, view: AppCompatAutoCompleteTextView, position: Int) {
                dropdownReferenceView = view
                searchposition=position
                viewModel?.getLabComplaintSearchResult(facility_id,query, getLabComplaintSearchRetrofitCallBack)
            }
        })


    }
     fun setRadiologyAdapter() {
        treatmentRadiologyAdapter =
            TreatmentKitRadiologyAdapter(
                requireActivity(),
                ArrayList(), ArrayList()
            )
        binding?.saveRadiologyRecyclerView?.adapter = treatmentRadiologyAdapter

        treatmentRadiologyAdapter?.addRow(FavouritesModel())
        treatmentRadiologyAdapter?.addTempleteRow(TempDetails())



        treatmentRadiologyAdapter?.setOnDeleteClickListener(object :
            TreatmentKitRadiologyAdapter.OnDeleteClickListener {
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

                    val check=treatmentRadiologyAdapter?.deleteRow(position)

                    if (responseContent?.viewRadiologystatus == 1) {
                        mCallbacktreatmentKitFavFragment?.ClearFavParticularPosition(responseContent.isFavposition)

                    }
                    customdialog!!.dismiss()


                }
                noBtn.setOnClickListener {
                    customdialog!! .dismiss()
                }
                customdialog!! .show()
            }
        })
        treatmentRadiologyAdapter?.setOnSearchInitiatedListener(object :
            TreatmentKitRadiologyAdapter.OnSearchInitiatedListener {
            override fun onSearchInitiated(query: String, view: AppCompatAutoCompleteTextView, position: Int) {
                dropdownReferenceView = view
                searchPosition=position
                viewModel?.getRadioSearchResult(facility_id,query, getRadiologyComplaintSearchRetrofitCallBack)
            }
        })
    }
     fun setInvestigationAdapter() {
        treatmentKitInvestigationAdapter =
            TreatmentKitInvestigationAdapter(
                requireActivity(),
                ArrayList(), ArrayList()
            )
        binding?.saveInvestigationRecyclerView?.adapter = treatmentKitInvestigationAdapter

        treatmentKitInvestigationAdapter?.addRow(FavouritesModel())
        treatmentKitInvestigationAdapter?.addTempleteRow(TempDetails())



        treatmentKitInvestigationAdapter?.setOnDeleteClickListener(object :
            TreatmentKitInvestigationAdapter.OnDeleteClickListener {
            override fun onDeleteClick(responseContent: FavouritesModel?, position: Int) {
                treatmentKitInvestigationAdapter?.deleteRow(position)
            }
        })
        treatmentKitInvestigationAdapter?.setOnSearchInitiatedListener(object :
            TreatmentKitInvestigationAdapter.OnSearchInitiatedListener {
            override fun onSearchInitiated(query: String, view: AppCompatAutoCompleteTextView) {
                dropdownReferenceView = view
                viewModel?.getInvestigationComplaintSearchResult(facility_id,query, getInvestigationComplaintSearchRetrofitCallBack)
            }
        })
    }
    val getInvestigationComplaintSearchRetrofitCallBack =
        object : RetrofitCallback<TreatmentkitSearchResponseModel> {
            override fun onSuccessfulResponse(response: Response<TreatmentkitSearchResponseModel>) {
                responseContents = Gson().toJson(response.body()?.responseContents)
                if (response.body()?.responseContents?.isNotEmpty()!!) {
                    treatmentKitInvestigationAdapter?.setAdapter(
                        dropdownReferenceView,
                        (response.body()?.responseContents as ArrayList<TreatmentkitSearchresponseContent>?)!!
                    )

                }

            }

            override fun onBadRequest(response: Response<TreatmentkitSearchResponseModel>) {
                val gson = GsonBuilder().create()
                val responseModel: TreatmentkitSearchResponseModel
                try {

                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        TreatmentkitSearchResponseModel::class.java
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


    val getRadiologyComplaintSearchRetrofitCallBack =
        object : RetrofitCallback<FavSearchResponce> {
            override fun onSuccessfulResponse(response: Response<FavSearchResponce>) {
                responseContents = Gson().toJson(response.body()?.responseContents)
                if (response.body()?.responseContents?.isNotEmpty()!!) {
                    treatmentRadiologyAdapter?.setAdapter(
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

    val getPresriptionComplaintSearchRetrofitCallBack =
        object : RetrofitCallback<PrescriptionSearchResponseModel> {
            override fun onSuccessfulResponse(response: Response<PrescriptionSearchResponseModel>) {
                responseContents = Gson().toJson(response.body()?.responseContents)
                if (response.body()?.responseContents?.isNotEmpty()!!) {
                    treatmentprescriptionAdapter?.setPrescriptionAdapter(
                        dropdownReferenceView,
                        response.body()?.responseContents!!,selectedSearchPosition
                    )

                }
            }

            override fun onBadRequest(response: Response<PrescriptionSearchResponseModel>) {
                val gson = GsonBuilder().create()
                val responseModel: PrescriptionSearchResponseModel
                try {

                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        PrescriptionSearchResponseModel::class.java
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



    var getDignosisSearchRetrofitCallBack = object : RetrofitCallback<DiagonosisSearchResponse> {
        override fun onSuccessfulResponse(response: Response<DiagonosisSearchResponse>?) {

            responseContents = Gson().toJson(response?.body()?.responseContents)
            if (response?.body()?.responseContents?.isNotEmpty()!!) {
                treatmentDiagnosisAdapter?.setAdapter(
                    dropdownReferenceView,
                    response.body()?.responseContents!!,
                    selectedSearchPosition
                )

            }
        }

        override fun onBadRequest(response: Response<DiagonosisSearchResponse>) {
            val gson = GsonBuilder().create()
            val responseModel: FavouritesResponseModel
            try {
                responseModel = gson.fromJson(
                    response.errorBody()!!.string(),
                    FavouritesResponseModel::class.java
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
    val getLabComplaintSearchRetrofitCallBack =
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


    val getFrequencyRetrofitCallback = object : RetrofitCallback<PresDrugFrequencyResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<PresDrugFrequencyResponseModel>?) {

            treatmentprescriptionAdapter?.setFrequencyList(responseBody?.body()?.responseContents)
            viewModel?.getRouteDetails(getRouteRetrofitCallback, facility_id)
        }
        override fun onBadRequest(errorBody: Response<PresDrugFrequencyResponseModel>?) {
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
    val getRouteRetrofitCallback = object : RetrofitCallback<PrescriptionRoutResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<PrescriptionRoutResponseModel>?) {

            treatmentprescriptionAdapter?.setadapterTypeValue(responseBody?.body()?.responseContents)
            viewModel?.getInstructionDetails(getInstructionRetrofitCallback, facility_id)

        }

        override fun onBadRequest(errorBody: Response<PrescriptionRoutResponseModel>?) {

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

    val getInstructionRetrofitCallback = object : RetrofitCallback<PresInstructionResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<PresInstructionResponseModel>?) {

            treatmentprescriptionAdapter?.setInstructionList(responseBody?.body()?.responseContents)
        }
        override fun onBadRequest(errorBody: Response<PresInstructionResponseModel>?) {
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
    private val getPrescriptionDurationRetrofitCallBack =
        object : RetrofitCallback<PrescriptionDurationResponseModel> {
            override fun onSuccessfulResponse(response: Response<PrescriptionDurationResponseModel>) {
                if (response.body()?.responseContents?.isNotEmpty()!!) {
                    treatmentprescriptionAdapter?.setDuration(response.body()?.responseContents!!)

                }
            }
            override fun onBadRequest(response: Response<PrescriptionDurationResponseModel>) {
                val gson = GsonBuilder().create()
                val responseModel: PrescriptionDurationResponseModel
                try {

                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        PrescriptionDurationResponseModel::class.java
                    )
                    utils?.showToast(
                        R.color.negativeToast,
                        binding?.mainLayout!!,
                        responseModel.message
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
            viewModel?.getLabToLocation(getLabToLoctionRetrofitCallback, facility_id)
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
            viewModel?.getToLocation(getRadiologyToLoctionRetrofitCallback, facility_id)


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
    val getRadiologyTypeRetrofitCallback = object : RetrofitCallback<LabTypeResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<LabTypeResponseModel>?) {
            treatmentRadiologyAdapter?.setadapterTypeValue(responseBody?.body()?.responseContents)
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
    val getRadiologyToLoctionRetrofitCallback = object : RetrofitCallback<LabToLocationResponse> {
        override fun onSuccessfulResponse(responseBody: Response<LabToLocationResponse>?) {

            treatmentRadiologyAdapter?.setToLocationList(responseBody?.body()?.responseContents)
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
    val getInvestigationTypeRetrofitCallback = object : RetrofitCallback<LabTypeResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<LabTypeResponseModel>?) {

            treatmentKitInvestigationAdapter?.setadapterTypeValue(responseBody?.body()?.responseContents)



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
    val getInvestigationToLoctionRetrofitCallback = object : RetrofitCallback<LabToLocationResponse> {
        override fun onSuccessfulResponse(responseBody: Response<LabToLocationResponse>?) {

            treatmentKitInvestigationAdapter?.setToLocationList(responseBody?.body()?.responseContents)
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




    override fun sendFavAddInLab(favmodel: FavouritesModel?, position: Int, selected: Boolean) {
        favmodel?.viewLabstatus = 1
        favmodel?.isFavposition = position
        if (!selected) {
            treatmentDiagnosisAdapter!!.addFavouritesInRow(favmodel)
        }
    }

    override fun sendPrevtoChild(responseContent: List<PodArrResult>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        if (childFragment is TreatmentKitFavouriteFragment) {
            childFragment.setOnTextClickedListener(this)
        }
        if(childFragment is SaveOrderTreatmentFragment){

            childFragment.setOnRefreshClickedListener(this)
        }

        if (childFragment is ClearFavParticularPositionListener) {
            mCallbacktreatmentKitFavFragment = childFragment as ClearFavParticularPositionListener
        }

        if (childFragment is PrevLabFragment) {
            childFragment.setOnTextClickedListener(this)
        }
    }

    override fun refresh() {
        labAdapter!!.clearall()
        treatmentRadiologyAdapter!!.clearall()
        treatmentprescriptionAdapter!!.clearall()
        treatmentDiagnosisAdapter!!.clearall()
        treatmentKitInvestigationAdapter!!.clearall()

        setLabAdapter()
        setPrescriptionADapter()
        setRadiologyAdapter()
        setInvestigationAdapter()
        setDiagnosisAdapter()
    }


}