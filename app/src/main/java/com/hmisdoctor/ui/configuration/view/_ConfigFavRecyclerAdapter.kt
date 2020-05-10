/*
package com.hmisdoctor.ui.configuration.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.callbacks.ItemMoveMyPumpCallback
import com.hmisdoctor.ui.configuration.model.ConfigResponseContent
import com.journaldev.androidrecyclerviewdraganddrop.ItemMoveCallback
import com.journaldev.androidrecyclerviewdraganddrop.StartDragListener

import java.util.ArrayList
import java.util.Collections

class _ConfigFavRecyclerAdapter (
    private val startDragListener: StartDragListener,
    private var configContent: ArrayList<ConfigResponseContent?>,
    private val context: Context
) : RecyclerView.Adapter<_ConfigFavRecyclerAdapter.MyViewHolder>(),
    ItemMoveMyPumpCallback.ItemTouchHelperContract {
    override fun onRowClear(myViewHolder: ConfigFavRecyclerAdapter.MyViewHolder?) {
        myViewHolder?.itemView?.setBackgroundColor(Color.WHITE)
    }

    override fun onRowSelected(myViewHolder: ConfigFavRecyclerAdapter.MyViewHolder?) {
        myViewHolder?.itemView?.setBackgroundColor(context.resources.getColor(R.color.colorLightGrey))
    }

    override fun getItemCount(): Int {
        return configContent.size
    }


    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): _ConfigFavRecyclerAdapter.MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.config_fav_list, parent, false)
        return MyViewHolder(view)
    }



    override fun onBindViewHolder(@NonNull holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.mTitle.text = configContent[position]!!.activity?.name
        holder.itemView.setOnClickListener {
            Toast.makeText(
                context,
                "Press and hold to drag and align widget",
                Toast.LENGTH_SHORT
            ).show()
        }
        holder.itemView.setOnLongClickListener {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        50,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                vibrator.vibrate(100)
            }
            startDragListener.requestDrag(holder, 1)
            true
        }
    }


    public inner class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val mTitle: TextView

        init {
            mTitle = itemView.findViewById(R.id.config_name)
        }
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {

        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(configContent, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(configContent, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    fun onRowSelected(myViewHolder: MyViewHolder) {
        myViewHolder.itemView.setBackgroundColor(context.resources.getColor(R.color.colorLightGrey))
    }

    fun onRowClear(myViewHolder: MyViewHolder) {
        myViewHolder.itemView.setBackgroundColor(Color.WHITE)
//        realm.beginTransaction()
//        realm.delete(WidgetEnableMyPumpModel::class.java)
//        realm.commitTransaction()
//
//        val widgetEnableMyPumpModelArrayList = RealmList()
//        widgetEnableMyPumpModelArrayList.addAll(widgetEnableMyPumpModelRealmResults)
//
//        realm.beginTransaction()
//        realm.copyToRealmOrUpdate(widgetEnableMyPumpModelArrayList)
//        realm.commitTransaction()
    }

    fun setConfigfavList(configResponseContent: ConfigResponseContent?) {
        if(configContent.contains(configResponseContent))
        {
            Toast.makeText(context,"Already Exist",Toast.LENGTH_LONG).show()
        }
        else{
            this.configContent.add(configResponseContent)
            notifyDataSetChanged()
        }

    }
}


*/
