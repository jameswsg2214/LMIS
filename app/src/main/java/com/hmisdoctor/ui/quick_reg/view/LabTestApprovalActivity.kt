package com.hmisdoctor.ui.quick_reg.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.PaginationScrollListener
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.ActivityLabApprovalTestBinding
import com.hmisdoctor.ui.quick_reg.model.labapprovalresult.LabApprovalResultResponse
import com.hmisdoctor.ui.quick_reg.model.labtest.request.LabTestApprovalRequestModel
import com.hmisdoctor.ui.quick_reg.model.labtest.request.LabrapidSaveRequestModel
import com.hmisdoctor.ui.quick_reg.model.labtest.request.SendIdList
import com.hmisdoctor.ui.quick_reg.model.labtest.response.*
import com.hmisdoctor.ui.quick_reg.view_model.LabTestApprovalViewModel
import com.hmisdoctor.ui.quick_reg.view_model.LabTestApprovalViewModelFactory
import com.hmisdoctor.utils.Utils
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class LabTestApprovalActivity : AppCompatActivity(),TestApprovalResultDialogFragment.DialogListener,RejectDialogFragment.DialogListener {
    var binding: ActivityLabApprovalTestBinding? = null
    var utils: Utils? = null
    private var selectTestitemUuid: String?=""
    private var selectAssignitemUuid: String?=""
    private var viewModel: LabTestApprovalViewModel? = null
    var appPreferences: AppPreferences? = null
    private var mAdapter: LabTestApprovalAdapter? = null
    private var endDate:String=""
    private  var startDate:String=""
    var linearLayoutManager: LinearLayoutManager? = null
    private var listfilteritem: ArrayList<LabTestSpinnerresponseContent?>? = ArrayList()
    private var listfilteritemAssignSpinner: ArrayList<LabAssignedToresponseContent?>? = ArrayList()
    private var FilterTestNameResponseMap = mutableMapOf<Int, String>()
    private var FilterAssignSpinnereResponseMap = mutableMapOf<Int, String>()
    /////Pagination

    private var currentPage = 0
    private var pageSize = 10
    private var isLoading = false
    private var isLastPage = false
    private var TOTAL_PAGES: Int = 0
    var checktestspinner = 0
    var checkassignedToSpinner = 0
    private var mYear: Int? = null
    private var mMonth: Int? = null
    private var mDay: Int? = null
    private var fromDate : String = ""
    private var toDate : String = ""
    private var fromDateRev : String = ""
    private var toDateRev : String = ""
    private var pinOrMobile : String = ""
    private var orderNumber : String = ""

    var cal = Calendar.getInstance()

    var ACCEPTEDUUId:Int=10

    var APPROVEDUUId:Int=7

    var EXECUTEDUUId:Int=13
    var APPROVELPENDINGAUTH:Int=1

    var REJECTEDUUId:Int=2

    var SAMPLE_RECEIVE:Int=14
    var SAMPLE_TRANSPORTUUId:Int=16

    var SENDFORAPPROVALUUId:Int=19

    private var facility_id : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lab_approval_test)
        viewModel = LabTestApprovalViewModelFactory(
            application

        ).create(LabTestApprovalViewModel::class.java)
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
        binding?.labTestApprpovalrecycleview!!.layoutManager = linearLayoutManager
        mAdapter = LabTestApprovalAdapter(this, ArrayList())
        binding?.labTestApprpovalrecycleview!!.adapter = mAdapter
        appPreferences = AppPreferences.getInstance(application, AppConstants.SHARE_PREFERENCE_NAME)
        facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)!!
        viewModel?.getTextMethod1(facility_id!!,getTestMethdCallBack1)


        /////////////Pagination scrollview

        binding?.labTestApprpovalrecycleview?.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager){

            override fun loadMoreItems() {
                println("bindingdsafgsethry = ${binding}")
                currentPage += 1
                if (currentPage <= TOTAL_PAGES) {

                    labListSeacondAPI(pageSize,currentPage)

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
                    selectTestitemUuid =""
                }
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    pos: Int,
                    id: Long
                ) {
                    if(++checktestspinner > 1) {
                        currentPage =0
                        val itemValue = parent!!.getItemAtPosition(pos).toString()
                        if(pos==0){

                            selectTestitemUuid = ""
                        }
                        else{
                            selectTestitemUuid = FilterTestNameResponseMap.filterValues { it == itemValue }.keys.toList()[0]?.toString()
                            Log.e("selectId",selectTestitemUuid.toString())
                        }
                    }


                }
            }

        binding?.assignedToSpinner!!.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    val itemValue = parent!!.getItemAtPosition(0).toString()
                    selectAssignitemUuid =""
                }
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    pos: Int,
                    id: Long
                ) {
                    if(++checkassignedToSpinner > 1) {
                        currentPage =0
                        val itemValue = parent!!.getItemAtPosition(pos).toString()
                        if(pos==0){

                            selectAssignitemUuid = ""
                        }
                        else{
                            selectAssignitemUuid = FilterAssignSpinnereResponseMap.filterValues { it == itemValue }.keys.toList()[0]?.toString()
                            Log.e("selectId",selectAssignitemUuid.toString())
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

                    Toast.makeText(this,"Select To Date", Toast.LENGTH_LONG).show()
                    fromDate = String.format(
                        "%02d",
                        dayOfMonth
                    )+"-"+String.format("%02d", monthOfYear + 1)+"-"+year

                    fromDateRev = year.toString()+"-"+String.format("%02d", monthOfYear + 1)+"-"+String.format(
                        "%02d",
                        dayOfMonth
                    )

                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val dateoickDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year1, month1, dayOfMonth1 ->

                        toDate = String.format(
                            "%02d",
                            dayOfMonth1
                        )+"-"+String.format("%02d", month1 + 1)+"-"+year1

                        toDateRev = year1.toString()+"-"+String.format("%02d", month1 + 1)+"-"+String.format(
                            "%02d",
                            dayOfMonth1
                        )

                        binding?.calendarEditText!!.setText(fromDate+"-"+toDate)

                    },mYear!!, mMonth!!, mDay!!
                    )

                    dateoickDialog.datePicker.maxDate= Calendar.getInstance().timeInMillis

                    dateoickDialog.datePicker.minDate=cal.timeInMillis

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
                binding?.drawerLayout!!.closeDrawer(GravityCompat.END)

            pinOrMobile = binding?.searchUsingMobileNo!!.text.trim().toString()
            orderNumber = binding?.searchOrderNumber!!.text.trim().toString()

            mAdapter!!.clearAll()

            pageSize=10

            currentPage=0

            labTestApprovalListAPI(pageSize, currentPage)



        }


        binding!!.result.setOnClickListener {

            val request: com.hmisdoctor.ui.quick_reg.model.labapprovalresult.Req = com.hmisdoctor.ui.quick_reg.model.labapprovalresult.Req()

            val datas=mAdapter!!.getSelectedCheckData()

            var OrderList: ArrayList<com.hmisdoctor.ui.quick_reg.model.labapprovalresult.OrderProcessDetail> = ArrayList()

            OrderList.clear()

            var status:Boolean=true

            if(datas!!.size!=0){

                for(i in datas!!.indices){

                    if(datas[i]!!.order_status_uuid==7 || datas[i]!!.order_status_uuid==SENDFORAPPROVALUUId ) {

                        var order: com.hmisdoctor.ui.quick_reg.model.labapprovalresult.OrderProcessDetail = com.hmisdoctor.ui.quick_reg.model.labapprovalresult.OrderProcessDetail()

                        order.Id= datas[i]!!.uuid!!

                        order.order_status_uuid=datas[i]!!.order_status_uuid!!

                        order.to_location_uuid=datas[i]!!.to_location_uuid!!

                        if(order.auth_status_uuid!=null) {

                            order.auth_status_uuid = datas[i]!!.auth_status_uuid!!

                        }
                        OrderList.add(order)

                    }
                    else{

                        status=false

                    }
                }

                if(status){

                    request.OrderProcessDetails=OrderList

                    viewModel!!.orderDetailsGet(request,orderDetailsRetrofitCallback)

                }
                else{

                    Toast.makeText(this,"Cannot process order",Toast.LENGTH_SHORT).show()
/*

                    val ft = supportFragmentManager.beginTransaction()
                    val dialog = TestApprovalResultDialogFragment()
                    dialog.show(ft, "Tag")
*/

                }

            }
            else{

                Toast.makeText(this,"Please Select Any one Item",Toast.LENGTH_SHORT).show()

            }





        }


        binding?.clear!!.setOnClickListener {

            clearSearch()

        }
        utils=Utils(this)

        /*
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        startDate = utils!!.getAgeMonth(1)
        endDate = sdf.format(Date())*/

        val sdf = SimpleDateFormat("yyyy-MM-dd")

        val formatter = SimpleDateFormat("dd-MM-yyyy")

        binding?.calendarEditText!!.setText("""${formatter.format(Date())}-${formatter.format(Date())}""")

        startDate = utils!!.getAgedayDifferent(1)+ "T18:30:00.000Z"

        endDate = sdf.format(Date())+"T18:29:59.000Z"

        binding?.rejected?.setOnClickListener{

            val datas=mAdapter!!.getSelectedCheckData()

            var reqest: LabrapidSaveRequestModel = LabrapidSaveRequestModel()

            var detailsArray: ArrayList<SendIdList> = ArrayList()

            detailsArray.clear()

            var status:Boolean=true

            if(datas!!.size!=0){

                for(i in datas!!.indices){


                    if((datas[i]!!.order_status_uuid==7 || datas[i]!!.order_status_uuid==SENDFORAPPROVALUUId ))  {


                        val details: SendIdList = SendIdList()

                    details.Id= datas[i]!!.uuid!!

                    detailsArray.add(details)

                    }
                    else{

                        status=false

                    }
                }

                if(status){

                    val ft = supportFragmentManager.beginTransaction()
                    val dialog = RejectDialogFragment()
                    val bundle = Bundle()

                    bundle.putParcelableArrayList(AppConstants.RESPONSECONTENT, detailsArray)
                    dialog.arguments = bundle
                    dialog.show(ft, "Tag")


                }
                else{

                    Toast.makeText(this,"Cannot process order",Toast.LENGTH_SHORT).show()

                }

            }
            else{

                Toast.makeText(this,"Please Select Any one Item",Toast.LENGTH_SHORT).show()

            }



        }

        binding!!.selectAllCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->

            mAdapter!!.selectAllCheckbox(isChecked)

        }
        labTestApprovalListAPI(pageSize,currentPage)
    }

    private fun labListSeacondAPI(pageSize: Int, currentPage: Int) {

        val labTestApprovalRequestModel = LabTestApprovalRequestModel()

        labTestApprovalRequestModel.pageNo = currentPage
        labTestApprovalRequestModel.paginationSize = pageSize
        labTestApprovalRequestModel.search = ""
        labTestApprovalRequestModel.test_name = selectTestitemUuid
        labTestApprovalRequestModel.to_facility_name = ""
        labTestApprovalRequestModel.from_facility_uuid = selectAssignitemUuid
        labTestApprovalRequestModel.order_number = orderNumber
        labTestApprovalRequestModel.fromDate = startDate
        labTestApprovalRequestModel.toDate = endDate
        labTestApprovalRequestModel.lab_uuid = null
        labTestApprovalRequestModel.sortField = "order_status_uuid"
        labTestApprovalRequestModel.sortOrder = "DESC"
        val arrayList :ArrayList<Int> = ArrayList()
        arrayList.add(19)
        arrayList.add(7)
        arrayList.add(2)
        arrayList.add(9)
        arrayList.add(2)
        labTestApprovalRequestModel.order_status_uuids = arrayList
        labTestApprovalRequestModel.is_approved_required = 1
        labTestApprovalRequestModel.is_requied_test_approval_list = true
     //   labTestApprovalRequestModel.to_location_uuid = null
        labTestApprovalRequestModel.pinOrMobile = pinOrMobile
        labTestApprovalRequestModel.widget_filter = ""
        labTestApprovalRequestModel.qualifier_filter = ""
        labTestApprovalRequestModel.auth_status_uuid = ""

        val labtest2 = Gson().toJson(labTestApprovalRequestModel)

        Log.i("",""+labtest2)

        viewModel?.getLabTestApprovalListSecond(labTestApprovalRequestModel, labTestApprovalResponseSecondRetrofitCallback)


    }


    private fun labTestApprovalListAPI(pageSize: Int, currentPage: Int) {

        val labTestApprovalRequestModel = LabTestApprovalRequestModel()

        labTestApprovalRequestModel.pageNo = currentPage
        labTestApprovalRequestModel.paginationSize = pageSize
        labTestApprovalRequestModel.search = ""
        labTestApprovalRequestModel.test_name = selectTestitemUuid
        labTestApprovalRequestModel.to_facility_name = ""
        labTestApprovalRequestModel.from_facility_uuid = selectAssignitemUuid
        labTestApprovalRequestModel.order_number = orderNumber
        labTestApprovalRequestModel.fromDate = startDate
        labTestApprovalRequestModel.toDate = endDate
        labTestApprovalRequestModel.lab_uuid = null
        labTestApprovalRequestModel.sortField = "order_status_uuid"
        labTestApprovalRequestModel.sortOrder = "DESC"
        val arrayList :ArrayList<Int> = ArrayList()
        arrayList.add(19)
        arrayList.add(7)
        arrayList.add(2)
        arrayList.add(9)
        arrayList.add(2)
        labTestApprovalRequestModel.order_status_uuids = arrayList
        labTestApprovalRequestModel.is_approved_required = 1
        labTestApprovalRequestModel.is_requied_test_approval_list = true
  //      labTestApprovalRequestModel.to_location_uuid = null
        labTestApprovalRequestModel.pinOrMobile = pinOrMobile
        labTestApprovalRequestModel.widget_filter = ""
        labTestApprovalRequestModel.qualifier_filter = ""
        labTestApprovalRequestModel.auth_status_uuid = ""

        var labtest1 = Gson().toJson(labTestApprovalRequestModel)

        Log.i("",""+labtest1)
        Log.i("",""+labtest1)
        Log.i("",""+labtest1)
        Log.i("",""+labtest1)


        viewModel?.getLabTestApprovalList(labTestApprovalRequestModel, labTestApprovalResponseRetrofitCallback)

    }
    val labTestApprovalResponseRetrofitCallback = object  :
        RetrofitCallback<LabTestApprovalResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<LabTestApprovalResponseModel>?) {
            Log.e("labTestResponse",responseBody?.body()?.responseContents.toString())

            var responsedata = Gson().toJson(responseBody?.body()?.responseContents)

            Log.i("",""+responsedata)
            Log.i("",""+responsedata)
            Log.i("",""+responsedata)
            Log.i("",""+responsedata)



            binding?.positiveTxt!!.setText("0")

            binding?.negativeTxt!!.setText("0")

            binding?.equivocalTxt!!.setText("0")

            binding?.rejectedTxt!!.setText("0")

            if (responseBody!!.body()?.responseContents?.isNotEmpty()!!) {


                Log.i("page",""+currentPage+" "+responseBody?.body()?.responseContents!!.size)
                TOTAL_PAGES = Math.ceil(responseBody!!.body()!!.totalRecords!!.toDouble() / 10).toInt()

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
                for (i in diseaseList!!.indices){

                    if(diseaseList.size != 0 && diseaseList[i]?.qualifier_uuid == 2){
                        binding?.positiveTxt!!.setText(diseaseList[i]?.qualifier_count.toString())
                    }else if(diseaseList.size != 0 && diseaseList[i]?.qualifier_uuid == 1){
                        binding?.negativeTxt!!.setText(diseaseList[i]?.qualifier_count.toString())
                    }else if(diseaseList.size != 0 && diseaseList[i]?.qualifier_uuid == 3){
                        binding?.equivocalTxt!!.setText(diseaseList[i]?.qualifier_count.toString())
                    }
                }

                val orderList = responseBody?.body()?.order_status_count

                if(orderList?.size!=0){

                    for (i in orderList!!.indices){

                        if(orderList[i]?.order_status_uuid==2){

                            binding?.rejectedTxt!!.setText(orderList[i]?.order_count.toString())

                        }

                    }

                }

            }else{
                Toast.makeText(this@LabTestApprovalActivity,"No records found",Toast.LENGTH_LONG).show()
                binding?.progressbar!!.setVisibility(View.GONE);

                binding?.positiveTxt!!.setText("0")

                binding?.negativeTxt!!.setText("0")

                binding?.equivocalTxt!!.setText("0")

                binding?.rejectedTxt!!.setText("0")
                mAdapter!!.clearAll()
            }

        }
        override fun onBadRequest(errorBody: Response<LabTestApprovalResponseModel>?) {
            val gson = GsonBuilder().create()
            val responseModel: LabTestApprovalResponseModel
            try {
                responseModel = gson.fromJson(
                    errorBody!!.errorBody()!!.string(),
                    LabTestApprovalResponseModel::class.java
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


    val labTestApprovalResponseSecondRetrofitCallback = object : RetrofitCallback<LabTestApprovalResponseModel> {
        override fun onSuccessfulResponse(response: Response<LabTestApprovalResponseModel>) {
            if (response.body()?.responseContents!!.isNotEmpty()!!) {

                mAdapter!!.removeLoadingFooter()
                isLoading = false
                mAdapter?.addAll(response.body()!!.responseContents)
                Log.i("page",""+currentPage+" "+response?.body()?.responseContents!!.size)
                println("testing for two  = $currentPage--$TOTAL_PAGES")
                binding?.progressbar!!.setVisibility(View.GONE);

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
                println("testing for six  = $currentPage--$TOTAL_PAGES")
                binding?.progressbar!!.setVisibility(View.GONE);
                mAdapter?.removeLoadingFooter()
                isLoading = false
                isLastPage = true
            }


        }

        override fun onBadRequest(response: Response<LabTestApprovalResponseModel>?) {
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
            viewModel!!.progress.value = 8
        }
    }


    val getTestMethdCallBack1 =
        object : RetrofitCallback<LabTestSpinnerResponseModel> {

            override fun onSuccessfulResponse(response: Response<LabTestSpinnerResponseModel>) {
                Log.i("",""+response.body()?.responseContents)

                listfilteritem?.add(LabTestSpinnerresponseContent())
                listfilteritem?.addAll(response.body()?.responseContents!!)

                FilterTestNameResponseMap =
                    listfilteritem!!.map { it?.uuid!! to it.name!! }!!.toMap().toMutableMap()

                try
                {
                    val adapter =
                        ArrayAdapter<String>(
                            this@LabTestApprovalActivity,
                            android.R.layout.simple_spinner_item,
                            FilterTestNameResponseMap.values.toMutableList()
                        )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding?.testSpinner!!.adapter = adapter
                }catch (e:Exception)
                {

                }
                binding?.testSpinner?.prompt = listfilteritem?.get(0)?.name
                binding?.testSpinner?.setSelection(0)

                viewModel?.getTextAssignedTo(facility_id,LabAssignedSpinnerRetrofitCallback)
            }

            override fun onBadRequest(response: Response<LabTestSpinnerResponseModel>) {
                val gson = GsonBuilder().create()
                val responseModel: LabTestSpinnerResponseModel
                try {
                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        LabTestSpinnerResponseModel::class.java
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


    val LabAssignedSpinnerRetrofitCallback = object : RetrofitCallback<LabAssignedToResponseModel>{
        override fun onSuccessfulResponse(responseBody: Response<LabAssignedToResponseModel>?) {

            Log.e("AssignedToSpinner",responseBody?.body()?.responseContents.toString())

            listfilteritemAssignSpinner?.add(LabAssignedToresponseContent())
            listfilteritemAssignSpinner?.addAll(responseBody!!.body()?.responseContents!!)

            FilterAssignSpinnereResponseMap =
                listfilteritemAssignSpinner!!.map { it?.uuid!! to it.name!! }!!.toMap().toMutableMap()

            try
            {
                val adapter =
                    ArrayAdapter<String>(
                        this@LabTestApprovalActivity,
                        android.R.layout.simple_spinner_item,
                        FilterAssignSpinnereResponseMap.values.toMutableList()
                    )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding?.assignedToSpinner!!.adapter = adapter
            }catch (e:Exception)
            {

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

    val orderDetailsRetrofitCallback = object  : RetrofitCallback<LabApprovalResultResponse>{
        override fun onSuccessfulResponse(responseBody: Response<LabApprovalResultResponse>?) {

            //  labListAPI()

            Log.i("order","Save"+responseBody!!.body()!!.responseContents.rows)

            val ft = supportFragmentManager.beginTransaction()

            val dialog = TestApprovalResultDialogFragment()

            val bundle = Bundle()

            val saveArray=responseBody!!.body()!!.responseContents.rows

            bundle.putParcelableArrayList(AppConstants.RESPONSECONTENT, saveArray)

            dialog.arguments = bundle

            dialog.show(ft, "Tag")

        }

        override fun onBadRequest(errorBody: Response<LabApprovalResultResponse>?) {
            val gson = GsonBuilder().create()
            val responseModel: LabApprovalResultResponse
            try {
                responseModel = gson.fromJson(
                    errorBody!!.errorBody()!!.string(),
                    LabApprovalResultResponse::class.java
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

    private fun clearSearch(){

        binding?.calendarEditText!!.setText("")
        binding?.searchUsingMobileNo!!.setText("")
        binding?.searchOrderNumber!!.setText("")
        binding?.assignedToSpinner?.setSelection(0)
        binding?.testSpinner?.setSelection(0)

    }

    override fun onOrderApprovel() {

        mAdapter!!.clearAll()

        pageSize=10

        currentPage=0

        labTestApprovalListAPI(10,0)

    }

    override fun onRejectDialogTolabtestactivity() {

        Toast.makeText(this,"Rejected Successfully",Toast.LENGTH_LONG).show()

        mAdapter!!.clearAll()

        pageSize=10

        currentPage=0

        labTestApprovalListAPI(10,0)
    }



}





