package com.hmisdoctor.ui.emr_workflow.investigation_result.ui

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.investigation_result.model.InvestigationResultResponseModel
import com.hmisdoctor.ui.emr_workflow.lab_result.model.SampleParentResponse
import com.hmisdoctor.ui.emr_workflow.radiology_result.model.RadilogyResultResponseContent
import com.squareup.picasso.Picasso


class InvestigationResultChildAdapter(
    private val mContext: Context,
    private var pod_Result: ArrayList<InvestigationResultResponseModel>

    ) : RecyclerView.Adapter<InvestigationResultChildAdapter.MyViewHolder>() {
    private val mLayoutInflater: LayoutInflater


    init {
        this.mLayoutInflater = LayoutInflater.from(mContext)

    }


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var RadiologyImageview: ImageView


        init {

            RadiologyImageview = view.findViewById<View>(R.id.imageView) as ImageView

        }
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): MyViewHolder {
        val itemLayout = LayoutInflater.from(mContext).inflate(
            R.layout.investigation_result_child_layout,
            viewGroup,
            false
        ) as CardView
        return MyViewHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val prevList = pod_Result?.get(position)
/*
            Glide.with(holder.RadiologyImageview.context).load(prevList.image_url).into(holder.RadiologyImageview)
*/

    }

    override fun getItemCount(): Int {
        return pod_Result?.size!!
    }
}
