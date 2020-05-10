package com.hmisdoctor.ui.quick_reg.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.radiology.model.RecyclerDto

class OrderProcessChildAdapter(context: Context, private val labTestList: List<RecyclerDto>) :
    RecyclerView.Adapter<OrderProcessChildAdapter.MyViewHolder>() {
    private val mLayoutInflater: LayoutInflater
    private val mContext: Context
    var orderNumString: String? = null
    var status: String? = null


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var serialNumberTextView: TextView
        var textProcess: TextView
        var result_text: Spinner
        var uom_text: TextView
        var normal_val_text: TextView
        var mainLinearLayout: LinearLayout


        init {
            serialNumberTextView = view.findViewById<View>(R.id.serialNumberTextView) as TextView

            textProcess = view.findViewById<View>(R.id.textProcess) as TextView
            result_text = view.findViewById<View>(R.id.result_spinner) as Spinner
            uom_text = view.findViewById<View>(R.id.uom_text) as TextView
            normal_val_text = view.findViewById<View>(R.id.normal_val_text) as TextView


            mainLinearLayout = view.findViewById(R.id.mainLinearLayout)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemLayout = LayoutInflater.from(mContext)
            .inflate(R.layout.row_approval_result_list, parent, false) as LinearLayout
        var recyclerView: RecyclerView
        return MyViewHolder(itemLayout)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        orderNumString = labTestList[position].toString()
        holder.serialNumberTextView.text = (position + 1).toString()
        val movie = labTestList[position]
        holder.textProcess.text = movie.title
/*
        status = holder.status.text as String?
*/
        holder.normal_val_text.text = movie.year
        holder.uom_text.text = movie.genre

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

    override fun getItemCount(): Int {
        return labTestList.size
    }

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }
}