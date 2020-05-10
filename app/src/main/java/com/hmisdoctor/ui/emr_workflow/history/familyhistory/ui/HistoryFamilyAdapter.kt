package com.hmisdoctor.ui.emr_workflow.history.familyhistory.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.history.allergy.ui.AllergyAdapter
import com.hmisdoctor.ui.emr_workflow.history.familyhistory.model.FamilyHistoryResponseContent

class HistoryFamilyAdapter(
    val context: Context,
    private var FamilyarrayList: List<FamilyHistoryResponseContent>?

) :
    RecyclerView.Adapter<HistoryFamilyAdapter.MyViewHolder>() {

    private var onClickListener: OnClickListener? = null

    inner class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val serialNumber: TextView = itemView.findViewById(R.id.serialNumber)
        internal val dateTimeTxt: TextView = itemView.findViewById(R.id.dateTextView)
        internal val typeTxt: TextView = itemView.findViewById(R.id.typeTextView)
        internal val diseaseName: TextView = itemView.findViewById(R.id.diseaseName)
        internal val durationTextView: TextView = itemView.findViewById(R.id.durationTextView)
        internal val mainLinearLayout: LinearLayout = itemView.findViewById(R.id.mainLinearLayout)
        internal val addNewFamilyIconView: ImageView = itemView.findViewById(R.id.addNewFamilyIconView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_history_family, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return FamilyarrayList?.size!!
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val responseContent = FamilyarrayList!!.get(position)!!
        val pos = position+1
        holder.serialNumber.text = pos.toString()
        holder.dateTimeTxt.setText(responseContent.created_date)
        holder.typeTxt.setText(responseContent.family_relation_type?.name)
        holder.diseaseName.setText(responseContent.disease_name)
        holder.durationTextView.setText(responseContent.duration.toString()+responseContent.periods.name)

        holder.addNewFamilyIconView.setOnClickListener {
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

    fun setData(responseContent: List<FamilyHistoryResponseContent>?) {

        this.FamilyarrayList= responseContent

        notifyDataSetChanged()
    }

    interface OnClickListener {
        fun OnClick(
            responseContent: FamilyHistoryResponseContent?,
            position: Int
        )
    }

    fun setOnClickListener(onclick: OnClickListener) {
        this.onClickListener = onclick
    }

}



