package com.hmisdoctor.ui.emr_workflow.blood_request.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.blood_request.model.BloodRequestModel

class BloodRequestAdapter(private val list: ArrayList<BloodRequestModel>) :
    RecyclerView.Adapter<BloodRequestAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var chkBloodComponent: CheckBox = view.findViewById(R.id.chkBloodComponent)
        var etBloodComponent: EditText = view.findViewById(R.id.etBloodComponent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_blood_request, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val bloodRequestModel = list[position]
        holder.chkBloodComponent.isChecked = bloodRequestModel.isChecked
        holder.etBloodComponent.hint = bloodRequestModel.hint

        holder.chkBloodComponent.addTextChangedListener {
            holder.etBloodComponent.isEnabled = holder.chkBloodComponent.isChecked
        }
    }
}