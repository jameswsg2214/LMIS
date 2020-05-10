package com.hmisdoctor.ui.quick_reg.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.quick_reg.model.QuickSearchresponseContent


class SearchPatientAdapter(context: Context, private var searchPatienList: ArrayList<QuickSearchresponseContent>) : RecyclerView.Adapter<SearchPatientAdapter.MyViewHolder>() {
    private val mLayoutInflater: LayoutInflater
    private val mContext: Context
    var orderNumString: String? = null
    private var onItemClickListener: OnItemClickListener? = null

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var serialNUmber: TextView
        var dateTextView: TextView
        var nameTextView: TextView
        var pinTextView: TextView
        var genderTextView:TextView


        init {
            serialNUmber = view.findViewById<View>(R.id.serialNoTextView) as TextView
            pinTextView = view.findViewById<View>(R.id.pinTextView) as TextView
            dateTextView = view.findViewById<View>(R.id.dateTextView) as TextView
            nameTextView = view.findViewById<View>(R.id.nameTextView) as TextView
            genderTextView = view.findViewById<View>(R.id.genderTextView) as TextView


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemLayout = LayoutInflater.from(mContext).inflate(R.layout.serach_row_list, parent, false) as LinearLayout
        var recyclerView: RecyclerView
        return MyViewHolder(itemLayout)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        orderNumString = searchPatienList[position].toString()
        val searchList = searchPatienList[position]
        holder.serialNUmber.text = (position + 1).toString()
        holder.pinTextView.text = searchList.uhid
        holder.nameTextView.text = searchList.first_name
        holder.dateTextView.text = searchList.registered_date
        when (searchList?.gender_uuid) {
            1 -> {
                holder.genderTextView.text = "Male"
            }
            2 -> {
                holder.genderTextView.text = "Female"
            }
            3 -> {
                holder.genderTextView.text = "Transgender"
            }
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(searchList)
        }


    }

    override fun getItemCount(): Int {
        return searchPatienList.size
    }

    /*
sent data
*/
    interface OnItemClickListener {
        fun onItemClick(responseContent: QuickSearchresponseContent)
    }


    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun setData(arrayListPatientList: ArrayList<QuickSearchresponseContent>) {
        searchPatienList = arrayListPatientList
        notifyDataSetChanged()


    }

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }
}