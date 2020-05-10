package com.hmisdoctor.ui.emr_workflow.investigation.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
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
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.search_complaint.ResponseContent
import com.hmisdoctor.ui.emr_workflow.chief_complaint.ui.ChiefComplaintSearchResultAdapter
import android.util.Log
import android.widget.*
import com.hmisdoctor.ui.emr_workflow.investigation.model.InvestigationSearchResponseContent
import com.hmisdoctor.ui.emr_workflow.lab.model.LabToLocationContent
import com.hmisdoctor.ui.emr_workflow.lab.model.LabTypeResponseContent




class InvestigationAdapter(
    private val context: Activity,
    private val favouritesArrayList: ArrayList<FavouritesModel?>,
    private val templeteArrayList: ArrayList<TempDetails?>
) :
    RecyclerView.Adapter<InvestigationAdapter.MyViewHolder>() {

    private var typeNamesList = mutableMapOf<Int, String>()
    private var toLocationMap = mutableMapOf<Int, String>()
    private lateinit var spinnerArray: MutableList<String>
    private var onItemClickListener: OnItemClickListener? = null
    private var onDeleteClickListener: OnDeleteClickListener? = null
    private var onSearchInitiatedListener: OnSearchInitiatedListener? = null
    private var durationArrayList: ArrayList<DurationResponseContent?>? = ArrayList()
    lateinit var selectedResponseContent: FavouritesModel

    @SuppressLint("NewApi")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.row_lab, parent, false)


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
                if (s.length > 2) {
                    onSearchInitiatedListener?.onSearchInitiated(
                        s.toString(),
                        holder.autoCompleteTextView
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
            onDeleteClickListener?.onDeleteClick(selectedResponseContent, position)
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
                Log.i(
                    "InvestigationType",
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
            view: AppCompatAutoCompleteTextView
        )
    }

    fun setOnSearchInitiatedListener(onSearchInitiatedListener: OnSearchInitiatedListener) {
        this.onSearchInitiatedListener = onSearchInitiatedListener
    }

    fun deleteRow(

        position1: Int
    ) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(context.currentFocus?.windowToken, 0)
        this.favouritesArrayList.removeAt(position1)
        notifyItemRemoved(position1);
        notifyItemRangeChanged(position1, getItemCount() - position1);
    }

    fun addFavouritesInRow(
        responseContent: FavouritesModel?
    ) {
        favouritesArrayList.removeAt(favouritesArrayList.size - 1)
        responseContent?.itemAppendString = responseContent?.test_master_name
        favouritesArrayList.add(responseContent)
        favouritesArrayList.add(FavouritesModel())
        notifyDataSetChanged()
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
        responseContents: ArrayList<InvestigationSearchResponseContent>
    ) {

        val responseContentAdapter = InvestigationSearchResultAdapter(
            context,
            R.layout.row_chief_complaint_search_result,
            responseContents
        )
        dropdownReferenceView.threshold = 1
        dropdownReferenceView.setAdapter(responseContentAdapter)
        dropdownReferenceView.setOnItemClickListener { parent, _, position, id ->
            val selectedPoi = parent.adapter.getItem(position) as InvestigationSearchResponseContent?
            dropdownReferenceView.setText(selectedPoi?.name)
            val st = FavouritesModel()
            st.chief_complaint_name = "dummy"
            selectedResponseContent.chief_complaint_name = selectedPoi?.name
            selectedResponseContent.itemAppendString = selectedPoi?.name
            if (!favouritesArrayList.contains(st)) {
                addRow(FavouritesModel())
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
/*
    fun getallDetails(arrayListFav: ArrayList<FavouritesModel>?): ArrayList<FavouritesModel>? {

        return arrayListFav

    }*/
}



