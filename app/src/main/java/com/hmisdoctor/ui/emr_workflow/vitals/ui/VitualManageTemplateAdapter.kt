package com.hmisdoctor.ui.emr_workflow.vitals.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.lab.model.favresponse.ResponseContentsfav

class VitualManageTemplateAdapter(context: Context, private var arrayListVitualFavList: ArrayList<ResponseContentsfav?>) : RecyclerView.Adapter<VitualManageTemplateAdapter.MyViewHolder>() {
    private val mLayoutInflater: LayoutInflater
    private var mContext: Context
    var orderNumString: String? = null
    private var onDeleteClickListener: OnDeleteClickListener? = null
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textName: TextView
        var textdisplayORder: TextView
        var serialNumber: TextView
        var deleteImage : ImageView

        init {
            serialNumber = view.findViewById<View>(R.id.serialNumberTextView) as TextView
            textName = view.findViewById<View>(R.id.textName) as TextView
            textdisplayORder = view.findViewById<View>(R.id.textdisplayORder) as TextView
            deleteImage = view.findViewById<View>(R.id.deleteImageView) as ImageView

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemLayout = LayoutInflater.from(mContext).inflate(R.layout.row_manage_template_vital, parent, false) as LinearLayout
        return MyViewHolder(itemLayout)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        orderNumString = arrayListVitualFavList[position].toString()
        val list = arrayListVitualFavList[position]
        holder.serialNumber.text = (position + 1).toString()
        holder.textName.text = list!!.vital_master_name
        holder.textdisplayORder.text = list.favourite_display_order?.toString()
        holder.deleteImage.setOnClickListener({

            onDeleteClickListener?.onDeleteClick(list,position)
//            arrayListVitualFavList.removeAt(position)
//            notifyItemRemoved(position)

        })

    }
    override fun getItemCount(): Int {
        return arrayListVitualFavList.size
    }
    fun setFavAddItem(responseContantSave: ArrayList<ResponseContentsfav?>) {
        arrayListVitualFavList = responseContantSave
        notifyDataSetChanged()
    }


    fun getItems(): ArrayList<ResponseContentsfav?> {

        return arrayListVitualFavList

    }


    /*
Delete
*/
    interface OnDeleteClickListener {
        fun onDeleteClick(
            responseData: ResponseContentsfav?,
            position: Int
        )
    }

    fun setOnDeleteClickListener(onDeleteClickListener: OnDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener
    }

    fun cleardata() {
        arrayListVitualFavList.clear()
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        arrayListVitualFavList.removeAt(position)
        notifyItemRemoved(position)

    }


    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }
}


