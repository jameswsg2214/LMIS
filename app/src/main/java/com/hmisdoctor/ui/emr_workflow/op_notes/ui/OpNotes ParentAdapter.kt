package com.hmisdoctor.ui.emr_workflow.op_notes.ui

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView

import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.op_notes.model.OpNotesExpandableResponseContent


class OpNotesParentAdapter(
    private val mContext: Context

) : RecyclerView.Adapter<OpNotesParentAdapter.MyViewHolder>() {
    private val mLayoutInflater: LayoutInflater
    internal lateinit var orderNumString: String
    private var PatientList: List<OpNotesExpandableResponseContent>? = ArrayList()

    private var onItemClickListener: OnItemClickListener? = null

    init {
        this.mLayoutInflater = LayoutInflater.from(mContext)


    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var firstTextView: TextView
        val resultLinearLayout: LinearLayout
        val previewLinearLayout: LinearLayout
        /* val repeat:Button

         val modify:Button*/


        internal var recyclerView: RecyclerView

        init {
            firstTextView = view.findViewById<View>(R.id.nameTextView) as TextView
            resultLinearLayout = view.findViewById(R.id.resultLinearLayout)
            previewLinearLayout = view.findViewById<View>(R.id.previewLinearLayout) as LinearLayout
            recyclerView = view.findViewById(R.id.child_recycler)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemLayout = LayoutInflater.from(mContext).inflate(
            R.layout.op_notes_parent_list,
            parent,
            false
        ) as CardView
        val recyclerView: RecyclerView
        return MyViewHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        orderNumString = PatientList?.get(position).toString()
        val OpParentListList = PatientList?.get(position)
        holder.firstTextView.text =
            OpParentListList?.profile_sections?.get(position)?.profile_section_categories?.get(
                position
            )?.categories?.name
        holder.recyclerView.layoutManager = LinearLayoutManager(mContext)
        val myOrderChildAdapter = OpNotesChildAdapter(
            mContext,
            OpParentListList?.profile_sections?.get(position)?.profile_section_categories?.get(
                position
            )?.profile_section_category_concepts
        )
        val itemDecor = DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL)
        holder.recyclerView.addItemDecoration(itemDecor)
        holder.recyclerView.adapter = myOrderChildAdapter

        holder.previewLinearLayout.setOnClickListener {

            if (holder.resultLinearLayout.visibility == View.VISIBLE) {
                holder.resultLinearLayout.visibility = View.GONE

            } else {
                holder.resultLinearLayout.visibility = View.VISIBLE


            }
         }

    }

    fun refreshList(preLabArrayList: List<OpNotesExpandableResponseContent>?) {
        this.PatientList = preLabArrayList!!
        this.notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return PatientList?.size!!
    }

    interface OnItemClickListener {
        fun onItemClick(
            responseContent: List<com.hmisdoctor.ui.emr_workflow.investigation.model.PodArrResult>
        )
    }


    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }


}




