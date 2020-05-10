package com.hmisdoctor.ui.quick_reg.view


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.databinding.DataBindingUtil
import com.hmisdoctor.R
import com.hmisdoctor.config.AppConstants

import com.hmisdoctor.utils.Utils

import android.app.Dialog
import android.content.Context
import android.util.Log
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.DialogOrderProcessListBinding
import com.hmisdoctor.databinding.DialogSearchListBinding
import com.hmisdoctor.ui.emr_workflow.radiology.model.RecyclerDto
import com.hmisdoctor.ui.quick_reg.model.QuickSearchresponseContent
import com.hmisdoctor.ui.quick_reg.model.labtest.request.orderRequest.Header
import com.hmisdoctor.ui.quick_reg.model.labtest.request.orderRequest.OrderProcessDetailsResponseModel
import com.hmisdoctor.ui.quick_reg.model.labtest.request.orderRequest.OrderToProcessReqestModel
import com.hmisdoctor.ui.quick_reg.model.labtest.response.OrderProcessResponseModel
import com.hmisdoctor.ui.quick_reg.view_model.LabTestViewModel
import com.hmisdoctor.ui.quick_reg.view_model.LabTestViewModelFactory
import com.hmisdoctor.ui.quick_reg.view_model.QuickRegistrationViewModel
import com.hmisdoctor.ui.quick_reg.view_model.QuickRegistrationViewModelFactory
import com.hmisdoctor.utils.CustomProgressDialog
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class OrderProcessDialogFragment : DialogFragment() {

    private var department_uuid: Int? = null
    private var facilitylevelID: Int? = null
    private var content: String? = null
    private var viewModel: LabTestViewModel? = null
    var binding: DialogOrderProcessListBinding? = null
    private val labTestList: MutableList<orderList> = ArrayList()
    private val searchList: MutableList<RecyclerDto> = ArrayList()
    private var mAdapter: OrderProcessAdapter? = null
    var dialogListener: DialogListener? = null
    private  var favouriteData:ArrayList<Header> =ArrayList()
    private var customProgressDialog: CustomProgressDialog? = null


    private var utils: Utils? = null
    var appPreferences: AppPreferences? = null
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
            DataBindingUtil.inflate(inflater, R.layout.dialog_order_process_list, container, false)
        viewModel = LabTestViewModelFactory(
            requireActivity().application

        )
            .create(LabTestViewModel::class.java)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this
        utils = Utils(requireContext())
        customProgressDialog = CustomProgressDialog(requireContext())

        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        facilitylevelID = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        department_uuid = appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)

        viewModel!!.progress.observe(requireActivity(), Observer {
                progress ->
            if (progress == View.VISIBLE) {
                customProgressDialog!!.show()
            } else if (progress == View.GONE) {
                customProgressDialog!!.dismiss()
            }

        })

        binding?.closeImageView?.setOnClickListener {
            dialog?.dismiss()
        }


        binding?.cancelCardView?.setOnClickListener {
            dialog?.dismiss()
        }


        mAdapter = OrderProcessAdapter(requireContext(), ArrayList())
        val args = arguments

        if (args == null) {

        } else {
            // get value from bundle..
            favouriteData = args.getParcelableArrayList<Header>(AppConstants.RESPONSECONTENT)!!

            Log.i("respose",""+favouriteData)

            val list:ArrayList<orderList> = ArrayList()

            val ol:orderList= orderList()

            ol.title=favouriteData[0].test_master.name

            list.add(ol)


       /*     for(i in favouriteData!!.indices){

                val ol:orderList= orderList()

                ol.title=favouriteData[i].test_master.name

                list.add(ol)
            }
            */

            mAdapter!!.setAll(list)

        }
        val layoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        binding?.orderProcessListrecycleview!!.layoutManager = layoutmanager

        binding?.orderProcessListrecycleview !!.adapter = mAdapter

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")

        binding!!.saveOrder.setOnClickListener {


            val data=mAdapter!!.getAll()

            for(i in favouriteData.indices){

                favouriteData[i].result_value= data[0].name.toString()
                favouriteData[i].qualifier_uuid= data[0].id!!
                favouriteData[i].qualifierid= data[0].id!!
                favouriteData[i].tat_session_end= sdf.format(Date())
                favouriteData[i].tat_session_start= sdf.format(Date())

            }
            val request:OrderToProcessReqestModel= OrderToProcessReqestModel()

            request.header=favouriteData

            viewModel!!.orderProcess(request,orderProcessRetrofitCallback)
        }

        return binding?.root
    }

    val orderProcessRetrofitCallback = object  :
        RetrofitCallback<OrderProcessResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<OrderProcessResponseModel>?) {

            //Call back
            val dialogListener = activity as DialogListener?
            dialogListener!!.onOrderProcessDialogTolabtestactivity()
            //  labListAPI()
            dialog!!.dismiss()

        }

        override fun onBadRequest(errorBody: Response<OrderProcessResponseModel>?) {
            val gson = GsonBuilder().create()
            val responseModel: OrderProcessResponseModel
            try {
                responseModel = gson.fromJson(
                    errorBody!!.errorBody()!!.string(),
                    OrderProcessResponseModel::class.java
                )
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    "something wrong"
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
    interface DialogListener {
        fun onOrderProcessDialogTolabtestactivity()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try { /* this line is main difference for fragment to fragment communication & fragment to activity communication
            fragment to fragment: onInputListener = (OnInputListener) getTargetFragment();
            fragment to activity: onInputListener = (OnInputListener) getActivity();
             */
            dialogListener = targetFragment as DialogListener?
        } catch (e: ClassCastException) {
        }
    }

}

