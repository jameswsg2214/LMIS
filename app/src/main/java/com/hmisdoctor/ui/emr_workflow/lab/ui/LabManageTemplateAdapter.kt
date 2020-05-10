package com.hmisdoctor.ui.emr_workflow.lab.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.lab.model.favresponse.ResponseContentsfav

class LabManageTemplateAdapter(context: Context, private var arrayListLabFavList: ArrayList<ResponseContentsfav?>) : RecyclerView.Adapter<LabManageTemplateAdapter.MyViewHolder>() {
    private val mLayoutInflater: LayoutInflater
    private var mContext: Context
    var orderNumString: String? = null
    private var onDeleteClickListener: OnDeleteClickListener? = null
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textName: TextView
        var textcode: TextView
        var serialNumber: TextView

        var deleteImage : ImageView

        init {
            serialNumber = view.findViewById<View>(R.id.serialNumberTextView) as TextView
            textName = view.findViewById<View>(R.id.textNames) as TextView
            textcode = view.findViewById<View>(R.id.codesTextView) as TextView
            deleteImage = view.findViewById<View>(R.id.deleteImageView) as ImageView

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemLayout = LayoutInflater.from(mContext).inflate(R.layout.row_manage_template_lab, parent, false) as ConstraintLayout
        return MyViewHolder(itemLayout)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        orderNumString = arrayListLabFavList[position].toString()
        val list = arrayListLabFavList[position]
        holder.serialNumber.text = (position + 1).toString()
        holder.textName.text = list!!.test_master_name
        holder.textcode.text = list.test_master_code
        holder.deleteImage.setOnClickListener({

            onDeleteClickListener?.onDeleteClick(list,position)


        })

    }
    override fun getItemCount(): Int {
        return arrayListLabFavList.size
    }
    fun setFavAddItem(responseContantSave: ArrayList<ResponseContentsfav?>) {
        arrayListLabFavList = responseContantSave
        notifyDataSetChanged()
    }


    fun getItems(): ArrayList<ResponseContentsfav?> {

        return arrayListLabFavList

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
        arrayListLabFavList.clear()
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        arrayListLabFavList.removeAt(position)
      notifyDataSetChanged()
    }


    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }
}


