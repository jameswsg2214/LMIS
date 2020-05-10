package com.hmisdoctor.ui.emr_workflow.vitals.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.databinding.adapters.ToolbarBindingAdapter
import androidx.fragment.app.DialogFragment
import com.google.gson.Gson
import com.google.gson.GsonBuilder

import com.hmisdoctor.R
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.DialogManageVitalTemplatesBinding
import com.hmisdoctor.ui.emr_workflow.lab.model.favresponse.ResponseContentsfav
import com.hmisdoctor.ui.emr_workflow.lab.model.template.request.Detail
import com.hmisdoctor.ui.emr_workflow.lab.model.template.request.Headers
import com.hmisdoctor.ui.emr_workflow.lab.model.template.request.RequestTemplateAddDetails
import com.hmisdoctor.ui.emr_workflow.lab.model.template.response.ReponseTemplateadd
import com.hmisdoctor.ui.emr_workflow.lab.model.updaterequest.NewDetail
import com.hmisdoctor.ui.emr_workflow.lab.model.updaterequest.RemovedDetail
import com.hmisdoctor.ui.emr_workflow.lab.model.updaterequest.UpdateRequestModule
import com.hmisdoctor.ui.emr_workflow.lab.model.updateresponse.UpdateResponse
import com.hmisdoctor.ui.emr_workflow.model.templete.TempleResponseModel
import com.hmisdoctor.ui.emr_workflow.prescription.model.PrescriptionDurationResponseModel
import com.hmisdoctor.ui.emr_workflow.vitals.model.VitalsSearchResponseModel
import com.hmisdoctor.ui.emr_workflow.vitals.model.response.GetVital
import com.hmisdoctor.ui.emr_workflow.vitals.model.responseedittemplatevitual.ResponseContentedittemplatevitual

import com.hmisdoctor.ui.emr_workflow.vitals.view_model.ManageVitalTemplateViewModel
import com.hmisdoctor.ui.emr_workflow.vitals.view_model.ManageVitalTemplateViewModelFactory
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import retrofit2.Response


class ManageVitalTemplateFragment : DialogFragment() {
    private var selectVitualValue: String?=""
    private var selectVitualUuid: Int?=0
    private var customdialog: Dialog?=null
    private var Str_auto_id: Int? = 0
    var arraylistresponse : ArrayList<ResponseContentsfav?> = ArrayList()
    private var content: String? = null
    private var Itemname: String? = ""
    private var viewModel: ManageVitalTemplateViewModel? = null
    var binding: DialogManageVitalTemplatesBinding? = null
    var appPreferences: AppPreferences? = null
    private val hashvitualSpinnerList: HashMap<Int,Int> = HashMap()
    private var utils: Utils? = null
    private var vitualSpinnerList = mutableMapOf<Int, String>()
    private var facility_UUID: Int? = 0
    private var deparment_UUID: Int? = 0
    private var mAdapter: VitualManageTemplateAdapter? = null
    private var arrayItemData: ArrayList<ResponseContentsfav?>? =null
    val header: Headers? = Headers()
    val detailsList: ArrayList<Detail> = ArrayList()
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    var callbacktemplate: OnTemplateRefreshListener? = null
    var status : Boolean ?=false
    var RequestTemplateAddDetails : RequestTemplateAddDetails = RequestTemplateAddDetails()
    var newDetailList : ArrayList<NewDetail> = ArrayList()
    var UpdateRequestModule : UpdateRequestModule = UpdateRequestModule()
    val removeList:  ArrayList<RemovedDetail> = ArrayList()
    var rasponsecontentLabGetTemplateDetails : ResponseContentedittemplatevitual = ResponseContentedittemplatevitual()
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
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.dialog_manage_vital_templates,
                container,
                false
            )
        viewModel = ManageVitalTemplateViewModelFactory(
            requireActivity().application
        )
            .create(ManageVitalTemplateViewModel::class.java)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this

        userDetailsRoomRepository = UserDetailsRoomRepository(requireActivity().application)
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        facility_UUID = appPreferences?.getInt(AppConstants?.FACILITY_UUID)
        deparment_UUID = appPreferences?.getInt(AppConstants?.DEPARTMENT_UUID)

        mAdapter = VitualManageTemplateAdapter(requireContext(), ArrayList())
        binding?.favManageRecyclerview!!.adapter = mAdapter

        binding?.closeImageView?.setOnClickListener {
            dialog?.dismiss()
        }
        /*
        Vitual Name
         */
        viewModel?.getVitalsSearch(facility_UUID, vitalsSearchRetrofitCallBack)
        binding?.addbutton?.setOnClickListener({

            Itemname = binding?.username?.text.toString()
            val displayordervalue = binding?.editdisplayorder?.text.toString()
            val testmasterId = Str_auto_id
            val responseContentsfav = ResponseContentsfav()
            responseContentsfav.vital_master_name = selectVitualValue
            responseContentsfav.test_master_id =   selectVitualUuid
            responseContentsfav.favourite_display_order = displayordervalue.toInt()
            val existItemCheck = mAdapter?.getItems()
            val check= existItemCheck!!.any{ it!!.test_master_id == responseContentsfav.test_master_id}

            if(check)
            {
                Toast.makeText(context,"Already this vitual added Please select other vitual name",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            arraylistresponse.add(responseContentsfav)
            mAdapter?.setFavAddItem(arraylistresponse)

        })

        mAdapter?.setOnDeleteClickListener(object : VitualManageTemplateAdapter.OnDeleteClickListener {
            @SuppressLint("SetTextI18n")
            override fun onDeleteClick(
                responseData: ResponseContentsfav?,
                position: Int
            ) {
                        Log.i("",""+responseData);
                        Log.i("",""+responseData);
                        Log.i("",""+responseData);
                        Log.i("",""+responseData);
                        customdialog = Dialog(requireContext())
                        customdialog!! .requestWindowFeature(Window.FEATURE_NO_TITLE)
                        customdialog!! .setCancelable(false)
                        customdialog!! .setContentView(R.layout.delete_cutsom_layout)
                        val closeImageView = customdialog!! .findViewById(R.id.closeImageView) as ImageView

                        closeImageView.setOnClickListener {
                            customdialog?.dismiss()
                        }
                        val drugNmae = customdialog!! .findViewById(R.id.addDeleteName) as TextView
                        drugNmae.text ="${drugNmae.text.toString()} '"+responseData?.vital_master_name+"' Record ?"
                        val yesBtn = customdialog!! .findViewById(R.id.yes) as CardView
                        val noBtn = customdialog!! .findViewById(R.id.no) as CardView
                        yesBtn.setOnClickListener {

                            Log.i("",""+responseData)
                            val removedDetail : RemovedDetail = RemovedDetail()
                            removedDetail.template_uuid = responseData?.template_master_uuid
                            removedDetail.template_details_uuid = responseData?.test_master_id
                            removeList.add(removedDetail)
                            mAdapter?.removeItem(position)
                            customdialog!!.dismiss()
                        }
                        noBtn.setOnClickListener {
                            customdialog!! .dismiss()
                        }
                        customdialog!! .show()
                    }




        })

        val args = arguments
        if (args == null) {
            status = true
            //  Toast.makeText(activity, "arguments is null ", Toast.LENGTH_LONG).show()
        } else {
            // get value from bundle..
            rasponsecontentLabGetTemplateDetails = args.getParcelable(AppConstants.RESPONSECONTENT)!!
            Log.i("",""+rasponsecontentLabGetTemplateDetails)
            Log.i("",""+rasponsecontentLabGetTemplateDetails)
            Log.i("",""+rasponsecontentLabGetTemplateDetails)
            Log.i("",""+rasponsecontentLabGetTemplateDetails)
            binding?.username?.setText(rasponsecontentLabGetTemplateDetails?.name)
            binding?.editdisplayorder?.setText(rasponsecontentLabGetTemplateDetails.display_order?.toString())
            arraylistresponse.clear()

            for(i in rasponsecontentLabGetTemplateDetails.template_master_details?.indices!!)
            {

                val responseContentsfav = ResponseContentsfav()
                responseContentsfav.vital_master_name = rasponsecontentLabGetTemplateDetails?.template_master_details?.get(i)?.vital_master?.name
                responseContentsfav.test_master_id =   rasponsecontentLabGetTemplateDetails?.template_master_details?.get(i)?.vital_master?.uuid
                responseContentsfav.template_master_uuid = rasponsecontentLabGetTemplateDetails?.uuid
                arraylistresponse.add(responseContentsfav)
            }
            mAdapter?.setFavAddItem(arraylistresponse)

        }

        binding?.save?.setOnClickListener({
            if(status as Boolean)
            {
                /*
                Add Details
                 */
                val displayordervalue = binding?.editdisplayorder?.text.toString()
                arrayItemData = mAdapter?.getItems()
                detailsList.clear()
                if (arrayItemData?.size!! > 0) {
                    for (i in arrayItemData?.indices!!) {
                        val details: Detail = Detail()
                        details.chief_complaint_uuid=0
                        details.vital_master_uuid=arrayItemData?.get(i)?.test_master_id
                        details.test_master_uuid=0
                        details.item_master_uuid = 0
                        details.drug_route_uuid=0
                        details.drug_frequency_uuid=0
                        details.duration=0
                        details.duration_period_uuid=0
                        details.drug_instruction_uuid=0
                        details.revision=true
                        details.is_active=true
                        detailsList.add(details)
                    }

                    header?.name = Itemname
                    header?.description = ""
                    header?.template_type_uuid = AppConstants.FAV_TYPE_ID_Vitual
                    header?.diagnosis_uuid =0
                    header?.is_public="false"
                    header?.facility_uuid = facility_UUID?.toString()
                    header?.department_uuid = deparment_UUID?.toString()
                    header?.display_order = binding?.editdisplayorder?.text?.toString()
                    header?.revision = true
                    header?.is_active = true

                    RequestTemplateAddDetails.headers = header!!
                    RequestTemplateAddDetails.details = this.detailsList

                    val request =  Gson().toJson(RequestTemplateAddDetails)

                    Log.i("",""+request)
                    viewModel?.labTemplateDetails(facility_UUID, RequestTemplateAddDetails!!, emrlabtemplatepostRetrofitCallback)

                }
            }

            else{
                val displayordervalue = binding?.editdisplayorder?.text.toString()
                arrayItemData = mAdapter?.getItems()
                newDetailList.clear()
                if(arrayItemData?.size!! > 0)
                {
                    for(i in arrayItemData?.indices!!)
                    {
                        val newDetail : NewDetail = NewDetail()
                        newDetail.template_master_uuid = arrayItemData?.get(i)?.template_master_uuid
                        newDetail.test_master_uuid = 0
                        newDetail.chief_complaint_uuid=0
                        newDetail.vital_master_uuid=arrayItemData?.get(i)!!.test_master_id
                        newDetail.drug_id=0
                        newDetail.drug_route_uuid=0
                        newDetail.drug_frequency_uuid=0
                        newDetail.drug_duration=0
                        newDetail.drug_period_uuid=0
                        newDetail.drug_instruction_uuid=0
                        newDetail.display_order=0
                        newDetail.quantity=0
                        newDetail.revision=true
                        newDetail.is_active=true
                        newDetailList.add(newDetail)
                    }

                    header?.template_id = arrayItemData?.get(0)?.template_master_uuid
                    header?.name =Itemname
                    header?.display_order = displayordervalue
                    header?.is_public = "false"
                    UpdateRequestModule.headers = header!!
                    UpdateRequestModule.new_details = this.newDetailList
                    UpdateRequestModule.removed_details = this.removeList
                    val requestupdate =  Gson().toJson(UpdateRequestModule)
                    Log.i("",""+requestupdate)
                    viewModel?.labUpdateTemplateDetails(facility_UUID, UpdateRequestModule!!, UpdateemrlabtemplatepostRetrofitCallback)
                }
            }
        })
        binding?.vitualSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                val itemValue=parent!!.getItemAtPosition(0).toString()
             
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                pos: Int,
                id: Long) {

                val itemValue=parent!!.getItemAtPosition(pos).toString()
                selectVitualUuid=vitualSpinnerList.filterValues { it == itemValue }.keys.toList()[0]
                selectVitualValue = itemValue

            }
        }

        return binding!!.root
    }

    /*
    Vitual add
     */

    val vitalsSearchRetrofitCallBack =
        object : RetrofitCallback<VitalsSearchResponseModel> {
            override fun onSuccessfulResponse(response: Response<VitalsSearchResponseModel>) {
                //responseContents = Gson().toJson(response.body()?.responseContents)
                if (response.body()?.responseContents?.getVitals?.isNotEmpty()!!) {


                    setVitualSpinnerValue(response?.body()?.responseContents?.getVitals!!)

                      }
            }
            override fun onBadRequest(response: Response<VitalsSearchResponseModel>) {
                val gson = GsonBuilder().create()
                val responseModel: VitalsSearchResponseModel
                try {
                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        VitalsSearchResponseModel::class.java
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

    private fun setVitualSpinnerValue(responseContents: List<GetVital>) {

        vitualSpinnerList = responseContents.map { it.uuid to it.name!! }!!.toMap().toMutableMap()
        hashvitualSpinnerList.clear()

        for(i in responseContents.indices){

            hashvitualSpinnerList[responseContents[i].uuid] = i
        }
//        saveTemplateAdapter!!.setRote(vitualSpinnerList)
        val adapter = ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_item, vitualSpinnerList.values.toMutableList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.vitualSpinner!!.adapter = adapter

    }


    /*
    Add Details
     */

    val emrlabtemplatepostRetrofitCallback = object : RetrofitCallback<ReponseTemplateadd> {
        override fun onSuccessfulResponse(responseBody: Response<ReponseTemplateadd>?) {
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                responseBody?.body()?.message!!
            )
            mAdapter?.cleardata()
            viewModel!!.getTemplete(getTempleteRetrofitCallBack)
        }
        override fun onBadRequest(response: Response<ReponseTemplateadd>) {
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
                    getString(R.string.something_went_wrong)
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

    val getTempleteRetrofitCallBack =
        object : RetrofitCallback<TempleResponseModel> {
            override fun onSuccessfulResponse(response: Response<TempleResponseModel>) {
                var responseContents = Gson().toJson(response.body()?.responseContents)
                callbacktemplate?.onRefreshList()

                /*     if (response.body()?.responseContents?.templates_lab_list?.isNotEmpty()!!) {
                         templeteAdapter.refreshList(response.body()?.responseContents?.templates_lab_list)
                     }*/
            }

            override fun onBadRequest(response: Response<TempleResponseModel>) {
                val gson = GsonBuilder().create()
                val responseModel: TempleResponseModel
                try {
                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        TempleResponseModel::class.java
                    )
                    utils?.showToast(
                        R.color.negativeToast,
                        binding?.mainLayout!!,
                        getString(R.string.bad)
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


    val UpdateemrlabtemplatepostRetrofitCallback = object : RetrofitCallback<UpdateResponse> {
        override fun onSuccessfulResponse(responseBody: Response<UpdateResponse>?) {
            utils?.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                responseBody?.body()?.message!!
            )

            Log.i("",""+responseBody?.body()?.responseContent)

            mAdapter?.cleardata()
            viewModel!!.getTemplete(getTempleteRetrofitCallBack)


        }
        override fun onBadRequest(response: Response<UpdateResponse>) {
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
                    getString(R.string.something_went_wrong)
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




    fun setOnTemplateRefreshListener(callback: OnTemplateRefreshListener) {
        this.callbacktemplate = callback
    }
    // This interface can be implemented by the Activity, parent Fragment,
    // or a separate test implementation.
    interface OnTemplateRefreshListener {
        fun onTemplateID(position: Int)
        fun onRefreshList()
    }


}