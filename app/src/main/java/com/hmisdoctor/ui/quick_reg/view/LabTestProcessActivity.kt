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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.PaginationScrollListener
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.ActivityLabTestProcessBinding
import com.hmisdoctor.ui.emr_workflow.radiology.model.RecyclerDto
import com.hmisdoctor.ui.out_patient.search_response_model.SearchResponseModel
import com.hmisdoctor.ui.quick_reg.model.ResponseTestMethod
import com.hmisdoctor.ui.quick_reg.model.ResponseTestMethodContent
import com.hmisdoctor.ui.quick_reg.model.labtest.request.LabrapidSaveRequestModel
import com.hmisdoctor.ui.quick_reg.model.labtest.request.SendIdList
import com.hmisdoctor.ui.quick_reg.model.labtest.request.TestProcessRequestModel
import com.hmisdoctor.ui.quick_reg.model.labtest.response.LabAssignedToResponseModel
import com.hmisdoctor.ui.quick_reg.model.labtest.response.LabAssignedToresponseContent
import com.hmisdoctor.ui.quick_reg.model.labtest.response.LabTestSpinnerResponseModel
import com.hmisdoctor.ui.quick_reg.model.labtest.response.LabTestSpinnerresponseContent
import com.hmisdoctor.ui.quick_reg.model.labtest.request.orderRequest.OrderProcessDetail
import com.hmisdoctor.ui.quick_reg.model.labtest.request.orderRequest.OrderProcessDetailsResponseModel
import com.hmisdoctor.ui.quick_reg.model.labtest.request.orderRequest.Req
import com.hmisdoctor.ui.quick_reg.model.labtest.response.SimpleResponseModel
import com.hmisdoctor.ui.quick_reg.model.labtest.response.testprocess.TestProcessResponseModel
import com.hmisdoctor.ui.quick_reg.model.testprocess.sampleTransportRequestModel
import com.hmisdoctor.ui.quick_reg.view_model.LabTestProcessViewModel
import com.hmisdoctor.ui.quick_reg.view_model.LabTestProcessViewModelFactory
import com.hmisdoctor.utils.Utils
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class LabTestProcessActivity : AppCompatActivity(),RejectDialogFragment.DialogListener,OrderProcessDialogFragment.DialogListener,SendForApprovalDialogFragment.DialogListener, AssignToOtherDialogFragment.DialogListener {
    var binding: ActivityLabTestProcessBinding? = null
    var utils: Utils? = null
    private var endDate:String=""
    private  var startDate:String=""
    private var viewModel: LabTestProcessViewModel? = null
    var appPreferences: AppPreferences? = null
    private var mAdapter: LabTestsProcessAdapter? = null
    private val labTestList: MutableList<RecyclerDto> = ArrayList()
    private var selectAssignitemUuid: String?=""
    private var selectTestitemUuid: String?=""
    var linearLayoutManager: LinearLayoutManager? = null
    private var facility_id : Int = 0
    private var mYear: Int? = null
    private var mMonth: Int? = null
    private var mDay: Int? = null
    private var fromDate : String = ""
    private var toDate : String = ""
    private var fromDateRev : String = ""
    private var toDateRev : String = ""
    var checktestspinner = 0
    var checkassignedToSpinner = 0

    var cal = Calendar.getInstance()

    var ACCEPTEDUUId:Int=10


    var APPROVEDUUId:Int=7

    var EXECUTEDUUId:Int=13

    var REJECTEDUUId:Int=2

    var SAMPLE_RECEIVE:Int=14

    var SAMPLE_TRANSPORTUUId:Int=16

    var APPROVELPENDINGAUTHID:Int=1

    var SENDFORAPPROVALUUId:Int=19

    private var currentPage = 0
    private var pageSize = 10
    private var isLoading = false
    private var isLastPage = false
    private var TOTAL_PAGES: Int = 0

    private var pinOrMobile : String = ""
    private var orderNumber : String = ""

    private var listfilteritem: ArrayList<LabTestSpinnerresponseContent?>? = ArrayList()
    private var listfilteritemAssignSpinner: ArrayList<LabAssignedToresponseContent?>? = ArrayList()
    private var FilterTestNameResponseMap = mutableMapOf<Int, String>()
    private var FilterAssignSpinnereResponseMap = mutableMapOf<Int, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lab_test_process)
        viewModel = LabTestProcessViewModelFactory(
            application

        ).create(LabTestProcessViewModel::class.java)
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
        binding?.labTestProcessrecycleview!!.layoutManager = linearLayoutManager
        mAdapter = LabTestsProcessAdapter(this, ArrayList())
        binding?.labTestProcessrecycleview!!.adapter = mAdapter

        appPreferences = AppPreferences.getInstance(application, AppConstants.SHARE_PREFERENCE_NAME)
         facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)!!
        viewModel?.getTextMethod1(facility_id,getTestMethdCallBack1)


        binding?.result?.setOnClickListener{

            val dates=mAdapter!!.getSelectedCheckData()


            val RequestModel: sampleTransportRequestModel =sampleTransportRequestModel()

            val request:ArrayList<Int> = ArrayList()

            var status:Boolean=true

            if(dates!!.size!=0){

                for(i in dates!!.indices){

                    if(dates[i]!!.test_method_uuid==1 && dates[i]!!.order_status_uuid==SAMPLE_TRANSPORTUUId) {

                        request.add(dates[i]!!.sample_transport_details_uuid!!)
                    }
                    else{

                        status=false

                    }
                }

                if(status){

                    RequestModel.sample_transport_details_uuid_s=request

                    viewModel!!.sampleRecived(RequestModel,sampleRecivedRetrofitCallback)


                    binding?.progressbar!!.setVisibility(View.VISIBLE);


                }
                else{

                    Toast.makeText(this,"Cannot process order",Toast.LENGTH_SHORT).show()

                }

            }
            else{

                Toast.makeText(this,"Please Select Any one Item",Toast.LENGTH_SHORT).show()

            }


        }
        binding?.order?.setOnClickListener{
            val ft = supportFragmentManager.beginTransaction()
            val dialog = TestProcessOrderDialogFragment()
            val bundle = Bundle()
            dialog.show(ft, "Tag")

        }

        binding!!.selectAllCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->

            mAdapter!!.selectAllCheckboxes(isChecked)

        }

        utils=Utils(this)

      /*
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        startDate = utils!!.getAgeMonth(1)
        endDate = sdf.format(Date())*/

        val sdf = SimpleDateFormat("yyyy-MM-dd")

        val formatter = SimpleDateFormat("dd-MM-yyyy")

        binding?.calendarEditText!!.setText("""${formatter.format(Date())}-${formatter.format(Date())}""")

        startDate = utils!!.getAgedayDifferent(1)+ "T23:59:59.000Z"

        endDate = sdf.format(Date())+"T23:59:59.000Z"


        binding?.labTestProcessrecycleview?.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {

            override fun loadMoreItems() {
                println("bindingdsafgsethry = ${binding}")
                currentPage += 1
                if (currentPage <= TOTAL_PAGES) {
                    getTestProcessAPISecond(pageSize,currentPage)
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
                    if(++checktestspinner > 1)
                    {
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
                    if(++checkassignedToSpinner > 1)
                    {
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

            getTestProcessAPI(pageSize, currentPage)

        }

        binding!!.order.setOnClickListener {

            val request: Req = Req()

            val datas=mAdapter!!.getSelectedCheckData()

            var OrderList: ArrayList<OrderProcessDetail> = ArrayList()

            OrderList.clear()

            var status:Boolean=true

            if(datas!!.size!=0){

                for(i in datas!!.indices){

                    if(datas[i]!!.test_method_uuid==1 && datas[i]!!.order_status_uuid==SAMPLE_RECEIVE) {

                        var order: OrderProcessDetail = OrderProcessDetail()

                        order.Id= datas[i]!!.uuid!!

                        order.order_status_uuid=datas[i]!!.order_status_uuid!!

                        order.to_location_uuid=datas[i]!!.to_location_uuid!!

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

                }

            }
            else{

                Toast.makeText(this,"Please Select Any one Item",Toast.LENGTH_SHORT).show()

            }

        }

        binding!!.saveOfApproval.setOnClickListener {

            val datas=mAdapter!!.getSelectedCheckData()

            var reqest: LabrapidSaveRequestModel=LabrapidSaveRequestModel()

            var detailsArray: ArrayList<SendIdList> = ArrayList()

            detailsArray.clear()

            var status:Boolean=true

            if(datas!!.size!=0){

                for(i in datas!!.indices){

                    if(datas[i]!!.test_method_uuid==1 && datas[i]!!.order_status_uuid==EXECUTEDUUId) {

                        val details: SendIdList=SendIdList()

                        details.Id= datas[i]!!.uuid!!

                        detailsArray.add(details)

                    }
                    else{

                        status=false

                    }
                }

                if(status){


                    val ft = supportFragmentManager.beginTransaction()
                    val dialog = SendForApprovalDialogFragment()
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


        binding?.clear!!.setOnClickListener {

            clearSearch()

        }

        binding?.rejected?.setOnClickListener{

            val datas=mAdapter!!.getSelectedCheckData()

            var reqest: LabrapidSaveRequestModel = LabrapidSaveRequestModel()

            var detailsArray: ArrayList<SendIdList> = ArrayList()

            detailsArray.clear()

            var status:Boolean=true

            if(datas!!.size!=0){

                for(i in datas!!.indices){

                    if((datas[i]!!.order_status_uuid==SAMPLE_RECEIVE || datas[i]!!.order_status_uuid==SAMPLE_TRANSPORTUUId ) || ( datas[i]!!.order_status_uuid==EXECUTEDUUId || datas[i]!!.order_status_uuid==SAMPLE_RECEIVE) ) {

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

        getTestProcessAPI(pageSize,currentPage)


    }

    private fun getTestProcessAPISecond(pageSize: Int, currentPage: Int) {
        val testProcessRequestModel = TestProcessRequestModel()

        testProcessRequestModel.pageNo = currentPage
        testProcessRequestModel.paginationSize = pageSize
        testProcessRequestModel.search = ""
        testProcessRequestModel.test_name = selectTestitemUuid
        testProcessRequestModel.to_facility_name = selectAssignitemUuid
        testProcessRequestModel.order_number = orderNumber
        testProcessRequestModel.fromDate = startDate
        testProcessRequestModel.toDate = endDate
        testProcessRequestModel.is_requied_test_process_list = true
        testProcessRequestModel.lab_uuid = null
        val arrayList :ArrayList<Int> = ArrayList()
        arrayList.add(14)
        arrayList.add(15)
        arrayList.add(16)
        arrayList.add(13)
        arrayList.add(19)
        arrayList.add(7)
        arrayList.add(2)
        testProcessRequestModel.order_status_uuids = arrayList
      //  testProcessRequestModel.to_location_uuid = null
        testProcessRequestModel.pinOrMobile = pinOrMobile
        testProcessRequestModel.qualifier_filter = ""
        testProcessRequestModel.widget_filter = ""


        viewModel?.getLabTestProcessListSecond(testProcessRequestModel, testProcessSecondRetrofitCallback)
    }


    private fun getTestProcessAPI(pageSize: Int, currentPage: Int) {

        val testProcessRequestModel = TestProcessRequestModel()

        testProcessRequestModel.pageNo = currentPage
        testProcessRequestModel.paginationSize = pageSize
        testProcessRequestModel.search = ""
        testProcessRequestModel.test_name = selectTestitemUuid
        testProcessRequestModel.to_facility_name = selectAssignitemUuid
        testProcessRequestModel.order_number = orderNumber
        testProcessRequestModel.fromDate = startDate
        testProcessRequestModel.toDate = endDate
        testProcessRequestModel.is_requied_test_process_list = true
        testProcessRequestModel.lab_uuid = null
        val arrayList :ArrayList<Int> = ArrayList()
        arrayList.add(14)
        arrayList.add(15)
        arrayList.add(16)
        arrayList.add(13)
        arrayList.add(19)
        arrayList.add(7)
        arrayList.add(2)
        testProcessRequestModel.order_status_uuids = arrayList
 //       testProcessRequestModel.to_location_uuid = null
        testProcessRequestModel.pinOrMobile = pinOrMobile
        testProcessRequestModel.qualifier_filter = ""
        testProcessRequestModel.widget_filter = ""



       viewModel?.getLabTestProcessList(testProcessRequestModel, testProcessRetrofitCallback)

    }

    val testProcessRetrofitCallback = object : RetrofitCallback<TestProcessResponseModel>{
        override fun onSuccessfulResponse(responseBody: Response<TestProcessResponseModel>?) {

            Log.e("labTestProcess",responseBody?.body()?.responseContents.toString())

            if (responseBody!!.body()?.responseContents?.isNotEmpty()!!) {
//                viewModel?.errorTextVisibility?.value = 8

                Log.i("page",""+currentPage+" "+responseBody?.body()?.responseContents!!.size)
                TOTAL_PAGES = Math.ceil(responseBody!!.body()!!.totalRecords!!.toDouble() / 10).toInt()

                if (responseBody.body()!!.responseContents!!.isNotEmpty()!!) {
                    mAdapter!!.addAll(responseBody!!.body()!!.responseContents)



                    binding?.positiveTxt!!.setText("0")

                    binding?.negativeTxt!!.setText("0")

                    binding?.equivocalTxt!!.setText("0")

                    binding?.rejectedTxt!!.setText("0")

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

                    val diseaseList = responseBody?.body()?.disease_result_data
                    for (i in diseaseList!!.indices){

                        if(diseaseList.isNotEmpty() && diseaseList[i]?.qualifier_uuid == 2){
                            binding?.positiveTxt!!.setText(diseaseList[i]?.qualifier_count.toString())
                        }else if(diseaseList.isNotEmpty() && diseaseList[i]?.qualifier_uuid == 1){
                            binding?.negativeTxt!!.setText(diseaseList[i]?.qualifier_count.toString())
                        }else if(diseaseList.isNotEmpty() && diseaseList[i]?.qualifier_uuid == 3){
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

                } else {
                    binding?.progressbar!!.setVisibility(View.GONE);
                    mAdapter!!.removeLoadingFooter()
                    isLoading = false
                    isLastPage = true
                }
            }

            else
            {
                Toast.makeText(this@LabTestProcessActivity,"No records found",Toast.LENGTH_LONG).show()

                binding?.progressbar!!.setVisibility(View.GONE);

                binding?.positiveTxt!!.setText("0")

                binding?.negativeTxt!!.setText("0")

                binding?.equivocalTxt!!.setText("0")

                binding?.rejectedTxt!!.setText("0")
                mAdapter!!.clearAll()
            }

        }

        override fun onBadRequest(errorBody: Response<TestProcessResponseModel>?) {
            val gson = GsonBuilder().create()
            val responseModel: TestProcessResponseModel
            try {
                responseModel = gson.fromJson(
                    errorBody!!.errorBody()!!.string(),
                    TestProcessResponseModel::class.java
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

    val orderDetailsRetrofitCallback = object  : RetrofitCallback<OrderProcessDetailsResponseModel>{
        override fun onSuccessfulResponse(responseBody: Response<OrderProcessDetailsResponseModel>?) {

            //  labListAPI()

            Log.i("order","Save"+responseBody!!.body()!!.responseContents.rows)
            val ft = supportFragmentManager.beginTransaction()
            val dialog = OrderProcessDialogFragment()
            val bundle = Bundle()
            val saveArray=responseBody!!.body()!!.responseContents.rows
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


    val testProcessSecondRetrofitCallback = object : RetrofitCallback<TestProcessResponseModel> {
        override fun onSuccessfulResponse(response: Response<TestProcessResponseModel>) {
            if (response.body()?.responseContents!!.isNotEmpty()!!) {

                Log.i("page",""+currentPage+" "+response?.body()?.responseContents!!.size)

   /*             val diseaseList = response?.body()?.disease_result_data

                for (i in diseaseList!!.indices){

                    if(diseaseList.size != 0 && diseaseList[i]?.result_value.equals("Positive")){

                        val datas=(binding?.positiveTxt!!.text.toString().toInt())+ diseaseList[i]?.qualifier_count!!

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

                        if(orderList[i]?.order_status_uuid==2){

                            var datas=(binding?.rejectedTxt!!.text.toString().toInt()) + orderList[i]?.order_count!!

                            binding?.rejectedTxt!!.setText(datas.toString())

                        }

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
                println("testing for six  = $currentPage--$TOTAL_PAGES")
                binding?.progressbar!!.setVisibility(View.GONE);
                mAdapter?.removeLoadingFooter()
                isLoading = false
                isLastPage = true
            }
        }

        override fun onBadRequest(response: Response<TestProcessResponseModel>?) {
            binding?.progressbar!!.setVisibility(View.GONE);
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
            @SuppressLint("SetTextI18n")
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
                            this@LabTestProcessActivity,
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
                        this@LabTestProcessActivity,
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

    val sampleRecivedRetrofitCallback = object  : RetrofitCallback<SimpleResponseModel>{
        override fun onSuccessfulResponse(responseBody: Response<SimpleResponseModel>?) {


            Toast.makeText(this@LabTestProcessActivity,"Sample Received Successfully",Toast.LENGTH_LONG).show()

            binding?.progressbar!!.setVisibility(View.GONE);
            Log.i("Sucess","")
            mAdapter!!.clearAll()

            pageSize=10

            currentPage=0

            getTestProcessAPI(10,0)


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

            binding?.progressbar!!.setVisibility(View.GONE);
            viewModel!!.progress.value = 8
        }

    }

    override fun onRejectDialogTolabtestactivity() {

        Toast.makeText(this,"Rejected Successfully",Toast.LENGTH_LONG).show()

        mAdapter!!.clearAll()

        pageSize=10

        currentPage=0

        getTestProcessAPI(10,0)

    }

    override fun onOrderProcessDialogTolabtestactivity() {

        Toast.makeText(this@LabTestProcessActivity,"Save Successfully",Toast.LENGTH_LONG).show()

        mAdapter!!.clearAll()

        pageSize=10

        currentPage=0

        getTestProcessAPI(10,0)

    }

    override fun onSendForApprovalTolabtestactivity() {
        mAdapter!!.clearAll()

        pageSize=10

        currentPage=0

        getTestProcessAPI(10,0)

    }

    private fun clearSearch(){

        binding?.calendarEditText!!.setText("")
        binding?.searchUsingMobileNo!!.setText("")
        binding?.searchOrderNumber!!.setText("")
        binding?.assignedToSpinner?.setSelection(0)
        binding?.testSpinner?.setSelection(0)

    }

    override fun onAssignTolabtestactivity() {
        mAdapter!!.clearAll()

        pageSize = 10

        currentPage = 0

        getTestProcessAPI(10,0)
    }

}
