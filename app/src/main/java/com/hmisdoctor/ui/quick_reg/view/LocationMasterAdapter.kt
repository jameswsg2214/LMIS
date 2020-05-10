package com.hmisdoctor.ui.quick_reg.view

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.search_complaint.ResponseContent
import com.hmisdoctor.ui.emr_workflow.lab.model.favresponse.FavSearch
import com.hmisdoctor.ui.quick_reg.model.LocationMaster
import com.hmisdoctor.ui.quick_reg.model.request.LabName

class LocationMasterAdapter(
    context: Context, @LayoutRes
    private val layoutResource: Int,
    private val allPois: ArrayList<LocationMaster>
) :
    ArrayAdapter<LocationMaster>(context, layoutResource, allPois),
    Filterable {
    private var mPois: List<LocationMaster> = allPois

    override fun getCount(): Int {
        return mPois.size
    }

    override fun getItem(p0: Int): LocationMaster? {
        return mPois.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return mPois.get(p0).uuid?.toLong()!!
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = (context as Activity).layoutInflater
        val view = inflater.inflate(layoutResource, parent, false)
        val name = view!!.findViewById<View>(R.id.nameTextView) as TextView
        val responseContent = getItem(position)
        name.text = responseContent!!.facility_name

        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(
                charSequence: CharSequence?,
                filterResults: Filter.FilterResults
            ) {
                try {
                    mPois = filterResults.values as List<LocationMaster>
                    notifyDataSetChanged()
                }catch (e : Exception){

                }
            }

            override fun performFiltering(charSequence: CharSequence?): Filter.FilterResults {
                val queryString = charSequence?.toString()?.toLowerCase()

                val filterResults = Filter.FilterResults()
                filterResults.values = if (queryString == null || queryString.isEmpty())
                    allPois
                else
                    allPois.filter {
                        it.facility_name?.toLowerCase()?.contains(queryString)!!
                    }
                return filterResults
            }
        }
    }
}