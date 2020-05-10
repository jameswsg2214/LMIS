package com.hmisdoctor.ui.emr_workflow.prescription.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.ZeroStockDialogFragmentBinding
import com.hmisdoctor.ui.emr_workflow.prescription.model.ZeroStockResponseModel
import com.hmisdoctor.ui.emr_workflow.prescription.view_model.ZeroStockViewModel
import com.hmisdoctor.ui.emr_workflow.prescription.view_model.ZeroStockViewModelFactory
import com.hmisdoctor.utils.Utils
import retrofit2.Response

class ZeroStockDialogFragment : DialogFragment() {
    private var content: String? = null
    private var viewModel: ZeroStockViewModel? = null
    var binding: ZeroStockDialogFragmentBinding? = null
    var appPreferences: AppPreferences? = null
    private var utils: Utils? = null
    private var facility_UUID: Int? = 0
    private var deparment_UUID: Int? = 0
    var zeroStockAdapter: ZeroStockAdapter? = null

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
            DataBindingUtil.inflate(inflater, R.layout.zero_stock_dialog_fragment, container, false)
        viewModel = ZeroStockViewModelFactory(
            requireActivity().application
        )   .create(ZeroStockViewModel::class.java)

        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this
        viewModel?.getZeroStock(facility_UUID.toString(), zeroStockCallBack)
        zeroStockAdapter =
            ZeroStockAdapter(
                requireActivity(),
                ArrayList()
            )

        val linearLayoutManager = LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false)
        binding?.zeroStockRecyclerView?.layoutManager = linearLayoutManager
        binding?.zeroStockRecyclerView?.adapter = zeroStockAdapter

        binding?.closeImageView?.setOnClickListener {
            dialog?.dismiss()
        }

        binding?.cancel!!.setOnClickListener({
            dialog!!.dismiss()
        })


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
                zeroStockAdapter!!.getFilter().filter(query)
            }

        })


        return binding!!.root
    }

    val zeroStockCallBack = object : RetrofitCallback<ZeroStockResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<ZeroStockResponseModel>?) {

            responseBody?.body()?.responseContents?.let { zeroStockAdapter?.setFavAddItem(it) }

            Log.e("zerStock", responseBody?.body()?.responseContents.toString())
        }

        override fun onBadRequest(errorBody: Response<ZeroStockResponseModel>?) {
            val gson = GsonBuilder().create()
            val responseModel: ZeroStockResponseModel
            try {
                responseModel = gson.fromJson(
                    errorBody!!.errorBody()!!.string(),
                    ZeroStockResponseModel::class.java
                )

                Log.e("zerStock", "BadRequest" + responseModel.message!!)

            } catch (e: Exception) {
                Log.e("zerStock", "BadRequest")
                e.printStackTrace()
            }

            // Log.e("postAllergyData", "BadRequest")

        }

        override fun onServerError(response: Response<*>?) {

            Log.e("zerStock", "server")
        }

        override fun onUnAuthorized() {
            Log.e("zerStock", "UnAuth")
        }

        override fun onForbidden() {
            Log.e("postAllergyData", "ForBidd")
        }

        override fun onFailure(s: String?) {
            Log.e("zerStock", s.toString())
        }

        override fun onEverytime() {

        }

    }


}