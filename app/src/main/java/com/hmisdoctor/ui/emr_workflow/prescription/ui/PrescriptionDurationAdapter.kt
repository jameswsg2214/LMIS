package com.hmisdoctor.ui.emr_workflow.prescription.ui
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.duration.DurationResponseContent
import com.hmisdoctor.ui.emr_workflow.prescription.model.PrescriptionDurationResponseContent
import com.hmisdoctor.ui.emr_workflow.prescription.model.PrevPrescriptionResponseContents

class PrescriptionDurationAdapter(
    private val context: Context,
    private val durationArrayList: List<PrescriptionDurationResponseContent?>?
) :
    RecyclerView.Adapter<PrescriptionDurationAdapter.MyViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null
    private var selectedPosition = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_allergy_duration, parent, false)
        return MyViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val responseContent = durationArrayList?.get(position)!!
        holder.durationTextView.text = responseContent.name?.get(0).toString()
        holder.durationCardView.setOnClickListener {
            selectedPosition = responseContent.uuid!!
            onItemClickListener?.onItemClick(selectedPosition)
            // notifyDataSetChanged()
        }

        if (selectedPosition==responseContent.uuid!!){
            holder.durationCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
            holder.durationTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
        }else{
            holder.durationCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
            holder.durationTextView.setTextColor(ContextCompat.getColor(context, R.color.navColor))

        }
    }

    override fun getItemCount(): Int {
        return durationArrayList?.size!!
    }

    inner class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val durationTextView: TextView = itemView.findViewById(R.id.durationTextView)
        internal val durationCardView: CardView = itemView.findViewById(R.id.durationCardView)
    }


    interface OnItemClickListener {
        fun onItemClick(
            pos: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun updateSelectStatus(position: Int) {

        selectedPosition=position

        notifyDataSetChanged()

    }
    fun selectDurationPosition(position : Int){
        selectedPosition = position
        notifyDataSetChanged()
    }
    fun setFocus(get: Int?) {

    }
}


