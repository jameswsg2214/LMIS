package com.hmisdoctor.ui.emr_workflow.history.allergy.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.history.allergy.model.response.AllergyAll
import com.hmisdoctor.ui.emr_workflow.radiology.ui.RadiologyAdapter

class AllergyAdapter (val context: Context,
private var allergyArrayList: List<AllergyAll?>?) : RecyclerView.Adapter<AllergyAdapter.MyViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllergyAdapter.MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_allergy, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return allergyArrayList?.size!!
    }

    override fun onBindViewHolder(holder: AllergyAdapter.MyViewHolder, position: Int) {
        val responseContent = allergyArrayList!!.get(position)!!
        val pos = position+1
        holder.serialNumber.text = pos.toString()
        holder.dateTimeTxt.setText(responseContent.start_date)
        holder.typeTxt.setText(responseContent.encounter?.encounter_type?.name)
        holder.allergyName.setText(responseContent.allergy_masters!!.allergy_name)
        holder.allergySource.setText(responseContent.allergy_source!!.name)
        holder.allergySeverty.setText(responseContent.allergy_severity!!.name)
        holder.durationTextView.setText(responseContent.duration!!.toString() +" - "+responseContent.periods?.name)


        holder.editImageView.setOnClickListener {

            onClickListener?.OnClick(responseContent, position)

        }

        if (position % 2 == 0) {
            holder.mainLinearLayout.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
        } else {
            holder.mainLinearLayout.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.alternateRow
                )
            )
        }

    }

    interface OnClickListener {
        fun OnClick(
            responseContent: AllergyAll?,
            position: Int
        )
    }

    fun setOnClickListener(onclick: OnClickListener) {
        this.onClickListener = onclick
    }

    fun setData(responseContent: List<AllergyAll?>?) {

        this.allergyArrayList=responseContent

        notifyDataSetChanged()
    }

    inner class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val serialNumber: TextView = itemView.findViewById(R.id.serialNumber)
        internal val dateTimeTxt: TextView = itemView.findViewById(R.id.dateTimeTxt)
        internal val typeTxt: TextView = itemView.findViewById(R.id.typeTxt)
        internal val allergyName: TextView = itemView.findViewById(R.id.allergyName)
        internal val allergySource: TextView = itemView.findViewById(R.id.allergySource)
        internal val allergySeverty: TextView = itemView.findViewById(R.id.allergySeverty)
        internal val durationTextView: TextView = itemView.findViewById(R.id.durationTextView)
        internal val editImageView: ImageView = itemView.findViewById(R.id.editImageView)
        internal val mainLinearLayout: LinearLayout = itemView.findViewById(R.id.mainLinearLayout)
    }
}