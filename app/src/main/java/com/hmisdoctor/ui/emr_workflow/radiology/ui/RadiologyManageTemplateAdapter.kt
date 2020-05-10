package com.hmisdoctor.ui.emr_workflow.radiology.ui

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

class RadiologyManageTemplateAdapter(context: Context, private var arrayListLabFavList: ArrayList<ResponseContentsfav?>) : RecyclerView.Adapter<RadiologyManageTemplateAdapter.MyViewHolder>() {
    private val mLayoutInflater: LayoutInflater
    private var mContext: Context
    var orderNumString: String? = null

    private  var onItemClickListener:OnItemClickListener?=null

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textName: TextView
        var serialNumber: TextView
        var deleteImage : ImageView
        var code: TextView
        var layout:ConstraintLayout

        init {
            serialNumber = view.findViewById<View>(R.id.serialNumberTextView) as TextView
            textName = view.findViewById<View>(R.id.textNames) as TextView
            code = view.findViewById<View>(R.id.codesTextView) as TextView
            deleteImage = view.findViewById<View>(R.id.deleteImageView) as ImageView
            layout = view.findViewById<View>(R.id.mainLinearLayout) as ConstraintLayout


        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemLayout = LayoutInflater.from(mContext).inflate(R.layout.row_manage_templates_radiology, parent, false) as ConstraintLayout
        return MyViewHolder(itemLayout)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        orderNumString = arrayListLabFavList[position].toString()
        val list = arrayListLabFavList[position]
        holder.serialNumber.text = (position + 1).toString()
        holder.textName.text = list!!.test_master_name
        holder.code.text = list.test_master_code
        holder.deleteImage.setOnClickListener {
            arrayListLabFavList.removeAt(position)
            notifyItemChanged(position)
            notifyDataSetChanged()

        }

        holder.layout.setOnClickListener {

            onItemClickListener!!.onItemClick(arrayListLabFavList[position],position)


        }
    }


    interface OnItemClickListener {
        fun onItemClick(
                responseContent: ResponseContentsfav?,
                position: Int
        )
    }

    fun setOnItemClickListener(onItemClickListener1: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener1
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

    fun cleardata() {
        arrayListLabFavList.clear()
        notifyDataSetChanged()
    }


    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }
}


