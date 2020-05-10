package com.hmisdoctor.ui.emr_workflow.history.radiology.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.view.lab.model.PrevLabResponseContent
import com.hmisdoctor.ui.emr_workflow.view.lab.model.PrevLabResponseModel
import com.hmisdoctor.ui.login.model.login_response_model.Activity

class HistoryRadiologyAdapter(private val context: Context,
                              private var historyRadiologyArrayList: List<PrevLabResponseContent?>) :
    RecyclerView.Adapter<HistoryRadiologyAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_history_radiology, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return historyRadiologyArrayList.size
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.historyRadioSerialno.text = (position+1).toString()
        holder.historyRadioDate.text = (historyRadiologyArrayList.get(position)?.created_date)
        holder.historyRadioCode.text = (historyRadiologyArrayList[position]?.pod_arr_result!![0].code)
        holder.historyRadioName.text = (historyRadiologyArrayList[position]?.pod_arr_result!![0].name)
        holder.historyRadioType.text = (historyRadiologyArrayList[position]?.pod_arr_result!![0].type)
        holder.historyRadioOrderLocation.text = (historyRadiologyArrayList[position]?.pod_arr_result!![0].order_to_location)


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
        historyRadiologyArrayList = responseContents!!
        notifyDataSetChanged()
    }

    inner class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val historyRadioSerialno: TextView = itemView.findViewById(R.id.historyRadioSerialno)
        internal val historyRadioDate: TextView = itemView.findViewById(R.id.historyRadioDate)
        internal val historyRadioCode: TextView = itemView.findViewById(R.id.historyRadioCode)
        internal val historyRadioName: TextView = itemView.findViewById(R.id.historyRadioName)
        internal val historyRadioType: TextView = itemView.findViewById(R.id.historyRadioType)
        internal val historyRadioOrderLocation: TextView = itemView.findViewById(R.id.historyRadioOrderLocation)
        internal val mainLinearLayout: LinearLayout = itemView.findViewById(R.id.mainLayout)
    }

}