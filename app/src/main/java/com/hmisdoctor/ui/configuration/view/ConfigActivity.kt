package com.hmisdoctor.ui.configuration.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.ActivityConfigBinding
import com.hmisdoctor.ui.configuration.model.ConfigResponseContent
import com.hmisdoctor.ui.configuration.model.ConfigResponseModel
import com.hmisdoctor.ui.configuration.model.ConfigUpdateRequestModel
import com.hmisdoctor.ui.configuration.model.ConfigUpdateResponseModel
import com.hmisdoctor.ui.configuration.view_model.ConfigViewModel
import com.hmisdoctor.ui.configuration.view_model.ConfigViewModelFactory
import com.hmisdoctor.ui.dashboard.view.DashBoardActivity
import com.hmisdoctor.ui.emr_workflow.model.EmrWorkFlowResponseModel
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.CustomProgressDialog
import com.hmisdoctor.utils.Utils
import com.journaldev.androidrecyclerviewdraganddrop.ItemMoveCallback
import com.journaldev.androidrecyclerviewdraganddrop.StartDragListener
import org.hamcrest.Matchers.contains
import retrofit2.Response


class ConfigActivity : AppCompatActivity(), StartDragListener {

    private var utils: Utils? = null
    private var conficFinalData: ArrayList<ConfigResponseContent?>? = ArrayList()
    private var binding: ActivityConfigBinding? = null
    private var viewModel: ConfigViewModel? = null
    private var configadapter: ConfigRecyclerAdapter? = null
    private var configfavadapter: ConfigFavRecyclerAdapter? = null

    internal var touchHelper: ItemTouchHelper? = null
    private var customProgressDialog: CustomProgressDialog? = null
    var requestparameter: ConfigUpdateRequestModel? = ConfigUpdateRequestModel()
    private var configRequestData: ArrayList<ConfigUpdateRequestModel?>? = ArrayList()
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    var appPreferences: AppPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_config)
        //
        viewModel = ConfigViewModelFactory(
            application,
            configRetrofitCallBack
        ).create(ConfigViewModel::class.java)
        binding?.viewModel = viewModel
        binding!!.lifecycleOwner = this
        userDetailsRoomRepository = UserDetailsRoomRepository(application!!)
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        appPreferences = AppPreferences.getInstance(this, AppConstants.SHARE_PREFERENCE_NAME)
        val DepartmentID = appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)
        val facility_id = appPreferences?.getInt(AppConstants?.FACILITY_UUID)
        customProgressDialog = CustomProgressDialog(this)
        utils = Utils(this)
        setSupportActionBar(binding!!.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        binding!!.toolbar.setNavigationOnClickListener { finish() }

        viewModel?.getConfigList()

        ///Config
        viewModel!!.progress.observe(this,
            Observer { progress ->
                if (progress == View.VISIBLE) {
                    customProgressDialog!!.show()
                } else if (progress == View.GONE) {
                    customProgressDialog!!.dismiss()
                }
            })
        viewModel!!.errorText.observe(this,
            Observer { toastMessage ->
                utils!!.showToast(R.color.negativeToast, binding?.mainLayout!!, toastMessage)
            })
        viewModel!!.errorText.observe(this,
            Observer { toastMessage ->
                utils!!.showToast(R.color.negativeToast, binding?.mainLayout!!, toastMessage)
            })
        configadapter = ConfigRecyclerAdapter(this, ArrayList())
        binding!!.ConfigRecyclerView.adapter = configadapter

        //Search get data

        val searchText =
            binding?.searchView?.findViewById(R.id.search_src_text) as TextView
        val tf = ResourcesCompat.getFont(this, R.font.poppins)
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

            startActivity(Intent(this@ConfigActivity, DashBoardActivity::class.java))

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


    override fun requestDrag(viewHolder: RecyclerView.ViewHolder) {
        touchHelper?.startDrag(viewHolder)
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