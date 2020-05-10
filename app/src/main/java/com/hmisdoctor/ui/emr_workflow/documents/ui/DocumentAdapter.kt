package com.hmisdoctor.ui.emr_workflow.documents.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.documents.model.Attachment

class DocumentAdapter(context: Context, private var documentArrayList: ArrayList<Attachment>) :
    RecyclerView.Adapter<DocumentAdapter.MyViewHolder>() {
    private val mLayoutInflater: LayoutInflater
    private val mContext: Context
    var orderNumString: String? = null
    private val hashMapType: HashMap<Int,Int> = HashMap()
    private val hashMapDocument: HashMap<Int,Int> = HashMap()
    var status: String? = null
    private var onItemClickListener: OnItemClickListener? = null
    private var onItemdownloadClickListener: OnItemdownloadClickListener? = null



    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var serialNumberTextView: TextView
        var typeTextView: TextView
        var visitDate: TextView
        var fileName: TextView
        var commentsText: TextView
        var mainLinearLayout: LinearLayout
        var deleteImageView: ImageView
        var downloadImageview : ImageView



        init {
            serialNumberTextView = view.findViewById<View>(R.id.serialNumberTextView) as TextView
            typeTextView = view.findViewById<View>(R.id.typeTextView) as TextView
            visitDate = view.findViewById<View>(R.id.visitDate) as TextView
            fileName = view.findViewById<View>(R.id.fileName) as TextView
            commentsText = view.findViewById<View>(R.id.commentsText) as TextView
            mainLinearLayout = view.findViewById(R.id.mainLinearLayout)
            deleteImageView = view.findViewById(R.id.deleteImageView)
            downloadImageview = view.findViewById(R.id.downloadImageview)



        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemLayout = LayoutInflater.from(mContext)
            .inflate(R.layout.row_document_list, parent, false) as LinearLayout
        var recyclerView: RecyclerView
        return MyViewHolder(itemLayout)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        orderNumString = documentArrayList[position].toString()
        holder.serialNumberTextView.text = (position + 1).toString()
        val documentLIst = documentArrayList[position]
        holder.visitDate.text = documentLIst.attached_date
        holder.typeTextView.text = documentLIst.attachment_type.name
        holder.commentsText.text = documentLIst.comments
        holder.fileName.text = documentLIst.attachment_name
        if (position % 2 == 0) {
            holder.mainLinearLayout.setBackgroundColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.alternateRow
                )
            )
        } else {
            holder.mainLinearLayout.setBackgroundColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.white
                )
            )
        }
        holder.downloadImageview.setOnClickListener({
            onItemdownloadClickListener?.onItemParamClick(documentLIst)
        })
        holder.deleteImageView.setOnClickListener({
            onItemClickListener?.onItemParamClick(documentLIst)
        })

    }
    fun setOnDeleteClickListener(OnitemClickListener:OnItemClickListener) {
        onItemClickListener = OnitemClickListener
    }

    fun setOnItemdowloadClickListener(onItemDownloadListener:OnItemdownloadClickListener) {
        onItemdownloadClickListener = onItemDownloadListener
    }

    fun refreshList(documentArrayList: ArrayList<Attachment>?) {
        this.documentArrayList = documentArrayList!!
        this.notifyDataSetChanged()
    }




    override fun getItemCount(): Int {
        return documentArrayList.size
    }
    interface OnItemClickListener {
        fun onItemParamClick(
            documentId: Attachment
        )


    }
    interface OnItemdownloadClickListener {
        fun onItemParamClick(
            documentId: Attachment
        )


    }





    fun clearadapter() {
        this?.documentArrayList?.clear()
        notifyDataSetChanged()
    }



    init {
        mLayoutInflater = LayoutInflater.from(context)
        mContext = context
    }
}