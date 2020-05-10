package com.hmisdoctor.ui.emr_workflow.prescription.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.prescription.model.DrugDetail
import com.hmisdoctor.ui.emr_workflow.prescription.model.PrescriptionTempDetails
import com.hmisdoctor.ui.emr_workflow.prescription.model.Templates

class PrescriptionTemplateAdapter(
    private val context: Context
) :
    RecyclerView.Adapter<PrescriptionTemplateAdapter.MyViewHolder>() {
    private var templateResponseContent: List<Templates?>? = ArrayList()
    private var filter: List<Templates?>? = ArrayList()
    private var selectedArrayList: ArrayList<Templates?>? = ArrayList()
    private var onItemClickListener: OnItemClickListener? = null
    private var onItemDeleteClickListner: OnItemDeleteClickListner? = null
    private var onNextButtonEnableListener: OnNextButtonEnableListener? = null
    private var onItemViewClickListner : OnItemViewClickListner?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_favourites, parent, false)
        return MyViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val response = filter!![position]?.temp_details
        val responseoriginal = templateResponseContent!![position]
        holder.nameTextView.text = response?.template_name
        if (response?.isSelected!!){
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
            holder.nameTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
        }else{
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
            holder.nameTextView.setTextColor(ContextCompat.getColor(context, R.color.navColor))
        }
        holder.itemView.setOnClickListener {
            Log.i("",""+responseoriginal)
            onItemClickListener?.onItemClick(responseoriginal?.drug_details, position, response.isSelected!!,response?.template_id!!)
        }
        holder.moreImageView.setOnClickListener(View.OnClickListener {
            //creating a popup menu
            val popup =
                PopupMenu(context, holder.moreImageView)
            //inflating menu from xml resource
            popup.inflate(R.menu.options_menu)
            //adding click listener
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.edit ->
                    {
                        viewItem(responseoriginal)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.delete ->
                    {
                        deleteItem(response)
                        return@setOnMenuItemClickListener true
                    }

                    else -> return@setOnMenuItemClickListener false
                }
            }
            //displaying the popup
            popup.show()
        })

    }

    private fun viewItem(response: Templates?) {
        onItemViewClickListner?.onItemClick(response)
    }

    interface OnItemViewClickListner {
        fun onItemClick(responseContent: Templates?)
    }

    fun setOnItemViewClickListener(onItemViewClickListner: OnItemViewClickListner) {
        this.onItemViewClickListner = onItemViewClickListner
    }

    private fun deleteItem(response: PrescriptionTempDetails) {

        onItemDeleteClickListner?.onItemClick(response)
    }

    override fun getItemCount(): Int {
        return filter!!.size
    }

    inner class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        internal val cardView: CardView = itemView.findViewById(R.id.cardView)
        internal val moreImageView: ImageView = itemView.findViewById(R.id.moreImageView)

    }

    fun refreshList(responseContent: List<Templates>?) {
        this.templateResponseContent = responseContent
        this.filter = responseContent
        notifyDataSetChanged()
    }

    fun getFavouritesList(): List<Templates?>{
        return this.templateResponseContent!!
    }

    fun getSelectedArrayList(): List<Templates?>{
        return this.selectedArrayList!!
    }

    fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {

                val charString = charSequence.toString()

                if (charString.isEmpty()) {

                    filter = templateResponseContent
                } else {

                    val filteredList = java.util.ArrayList<Templates>()

                    for (messageList in templateResponseContent!!) {

                        if (messageList?.temp_details?.template_name != null) {
                            if (messageList?.temp_details?.template_name?.toLowerCase()?.contains(
                                    charString
                                )!!
                            ) {

                                filteredList.add(messageList)
                            }
                        }
                    }

                    filter = filteredList
                }

                val filterResults = Filter.FilterResults()
                filterResults.values = filter
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: Filter.FilterResults
            ) {
                filter = filterResults.values as java.util.ArrayList<Templates>
                notifyDataSetChanged()
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(
            responseContent: List<DrugDetail>?,
            position: Int,
            selected: Boolean,
            templateID : Int
        )
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemDeleteClickListner {
        fun onItemClick(responseContent: PrescriptionTempDetails?)
    }

    fun setOnItemDeleteClickListener(onItemDeleteClickListner: OnItemDeleteClickListner) {
        this.onItemDeleteClickListner = onItemDeleteClickListner
    }

    interface OnNextButtonEnableListener {
        fun onButtonsEnable(
            isEnable: Boolean
        )
    }

    fun setOnNextButtonEnableListener(onNextButtonEnableListener: OnNextButtonEnableListener) {
        this.onNextButtonEnableListener = onNextButtonEnableListener
    }

    fun updateSelectStatus(position: Int, selected: Boolean) {
        if (selected) {
            this.filter?.get(position)?.temp_details?.isSelected = false
            selectedArrayList?.remove(filter?.get(position))
        }else{
            this.filter?.get(position)?.temp_details?.isSelected = true
            selectedArrayList?.add(filter?.get(position))
        }
        if (selectedArrayList?.isNotEmpty()!!){
            onNextButtonEnableListener?.onButtonsEnable(true)
        }else{
            onNextButtonEnableListener?.onButtonsEnable(false)
        }
        notifyDataSetChanged()
    }

    fun refreshParticularData(position: Int) {
        this.filter?.get(position)?.temp_details?.isSelected= false
//        selectedArrayList!!.get(position)!!.temp_details?.isSelected=false
        notifyDataSetChanged()
    }

    fun refreshAllData() {
        for (i in filter!!.indices) {
            filter!!.get(i)!!.temp_details.isSelected = false
        }

        notifyDataSetChanged()
    }
}

