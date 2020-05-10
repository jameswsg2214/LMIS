package com.hmisdoctor.ui.emr_workflow.certificate.ui

import android.annotation.SuppressLint

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.FragmentBackClick
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.FragmentCertificateChildBinding
import com.hmisdoctor.databinding.FragmentOpNotesChildBinding
import com.hmisdoctor.ui.emr_workflow.admission_referal.model.ReasonResponseModel
import com.hmisdoctor.ui.emr_workflow.admission_referal.model.RefferaNextResponseModel
import com.hmisdoctor.ui.emr_workflow.admission_referal.model.RefferalNextRequestModel
import com.hmisdoctor.ui.emr_workflow.certificate.model.*
import com.hmisdoctor.ui.emr_workflow.certificate.view_model.CertificateViewModel
import com.hmisdoctor.ui.emr_workflow.certificate.view_model.CertificateViewModelFactory
import com.hmisdoctor.ui.emr_workflow.diagnosis.view.FavaddDiagonosisDialog
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddResponseContent
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import com.hmisdoctor.ui.emr_workflow.model.EMR_Request.Detail
import com.hmisdoctor.ui.emr_workflow.model.EMR_Request.EmrRequestModel
import com.hmisdoctor.ui.emr_workflow.model.EMR_Request.Header
import com.hmisdoctor.ui.emr_workflow.op_notes.model.OpNotesExpandableResponseModel
import com.hmisdoctor.ui.emr_workflow.op_notes.model.OpNotesResponsModel
import com.hmisdoctor.ui.emr_workflow.op_notes.model.OpNotesResponseContent
import com.hmisdoctor.ui.emr_workflow.op_notes.view_model.OpNotesViewModel
import com.hmisdoctor.ui.emr_workflow.op_notes.view_model.OpNotesViewModelFactory
import com.hmisdoctor.ui.emr_workflow.prescription.ui.ErrorAPIClass
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import kotlinx.android.synthetic.main.activity_quick_registration.*


import retrofit2.Response
import java.io.IOException

class CertificateChildFragment : Fragment() {
    lateinit var drugNmae: TextView
    private var facility_id: Int? = 0

    private var binding: FragmentCertificateChildBinding? = null
    private var viewModel: CertificateViewModel? = null
    private var utils: Utils? = null
    private var responseContents: String? = ""
    private var responsedata: Int? = 0

    private var favmodel: FavouritesModel? = null
    private var listTemplatesItems: List<TemplateResponseContent?> =ArrayList()
    private var templateResponseMap = mutableMapOf<Int, String>()


    lateinit var dropdownReferenceView: AppCompatAutoCompleteTextView
    private var fragmentBackClick: FragmentBackClick? = null
    var appPreferences: AppPreferences? = null
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
                R.layout.fragment_certificate_child,
                container,
                false
            )
        viewModel = CertificateViewModelFactory(
            requireActivity().application
        )
            .create(CertificateViewModel::class.java)
        binding?.viewModel = viewModel
        binding!!.lifecycleOwner = this
        utils = Utils(requireContext())
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        userDetailsRoomRepository = UserDetailsRoomRepository(requireActivity().application)
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        appPreferences?.saveInt(AppConstants.LAB_MASTER_TYPE_ID, 2)
        facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        val patient_UUID = appPreferences?.getInt(AppConstants.PATIENT_UUID)
        val department_UUID = appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)
        val encounter_Type = appPreferences?.getInt(AppConstants.ENCOUNTER_TYPE)
        val encounter_uuid = appPreferences?.getInt(AppConstants.ENCOUNTER_UUID)
        binding?.opNotesSpinner?.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            viewModel?.getNoteTemplate(facility_id!!,noteTemplateRetrofitCallback)

            false
        })


        viewModel!!.errorText.observe(viewLifecycleOwner,
            Observer { toastMessage ->
                utils!!.showToast(R.color.negativeToast, binding?.mainLayout!!, toastMessage)
            })
        binding?.saveCardView?.setOnClickListener{
            val certificateRequestModel: CertificateRequestModel = CertificateRequestModel()
            certificateRequestModel.patient_uuid= patient_UUID!!.toString()
            certificateRequestModel.encounter_uuid= encounter_uuid!!
            certificateRequestModel.ward_master_uuid=1
            certificateRequestModel.doctor_uuid=  userDataStoreBean?.uuid!!.toString()
            certificateRequestModel.facility_uuid= facility_id.toString()
            certificateRequestModel.department_uuid= department_UUID.toString()
            certificateRequestModel.admission_status_uuid=1
            certificateRequestModel.discharge_type_uuid=1
            certificateRequestModel.data_template=responseContents!!
            certificateRequestModel.note_template_uuid=1
            certificateRequestModel.note_template_uuid=responsedata!!
            certificateRequestModel.note_type_uuid=1
            certificateRequestModel.certified_by=userDataStoreBean?.uuid!!.toString()
            certificateRequestModel.certificate_status_uuid=1
            certificateRequestModel.released_to_patient=1
            certificateRequestModel.released_by=userDataStoreBean?.uuid!!.toString()
            certificateRequestModel.aproved_by=userDataStoreBean?.uuid!!.toString()
            certificateRequestModel.approved_on="2020-05-09T09:27:52.183Z"
            viewModel!!.getSaveNext(certificateRequestModel,NextSAveRetrofitCallback)


        }
        binding?.PrevCertificate?.setOnClickListener{
            val ft = childFragmentManager.beginTransaction()
            val dialog = PrevCertificateDialog()
            dialog.show(ft, "Tag")

        }



        return binding!!.root
    }

    val noteTemplateRetrofitCallback = object : RetrofitCallback<TemplateResponseModel> {
        override fun onSuccessfulResponse(response: Response<TemplateResponseModel>) {

            Log.i("", "" + response?.body()?.responseContents);
            listTemplatesItems = response?.body()?.responseContents!!
            templateResponseMap  =
                listTemplatesItems.map { it?.uuid!! to it.name!! }.toMap().toMutableMap()
            val adapter =
                ArrayAdapter<String>(
                    requireContext(),
                    R.layout.spinner_item,
                    templateResponseMap.values.toMutableList()
                )

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding?.opNotesSpinner!!.adapter = adapter
            adapterCall(response?.body()?.responseContents!!)





        }

        override fun onBadRequest(response: Response<TemplateResponseModel>) {

            //Log.e("InsData", "badReq")
            val gson = GsonBuilder().create()
            val responseModel: TemplateResponseModel
            try {
                responseModel = gson.fromJson(
                    response.errorBody()!!.string(),
                    TemplateResponseModel::class.java
                )
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    response.message()
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

    private fun adapterCall(responseContents: List<TemplateResponseContent>) {

        val adapter = ArrayAdapter<String>(
            this!!.activity!!,
            android.R.layout.simple_spinner_item,
            templateResponseMap.values.toMutableList()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.opNotesSpinner!!.adapter = adapter

        binding?.opNotesSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                val itemValue=parent!!.getItemAtPosition(0).toString()

                //val selectedPoi = parent!!.adapter.getItem(0) as OpNotesResponseContent?

                 responsedata =
                    templateResponseMap.filterValues { it == itemValue }.keys.toList()[0]

                viewModel?.getItemTemplate(facility_id, notesTemplatesRetrofitCallback,responsedata!!)

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                pos: Int,
                id: Long) {



                val itemValue=parent!!.getItemAtPosition(pos).toString()


                 responsedata=    templateResponseMap.filterValues { it == itemValue }.keys.toList()[0]


                viewModel?.getItemTemplate(facility_id, notesTemplatesRetrofitCallback,responsedata!!)

            }
        }


    }
    val notesTemplatesRetrofitCallback =
        object : RetrofitCallback<TemplateItemResponseModel> {
            override fun onSuccessfulResponse(response: Response<TemplateItemResponseModel>) {
                Log.i("", "" + response?.body()?.responseContents?.data_template);
                if (response.body()?.responseContents!=null) {
                    responseContents =
                        binding?.TemplateData?.setText(response?.body()?.responseContents?.data_template)
                            .toString()

                } else {
                    responseContents= binding?.TemplateData?.setText("Comment Here").toString()
                }

            }

            override fun onBadRequest(response: Response<TemplateItemResponseModel>) {
                val gson = GsonBuilder().create()
                val responseModel: TemplateItemResponseModel
                try {
                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        TemplateItemResponseModel::class.java
                    )
                    utils?.showToast(
                        R.color.negativeToast,
                        binding?.mainLayout!!,
                        response.message()
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

    var NextSAveRetrofitCallback = object :
        RetrofitCallback<CertificateResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<CertificateResponseModel>?) {
            Log.i("", "" + responseBody?.body()?.message);

            utils?.showToast(
                R.color.positiveToast,
                mainLayout!!,
                "Register Success"

            )



        }

        override fun onBadRequest(response: Response<CertificateResponseModel>) {
            Log.e("badreq",response.toString())
            val gson = GsonBuilder().create()
            val responseModel: CertificateResponseModel
            var mError = ErrorAPIClass()
            try {
                mError = gson.fromJson(response.errorBody()!!.string(), ErrorAPIClass::class.java)

            } catch (e: IOException) { // handle failure to read error
            }
        }
        override fun onServerError(response: Response<*>) {
            utils?.showToast(
                R.color.negativeToast,
                mainLayout!!,
                "serverError"
            )

        }
        override fun onUnAuthorized() {
            utils?.showToast(
                R.color.negativeToast,
                mainLayout,
                getString(R.string.unauthorized)
            )
        }
        override fun onForbidden() {
            utils?.showToast(
                R.color.negativeToast,
                mainLayout!!,
                "Forbidden"
            )

        }
        override fun onFailure(failure: String) {
            utils?.showToast(
                R.color.negativeToast,
                mainLayout!!,
                failure
            )
        }
        override fun onEverytime() {
            viewModel!!.progress.value = 8
        }

    }








}




