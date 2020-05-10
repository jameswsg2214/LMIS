package com.hmisdoctor.ui.emr_workflow.history.diagnosis.view

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.history.diagnosis.model.HistoryDiagnosisresponseContent
import com.hmisdoctor.ui.emr_workflow.history.familyhistory.ui.HistoryFamilyAdapter
import java.text.SimpleDateFormat

class HistoryDiagnosisAdapter(val context: Activity, private var diagnasisArrayList: ArrayList<HistoryDiagnosisresponseContent>) : RecyclerView.Adapter<HistoryDiagnosisAdapter.MyViewHolder>() {
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryDiagnosisAdapter.MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_history_diagnosis_list, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return diagnasisArrayList?.size!!
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val responseContent = diagnasisArrayList!![position]!!

        val pos = position+1

        holder.serialNumber.text = pos.toString()


       /* var spf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val date = spf.parse(responseContent.diagnosis_created_date)
        spf = SimpleDateFormat("dd MMM yyyy hh:mm")
        holder.dateTimeTxt.text = spf.format(date!!)*/

        holder.dateTimeTxt.text = responseContent.diagnosis_created_date

        if(responseContent.encounter_type_id == 1){
            holder.typeTxt.setText("OP")
        }else if(responseContent.encounter_type_id == 2){
            holder.typeTxt.setText("IP")
        }

        holder.diagnosisName.text = responseContent.diagnosis_name
        holder.remarks.text = responseContent.diagnosis_comments
        holder.edit.setOnClickListener {

            onClickListener?.OnClick(responseContent, position)
        }

        if (position % 2 == 0) {
            holder.mainLinearLayout.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
        } else {
            holder.mainLinearLayout.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.alternateRow
                )
            )
        }

    }

    inner class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal val serialNumber: TextView = itemView.findViewById(R.id.tvS_no)
        internal val dateTimeTxt: TextView = itemView.findViewById(R.id.tvDiagnosisDateTime)
        internal val typeTxt: TextView = itemView.findViewById(R.id.tvEncoder)
        internal val diagnosisName: TextView = itemView.findViewById(R.id.tvDiagnosisName)
        internal val remarks: TextView = itemView.findViewById(R.id.tvDiagnosisRemarks)
        internal val edit: ImageView = itemView.findViewById(R.id.editDiagnosis)
        internal val mainLinearLayout: LinearLayout = itemView.findViewById(R.id.mainLinearLayout)
    }


    interface OnClickListener {
        fun OnClick(
            responseContent: HistoryDiagnosisresponseContent?,
            position: Int
        )
    }

    fun setOnClickListener(onclick: OnClickListener) {
        this.onClickListener = onclick
    }


    fun pushAll(listAllData: ArrayList<HistoryDiagnosisresponseContent>?){

        this.diagnasisArrayList= listAllData!!

        notifyDataSetChanged()

    }

    fun addRow(rowData: HistoryDiagnosisresponseContent){

        this.diagnasisArrayList!!.add(rowData)

        notifyDataSetChanged()

    }
}