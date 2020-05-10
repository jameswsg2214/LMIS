package com.hmisdoctor.ui.emr_workflow.radiology_result.ui

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.lab_result.model.SampleParentResponse
import com.hmisdoctor.ui.emr_workflow.radiology_result.model.RadilogyResultResponseContent
import com.squareup.picasso.Picasso


class RadiologyResultChildAdapter(
    private val mContext: Context,
    private var pod_Result: ArrayList<RadilogyResultResponseContent>

    ) : RecyclerView.Adapter<RadiologyResultChildAdapter.MyViewHolder>() {
    private val mLayoutInflater: LayoutInflater


    init {
        this.mLayoutInflater = LayoutInflater.from(mContext)

    }


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var RadiologyImageview: AppCompatImageView


        init {

            RadiologyImageview = view.findViewById<View>(R.id.imageView) as AppCompatImageView

        }
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): MyViewHolder {
        val itemLayout = LayoutInflater.from(mContext).inflate(
            R.layout.radiology_result_child_layout,
            viewGroup,
            false
        ) as CardView
        return MyViewHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val prevList = pod_Result?.get(position)
      /*  Picasso.get()
            .load("http://image10.bizrate-images.com/resize?sq=60&uid=2216744464")
            .into(holder.RadiologyImageview);*/
            Glide.with(holder.RadiologyImageview.context).load(R.drawable.rajivi_hospital).into(holder.RadiologyImageview)

    }

    override fun getItemCount(): Int {
        return pod_Result?.size!!
    }
}
