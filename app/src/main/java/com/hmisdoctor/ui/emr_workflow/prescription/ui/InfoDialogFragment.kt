package com.hmisdoctor.ui.emr_workflow.prescription.ui
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment

import com.google.gson.GsonBuilder

import com.hmisdoctor.R
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.InfoDialogFragmentLayoutBinding
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import com.hmisdoctor.ui.emr_workflow.prescription.model.*
import com.hmisdoctor.ui.emr_workflow.prescription.view_model.InfoViewModel
import com.hmisdoctor.ui.emr_workflow.prescription.view_model.InfoViewModelFactory

import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.Utils
import retrofit2.Response


class InfoDialogFragment : DialogFragment() {
    private var drugID: String?=""
    private var content: String? = null
    private var routeSpinnerList = mutableMapOf<Int, String>()
    private var frequencySpinnerList = mutableMapOf<Int, String>()
    private var instructionSpinnerList = mutableMapOf<Int, String>()
    private var viewModel: InfoViewModel? = null
    private var prescriptionInfoData: PrescriptionInfoResponsModel?=null

    var binding: InfoDialogFragmentLayoutBinding? = null
    var appPreferences: AppPreferences? = null
    private var utils: Utils? = null
    private var facility_UUID: Int? = 0
    private var deparment_UUID: Int? = 0
    private var facility_id: Int? = 0
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null

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
            DataBindingUtil.inflate(inflater, R.layout.info_dialog_fragment_layout, container, false)
        viewModel = InfoViewModelFactory(
            requireActivity().application
        )
            .create(InfoViewModel::class.java)

        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        userDetailsRoomRepository = UserDetailsRoomRepository(requireActivity().application)
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
//
        facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)


        binding?.closeImageView?.setOnClickListener {
            dialog?.dismiss()
        }
        val args = arguments
        if (args != null) {
           drugID = args.getString("DrugID")
        }
        viewModel?.getPrescriptionInfoDetails(drugID,facility_id,prescriptionInfoRetrofitCallback)
        return binding!!.root
    }
    val prescriptionInfoRetrofitCallback =
        object : RetrofitCallback<PrescriptionInfoResponsModel> {
            override fun onSuccessfulResponse(response: Response<PrescriptionInfoResponsModel>) {
                Log.i("res", "" + response?.body()?.responseContents)
                binding?.drugNameAutoTextView?.setText(response?.body()?.responseContents?.name)
                binding?.pharmacyAutoTextView?.setText(response?.body()?.responseContents?.stock_item?.store_master?.store_name)
                binding?.drugCodeAutoTextView?.setText(response?.body()?.responseContents?.code);
                binding?.availableQuantityAutoTextVIew?.setText(Integer.toString(response?.body()?.responseContents?.stock_item?.quantity!!));

            }

            override fun onBadRequest(response: Response<PrescriptionInfoResponsModel>) {
                val gson = GsonBuilder().create()
                val responseModel: PrescriptionInfoResponsModel
                try {

                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        PrescriptionInfoResponsModel::class.java
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
}