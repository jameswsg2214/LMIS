package com.hmisdoctor.ui.emr_workflow.prescription.ui

import android.content.Context
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
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel

class PrescriptionFavouriteAdpter(private val context: Context) :
    RecyclerView.Adapter<PrescriptionFavouriteAdpter.MyViewHolder>() {
    private var responseContent: List<FavouritesModel?>? = ArrayList()
    private var filter: List<FavouritesModel?>? = ArrayList()
    private var selectedArrayList: ArrayList<FavouritesModel?>? = ArrayList()
    private var onItemClickListener: OnItemClickListener? = null
    private var onItemDeleteClickListner: OnItemDeleteClickListner? = null
    private var onNextButtonEnableListener: OnNextButtonEnableListener? = null
    private var onItemViewClickListner : OnItemViewClickListner?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_favourites, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filter!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val response = filter!![position]
        holder.nameTextView.text = response?.drug_name as CharSequence?
        if (response?.isSelected!!) {
            holder.cardView.setCardBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorPrimary
                )
            )
            holder.nameTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
            holder.nameTextView.setTextColor(ContextCompat.getColor(context, R.color.navColor))

        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(response, position, response.isSelected!!)
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

                        viewItem(response)
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

    private fun viewItem(response: FavouritesModel) {
        onItemViewClickListner?.onItemClick(response)
    }

    private fun deleteItem(response: FavouritesModel) {

        onItemDeleteClickListner?.onItemClick(response)
    }

    interface OnItemViewClickListner {
        fun onItemClick(responseContent: FavouritesModel?)
    }

    fun setOnItemViewClickListener(onItemViewClickListner: OnItemViewClickListner) {
        this.onItemViewClickListner = onItemViewClickListner
    }

    inner class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        internal val cardView: CardView = itemView.findViewById(R.id.cardView)
        internal val moreImageView: ImageView = itemView.findViewById(R.id.moreImageView)

    }

    fun refreshList(responseContent: List<FavouritesModel?>?) {
        this.responseContent = responseContent
        this.filter = responseContent
        notifyDataSetChanged()
    }

    fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {

                val charString = charSequence.toString()

                if (charString.isEmpty()) {

                    filter = responseContent
                } else {

                    val filteredList = java.util.ArrayList<FavouritesModel>()

                    for (messageList in responseContent!!) {

                        if (messageList?.drug_name != null) {
                            if (messageList.drug_name?.toLowerCase()?.contains(
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
                filter = filterResults.values as java.util.ArrayList<FavouritesModel>
                notifyDataSetChanged()
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(
            responseContent: FavouritesModel?,
            position: Int,
            selected: Boolean
        )
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemDeleteClickListner {
        fun onItemClick(responseContent: FavouritesModel?)
    }

    fun setOnItemDeleteClickListener(onItemDeleteClickListner: OnItemDeleteClickListner) {
        this.onItemDeleteClickListner = onItemDeleteClickListner
    }

    interface OnNextButtonEnableListener {
        fun onButtonsEnable(
            isEnable: Boolean
        )
    }

    fun updateSelectStatus(position: Int, selected: Boolean) {
        if (selected) {
            this.filter?.get(position)?.isSelected = false
            selectedArrayList?.remove(filter?.get(position))
        } else {
            this.filter?.get(position)?.isSelected = true
            selectedArrayList?.add(filter?.get(position))
        }
        if (selectedArrayList?.isNotEmpty()!!) {
            onNextButtonEnableListener?.onButtonsEnable(true)
        } else {
            onNextButtonEnableListener?.onButtonsEnable(false)
        }
        notifyDataSetChanged()
    }

    fun refreshAllData() {

        for (i in filter!!.indices) {
            filter!!.get(i)!!.isSelected = false
        }

        notifyDataSetChanged()
    }

    fun refreshFavParticularData(position: Int) {

        filter!!.get(position)!!.isSelected = false
        //selectedArrayList!!.get(position)!!.isSelected=false
        notifyDataSetChanged()
    }

    fun checkunchecktempleteData(drug_id:Int,isSelect:Boolean){

        for (i in filter!!.indices) {

            if(filter!![i]!!.drug_id==drug_id) {

                filter!!.get(i)!!.isSelected = isSelect

            }

        }

        notifyDataSetChanged()

    }

    fun isCheckAlreadyExist(drugId: Int):Boolean {

        var isCheck:Boolean=false

        for (i in filter!!.indices)
        {
            if(filter!!.get(i)?.drug_id?.equals(drugId)!!)
            {
                isCheck=true
                break
            }

        }


        return isCheck
    }



    fun clearDataDrugid(drugId: Int){

        for (i in filter!!.indices)
        {
            if(filter!!.get(i)?.drug_id?.equals(drugId)!!)
            {
                filter!!.get(i)?.isSelected=false
                break
            }

        }

        notifyDataSetChanged()

    }


}

