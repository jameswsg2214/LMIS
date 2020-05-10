package com.hmisdoctor.ui.emr_workflow.blood_request.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.blood_request.model.BloodRequestDetail
import com.hmisdoctor.ui.emr_workflow.blood_request.model.ResponseContentXX

class PrevBloodRequestAdapter(private val list: ArrayList<ResponseContentXX>) :
    RecyclerView.Adapter<PrevBloodRequestAdapter.MyViewHolder>() {

    private val bloodRequestDetails = ArrayList<BloodRequestDetail>()
    private var prevBloodRequestChildAdapter: PreviousBloodRequestChildAdapter? = null

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvRequestedBy: TextView = view.findViewById(R.id.tvRequestedBy)
        var tvStatus: TextView = view.findViewById(R.id.tvStatus)
        var imgDown: ImageView = view.findViewById(R.id.imgDown)
        var recyclerViewPrevBloodRequestChild: RecyclerView =
            view.findViewById(R.id.recyclerViewPrevBloodRequestChild)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_prev_blood_request, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val previousBloodRequest = list[position]

        holder.tvRequestedBy.text = previousBloodRequest.bloodRequestedBy?.toString() ?: ""
        holder.tvStatus.text = previousBloodRequest.bloodRequestStatus?.name ?: ""

        previousBloodRequest.bloodRequestDetails?.let { bloodRequestDetailsList ->
            bloodRequestDetails.addAll(bloodRequestDetailsList)
        }

        holder.recyclerViewPrevBloodRequestChild.layoutManager =
            LinearLayoutManager(holder.itemView.context)
        prevBloodRequestChildAdapter = PreviousBloodRequestChildAdapter(bloodRequestDetails)
        holder.recyclerViewPrevBloodRequestChild.adapter = prevBloodRequestChildAdapter

        holder.imgDown.setOnClickListener {

        }
    }
}