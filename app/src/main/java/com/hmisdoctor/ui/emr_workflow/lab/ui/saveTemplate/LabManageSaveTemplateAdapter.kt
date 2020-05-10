package com.hmisdoctor.ui.emr_workflow.lab.ui.saveTemplate

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.lab.model.favresponse.ResponseContentsfav
import com.hmisdoctor.ui.emr_workflow.lab.model.updaterequest.RemovedDetail

class LabManageSaveTemplateAdapter(context: Context, private var arrayListLabFavList: ArrayList<ResponseContentsfav?>) : RecyclerView.Adapter<LabManageSaveTemplateAdapter.MyViewHolder>() {
    private val mLayoutInflater: LayoutInflater
    private var mContext: Context
    var orderNumString: String? = null
    private var onDeleteClickListener: OnDeleteClickListener? = null
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textName: TextView
/*
        var textcode: TextView
*/
        var serialNumber: TextView
        var displayOrder: TextView

        var deleteImage : ImageView

        init {
            serialNumber = view.findViewById<View>(R.id.serialNumberTextView) as TextView
            textName = view.findViewById<View>(R.id.textName) as TextView
            displayOrder = view.findViewById<View>(R.id.textdisplayOrder) as TextView
            deleteImage = view.findViewById<View>(R.id.deleteImageView) as ImageView

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemLayout = LayoutInflater.from(mContext).inflate(R.layout.row_manage_save_template_lab, parent, false) as ConstraintLayout
        return MyViewHolder(itemLayout)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        orderNumString = arrayListLabFavList[position].toString()
        val list = arrayListLabFavList[position]
        holder.serialNumber.text = (position + 1).toString()
        holder.textName.text = list!!.test_master_name
        holder.displayOrder.text = list.test_master_code.toString()

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
            responseData : ResponseContentsfav?,
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

    fun delete(position: Int) {
/*

        for (i in arrayListLabFavList.indices)
        {
            if(arrayListLabFavList.get(i)?.test_master_id?.equals(arraylistresponse.template_uuid!!)!!)
            {
                this.arrayListLabFavList.removeAt(i)
                notifyItemRemoved(i);
                break
            }

        }
*/


        arrayListLabFavList?.removeAt(position)
        notifyItemRemoved(position)

    }

    fun setAddItem(responseContantSave: ResponseContentsfav) {


        val check= arrayListLabFavList.any{ it!!.test_master_id == responseContantSave?.test_master_id}

        if (!check) {
            arrayListLabFavList.add(responseContantSave)

            notifyDataSetChanged()

        }
        else{

            Toast.makeText(this!!.mContext,"Already Item available in the list", Toast.LENGTH_LONG)?.show()

        }

    }


    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }
}


