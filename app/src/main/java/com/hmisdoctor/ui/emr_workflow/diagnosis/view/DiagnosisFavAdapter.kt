package com.hmisdoctor.ui.emr_workflow.diagnosis.view

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
import com.hmisdoctor.ui.emr_workflow.diagnosis.model.addFavlistDiagonosisDataModel


class DiagnosisFavAdapter(
    private val context: Activity,
    var arrayListLabFavList: ArrayList<addFavlistDiagonosisDataModel?>
): RecyclerView.Adapter<DiagnosisFavAdapter.MyViewHolder>(){
    private var onDeleteClickListener: OnDeleteClickListener? = null
    inner class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val serialNumberTextView: TextView = itemView.findViewById(R.id.sNoTextView)
        internal val deleteImageView: ImageView = itemView.findViewById(R.id.deleteImageView)
        internal val testNameTextView: TextView = itemView.findViewById(R.id.textName)
        internal val mainLinearLayout: LinearLayout = itemView.findViewById(R.id.mainLinearLayout)
        internal val displayORder: TextView = itemView.findViewById(R.id.displayOrderTextView)
    }

    @SuppressLint("NewApi")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiagnosisFavAdapter.MyViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.row_diagnosis_addfav_list, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return arrayListLabFavList?.size
    }


    override fun onBindViewHolder(holder: DiagnosisFavAdapter.MyViewHolder, position: Int) {

        if(arrayListLabFavList.size!=0) {
            val response = arrayListLabFavList!![position]
            /*holder.serialNumberTextView.text = (position + 1).toString()
        holder?.testNameTextView?.text = response?.test_master_name
        holder?.displayORder?.text = response?.favourite_display_order?.toString()*/
            holder.serialNumberTextView.text = (position + 1).toString()
            holder.testNameTextView?.text = response!!.diagonosisname
            holder.displayORder?.text = response!!.display_order.toString()

            holder.deleteImageView.setOnClickListener({
                onDeleteClickListener?.onDeleteClick(response?.diagonosis_id!!.toInt(),response?.diagonosisname)
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
            diagonosisname: String
        )
    }

    fun setOnDeleteClickListener(onDeleteClickListener: OnDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener
    }

    fun setFavAddItem(responseContents: addFavlistDiagonosisDataModel?) {

        arrayListLabFavList.add(responseContents)
        notifyDataSetChanged()
    }

    fun refreshList(favouritesModel: ArrayList<addFavlistDiagonosisDataModel?>?) {

        this.arrayListLabFavList = favouritesModel!!
            // this.filter = favouritesModel

        notifyDataSetChanged()

    }

    fun addRow(data: addFavlistDiagonosisDataModel) {

        this.arrayListLabFavList.add(data)

        notifyDataSetChanged()

    }

    fun clearadapter() {
        this?.arrayListLabFavList?.clear()
        notifyDataSetChanged()
    }

}