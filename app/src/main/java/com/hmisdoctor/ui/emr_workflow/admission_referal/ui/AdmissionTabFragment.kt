package com.hmisdoctor.ui.emr_workflow.admission_referal.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
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
import com.hmisdoctor.databinding.FragmentTabAdmissionBinding
import com.hmisdoctor.ui.emr_workflow.admission_referal.model.AdmissionWardResponseModel
import com.hmisdoctor.ui.emr_workflow.admission_referal.model.AmissionWardResponseContent
import com.hmisdoctor.ui.emr_workflow.admission_referal.view_model.AdmissionViewModel
import com.hmisdoctor.ui.emr_workflow.chief_complaint.view_model.AdmissionViewModelFactory
import com.hmisdoctor.ui.emr_workflow.chief_complaint.view_model.LabViewModelFactory
import com.hmisdoctor.ui.emr_workflow.delete.model.DeleteResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddAllDepatResponseContent
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddAllDepatResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddResponseContent
import com.hmisdoctor.ui.emr_workflow.lab.view_model.LabViewModel
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesResponseModel
import com.hmisdoctor.ui.emr_workflow.radiology.ui.RadiologyFavouriteFragment
import com.hmisdoctor.ui.emr_workflow.radiology.ui.RadiologyTempleteFragment
import com.hmisdoctor.utils.Utils
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class AdmissionTabFragment : Fragment(){

    private var customdialog: Dialog?=null
    private var typeDepartmentList = mutableMapOf<Int,String>()
    private var facility_UUID: Int?  =0
    @SuppressLint("ClickableViewAccessibility")
    var binding : FragmentTabAdmissionBinding ?=null
    private var department_uuid: Int? = null
    private var viewModel: AdmissionViewModel? = null
    private var listAllAddDepartmentItems: List<FavAddAllDepatResponseContent?> = ArrayList()
    private var wardItems: List<AmissionWardResponseContent?> = ArrayList()

    private var favAddResponseMap = mutableMapOf<Int, String>()

    private var wardResponseMap = mutableMapOf<Int, String>()



    private var utils: Utils? = null
    var appPreferences: AppPreferences? = null
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_tab_admission,
                container,
                false
            )

        viewModel = AdmissionViewModelFactory(
            activity!!.application
        ).create(AdmissionViewModel::class.java)
        binding?.viewModel = viewModel
        binding!!.lifecycleOwner = this
        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        facility_UUID = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        department_uuid = appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)

        utils = Utils(requireContext())

        binding?.viewModel?.getAllDepartment(
            facility_UUID,
            AddAllDepartmentCallBack
        )
        binding?.viewModel?.getWArdList(
            facility_UUID,department_uuid!!,WardCallBack

        )
        binding?.calendarEditText?.setOnClickListener {
            val c: Calendar = Calendar.getInstance()
            val mYear = c.get(Calendar.YEAR)
            val mMonth = c.get(Calendar.MONTH)
            val mDay = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                    val c1 = Calendar.getInstance()
                    val mHour = c1[Calendar.HOUR_OF_DAY]
                    val mMinute = c1[Calendar.MINUTE]
                    val mSeconds = c1[Calendar.SECOND]


                            binding?.calendarEditText?.setText(
                                String.format(
                                    "%02d",
                                    dayOfMonth
                                ) + "-" + String.format("%02d", monthOfYear + 1) + "-" + year )
                }, mYear, mMonth, mDay
            )
            //datePickerDialog?.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.show()
        }
        binding?.timerEditText?.setOnClickListener{
            val c1 = Calendar.getInstance()
            val mHour = c1[Calendar.HOUR_OF_DAY]
            val mMinute = c1[Calendar.MINUTE]
            val mSeconds = c1[Calendar.SECOND]

            val timePickerDialog = TimePickerDialog(
                this!!.activity,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->

                    binding?.timerEditText?.setText(
                        String.format(
                            "%02d",
                            hourOfDay)+":"+ String.format(
                            "%02d",
                            minute)+":"+String.format(
                            "%02d",mSeconds)
                    )

                },
                mHour,
                mMinute,
                false
            )
            timePickerDialog.show()

        }




        return binding!!.root
    }
    val AddAllDepartmentCallBack =
        object : RetrofitCallback<FavAddAllDepatResponseModel> {
            @SuppressLint("NewApi")
            override fun onSuccessfulResponse(responseBody: Response<FavAddAllDepatResponseModel>?) {
                Log.i("", "" + responseBody?.body()?.responseContents);
                listAllAddDepartmentItems = responseBody?.body()?.responseContents!!
                favAddResponseMap =
                    listAllAddDepartmentItems.map { it?.uuid!! to it.name }.toMap().toMutableMap()
                val adapter =
                    ArrayAdapter<String>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        favAddResponseMap.values.toMutableList()
                    )

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding?.deparment!!.adapter = adapter

            }

            override fun onBadRequest(errorBody: Response<FavAddAllDepatResponseModel>?) {
                val gson = GsonBuilder().create()
                val responseModel: FavAddAllDepatResponseModel
                try {
                    responseModel = gson.fromJson(
                        errorBody?.errorBody()!!.string(),
                        FavAddAllDepatResponseModel::class.java
                    )
                    utils?.showToast(
                        R.color.negativeToast,
                        binding?.mainLayout!!,
                        ""
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

            override fun onServerError(response: Response<*>?) {
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
    val WardCallBack =
        object : RetrofitCallback<AdmissionWardResponseModel> {
            @SuppressLint("NewApi")
            override fun onSuccessfulResponse(responseBody: Response<AdmissionWardResponseModel>?) {
                Log.i("", "" + responseBody?.body()?.responseContents);
                wardItems = responseBody?.body()?.responseContents!!
                wardResponseMap =
                    wardItems.map { it?.ward_uuid!! to it.ward_name }.toMap().toMutableMap()
                val adapter =
                    ArrayAdapter<String>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        wardResponseMap.values.toMutableList()
                    )

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding?.wardSpinner!!.adapter = adapter
            }

            override fun onBadRequest(errorBody: Response<AdmissionWardResponseModel>?) {
                val gson = GsonBuilder().create()
                val responseModel: AdmissionWardResponseModel
                try {
                    responseModel = gson.fromJson(
                        errorBody?.errorBody()!!.string(),
                        AdmissionWardResponseModel::class.java
                    )
                    utils?.showToast(
                        R.color.negativeToast,
                        binding?.mainLayout!!,
                        ""
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

            override fun onServerError(response: Response<*>?) {
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