package com.hmisdoctor.ui.emr_workflow.lab.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.duration.DurationResponseContent
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.search_complaint.ResponseContent
import com.hmisdoctor.ui.emr_workflow.chief_complaint.ui.ChiefComplaintSearchResultAdapter
import com.hmisdoctor.ui.emr_workflow.lab.model.LabSearchResultAdapter
import com.hmisdoctor.ui.emr_workflow.lab.model.LabToLocationContent
import com.hmisdoctor.ui.emr_workflow.lab.model.LabTypeResponseContent
import com.hmisdoctor.ui.emr_workflow.lab.model.favresponse.FavSearch
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import com.hmisdoctor.ui.emr_workflow.model.templete.TempDetails
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class LabAdapter(
    private val context: Activity,
    private val favouritesArrayList: ArrayList<FavouritesModel?>,
    private val templeteArrayList: ArrayList<TempDetails?>) : RecyclerView.Adapter<LabAdapter.MyViewHolder>() {
    private val hashMapType: HashMap<Int,Int> = HashMap()
    private val hashMapOrderToLocation: HashMap<Int,Int> = HashMap()
    private var typeNamesList = mutableMapOf<Int, String>()
    private var toLocationMap = mutableMapOf<Int, String>()
    private lateinit var spinnerArray: MutableList<String>
    private var onItemClickListener: OnItemClickListener? = null
    private var onDeleteClickListener: OnDeleteClickListener? = null
    private var onSearchInitiatedListener: OnSearchInitiatedListener? = null
    private var durationArrayList: ArrayList<DurationResponseContent?>? = ArrayList()
    lateinit var selectedResponseContent: FavouritesModel

    var hashMapLabList : HashMap<Int,Int> = HashMap()


    @SuppressLint("NewApi")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.row_lab, parent, false)
        return MyViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        selectedResponseContent = favouritesArrayList[position]!!
        hashMapLabList.put(favouritesArrayList[position]!!.test_master_id!!,favouritesArrayList[position]!!.template_id)

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
                        holder.autoCompleteTextView,position
                    )
                }}
        })
        if (position % 2 == 0) {
            holder.mainLinearLayout.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.alternateRow
                )
            )
        } else {
            holder.mainLinearLayout.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(selectedResponseContent, position)
        }
        holder.deleteImageView.setOnClickListener {

            selectedResponseContent = favouritesArrayList[position]!!

            if(!holder.autoCompleteTextView.text.trim().isEmpty()){

                 onDeleteClickListener?.onDeleteClick(selectedResponseContent, position)
                hashMapOrderToLocation.remove(selectedResponseContent.test_master_id)
                hashMapType.remove(selectedResponseContent.test_master_id)

            }

        }

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
            if (hashMapType.containsKey(selectedResponseContent.test_master_id!!)) {
                holder.spinner_type.setSelection(hashMapType.get(selectedResponseContent.test_master_id!!)!!)
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
                R.layout.spinner_item,
                toLocationMap.values.toMutableList()
            )
        adapter.setDropDownViewResource(R.layout.spinner_item);
        holder.spinnerToLocation.adapter = locationAdapter
        try {
            if (hashMapOrderToLocation.containsKey(selectedResponseContent?.test_master_id)) {
                holder.spinnerToLocation.setSelection(hashMapOrderToLocation!!.get(selectedResponseContent?.test_master_id)!!);
            }
        }catch (e:Exception){
            Log.i("SpinnerMapErr",e.toString())
        }
        holder.spinnerToLocation.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
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

    fun clearall(){

        favouritesArrayList.clear()
        notifyDataSetChanged()

    }


    fun clearallAddone() {
        favouritesArrayList.clear()
        favouritesArrayList.add(FavouritesModel())
        notifyDataSetChanged()
    }
    fun deleteRow(
       position1: Int
    ):Boolean {

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
            if(favouritesArrayList.get(i)?.test_master_id?.equals(tempId!!)!!&& favouritesArrayList.get(i)?.viewLabstatus?.equals(position1!!)!! )
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
            val check= favouritesArrayList.any{ it!!.test_master_id == responseContent?.test_master_id}
            if (!check) {
                favouritesArrayList.removeAt(favouritesArrayList.size - 1)
                responseContent?.itemAppendString = responseContent?.test_master_name
                responseContent?.test_master_id=responseContent?.test_master_id
                favouritesArrayList.add(responseContent)
                favouritesArrayList.add(FavouritesModel())
                notifyDataSetChanged()
            }
            else{
                notifyDataSetChanged()
                Toast.makeText(context,"Already Item available in the list",Toast.LENGTH_LONG)?.show()
            }

    }



    fun addSaveTemplateInRow(
            responseContent: FavouritesModel?
    ) {


        val check= favouritesArrayList.any{ it!!.test_master_id == responseContent?.test_master_id}

        if (!check) {
            favouritesArrayList.removeAt(favouritesArrayList.size - 1)
            responseContent?.itemAppendString = responseContent?.test_master_name
            responseContent?.test_master_id=responseContent?.test_master_id
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
        val check= favouritesArrayList.any{ it!!.test_master_id == responseContent?.test_master_id}

        if (!check) {
            favouritesArrayList.add(responseContent)
            notifyItemInserted(favouritesArrayList.size - 1)
        }
        else{
            notifyDataSetChanged()
            Toast.makeText(context,"Already Item available in the list",Toast.LENGTH_LONG)?.show()
        }}
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
            searchposition: Int
    ) {
        val responseContentAdapter = LabSearchResultAdapter(
            context,
            R.layout.row_chief_complaint_search_result,
            responseContents
        )
        dropdownReferenceView.threshold = 1
        dropdownReferenceView.setAdapter(responseContentAdapter)
        dropdownReferenceView.showDropDown()

        dropdownReferenceView.setOnItemClickListener { parent, _, pos, id ->
            val selectedPoi = parent.adapter.getItem(pos) as FavSearch?


           val check= favouritesArrayList.any{ it!!.test_master_id == selectedPoi?.uuid}

            if (!check) {

                dropdownReferenceView.setText(selectedPoi?.name)
                favouritesArrayList[searchposition]!!.chief_complaint_name = selectedPoi?.name
                favouritesArrayList[searchposition]!!.itemAppendString = selectedPoi?.name
                favouritesArrayList[searchposition]!!.test_master_id=selectedPoi?.uuid
                favouritesArrayList[searchposition]!!.test_master_code=selectedPoi?.code
                favouritesArrayList[searchposition]!!.test_master_name=selectedPoi?.name
                notifyDataSetChanged()
                addRow(FavouritesModel())
            }
            else{

                notifyDataSetChanged()
                Toast.makeText(context,"Already Item available in the list",Toast.LENGTH_LONG)?.show()

            }

        }}

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

    fun getAll():ArrayList<FavouritesModel?> {

        return this!!.favouritesArrayList

    }

    fun addPrevInRow(
        responseContent: FavouritesModel?
    ) {


        val check= favouritesArrayList.any{ it!!.test_master_id == responseContent?.test_master_id}

        if (!check) {
//            favouritesArrayList.removeAt(favouritesArrayList.size - 1)
            responseContent?.itemAppendString = responseContent?.test_master_name
            responseContent?.test_master_id=responseContent?.test_master_id
            favouritesArrayList.add(responseContent)
            favouritesArrayList.add(FavouritesModel())
            notifyDataSetChanged()
        }
        else{

            notifyDataSetChanged()

            Toast.makeText(context,"Already Item available in the list",Toast.LENGTH_LONG)?.show()

        }
    }

}


