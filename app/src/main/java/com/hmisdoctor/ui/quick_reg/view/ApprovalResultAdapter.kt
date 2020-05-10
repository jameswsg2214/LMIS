package com.hmisdoctor.ui.quick_reg.view


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.radiology.model.RecyclerDto

class ApprovalResultAdapter(context: Context, private var labTestList: ArrayList<orderList>) :
    RecyclerView.Adapter<ApprovalResultAdapter.MyViewHolder>() {
    private val mLayoutInflater: LayoutInflater
    private val mContext: Context
    var orderNumString: String? = null
    var status: String? = null

    //private var spinnerData = mutableMapOf<Int, String>()

    private var spinnerData= mutableMapOf(1 to "Negative", 2 to "Positive", 3 to "Equivocal")

    private var spinner= mutableMapOf(1 to 0, 2 to 1, 3 to 2)


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
            .inflate(R.layout.row_approval_results_list, parent, false) as LinearLayout
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


        val adapter = ArrayAdapter<String>(
            this.mContext,
            R.layout.spinner_item,
            spinnerData.values.toMutableList()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder!!.result_text!!.adapter = adapter

        holder!!.result_text?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    val itemValue = parent!!.getItemAtPosition(0).toString()
                    labTestList[position].id =
                        spinnerData.filterValues { it == itemValue }.keys.toList()[0]

                    labTestList[position].name =
                        spinnerData.filterValues { it == itemValue }.values.toList()[0]
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    pos: Int,
                    id: Long
                ) {
                    val itemValue = parent!!.getItemAtPosition(pos).toString()
                    labTestList[position].id =
                        spinnerData.filterValues { it == itemValue }.keys.toList()[0]

                    labTestList[position].name =
                        spinnerData.filterValues { it == itemValue }.values.toList()[0]

                }

            }


        holder.result_text.setSelection(spinner[movie.id]!!)


    }

    override fun getItemCount(): Int {
        return labTestList.size
    }

    fun setData(size: Int) {

        for (i in 0 ..size) {

            labTestList.add(orderList())
        }

        notifyDataSetChanged()
    }

    fun setAll(list: ArrayList<orderList>) {

        this.labTestList=list

        notifyDataSetChanged()
    }

    fun getAll():ArrayList<orderList> {

        return this.labTestList
    }

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }
}