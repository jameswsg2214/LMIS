package com.hmisdoctor.ui.emr_workflow.certificate.ui

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.DialogManageDiagonosisFavouritesBinding
import com.hmisdoctor.databinding.DialogPrevCertificateBinding
import com.hmisdoctor.ui.dashboard.model.registration.DistrictListResponseModel
import com.hmisdoctor.ui.emr_workflow.certificate.model.GetCertificateResponseModel
import com.hmisdoctor.ui.emr_workflow.certificate.view_model.CertificateViewModel
import com.hmisdoctor.ui.emr_workflow.certificate.view_model.CertificateViewModelFactory
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
import com.hmisdoctor.ui.emr_workflow.diagnosis.view.DiagnosisFavAdapter
import com.hmisdoctor.ui.emr_workflow.diagnosis.view_model.DiagonosisDialogViewModel
import com.hmisdoctor.ui.emr_workflow.diagnosis.view_model.DiagonosisDialogViewModelFactory
import com.hmisdoctor.ui.emr_workflow.lab.model.LabFavManageResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.faveditresponse.FavEditResponse
import com.hmisdoctor.ui.emr_workflow.lab.model.favresponse.FavAddListResponse
import com.hmisdoctor.ui.emr_workflow.lab.ui.PrevLabParentAdapter
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import com.hmisdoctor.ui.emr_workflow.prescription.ui.ErrorAPIClass
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.ui.quick_reg.view.AssignToAdapter
import com.hmisdoctor.utils.Utils
import retrofit2.Response
import java.io.IOException

class PrevCertificateDialog: DialogFragment()  {
    private var content: String? = null
    private var viewModel: CertificateViewModel? = null
    var binding: DialogPrevCertificateBinding? = null
    var appPreferences: AppPreferences? = null
    private var utils: Utils? = null
    private var facility_UUID: Int? = 0
    private var deparment_UUID: Int? = 0
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    val header: Headers= Headers()
    val detailsList : ArrayList<Detail> = ArrayList()
    var prevCertificateDialogAdapter: PrevCertificateDialogAdapter? = null

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
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_prev_certificate, container, false)
        viewModel = CertificateViewModelFactory(
            requireActivity().application
        ).create(CertificateViewModel::class.java)

        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this

        userDetailsRoomRepository = UserDetailsRoomRepository(requireActivity().application)

        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        facility_UUID = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        deparment_UUID = appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)
        val patient_UUID = appPreferences?.getInt(AppConstants.PATIENT_UUID)
        binding?.viewModel?.getCertificateAll(facility_UUID!!,patient_UUID!!, getCertificateRetrofitCallback)


        preparePatientLIstData()


        binding?.closeImageView?.setOnClickListener {
            dialog?.dismiss()
        }

        binding?.cancelCardView?.setOnClickListener {
            dialog?.dismiss()
        }




        return binding!!.root
    }



    val getCertificateRetrofitCallback = object : RetrofitCallback<GetCertificateResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<GetCertificateResponseModel>?) {
            if (responseBody?.body()?.responseContent?.isNotEmpty()!!) {
                viewModel?.errorText?.value = 8.toString()
                prevCertificateDialogAdapter?.refreshList(responseBody?.body()?.responseContent!!)

            } else {
                viewModel?.errorText?.value = 0.toString()
            }






        }

        override fun onBadRequest(errorBody: Response<GetCertificateResponseModel>?) {

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

    private fun preparePatientLIstData() {

        prevCertificateDialogAdapter =
            activity?.let {
                PrevCertificateDialogAdapter((requireActivity())!!)
            }!!
        binding?.certificateRecyclerView!!.adapter = prevCertificateDialogAdapter
    }





}