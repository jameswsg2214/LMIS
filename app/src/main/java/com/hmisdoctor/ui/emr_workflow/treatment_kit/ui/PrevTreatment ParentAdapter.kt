package com.makkalnalam.ui.Expandable

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView


import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.treatment_kit.model.TreatmentKitResponsResponseContent
import com.hmisdoctor.ui.emr_workflow.view.lab.model.PodArrResult


import com.hmisdoctor.ui.emr_workflow.view.lab.model.PrevLabResponseContent


class PrevTreatmentParentParentAdapter(
    private val mContext: Context

) : RecyclerView.Adapter<PrevTreatmentParentParentAdapter.MyViewHolder>() {
    private val mLayoutInflater: LayoutInflater
    internal lateinit var orderNumString: String
    private var PatientList: List<TreatmentKitResponsResponseContent>? = ArrayList()

    private var onItemClickListener: OnItemClickListener? = null

    init {
        this.mLayoutInflater = LayoutInflater.from(mContext)


    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var firstTextView: TextView
        val byTextView: TextView
        val dateTextView: TextView
        val resultLinearLayout: LinearLayout
        val previewLinearLayout: LinearLayout
        val repeat:Button

        val modify:Button

        init {
            firstTextView = view.findViewById<View>(R.id.drName) as TextView
            byTextView = view.findViewById<View>(R.id.byTextView) as TextView
            dateTextView = view.findViewById<View>(R.id.dateTextView) as TextView
            resultLinearLayout = view.findViewById(R.id.resultLinearLayout)
            previewLinearLayout = view.findViewById<View>(R.id.previewLinearLayout) as LinearLayout
            repeat = view.findViewById<View>(R.id.repeatLab) as Button
            modify = view.findViewById<View>(R.id.modifyLab) as Button


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemLayout = LayoutInflater.from(mContext).inflate(
            R.layout.preview_treatment_kit_parent_list,
            parent,
            false
        ) as CardView
        val recyclerView: RecyclerView
        return MyViewHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        orderNumString = PatientList?.get(position).toString()
        val prevList = PatientList?.get(position)
        holder.firstTextView.text = prevList?.doctor_name
        holder.dateTextView.text = prevList?.ordered_date

    }

    fun refreshList(preLabArrayList: List<TreatmentKitResponsResponseContent>?) {
        this.PatientList = preLabArrayList!!
        this.notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return PatientList?.size!!
    }

    interface OnItemClickListener {
        fun onItemClick(
                responseContent: List<PodArrResult>?
        )
    }


    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }


}




