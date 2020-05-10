package com.hmisdoctor.ui.quick_reg.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.radiology.model.RecyclerDto
import com.makkalnalam.ui.Expandable.PrevLabChildAdapter

class OrderProcessPrentAdapter(context: Context, private var labTestList: List<RecyclerDto>) :
    RecyclerView.Adapter<OrderProcessPrentAdapter.MyViewHolder>() {
    private val mLayoutInflater: LayoutInflater
    private val mContext: Context
    var orderNumString: String? = null
    var status: String? = null
    var selectAllCheckbox: Boolean? = false


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var dateText: TextView
        var mainLinearLayout: LinearLayout
        var resultLinearLayout: LinearLayout
        internal var recyclerView: RecyclerView


        init {
            dateText = view.findViewById<View>(R.id.dateTextView) as TextView
            mainLinearLayout = view.findViewById(R.id.previewLinearLayout)
            resultLinearLayout = view.findViewById(R.id.resultLinearLayout)
            recyclerView = view.findViewById(R.id.child_recycler)



        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemLayout = LayoutInflater.from(mContext)
            .inflate(R.layout.row_lab_order_process_parent, parent, false) as LinearLayout
        var recyclerView: RecyclerView
        return MyViewHolder(itemLayout)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //orderNumString = labTestList[position].toString()
        val labAllData = labTestList[position]
        holder.dateText.text =labAllData.genre.toString()
        holder.recyclerView.layoutManager = LinearLayoutManager(mContext)
        val myOrderChildAdapter = OrderProcessChildAdapter(mContext,labTestList)
        val itemDecor = DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL)
        holder.recyclerView.addItemDecoration(itemDecor)
        holder.recyclerView.adapter = myOrderChildAdapter


/*
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
*/
        holder.mainLinearLayout.setOnClickListener {

            if (holder.resultLinearLayout.visibility == View.VISIBLE) {
                holder.resultLinearLayout.visibility = View.GONE

            } else {
                holder.resultLinearLayout.visibility = View.VISIBLE


            }
        }


    }

    override fun getItemCount(): Int {
        return labTestList.size
    }

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }




}