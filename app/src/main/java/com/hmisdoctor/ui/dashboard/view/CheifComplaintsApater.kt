package com.hmisdoctor.ui.dashboard.view

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.dashboard.model.ChiefComplients
import kotlinx.android.synthetic.main.row_patients_complients.view.*


class CheifComplaintsApater (var context: Context,var data : ArrayList<ChiefComplients>?) : RecyclerView.Adapter<CheifComplaintsApater.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheifComplaintsApater.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_patients_complients, parent, false)
        return ViewHolder(v)
    }

    private fun setHeaderBg(view: View) {
        view.setBackgroundResource(R.drawable.tab_header_even)
    }

    private fun setContentBg(view: View) {
        view.setBackgroundResource(R.drawable.tab_row_odd)
    }


    //this method is binding the data on the list
    override fun onBindViewHolder(holder: CheifComplaintsApater.ViewHolder, position: Int) {
       holder.itemView.ll_parent.weightSum = 10f
        holder.itemView.tv_name.setLayoutParams(
            LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,5f
            )
        )

        holder.itemView.tv_count.setLayoutParams(
            LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,5f
            )
        )


        if(position % 2 == 1){
            setContentBg(holder.itemView.tv_name)
            setContentBg(holder.itemView.tv_count)
        }else{
            setHeaderBg(holder.itemView.tv_name)
            setHeaderBg(holder.itemView.tv_count)
        }


        val rowPos = holder.adapterPosition
        if(rowPos == 0){
            holder.itemView.apply {
                tv_name.text = "Name"
                tv_count .text = "COUNT"
                tv_code.visibility = View.GONE
            }
        }
        else {
            val dataModel = data?.get(rowPos - 1)
            holder.itemView.tv_code.visibility = View.GONE
            holder.itemView.tv_count.text = dataModel?.Count.toString()
            //holder.itemView.tv_code.text = data?.get(position)?.d_code.toString()
            holder.itemView.tv_name.text = dataModel?.cc_name
        }
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return data!!.size + 1
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}

