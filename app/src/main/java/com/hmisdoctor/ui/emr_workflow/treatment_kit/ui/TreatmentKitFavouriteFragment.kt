package com.hmisdoctor.ui.emr_workflow.treatment_kit.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.FragmentFavouriteBinding
import com.hmisdoctor.databinding.TreatmentKitFragmentFavouriteBinding
import com.hmisdoctor.ui.emr_workflow.chief_complaint.view_model.LabViewModelFactory
import com.hmisdoctor.ui.emr_workflow.delete.model.DeleteResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.ui.ClearFavParticularPositionListener
import com.hmisdoctor.ui.emr_workflow.lab.view_model.LabViewModel
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesResponseModel
import com.hmisdoctor.ui.emr_workflow.radiology.ui.RadiologyFavouriteFragment
import com.hmisdoctor.ui.emr_workflow.radiology.ui.RadiologyTempleteFragment
import com.hmisdoctor.ui.emr_workflow.treatment_kit.view_model.TreatmentKitViewModel
import com.hmisdoctor.ui.emr_workflow.treatment_kit.view_model.TreatmentKitViewModelFactory
import com.hmisdoctor.utils.Utils
import retrofit2.Response


class TreatmentKitFavouriteFragment : Fragment(), ClearFavParticularPositionListener,FavaddTreatmentKitDialog.OnFavRefreshListener{

    private var customdialog: Dialog?=null
    private var typeDepartmentList = mutableMapOf<Int,String>()
    private var facility_UUID: Int?  =0
    @SuppressLint("ClickableViewAccessibility")
    var binding : TreatmentKitFragmentFavouriteBinding ?=null
    private var department_uuid: Int? = null
    private var viewModel: TreatmentKitViewModel? = null
    private var responseContents: String? = ""

    lateinit var treatmentFavouritesAdapter: TreatmentKitDiagnosisFavouritesAdapter

    var mCallback: FavClickedListener? =null

    private var utils: Utils? = null
    var appPreferences: AppPreferences? = null
     override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.treatment_kit_fragment_favourite,
                container,
                false
            )

        viewModel = TreatmentKitViewModelFactory(
            activity!!.application
        ).create(TreatmentKitViewModel::class.java)
        binding?.viewModel = viewModel
        binding!!.lifecycleOwner = this
         appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
         facility_UUID = appPreferences?.getInt(AppConstants.FACILITY_UUID)
         department_uuid = appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)

         utils = Utils(requireContext())

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
                treatmentFavouritesAdapter.getFilter().filter(query)
            }

        })
        binding?.manageFavouritesCardView?.setOnClickListener {
            val ft = childFragmentManager.beginTransaction()
            val managedialog = FavaddTreatmentKitDialog()
            managedialog.show(ft, "Tag")

        }
        initFavouritesAdapter()
         viewModel!!.getdiagnosisFavourites(department_uuid,getFavouritesRetrofitCallBack)
         viewModel!!.getPrescriptionFavourites(department_uuid,getFavouritesRetrofitCallBack)


         return binding!!.root
    }
    private fun initFavouritesAdapter() {
        treatmentFavouritesAdapter =
            TreatmentKitDiagnosisFavouritesAdapter(requireContext())
        val gridLayoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        binding?.recyclerView?.layoutManager = gridLayoutManager
        binding?.recyclerView?.adapter = treatmentFavouritesAdapter
        treatmentFavouritesAdapter.setOnItemClickListener(object :
            TreatmentKitDiagnosisFavouritesAdapter.OnItemClickListener {
            override fun onItemClick(
                responseContent: FavouritesModel?,
                position: Int,
                selected: Boolean
            ) {
                treatmentFavouritesAdapter.updateSelectStatus(position, selected)
                mCallback?.sendFavAddInLab(responseContent,position,selected)
            }})
        treatmentFavouritesAdapter.setOnItemDeleteClickListener(object :
            TreatmentKitDiagnosisFavouritesAdapter.OnItemDeleteClickListner {
            override fun onItemClick(
                responseContent: FavouritesModel?
            ) {
                customdialog = Dialog(requireContext())
                customdialog!! .requestWindowFeature(Window.FEATURE_NO_TITLE)
                customdialog!! .setCancelable(false)
                customdialog!! .setContentView(R.layout.delete_cutsom_layout)
                val closeImageView = customdialog!! .findViewById(R.id.closeImageView) as ImageView

                closeImageView.setOnClickListener {
                    customdialog?.dismiss()
                }
               val drugNmae = customdialog!!.findViewById(R.id.addDeleteName) as TextView
                drugNmae.text ="${drugNmae.text.toString()} '"+responseContent?.diagnosis_name+"' Record ?"

                val yesBtn = customdialog!! .findViewById(R.id.yes) as CardView
                val noBtn = customdialog!! .findViewById(R.id.no) as CardView
                yesBtn.setOnClickListener {
                    viewModel!!.deleteFavourite(
                        facility_UUID,
                        responseContent?.favourite_id!!,
                        deleteRetrofitResponseCallback
                    )
                }
                noBtn.setOnClickListener {
                    customdialog!! .dismiss()
                }
                customdialog!! .show()
            }
        })
        treatmentFavouritesAdapter?.setOnItemViewClickListener(object :TreatmentKitDiagnosisFavouritesAdapter.OnItemViewClickListner{
            override fun onItemClick(responseContent: FavouritesModel?) {

                val ft = childFragmentManager.beginTransaction()
                val managedialog = FavaddTreatmentKitDialog()
                val bundle = Bundle()
                bundle.putParcelable(AppConstants.RESPONSECONTENT, responseContent)
                managedialog.setArguments(bundle)
                managedialog.show(ft, "Tag")
            }

        })
    }
    val getFavouritesRetrofitCallBack =
        object : RetrofitCallback<FavouritesResponseModel> {
            override fun onSuccessfulResponse(response: Response<FavouritesResponseModel>) {
                responseContents = Gson().toJson(response.body()?.responseContents)
                if (response.body()?.responseContents?.isNotEmpty()!!) {
                    treatmentFavouritesAdapter.refreshList(response.body()?.responseContents)
                }
            }

            override fun onBadRequest(response: Response<FavouritesResponseModel>) {
                val gson = GsonBuilder().create()
                val responseModel: FavouritesResponseModel
                try {
                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        FavouritesResponseModel::class.java
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
    //defining Interface
    interface FavClickedListener {
        fun sendFavAddInLab(
            favmodel: FavouritesModel?,
            position: Int,
            selected: Boolean
        )
    }
    fun setOnTextClickedListener(callback: FavClickedListener) {
        this.mCallback = callback
    }
    override fun ClearFavParticularPosition(position: Int) {
        treatmentFavouritesAdapter.refreshFavParticularData(position)
    }

    override fun ClearAllData() {
        treatmentFavouritesAdapter.refreshAllData()
    }

    override fun checkanduncheck(position: Int,isSelect:Boolean) {

    }

    override fun drugIdPresentCheck(drug_id: Int): Boolean {
       return false
    }

    override fun clearDataUsingDrugid(drug_id: Int) {

    }
    override fun favouriteID(favouriteID: Int) {
        viewModel!!.getdiagnosisFavourites(department_uuid,getFavouritesRetrofitCallBack)
        viewModel!!.getPrescriptionFavourites(department_uuid,getFavouritesRetrofitCallBack)

    }
    var deleteRetrofitResponseCallback = object : RetrofitCallback<DeleteResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<DeleteResponseModel>?) {
            viewModel!!.getdiagnosisFavourites(department_uuid,getFavouritesRetrofitCallBack)
            viewModel!!.getPrescriptionFavourites(department_uuid,getFavouritesRetrofitCallBack)

            customdialog!!.dismiss()
            Log.e("DeleteResponse", responseBody?.body().toString())
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
        }}
    override fun onFavID(position: Int) {
        viewModel!!.getdiagnosisFavourites(department_uuid,getFavouritesRetrofitCallBack)
        viewModel!!.getPrescriptionFavourites(department_uuid,getFavouritesRetrofitCallBack)


    }

    override fun onRefreshList() {
        viewModel!!.getdiagnosisFavourites(department_uuid,getFavouritesRetrofitCallBack)
        viewModel!!.getPrescriptionFavourites(department_uuid,getFavouritesRetrofitCallBack)

    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        if (childFragment is FavaddTreatmentKitDialog) {
            childFragment.setOnFavRefreshListener(this)
        }
    }

}