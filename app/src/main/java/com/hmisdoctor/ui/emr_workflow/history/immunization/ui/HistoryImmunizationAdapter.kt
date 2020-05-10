package com.hmisdoctor.ui.emr_workflow.history.immunization.ui

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
import com.hmisdoctor.ui.emr_workflow.history.immunization.model.GetImmunizationresponseContent
import com.hmisdoctor.ui.emr_workflow.history.surgery.ui.HistorySurgeryAdapter

class HistoryImmunizationAdapter(
    val context: Context,
    private var ImmunizationarrayList: List<GetImmunizationresponseContent>?

) :
    RecyclerView.Adapter<HistoryImmunizationAdapter.MyViewHolder>() {

    private var onClickListener: OnClickListener? = null

    inner class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val serialNumberTextView: TextView = itemView.findViewById(R.id.serialNumberTextView)
        internal val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        internal val typeTextView: TextView = itemView.findViewById(R.id.typeTextView)
        internal val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        internal val institutionTextView: TextView = itemView.findViewById(R.id.institutionTextView)
        internal val remarksTextView: TextView = itemView.findViewById(R.id.remarksTextView)
        internal val mainLinearLayout: LinearLayout = itemView.findViewById(R.id.mainLinearLayout)
        internal val actionImageView: ImageView = itemView.findViewById(R.id.deleteImageView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_immunization, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ImmunizationarrayList?.size!!
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val responseContent = ImmunizationarrayList!!.get(position)!!
        val pos = position+1
        holder.serialNumberTextView.text = pos.toString()
        holder.dateTextView.setText(responseContent.pis_immunization_date)
        holder.nameTextView.setText(responseContent.i_name)
        holder.typeTextView.setText(responseContent.et_name)
        holder.institutionTextView.setText(responseContent.f_name)
        holder.remarksTextView.setText(responseContent.pis_comments)


        holder.actionImageView.setOnClickListener {

            onClickListener!!.OnClick(responseContent,position)
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

    fun setData(responseContents: List<GetImmunizationresponseContent?>?) {
        ImmunizationarrayList = responseContents as List<GetImmunizationresponseContent>?
        notifyDataSetChanged()
    }

    interface OnClickListener {
        fun OnClick(
            responseContent: GetImmunizationresponseContent?,
            position: Int
        )
    }

    fun setOnClickListener(onclick: OnClickListener) {
        this.onClickListener = onclick
    }

}



