package com.hmisdoctor.ui.emr_workflow.treatment_kit.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import com.hmisdoctor.ui.emr_workflow.model.templete.TempDetails
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.duration.DurationResponseContent

import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.hmisdoctor.ui.emr_workflow.lab.model.LabToLocationContent
import com.hmisdoctor.ui.emr_workflow.lab.model.LabTypeResponseContent
import com.hmisdoctor.ui.emr_workflow.lab.model.favresponse.FavSearch
import com.hmisdoctor.ui.emr_workflow.prescription.ui.CommentDialogFragment
import com.hmisdoctor.ui.emr_workflow.prescription.ui.InfoDialogFragment
import com.hmisdoctor.ui.emr_workflow.radiology.ui.RadioSearchResultAdapter


class TreatmentKitRadiologyAdapter(
    private val context: Activity,
    private val favouritesArrayList: ArrayList<FavouritesModel?>,
    private val templeteArrayList: ArrayList<TempDetails?>
) :
    RecyclerView.Adapter<TreatmentKitRadiologyAdapter.MyViewHolder>() {

    private var typeNamesList = mutableMapOf<Int, String>()
    private var toLocationMap = mutableMapOf<Int, String>()
    private lateinit var spinnerArray: MutableList<String>
    private var onItemClickListener: OnItemClickListener? = null
    private var onDeleteClickListener: OnDeleteClickListener? = null
    private var onSearchInitiatedListener: OnSearchInitiatedListener? = null
    private var durationArrayList: ArrayList<DurationResponseContent?>? = ArrayList()
    lateinit var selectedResponseContent: FavouritesModel

    private val hashMapType: HashMap<Int,Int> = HashMap()
    private val hashMapOrderToLocation: HashMap<Int,Int> = HashMap()

    @SuppressLint("NewApi")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.row_treatment_kit_radiology, parent, false)
       return MyViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        selectedResponseContent = favouritesArrayList[position]!!
        holder.serialNumberTextView.text = (position + 1).toString()
        holder.autoCompleteTextView.setText(selectedResponseContent.itemAppendString, false)
        holder.codeTextView.setText(selectedResponseContent.test_master_code.toString())

        holder.autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.length > 2 && s.length<5) {
                    onSearchInitiatedListener?.onSearchInitiated(
                        s.toString(),
                        holder.autoCompleteTextView,
                            position
                    )
                }
            }
        })

        if (position % 2 == 0) {
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
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(selectedResponseContent, position)
        }
        holder.deleteImageView.setOnClickListener {
            selectedResponseContent = favouritesArrayList[position]!!

            if(!holder.autoCompleteTextView.text.trim().isNullOrEmpty()){

                onDeleteClickListener?.onDeleteClick(selectedResponseContent, position)
            }}

        if (position == favouritesArrayList.size - 1) {
            holder.deleteImageView.alpha = 0.2f
            holder.deleteImageView.isEnabled = false
        } else {
            holder.deleteImageView.alpha = 1f
            holder.deleteImageView.isEnabled = true
        }

        val gridLayoutManager = GridLayoutManager(
            context, 1,
            LinearLayoutManager.HORIZONTAL, false
        )
        val adapter =
            ArrayAdapter<String>(
                context,
                android.R.layout.simple_spinner_item,
                typeNamesList.values.toMutableList()
            )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.spinner_type.adapter = adapter
        try {
            if (hashMapType!!.containsKey(selectedResponseContent.test_master_id!!)) {
                holder.spinner_type.setSelection(hashMapType!!.get(selectedResponseContent.test_master_id!!)!!);
            }
        }catch (e:Exception){
            Log.i("SpinnerMapErr",e.toString())
        }

        holder.spinner_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }


            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                pos: Int,
                id: Long
            ) {
                val itemValue = parent?.getItemAtPosition(pos).toString()
                val favmodel: FavouritesModel? = FavouritesModel()
                selectedResponseContent = favouritesArrayList[position]!!
                selectedResponseContent.selectTypeUUID =  typeNamesList.filterValues { it == itemValue }.keys.toList()[0]
                hashMapType.put(selectedResponseContent.test_master_id!!,pos)

                Log.i(
                    "LabType",
                    "name = " + itemValue + "uuid=" + typeNamesList.filterValues { it == itemValue }.keys.toList()[0]
                )
            }

        }


        val locationAdapter =
            ArrayAdapter<String>(
                context,
                android.R.layout.simple_spinner_item,
                toLocationMap.values.toMutableList()
            )
        adapter.setDropDownViewResource(R.layout.spinner_item);
        holder.spinnerToLocation.adapter = locationAdapter
        try {
            if (hashMapOrderToLocation.containsKey(selectedResponseContent.test_master_id!!)) {
                holder.spinnerToLocation.setSelection(hashMapOrderToLocation!!.get(selectedResponseContent.test_master_id!!)!!);
            }
        }catch (e:Exception){
            Log.i("SpinnerMapErr",e.toString())
        }
        holder.spinnerToLocation.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                    val itemValue = parent?.getItemAtPosition(0).toString()
                    selectedResponseContent.selectToLocationUUID =
                            toLocationMap.filterValues { it == itemValue }.keys.toList()[0]

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    pos: Int,
                    id: Long
                ) {
                    val itemValue = parent?.getItemAtPosition(pos).toString()
                    selectedResponseContent = favouritesArrayList[position]!!
                    selectedResponseContent.selectToLocationUUID =
                        toLocationMap.filterValues { it == itemValue }.keys.toList()[0]
                    hashMapOrderToLocation.put(selectedResponseContent.test_master_id!!,pos)
                }
            }
        holder.infoImageView.setOnClickListener{
            selectedResponseContent = favouritesArrayList[position]!!
            val ft = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            val bundle = Bundle()
            val dialog = InfoDialogFragment()
            bundle.putString("DrugID", selectedResponseContent.test_master_id.toString())
            dialog.setArguments(bundle)

            dialog.show(ft, "Tag")

        }

        holder.commentsImageView.setOnClickListener{
            val ft = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            val dialog = CommentDialogFragment()
            dialog.show(ft, "Tag")



        }

    }

    override fun getItemCount(): Int {
        return favouritesArrayList!!.size
    }

    inner class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val serialNumberTextView: TextView =
            itemView.findViewById(R.id.serialNumberTextView)
        internal val deleteImageView: ImageView = itemView.findViewById(R.id.deleteImageView)
        internal val autoCompleteTextView: AppCompatAutoCompleteTextView =
            itemView.findViewById(R.id.autoCompleteTextView)
        internal val mainLinearLayout: LinearLayout = itemView.findViewById(R.id.mainLinearLayout)
        internal val spinner_type: Spinner = itemView.findViewById(R.id.type_spinner)
        internal val spinnerToLocation: Spinner = itemView.findViewById(R.id.tolocation)
        internal val codeTextView: TextView = itemView.findViewById(R.id.codeTextView)
        internal val infoImageView: ImageView = itemView.findViewById(R.id.infoImageView)
        internal val commentsImageView: ImageView = itemView.findViewById(R.id.commentsImageView)
    }

    interface OnItemClickListener {
        fun onItemClick(
            responseContent: FavouritesModel?,
            position: Int
        )
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnDeleteClickListener {
        fun onDeleteClick(
            responseContent: FavouritesModel?,
            position: Int
        )
    }

    fun setOnDeleteClickListener(onDeleteClickListener: OnDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener
    }

    interface OnSearchInitiatedListener {
        fun onSearchInitiated(
                query: String,
                view: AppCompatAutoCompleteTextView,
                position: Int
        )
    }

    fun setOnSearchInitiatedListener(onSearchInitiatedListener: OnSearchInitiatedListener) {
        this.onSearchInitiatedListener = onSearchInitiatedListener
    }

    fun deleteRow(
        position1: Int
    ): Boolean {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(context.currentFocus?.windowToken, 0)

        val data=favouritesArrayList[position1]

        this.favouritesArrayList.removeAt(position1)

        var ischeck:Boolean=true
        for (i in this.favouritesArrayList.indices){
            if(favouritesArrayList[i]!!.template_id==data!!.template_id){
                ischeck=false
                break
            }
        }


        notifyItemRemoved(position1);
        notifyDataSetChanged()

        return ischeck
    }
    /*
  Delete row from template
   */

    fun deleteRowFromTemplate(
        tempId: Int?,
        position1: Int
    ) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(context.currentFocus?.windowToken, 0)
        val OriginalItemCount = itemCount
        if(favouritesArrayList.size > 0)
        {
            favouritesArrayList.removeAt(favouritesArrayList.size - 1);
        }

        for (i in favouritesArrayList.indices)
        {
            if(favouritesArrayList.get(i)?.test_master_id?.equals(tempId!!)!!)
            {
                this.favouritesArrayList.removeAt(i)
                notifyItemRemoved(i);
                break
            }

        }
        notifyDataSetChanged()
        addRow(FavouritesModel())
    }

    fun addFavouritesInRow(
        responseContent: FavouritesModel?
    ) {
        val check= this.favouritesArrayList.any{ it!!.test_master_id == responseContent?.test_master_id}

        if (!check) {

            favouritesArrayList.removeAt(favouritesArrayList.size - 1)
            responseContent?.itemAppendString = responseContent?.test_master_name
            favouritesArrayList.add(responseContent)
            favouritesArrayList.add(FavouritesModel())
            notifyDataSetChanged()
        }
        else{

            notifyDataSetChanged()

            Toast.makeText(context,"Already Item available in the list",Toast.LENGTH_LONG)?.show()

        }


    }

    fun addRow(
        responseContent: FavouritesModel?

    ) {
        favouritesArrayList.add(responseContent)
        notifyItemInserted(favouritesArrayList.size - 1)
    }

    fun addTempleteRow(
        responseContent: TempDetails?
    ) {
        templeteArrayList.add(responseContent)
        notifyItemInserted(templeteArrayList.size - 1)
    }

    fun setDuration(durationArrayList_: ArrayList<DurationResponseContent?>) {
        this.durationArrayList = durationArrayList_
        notifyDataSetChanged()
    }

    fun setAdapter(
            dropdownReferenceView: AppCompatAutoCompleteTextView,
            responseContents: ArrayList<FavSearch>,
            searchPosition: Int?
    ) {

        val responseContentAdapter = RadioSearchResultAdapter(
            context,
            R.layout.row_chief_complaint_search_result,
            responseContents
        )
        dropdownReferenceView.threshold = 1
        dropdownReferenceView.setAdapter(responseContentAdapter)
        dropdownReferenceView.setOnItemClickListener { parent, _, position, id ->
            val selectedPoi = parent.adapter.getItem(position) as FavSearch?

            val check= this.favouritesArrayList.any{ it!!.test_master_id == selectedPoi?.uuid}

            if (!check) {

                favouritesArrayList[searchPosition!!]!!.chief_complaint_name = selectedPoi?.name
                favouritesArrayList[searchPosition]!!.itemAppendString = selectedPoi?.name
                favouritesArrayList[searchPosition]!!.test_master_id=selectedPoi?.uuid
                favouritesArrayList[searchPosition]!!.test_master_code=selectedPoi?.code
                notifyDataSetChanged()
                addRow(FavouritesModel())
            }
            else{

                notifyDataSetChanged()

                Toast.makeText(context,"Already Item available in the list",Toast.LENGTH_LONG)?.show()

            }
        }
    }

    fun setadapterTypeValue(responseContents: List<LabTypeResponseContent?>?) {

        typeNamesList = responseContents?.map { it?.uuid!! to it.name!! }!!.toMap().toMutableMap()
        notifyDataSetChanged()

    }

    fun setToLocationList(responseContents: List<LabToLocationContent?>?) {
        toLocationMap =
            responseContents?.map { it?.uuid!! to it.location_name!! }!!.toMap().toMutableMap()
        notifyDataSetChanged()

    }
    fun getItems(): ArrayList<FavouritesModel?> {
        return favouritesArrayList
    }

    fun clearall(){

        favouritesArrayList.clear()
        notifyDataSetChanged()

    }

    fun getAll(): ArrayList<FavouritesModel?> {

        return this.favouritesArrayList

    }

}



