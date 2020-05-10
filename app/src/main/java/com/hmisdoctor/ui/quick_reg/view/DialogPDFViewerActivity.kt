package com.hmisdoctor.ui.quick_reg.view


import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Environment.getExternalStoragePublicDirectory
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.PdfviewBinding
import com.hmisdoctor.ui.emr_workflow.prescription.ui.ErrorAPIClass
import com.hmisdoctor.ui.quick_reg.model.PDFRequestModel
import com.hmisdoctor.ui.quick_reg.model.QuickRegistrationSaveResponseModel
import com.hmisdoctor.ui.quick_reg.model.SampleErrorResponse
import com.hmisdoctor.ui.quick_reg.view_model.PDFViewModel
import com.hmisdoctor.ui.quick_reg.view_model.PDFViewModelFactory
import com.hmisdoctor.utils.Utils
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.*


class DialogPDFViewerActivity : AppCompatActivity() {
    private var destinationFile: File?=null
    var binding: PdfviewBinding? = null
    private var department_uuid: Int? = null
    private var facilitylevelID: Int? = null
    private var viewModel: PDFViewModel? = null

    var downloadZipFileTask: DownloadZipFileTask? =
        null
   var pdfRequestModel : PDFRequestModel = PDFRequestModel()
    private var utils: Utils? = null
    var appPreferences: AppPreferences? = null

    var nextpageStatus:Int=0

    companion object {
        const val PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 101

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.pdfview)
        viewModel = PDFViewModelFactory(
            application

        ).create(PDFViewModel::class.java)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this

        utils = Utils(this)


        binding!!.closeImageView.setOnClickListener {

            finish()
            startActivity(Intent(this,QuickRegistrationActivity::class.java))
        }

        appPreferences = AppPreferences.getInstance(this, AppConstants.SHARE_PREFERENCE_NAME)
        facilitylevelID = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        department_uuid = appPreferences?.getInt(AppConstants.DEPARTMENT_UUID)

        val gson = Gson()

        pdfRequestModel = gson.fromJson<PDFRequestModel>(intent.getStringExtra(AppConstants?.RESPONSECONTENT),PDFRequestModel::class.java)

        nextpageStatus=intent.getIntExtra(AppConstants?.RESPONSENEXT,0)

        Log.i("res",""+pdfRequestModel)
        Log.i("res",""+pdfRequestModel)
        Log.i("res",""+pdfRequestModel)
/*
        pDFRequestModel.componentName = "quick"
        pDFRequestModel.uuid = 497
        pDFRequestModel.uhid = "55510000002688"
        pDFRequestModel.facilityName = "Govt.Hospital Chennai"
        pDFRequestModel.firstName = "GOMATHI"
        pDFRequestModel.period = "Year"
        pDFRequestModel.age =30
        pDFRequestModel.registered_date = "2020-04-30T12:56:06.770Z"
        pDFRequestModel.sari = false
        pDFRequestModel.ili = true
        pDFRequestModel.noSymptom = false
        pDFRequestModel.testMethod = "RT - PCR(Nasopharyngeal Swab for Covid 19)"
        pDFRequestModel.gender = "Male"
        pDFRequestModel.mobile = "7358596142"
        pDFRequestModel.addressDetails?.doorNum="No. 10 Kancheepuram high road"
        pDFRequestModel.addressDetails?.country="India"
        pDFRequestModel.addressDetails?.state="TAMIL NADU"
        pDFRequestModel.addressDetails?.district="Chennai"
        pDFRequestModel.addressDetails?.block="Chennai"
        pDFRequestModel.addressDetails?.pincode="603002"*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            runTimePermission()

        } else {

            if(pdfRequestModel.uuid!=0) {
                viewModel?.GetPDFf(pdfRequestModel, GetPDFRetrofitCallback)
            }
        }
    }

    private fun runTimePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE
            )
            return
        }

        else{
            viewModel?.GetPDFf(pdfRequestModel,GetPDFRetrofitCallback)
            return
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            Log.i("", "" + grantResults)

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // now, you have permission go ahead
                viewModel?.GetPDFf(pdfRequestModel,GetPDFRetrofitCallback)

            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this@DialogPDFViewerActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    // now, user has denied permission (but not permanently!)
                    //Only denied
                    getCustomDialog()

                } else {
                    //Dontshowagain
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", packageName, null)
                    })
                }}}

    }

    private fun getCustomDialog() {
        // build alert dialog
        val dialogBuilder = AlertDialog.Builder(this)
        // set message of alert dialog
        dialogBuilder.setMessage("App need this permission")
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton("Proceed", DialogInterface.OnClickListener { dialog, id ->
                runTimePermission()
            })
            // negative button text and action
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })
        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("Permission!!")
        // show alert dialog
        alert.show()
    }


    val GetPDFRetrofitCallback = object : RetrofitCallback<ResponseBody> {
        override fun onSuccessfulResponse(responseBody: Response<ResponseBody>?) {
            //you can now get your file in the InputStream
            downloadZipFileTask = DownloadZipFileTask()
            downloadZipFileTask!!.execute(responseBody?.body())

            if(nextpageStatus==1){

                finish()

                startActivity(Intent(this@DialogPDFViewerActivity,LabTestActivity::class.java))

            }
        }

        override fun onBadRequest(response: Response<ResponseBody>?) {

            Log.e("badreq",response.toString())
            val gson = GsonBuilder().create()
            val responseModel: QuickRegistrationSaveResponseModel
            var mError = SampleErrorResponse()
            try {
                mError = gson.fromJson(response!!.errorBody()!!.string(), SampleErrorResponse::class.java)


                if(mError.statusCode==422 && mError.printValidationError!!.field=="uhid"){

                    finish()

                    startActivity(Intent(this@DialogPDFViewerActivity,LabTestActivity::class.java))

                }

            } catch (e: IOException) { // handle failure to read error
            }
        }

        override fun onServerError(response: Response<*>) {

        }

        override fun onUnAuthorized() {

        }

        override fun onForbidden() {

        }

        override fun onFailure(failure: String) {
//            utils?.showToast(R.color.negativeToast, binding?.mainLayout!!, failure)
        }

        override fun onEverytime() {
            viewModel!!.progress.value = 8
        }

    }
   inner class DownloadZipFileTask :
        AsyncTask<ResponseBody?, Pair<Int?, Long?>?, String?>() {

        override fun onPreExecute() {
            super.onPreExecute()

        }
        fun doProgress(progressDetails: Pair<Int?, Long?>?) {
            publishProgress(progressDetails)
        }

        override fun onPostExecute(result: String?) {
            binding?.progressbar!!.setVisibility(View.GONE);

            binding?.pdfView!!.fromFile(destinationFile)
                .password(null)
                .defaultPage(0)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .onPageError { page, _ ->
                    Toast.makeText(
                        this@DialogPDFViewerActivity,
                        "Error at page: $page", Toast.LENGTH_LONG
                    ).show()
                }
                .load()
            Toast.makeText(
                this@DialogPDFViewerActivity,
                "Storage path: $destinationFile", Toast.LENGTH_LONG
            ).show()


        }
        override fun doInBackground(vararg params: ResponseBody?): String? {
            saveToDisk(params[0]!!, "${pdfRequestModel.firstName} ${pdfRequestModel.uuid}.pdf")
            return null
        }}

    override fun onBackPressed() {

        finish()

        startActivity(Intent(this,QuickRegistrationActivity::class.java))

        super.onBackPressed()


    }


    private fun saveToDisk(body: ResponseBody, filename: String) {
        try {
            destinationFile = File(
                getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                filename
            )
            val inputstream : InputStream? = body.byteStream()
            val os: OutputStream = FileOutputStream(destinationFile)
            val buffer = ByteArray(1024)
            var bytesRead: Int
            //read from is to buffer
            while (inputstream!!.read(buffer).also { bytesRead = it } != -1) {
                os.write(buffer, 0, bytesRead)
            }
            inputstream.close()
            //flush OutputStream to write any buffered data to file
            os.flush()
            os.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}

