package com.hmisdoctor.ui.emr_workflow.chief_complaint.ui.chiefcomplaintadddialog.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.chief_complaint.ui.chiefcomplaintadddialog.model.addFavlistDataModel


class ChiefManageFavAdapter(
    private val context: Activity,
    var arrayListLabFavList: ArrayList<addFavlistDataModel?>
): RecyclerView.Adapter<ChiefManageFavAdapter.MyViewHolder>(){
    private var onDeleteClickListener: OnDeleteClickListener? = null
    inner class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val serialNumberTextView: TextView = itemView.findViewById(R.id.sNoTextView)
        internal val deleteImageView: ImageView = itemView.findViewById(R.id.deleteImageView)
        internal val testNameTextView: TextView = itemView.findViewById(R.id.textName)
        internal val mainLinearLayout: LinearLayout = itemView.findViewById(R.id.mainLinearLayout)
        internal val displayORder: TextView = itemView.findViewById(R.id.displayOrderTextView)
    }

    @SuppressLint("NewApi")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChiefManageFavAdapter.MyViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.row_chief_addfav_list, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return arrayListLabFavList?.size
    }


    override fun onBindViewHolder(holder: ChiefManageFavAdapter.MyViewHolder, position: Int) {

        if(arrayListLabFavList.size!=0) {
            val response = arrayListLabFavList!![position]
            /*holder.serialNumberTextView.text = (position + 1).toString()
        holder?.testNameTextView?.text = response?.test_master_name
        holder?.displayORder?.text = response?.favourite_display_order?.toString()*/
            holder.serialNumberTextView.text = (position + 1).toString()
            holder.testNameTextView?.text = response!!.chiefComplaintName
            holder.displayORder.text = response.displayOrder.toString()

            holder.deleteImageView.setOnClickListener({
                onDeleteClickListener?.onDeleteClick(response.chiefComplaintId,response.chiefComplaintName)
            })
        }
        else{
        }
    }

    /*
Delete
*/
    interface OnDeleteClickListener {
        fun onDeleteClick(
            favouritesID: Int?,
            chiefComplaintName: String
        )
    }

    fun setOnDeleteClickListener(onDeleteClickListener: OnDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener
    }

    fun setFavAddItem(responseContents: addFavlistDataModel?) {

        arrayListLabFavList.add(responseContents)
        notifyDataSetChanged()
    }

    fun refreshList(favouritesModel: ArrayList<addFavlistDataModel?>?) {

        this.arrayListLabFavList = favouritesModel!!
            // this.filter = favouritesModel

        notifyDataSetChanged()

    }

    fun addRow(data: addFavlistDataModel) {

        this.arrayListLabFavList.add(data)
         notifyDataSetChanged()

    }

    fun clearadapter() {
        this?.arrayListLabFavList?.clear()
        notifyDataSetChanged()
    }

}