package com.hmisdoctor.ui.emr_workflow.lab_result.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.lab_result.model.LabResultGetByDataResponseContent
import kotlin.collections.ArrayList


class LabResultByDateAdapter(
    private val mContext: Context,
    private var LabResultByDateList: List<LabResultGetByDataResponseContent>?

    ) : RecyclerView.Adapter<LabResultByDateAdapter.MyViewHolder>() {
    private val mLayoutInflater: LayoutInflater


    init {
        this.mLayoutInflater = LayoutInflater.from(mContext) }


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var sNoTextView: TextView
        var resultTextView: TextView
        var observationTextView: TextView
        var uomTextView: TextView
        var referenceRange: TextView


        init {

            sNoTextView = view.findViewById<View>(R.id.sNoTextView) as TextView
            resultTextView = view.findViewById<View>(R.id.resultTextView) as TextView
            observationTextView = view.findViewById<View>(R.id.observationTextView) as TextView
            uomTextView = view.findViewById<View>(R.id.uomTextView) as TextView
            referenceRange = view.findViewById<View>(R.id.referenceRange) as TextView

        }
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): MyViewHolder {
        val itemLayout = LayoutInflater.from(mContext).inflate(
            R.layout.lab_result_by_date_row,
            viewGroup,
            false
        ) as LinearLayout
        return MyViewHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val prevList = LabResultByDateList?.get(position)
        var pos = position+1
        holder.sNoTextView.text = pos.toString()
        holder.resultTextView.text = prevList?.order_request_date?.result_value as CharSequence?
        holder.observationTextView.text = prevList?.order_request_date?.test_or_analyte
        holder.referenceRange.text = prevList?.test_or_analyte_ref_min.toString()+"-"+prevList?.test_or_analyte_ref_max.toString()
        holder.uomTextView.text = prevList?.order_request_date?.analyte_uom_name.toString()


    }

    override fun getItemCount(): Int {
        return LabResultByDateList?.size!!
    }
    fun setData(LabResultByDateList: ArrayList<LabResultGetByDataResponseContent>) {

        this!!.LabResultByDateList=LabResultByDateList

        notifyDataSetChanged()

    }

}
