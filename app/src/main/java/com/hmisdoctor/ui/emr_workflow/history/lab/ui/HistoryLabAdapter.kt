package com.hmisdoctor.ui.emr_workflow.history.lab.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.view.lab.model.PrevLabResponseContent
import java.lang.Exception

class HistoryLabAdapter(private val context: Context,
                        private var historyLabArrayList: List<PrevLabResponseContent?>) :
    RecyclerView.Adapter<HistoryLabAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_history_lab, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return historyLabArrayList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        try {
            val responseData = historyLabArrayList?.get(position);
            holder.historyLabSerialno.text = (position + 1).toString()
            holder.historyLabDate.text = responseData?.created_date
            holder.historyLabCode.text = responseData?.pod_arr_result!![0].code
            holder.historyLabName.text = responseData.pod_arr_result[0].name
            holder.historyLabType.text = responseData.pod_arr_result[0].type
            holder.historyLabOrderLocation.text = responseData.pod_arr_result[0].order_to_location
        }catch (e: Exception){

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


    fun setData(responseContents: List<PrevLabResponseContent>?) {
        historyLabArrayList = responseContents!!
        notifyDataSetChanged()
    }

    inner class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val historyLabSerialno: TextView = itemView.findViewById(R.id.historyLabSerialno)
        internal val historyLabDate: TextView = itemView.findViewById(R.id.historyLabDate)
        internal val historyLabCode: TextView = itemView.findViewById(R.id.historyLabCode)
        internal val historyLabName: TextView = itemView.findViewById(R.id.historyLabName)
        internal val historyLabType: TextView = itemView.findViewById(R.id.historyLabType)
        internal val historyLabOrderLocation: TextView = itemView.findViewById(R.id.historyLabOrderLocation)
        internal val mainLinearLayout: LinearLayout = itemView.findViewById(R.id.mainLayout)
    }

}