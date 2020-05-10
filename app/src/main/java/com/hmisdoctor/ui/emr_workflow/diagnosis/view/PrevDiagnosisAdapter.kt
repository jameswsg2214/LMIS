package com.hmisdoctor.ui.emr_workflow.diagnosis.view

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.PrevChiefComplainResponseModel
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.search_complaint.PrevChiefComplaintResponseContent
import com.hmisdoctor.ui.emr_workflow.diagnosis.model.PreDiagnosisResponseContent


class PrevDiagnosisAdapter(
    private val mContext: Context

    ) : RecyclerView.Adapter<PrevDiagnosisAdapter.MyViewHolder>() {
    private val mLayoutInflater: LayoutInflater
    private var prevDiagnosis_Result: List<PreDiagnosisResponseContent>? = ArrayList()


    init {
        this.mLayoutInflater = LayoutInflater.from(mContext)

    }


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var sNoTextView: TextView
        var nameTextView: TextView
        var codeTextView: TextView
        var mainLinearLayout: LinearLayout

        init {

            sNoTextView = view.findViewById<View>(R.id.sNoTextView) as TextView
            nameTextView = view.findViewById<View>(R.id.nameTextview) as TextView
            codeTextView = view.findViewById<View>(R.id.codeTextView) as TextView
            mainLinearLayout = view.findViewById<View>(R.id.mainLinearLayout) as LinearLayout



        }
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): MyViewHolder {
        val itemLayout = LayoutInflater.from(mContext).inflate(
            R.layout.prev_diagnosis_recycler_list,
            viewGroup,
            false
        ) as ConstraintLayout
        return MyViewHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val prevList = prevDiagnosis_Result?.get(position)
        val pos = position+1
        holder.sNoTextView.text = pos.toString()
        holder.nameTextView.text = prevList?.diagnosis?.name
        holder.codeTextView.text =prevList?.diagnosis?.code
        if (position % 2 == 0) {
            holder.mainLinearLayout.setBackgroundColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.alternateRow
                )
            )
        } else {
            holder.mainLinearLayout.setBackgroundColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.white
                )
            )
        }

    }

    override fun getItemCount(): Int {
        return prevDiagnosis_Result?.size!!
    }
    fun refreshList(preLabArrayList: List<PreDiagnosisResponseContent>?) {
        this.prevDiagnosis_Result = preLabArrayList!!
        this.notifyDataSetChanged()
    }

}
