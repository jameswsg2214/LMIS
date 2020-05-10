package com.hmisdoctor.ui.quick_reg.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.history.familyhistory.model.responseContents
import com.hmisdoctor.ui.emr_workflow.radiology.model.RecyclerDto
import com.hmisdoctor.ui.out_patient.search_response_model.ResponseContent
import com.hmisdoctor.ui.quick_reg.model.labtest.response.LabTestApprovalresponseContent
import com.hmisdoctor.ui.quick_reg.model.labtest.response.testprocess.TestPrecessresponseContent

class LabTestsProcessAdapter(context: Context, private var labTestList: ArrayList<TestPrecessresponseContent?>?) :
    RecyclerView.Adapter<LabTestsProcessAdapter.MyViewHolder>() {
    private val mLayoutInflater: LayoutInflater
    private val mContext: Context
    var orderNumString: String? = null
    var status: String? = null
    var selectAllCheckbox: Boolean? = false
    private var isLoadingAdded = false
    private var SelectedLabData: ArrayList<TestPrecessresponseContent?>? = ArrayList()

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var patient_info: TextView
        var status: TextView
        var dateText: TextView
        var sampleid: TextView
        var testMethod: TextView
        var mainLinearLayout: LinearLayout
        var selectCheckBox : CheckBox

        init {
            patient_info = view.findViewById<View>(R.id.patient_info) as TextView
            dateText = view.findViewById<View>(R.id.dateText) as TextView
            status = view.findViewById<View>(R.id.status) as TextView
            mainLinearLayout = view.findViewById(R.id.mainLinearLayout)
            selectCheckBox = view.findViewById(R.id.checkbox)
            sampleid = view.findViewById(R.id.sample_id)
            testMethod = view.findViewById(R.id.testMethod)


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemLayout = LayoutInflater.from(mContext)
            .inflate(R.layout.row_lab_test_process_, parent, false) as LinearLayout
        var recyclerView: RecyclerView
        return MyViewHolder(itemLayout)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //orderNumString = labTestList[position].toString()
        val labAllData = labTestList!![position]
        holder.patient_info.text = labAllData!!.first_name+"/"+labAllData!!.ageperiod+"/"+labAllData.gender_name
        holder.dateText.text =labAllData.order_request_date
        holder.status.text = labAllData.order_status_name
        holder.sampleid.text = labAllData.order_number.toString()
        holder.testMethod.text = labAllData.test_method_name

        holder.selectCheckBox.isChecked = labAllData.is_selected!!

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


        if(labTestList!![position]!!.order_status_uuid ==7 && labTestList!![position]!!.auth_status_uuid==4 || labTestList!![position]!!.order_status_uuid==2)
        {
            if(labTestList!![position]!!.order_status_uuid ==2)
            {

                holder?.status?.setText(labTestList!![position]!!.order_status_name)
            }
            else
            {

                holder?.status?.setText(labTestList!![position]!!.auth_status_name)
            }

            labTestList!![position]!!.checkboxdeclardtatus = false
        }
        else{
            labTestList!![position]!!.checkboxdeclardtatus = true
        }

        if(labTestList!![position]!!.checkboxdeclardtatus ==false)
        {
            holder.selectCheckBox.isEnabled = false
            holder?.status?.setTextColor(Color.parseColor("#FF0000"))
        }
        else{

            holder.selectCheckBox.isEnabled = true
            holder?.status?.setTextColor(Color.parseColor("#000000"))
        }

        holder.selectCheckBox.setOnClickListener {

            val myCheckBox = it as CheckBox

            val responseLabTestContent = labTestList!![position]

            if (myCheckBox.isChecked) {

                labTestList!![position]!!.is_selected = true

                SelectedLabData!!.add(responseLabTestContent)

            }else{

                labTestList!![position]!!.is_selected = false

                SelectedLabData!!.remove(responseLabTestContent)

            }

        }

        if(selectAllCheckbox == true){

            if(labTestList!![position]!!.checkboxdeclardtatus ==false)
            {
                holder.selectCheckBox.isEnabled = false
                holder.selectCheckBox.isChecked = false
            }
            else{
                holder.selectCheckBox.isChecked = true
                holder.selectCheckBox.isEnabled = true
            }
        }


    }


    fun getSelectedCheckData(): ArrayList<TestPrecessresponseContent?>? {

        return SelectedLabData
    }

    fun clearAll() {
        this.labTestList?.clear()
        this.SelectedLabData?.clear()
        notifyDataSetChanged()
    }

    fun setData(setContents : List<TestPrecessresponseContent?>?){
        this.labTestList = setContents as ArrayList<TestPrecessresponseContent?>?
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return labTestList!!.size
    }
    fun addAll(responseContent: List<TestPrecessresponseContent?>?) {
        labTestList?.addAll(responseContent!!)
        notifyDataSetChanged()
    }

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }


    fun addLoadingFooter() {
        isLoadingAdded = true
        add(TestPrecessresponseContent())
    }
    fun add(r: TestPrecessresponseContent) {
        labTestList!!.add(r)
        notifyItemInserted(labTestList!!.size - 1)
    }
    fun getItem(position: Int): TestPrecessresponseContent? {
        return labTestList!![position]
    }
    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position = labTestList!!.size - 1
        val result = getItem(position)
        if (result != null) {
            labTestList!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun selectAllCheckboxes(ischeckedBox : Boolean){

        for (i in labTestList!!.indices){

            if(ischeckedBox) {

                this!!.labTestList!![i]!!.is_selected = true
                selectAllCheckbox = true

                SelectedLabData!!.add(this!!.labTestList!![i])

                notifyDataSetChanged()

            }else{
                labTestList!![i]!!.is_selected = false
                selectAllCheckbox = false
                SelectedLabData!!.remove(this!!.labTestList!![i])
                notifyDataSetChanged()
            }

        }

    }
}
