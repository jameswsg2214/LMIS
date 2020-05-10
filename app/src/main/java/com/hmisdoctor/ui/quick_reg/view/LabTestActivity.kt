package com.hmisdoctor.ui.quick_reg.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder

import com.hmisdoctor.R
import com.hmisdoctor.callbacks.PaginationScrollListener
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.ActivityLabTestBinding
import com.hmisdoctor.ui.emr_workflow.radiology.model.RecyclerDto
import com.hmisdoctor.ui.quick_reg.model.ResponseTestMethod
import com.hmisdoctor.ui.quick_reg.model.ResponseTestMethodContent
import com.hmisdoctor.ui.quick_reg.model.SampleAcceptanceResponseModel
import com.hmisdoctor.ui.quick_reg.model.labtest.request.*
import com.hmisdoctor.ui.quick_reg.model.labtest.request.orderRequest.OrderProcessDetail
import com.hmisdoctor.ui.quick_reg.model.labtest.request.orderRequest.OrderProcessDetailsResponseModel
import com.hmisdoctor.ui.quick_reg.model.labtest.request.orderRequest.Req
import com.hmisdoctor.ui.quick_reg.model.labtest.response.LabAssignedToResponseModel
import com.hmisdoctor.ui.quick_reg.model.labtest.response.LabAssignedToresponseContent
import com.hmisdoctor.ui.quick_reg.model.labtest.response.LabTestResponseModel
import com.hmisdoctor.ui.quick_reg.model.labtest.response.SimpleResponseModel
import com.hmisdoctor.ui.quick_reg.view_model.LabTestViewModel
import com.hmisdoctor.ui.quick_reg.view_model.LabTestViewModelFactory
import com.hmisdoctor.utils.CustomProgressDialog

import com.hmisdoctor.utils.Utils
import kotlinx.android.synthetic.main.activity_lab_test.view.*
import kotlinx.android.synthetic.main.navigation_layout.*
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

import kotlin.collections.ArrayList
import kotlin.text.clear

class LabTestActivity : AppCompatActivity(), OrderProcessDialogFragment.DialogListener,
    RejectDialogFragment.DialogListener, SendForApprovalDialogFragment.DialogListener,
    AssignToOtherDialogFragment.DialogListener {
    private var selectTestitemUuid: String? = ""
    private var selectAssignitemUuid: String? = ""
    private var details: Detail? = Detail()
    var binding: ActivityLabTestBinding? = null
    var utils: Utils? = null
    private var viewModel: LabTestViewModel? = null
    var appPreferences: AppPreferences? = null
    private val labTestList: MutableList<RecyclerDto> = ArrayList()
    private var mAdapter: LabTestAdapter? = null
    var linearLayoutManager: LinearLayoutManager? = null
    private var endDate: String = ""
    private var listfilteritem: ArrayList<ResponseTestMethodContent?>? = ArrayList()
    private var listfilteritemAssignSpinner: ArrayList<LabAssignedToresponseContent?>? = ArrayList()
    private var FilterTestNameResponseMap = mutableMapOf<Int, String>()
    private var FilterAssignSpinnereResponseMap = mutableMapOf<Int, String>()
    private var startDate: String = ""
    var checktestspinner = 0
    var checkassignedToSpinner = 0
    private var mYear: Int? = null
    private var mMonth: Int? = null
    private var mDay: Int? = null
    private var fromDate: String = ""
    private var toDate: String = ""

    private var pinOrMobile: String = ""
    private var orderNumber: String = ""

    var ACCEPTEDUUId: Int = 10


    var APPROVEDUUId: Int = 7


    var EXECUTEDUUId: Int = 13

    var REJECTEDUUId: Int = 2

    var SAMPLE_RECEIVE: Int = 14
    var SAMPLE_TRANSPORTUUId: Int = 16
    var SENDFORAPPROVALUUId: Int = 19

    private var fromDateRev: String = ""
    private var toDateRev: String = ""

    private var facility_id: Int = 0

    /////Pagination

    private var currentPage = 0
    private var pageSize = 10
    private var isLoading = false
    private var isLastPage = false
    private var TOTAL_PAGES: Int = 0

    var cal = Calendar.getInstance()

    //private var customProgressDialog: CustomProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lab_test)
        viewModel = LabTestViewModelFactory(
            application
        ).create(LabTestViewModel::class.java)
        binding?.viewModel = viewModel

        binding?.lifecycleOwner = this

        binding?.searchDrawerCardView?.setOnClickListener {
            binding?.drawerLayout!!.openDrawer(GravityCompat.END)

        }
        binding?.drawerLayout?.drawerElevation = 0f
        binding?.drawerLayout?.setScrimColor(
            ContextCompat.getColor(
                this,
                android.R.color.transparent
            )
        )
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding?.labTestrecycleview!!.layoutManager = linearLayoutManager
        mAdapter = LabTestAdapter(this, ArrayList())
        binding?.labTestrecycleview!!.adapter = mAdapter

        appPreferences = AppPreferences.getInstance(application, AppConstants.SHARE_PREFERENCE_NAME)

        facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)!!
        utils = Utils(this)

        //customProgressDialog = CustomProgressDialog(this)
        /////////////Pagination scrollview

        /*
     TestMethod Spinner
      */

        viewModel?.getTextMethod1(facility_id!!, getTestMethdCallBack1)

        binding?.labTestrecycleview?.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {

            override fun loadMoreItems() {
                println("bindingdsafgsethry = ${binding}")
                currentPage += 1
                if (currentPage <= TOTAL_PAGES) {

                    labListSeacondAPI(pageSize, currentPage)

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

        binding?.testSpinner!!.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    val itemValue = parent!!.getItemAtPosition(0).toString()
                    selectTestitemUuid = ""
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    pos: Int,
                    id: Long
                ) {
                    if (++checktestspinner > 1) {
                        currentPage = 0
                        val itemValue = parent!!.getItemAtPosition(pos).toString()
                        if (pos == 0) {

                            selectTestitemUuid = ""
                        } else {
                            selectTestitemUuid =
                                FilterTestNameResponseMap.filterValues { it == itemValue }.keys.toList()[0]?.toString()

                        }

                    }

//                     labListAPI(pageSize,currentPage)
                }
            }

        binding?.assignedToSpinner!!.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    val itemValue = parent!!.getItemAtPosition(0).toString()
                    selectAssignitemUuid = ""
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    pos: Int,
                    id: Long
                ) {
                    if (++checkassignedToSpinner > 1) {
                        currentPage = 0
                        val itemValue = parent!!.getItemAtPosition(pos).toString()
                        if (pos == 0) {

                            selectAssignitemUuid = ""
                        } else {
                            selectAssignitemUuid =
                                FilterAssignSpinnereResponseMap.filterValues { it == itemValue }.keys.toList()[0]?.toString()
                            Log.e("selectId", selectAssignitemUuid.toString())
                        }

                    }

                }
            }

        binding?.calendarEditText!!.setOnClickListener {

            Toast.makeText(this, "Select Start Date", Toast.LENGTH_LONG).show()
            val c: Calendar = Calendar.getInstance()
            mYear = c.get(Calendar.YEAR)
            mMonth = c.get(Calendar.MONTH)
            mDay = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                    Toast.makeText(this, "Select End Date", Toast.LENGTH_LONG).show()

                    fromDate = String.format(
                        "%02d",
                        dayOfMonth
                    ) + "-" + String.format("%02d", monthOfYear + 1) + "-" + year

                    fromDateRev = year.toString() + "-" + String.format(
                        "%02d",
                        monthOfYear + 1
                    ) + "-" + String.format(
                        "%02d",
                        dayOfMonth
                    )

                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)


                    val dateoickDialog = DatePickerDialog(
                        this,
                        DatePickerDialog.OnDateSetListener { view, year1, month1, dayOfMonth1 ->

                            toDate = String.format(
                                "%02d",
                                dayOfMonth1
                            ) + "-" + String.format("%02d", month1 + 1) + "-" + year1

                            toDateRev = year1.toString() + "-" + String.format(
                                "%02d",
                                month1 + 1
                            ) + "-" + String.format(
                                "%02d",
                                dayOfMonth1
                            )

                            binding?.calendarEditText!!.setText(fromDate + "-" + toDate)

                        },
                        mYear!!,
                        mMonth!!,
                        mDay!!
                    )

                    dateoickDialog.datePicker.maxDate = Calendar.getInstance().timeInMillis

                    dateoickDialog.datePicker.minDate = cal.timeInMillis

                    dateoickDialog.show()


                }, mYear!!, mMonth!!, mDay!!
            )

            datePickerDialog.datePicker.maxDate = Calendar.getInstance().timeInMillis

            datePickerDialog.show()

        }

        binding?.searchButton!!.setOnClickListener {

            if (!binding?.calendarEditText!!.text.trim().toString().isEmpty()) {

                startDate = fromDateRev + "T00:01:00.000Z"
                endDate = toDateRev + "T23:59:59.000Z"
            }

            pinOrMobile = binding?.searchUsingMobileNo!!.text.trim().toString()
            orderNumber = binding?.searchOrderNumber!!.text.trim().toString()

            binding?.drawerLayout!!.closeDrawer(GravityCompat.END)

            mAdapter!!.clearAll()

            pageSize = 10

            currentPage = 0

            labListAPI(pageSize, currentPage)
        }

        binding?.rejected?.setOnClickListener {

            val datas = mAdapter!!.getSelectedCheckData()

            var reqest: LabrapidSaveRequestModel = LabrapidSaveRequestModel()

            var detailsArray: ArrayList<SendIdList> = ArrayList()

            detailsArray.clear()

            var status: Boolean = true

            if (datas!!.size != 0) {

                for (i in datas!!.indices) {

                    if ((datas[i]!!.order_status_uuid == ACCEPTEDUUId || datas[i]!!.order_status_uuid == EXECUTEDUUId) || datas[i]!!.order_status_uuid == SENDFORAPPROVALUUId) {

                        val details: SendIdList = SendIdList()

                        details.Id = datas[i]!!.uuid!!

                        detailsArray.add(details)

                    } else {

                        status = false

                    }
                }

                if (status) {

                    val ft = supportFragmentManager.beginTransaction()
                    val dialog = RejectDialogFragment()
                    val bundle = Bundle()

                    bundle.putParcelableArrayList(AppConstants.RESPONSECONTENT, detailsArray)
                    dialog.arguments = bundle
                    dialog.show(ft, "Tag")


                } else {

                    Toast.makeText(this, "Cannot process order", Toast.LENGTH_SHORT).show()

                }

            } else {

                Toast.makeText(this, "Please Select Any one Item", Toast.LENGTH_SHORT).show()

            }


        }


        binding?.assignOthers?.setOnClickListener {


            val datas = mAdapter!!.getSelectedCheckData()

            var reqest: LabrapidSaveRequestModel = LabrapidSaveRequestModel()

            var detailsArray: ArrayList<SendIdList> = ArrayList()

            detailsArray.clear()

            var status: Boolean = true

            if (datas!!.size != 0) {

                for (i in datas!!.indices) {

                    if (datas[i]!!.test_method_uuid == 1 && datas[i]!!.order_status_uuid == ACCEPTEDUUId) {

                        val details: SendIdList = SendIdList()

                        details.Id = datas[i]!!.uuid!!

                        details.name = datas[i]!!.test_name!!

                        detailsArray.add(details)

                    } else {

                        status = false

                    }
                }

                if (status) {


                    val ft = supportFragmentManager.beginTransaction()
                    val dialog = AssignToOtherDialogFragment()
                    val bundle = Bundle()

                    bundle.putParcelableArrayList(AppConstants.RESPONSECONTENT, detailsArray)
                    dialog.arguments = bundle
                    dialog.show(ft, "Tag")

                } else {

                    Toast.makeText(this, "Cannot process order", Toast.LENGTH_SHORT).show()

                }

            } else {

                Toast.makeText(this, "Please Select Any one Item", Toast.LENGTH_SHORT).show()

            }

        }

        val sdf = SimpleDateFormat("yyyy-MM-dd")

        val formatter = SimpleDateFormat("dd-MM-yyyy")

        binding?.calendarEditText!!.setText("""${formatter.format(Date())}-${formatter.format(Date())}""")

        startDate = utils!!.getAgedayDifferent(1)+ "T23:59:59.000Z"

        endDate = sdf.format(Date())+"T23:59:59.000Z"

        labListAPI(pageSize, currentPage)

        binding?.clear!!.setOnClickListener {

            clearSearch()

        }

        binding?.sampleAcceptanceBtn!!.setOnClickListener {

            Log.i("select daea", "" + mAdapter!!.getSelectedCheckData())


        }

        binding!!.selectAllCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->

            mAdapter!!.selectAllCheckbox(isChecked)

        }

        binding!!.orderProccess.setOnClickListener {

            val request: Req = Req()

            val datas = mAdapter!!.getSelectedCheckData()

            var OrderList: ArrayList<OrderProcessDetail> = ArrayList()

            OrderList.clear()

            var status: Boolean = true

            if (datas!!.size != 0) {

                for (i in datas!!.indices) {

                    if (datas[i]!!.test_method_uuid == 1 && datas[i]!!.order_status_uuid == ACCEPTEDUUId) {

                        var order: OrderProcessDetail = OrderProcessDetail()

                        order.Id = datas[i]!!.uuid!!

                        order.order_status_uuid = datas[i]!!.order_status_uuid!!

                        order.to_location_uuid = datas[i]!!.to_location_uuid!!

                        OrderList.add(order)

                    } else {

                        status = false

                    }
                }

                if (status) {

                    request.OrderProcessDetails = OrderList

                    viewModel!!.orderDetailsGet(request, orderDetailsRetrofitCallback)

                } else {

                    Toast.makeText(this, "Cannot process order", Toast.LENGTH_SHORT).show()

                }

            } else {

                Toast.makeText(this, "Please Select Any one Item", Toast.LENGTH_SHORT).show()

            }


        }

        binding!!.saveOfApproval.setOnClickListener {

            val datas = mAdapter!!.getSelectedCheckData()

            var reqest: LabrapidSaveRequestModel = LabrapidSaveRequestModel()

            var detailsArray: ArrayList<SendIdList> = ArrayList()

            detailsArray.clear()

            var status: Boolean = true

            if (datas!!.size != 0) {

                for (i in datas!!.indices) {

                    if (datas[i]!!.test_method_uuid == 1 && datas[i]!!.order_status_uuid == EXECUTEDUUId) {

                        val details: SendIdList = SendIdList()

                        details.Id = datas[i]!!.uuid!!

                        detailsArray.add(details)

                    } else {

                        status = false

                    }
                }

                if (status) {

                    val ft = supportFragmentManager.beginTransaction()
                    val dialog = SendForApprovalDialogFragment()
                    val bundle = Bundle()

                    bundle.putParcelableArrayList(AppConstants.RESPONSECONTENT, detailsArray)
                    dialog.arguments = bundle
                    dialog.show(ft, "Tag")


                } else {

                    Toast.makeText(this, "Cannot process order", Toast.LENGTH_SHORT).show()

                }

            } else {

                Toast.makeText(this, "Please Select Any one Item", Toast.LENGTH_SHORT).show()

            }


        }

        binding!!.saveCardView.setOnClickListener {

            val datas = mAdapter!!.getSelectedCheckData()

            var reqest: LabrapidSaveRequestModel = LabrapidSaveRequestModel()

            var detailsArray: ArrayList<DetailX> = ArrayList()

            detailsArray.clear()

            var status: Boolean = true

            if (datas!!.size != 0) {

                for (i in datas!!.indices) {

                    if (datas[i]!!.test_method_uuid == 2) {
                        val details: DetailX = DetailX()

                        details.patient_order_test_detail_uuids = datas[i]!!.uuid!!

                        when (datas[i]!!.radioselectName) {
                            1 -> {

                                details.result_value = "Positive"

                                details.qualifier_uuid = 2

                            }
                            2 -> {

                                details.result_value = "Negative"

                                details.qualifier_uuid = 1


                            }
                            3 -> {

                                details.result_value = "Equivocal"

                                details.qualifier_uuid = 3

                            }
                            else -> {

                                status = false

                                Toast.makeText(this, "Select one Result", Toast.LENGTH_SHORT).show()



                            }
                        }

                        detailsArray.add(details)

                    } else {

                        status = false

                    }
                }

                if (status) {

                    Log.i("Save","Saved")

                    reqest.details = detailsArray

                    viewModel!!.rapidSave(reqest, saveRapidRetrofitCallback)

                } else {

                    Toast.makeText(this, "Cannot process order", Toast.LENGTH_SHORT).show()

                }

            } else {

                Toast.makeText(this, "Please Select Any one Item", Toast.LENGTH_SHORT).show()

            }

        }


    }

    private fun labListSeacondAPI(
        pageSize: Int,
        currentPage: Int
    ) {
        val labTestRequestModel = LabTestRequestModel()
        labTestRequestModel.pageNo = currentPage
        labTestRequestModel.paginationSize = pageSize
        labTestRequestModel.search = ""
        labTestRequestModel.test_name = ""
        labTestRequestModel.to_facility_name = selectAssignitemUuid
        labTestRequestModel.order_number = orderNumber
        labTestRequestModel.test_method_name = selectTestitemUuid
        labTestRequestModel.is_lab_test = true
        labTestRequestModel.fromDate = startDate
        labTestRequestModel.toDate = endDate
        labTestRequestModel.to_facility_uuid = facility_id.toString()
//        labTestRequestModel.to_location_uuid = 0
        labTestRequestModel.pinOrMobile = pinOrMobile

        viewModel?.getLabListSecond(labTestRequestModel, labTestResponseSecondRetrofitCallback)
    }

    private fun labListAPI(
        pageSize: Int,
        currentPage: Int
    ) {

        //customProgressDialog!!.show()
        val labTestRequestModel = LabTestRequestModel()
        labTestRequestModel.pageNo = currentPage
        labTestRequestModel.paginationSize = pageSize
        labTestRequestModel.search = ""
        labTestRequestModel.test_name = ""
        labTestRequestModel.test_method_name = selectTestitemUuid
        labTestRequestModel.to_facility_name = selectAssignitemUuid
        labTestRequestModel.order_number = orderNumber
        labTestRequestModel.is_lab_test = true
        labTestRequestModel.fromDate = startDate
        labTestRequestModel.toDate = endDate
        labTestRequestModel.to_facility_uuid = facility_id.toString()
        //labTestRequestModel.to_location_uuid = 0
        labTestRequestModel.pinOrMobile = pinOrMobile

        viewModel?.getLabList(labTestRequestModel, labTestResponseRetrofitCallback)
    }

    val labTestResponseRetrofitCallback = object : RetrofitCallback<LabTestResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<LabTestResponseModel>?) {
            if (responseBody!!.body()?.responseContents?.isNotEmpty()!!) {
                TOTAL_PAGES =
                    Math.ceil(responseBody!!.body()!!.totalRecords!!.toDouble() / 10).toInt()



                binding?.positiveTxt!!.setText("0")

                binding?.negativeTxt!!.setText("0")

                binding?.equivocalTxt!!.setText("0")

                binding?.rejectedTxt!!.setText("0")


                if (responseBody.body()!!.responseContents!!.isNotEmpty()!!) {
                    mAdapter!!.addAll(responseBody!!.body()!!.responseContents)
                    if (currentPage < TOTAL_PAGES!!) {
                        binding?.progressbar!!.setVisibility(View.VISIBLE);
                        mAdapter!!.addLoadingFooter()
                        isLoading = true
                        isLastPage = false
                    } else {
                        binding?.progressbar!!.setVisibility(View.GONE);
                        mAdapter!!.removeLoadingFooter()
                        isLoading = false
                        isLastPage = true
                    }

                } else {
                    binding?.progressbar!!.setVisibility(View.GONE);
                    mAdapter!!.removeLoadingFooter()
                    isLoading = false
                    isLastPage = true
                }

                val diseaseList = responseBody?.body()?.disease_result_data

                for (i in diseaseList!!.indices) {

                    if (diseaseList.size != 0 && diseaseList[i]?.result_value.equals("Positive")) {
                        binding?.positiveTxt!!.setText(diseaseList[i]?.qualifier_count.toString())
                    } else if (diseaseList.size != 0 && diseaseList[i]?.result_value.equals("Negative")) {
                        binding?.negativeTxt!!.setText(diseaseList[i]?.qualifier_count.toString())
                    } else if (diseaseList.size != 0 && diseaseList[i]?.result_value.equals("Equivocal")) {
                        binding?.equivocalTxt!!.setText(diseaseList[i]?.qualifier_count.toString())
                    }

                }

                val orderList = responseBody?.body()?.order_status_count

                if (orderList?.size != 0) {

                    for (i in orderList!!.indices) {

                        if (orderList[i]?.order_status_uuid == 2) {

                            binding?.rejectedTxt!!.setText(orderList[i]?.order_count.toString())

                        }

                        when {
                            orderList[i]?.order_status_name.equals("ACCEPTED") -> {

                                ACCEPTEDUUId = orderList[i]?.order_status_uuid!!

                            }
                            orderList[i]?.order_status_name.equals("APPROVED") -> {

                                APPROVEDUUId = orderList[i]?.order_status_uuid!!

                            }
                            orderList[i]?.order_status_name.equals("EXECUTED") -> {

                                EXECUTEDUUId = orderList[i]?.order_status_uuid!!

                            }
                            orderList[i]?.order_status_name.equals("REJECTED") -> {

                                REJECTEDUUId = orderList[i]?.order_status_uuid!!

                            }
                            orderList[i]?.order_status_name.equals("SAMPLE RECEIVE") -> {

                                SAMPLE_RECEIVE = orderList[i]?.order_status_uuid!!

                            }
                            orderList[i]?.order_status_name.equals("SAMPLE TRANSPORT") -> {

                                SAMPLE_TRANSPORTUUId = orderList[i]?.order_status_uuid!!

                            }
                            orderList[i]?.order_status_name.equals("SENDFORAPPROVAL") -> {

                                SENDFORAPPROVALUUId = orderList[i]?.order_status_uuid!!

                            }
                        }

                    }

                }


            } else {
                Toast.makeText(this@LabTestActivity, "No records found", Toast.LENGTH_LONG).show()
                binding?.progressbar!!.setVisibility(View.GONE);
                binding?.positiveTxt!!.setText("0")

                binding?.negativeTxt!!.setText("0")

                binding?.equivocalTxt!!.setText("0")

                binding?.rejectedTxt!!.setText("0")

                mAdapter!!.clearAll()
            }

        }

        override fun onBadRequest(errorBody: Response<LabTestResponseModel>?) {
            val gson = GsonBuilder().create()
            val responseModel: LabTestResponseModel
            try {
                responseModel = gson.fromJson(
                    errorBody!!.errorBody()!!.string(),
                    LabTestResponseModel::class.java
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
            binding?.progressbar!!.setVisibility(View.GONE);

        }

    }

    val saveRapidRetrofitCallback = object : RetrofitCallback<SimpleResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<SimpleResponseModel>?) {

            Toast.makeText(this@LabTestActivity,"Save Successfully",Toast.LENGTH_LONG).show()

            mAdapter!!.clearAll()

            pageSize = 10

            currentPage = 0

            labListAPI(10, 0)

        }

        override fun onBadRequest(errorBody: Response<SimpleResponseModel>?) {
            val gson = GsonBuilder().create()
            val responseModel: SimpleResponseModel
            try {
                responseModel = gson.fromJson(
                    errorBody!!.errorBody()!!.string(),
                    SimpleResponseModel::class.java
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


    val orderDetailsRetrofitCallback = object : RetrofitCallback<OrderProcessDetailsResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<OrderProcessDetailsResponseModel>?) {

            Log.i("order", "Save" + responseBody!!.body()!!.responseContents.rows)
            val ft = supportFragmentManager.beginTransaction()
            val dialog = OrderProcessDialogFragment()
            val bundle = Bundle()
            val saveArray = responseBody!!.body()!!.responseContents.rows
            bundle.putParcelableArrayList(AppConstants.RESPONSECONTENT, saveArray)
            dialog.arguments = bundle
            dialog.show(ft, "Tag")

        }

        override fun onBadRequest(errorBody: Response<OrderProcessDetailsResponseModel>?) {
            val gson = GsonBuilder().create()
            val responseModel: OrderProcessDetailsResponseModel
            try {
                responseModel = gson.fromJson(
                    errorBody!!.errorBody()!!.string(),
                    OrderProcessDetailsResponseModel::class.java
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


    val labTestAcceptRetrofitCallback = object : RetrofitCallback<SampleAcceptanceResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<SampleAcceptanceResponseModel>?) {

        }

        override fun onBadRequest(errorBody: Response<SampleAcceptanceResponseModel>?) {
            val gson = GsonBuilder().create()
            val responseModel: SampleAcceptanceResponseModel
            try {
                responseModel = gson.fromJson(
                    errorBody!!.errorBody()!!.string(),
                    SampleAcceptanceResponseModel::class.java
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


    val labTestResponseSecondRetrofitCallback = object : RetrofitCallback<LabTestResponseModel> {
        override fun onSuccessfulResponse(response: Response<LabTestResponseModel>) {
            if (response.body()?.responseContents!!.isNotEmpty()!!) {
                binding?.progressbar!!.setVisibility(View.GONE);
                mAdapter!!.removeLoadingFooter()
                isLoading = false

                mAdapter?.addAll(response.body()!!.responseContents)

                println("testing for two  = $currentPage--$TOTAL_PAGES")

                if (currentPage < TOTAL_PAGES!!) {
                    binding?.progressbar!!.setVisibility(View.VISIBLE);
                    mAdapter?.addLoadingFooter()
                    isLoading = true
                    isLastPage = false
                    println("testing for four  = $currentPage--$TOTAL_PAGES")
                } else {
                    isLastPage = true
                    binding?.progressbar!!.setVisibility(View.GONE);
//                    visitHistoryAdapter.removeLoadingFooter()
                    isLoading = false
                    isLastPage = true
                    println("testing for five  = $currentPage--$TOTAL_PAGES")
                }

            } else {
                binding?.progressbar!!.setVisibility(View.GONE);
                println("testing for six  = $currentPage--$TOTAL_PAGES")
                mAdapter?.removeLoadingFooter()
                isLoading = false
                isLastPage = true
            }
/*
            val diseaseList = response?.body()?.disease_result_data

            for (i in diseaseList!!.indices){

                if(diseaseList.size != 0 && diseaseList[i]?.result_value.equals("Positive")){

                    var datas=(binding?.positiveTxt!!.text.toString().toInt())+ diseaseList[i]?.qualifier_count!!

                    binding?.positiveTxt!!.setText(datas.toString())

                }else if(diseaseList.size != 0 && diseaseList[i]?.result_value.equals("Negative")){

                    var datas=(binding?.negativeTxt!!.text.toString().toInt())+ diseaseList[i]?.qualifier_count!!

                    binding?.negativeTxt!!.setText(datas.toString())

                }else if(diseaseList.size != 0 && diseaseList[i]?.result_value.equals("Equivocal")){

                    var datas=(binding?.equivocalTxt!!.text.toString().toInt())+ diseaseList[i]?.qualifier_count!!

                    binding?.equivocalTxt!!.setText(datas.toString())

                }

            }

            val orderList = response?.body()?.order_status_count

            if(orderList?.size!=0){

                for (i in orderList!!.indices){

                    when {
                        orderList[i]?.order_status_name.equals("ACCEPTED") -> {

                            ACCEPTEDUUId= orderList[i]?.order_status_uuid!!

                        }
                        orderList[i]?.order_status_name.equals("APPROVED") -> {

                            APPROVEDUUId= orderList[i]?.order_status_uuid!!

                        }
                        orderList[i]?.order_status_name.equals("EXECUTED") -> {

                            EXECUTEDUUId= orderList[i]?.order_status_uuid!!

                        }
                        orderList[i]?.order_status_name.equals("REJECTED") -> {

                            REJECTEDUUId= orderList[i]?.order_status_uuid!!

                        }
                        orderList[i]?.order_status_name.equals("SAMPLE RECEIVE") -> {

                            SAMPLE_RECEIVE= orderList[i]?.order_status_uuid!!

                        }
                        orderList[i]?.order_status_name.equals("SAMPLE TRANSPORT") -> {

                            SAMPLE_TRANSPORTUUId= orderList[i]?.order_status_uuid!!

                        }
                        orderList[i]?.order_status_name.equals("SENDFORAPPROVAL") -> {

                            SENDFORAPPROVALUUId= orderList[i]?.order_status_uuid!!

                        }
                    }

                }

            }*/
        }

        override fun onBadRequest(response: Response<LabTestResponseModel>?) {
            mAdapter?.removeLoadingFooter()
            isLoading = false
            isLastPage = true

        }

        override fun onServerError(response: Response<*>) {
            viewModel!!.progress.value = View.GONE
            utils!!.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }

        override fun onUnAuthorized() {
            viewModel!!.progress.value = View.GONE
            utils!!.showToast(
                R.color.negativeToast,
                binding?.mainLayout!!,
                getString(R.string.something_went_wrong)
            )
        }

        override fun onForbidden() {
            viewModel!!.progress.value = View.GONE
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
            binding?.progressbar!!.setVisibility(View.GONE);
        }
    }

    val getTestMethdCallBack1 =
        object : RetrofitCallback<ResponseTestMethod> {
            @SuppressLint("SetTextI18n")
            override fun onSuccessfulResponse(response: Response<ResponseTestMethod>) {
                Log.i("", "" + response.body()?.responseContents)
                Log.i("", "" + response.body()?.req)
                listfilteritem?.add(ResponseTestMethodContent())
                listfilteritem?.addAll((response?.body()?.responseContents)!!)
                FilterTestNameResponseMap =
                    listfilteritem!!.map { it?.uuid!! to it.name!! }!!.toMap().toMutableMap()
                try {
                    val adapter =
                        ArrayAdapter<String>(
                            this@LabTestActivity,
                            android.R.layout.simple_spinner_item,
                            FilterTestNameResponseMap.values.toMutableList()
                        )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding?.testSpinner!!.adapter = adapter
                } catch (e: Exception) {

                }
                binding?.testSpinner?.prompt = listfilteritem?.get(0)?.name
                binding?.testSpinner?.setSelection(0)

                viewModel?.getTextAssignedTo(facility_id, LabAssignedSpinnerRetrofitCallback)
            }

            override fun onBadRequest(response: Response<ResponseTestMethod>) {
                val gson = GsonBuilder().create()
                val responseModel: ResponseTestMethod
                try {
                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        ResponseTestMethod::class.java
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


    val LabAssignedSpinnerRetrofitCallback = object : RetrofitCallback<LabAssignedToResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<LabAssignedToResponseModel>?) {

            Log.e("AssignedToSpinner", responseBody?.body()?.responseContents.toString())

            listfilteritemAssignSpinner?.add(LabAssignedToresponseContent())
            listfilteritemAssignSpinner?.addAll(responseBody!!.body()?.responseContents!!)

            FilterAssignSpinnereResponseMap =
                listfilteritemAssignSpinner!!.map { it?.uuid!! to it.name!! }!!.toMap()
                    .toMutableMap()

            try {
                val adapter =
                    ArrayAdapter<String>(
                        this@LabTestActivity,
                        android.R.layout.simple_spinner_item,
                        FilterAssignSpinnereResponseMap.values.toMutableList()
                    )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding?.assignedToSpinner!!.adapter = adapter
            } catch (e: Exception) {

            }
            binding?.assignedToSpinner?.prompt = listfilteritem?.get(0)?.name
            binding?.assignedToSpinner?.setSelection(0)

        }

        override fun onBadRequest(response: Response<LabAssignedToResponseModel>) {
            val gson = GsonBuilder().create()
            val responseModel: LabAssignedToResponseModel
            try {
                responseModel = gson.fromJson(
                    response.errorBody()!!.string(),
                    LabAssignedToResponseModel::class.java
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


    override fun onOrderProcessDialogTolabtestactivity() {

        Toast.makeText(this@LabTestActivity,"Save Successfully",Toast.LENGTH_LONG).show()

        mAdapter!!.clearAll()

        pageSize = 10

        currentPage = 0

        labListAPI(10, 0)

    }

    override fun onRejectDialogTolabtestactivity() {

        Toast.makeText(this,"Rejected Successfully",Toast.LENGTH_LONG).show()

        mAdapter!!.clearAll()

        pageSize = 10

        currentPage = 0

        labListAPI(10, 0)
    }

    override fun onSendForApprovalTolabtestactivity() {

        mAdapter!!.clearAll()

        pageSize = 10

        currentPage = 0

        labListAPI(10, 0)
    }

    override fun onAssignTolabtestactivity() {
        mAdapter!!.clearAll()

        pageSize = 10

        currentPage = 0

        labListAPI(10, 0)
    }

    private fun clearSearch() {

        binding?.calendarEditText!!.setText("")
        binding?.searchUsingMobileNo!!.setText("")
        binding?.searchOrderNumber!!.setText("")
        binding?.assignedToSpinner?.setSelection(0)
        binding?.testSpinner?.setSelection(0)

    }

}