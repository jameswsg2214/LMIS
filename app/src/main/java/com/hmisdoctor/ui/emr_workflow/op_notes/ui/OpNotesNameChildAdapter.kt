package com.hmisdoctor.ui.emr_workflow.op_notes.ui

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.op_notes.model.ProfileSection
import com.hmisdoctor.ui.emr_workflow.op_notes.model.ProfileSectionCategoryConceptValue

class OpNotesNameChildAdapter(
    private val mContext: Context,
    private var child_result_Result: List<ProfileSectionCategoryConceptValue>?

) : RecyclerView.Adapter<OpNotesNameChildAdapter.MyViewHolder>() {
    private val mLayoutInflater: LayoutInflater


    init {
        this.mLayoutInflater = LayoutInflater.from(mContext)

    }


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var secondTextView: TextView

        init {

            secondTextView = view.findViewById<View>(R.id.secondTextView) as TextView
        }
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): MyViewHolder {
        val itemLayout = LayoutInflater.from(mContext).inflate(
            R.layout.op_notes_name_child_recycler_list,
            viewGroup,
            false
        ) as ConstraintLayout
        return MyViewHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val opNotesChildList = child_result_Result?.get(position)

        holder.secondTextView.text = opNotesChildList?.value_name

    }

    override fun getItemCount(): Int {
        return child_result_Result?.size!!
    }

}
