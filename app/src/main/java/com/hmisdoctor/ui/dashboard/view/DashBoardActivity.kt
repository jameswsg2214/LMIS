package com.hmisdoctor.ui.dashboard.view

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.ActivityDashboardBinding
import com.hmisdoctor.ui.configuration.view.ConfigActivity
import com.hmisdoctor.ui.dashboard.model.*
import com.hmisdoctor.ui.dashboard.view_model.DashboardViewModel
import com.hmisdoctor.ui.dashboard.view_model.DashboardViewModelFactory
import com.hmisdoctor.ui.emr_workflow.chief_complaint.ui.ChiefComplaintChildFragment
import com.hmisdoctor.ui.emr_workflow.chief_complaint.ui.DocumentFragment
import com.hmisdoctor.ui.emr_workflow.model.EmrWorkFlowResponseModel
import com.hmisdoctor.ui.institute.view.ManageInstituteDialogFragment
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.ui.login.view.LoginActivity
import com.hmisdoctor.ui.myprofile.view.MyProfileActivity
import com.hmisdoctor.ui.out_patient.view.OutPatientActivity
import com.hmisdoctor.ui.quick_reg.view.LabTestActivity
import com.hmisdoctor.ui.quick_reg.view.LabTestApprovalActivity
import com.hmisdoctor.ui.quick_reg.view.LabTestProcessActivity
import com.hmisdoctor.ui.quick_reg.view.QuickRegistrationActivity
import com.hmisdoctor.utils.Utils
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.home_layout.*
import kotlinx.android.synthetic.main.home_layout.view.*
import kotlinx.android.synthetic.main.navigation_layout.*
import retrofit2.Response


class DashBoardActivity : AppCompatActivity() {
    var utils: Utils? = null
    var binding: ActivityDashboardBinding? = null
    private var viewModel: DashboardViewModel? = null
    var appPreferences: AppPreferences? = null
    var name: String? = null
    private var patientsAdapter: PatientsAdapter? = null
    private var data = ArrayList<CommonCount>()
    private var diagnosisData = ArrayList<Diagnosis>()
    private var chiefComplaintsData = ArrayList<ChiefComplients>()
    var fragmentAdapter: PatientsPagerAdapter? = null

    private val tabIcons = intArrayOf(
        R.drawable.back_to_home_button,
        R.drawable.calender_icon,
        R.drawable.cell_shape
    )

    var plus : Boolean?=true

    var emrcheck:Boolean?=null

    var registercheck:Boolean?=null

    var LmisCheck:Boolean?=null

    var userDetailsRoomRepository: UserDetailsRoomRepository? = null

    private var customdialog: Dialog?=null

    private var patientType: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        viewModel = DashboardViewModelFactory(
            application
        ).create(DashboardViewModel::class.java)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this


        setSupportActionBar(homeLayoutInclude?.toolbar)
        val toggle = object :
            ActionBarDrawerToggle(
                this,
                drawerLayout, homeLayoutInclude?.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            ) {}
        drawerLayout?.addDrawerListener(toggle)

        utils = Utils(this)
        toggle.toolbarNavigationClickListener =
            View.OnClickListener { drawerLayout?.openDrawer(GravityCompat.START) }
        homeLayoutInclude?.toolbar?.setNavigationIcon(R.drawable.ic_hamburger_icon)
        appPreferences = AppPreferences.getInstance(this, AppConstants.SHARE_PREFERENCE_NAME)
        name = appPreferences?.getString(AppConstants.INSTITUTION_NAME)
        initPatientsAdapter()

        emrcheck= appPreferences?.getBoolean(AppConstants.EMRCHECK)

        LmisCheck= appPreferences?.getBoolean(AppConstants.LMISCHECK)

        registercheck= appPreferences?.getBoolean(AppConstants.REGISTRATIONCHECK)

        userDetailsRoomRepository = UserDetailsRoomRepository(application!!)
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()

        userNameTextView?.setText(userDataStoreBean?.first_name)

        if(this.emrcheck!!){

            emr.visibility=View.VISIBLE
        }
        else{

            emr.visibility=View.GONE

        }

        if(this.LmisCheck!!){

            lab.visibility=View.VISIBLE
        }
        else{

            lab.visibility=View.GONE
        }

        if(this.registercheck!!){

            registration.visibility=View.VISIBLE
        }
        else{

            registration.visibility=View.GONE
        }


        // pass your values and retrieve them in the other Activity using keyName
        when {
            this.emrcheck!! -> {
                emr_view.visibility=View.VISIBLE
                register_view.visibility=View.GONE
                LMIS_view.visibility=View.GONE
            }
            this.registercheck!! -> {
                emr_view.visibility=View.GONE
                register_view.visibility=View.VISIBLE
                LMIS_view.visibility=View.GONE
            }
            this.LmisCheck!! -> {

                emr_view.visibility=View.GONE
                register_view.visibility=View.GONE
                LMIS_view.visibility=View.VISIBLE
            }
        }

        officeName!!.setText(name)
        officeName!!.setOnClickListener {
            val ft = supportFragmentManager.beginTransaction()
            val dialog = ManageInstituteDialogFragment()
            dialog.show(ft, "Tag")
        }
        config.setOnClickListener {
            startActivity(Intent(this, ConfigActivity::class.java))
            drawerLayout!!.closeDrawer(GravityCompat.START)
        }
        outpatient?.setOnClickListener {
            patientType = AppConstants.OUT_PATIENT
            viewModel?.getEmrWorkFlow(emrWorkFlowRetrofitCallBack)
            drawerLayout!!.closeDrawer(GravityCompat.START)
        }

        inPatient?.setOnClickListener{
            patientType = AppConstants.IN_PATIENT
            viewModel?.getEmrWorkFlow(emrWorkFlowRetrofitCallBack)
            drawerLayout?.closeDrawer(GravityCompat.START)
        }
        outPatientCardView?.setOnClickListener {
            patientType = AppConstants.OUT_PATIENT
            viewModel?.getEmrWorkFlow(emrWorkFlowRetrofitCallBack)
            drawerLayout!!.closeDrawer(GravityCompat.START)
        }

        inPatientCardView?.setOnClickListener{
            patientType = AppConstants.IN_PATIENT
            viewModel?.getEmrWorkFlow(emrWorkFlowRetrofitCallBack)
            drawerLayout?.closeDrawer(GravityCompat.START)
        }




        confifav?.setOnClickListener {

            startActivity(Intent(this, ConfigActivity::class.java))
        }
        myProfile?.setOnClickListener {

            startActivity(Intent(this, MyProfileActivity::class.java))
            drawerLayout!!.closeDrawer(GravityCompat.START)
        }

        changePassword?.setOnClickListener {
            val ft = supportFragmentManager.beginTransaction()
            val dialog = ChangePasswordFragemnt()
            dialog.show(ft, "Tag")
        }
        covidReg?.setOnClickListener {
            startActivity(Intent(this, CovidRegistrationActivity::class.java))
            drawerLayout!!.closeDrawer(GravityCompat.START)
        }

        covidRegisterButton?.setOnClickListener {
            startActivity(Intent(this, CovidRegistrationActivity::class.java))
            drawerLayout!!.closeDrawer(GravityCompat.START)
        }

        registration?.setOnClickListener {

            if (quick_Reg?.visibility == View.VISIBLE) {
                quick_Reg?.visibility = View.GONE
                add_image?.setImageResource(R.drawable.ic_add)

            } else {
                quick_Reg?.visibility = View.VISIBLE
                add_image?.setImageResource(R.drawable.ic_minus_white)

            }
        }
        lab?.setOnClickListener {

            if (lab_test?.visibility == View.VISIBLE && lab_approvl?.visibility == View.VISIBLE &&  lab_process?.visibility == View.VISIBLE) {
                lab_test?.visibility = View.GONE
                lab_approvl?.visibility = View.GONE
                lab_process?.visibility = View.GONE
                lab_image?.setImageResource(R.drawable.ic_add)

            } else {
                lab_test?.visibility = View.VISIBLE
                lab_approvl?.visibility = View.VISIBLE
                lab_process?.visibility = View.VISIBLE

                lab_image?.setImageResource(R.drawable.ic_minus_white)

            }
        }

        emr.setOnClickListener {

            if (outpatient.visibility == View.VISIBLE && inPatient.visibility == View.VISIBLE &&  config.visibility == View.VISIBLE) {
                outpatient.visibility = View.GONE
                inPatient.visibility = View.GONE
                config.visibility = View.GONE
                emr_image.setImageResource(R.drawable.ic_add)

            } else {
                outpatient.visibility = View.VISIBLE
                inPatient.visibility = View.VISIBLE
                config.visibility = View.VISIBLE

                emr_image.setImageResource(R.drawable.ic_minus_white)

            }
        }
        settings.setOnClickListener {

            if (changePassword.visibility == View.VISIBLE ) {
                changePassword.visibility = View.GONE
                settings_image.setImageResource(R.drawable.ic_add)

            } else {
                changePassword.visibility = View.VISIBLE


                settings_image.setImageResource(R.drawable.ic_minus_white)

            }
        }




        logout?.setOnClickListener {
            drawerLayout!!.closeDrawer(GravityCompat.START)

            customdialog = Dialog(this)
            customdialog!! .requestWindowFeature(Window.FEATURE_NO_TITLE)
            customdialog!! .setCancelable(false)
            customdialog!! .setContentView(R.layout.logout_dialog)
            val closeImageView = customdialog!! .findViewById(R.id.closeImageView) as ImageView
            closeImageView.setOnClickListener {

                customdialog?.dismiss()
            }

            val yesBtn = customdialog!! .findViewById(R.id.yes) as CardView
            val noBtn = customdialog!! .findViewById(R.id.no) as CardView
            yesBtn.setOnClickListener {

                startActivity(Intent(this, LoginActivity::class.java))

                finishAffinity()


                customdialog!!.dismiss()


            }
            noBtn.setOnClickListener {
                customdialog!! .dismiss()


            }
            customdialog!! .show()
        }



                lab_test?.setOnClickListener {
                    startActivity(Intent(this, LabTestActivity::class.java))
                    drawerLayout!!.closeDrawer(GravityCompat.START)

                }
        lab_approvl?.setOnClickListener {
            startActivity(Intent(this, LabTestApprovalActivity::class.java))
            drawerLayout!!.closeDrawer(GravityCompat.START)

        }


        quickRegisiter?.setOnClickListener {
            val intent = Intent(this@DashBoardActivity, QuickRegistrationActivity::class.java)
            intent.putExtra(
                AppConstants?.FirstTime,
                "1"
            ) // pass your values and retrieve them in the other Activity using keyName

            startActivity(intent)

            drawerLayout!!.closeDrawer(GravityCompat.START)
        }

        quick_Reg?.setOnClickListener {
            val intent = Intent(this@DashBoardActivity, QuickRegistrationActivity::class.java)
            intent.putExtra(
                AppConstants?.FirstTime,
                "1"
            ) // pass your values and retrieve them in the other Activity using keyName

            startActivity(intent)

            drawerLayout!!.closeDrawer(GravityCompat.START)
        }
        getPatientsInfo()
        //setUpViewPager()
        lab_process?.setOnClickListener {
            startActivity(Intent(this, LabTestProcessActivity::class.java))
            drawerLayout!!.closeDrawer(GravityCompat.START)
        }
    }







    private fun setupTabIcons() {
        tabLayout.getTabAt(0)!!.setIcon(tabIcons[0])
        tabLayout.getTabAt(1)!!.setIcon(tabIcons[1])
    }


    private fun setUpViewPager() {
      //  nestedScrollView.isFillViewport = true
        fragmentAdapter =
            PatientsPagerAdapter(supportFragmentManager, diagnosisData, chiefComplaintsData)
        viewPager.adapter = fragmentAdapter

        tabLayout.setupWithViewPager(viewPager)
    }

    private fun getPatientsInfo() {
        viewModel!!.getPatientsDetails(dashBoardDetailRetrofitCallBack)
    }


    private fun initPatientsAdapter() {
        patientsAdapter = PatientsAdapter(this, data)
        rvPatientsCount.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvPatientsCount.adapter = patientsAdapter
    }


    private val dashBoardDetailRetrofitCallBack = object : RetrofitCallback<DashBoardResponse> {
        override fun onSuccessfulResponse(responseBody: Response<DashBoardResponse>?) {
            prepareDataForPatientsAdapter(responseBody?.body()?.responseContents)
            chiefComplaintsData = responseBody?.body()?.responseContents?.cieif_complaints!!
            diagnosisData = responseBody?.body()?.responseContents?.diagnosis!!
            setUpViewPager()
            setupTabIcons()
            prepareDataForGraphLines(responseBody?.body()?.responseContents!!)
        }

        override fun onBadRequest(errorBody: Response<DashBoardResponse>?) {
            val gson = GsonBuilder().create()
            val responseModel: DashBoardResponse
            try {
                responseModel = gson.fromJson(
                    errorBody?.errorBody()!!.string(),
                    DashBoardResponse::class.java
                )
                utils?.showToast(
                    R.color.negativeToast,
                    mainLayout!!,
                    responseModel.message!!
                )
            } catch (e: Exception) {
                utils?.showToast(
                    R.color.negativeToast,
                    mainLayout!!,
                    getString(R.string.something_went_wrong)
                )
                e.printStackTrace()
            }
        }

        override fun onServerError(response: Response<*>?) {
            Toast.makeText(this@DashBoardActivity, "Server Error", Toast.LENGTH_LONG).show()
        }

        override fun onUnAuthorized() {
            utils?.showToast(
                R.color.negativeToast,
                mainLayout,
                getString(R.string.unauthorized)
            )
        }

        override fun onForbidden() {
            Toast.makeText(this@DashBoardActivity, "Server Error", Toast.LENGTH_LONG).show()
        }

        override fun onEverytime() {
            viewModel!!.progressBar.value = 8
        }


        override fun onFailure(s: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }


    private val emrWorkFlowRetrofitCallBack = object : RetrofitCallback<EmrWorkFlowResponseModel> {
        override fun onSuccessfulResponse(response: Response<EmrWorkFlowResponseModel>) {
            if (response.body()?.responseContents?.isNotEmpty()!!) {
                val intent = Intent(this@DashBoardActivity,OutPatientActivity::class.java)
                intent.putExtra(AppConstants.PATIENT_TYPE,patientType)
                startActivity(intent)
            } else {
                val intent = Intent(this@DashBoardActivity,ConfigActivity::class.java)
                intent.putExtra(AppConstants.PATIENT_TYPE,patientType)
                startActivity(intent)
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
                    mainLayout!!,
                    responseModel.message!!
                )
            } catch (e: Exception) {
                utils?.showToast(
                    R.color.negativeToast,
                    mainLayout!!,
                    getString(R.string.something_went_wrong)
                )
                e.printStackTrace()
            }
        }

        override fun onServerError(response: Response<*>) {
            Toast.makeText(this@DashBoardActivity, "Server Error", Toast.LENGTH_LONG).show()

        }

        override fun onUnAuthorized() {
            utils?.showToast(
                R.color.negativeToast,
                mainLayout,
                getString(R.string.unauthorized)
            )
        }

        override fun onForbidden() {
            Toast.makeText(this@DashBoardActivity, "Server Error", Toast.LENGTH_LONG).show()

        }

        override fun onFailure(failure: String) {
            utils?.showToast(
                R.color.negativeToast,
                mainLayout!!,
                failure
            )
        }

        override fun onEverytime() {
            viewModel!!.progressBar.value = 8
        }
    }

    override fun onBackPressed() { // Do Here what ever you want do on back press;
    }


    fun prepareDataForPatientsAdapter(responseContents: DashBoardContent?) {
        if (responseContents?.consulted!!.isNotEmpty()) {
            val consultendData = responseContents?.consulted!![0]
            val groupDataOne = CommonCount(
                "F-" + consultendData.F_Count!!.toString(),
                "M-" + consultendData.M_Count!!.toString(),
                "TG-" + consultendData.T_Count!!.toString(),
                consultendData.Tot_Count!!.toString(),
                "Consulted Patients",
                resources.getColor(R.color.cp)
            )
            data.add(groupDataOne)
        }

        val orderData = responseContents?.orders
        val groupDataTwo = CommonCount(
            "RAD-" + orderData?.rad_count!!.toString(),
            "LAB-" + orderData.lab_count!!.toString(),
            "IVG-" + orderData.inv_count!!.toString(),
            orderData.total_count!!.toString(),
            "Orders",
            resources.getColor(R.color.orders
            )
        )

        data.add(groupDataTwo)
        patientsAdapter?.updateAdapter(data)
        if (responseContents?.prescription!!.isNotEmpty()) {
            val prescriptionData = responseContents?.prescription!![0]
            val groupDataTwo = CommonCount(
                "F-" + prescriptionData.F_Count!!.toString(),
                "M-" + prescriptionData.M_Count!!.toString(),
                "TG-" + prescriptionData.T_Count!!.toString(),
                prescriptionData.Tot_Count!!.toString(),
                "Prescription",
                resources.getColor(R.color.prescriptionsYellow)
            )

            data.add(groupDataTwo)
        }
        patientsAdapter?.updateAdapter(data)
    }

    fun prepareDataForGraphLines(responseContents: DashBoardContent) {
        var consMap: HashMap<String?, Int?>? = HashMap<String?, Int?>()
        consMap = responseContents?.cons_graph!!

        val graphValue1: MutableCollection<Int?> = consMap.values
        val listGraphValues1: ArrayList<Int> = ArrayList<Int>(graphValue1)
        val graphKey1: MutableCollection<String?> = consMap.keys
        val listKey1: ArrayList<String> = ArrayList<String>(graphKey1)

        var orderMap: HashMap<String?, Int?>? = HashMap<String?, Int?>()
        orderMap = responseContents.orders_graph!!

        val graphValue2: MutableCollection<Int?> = orderMap.values
        val listGraphValues2: ArrayList<Int> = ArrayList<Int>(graphValue2)
        val graphKey2: MutableCollection<String?> = orderMap.keys
        val listKey2: ArrayList<String> = ArrayList<String>(graphKey2)

        val xAxisLabel1 = ArrayList<String>()
        /*for(i in listKey1.indices){
            xAxisLabel1.add(xAxisLabel1[i])
        }*/

        xAxisLabel1.add("01-02-2010")
        xAxisLabel1.add("02-02-2010")
        xAxisLabel1.add("03-02-2010")
        xAxisLabel1.add("04-02-2010")
        xAxisLabel1.add("05-02-2010")
        xAxisLabel1.add("06-02-2010")
        xAxisLabel1.add("07-02-2010")
        xAxisLabel1.add("08-02-2010")
        xAxisLabel1.add("09-02-2010")
        xAxisLabel1.add("10-02-2010")
        xAxisLabel1.add("11-02-2010")
        xAxisLabel1.add("12-02-2010")
        xAxisLabel1.add("13-02-2010")
        xAxisLabel1.add("14-02-2010")
        xAxisLabel1.add("15-02-2010")
        xAxisLabel1.add("16-02-2010")
        xAxisLabel1.add("17-02-2010")

        graphview.getLayoutParams().height = 250
        graphview.invalidate()

        val xAxis: XAxis = graphview.getXAxis()
         xAxis.setValueFormatter( IndexAxisValueFormatter(getDate(xAxisLabel1)));
        xAxis.textSize = 12f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = ColorTemplate.getHoloBlue()
        xAxis.isEnabled = true
        xAxis.enableGridDashedLine(10f, 10f, 0f)

        val leftAxis: YAxis = graphview.getAxisLeft()
        leftAxis.removeAllLimitLines()
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        leftAxis.textColor = ColorTemplate.getHoloBlue()
        leftAxis.axisMinimum = 0f
        leftAxis.enableGridDashedLine(10f, 10f, 0f)
        leftAxis.setDrawLimitLinesBehindData(true)
        leftAxis.setDrawGridLines(true)

        graphview.getAxisRight().setEnabled(false)

      /*  val entries1 = ArrayList<Entry>()
        val  entries2 = ArrayList<Entry>()*/

        val entries1 = ArrayList<Entry>()
        entries1.add(Entry(1F, 20F))
        entries1.add(Entry(2F, 10F))
        entries1.add(Entry(3F, 20F))
        entries1.add(Entry(4F, 10F))
        entries1.add(Entry(5F, 10F))
        entries1.add(Entry(6F, 10F))
        entries1.add(Entry(7F, 20F))
        entries1.add(Entry(8F, 10F))
        entries1.add(Entry(9F, 20F))
        entries1.add(Entry(10F, 10F))
        entries1.add(Entry(11F, 10F))
        entries1.add(Entry(12F, 10F))
        entries1.add(Entry(13F, 20F))
        entries1.add(Entry(14F, 10F))
        entries1.add(Entry(15F, 20F))
        entries1.add(Entry(16F, 10F))
        entries1.add(Entry(17F, 10F))


        val entries2 = ArrayList<Entry>()
        entries2.add(Entry(1F, 13F))
        entries2.add(Entry(1F, 13F))
        entries2.add(Entry(2F, 13F))
        entries2.add(Entry(3F, 14F))
        entries2.add(Entry(4F, 12F))
        entries2.add(Entry(5F, 10F))
        entries2.add(Entry(6F, 18F))
        entries2.add(Entry(7F, 13F))
        entries2.add(Entry(8F, 13F))
        entries2.add(Entry(9F, 13F))
        entries2.add(Entry(10F, 14F))
        entries2.add(Entry(11F, 12F))
        entries2.add(Entry(12F, 10F))
        entries2.add(Entry(13F, 18F))
        entries2.add(Entry(14F, 13F))
        entries2.add(Entry(15F, 13F))
        entries2.add(Entry(16F, 13F))
        entries2.add(Entry(17F, 14F))
        entries2.add(Entry(18F, 12F))
        entries2.add(Entry(19F, 10F))
        entries2.add(Entry(20F, 18F))
/*
        for(i in listKey1.indices){
            entries1.add(Entry(i.toFloat(),listGraphValues1[i].toFloat()))
        }
        for(i in listKey2.indices){
            entries1.add(Entry(i.toFloat(),listGraphValues2[i].toFloat()))
        }*/

        val lines =ArrayList<ILineDataSet>()

        val set1 = LineDataSet(entries1, "DataSet 1")
        set1.mode = LineDataSet.Mode.CUBIC_BEZIER
        set1.cubicIntensity = 0.2f
        set1.setDrawFilled(true)
        set1.setDrawCircles(false)
        set1.lineWidth = 1.8f
        set1.circleRadius = 4f
        set1.setCircleColor(Color.GREEN)
        set1.highLightColor = Color.rgb(244, 117, 117)
        set1.color = Color.YELLOW
        set1.fillColor = Color.YELLOW
        set1.fillAlpha = 100
        set1.setDrawHorizontalHighlightIndicator(false)
        set1.fillFormatter =
            IFillFormatter { dataSet, dataProvider -> graphview.getAxisLeft().getAxisMinimum() }

        lines.add(set1)


        val set2 = LineDataSet(entries2, "DataSet 2")
        set2.mode = LineDataSet.Mode.CUBIC_BEZIER
        set2.cubicIntensity = 0.2f
        set2.setDrawFilled(true)
        set2.setDrawCircles(false)
        set2.lineWidth = 1.8f
        set2.circleRadius = 4f
        set2.setCircleColor(Color.GREEN)
        set2.highLightColor = Color.rgb(244, 117, 117)
        set2.color = Color.BLUE
        set2.fillColor = Color.BLUE
        set2.fillAlpha = 100
        set2.setDrawHorizontalHighlightIndicator(false)
        set2.fillFormatter =
            IFillFormatter { dataSet, dataProvider -> graphview.getAxisLeft().getAxisMinimum() }
        lines.add(set2)
        val data = LineData(lines)
        data.setValueTextSize(9f)
        data.setDrawValues(false)

        graphview.setData(data)

    }

    fun getDate(xAxisLabel1: ArrayList<String>): ArrayList<String>? {
        val label = java.util.ArrayList<String>()
        for (i in xAxisLabel1.indices) label.add(xAxisLabel1.get(i))
        return label
    }

}


