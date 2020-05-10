package com.hmisdoctor.utils


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.hmisdoctor.R
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddAllDepatResponseContent
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Stream
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.reflect.KProperty1

class Utils(val context: Context) {
    companion object {


        fun isNetworkConnected(application: Context): Boolean {
            val cm =
                application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT < 23) {
                val ni = cm.activeNetworkInfo
                if (ni != null) {
                    return ni.isConnected && (ni.type == ConnectivityManager.TYPE_WIFI || ni.type == ConnectivityManager.TYPE_MOBILE)
                }
            } else {
                val n = cm.activeNetwork
                if (n != null) {
                    val nc = cm.getNetworkCapabilities(n)
                    return nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI
                    ) || nc.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                }
            }

            return false
        }

        /**
         * Expand View
         *
         * @param v
         */


         fun expand(horizontalScrollView: View) {


            val matchParentMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(
                    (horizontalScrollView.parent as View).width,
                    View.MeasureSpec.EXACTLY
                )
            val wrapContentMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            horizontalScrollView.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
            val targetHeight = horizontalScrollView.measuredHeight
            horizontalScrollView.layoutParams.height = 1
            horizontalScrollView.visibility = View.VISIBLE
            val a = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    horizontalScrollView.layoutParams.height = if (interpolatedTime == 1f)
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    else
                        (targetHeight * interpolatedTime).toInt()
                    horizontalScrollView.requestLayout()
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }
            a.duration =
                (targetHeight / horizontalScrollView.context.resources.displayMetrics.density).toInt()
                    .toLong()
            horizontalScrollView.startAnimation(a)
        }


        /**
         * Collapsing Views
         *
         * @param v
         */
        public fun collapse(v: View) {
            val initialHeight = v.measuredHeight
            val a = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    if (interpolatedTime == 1f) {
                        v.visibility = View.GONE
                    } else {
                        v.layoutParams.height =
                            initialHeight - (initialHeight * interpolatedTime).toInt()
                        v.requestLayout()
                    }
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }
            a.duration =
                (initialHeight / v.context.resources.displayMetrics.density).toInt().toLong()
            v.startAnimation(a)
        }


        @SuppressLint("SimpleDateFormat")
        fun parseDate(
            inputDateString: String, inputDateFormat: String,
            outputDateFormat: String
        ): String? {
            val date: Date?
            var outputDateString: String? = ""
            try {
                date = SimpleDateFormat(inputDateFormat).parse(inputDateString)
                outputDateString = SimpleDateFormat(outputDateFormat).format(date!!)
            } catch (e: java.text.ParseException) {
                e.printStackTrace()
            }
            return outputDateString
        }


        fun encrypt(value: String?): String {
            var encode: String? = null
        val iv = IvParameterSpec(AppConstants.IV.toByteArray(Charsets.UTF_8))

        val skeySpec = SecretKeySpec(AppConstants.ENCRYPT_KEY.toByteArray(Charsets.UTF_8),AppConstants.ALGORITHAM)

        val cipher = Cipher.getInstance(AppConstants.AES_SELECTED_MODE)
        cipher.init(Cipher.ENCRYPT_MODE,skeySpec,iv)
        val encrypted = cipher?.doFinal(value?.toByteArray())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                encode = Base64.getEncoder().encodeToString(encrypted);
            } else {
                encode = android.util.Base64.encodeToString(encrypted, android.util.Base64.DEFAULT);
                encode = encode.replace("\n","");

            }
            Log.i("",""+encode)
            return encode!!}}

    fun showToast(color: Int, view: View, message: String) {
        val rect = Rect()
        view.getLocalVisibleRect(rect)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.custom_toast, null)
        val customToastLayout = layout.findViewById(R.id.customToastLayout) as LinearLayout
        customToastLayout.setBackgroundColor(ContextCompat.getColor(context, color))
        val toastMessageTextView = layout.findViewById(R.id.toastMessageTextView) as TextView
        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, rect.left, rect.top)
        toast.view = layout
        toastMessageTextView.text = message
        toast.show()
    }

    fun getAgeMonth(DOBMonth: Int): String {
        var age: Int
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")

        val c = Calendar.getInstance()

        c.time = Date()

        c.add(Calendar.MONTH, -DOBMonth)

        val d = c.time
        val res = format.format(d)


        return res
    }

    fun getAgedayDifferent(numOfDaysAgo: Int): String {
        var age: Int
        val format = SimpleDateFormat("yyyy-MM-dd")

        val c = Calendar.getInstance()
        c.time = Date()
        c.add(Calendar.DAY_OF_YEAR, -1 * numOfDaysAgo)
        val d = c.time
        val res = format.format(d)
        return res
    }

    fun getDateDaysAgo(numOfDaysAgo: Int): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        val c = Calendar.getInstance()
        c.time = Date()
        c.add(Calendar.DAY_OF_YEAR, -1 * numOfDaysAgo)
        val d = c.time
        val res = format.format(d)
        return res
    }

    fun getYear(numOfDaysAgo: Int): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        val c = Calendar.getInstance()
        c.time = Date()
        c.add(Calendar.YEAR, -numOfDaysAgo)
        val d = c.time
        val res = format.format(d)
        return res
    }



    @RequiresApi(Build.VERSION_CODES.N)
    fun containsName(list : List<FavAddAllDepatResponseContent>, name : String): Stream<KProperty1<FavAddAllDepatResponseContent, Int>>? {
    return list.stream().map { FavAddAllDepatResponseContent::uuid }.filter(name::equals)
}


}








