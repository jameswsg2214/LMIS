package com.hmisdoctor.ui.quick_reg.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.CompoundButtonCompat
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.quick_reg.model.labtest.response.LabTestresponseContent
import kotlinx.android.synthetic.main.activity_lab_test.view.*

class LabTestAdapter(context: Context, private var labTestList: ArrayList<LabTestresponseContent?>?) :
    RecyclerView.Adapter<LabTestAdapter.MyViewHolder>() {

    private val mLayoutInflater: LayoutInflater
    private var isLoadingAdded = false
    private val mContext: Context

    var orderNumString: String? = null

    var status: String? = null

    var selectAllCheckbox: Boolean? = false

    private var RTLabData: ArrayList<LabTestresponseContent?>? = ArrayList()

    private var RapidLabData: ArrayList<LabTestresponseContent?>? = ArrayList()

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var patient_info: TextView
        var status: TextView
        var order_num: TextView
        var mainLinearLayout: LinearLayout
        var selectCheckBox : CheckBox
        var radioGroup : RadioGroup
        var radioButton1 : RadioButton
        var radioButton2 : RadioButton
        var radioButton3 : RadioButton
        init {
            patient_info = view.findViewById<View>(R.id.patient_info) as TextView
            order_num = view.findViewById<View>(R.id.order_num) as TextView
            status = view.findViewById<View>(R.id.status) as TextView
            mainLinearLayout = view.findViewById(R.id.mainLinearLayout)
            selectCheckBox = view.findViewById(R.id.checkbox)
            radioGroup = view.findViewById(R.id.radioGroup)
            radioButton1 = view.findViewById(R.id.radiobutton1)
            radioButton2 = view.findViewById(R.id.radiobutton2)
            radioButton3 = view.findViewById(R.id.radiobutton3)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemLayout = LayoutInflater.from(mContext)
            .inflate(R.layout.row_lab_test_list, parent, false) as LinearLayout
        var recyclerView: RecyclerView
        return MyViewHolder(itemLayout)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        //orderNumString = labTestList[position].toString()
        holder.selectCheckBox.setTag(position);

        val labAllData = this!!.labTestList!![position]

        holder.patient_info.text = labAllData!!.first_name+"/"+labAllData.ageperiod+"/"+labAllData.gender_name

        holder.order_num.text =labAllData.order_number.toString()

        holder.status.text = labAllData.order_status_name

        holder.selectCheckBox.isChecked = labAllData.is_selected!!

        holder.radioGroup.clearCheck()

        if(this.labTestList!![position]!!.test_method_uuid==2){

            holder.radioGroup.visibility = View.VISIBLE

        }else{
            holder.radioGroup.visibility = View.INVISIBLE
        }

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
            holder.radioButton1.isEnabled=false

            holder.radioButton2.isEnabled=false

            holder.radioButton3.isEnabled=false
            holder.selectCheckBox.isEnabled = false
            holder?.status?.setTextColor(Color.parseColor("#FF0000"))

            if(labTestList!![position]!!.qualifier_uuid!=null) {

                when {

                    labTestList!![position]!!.qualifier_uuid == 2 -> {

                        holder.radioButton1.isChecked = true

                    }
                    labTestList!![position]!!.qualifier_uuid == 1 -> {

                        holder.radioButton2.isChecked = true

                    }
                    labTestList!![position]!!.qualifier_uuid == 3 -> {

                        holder.radioButton3.isChecked = true
                    }
                }

            }
           /* //Setting up background color on checkbox.
            if (Build.VERSION.SDK_INT < 21) {
                CompoundButtonCompat.setButtonTintList(holder?.selectCheckBox, ColorStateList.valueOf(Color.parseColor("#00e2a5")));//Use android.support.v4.widget.CompoundButtonCompat when necessary else
            } else {
                holder?.selectCheckBox.setButtonTintList(ColorStateList.valueOf());//setButtonTintList is accessible directly on API>19
            }*/
        }
        else{
            holder.radioButton1.isEnabled=true
            holder.radioButton2.isEnabled=true
            holder.radioButton3.isEnabled=true
            holder.selectCheckBox.isEnabled = true
            holder?.status?.setTextColor(Color.parseColor("#000000"))
        }

        holder.selectCheckBox.setOnClickListener {

            val myCheckBox = it as CheckBox
            val responseLabTestContent = labTestList!![position]
            if (myCheckBox.isChecked) {
                responseLabTestContent!!.is_selected = true

                RTLabData!!.add(responseLabTestContent)

                if(responseLabTestContent.test_method_uuid==2 && responseLabTestContent.radioselectName==0){

                    Toast.makeText(this.mContext,"Please select one Result",Toast.LENGTH_SHORT).show()
                }


            }else{
                holder.radioGroup.clearCheck()

                labTestList!![position]!!.radioselectName = 0

                responseLabTestContent!!.is_selected = false

                RTLabData!!.remove(responseLabTestContent)


            }

        }


        holder.radioButton1.setOnClickListener {

            if(holder.radioButton1.isChecked) {


                val check = RTLabData!!.any { it!!.uuid == this!!.labTestList!![position]!!.uuid }

                if (!check) {

                    this!!.labTestList!![position]!!.radioselectName = 1

                } else {

                    for (i in RTLabData!!.indices) {

                        if (RTLabData!![i]!!.uuid == this!!.labTestList!![position]!!.uuid) {

                            RTLabData!![i]?.radioselectName = 1

                        }

                    }

                }

            }

        }

        holder.radioButton2.setOnClickListener {

            if(holder.radioButton2.isChecked) {
                val check = RTLabData!!.any { it!!.uuid == this!!.labTestList!![position]!!.uuid }

                if (!check) {

                    this!!.labTestList!![position]!!.radioselectName = 2

                } else {

                    for (i in RTLabData!!.indices) {

                        if (RTLabData!![i]!!.uuid == this!!.labTestList!![position]!!.uuid) {

                            RTLabData!![i]?.radioselectName = 2

                        }

                    }

                }


            }

        }


        holder.radioButton3.setOnClickListener {

            if(holder.radioButton3.isChecked) {

                val check = RTLabData!!.any { it!!.uuid == this!!.labTestList!![position]!!.uuid }

                if (!check) {

                    this!!.labTestList!![position]!!.radioselectName = 3

                } else {

                    for (i in RTLabData!!.indices) {

                        if (RTLabData!![i]!!.uuid == this!!.labTestList!![position]!!.uuid) {

                            RTLabData!![i]?.radioselectName = 3

                        }

                    }

                }

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

    override fun getItemCount(): Int {
        return labTestList!!.size
    }

    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }

    fun setData(responseContent: List<LabTestresponseContent?>?) {
        this.labTestList= responseContent as ArrayList<LabTestresponseContent?>?
        notifyDataSetChanged()
    }

    fun getSelectedCheckData(): ArrayList<LabTestresponseContent?>? {

        return RTLabData
    }
    fun selectAllCheckbox(ischeckedBox : Boolean){
        for (i in labTestList!!.indices){
            if(ischeckedBox) {
                if (this!!.labTestList!![i]!!.order_status_uuid != 7) {
                    this!!.labTestList!![i]!!.is_selected = true
                    selectAllCheckbox = true
                    RTLabData!!.add(this!!.labTestList!![i])
                }
                else{
                    labTestList!![i]!!.is_selected = false
                    selectAllCheckbox = false
                    RTLabData!!.remove(this!!.labTestList!![i])
                }
            }else{
                labTestList!![i]!!.is_selected = false
                selectAllCheckbox = false
                RTLabData!!.remove(this!!.labTestList!![i])
            }}
        notifyDataSetChanged()}

    fun addAll(responseContent: List<LabTestresponseContent?>?) {
        this.labTestList!!.addAll(responseContent!!)
        notifyDataSetChanged()
    }

    fun clearAll() {
        this.labTestList?.clear()
        this.RTLabData?.clear()
        notifyDataSetChanged()
    }
    fun addLoadingFooter() {
        isLoadingAdded = true
        add(LabTestresponseContent())
    }
    fun add(r: LabTestresponseContent) {
        labTestList!!.add(r)
        notifyItemInserted(labTestList!!.size - 1)
    }
    fun getItem(position: Int): LabTestresponseContent? {
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
}