package com.hmisdoctor.ui.emr_workflow.certificate.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R

import com.hmisdoctor.ui.emr_workflow.certificate.model.GetCertificateResponseContent
import com.hmisdoctor.ui.emr_workflow.certificate.model.GetCertificateResponseModel
import com.hmisdoctor.ui.emr_workflow.lab.ui.ManageLabPervLabDialogFragment
import com.hmisdoctor.ui.emr_workflow.view.lab.model.PodArrResult
import com.hmisdoctor.ui.emr_workflow.view.lab.model.PrevLabResponseContent
import com.makkalnalam.ui.Expandable.PrevLabChildAdapter

class PrevCertificateDialogAdapter(
    private val mContext: Context

) : RecyclerView.Adapter<PrevCertificateDialogAdapter.MyViewHolder>() {
    private val mLayoutInflater: LayoutInflater
    internal lateinit var orderNumString: String
    private var arrayCertificateList: List<GetCertificateResponseContent>? = ArrayList()


    init {
        this.mLayoutInflater = LayoutInflater.from(mContext)


    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {






        internal val serialNumberTextView: TextView = itemView.findViewById(R.id.sNoTextView)
        internal val deleteImageView: ImageView = itemView.findViewById(R.id.downloadImageView)
        internal val certificateName: TextView = itemView.findViewById(R.id.certificateName)
        internal val mainLinearLayout: LinearLayout = itemView.findViewById(R.id.mainLinearLayout)
        internal val issuedByText: TextView = itemView.findViewById(R.id.issuedByText)
        internal val dateText: TextView = itemView.findViewById(R.id.dateText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemLayout = LayoutInflater.from(mContext).inflate(
            R.layout.row_prev_certificate,
            parent,
            false
        ) as LinearLayout
        val recyclerView: RecyclerView
        return MyViewHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        orderNumString = arrayCertificateList?.get(position).toString()
        val response = arrayCertificateList?.get(position)
        holder.serialNumberTextView.text = (position + 1).toString()
        holder.certificateName?.text = response!!.nt_name
        holder.issuedByText?.text = response!!.u_first_name.toString()
        holder.dateText?.text = response!!.pc_approved_on.toString()
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





    }

    fun refreshList(preLabArrayList: List<GetCertificateResponseContent>?) {
        this.arrayCertificateList = preLabArrayList!!
        this.notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return arrayCertificateList?.size!!
    }
}




