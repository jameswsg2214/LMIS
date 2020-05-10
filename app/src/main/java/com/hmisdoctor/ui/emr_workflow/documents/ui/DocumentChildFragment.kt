package com.hmisdoctor.ui.emr_workflow.documents.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.loader.content.CursorLoader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.hmisdoctor.R
import com.hmisdoctor.api_service.APIService
import com.hmisdoctor.application.HmisApplication
import com.hmisdoctor.callbacks.RetrofitCallback
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.config.AppPreferences
import com.hmisdoctor.databinding.FragmentDocumentChildBinding
import com.hmisdoctor.ui.emr_workflow.chief_complaint.view_model.DocumentViewModelFactory
import com.hmisdoctor.ui.emr_workflow.documents.model.*
import com.hmisdoctor.ui.emr_workflow.documents.view_model.DocumentViewModel
import com.hmisdoctor.ui.emr_workflow.radiology.model.RecyclerDto
import com.hmisdoctor.ui.login.model.UserDetailsRoomRepository
import com.hmisdoctor.utils.CustomProgressDialog
import com.hmisdoctor.utils.Utils
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

import kotlin.collections.ArrayList


class DocumentChildFragment : Fragment() {

    private var getDirectoryPath: String?=""
    private var file: File?=null
    private var encounter_uuid: Int? = null
    private var patient_uuid: Int? = null
    private var attacheddate: String?=null
    private var attachedname: String?=null
    private var fileformat: String?=null
    private var selectedImagePath: Uri? = null
    private var apiService: APIService? = null
    private var aiiceApplication: HmisApplication = HmisApplication()
    private var binding: FragmentDocumentChildBinding? = null
    private var viewModel: DocumentViewModel? = null
    private var destinationFile: File?=null
    var enableeye : Boolean?=true

    var uripath : Uri?=null
    private var deletefavouriteID: Int?=0
    private var utils: Utils? = null
    var appPreferences: AppPreferences? = null
    var userDetailsRoomRepository: UserDetailsRoomRepository? = null
    private val dataList: MutableList<RecyclerDto> = ArrayList()
    private var mAdapter: DocumentAdapter? = null
    private var typeList = mutableMapOf<Int, String>()
    private var selectPeriodUuid: Int? = 0
    private var facility_id: Int? = 0
    private var fromCalenderDateSetListener: DatePickerDialog.OnDateSetListener? = null
    private var toCalenderDateSetListener: DatePickerDialog.OnDateSetListener? = null
    private val fromCalender = Calendar.getInstance()
    private val toCalender = Calendar.getInstance()
    var startDate: String? = null
    var endDate: String? = null
    private var mHour: Int? = null
    private var mMinute: Int? = null
    val PERMISSION_REQUEST_CODE = 1001
    val PICK_IMAGE_REQUEST = 900
    lateinit var filePath : Uri
    private var customdialog: Dialog?=null
    var fileNmae: TextView?=null
    private var customProgressDialog: CustomProgressDialog? = null
    var downloadZipFileTask: DownloadZipFileTask? =
        null

    companion object {
        const val PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 101
        const val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 102

    }


    var prevOrder: Boolean = false

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_document_child,
                container,
                false
            )

        appPreferences = AppPreferences.getInstance(context, AppConstants.SHARE_PREFERENCE_NAME)
        userDetailsRoomRepository = UserDetailsRoomRepository(requireActivity().application)
        val userDataStoreBean = userDetailsRoomRepository?.getUserDetails()
        facility_id = appPreferences?.getInt(AppConstants.FACILITY_UUID)
        patient_uuid = appPreferences?.getInt(AppConstants.PATIENT_UUID)
        encounter_uuid = appPreferences?.getInt(AppConstants?.ENCOUNTER_UUID)
        aiiceApplication = HmisApplication.get(requireContext())
        apiService = aiiceApplication.getRetrofitService()
        customProgressDialog = CustomProgressDialog(requireContext())

        ///Config
        viewModel!!.progress.observe(requireActivity(), Observer { progress ->
                if (progress == View.VISIBLE) {
                    customProgressDialog!!.show()
                } else if (progress == View.GONE) {
                    customProgressDialog!!.dismiss()
                }
            })

        appPreferences?.saveInt(AppConstants.LAB_MASTER_TYPE_ID, 1)


        viewModel = DocumentViewModelFactory(
            requireActivity().application
        )
            .create(DocumentViewModel::class.java)
        binding?.viewModel = viewModel
        binding!!.lifecycleOwner = this

        utils = Utils(requireContext())

        binding?.previewLinearLayout?.setOnClickListener {
            if (binding?.resultLinearLayout?.visibility == View.VISIBLE) {
                binding?.resultLinearLayout?.visibility = View.GONE
            } else {
                binding?.resultLinearLayout?.visibility = View.VISIBLE
            }
        }
        viewModel?.getDocumentTypeList(facility_id!!, documentTypeResponseCallback)
        viewModel?.getAddDocumentTypeList(facility_id!!, addDocumentDeatilsRetrofitCallback)

        prepareDocumentLIstData()

        val layoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        binding?.documentRecyclerView!!.layoutManager = layoutmanager
        binding?.documentRecyclerView!!.adapter = mAdapter

        binding?.calendarEditText1?.setOnClickListener {
            showFromDatePickerDialog()
        }

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

                    val timePickerDialog = TimePickerDialog(
                        this.activity,
                        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->

                            binding?.calendarEditText?.setText(
                                String.format(
                                    "%02d",
                                    dayOfMonth
                                ) + "-" + String.format("%02d", monthOfYear + 1) + "-" + year +" "+ String.format(
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

                }, mYear, mMonth, mDay
            )
            //datePickerDialog?.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.show()
        }

        binding?.saveCardView?.setOnClickListener({

            val requestFile = RequestBody.create(
                MediaType.parse("multipart/form-data"),
                file
            ) //allow image and any other file
            // MultipartBody.Part is used to send also the actual file name
            val body =
                MultipartBody.Part.createFormData("upfile", file!!.name, requestFile)

            var comment= binding?.comments?.text?.toString()

            viewModel?.getUploadFile(facility_id!!, body,getDirectoryPath!!,"Fri May 08 2020 19:30:30 GMT+0530","1",comment!!,getDirectoryPath!!,
                encounter_uuid,patient_uuid,adduploadCallback)


        })

        fromCalenderDateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                fromCalender.set(Calendar.YEAR, year)
                fromCalender.set(Calendar.MONTH, monthOfYear)
                fromCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                setFromDate()
            }

        toCalenderDateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                toCalender.set(Calendar.YEAR, year)
                toCalender.set(Calendar.MONTH, monthOfYear)
                toCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                setToDate()
            }


        mAdapter?.setOnDeleteClickListener(object : DocumentAdapter.OnItemClickListener {

            override fun onItemParamClick(documentId: Attachment) {

                deletefavouriteID = documentId.uuid
                customdialog = Dialog(requireContext())
                customdialog!! .requestWindowFeature(Window.FEATURE_NO_TITLE)
                customdialog!! .setCancelable(false)
                customdialog!! .setContentView(R.layout.delete_cutsom_layout)
                val closeImageView = customdialog!! .findViewById(R.id.closeImageView) as ImageView

                closeImageView.setOnClickListener {
                    customdialog?.dismiss()
                }
                fileNmae = customdialog!! .findViewById(R.id.addDeleteName) as TextView
                fileNmae!!.text ="${fileNmae!!.text} '"+ documentId.attachment_name +"' Record ?"
                val yesBtn = customdialog!! .findViewById(R.id.yes) as CardView
                val noBtn = customdialog!! .findViewById(R.id.no) as CardView
                yesBtn.setOnClickListener {
                    viewModel!!.deleteAttachments(
                        facility_id,
                        deletefavouriteID,deleteRetrofitResponseCallback

                    )
                    customdialog!!.dismiss()


                }
                noBtn.setOnClickListener {
                    customdialog!! .dismiss()
                }
                customdialog!! .show()
            }

        })

        mAdapter?.setOnItemdowloadClickListener(object : DocumentAdapter.OnItemdownloadClickListener{
            override fun onItemParamClick(documentId: Attachment) {
                Log.i("",""+documentId)

                fileformat = documentId.file_path.substring(documentId.file_path.lastIndexOf(".") + 1) // Without dot jpg, png
                attacheddate = documentId.attached_date
                attachedname = documentId.attachment_name

                viewModel?.getDownload(documentId,facility_id,downloadimagecallback)
            }

        })

        binding?.edtFileuploadName?.setOnTouchListener(View.OnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding?.edtFileuploadName?.right!! - binding?.edtFileuploadName?.compoundDrawables
                        ?.get(DRAWABLE_RIGHT)?.bounds?.width()!!
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        runTimePermission()

                    } else {
                        val galleryIntent = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )
                        startActivityForResult(galleryIntent, 0)

                    }


                    return@OnTouchListener true
                }
            }
            false
        })

        return binding!!.root
    }


    private fun runTimePermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_READ_EXTERNAL_STORAGE
            )
            return
        }

        else{
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(galleryIntent, 0)
            return
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
            Log.i("", "" + grantResults)

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // now, you have permission go ahead
                val FileIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(FileIntent, 0)

            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    // now, user has denied permission (but not permanently!)
                    //Only denied
                    getCustomDialog()

                } else {
                    //Dontshowagain
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", requireActivity().packageName, null)
                    })
                }}}

    }

    private fun getCustomDialog() {
        // build alert dialog
        val dialogBuilder = AlertDialog.Builder(requireContext())
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

    private fun UploadFile(uri: Uri) {

    }

    private fun showToDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.TimePickerTheme,
            toCalenderDateSetListener,
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis
        datePickerDialog.show()
    }


    private fun showFromDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(), R.style.TimePickerTheme,
            fromCalenderDateSetListener, calendar[Calendar.YEAR], calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    private fun setFromDate() {
        val dateMonthAndYear =
            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        startDate = dateMonthAndYear.format(fromCalender.time)

        showToDatePickerDialog()
    }

    private fun setToDate() {
        val dateMonthAndYear =
            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        endDate = dateMonthAndYear.format(toCalender.time)
        binding?.calendarEditText1?.setText(startDate + "~" + endDate)
    }


    fun setDocumentType(responseContents: List<DocumentTypeResponseContent?>?) {
        typeList =
            responseContents?.map { it?.uuid!! to it.name }!!.toMap().toMutableMap()


        val adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            typeList.values.toMutableList()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.typeSpinner!!.adapter = adapter
        binding?.spinnerType!!.adapter = adapter

    }

    private fun prepareDocumentLIstData() {
        mAdapter = DocumentAdapter((requireActivity()), ArrayList())
        binding?.documentRecyclerView!!.adapter = mAdapter
    }

    val documentTypeResponseCallback = object : RetrofitCallback<DocumentTypeResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<DocumentTypeResponseModel>?) {

            selectPeriodUuid = responseBody?.body()?.responseContents?.get(0)!!.uuid
            setDocumentType(responseBody.body()?.responseContents)
        }

        override fun onBadRequest(errorBody: Response<DocumentTypeResponseModel>?) {
            val gson = GsonBuilder().create()
            val responseModel: DocumentTypeResponseModel
            try {
                responseModel = gson.fromJson(
                    errorBody!!.errorBody()!!.string(),
                    DocumentTypeResponseModel::class.java
                )
                utils?.showToast(
                    R.color.negativeToast,
                    binding?.mainLayout!!,
                    responseModel.req
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

        override fun onFailure(failure: String?) {
            utils?.showToast(R.color.negativeToast, binding?.mainLayout!!, failure!!)
        }

        override fun onEverytime() {
            viewModel!!.progress.value = 8
        }
    }
    val addDocumentDeatilsRetrofitCallback =
        object : RetrofitCallback<AddDocumentDetailsResponseModel> {
            override fun onSuccessfulResponse(response: Response<AddDocumentDetailsResponseModel>) {
                mAdapter?.refreshList(response.body()?.responseContents?.attachment as ArrayList<Attachment>?)
            }

            override fun onBadRequest(response: Response<AddDocumentDetailsResponseModel>) {
                val gson = GsonBuilder().create()
                val responseModel: AddDocumentDetailsResponseModel
                try {
                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        AddDocumentDetailsResponseModel::class.java
                    )
                    utils?.showToast(
                        R.color.negativeToast,
                        binding?.mainLayout!!,
                        response.message()
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


    var deleteRetrofitResponseCallback = object : RetrofitCallback<DeleteDocumentResponseModel> {
        override fun onSuccessfulResponse(responseBody: Response<DeleteDocumentResponseModel>?) {
            var response = responseBody?.body()?.message
            viewModel?.getAddDocumentTypeList(facility_id!!, addDocumentDeatilsRetrofitCallback)
//            mAdapter?.clearadapter()

            utils?.showToast(
                R.color.positiveToast,
                binding?.mainLayout!!,
                responseBody?.body()?.message!!
            )
        }

        override fun onBadRequest(errorBody: Response<DeleteDocumentResponseModel>?) {

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

        }
    }


    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
                // When an Image is picked
        if (requestCode == 0 && resultCode == RESULT_OK && null != data) {
            // Get the Image from data
            selectedImagePath = data.data
            // use the FileUtils to get the actual file by uri
            file = File(getPathname(selectedImagePath))
            getDirectoryPath = file!!.parent
            Log.i("", "" + selectedImagePath)
            val strFileName: String = file!!.name
            val fileuploadname: String = selectedImagePath.toString()
            val fileuploadName: String = fileuploadname.substring(fileuploadname.lastIndexOf("/") + 1)
            Log.i("",""+strFileName)
            Log.i("",""+fileuploadName)
            binding?.editfilename?.setText(strFileName)
            binding?.edtFileuploadName?.setText(fileuploadName)

        }// When an Video is picked
        else if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
/*
                // Get the Video from data
                val selectedVideo = data.data
                val filePathColumn =
                    arrayOf(MediaStore.Video.Media.DATA)
                val cursor: Cursor =
                    getContentResolver().query(selectedVideo, filePathColumn, null, null, null)!!
                cursor.moveToFirst()
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                mediaPath1 = cursor.getString(columnIndex)
                str2.setText(mediaPath1)
                // Set the Video Thumb in ImageView Previewing the Media
                imgView.setImageBitmap(
                    getThumbnailPathForLocalFile(
                        this@MainActivity,
                        selectedVideo
                    )
                )
                cursor.close()*/
        } else {
//                Toast.makeText(this, "You haven't picked Image/Video", Toast.LENGTH_LONG).show()
        }
    }



    private fun getPathname(selectedImagePath: Uri?): String {
        val data = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(this!!.context!!, selectedImagePath!!, data, null, null, null)
        val cursor: Cursor = loader.loadInBackground()!!
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)

    }


    val downloadimagecallback =
        object : RetrofitCallback<ResponseBody> {
            override fun onSuccessfulResponse(response: Response<ResponseBody>) {
                Log.i("","message")

                downloadZipFileTask = DownloadZipFileTask()
                downloadZipFileTask!!.execute(response.body())

            }

            override fun onBadRequest(response: Response<ResponseBody>) {
                val gson = GsonBuilder().create()
                val responseModel: AddDocumentDetailsResponseModel
                try {
                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        AddDocumentDetailsResponseModel::class.java
                    )
                    utils?.showToast(
                        R.color.negativeToast,
                        binding?.mainLayout!!,
                        response.message()
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

    inner class DownloadZipFileTask :
        AsyncTask<ResponseBody?, Pair<Int?, Long?>?, String?>() {

        fun doProgress(progressDetails: Pair<Int?, Long?>?) {
            publishProgress(progressDetails)
        }

        override fun onPostExecute(result: String?) {
            // binding?.progressbar!!.setVisibility(View.GONE);

            Toast.makeText(
                requireContext(),
                "Storage path: $destinationFile", Toast.LENGTH_LONG
            ).show()
        }
        override fun doInBackground(vararg params: ResponseBody?): String? {

            saveToDisk(params[0]!!, "${attachedname} ${attacheddate}."+fileformat)

            return null
        }}

    private fun saveToDisk(body: ResponseBody, filename: String) {
        try {
            destinationFile = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
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

    val adduploadCallback =
        object : RetrofitCallback<FileUploadResponseModel> {
            override fun onSuccessfulResponse(response: Response<FileUploadResponseModel>) {
                Log.i("","message")
            }

            override fun onBadRequest(response: Response<FileUploadResponseModel>) {
                val gson = GsonBuilder().create()
                val responseModel: AddDocumentDetailsResponseModel
                try {
                    responseModel = gson.fromJson(
                        response.errorBody()!!.string(),
                        AddDocumentDetailsResponseModel::class.java
                    )
                    utils?.showToast(
                        R.color.negativeToast,
                        binding?.mainLayout!!,
                        response.message()
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


}





