package com.hmisdoctor.ui.emr_workflow.diet.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R


class DietAdapter(val context: Context) : RecyclerView.Adapter<DietAdapter.MyViewHolder>() {


    @SuppressLint("NewApi")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.row_diet, parent, false)

        return MyViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

    }


    override fun getItemCount(): Int {
        return 1
    }

    inner class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val serialNumberTextView: TextView =
            itemView.findViewById(R.id.serialNumberTextView)
        internal val autoCompleteTextView: AppCompatAutoCompleteTextView =
            itemView.findViewById(R.id.autoCompleteTextView)
        internal val deleteImageView: ImageView = itemView.findViewById(R.id.deleteImageView)
        internal val mainLinearLayout: LinearLayout = itemView.findViewById(R.id.mainLinearLayout)
        internal val spinner_type: Spinner = itemView.findViewById(R.id.type_spinner)
        internal val catagory: TextView = itemView.findViewById(R.id.tv_catagory)
        internal val frequency: TextView = itemView.findViewById(R.id.tv_frequency)

    }


}