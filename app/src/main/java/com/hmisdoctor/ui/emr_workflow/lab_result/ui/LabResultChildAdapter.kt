package com.hmisdoctor.ui.emr_workflow.lab_result.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.lab_result.model.LabResultResponseContent


class LabResultChildAdapter(
    private val mContext: Context,
    private var pod_Result: List<LabResultResponseContent>?

    ) : RecyclerView.Adapter<LabResultChildAdapter.MyViewHolder>() {
    private val mLayoutInflater: LayoutInflater


    init {
        this.mLayoutInflater = LayoutInflater.from(mContext)

    }


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
            R.layout.lab_result_child_layout,
            viewGroup,
            false
        ) as ConstraintLayout
        return MyViewHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val prevList = pod_Result?.get(position)
        var pos = position+1
        holder.sNoTextView.text = pos.toString()
        holder.resultTextView.text = prevList?.repsonse?.get(0)?.result_value as CharSequence?
        holder.observationTextView.text = prevList?.repsonse?.get(0)?.test_or_analyte
        holder.referenceRange.text = prevList?.repsonse?.get(0)?.test_or_analyte_ref_min.toString()+"-"+prevList?.repsonse?.get(0)?.test_or_analyte_ref_max.toString()
        holder.uomTextView.text = prevList?.repsonse?.get(0)?.analyte_uom.toString()


    }

    override fun getItemCount(): Int {
        return pod_Result?.size!!
    }
}
