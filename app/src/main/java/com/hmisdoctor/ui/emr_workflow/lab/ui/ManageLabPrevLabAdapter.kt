package com.hmisdoctor.ui.emr_workflow.lab.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.view.lab.model.PrevLabResponseContent
import java.lang.Exception

class ManageLabPrevLabAdapter(private val context: Context) :
    RecyclerView.Adapter<ManageLabPrevLabAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_labprev_dialog, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        try {
            //val responseData = manageLabPrevLabArrayList?.get(position);
            holder.sNoTextView.setText(1.toString())
            holder.observationTxtView.setText("jfnjf")
            holder.resultTextView.setText( "2&%3455")
            holder.uomTextView.setText("Test date")
            holder.referenceRangeTextView.setText("ref@3")

        }catch (e: Exception){

        }


        /*if (position % 2 == 0) {
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
        }*/

    }


    /*fun setData(responseContents: List<PrevLabResponseContent>?) {
        manageLabPrevLabArrayList = responseContents!!
        notifyDataSetChanged()
    }*/

    inner class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val sNoTextView: TextView = itemView.findViewById(R.id.sNoTextView)
        internal val observationTxtView: TextView = itemView.findViewById(R.id.observationTxtView)
        internal val resultTextView: TextView = itemView.findViewById(R.id.resultTextView)
        internal val uomTextView: TextView = itemView.findViewById(R.id.uomTextView)
        internal val referenceRangeTextView: TextView = itemView.findViewById(R.id.referenceRangeTextView)
        //internal val mainLinearLayout: LinearLayout = itemView.findViewById(R.id.mainLayout)
    }

}