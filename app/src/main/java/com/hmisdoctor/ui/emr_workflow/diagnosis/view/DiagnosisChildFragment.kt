package com.hmisdoctor.ui.emr_workflow.diagnosis.view

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.FragmentBackClick
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.DiagnosisChildFragmentBinding
import com.hmisdoctor.ui.emr_workflow.delete.model.DeleteResponseModel
import com.hmisdoctor.ui.emr_workflow.diagnosis.model.DiagnosisRequest
import com.hmisdoctor.ui.emr_workflow.diagnosis.model.DiagnosisResponseModel
import com.hmisdoctor.ui.emr_workflow.diagnosis.model.diagonosissearch.DiagonosisSearchResponse
import com.hmisdoctor.ui.emr_workflow.diagnosis.model.diagonosissearch.ResponseContentsdiagonosissearch
import com.hmisdoctor.ui.emr_workflow.diagnosis.view.snomed_dialog.SnomedDialogFragment
import com.hmisdoctor.ui.emr_workflow.diagnosis.view.snomed_dialog.response.SnomedData
import com.hmisdoctor.ui.emr_workflow.diagnosis.view_model.DiagnosisViewModel
import com.hmisdoctor.ui.emr_workflow.diagnosis.view_model.DiagnosisViewModelFactory
import com.hmisdoctor.ui.emr_workflow.lab.ui.ClearFavParticularPositionListener
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesResponseModel
import com.hmisdoctor.utils.Utils
import org.json.JSONObject
import retrofit2.Response


class DiagnosisChildFragment:Fragment(),FavaddDiagonosisDialog.OnFavRefreshListener,SnomedDialogFragment.OnSnomedListener,DiagnosisFavouriteFragment.FavClickedListener {
    private var customdialog: Dialog?=null
    lateinit var drugNmae: TextView
    private var jsonObj_: JSONObject?=null
    private var gsonObject: JsonObject?=null
    private var facility_id: Int=0
    private var Str_auto_id: Int? = 0
    private var fragmentBackClick: FragmentBackClick? = null
    var mCallbackdiagnosisFavFragment: ClearFavParticularPositionListener? = null
    private var callbackPrevDiagnosis: CallBackPreviousDiagnosis? = null


    var binding:DiagnosisChildFragmentBinding?=null
    private var departmentID: Int? = 0
    var diagnosisFavouritesAdapter:DiagnosisFavouritesAdapter?=null

    private var utils: Utils? = null

    private var responseContents: String? = ""

    private var viewModel: DiagnosisViewModel? = null

    private var dignoisisAdapter:DiagnosisAdapter?=null

    private var selectedSearchPosition = -1

    lateinit var dropdownReferenceView:AppCompatAutoCompleteTextView

    private var appPreferences : AppPreferences?= null

    private var deparment_UUID : Int? = 0;
    var gson : Gson ?=Gson()
    val diagnosisRequestArrayList = ArrayList<DiagnosisRequest>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding= DataBindingUtil.inflate(inflater, R.layout.diagnosis_child_fragment,container,false)

        viewModel= DiagnosisViewModelFactory(requireActivity().application
        ).create(DiagnosisViewModel::class.java)


        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)

        deparment_UUID = appPreferences?.getInt(AppConstants?.DEPARTMENT_UUID)!!
        facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)!!

        binding?.viewpager?.offscreenPageLimit

        setAdapter()

       // initFavouritesAdapter()
        binding?.ICDfavouriteDrawerCardView?.setOnClickListener {
            binding?.ICDdrawerLayout!!.openDrawer(GravityCompat.END)
        }

        binding?.ICDdrawerLayout?.drawerElevation = 0f
        binding?.ICDdrawerLayout?.setScrimColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )
        setupViewPager(binding?.viewpager!!)
        binding?.tabs!!.setupWithViewPager(binding?.viewpager!!)
        binding?.clearCardView!!.setOnClickListener {
            dignoisisAdapter!!.clearall()
            dignoisisAdapter!!.addRow(FavouritesModel())
            mCallbackdiagnosisFavFragment?.ClearAllData()
            callbackPrevDiagnosis?.onRefresh()



        }

        binding?.ICDdrawerLayout?.drawerElevation = 0f

        binding?.ICDdrawerLayout?.setScrimColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )

        utils = Utils(requireContext())

        val patient_UUID = appPreferences?.getInt(AppConstants?.PATIENT_UUID)
        val encounter_Type = appPreferences?.getInt(AppConstants?.ENCOUNTER_TYPE)
        val encounter_uuid = appPreferences?.getInt(AppConstants?.ENCOUNTER_UUID)
        facility_id = appPreferences?.getInt(AppConstants?.FACILITY_UUID)!!


        binding?.snomedCardView!!.setOnClickListener {

            val ft = childFragmentManager.beginTransaction()
            val dialog = SnomedDialogFragment()
            dialog.show(ft, "Tag")

        }

        binding!!.saveDiagonsis.setOnClickListener {

            val arrayItemData=dignoisisAdapter!!.getall()

            if(arrayItemData.size > 0)
            {
                arrayItemData.removeAt(arrayItemData.size - 1);
            }

            if(arrayItemData.size > 0) {

                diagnosisRequestArrayList.clear()

                for (i in arrayItemData.indices) {

                    val diagnosisRequest: DiagnosisRequest? = DiagnosisRequest()
                    diagnosisRequest!!.facility_uuid = facility_id.toString()
                    diagnosisRequest.department_uuid = deparment_UUID.toString()
                    diagnosisRequest.patient_uuid = patient_UUID.toString()
                    diagnosisRequest.test_master_type_uuid = 1
                    diagnosisRequest.encounter_uuid = encounter_uuid!!
                    diagnosisRequest.encounter_type_uuid=encounter_Type!!
                    diagnosisRequest.other_diagnosis= arrayItemData[i]!!.itemAppendString!!
                    diagnosisRequest.diagnosis_uuid = arrayItemData.get(i)!!.diagnosis_id!!.toString()
                    diagnosisRequest.tat_start_time="2020-03-03T12:57:14.548Z"
                    diagnosisRequest.tat_end_time="2020-03-03T12:57:14.548Z"
                    diagnosisRequest.is_snomed=arrayItemData[i]!!.is_snomed
                    diagnosisRequestArrayList.add(diagnosisRequest)
                }

                var request = Gson().toJson(diagnosisRequestArrayList)

                Log.i("",""+request)
                Log.i("",""+request)
                Log.i("",""+request)

                viewModel!!.InsertDiagnosis(
                    diagnosisRequestArrayList!!,
                    insertDiagnoisisRetrofitCallback!!,
                    facility_id
                )

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



        binding?.diagnosisSearchSnod?.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun afterTextChanged(s: Editable) {

                if (s.length > 2 && s.length<5) {

                    Log.i("textsixe",""+s.length)

                    viewModel!!.getdiagnosisSearchResult(s.toString(),

                        facility_id, getComplaintSearchSnodRetrofitCallBack)

                }}
        })


        return binding!!.root
    }



    fun setdiagnosisAdapter(
        responseContents: List<ResponseContentsdiagonosissearch?>?,
        selectedSearchPosition: Int
    ) {
        val responseContentAdapter = DianosisSearchResultAdapter(
            context!!,
            R.layout.row_chief_complaint_search_result,
            responseContents
        )
        binding!!.diagnosisSearchSnod!!.threshold = 1
        binding!!.diagnosisSearchSnod!!.setAdapter(responseContentAdapter)
        binding!!.diagnosisSearchSnod!!.setOnItemClickListener { parent, _, position, id ->
            val selectedPoi = parent.adapter.getItem(position) as ResponseContentsdiagonosissearch?
            binding!!.diagnosisSearchSnod!!.setText(selectedPoi?.name)
            Str_auto_id = selectedPoi?.uuid
        }
    }


    private fun setAdapter() {

        dignoisisAdapter =
            DiagnosisAdapter(
                requireActivity(),
                ArrayList()
            )
        binding?.saveRecyclerView?.adapter = dignoisisAdapter
        dignoisisAdapter?.addRow(FavouritesModel())

            dignoisisAdapter!!.setOnDeleteClickListener(object :DiagnosisAdapter.OnDeleteClickListener{
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
                    dignoisisAdapter!!.deleteRow(favouritesModel, position)

                    if(favouritesModel?.viewDiagnosisstatus==1) {
                        mCallbackdiagnosisFavFragment?.ClearFavParticularPosition(favouritesModel.isFavposition)

                    }

                    customdialog!!.dismiss()


                }
                noBtn.setOnClickListener {
                    customdialog!! .dismiss()
                }
                customdialog!! .show()


            }
        })


        dignoisisAdapter!!.setOnSearchInitiatedListener(object :DiagnosisAdapter.OnSearchDignisisInitiatedListener{
            override fun onSearchInitiated(
                query: String,
                view: AppCompatAutoCompleteTextView,
                position: Int
            ) {
                selectedSearchPosition = position
                dropdownReferenceView = view

//                viewModel!!.getComplaintSearchResult(query,facility_id, getDignosisSearchRetrofitCallBack)


                viewModel?.getComplaintSearchResult(facility_id,query, getDignosisSearchRetrofitCallBack)
            }
        })
    }
    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(DiagnosisFavouriteFragment(), "Favourite")
        adapter.addFragment(PreDiagnosisFragment(), "Prev.diagnosis")
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


    val insertDiagnoisisRetrofitCallback=object :RetrofitCallback<DiagnosisResponseModel>{
        override fun onSuccessfulResponse(response: Response<DiagnosisResponseModel>?) {
            Log.i("res", "" + response?.body()?.message)


           utils?.showToast(
                R.color.positiveToast,
                binding?.mainLayout!!,response!!.body()!!.message!!
            )
           // diagnosisFavouritesAdapter!!.clearFacusData()

            dignoisisAdapter!!.clearall()
            dignoisisAdapter!!.addRow(FavouritesModel())
            mCallbackdiagnosisFavFragment?.ClearAllData()
            callbackPrevDiagnosis?.onRefresh()



        }

        override fun onBadRequest(response: Response<DiagnosisResponseModel>) {
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

    var getDignosisSearchRetrofitCallBack=object :RetrofitCallback<DiagonosisSearchResponse>{
        override fun onSuccessfulResponse(response: Response<DiagonosisSearchResponse>?) {

            responseContents = Gson().toJson(response?.body()?.responseContents)
            if (response?.body()?.responseContents?.isNotEmpty()!!) {
                dignoisisAdapter?.setAdapter(dropdownReferenceView, response.body()?.responseContents!!, selectedSearchPosition)

            }}
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
    var deleteRetrofitResponseCallback = object : RetrofitCallback<DeleteResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<DeleteResponseModel>?) {
          //  viewModel!!.getFavourites(deparment_UUID,getFavouritesRetrofitCallBack)
            customdialog!!.dismiss()
            Log.e("DeleteResponse", responseBody?.body().toString())
        }

        override fun onBadRequest(errorBody: Response<DeleteResponseModel>?) {

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

    val getComplaintSearchSnodRetrofitCallBack =
        object : RetrofitCallback<DiagonosisSearchResponse> {
            override fun onSuccessfulResponse(response: Response<DiagonosisSearchResponse>) {
                //responseContents = Gson().toJson(response.body()?.responseContents)
                if (response.body()?.responseContents?.isNotEmpty()!!) {
                    setdiagnosisAdapter(response.body()?.responseContents,0)
                }
            }
            override fun onBadRequest(response: Response<DiagonosisSearchResponse>) {
                val gson = GsonBuilder().create()
                val responseModel: DiagonosisSearchResponse
                try {
                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        DiagonosisSearchResponse::class.java
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


    override fun onFavID(position: Int) {

    }
    override fun onRefreshList() {
//        viewModel!!.getFavourites(departmentID,getFavouritesRetrofitCallBack)
    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        if (childFragment is DiagnosisFavouriteFragment) {
            childFragment.setOnTextClickedListener(this)
        }
        if (childFragment is ClearFavParticularPositionListener) {
            mCallbackdiagnosisFavFragment = childFragment as ClearFavParticularPositionListener
        }

        if (childFragment is SnomedDialogFragment) {
            childFragment.setOnSnomedRefreshListener(this)
        }

        if (childFragment is CallBackPreviousDiagnosis) {
            callbackPrevDiagnosis = childFragment as CallBackPreviousDiagnosis
        }


    }

    interface CallBackPreviousDiagnosis {
        fun onRefresh()
    }

    override fun onDetach() {
        super.onDetach()
        mCallbackdiagnosisFavFragment = null
    }

    override fun onSnomeddata(data: SnomedData) {

        val favouritesModel:FavouritesModel= FavouritesModel()

        favouritesModel.itemAppendString=data.ConceptName

        favouritesModel.diagnosis_name=data.ConceptName

        favouritesModel.diagnosis_code= data.ConceptId

        favouritesModel.diagnosis_id= data.ConceptId

        favouritesModel.is_snomed=1

        dignoisisAdapter!!.addFavouritesInRow(favouritesModel)

    }
    override fun sendFavAddInChiefFav(favmodel: FavouritesModel?, position: Int, selected: Boolean) {
        favmodel?.viewDiagnosisstatus = 1
        favmodel?.isFavposition = position
        if (!selected) {
            dignoisisAdapter?.addFavouritesInRow(favmodel)
        } else {
            dignoisisAdapter!!.deleteRowFromFavourites(favmodel, position)
        }
    }


}