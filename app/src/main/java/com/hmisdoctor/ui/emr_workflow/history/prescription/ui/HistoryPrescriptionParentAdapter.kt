package com.hmisdoctor.ui.emr_workflow.history.prescription.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextClock
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.duration.DurationResponseContent
import com.hmisdoctor.ui.emr_workflow.history.prescription.model.HistoryPresRow

class HistoryPrescriptionParentAdapter(private val mContext: Context) :
    RecyclerView.Adapter<HistoryPrescriptionParentAdapter.MyViewHolder>() {
    private val mLayoutInflater: LayoutInflater
    internal lateinit var orderNumString: String
    private var historyPrescriptionList: List<HistoryPresRow?>? = ArrayList()
    private var durationArrayList: ArrayList<DurationResponseContent?>? = ArrayList()

    init {
        this.mLayoutInflater = LayoutInflater.from(mContext)
    }

     class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var sNoTxt: TextView
        var presHistoryDateTxt: TextView
        val historyPresdepartmentTxt: TextView
        val prescribedTxt: TextView
        val institutionTxt: TextView
        // val tableLayout: TableLayout
        val parentLayout: LinearLayout
        val ChildLayout: LinearLayout
         val spinnerType: TextView

         internal var recyclerView: RecyclerView

        init {
            sNoTxt = itemView.findViewById<View>(R.id.sNoTxt) as TextView
            presHistoryDateTxt = itemView.findViewById<View>(R.id.presHistoryDateTxt) as TextView
            historyPresdepartmentTxt = itemView.findViewById<View>(R.id.historyPresdepartmentTxt) as TextView
            institutionTxt = itemView.findViewById<View>(R.id.institutionTxt) as TextView
            prescribedTxt = itemView.findViewById<View>(R.id.prescribedTxt) as TextView
            parentLayout = itemView.findViewById(R.id.parentLayout)
            ChildLayout = itemView.findViewById<View>(R.id.ChildLayout) as LinearLayout
            spinnerType = itemView.findViewById<View>(R.id.typeTxt) as TextView

            recyclerView = itemView.findViewById(R.id.historyPrescriptionRecyclerView)
            //  tableLayout = view.findViewById<View>(R.id.tableLayout) as TableLayout

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemLayout = LayoutInflater.from(mContext).inflate(
            R.layout.row_parent_history_prescription,
            parent,
            false
        ) as LinearLayout
        return MyViewHolder(itemLayout)
    }

    override fun getItemCount(): Int {
        return historyPrescriptionList?.size!!
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        orderNumString = historyPrescriptionList?.get(position).toString()
        val presList = historyPrescriptionList?.get(position)
        val pos = position+1
        holder.sNoTxt.text = pos.toString()
        holder.historyPresdepartmentTxt.text = presList?.department_name.toString()
        holder.presHistoryDateTxt.text = presList?.created_date.toString()
        holder.institutionTxt.text = presList?.facility_name.toString()
        holder.prescribedTxt.text = presList?.priscribedDoctor_name.toString()
        holder.spinnerType.text = presList?.encounter_type_name.toString()



        holder.recyclerView.layoutManager = LinearLayoutManager(mContext)
        val myOrderChildAdapter = HistoryPrescriptionChildAdapter(mContext,presList?.prescription_details)
        val itemDecor = DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL)
        holder.recyclerView.addItemDecoration(itemDecor)
        holder.recyclerView.adapter = myOrderChildAdapter

        holder.parentLayout.setOnClickListener {

            if (holder.ChildLayout.visibility == View.VISIBLE) {
                holder.ChildLayout.visibility = View.GONE

            } else {
                holder.ChildLayout.visibility = View.VISIBLE


            }
        }


    }


    fun refreshList(presArrayList: List<HistoryPresRow>?) {
        this.historyPrescriptionList = presArrayList!!
        this.notifyDataSetChanged()
    }

}