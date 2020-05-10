package com.hmisdoctor.ui.emr_workflow.chief_complaint.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.FragmentBackClick
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.FragmentChiefComplaintsChildBinding
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesResponseModel
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.duration.DurationResponseModel
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.request.ChiefComplaintRequestModel
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.response.ChiefComplaintResponse
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.search_complaint.ComplaintSearchResponseModel
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.search_complaint.ResponseContent
import com.hmisdoctor.ui.emr_workflow.chief_complaint.view_model.ChiefComplaintViewModel
import com.hmisdoctor.ui.emr_workflow.chief_complaint.view_model.ChiefComplaintViewModelFactory
import com.hmisdoctor.ui.emr_workflow.lab.ui.ClearFavParticularPositionListener
import com.hmisdoctor.utils.Utils
import retrofit2.Response

class ChiefComplaintChildFragment : Fragment(),ChiefComplaintFavouriteFragment.FavClickedListener
     {
    private var customdialog: Dialog?=null
    private var binding: FragmentChiefComplaintsChildBinding? = null
    private var viewModel: ChiefComplaintViewModel? = null
    private var utils: Utils? = null
     var chiefComplaintFavouritesAdapter: Chief_Complaint_FavouritesAdapter?=null
    private var responseContents: String? = ""
    lateinit var dropdownReferenceView:AppCompatAutoCompleteTextView
    lateinit var chiefComplaintsAdapter: ChiefComplaintsAdapter
    private var fragmentBackClick: FragmentBackClick? = null
    private var selectedSearchPosition = -1
    lateinit var drugNmae: TextView
    lateinit var searchAutoCompleteTextView:AppCompatAutoCompleteTextView
    private val chiefcomplaintRequestArray= ArrayList<ChiefComplaintRequestModel>()
    private var appPreferences : AppPreferences?= null
    private var facility_id: Int? = 0
    private var departmentID: Int? = 0
    var mCallbackChiefFavFragment: ClearFavParticularPositionListener? = null



    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_chief_complaints_child,
                container,
                false
            )

        if (activity !is FragmentBackClick) {
            throw ClassCastException("Hosting activity must implement BackHandlerInterface")
        } else {
            fragmentBackClick = activity as FragmentBackClick?
        }
        viewModel = ChiefComplaintViewModelFactory(
            requireActivity().application
        )
            .create(ChiefComplaintViewModel::class.java)

        binding?.viewModel = viewModel

        binding!!.lifecycleOwner = this
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
         departmentID =appPreferences?.getInt(AppConstants?.DEPARTMENT_UUID)

        binding?.viewpager?.offscreenPageLimit
        utils = Utils(requireContext())

        viewModel!!.errorText.observe(viewLifecycleOwner,
            Observer { toastMessage ->
                utils!!.showToast(R.color.negativeToast, binding?.mainLayout!!, toastMessage)
            })

        binding?.favouriteDrawerCardView?.setOnClickListener {
            binding?.drawerLayout!!.openDrawer(GravityCompat.END)
        }

        binding?.drawerLayout?.drawerElevation = 0f
        binding?.drawerLayout?.setScrimColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )

        setAdapter()
        setupViewPager(binding?.viewpager!!)
        binding?.tabs!!.setupWithViewPager(binding?.viewpager!!)
        //  initFavouritesAdapter()

       // viewModel?.getFavourites(departmentID,getFavouritesRetrofitCallBack)

        val patient_UUID = appPreferences?.getInt(AppConstants.PATIENT_UUID)
        val encounter_Type = appPreferences?.getInt(AppConstants.ENCOUNTER_TYPE)
        val encounter_uuid = appPreferences?.getInt(AppConstants.ENCOUNTER_UUID)
         facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        binding?.saveCardView?.setOnClickListener {

            val arrayItemData=chiefComplaintsAdapter.getall()
            if(arrayItemData.size > 0)
            {
                arrayItemData.removeAt(arrayItemData.size - 1);
            }
            chiefcomplaintRequestArray.clear()
            if (arrayItemData.size > 0) {
                for (i in arrayItemData.indices) {
                    val chiefcomplaintRequestModel = ChiefComplaintRequestModel()
                    chiefcomplaintRequestModel.chief_complaint_duration =
                        arrayItemData.get(i)!!.duration.toString()
                    chiefcomplaintRequestModel.chief_complaint_duration_period_uuid =
                        arrayItemData.get(i)!!.durationPeriodId.toString()
                    chiefcomplaintRequestModel.chief_complaint_uuid =
                        arrayItemData.get(i)!!.chief_complaint_id!!
                    chiefcomplaintRequestModel.consultation_uuid = 1
                    chiefcomplaintRequestModel.encounter_type_uuid = encounter_Type!!
                    chiefcomplaintRequestModel.encounter_uuid = encounter_uuid!!
                    chiefcomplaintRequestModel.patient_uuid = patient_UUID.toString()
                    chiefcomplaintRequestModel.revision = 1
                    chiefcomplaintRequestModel.tat_end_time = "2020-02-28T06:31:37.216Z"
                    chiefcomplaintRequestModel.tat_start_time = "2020-02-28T06:31:37.216Z"

                    chiefcomplaintRequestArray.add(chiefcomplaintRequestModel)
                }
                Log.i("",""+chiefcomplaintRequestArray)

                viewModel!!.InsertCheifComplant(facility_id,chiefcomplaintRequestArray,insertChiefComplaintsRetrofitCallback)

            }
            else
            {
                utils?.showToast(
                    R.color.positiveToast,
                    binding?.mainLayout!!,
                    "Please select any one item"
                )
            }
        }

        binding?.clearCardView?.setOnClickListener({

            chiefComplaintsAdapter.clearall()
            chiefComplaintsAdapter.addRow(FavouritesModel())
            mCallbackChiefFavFragment?.ClearAllData()


        })

        viewModel?.getDuration(getDurationRetrofitCallBack)

        return binding!!.root
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(ChiefComplaintFavouriteFragment(), "Favourite")
        adapter.addFragment(PrevChiefComplaintFragment(), "Prev.chiefComplaint")
        viewPager.offscreenPageLimit = 2
        viewPager.adapter = adapter

    }
    internal inner class ViewPagerAdapter(manager: FragmentManager) :
        FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val mFragmentList = java.util.ArrayList<Fragment>()
        private val mFragmentTitleList = java.util.ArrayList<String>()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }



    private fun setAdapter() {
        chiefComplaintsAdapter =
            ChiefComplaintsAdapter(
                requireActivity(),
                ArrayList()
            )
        binding!!.saveChiefComplaintsRecyclerView.adapter = chiefComplaintsAdapter
        chiefComplaintsAdapter.addRow(FavouritesModel())
        chiefComplaintsAdapter.setOnDeleteClickListener(object :
            ChiefComplaintsAdapter.OnDeleteClickListener {
            override fun onDeleteClick(favouritesModel: FavouritesModel?, position: Int) {
                customdialog = Dialog(requireContext())
                customdialog!! .requestWindowFeature(Window.FEATURE_NO_TITLE)
                customdialog!! .setCancelable(false)
                customdialog!! .setContentView(R.layout.delete_cutsom_layout)
                val closeImageView = customdialog!! .findViewById(R.id.closeImageView) as ImageView
                closeImageView.setOnClickListener {
                    customdialog?.dismiss()
                }
                drugNmae = customdialog!! .findViewById(R.id.addDeleteName) as TextView
                drugNmae.text ="${drugNmae.text.toString()} '"+favouritesModel!!.chief_complaint_name+"' Record ?"
                val yesBtn = customdialog!! .findViewById(R.id.yes) as CardView
                val noBtn = customdialog!! .findViewById(R.id.no) as CardView
                yesBtn.setOnClickListener {

                    chiefComplaintsAdapter.deleteRow(favouritesModel, position)

                    if(favouritesModel.viewChiefcomplaintstatus==1) {

                        mCallbackChiefFavFragment?.ClearFavParticularPosition(favouritesModel.isFavposition)
                    }
                   customdialog!!.dismiss()


                }
                noBtn.setOnClickListener {
                    customdialog!! .dismiss()
                }
                customdialog!! .show()

            }
        })
        chiefComplaintsAdapter.setOnSearchInitiatedListener(object :
            ChiefComplaintsAdapter.OnSearchInitiatedListener {
            override fun onSearchInitiated(
                query: String,
                view: AppCompatAutoCompleteTextView,
                position: Int
            ) {

                selectedSearchPosition = position
                dropdownReferenceView = view
                viewModel?.getComplaintSearchResult(query, getComplaintSearchRetrofitCallBack)

            }
        })
    }



    fun setSearchAdapter(
        dropdownReferenceView: AppCompatAutoCompleteTextView,
        responseContents: ArrayList<ResponseContent>
    ) {

        val responseContentAdapter = ChiefComplaintSearchResultAdapter(
            this.context!!,
            R.layout.row_chief_complaint_search_result,
            responseContents
        )
        dropdownReferenceView.threshold = 1
        dropdownReferenceView.setAdapter(responseContentAdapter)
        dropdownReferenceView.setOnItemClickListener { parent, _, position, id ->

            val selectedPoi = parent.adapter.getItem(position) as ResponseContent?

            dropdownReferenceView.setText(selectedPoi?.name)


        }
    }

    val insertChiefComplaintsRetrofitCallback=object:RetrofitCallback<ChiefComplaintResponse>{
        override fun onSuccessfulResponse(responseBody: Response<ChiefComplaintResponse>?) {
            utils?.showToast(
                R.color.positiveToast,
                binding?.mainLayout!!,
                responseBody?.body()?.message!!
            )
            chiefComplaintsAdapter.clearall()
            chiefComplaintsAdapter.addRow(FavouritesModel())
            mCallbackChiefFavFragment?.ClearAllData()

//            chiefComplaintsAdapter?.addTempleteRow(TempDetails())

            Log.i(":",""+responseBody!!.body().toString())

        }
        override fun onBadRequest(response: Response<ChiefComplaintResponse>) {
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

    val getComplaintSearchRetrofitCallBack =
        object : RetrofitCallback<ComplaintSearchResponseModel> {
            override fun onSuccessfulResponse(response: Response<ComplaintSearchResponseModel>) {
                responseContents = Gson().toJson(response.body()?.responseContents)
                if (response.body()?.responseContents?.isNotEmpty()!!) {
                    chiefComplaintsAdapter.setAdapter(dropdownReferenceView, response.body()?.responseContents!!, selectedSearchPosition)

                }
            }

            override fun onBadRequest(response: Response<ComplaintSearchResponseModel>) {
                val gson = GsonBuilder().create()
                val responseModel: ComplaintSearchResponseModel
                try {

                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        ComplaintSearchResponseModel::class.java
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

    val getDurationRetrofitCallBack =
        object : RetrofitCallback<DurationResponseModel> {
            override fun onSuccessfulResponse(response: Response<DurationResponseModel>) {
                if (response.body()?.responseContents?.isNotEmpty()!!) {
                    chiefComplaintsAdapter.setDuration(response.body()?.responseContents!!)

                }
            }

            override fun onBadRequest(response: Response<DurationResponseModel>) {
                val gson = GsonBuilder().create()
                val responseModel: DurationResponseModel
                try {

                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        DurationResponseModel::class.java
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
    override fun onStart() {
        super.onStart()
        fragmentBackClick!!.setSelectedFragment(this)
    }
         override fun onAttachFragment(childFragment: Fragment) {
             super.onAttachFragment(childFragment)
             if (childFragment is ChiefComplaintFavouriteFragment) {
                 childFragment.setOnTextClickedListener(this)
             }
             if (childFragment is ClearFavParticularPositionListener) {
                 mCallbackChiefFavFragment = childFragment as ClearFavParticularPositionListener
             }
         }


    override fun sendFavAddInChiefFav(favmodel: FavouritesModel?, position: Int, selected: Boolean) {
        favmodel?.viewChiefcomplaintstatus = 1
        favmodel?.isFavposition = position
        if (!selected) {
            chiefComplaintsAdapter.addFavouritesInRow(favmodel)
        } else {
            chiefComplaintsAdapter.deleteRowFromFav(favmodel?.chief_complaint_id!!, position)
        }
    }
}

