package com.hmisdoctor.ui.emr_workflow.prescription.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import com.hmisdoctor.ui.emr_workflow.prescription.model.ZeroStockResponseContent

class ZeroStockAdapter(private val context: Activity,
                       var zeroStockLIst: List<ZeroStockResponseContent?>):
    RecyclerView.Adapter<ZeroStockAdapter.MyViewHolder>() {
    private var responseContent: List<ZeroStockResponseContent?> = ArrayList()
    private var filter: List<ZeroStockResponseContent?> = ArrayList()
    inner class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val serialNumberTextView: TextView =
            itemView.findViewById(R.id.sNoTextView)
        internal val drugCode: TextView =
            itemView.findViewById(R.id.drugCode)
        internal val mainLinearLayout: LinearLayout = itemView.findViewById(R.id.mainLinearLayout)
        internal val drugNmae: TextView = itemView.findViewById(R.id.drugNmae)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.row_prescrtiption_zero_stock, parent, false)
        return MyViewHolder(view)    }

    override fun getItemCount(): Int {
        return filter.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val zeroStockLIst = filter.get(position)
        val pos = position+1
        holder.serialNumberTextView.text = pos.toString()
        holder.drugCode.text = zeroStockLIst?.item_master?.code
        holder.drugNmae.text = zeroStockLIst?.item_master?.name
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

    }
    fun setFavAddItem(responseContents: List<ZeroStockResponseContent?>) {
        responseContent = responseContents
        filter = responseContents
        notifyDataSetChanged()
    }

    fun getFilter(): Filter {

        return object : Filter() {
            @SuppressLint("DefaultLocale")
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {

                val charString = charSequence.toString()

                if (charString.isEmpty()) {

                    filter = responseContent
                } else {

                    val filteredList = java.util.ArrayList<ZeroStockResponseContent>()

                    for (messageList in responseContent!!) {

                        if (messageList?.item_master?.name != null) {
                            if (messageList.item_master.name!!.toLowerCase().contains(
                                    charString.toLowerCase()
                                )
                            ) {
                                filteredList.add(messageList)
                            }
                        }
                        if (messageList?.item_master?.code != null) {
                            if (messageList.item_master.code!!.toLowerCase().contains(
                                    charString?.toLowerCase()
                                )
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
                filter = filterResults.values as java.util.ArrayList<ZeroStockResponseContent>
                notifyDataSetChanged()
            }
        }
    }


}