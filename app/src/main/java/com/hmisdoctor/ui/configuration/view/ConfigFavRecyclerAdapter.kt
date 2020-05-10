package com.hmisdoctor.ui.configuration.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.configuration.model.ConfigResponseContent
import com.hmisdoctor.ui.emr_workflow.model.ResponseContent
import com.journaldev.androidrecyclerviewdraganddrop.ItemMoveCallback
import com.journaldev.androidrecyclerviewdraganddrop.StartDragListener

import java.util.ArrayList
import java.util.Collections

class ConfigFavRecyclerAdapter(private val mStartDragListener: StartDragListener, private var configContent: ArrayList<ConfigResponseContent?>) : RecyclerView.Adapter<ConfigFavRecyclerAdapter.MyViewHolder>(),
    ItemMoveCallback.ItemTouchHelperContract {
    private var onItemClickListener: OnItemClickListener? = null
    private var boolean: Boolean? = false
    private var topos: Int? = 0;
    private var from_pos: Int? = 0
    //private var configFavArrList: List<com.hmisdoctor.ui.emr_workflow.model.EMR_Response.ResponseContent>? = null
    private lateinit var context : Context
    private var arrayListConfigData : ArrayList<ConfigResponseContent?> = ArrayList()


    class MyViewHolder(internal var rowView: View) : RecyclerView.ViewHolder(rowView) {

        val mTitle: TextView
        val diaplay_order: TextView
        internal var imageView: ImageView

        init {
            mTitle = rowView.findViewById(R.id.config_name)
            diaplay_order = rowView.findViewById(R.id.order_no)
            imageView = rowView.findViewById(R.id.item_remove)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.config_fav_list, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        holder.mTitle.text = configContent[position]!!.activity?.name

        holder.diaplay_order.text = configContent[position]!!.activity?.display_order.toString()

        holder.imageView.setOnClickListener {
            onItemClickListener?.onItemClick(configContent[position]!!, position)
        }
        holder.itemView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                mStartDragListener.requestDrag(holder)
            }
            false
        }

    }

    override fun getItemCount(): Int {
        return configContent.size
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        boolean = true
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(configContent, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(configContent, i, i - 1)
            }
        }
        topos = toPosition
        from_pos = fromPosition
        notifyItemMoved(fromPosition, toPosition)


    }

    override fun onRowSelected(myViewHolder: MyViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.GRAY)

    }

    override fun onRowClear(myViewHolder: MyViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.WHITE)
        notifyDataSetChanged();

    }

    fun setConfigfavList(configResponseContent: ConfigResponseContent?) {
        boolean = false
        this.configContent.add(configResponseContent)
        notifyDataSetChanged()
    }


    fun setConfigfavarrayList(workflowOrderResponseContent: List<ResponseContent?>) {

        boolean = false
        for(i in workflowOrderResponseContent?.indices)
        {
            val configResponseContent : ConfigResponseContent = ConfigResponseContent()
            configResponseContent.activity?.display_order =  workflowOrderResponseContent?.get(i)?.work_flow_order
            configResponseContent.activity?.name = workflowOrderResponseContent?.get(i)?.activity_name
            configResponseContent?.context_uuid = workflowOrderResponseContent?.get(i)?.context_id
            configResponseContent?.activity_uuid = workflowOrderResponseContent?.get(i)?.activity_id
            arrayListConfigData.add(configResponseContent)
        }

        this.configContent = arrayListConfigData
        notifyDataSetChanged()
    }

    fun getFinalData(): ArrayList<ConfigResponseContent?> {
        return configContent
    }

    interface OnItemClickListener {
        fun onItemClick(configfavResponseContent: ConfigResponseContent?, position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun getItemSize(): Int {

        return this.configContent.size
    }

    fun removeitem(position: Int) {
        this.configContent.removeAt(position)
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount() - position);

    }

    fun getConfigFavData(): ArrayList<ConfigResponseContent?> {
        return this.configContent
    }
}

