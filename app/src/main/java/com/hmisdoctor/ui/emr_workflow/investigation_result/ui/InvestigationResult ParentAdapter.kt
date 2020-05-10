package com.hmisdoctor.ui.emr_workflow.investigation_result.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.investigation_result.model.InvestigationResultResponseModel


class InvestigationResultParentAdapter(
    private val mContext: Context, private var radiologyResultList: ArrayList<InvestigationResultResponseModel>


) : RecyclerView.Adapter<InvestigationResultParentAdapter.MyViewHolder>() {
    private val mLayoutInflater: LayoutInflater
    internal lateinit var orderNumString: String



    init {
        this.mLayoutInflater = LayoutInflater.from(mContext)
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val dateTextView: TextView
        val resultLinearLayout: LinearLayout
        val previewLinearLayout: LinearLayout


        internal var recyclerView: RecyclerView

        init {

            dateTextView = view.findViewById<View>(R.id.dateTextView) as TextView
            resultLinearLayout = view.findViewById(R.id.resultLinearLayout)
            previewLinearLayout = view.findViewById<View>(R.id.previewLinearLayout) as LinearLayout
            recyclerView = view.findViewById(R.id.child_recycler)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemLayout = LayoutInflater.from(mContext).inflate(
            R.layout.investigation_result_parent_layout,
            parent,
            false
        ) as CardView
        val recyclerView: RecyclerView
        return MyViewHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        orderNumString = radiologyResultList?.get(position).toString()
        val resultListList = radiologyResultList?.get(position)

        holder.dateTextView.text = "09-sep-2019 10:00AM-coronary Anglography-General medicine-OP"
        holder.recyclerView.layoutManager = LinearLayoutManager(mContext)
       val myOrderChildAdapter = InvestigationResultChildAdapter(mContext,radiologyResultList)
        val gridLayoutManagerHospitals = GridLayoutManager(
            mContext, 3,
            LinearLayoutManager.HORIZONTAL, false
        )
        holder.recyclerView!!.layoutManager = gridLayoutManagerHospitals
        holder.recyclerView.adapter = myOrderChildAdapter
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

    override fun getItemCount(): Int {
        return radiologyResultList?.size!!
    }

    fun setData(labResultLIst: ArrayList<InvestigationResultResponseModel>) {

        this!!.radiologyResultList=labResultLIst

        notifyDataSetChanged()

    }


}




