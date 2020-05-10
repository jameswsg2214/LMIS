package com.hmisdoctor.ui.emr_workflow.lab_result.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox

import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.lab_result.model.LabResultResponseContent


class LabResultParentAdapter(
    private val mContext: Context, var LabResultList: ArrayList<LabResultResponseContent>


) : RecyclerView.Adapter<LabResultParentAdapter.MyViewHolder>() {
    private val mLayoutInflater: LayoutInflater
    internal lateinit var orderNumString: String
      var LabResultList1:ArrayList<LabResultResponseContent>? = null


    init {
        this.mLayoutInflater = LayoutInflater.from(mContext)
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val dateTextView: TextView
        val resultLinearLayout: LinearLayout
        val previewLinearLayout: LinearLayout
        internal var recyclerView: RecyclerView
        val dateCheckbox: CheckBox


        init {

            dateTextView = view.findViewById<View>(R.id.dateTextView) as TextView
            resultLinearLayout = view.findViewById(R.id.resultLinearLayout)
            previewLinearLayout = view.findViewById<View>(R.id.previewLinearLayout) as LinearLayout
            recyclerView = view.findViewById(R.id.child_recycler)
            dateCheckbox = view.findViewById(R.id.dateCheckbox)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemLayout = LayoutInflater.from(mContext).inflate(
            R.layout.lab_result_parent_layout,
            parent,
            false
        ) as CardView
        val recyclerView: RecyclerView
        return MyViewHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        orderNumString = LabResultList?.get(position).toString()
        val labresultList = LabResultList?.get(position)
/*
        holder.dateCheckbox.setText("$position")
*/
        holder.dateCheckbox.setChecked(LabResultList.get(position).isSelected)
        holder.dateCheckbox.setTag(position)

        holder.dateTextView.text = labresultList?.order_request_date+"-"+labresultList?.test_master+"-"+"-"+labresultList?.department+"-"+labresultList?.encounter_type
        holder.recyclerView.layoutManager = LinearLayoutManager(mContext)
       val myOrderChildAdapter = LabResultChildAdapter(mContext,LabResultList)
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
        holder.dateCheckbox.setOnClickListener(View.OnClickListener {
            val pos = holder.dateCheckbox.getTag() as Int

            if( LabResultList.get(pos).isSelected) {
                LabResultList.get(pos).isSelected==false
            } else {
                LabResultList.get(pos).isSelected==true
            }
        })


    }

    fun refreshList(preLabArrayList: ArrayList<LabResultResponseContent>?) {
        this.notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return LabResultList?.size!!
    }

    fun setData(labResultLIst: ArrayList<LabResultResponseContent>) {

        this!!.LabResultList=labResultLIst

        notifyDataSetChanged()

    }


}




