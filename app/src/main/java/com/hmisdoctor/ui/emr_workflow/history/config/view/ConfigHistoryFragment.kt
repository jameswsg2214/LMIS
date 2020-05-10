package com.hmisdoctor.ui.emr_workflow.history.config.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.ActivityConfigBinding
import com.hmisdoctor.databinding.FragmentHisoryConfigBinding
import com.hmisdoctor.ui.configuration.model.ConfigResponseContent
import com.hmisdoctor.ui.configuration.model.ConfigResponseModel
import com.hmisdoctor.ui.configuration.model.ConfigUpdateRequestModel
import com.hmisdoctor.ui.configuration.model.ConfigUpdateResponseModel
import com.hmisdoctor.ui.configuration.view.ConfigFavRecyclerAdapter
import com.hmisdoctor.ui.configuration.view.ConfigRecyclerAdapter

import com.hmisdoctor.ui.emr_workflow.history.config.view_model.HistoryConfigViewModel
import com.hmisdoctor.ui.emr_workflow.history.config.view_model.HistoryConfigViewModelFactory
import com.hmisdoctor.ui.emr_workflow.model.EmrWorkFlowResponseModel

import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.CustomProgressDialog
import com.hmisdoctor.utils.Utils
import com.journaldev.androidrecyclerviewdraganddrop.ItemMoveCallback
import com.journaldev.androidrecyclerviewdraganddrop.StartDragListener
import retrofit2.Response


class ConfigHistoryFragment : DialogFragment(), StartDragListener {

    private var utils: Utils? = null
    private var conficFinalData: ArrayList<ConfigResponseContent?>? = ArrayList()
    private var binding: FragmentHisoryConfigBinding? = null
    private var viewModel: HistoryConfigViewModel? = null
    private var configadapter: ConfigRecyclerAdapter? = null
    private var configfavadapter: ConfigFavRecyclerAdapter? = null

    internal var touchHelper: ItemTouchHelper? = null
    private var customProgressDialog: CustomProgressDialog? = null
    var requestparameter: ConfigUpdateRequestModel? = ConfigUpdateRequestModel()
    private var configRequestData: ArrayList<ConfigUpdateRequestModel?>? = ArrayList()
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    var appPreferences: AppPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_hisory_config, container, false)
        viewModel = HistoryConfigViewModelFactory(
            requireActivity().application,configRetrofitCallBack
        )  .create(HistoryConfigViewModel::class.java)

        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this

        userDetailsRoomRepository = UserDetailsRoomRepository(requireActivity().application)
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        val DepartmentID = appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)
        val facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        customProgressDialog = CustomProgressDialog(requireContext())
        utils = Utils(requireContext())
        viewModel?.getConfigList()
        ///Config
        viewModel!!.progress.observe(requireActivity(),
            Observer { progress ->
                if (progress == View.VISIBLE) {
                    customProgressDialog!!.show()
                } else if (progress == View.GONE) {
                    customProgressDialog!!.dismiss()
                }
            })
        viewModel!!.errorText.observe(requireActivity(),
            Observer { toastMessage ->
                utils!!.showToast(R.color.negativeToast, binding?.mainLayout!!, toastMessage)
            })

        configadapter = ConfigRecyclerAdapter(requireContext(), ArrayList())
        binding!!.ConfigRecyclerView.adapter = configadapter

        //Search get data

        val searchText =
            binding?.searchView?.findViewById(R.id.search_src_text) as TextView
        val tf = ResourcesCompat.getFont(requireContext(), R.font.poppins)
        searchText.typeface = tf
        binding?.searchView?.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            @SuppressLint("RestrictedApi")
            override fun onQueryTextSubmit(query: String): Boolean {
                callSearch(query)
                binding?.searchView?.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                callSearch(newText)
                return true
            }

            fun callSearch(query: String) {
                configadapter?.getFilter()?.filter(query)
            }

        })
        binding?.closeImageView?.setOnClickListener {
            dialog?.dismiss()
        }




        configfavadapter = ConfigFavRecyclerAdapter(this, ArrayList())


        val callback = ItemMoveCallback(configfavadapter)
        touchHelper = ItemTouchHelper(callback)
        touchHelper!!.attachToRecyclerView(binding?.ConfigfavRecyclerView)


        binding!!.ConfigfavRecyclerView.adapter = configfavadapter


        binding!!.click.setOnClickListener {

            conficFinalData = configfavadapter?.getFinalData()

            if (conficFinalData?.size!! > 0) {
                for (i in conficFinalData?.indices!!) {
                    val configData: ConfigUpdateRequestModel = ConfigUpdateRequestModel()
                    configData.facility_uuid = facility_id
                    configData.department_uuid = DepartmentID
                    configData.work_flow_order = i + 1
                    configData.user_uuid = userDataStoreBean?.uuid
                    configData.role_uuid = 1
                    configData.status = true
                    configData.revision = 1
                    configData.context_uuid = conficFinalData?.get(i)?.context_uuid
                    configData.context_activity_map_uuid = conficFinalData?.get(i)!!.uuid
                    configData.activity_uuid = conficFinalData?.get(i)?.activity_uuid

                    configRequestData!!.add(configData)
                }
                viewModel?.postRequestParameter(
                    facility_id,
                    configRequestData!!,
                    configFinalRetrofitCallBack
                )
            } else {
//                utils!!.showToast(R.color.negativeToast, binding?.mainLayout!!, "Please add any one item")
            }

        }

        binding?.moveall!!.setOnClickListener {
            conficFinalData = configadapter?.getConfigData()
            for (i in conficFinalData!!.indices) {
                configfavadapter?.setConfigfavList(conficFinalData!!.get(i))
            }
            configadapter?.removeall(conficFinalData)


            binding?.conficount?.setText(configadapter?.getItemSize().toString())

            binding?.confifavcount?.setText(configfavadapter?.getItemSize().toString())

        }
        configadapter!!.setOnItemClickListener(object :
            ConfigRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(
                configResponseContent: ConfigResponseContent,
                position: Int
            ) {

                configfavadapter?.setConfigfavList(configResponseContent)
                binding?.confifavcount?.setText(configfavadapter?.getItemSize().toString())
                configadapter?.removeitem(position)
                binding?.conficount?.setText(configadapter?.getItemSize().toString())


            }
        })
        configfavadapter!!.setOnItemClickListener(object :
            ConfigFavRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(
                configfavResponseContent: ConfigResponseContent?,
                position: Int
            ) {
                configadapter?.setConfigList(configfavResponseContent)
                configfavadapter?.removeitem(position)

                binding?.conficount?.setText(configadapter?.getItemSize().toString())

                binding?.confifavcount?.setText(configfavadapter?.getItemSize().toString())

            }

        })

        return binding!!.root

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }
    override fun requestDrag(viewHolder: RecyclerView.ViewHolder) {
        touchHelper?.startDrag(viewHolder)
    }

    val configRetrofitCallBack = object : RetrofitCallback<ConfigResponseModel> {

        override fun onSuccessfulResponse(responseBody: Response<ConfigResponseModel>?) {

            configadapter?.setConfigItem(responseBody!!.body()?.responseContents!!)

            binding?.conficount?.setText(responseBody!!.body()?.responseContents!!?.size.toString())
            viewModel?.getEmrWorkFlowFav(emrWorkFlowRetrofitCallBack)

        }

        override fun onBadRequest(errorBody: Response<ConfigResponseModel>?) {
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

    private val configFinalRetrofitCallBack = object : RetrofitCallback<ConfigUpdateResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<ConfigUpdateResponseModel>?) {
            utils!!.showToast(
                R.color.positiveToast,
                binding?.mainLayout!!,
                responseBody?.body()?.message!!
            )

        }

        override fun onBadRequest(errorBody: Response<ConfigUpdateResponseModel>?) {
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



    private val emrWorkFlowRetrofitCallBack = object : RetrofitCallback<EmrWorkFlowResponseModel> {
        override fun onSuccessfulResponse(response: Response<EmrWorkFlowResponseModel>) {
            if (response.body()?.responseContents?.isNotEmpty()!!) {

                configfavadapter?.setConfigfavarrayList(response.body()?.responseContents!!)
                binding?.confifavcount?.setText(configfavadapter?.getItemSize().toString())

            }
        }

        override fun onBadRequest(response: Response<EmrWorkFlowResponseModel>) {
            val gson = GsonBuilder().create()
            val responseModel: EmrWorkFlowResponseModel
            try {
                responseModel = gson.fromJson(
                    response.errorBody()!!.string(),
                    EmrWorkFlowResponseModel::class.java
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

}