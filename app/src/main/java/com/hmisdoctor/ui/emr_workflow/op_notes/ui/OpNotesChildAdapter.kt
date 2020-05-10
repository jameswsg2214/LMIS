package com.hmisdoctor.ui.emr_workflow.op_notes.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R

import com.hmisdoctor.ui.emr_workflow.op_notes.model.ProfileSectionCategoryConcept


class OpNotesChildAdapter(
    private val mContext: Context,
    private var child_result_Result: List<ProfileSectionCategoryConcept>?

) : RecyclerView.Adapter<OpNotesChildAdapter.MyViewHolder>() {
    private val mLayoutInflater: LayoutInflater


    init {
        this.mLayoutInflater = LayoutInflater.from(mContext)

    }


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var fisrstTextView: TextView
        internal var recyclerView: RecyclerView




        init {

            fisrstTextView = view.findViewById<View>(R.id.fisrstTextView) as TextView
            recyclerView = view.findViewById(R.id.child_name_recycler)



        }
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): MyViewHolder {
        val itemLayout = LayoutInflater.from(mContext).inflate(
            R.layout.op_notes_child_recycler_list,
            viewGroup,
            false
        ) as ConstraintLayout
        return MyViewHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val opNotesChildList = child_result_Result?.get(position)

        holder.fisrstTextView.text = opNotesChildList?.name
        holder.recyclerView.layoutManager = LinearLayoutManager(mContext)
        val myOrderChildAdapter = OpNotesNameChildAdapter(mContext,child_result_Result?.get(position)?.profile_section_category_concept_values)
        val itemDecor = DividerItemDecoration(mContext, DividerItemDecoration.HORIZONTAL)
        val layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, true)
        holder.recyclerView.setLayoutManager(layoutManager)
        holder.recyclerView.addItemDecoration(itemDecor)
        holder.recyclerView.adapter = myOrderChildAdapter


    }

    override fun getItemCount(): Int {
        return child_result_Result?.size!!
    }

}
