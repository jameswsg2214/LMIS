package com.hmisdoctor.ui.emr_workflow.lab.ui

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView


import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.view.lab.model.PodArrResult


import com.hmisdoctor.ui.emr_workflow.view.lab.model.PrevLabResponseContent
import com.makkalnalam.ui.Expandable.PrevLabChildAdapter


class PrevLabParentAdapter(
    private val mContext: Context

) : RecyclerView.Adapter<PrevLabParentAdapter.MyViewHolder>() {
    private val mLayoutInflater: LayoutInflater
    internal lateinit var orderNumString: String
    private var PatientList: List<PrevLabResponseContent>? = ArrayList()

    private var onItemClickListener: OnItemClickListener? = null

    init {
        this.mLayoutInflater = LayoutInflater.from(mContext)


    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var firstTextView: TextView
        var statusTextView: TextView
        val byTextView: TextView
        val dateTextView: TextView
        val statusTV: TextView
        val resultLinearLayout: LinearLayout
        val previewLinearLayout: LinearLayout
        val repeat:Button
        val modify:Button
        val viewLab:Button



        internal var recyclerView: RecyclerView

        init {
            firstTextView = view.findViewById<View>(R.id.drName) as TextView
            statusTextView = view.findViewById<View>(R.id.statusTextView) as TextView
            byTextView = view.findViewById<View>(R.id.byTextView) as TextView
            statusTV = view.findViewById<View>(R.id.statusTV) as TextView
            dateTextView = view.findViewById<View>(R.id.dateTextView) as TextView
            resultLinearLayout = view.findViewById(R.id.resultLinearLayout)
            previewLinearLayout = view.findViewById<View>(R.id.previewLinearLayout) as LinearLayout
            recyclerView = view.findViewById(R.id.child_recycler)
            repeat = view.findViewById<View>(R.id.repeatLab) as Button
            modify = view.findViewById<View>(R.id.modifyLab) as Button
            viewLab = view.findViewById<View>(R.id.viewButton) as Button


/*
            tableLayout = view.findViewById<View>(R.id.tableLayout) as TableLayout
*/

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemLayout = LayoutInflater.from(mContext).inflate(
            R.layout.preview_parent_list,
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
        holder.statusTextView.text = prevList?.order_status
        holder.dateTextView.text = prevList?.created_date
        holder.recyclerView.layoutManager = LinearLayoutManager(mContext)
        val myOrderChildAdapter = PrevLabChildAdapter(mContext,prevList?.pod_arr_result)
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

        holder.repeat.setOnClickListener {

            onItemClickListener!!.onItemClick(this.PatientList!![position].pod_arr_result)

        }
        holder.viewLab.setOnClickListener {
            val ft = (mContext as AppCompatActivity).supportFragmentManager.beginTransaction()
            val dialog = ManageLabPervLabDialogFragment()
            dialog.show(ft, "Tag")


        }


        holder.modify.setOnClickListener {

            onItemClickListener!!.onItemClick(this.PatientList!![position].pod_arr_result)

        }
    }

    fun refreshList(preLabArrayList: List<PrevLabResponseContent>?) {
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




