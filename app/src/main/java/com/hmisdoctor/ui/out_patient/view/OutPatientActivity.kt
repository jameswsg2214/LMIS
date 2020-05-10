package com.hmisdoctor.ui.out_patient.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder

import com.hmisdoctor.R
import com.hmisdoctor.callbacks.PaginationScrollListener
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppConstants.Companion.PATIENT_UUID
import com.hmisdoctor.config.AppConstants.Companion.TYPE_OUT_PATIENT
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.ActivityOutPatientBinding
import com.hmisdoctor.ui.emr_workflow.view.EmrWorkFlowActivity
import com.hmisdoctor.ui.out_patient.search_response_model.ResponseContent
import com.hmisdoctor.ui.out_patient.search_response_model.SearchResponseModel
import com.hmisdoctor.ui.out_patient.view_model.OutPatientViewModel
import com.hmisdoctor.ui.out_patient.view_model.OutPatientViewModelFactory
import com.hmisdoctor.utils.Utils

import retrofit2.Response


class OutPatientActivity : AppCompatActivity() {
    private var binding: ActivityOutPatientBinding? = null
    private var viewModel: OutPatientViewModel? = null
    private var utils: Utils? = null
    lateinit var outPatientAdapter: OutPatientAdapter
    private var currentPage = 0
    private var pageSize = 10
    private var isLoading = false
    private var isLastPage = false
    private var TOTAL_PAGES: Int = 0
    private var appPreferences: AppPreferences? = null
    private var queryvalue : String ?=""
    private var patientType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_out_patient)
        viewModel = OutPatientViewModelFactory(
            application
        ).create(OutPatientViewModel::class.java)
        binding?.viewModel = viewModel
        binding!!.lifecycleOwner = this
        utils = Utils(this)
        viewModel!!.errorText.observe(this,
            Observer { toastMessage ->
                utils!!.showToast(R.color.negativeToast, binding?.mainLayout!!, toastMessage)
            })
        patientType = intent.getStringExtra(AppConstants.PATIENT_TYPE)
        setSupportActionBar(binding!!.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        binding!!.toolbar.setNavigationOnClickListener { finish() }
        toolBarTitle()
        appPreferences = AppPreferences.getInstance(this, AppConstants.SHARE_PREFERENCE_NAME)
        val searchText =
            binding?.searchView?.findViewById(R.id.search_src_text) as TextView

        val tf = ResourcesCompat.getFont(this, R.font.poppins)
        searchText.typeface = tf
        searchText.text = ""
        initVisitOutPatientAdapter()
        binding?.searchView?.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            @SuppressLint("RestrictedApi")
            override fun onQueryTextSubmit(query: String): Boolean {
                callSearch(query)
                binding?.searchView?.clearFocus()
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
            internal fun callSearch(query: String) {
                queryvalue = query
                outPatientAdapter.clearAll()
                viewModel?.searchPatient(
                    query,
                    currentPage,
                    pageSize,
                    "modified_date",
                    "DESC",
                    patientSearchRetrofitCallBack
                )
            }

        })
        binding?.qrCodeImageview?.setOnClickListener{
//            showDialog()

        }
    }
/*    private fun showDialog() {
        val dialog = Dialog(this)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog .setCancelable(false)
        dialog .setContentView(R.layout.custom_layout)
        val body = dialog .findViewById(R.id.customdialog) as ImageView
        val closebtn = dialog.findViewById(R.id.close_btn) as Button
// String text="" // Whatever you need to encode in the QR code
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix = multiFormatWriter.encode(title as String?, BarcodeFormat.QR_CODE,300,300);
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix);
            body.setImageBitmap(bitmap);
            closebtn.setOnClickListener {
                dialog .dismiss()
            }
        } catch ( e: Exception) {
            e.printStackTrace();
        }


        dialog .show()

    }*/


    private fun toolBarTitle(){
        binding!!.toolbar.title = patientType
    }

    private fun initVisitOutPatientAdapter() {
        outPatientAdapter = OutPatientAdapter(this)
        val gridLayoutManager =
            GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
        binding?.recyclerView?.layoutManager = gridLayoutManager
        binding?.recyclerView?.adapter = outPatientAdapter

       outPatientAdapter.setOnItemClickListener(object :
            OutPatientAdapter.OnItemClickListener {
            override fun onItemClick(responseContent: ResponseContent?, position: Int) {
                appPreferences?.saveInt(
                    AppConstants.PATIENT_UUID,
                    responseContent?.patient_visits?.get(0)?.patient_uuid!!
                )
                appPreferences?.saveInt(AppConstants.ENCOUNTER_TYPE, TYPE_OUT_PATIENT)
                startActivity(
                    Intent(this@OutPatientActivity, EmrWorkFlowActivity::class.java)
                        .putExtra(
                            PATIENT_UUID,
                            responseContent?.patient_visits?.get(0)?.patient_uuid
                        )
                        .putExtra(AppConstants.ENCOUNTER_TYPE, TYPE_OUT_PATIENT)
                )
            }
        })
     /*   val linearLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding?.recyclerView?.layoutManager = linearLayoutManager*/
        binding?.recyclerView?.addOnScrollListener(object :
            PaginationScrollListener(gridLayoutManager) {

            override fun loadMoreItems() {
                println("bindingdsafgsethry = ${binding}")
                currentPage += 1
                if (currentPage <= TOTAL_PAGES) {
                    viewModel?.getPatientListNextPage(
                        queryvalue!!,currentPage,
                        patientSearchNextRetrofitCallBack

                    )
                }
            }
            override fun getTotalPageCount(): Int {
                return TOTAL_PAGES!!
            }

            override fun isLastPageReached(): Boolean {
                return isLastPage
            }

            override fun isLoadingHappening(): Boolean {
                println("isLoadingdefwregwtrhey = ${isLoading}")
                return isLoading
            }
        })
    }
    val patientSearchRetrofitCallBack = object : RetrofitCallback<SearchResponseModel> {
        override fun onSuccessfulResponse(response: Response<SearchResponseModel>) {

            if (response.body()?.responseContents?.isNotEmpty()!!) {
//                viewModel?.errorTextVisibility?.value = 8

                TOTAL_PAGES = Math.ceil(response!!.body()!!.totalRecords!!.toDouble() / 10).toInt()

                if (response.body()!!.responseContents!!.isNotEmpty()!!) {
                    outPatientAdapter!!.addAll(response!!.body()!!.responseContents)
                    if (currentPage < TOTAL_PAGES!!) {
                        outPatientAdapter!!.addLoadingFooter()
                        isLoading = true
                        isLastPage = false
                    } else {
                        outPatientAdapter!!.removeLoadingFooter()
                        isLoading = false
                        isLastPage = true
                    }

                } else {
                    outPatientAdapter!!.removeLoadingFooter()
                    isLoading = false
                    isLastPage = true
                }
            }

        }

           /* if (response.body()?.responseContents?.isNotEmpty()!! && response.body()?.responseContents!!.size == 1) {
                TOTAL_PAGES = ceil((response.body()!!.totalRecords as Int / 15).toDouble()).toInt()
                outPatientAdapter.addAll(response.body()?.responseContents)
                val pos = 0
                Handler().postDelayed(Runnable {
                    binding!!.recyclerView.findViewHolderForAdapterPosition(pos)!!.itemView.performClick()
                }, 1)
            }else{
                TOTAL_PAGES = ceil((response.body()!!.totalRecords as Int / 15).toDouble()).toInt()
                outPatientAdapter.addAll(response.body()?.responseContents)
            }
*/


        override fun onBadRequest(response: Response<SearchResponseModel>) {
            val gson = GsonBuilder().create()
            val responseModel: SearchResponseModel
            try {
                responseModel = gson.fromJson(
                    response.errorBody()!!.string(),
                    SearchResponseModel::class.java
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
            viewModel!!.progressBar.value = 8
        }
    }

    val patientSearchNextRetrofitCallBack = object : RetrofitCallback<SearchResponseModel> {
        override fun onSuccessfulResponse(response: Response<SearchResponseModel>) {
            if (response.body()?.responseContents!!.isNotEmpty()!!) {

                outPatientAdapter!!.removeLoadingFooter()
                isLoading = false

                outPatientAdapter?.addAll(response.body()!!.responseContents)

                println("testing for two  = $currentPage--$TOTAL_PAGES")

                if (currentPage < TOTAL_PAGES!!) {
                    outPatientAdapter?.addLoadingFooter()
                    isLoading = true
                    isLastPage = false
                    println("testing for four  = $currentPage--$TOTAL_PAGES")
                } else {
                    isLastPage = true
//                    visitHistoryAdapter.removeLoadingFooter()
                    isLoading = false
                    isLastPage = true
                    println("testing for five  = $currentPage--$TOTAL_PAGES")
                }
            } else {
                println("testing for six  = $currentPage--$TOTAL_PAGES")
                outPatientAdapter?.removeLoadingFooter()
                isLoading = false
                isLastPage = true
            }
        }

        override fun onBadRequest(response: Response<SearchResponseModel>?) {
            outPatientAdapter?.removeLoadingFooter()
            isLoading = false
            isLastPage = true

        }

        override fun onServerError(response: Response<*>) {
            viewModel!!.progressBar.value = View.GONE
            utils!!.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }

        override fun onUnAuthorized() {
            viewModel!!.progressBar.value = View.GONE
            utils!!.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }

        override fun onForbidden() {
            viewModel!!.progressBar.value = View.GONE
            utils!!.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }

        override fun onFailure(failure: String) {
            utils!!.showToast(R.color.negativeToast, binding?.mainLayout!!, failure)
        }

        override fun onEverytime() {
            viewModel!!.progressBar.value = 8
        }
    }


}