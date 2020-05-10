package com.hmisdoctor.ui.emr_workflow.treatment_kit.ui

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.DialogManageDiagonosisFavouritesBinding
import com.hmisdoctor.ui.emr_workflow.chief_complaint.ui.chiefcomplaintadddialog.model.request.ChiefCompliantAddRequestModel
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddAllDepatResponseContent
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddResponseContent
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddTestNameResponseContent
import com.hmisdoctor.ui.emr_workflow.chief_complaint.ui.chiefcomplaintadddialog.model.request.Detail
import com.hmisdoctor.ui.emr_workflow.chief_complaint.ui.chiefcomplaintadddialog.model.request.Headers
import com.hmisdoctor.ui.emr_workflow.delete.model.DeleteResponseModel
import com.hmisdoctor.ui.emr_workflow.diagnosis.model.addFavlistDiagonosisDataModel
import com.hmisdoctor.ui.emr_workflow.diagnosis.model.diagonosissearch.DiagonosisSearchResponse
import com.hmisdoctor.ui.emr_workflow.diagnosis.model.diagonosissearch.ResponseContentsdiagonosissearch
import com.hmisdoctor.ui.emr_workflow.diagnosis.view_model.DiagonosisDialogViewModel
import com.hmisdoctor.ui.emr_workflow.diagnosis.view_model.DiagonosisDialogViewModelFactory
import com.hmisdoctor.ui.emr_workflow.lab.model.LabFavManageResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.faveditresponse.FavEditResponse
import com.hmisdoctor.ui.emr_workflow.lab.model.favresponse.FavAddListResponse
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import retrofit2.Response

class FavaddTreatmentKitDialog: DialogFragment() {
    private var deletefavouriteID: Int?=0
    private var is_active: Boolean = true
    private var Str_auto_id: Int? = 0
    private var content: String? = null
    private var viewModel: DiagonosisDialogViewModel? = null
    var binding: DialogManageDiagonosisFavouritesBinding? = null
    var appPreferences: AppPreferences? = null
    private var utils: Utils? = null
    private var facility_UUID: Int? = 0
    private var deparment_UUID: Int? = 0
    private var listDepartmentItems: ArrayList<FavAddResponseContent?> = ArrayList()
    private var listAllAddDepartmentItems: List<FavAddAllDepatResponseContent?> = ArrayList()
    private var favAddResponseMap = mutableMapOf<Int, String>()
    private var autocompleteTestResponse: List<FavAddTestNameResponseContent>? = null
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    val header: Headers= Headers()
    val detailsList : ArrayList<Detail> = ArrayList()
    var labManageFavAdapter: TreatmentKitFavouritesAdapter? = null
    val emrFavRequestModel: ChiefCompliantAddRequestModel = ChiefCompliantAddRequestModel()
    private var favouriteData: FavouritesModel?=null
    var callbackfavourite: OnFavRefreshListener? = null
    private var departmentID: Int? = 0
    private var customdialog: Dialog?=null
    var status : Boolean ?=false
    lateinit var drugNmae: TextView
    lateinit var dropdownReferenceView: AppCompatAutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content = arguments?.getString(AppConstants.ALERTDIALOG)
        val style = STYLE_NO_FRAME
        val theme = R.style.DialogTheme
        setStyle(style, theme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_manage_diagonosis_favourites, container, false)
        viewModel = DiagonosisDialogViewModelFactory(
            requireActivity().application
        ).create(DiagonosisDialogViewModel::class.java)

        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this

        dropdownReferenceView=binding!!.chiefComplaintSearch
        userDetailsRoomRepository = UserDetailsRoomRepository(requireActivity().application)

        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        facility_UUID = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        deparment_UUID = appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)

     /*   binding?.viewModel?.getDepartmentList(facility_UUID, FavLabdepartmentRetrofitCallBack)

        viewModel?.getTestName(facility_UUID.toString(), favAddTestNameCallBack)
*/
        labManageFavAdapter =
            TreatmentKitFavouritesAdapter(
                requireActivity(),
                ArrayList()
            )
        binding?.manageFavouritesRecyclerView?.adapter = labManageFavAdapter
        binding?.switchCheckCheif!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
            {
                is_active = true

            }
            else{
                is_active = false

            }
        })


        binding?.clearCardView?.setOnClickListener({
            binding?.chiefComplaintSearch?.setText("")
            binding?.displayorderedittext?.setText("")
        })

        val args = arguments
        if (args == null) {
            status = true
            //  Toast.makeText(activity, "arguments is null ", Toast.LENGTH_LONG).show()
        } else {
            // get value from bundle..
            favouriteData = args.getParcelable(AppConstants.RESPONSECONTENT)
            Log.i("",""+favouriteData)
            binding?.diagonosisAdd?.text = "Update"

            val data:addFavlistDiagonosisDataModel= addFavlistDiagonosisDataModel()
            data.diagonosis_description= favouriteData?.diagnosis_description
            data.diagonosisname= favouriteData?.diagnosis_name!!
            data.diagonosiscode=favouriteData?.diagnosis_code
            data.diagonosis_id = favouriteData?.diagnosis_id!!

            binding?.chiefComplaintSearch?.setText(favouriteData?.chief_complaint_name)
            binding?.displayorderedittext?.setText(favouriteData?.favourite_display_order?.toString())
            binding?.chiefComplaintSearch?.isEnabled = false
            labManageFavAdapter!!.addRow(data)
        }
        labManageFavAdapter?.setOnDeleteClickListener(object : TreatmentKitFavouritesAdapter.OnDeleteClickListener{
            override fun onDeleteClick(favouritesID: Int?, diagonosisname: String) {
                deletefavouriteID = favouritesID

                customdialog = Dialog(requireContext())
                customdialog!! .requestWindowFeature(Window.FEATURE_NO_TITLE)
                customdialog!! .setCancelable(false)
                customdialog!! .setContentView(R.layout.delete_cutsom_layout)
                val closeImageView = customdialog!! .findViewById(R.id.closeImageView) as ImageView

                closeImageView.setOnClickListener {
                    customdialog?.dismiss()
                }
                drugNmae = customdialog!! .findViewById(R.id.addDeleteName) as TextView
                drugNmae.text =diagonosisname
                val yesBtn = customdialog!! .findViewById(R.id.yes) as CardView
                val noBtn = customdialog!! .findViewById(R.id.no) as CardView
                yesBtn.setOnClickListener {
                    viewModel!!.deleteFavourite(
                        facility_UUID,
                        favouritesID,deleteRetrofitResponseCallback
                    )

                }
                noBtn.setOnClickListener {
                    customdialog!! .dismiss()
                }
                customdialog!! .show()

            }

        })

//        viewModel!!.getFavourites(departmentID,getFavouritesRetrofitCallBack)

        binding?.closeImageView?.setOnClickListener {
            dialog?.dismiss()
        }


        binding?.chiefComplaintSearch?.addTextChangedListener {  }

        binding?.chiefComplaintSearch?.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun afterTextChanged(s: Editable) {

                if (s.length > 2 && s.length<5) {

                    Log.i("textsixe",""+s.length)

                    viewModel!!.getComplaintSearchResult(s.toString(),facility_UUID, getComplaintSearchRetrofitCallBack)

                }}
        })

        binding?.diagonosisAdd?.setOnClickListener {
            val Str_DisplayOrder = binding?.displayorderedittext?.text.toString()
            if(status as Boolean)
            {
                detailsList.clear()

                header.is_public = true

                header.facility_uuid = facility_UUID?.toString()!!

                header.favourite_type_uuid= AppConstants.FAV_TYPE_ID_DIAGNOSIS

                header.department_uuid = deparment_UUID?.toString()!!

                header.user_uuid = userDataStoreBean?.uuid!!

                header.display_order = binding!!.displayorderedittext.text.toString()

                header.revision = true

                header.ticksheet_type_uuid=1

                if(is_active){

                    header.is_active="true"

                }
                else{
                    header.is_active="false"
                }

                val details: Detail = Detail()
                details.test_master_uuid=1
                details.test_master_type_uuid = 0
                details.item_master_uuid =0
                details.chief_complaint_uuid=0
                details.vital_master_uuid=0
                details.drug_route_uuid=0
                details.drug_frequency_uuid=0
                details.duration=0
                details.duration_period_uuid=0
                details.drug_instruction_uuid=0
                details.display_order=binding!!.displayorderedittext.text.toString()
                details.revision=true
                if(is_active){
                    details.is_active="true"
                }
                else{
                    details.is_active="false"
                }
                details.diagnosis_uuid=Str_auto_id!!.toInt()
                detailsList.add(details)
                emrFavRequestModel?.headers = this.header
                emrFavRequestModel?.details = this.detailsList
                viewModel?.AddFavourite(facility_UUID!!, emrFavRequestModel, addFavRetrofitCallBack)
            }

            else{
                //False
                viewModel?.getEditFavourite(facility_UUID,favouriteData?.chief_complaint_id,
                    favouriteData?.favourite_id,Str_DisplayOrder,is_active,emrposListDataFavtEditRetrofitCallback)

            }

        }
        return binding!!.root
    }


    fun setAdapter(
        responseContents: List<ResponseContentsdiagonosissearch?>?,
        selectedSearchPosition: Int
    ) {
        val responseContentAdapter = TreatmentDiagnosisSearchResultAdapter(
            context!!,
            R.layout.row_chief_complaint_search_result,responseContents
        )
        binding!!.chiefComplaintSearch.threshold = 1
        binding!!.chiefComplaintSearch.setAdapter(responseContentAdapter)
        binding!!.chiefComplaintSearch.setOnItemClickListener { parent, _, position, id ->
            val selectedPoi = parent.adapter.getItem(position) as ResponseContentsdiagonosissearch?
            binding!!.chiefComplaintSearch.setText(selectedPoi?.name)
            Str_auto_id = selectedPoi?.uuid
        }
    }
    val getComplaintSearchRetrofitCallBack =
        object : RetrofitCallback<DiagonosisSearchResponse> {
            override fun onSuccessfulResponse(response: Response<DiagonosisSearchResponse>) {
                //responseContents = Gson().toJson(response.body()?.responseContents)
                if (response.body()?.responseContents?.isNotEmpty()!!) {
                    setAdapter(response?.body()?.responseContents,0)
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

    val addFavRetrofitCallBack =
        object : RetrofitCallback<LabFavManageResponseModel> {
            override fun onSuccessfulResponse(response: Response<LabFavManageResponseModel>) {

                val responseContents = response.body()?.responseContents
                viewModel?.getAddListFav(facility_UUID,responseContents?.headers?.uuid,emrposListDataFavtRetrofitCallback)
                utils?.showToast(
                    R.color.positiveToast,
                    binding?.mainLayout!!,
                    getString(R.string.something_went_wrong)
                )

                callbackfavourite?.onRefreshList()
            }

            override fun onBadRequest(response: Response<LabFavManageResponseModel>) {
                val gson = GsonBuilder().create()
                val responseModel: LabFavManageResponseModel
                try {

                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        LabFavManageResponseModel::class.java
                    )

                    Toast.makeText(activity,responseModel.message!!,Toast.LENGTH_LONG).show()


                } catch (e: Exception) {

                    Toast.makeText(activity,getString(R.string.something_went_wrong),Toast.LENGTH_LONG).show()

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

    val emrposListDataFavtEditRetrofitCallback = object : RetrofitCallback<FavEditResponse> {
        override fun onSuccessfulResponse(responseBody: Response<FavEditResponse>?) {
            Log.i("", "" + responseBody?.body()?.requestContent);

            viewModel?.getAddListFav(facility_UUID,responseBody?.body()?.requestContent?.favourite_id,emrposListDataFavtRetrofitCallback)
            utils?.showToast(
                R.color.positiveToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                responseBody?.body()?.message!!
            )

            callbackfavourite?.onRefreshList()

        }
        override fun onBadRequest(response: Response<FavEditResponse>?) {
            val gson = GsonBuilder().create()
            val responseModel: FavEditResponse
            try {

                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    response?.body()?.message!!
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
            customdialog?.dismiss()
            labManageFavAdapter?.clearadapter()


            utils?.showToast(
                R.color.positiveToast,
                binding?.mainLayout!!,
                responseBody?.body()?.message!!
            )

//            callbackfavourite?.onFavID(deletefavouriteID!!)
            callbackfavourite?.onRefreshList()


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

    val emrposListDataFavtRetrofitCallback = object : RetrofitCallback<FavAddListResponse> {
        override fun onSuccessfulResponse(responseBody: Response<FavAddListResponse>?) {
            Log.i("", "" + responseBody?.body()?.responseContents);

            val data:addFavlistDiagonosisDataModel= addFavlistDiagonosisDataModel()
            data.diagonosis_description= responseBody?.body()?.responseContents?.diagnosis_description!!
            data.diagonosisname= responseBody.body()?.responseContents?.diagnosis_name!!
            data.diagonosiscode=responseBody.body()?.responseContents?.diagnosis_code!!
            data.diagonosis_id = responseBody.body()?.responseContents?.diagnosis_id?.toString()!!
            binding?.chiefComplaintSearch?.setText("")
            binding?.displayorderedittext?.setText("")
//            binding?.displayorderedittext?.setText(responseBody.body()?.responseContents?.favourite_display_order?.toString())
            labManageFavAdapter?.clearadapter()
            labManageFavAdapter!!.addRow(data)
            utils?.showToast(
                R.color.positiveToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
            callbackfavourite?.onFavID(responseBody.body()?.responseContents?.favourite_id!!)
        }
        override fun onBadRequest(response: Response<FavAddListResponse>?) {
            val gson = GsonBuilder().create()
            val responseModel: FavAddListResponse
            try {

                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    response?.body()?.message!!
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
    fun setOnFavRefreshListener(callback: OnFavRefreshListener) {
        this.callbackfavourite = callback
    }
    // This interface can be implemented by the Activity, parent Fragment,
    // or a separate test implementation.
    interface OnFavRefreshListener {
        fun onFavID(position: Int)
        fun onRefreshList()
    }
}