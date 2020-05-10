package com.hmisdoctor.ui.emr_workflow.vitals.ui
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
import com.hmisdoctor.ui.emr_workflow.lab.ui.LabFavouritesAdapter
import com.hmisdoctor.ui.emr_workflow.lab.ui.LabTempleteAdapter
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import com.hmisdoctor.ui.emr_workflow.vitals.model.TemplateDetail
import com.hmisdoctor.ui.emr_workflow.vitals.model.TemplateMasterDetail

class VitalsTemplateAdapter(
    private val context: Context
) :
    RecyclerView.Adapter<VitalsTemplateAdapter.MyViewHolder>() {
    private var arrayListTemplatesModel: ArrayList<TemplateDetail>? = ArrayList()
    private var filter: ArrayList<TemplateDetail>? = ArrayList()
    private var selectedArrayList: ArrayList<TemplateDetail?>? = ArrayList()
    private var onItemClickListener: OnItemClickListener? = null
    private var onNextButtonEnableListener: OnNextButtonEnableListener? = null
    private var onItemDeleteClickListner: OnItemDeleteClickListner? = null
    private var onItemViewClickListner : OnItemViewClickListner?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_favourites, parent, false)
        return MyViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val response = filter!![position]
        val responseoriginal = arrayListTemplatesModel!![position]
        responseoriginal.position = position

        holder.nameTextView.text = response.name
        if (response.isSelected!!){
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
            holder.nameTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
        }else{
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
            holder.nameTextView.setTextColor(ContextCompat.getColor(context, R.color.navColor))
        }
        holder.itemView.setOnClickListener {
            Log.i("",""+responseoriginal)
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
                    R.id.edit ->  //handle menu1 click
                    {
                        viewItem(response)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.delete ->  //handle menu2 click
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
    private fun deleteItem(response: TemplateDetail) {
        onItemDeleteClickListner?.onItemClick(response)

    }

    fun setOnItemViewClickListener(onItemViewClickListner: OnItemViewClickListner) {
        this.onItemViewClickListner = onItemViewClickListner
    }

    private fun viewItem(response: TemplateDetail) {
        onItemViewClickListner?.onItemClick(response)
    }
    interface OnItemDeleteClickListner {
        fun onItemClick(responseContent: TemplateDetail?)
    }
    interface OnItemViewClickListner {
        fun onItemClick(responseContent: TemplateDetail?)
    }
    fun setOnItemDeleteClickListener(onItemDeleteClickListner:OnItemDeleteClickListner) {
        this.onItemDeleteClickListner = onItemDeleteClickListner
    }




    override fun getItemCount(): Int {
        return filter!!.size
    }

    inner class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        internal val cardView: CardView = itemView.findViewById(R.id.cardView)
        internal val moreImageView: ImageView = itemView.findViewById(R.id.moreImageView)

    }


    fun getFavouritesList(): List<TemplateDetail?>{
        return this.arrayListTemplatesModel!!
    }



    fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    filter = arrayListTemplatesModel
                } else {
                    val filteredList = java.util.ArrayList<TemplateDetail>()
                    for (messageList in arrayListTemplatesModel!!) {
                        if (messageList.name != null) {
                            if (messageList.name.toLowerCase().contains(charString)!!
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
                filter = filterResults.values as java.util.ArrayList<TemplateDetail>
                notifyDataSetChanged()
            }
        }
    }


    interface OnItemClickListener {
        fun onItemClick(
            favouritesModel: TemplateDetail,
            position: Int,
            selected: Boolean
        )
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnNextButtonEnableListener {
        fun onButtonsEnable(
            isEnable: Boolean
        )
    }

    fun setOnNextButtonEnableListener(onNextButtonEnableListener: OnNextButtonEnableListener) {
        this.onNextButtonEnableListener = onNextButtonEnableListener
    }

    fun refreshList(templateDetails: ArrayList<TemplateDetail>?) {
        this.arrayListTemplatesModel = templateDetails
        this.filter = arrayListTemplatesModel
        notifyDataSetChanged()
    }


    fun updateSelectStatus(position: Int, selected: Boolean) {
        if (selected) {
            this.filter?.get(position)?.isSelected = false
            selectedArrayList?.remove(filter?.get(position))
        }else{
            this.filter?.get(position)?.isSelected = true
            selectedArrayList?.add(filter?.get(position))
        }
        if (selectedArrayList?.isNotEmpty()!!){
            onNextButtonEnableListener?.onButtonsEnable(true)
        }else{
            onNextButtonEnableListener?.onButtonsEnable(false)
        }
        notifyDataSetChanged()
    }

    fun unselect(vitalsTemplatesModel: TemplateMasterDetail) {

        Log.i("","Sucsess")

        for (i in arrayListTemplatesModel!!.indices){
            if(arrayListTemplatesModel!!.get(i).uuid==vitalsTemplatesModel.template_master_uuid){
                arrayListTemplatesModel!!.get(i).isSelected=false
                break
            }
        }
        notifyDataSetChanged()
    }


    fun unSelectAll(){

        for (i in arrayListTemplatesModel!!.indices){

                arrayListTemplatesModel!!.get(i).isSelected=false

        }

        notifyDataSetChanged()


    }
}


