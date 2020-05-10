package com.hmisdoctor.ui.emr_workflow.vitals.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.vitals.model.TemplateDetail
import com.hmisdoctor.ui.emr_workflow.vitals.model.TemplateMasterDetail
import com.hmisdoctor.ui.emr_workflow.vitals.model.Uom
import com.hmisdoctor.ui.emr_workflow.vitals.model.response.GetVital


class VitalsAdapter(
    private val context: Activity,
    private var vitalsList: ArrayList<TemplateMasterDetail>

) : RecyclerView.Adapter<VitalsAdapter.VitalHolder>() {
    private var onItemClickListener: OnItemClickListener? = null
    private var typeList = mutableMapOf<Int, String>()
    private val hashMapType: HashMap<Int,Int> = HashMap()
    private var onDeleteClickListener: OnDeleteClickListener? = null
    private var parentID = 0
    var hashMapVitalsList : HashMap<Int,Int> = HashMap()


    private var onSearchClickListener:OnSearchClickListener?=null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VitalHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.row_vitals, parent, false)
        return VitalHolder(view)

    }

    override fun getItemCount(): Int {
        return vitalsList.size

    }

    override fun onBindViewHolder(holder: VitalHolder, position: Int) {
        var vitalsTemplateMasterDetail = vitalsList[position]
        holder.nameTextView.setText(vitalsTemplateMasterDetail.vital_master.name)

        val pos = position+1
        holder.serialNumberTextView.setText(pos.toString())
        holder.deleteImageView.setOnClickListener {
         onDeleteClickListener?.onDeleteClick(vitalsTemplateMasterDetail, position)

        } //Fill EditText with the value you have in data source
        //Fill EditText with the value you have in data source
        holder.vitualEditText.setText(vitalsList.get(position).itemAppendString)
        holder.vitualEditText.setId(position)

        //we need to update adapter once we finish with editing
        //we need to update adapter once we finish with editing

        holder.vitualEditText.addTextChangedListener (object :TextWatcher{

            override fun afterTextChanged(s: Editable?) {

                vitalsList[position].vital_master.vitals_value=s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            }
        })


        holder.nameTextView.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun afterTextChanged(s: Editable) {
                if (s.length == 2) {

                    onSearchClickListener?.onSearchClick(
                            holder.nameTextView,position)

                }}
        })


        val adapter =
            ArrayAdapter<String>(
                context,
                android.R.layout.simple_spinner_item,
                typeList.values.toMutableList()
            )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.spinner_type.adapter = adapter

        holder.spinner_type.onItemSelectedListener= object :AdapterView.OnItemClickListener,
            AdapterView.OnItemSelectedListener {
            override fun onItemClick( parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

                val itemValue = parent?.getItemAtPosition(0).toString()
                vitalsTemplateMasterDetail = vitalsList[position]
                vitalsList[position].vital_master.uom_master_uuid =  typeList.filterValues { it == itemValue }.keys.toList()[0]

            }

            override fun onItemSelected( parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {

                val itemValue = parent?.getItemAtPosition(pos).toString()
                vitalsTemplateMasterDetail = vitalsList[position]
                vitalsList[position].vital_master.uom_master_uuid =  typeList.filterValues { it == itemValue }.keys.toList()[0]
                hashMapType[vitalsTemplateMasterDetail.uuid] = pos

            }
        }

        try {
            if (hashMapType.containsKey(vitalsTemplateMasterDetail.uuid)) {
                holder.spinner_type.setSelection(hashMapType[vitalsTemplateMasterDetail.uuid]!!);
            }
        }catch (e:Exception){
            Log.i("SpinnerMapErr",e.toString())
        }

        hashMapVitalsList.put(vitalsList.get(position).uuid,vitalsList.get(position).template_master_uuid)

    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun setOnDeleteClickListener(onDeleteClickListener: OnDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener
    }

    interface OnDeleteClickListener {
        fun onDeleteClick(
            templateDetail: TemplateMasterDetail,
            position: Int
        )
    }



    interface OnItemClickListener {
        fun onItemClick(
            templateDetail: TemplateDetail?,
            position: Int,
            selected: Boolean
        )
    }


    fun setOnSearchClickListener(onSearchClickListener: OnSearchClickListener) {
        this.onSearchClickListener = onSearchClickListener
    }

    interface OnSearchClickListener {
        fun onSearchClick(
                autoCompleteTextView: AppCompatAutoCompleteTextView,
                position: Int
        )
    }


    fun setAdapter(
            dropdownReferenceView: AppCompatAutoCompleteTextView,
            responseContents: ArrayList<GetVital>,
            searchPosition: Int?
    ) {
        val responseContentAdapter = VitalSearchAdapter(
                context,
                R.layout.row_chief_complaint_search_result,
                responseContents!!
        )
        dropdownReferenceView.threshold = 1
        dropdownReferenceView.setAdapter(responseContentAdapter)
        dropdownReferenceView.showDropDown()
        dropdownReferenceView.setOnItemClickListener { parent, _, position, id ->
            val selectedPoi = parent.adapter.getItem(position) as GetVital?

            val check= vitalsList.any{ it!!.test_master_uuid == selectedPoi?.uuid}

            if (!check) {

                dropdownReferenceView.setText(selectedPoi?.name)
                vitalsList[searchPosition!!].itemAppendString=selectedPoi!!.name
                vitalsList[searchPosition!!].test_master_uuid=selectedPoi!!.uuid
                notifyDataSetChanged()
                //addTemplateRow()
            }
            else{

                notifyDataSetChanged()
                Toast.makeText(context,"Already Item available in the list", Toast.LENGTH_LONG)?.show()

            }






        }
    }


    inner class VitalHolder constructor(view: View) : RecyclerView.ViewHolder(view) {

        internal val serialNumberTextView: TextView =
            itemView.findViewById(R.id.serialNumberTextView)
        internal val deleteImageView: ImageView = itemView.findViewById(R.id.deleteImageView)
        internal val nameTextView: AppCompatAutoCompleteTextView =
            itemView.findViewById(R.id.VitalName)
        internal val spinner_type:Spinner=itemView.findViewById(R.id.uomSpinner)
        internal var vitualEditText : EditText=itemView.findViewById(R.id.vitalEdittext)


    }

    fun addFavouritesInRow(templateMasterDetails: ArrayList<TemplateMasterDetail>) {

        for (i in templateMasterDetails.indices) {



            val check= vitalsList.any{ it!!.test_master_uuid == templateMasterDetails[i]?.test_master_uuid}

            if (!check) {


                vitalsList.add(templateMasterDetails.get(i))

                notifyDataSetChanged()
            }
            else{

                notifyDataSetChanged()
                Toast.makeText(context,"Already Item available in the list", Toast.LENGTH_LONG)?.show()

            }
        }
        notifyDataSetChanged()
    }

    fun deleteRowItem(templateMasterDetails: List<TemplateMasterDetail>) {
        for (i in templateMasterDetails.indices) {
            vitalsList.remove(templateMasterDetails[i])

        }
        notifyDataSetChanged()
    }

    @SuppressLint("NewApi")
    fun deleteItem(templateDetail: TemplateMasterDetail?, position: Int): Boolean {
        parentID = templateDetail?.template_master_uuid!!
        var ischeck:Boolean?=true
        vitalsList.remove(templateDetail)
        hashMapVitalsList.remove(templateDetail?.uuid)

        for ((key, value) in hashMapVitalsList.entries) {

            if (value == parentID) {
                ischeck=false
                break
            }
            else
            {
                ischeck = true
            }
        }
        notifyDataSetChanged()

        return ischeck!!
    }
    fun setTypeValue(responseContents: List<Uom>) {

        typeList=responseContents.map { it.uuid to it.name }.toMap().toMutableMap()

        notifyDataSetChanged()

    }

    fun getall(): ArrayList<TemplateMasterDetail> {

        return this.vitalsList

    }

    fun clearAll() {

        vitalsList= ArrayList()

        notifyDataSetChanged()
    }

    fun addTemplateRow() {

        val dummyData=TemplateMasterDetail()

        dummyData.itemAppendString="  "

        vitalsList.add(dummyData)

        notifyDataSetChanged()


    }


}









