package com.hmisdoctor.ui.emr_workflow.treatment_kit.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.diagnosis.model.diagonosissearch.ResponseContentsdiagonosissearch
import com.hmisdoctor.ui.emr_workflow.diagnosis.view.DianosisSearchResultAdapter
import com.hmisdoctor.ui.emr_workflow.diagnosis.view.snomed_dialog.response.SnomedData
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel

class TreatmentKitDiagnosisAdapter(
    private val context: Activity,
    private val favouritesArrayList: ArrayList<FavouritesModel?>
) :
    RecyclerView.Adapter<TreatmentKitDiagnosisAdapter.MyViewHolder>() {

    private var onSearchDignisisInitiatedListener:OnSearchDignisisInitiatedListener?=null
    private var onDeleteClickListener: OnDeleteClickListener? = null
    lateinit var selectedResponseContent: FavouritesModel
    /* private var onItemClickListener: OnItemClickListener? = null

     private var durationArrayList: ArrayList<DurationResponseContent?>? = ArrayList()*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_treatment_diagnosis_list, parent, false)
        return MyViewHolder(view)
    }


    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val chiefComplaintsModel = favouritesArrayList[position]!!
        holder.serialNumberTextView.text = (position + 1).toString()


        if(chiefComplaintsModel.is_snomed==0) {

            holder.typeTextView.text = "ICD 10"
        }
        else{
            holder.typeTextView.text = "Snomed"

        }
        holder.autoCompleteTextView.setText(chiefComplaintsModel.itemAppendString)



        holder.autoCompleteTextView.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.length > 2 && s.length<5) {
                    onSearchDignisisInitiatedListener?.onSearchInitiated(
                        s.toString(),
                        holder.autoCompleteTextView,
                        position
                    )
                }
            }
        })


        if (position % 2 == 0) {
            holder.mainLinearLayout.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
        } else {
            holder.mainLinearLayout.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.alternateRow
                )
            )
        }


        holder.deleteImageView.setOnClickListener {
            onDeleteClickListener?.onDeleteClick(chiefComplaintsModel, position)
        }

        if (position == favouritesArrayList.size - 1) {
            holder.deleteImageView.alpha = 0.2f
            holder.deleteImageView.isEnabled = false
        } else {
            holder.deleteImageView.alpha = 1f
            holder.deleteImageView.isEnabled = true
        }
    }

    override fun getItemCount(): Int {
        return favouritesArrayList.size
    }

    inner class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val serialNumberTextView: TextView = itemView.findViewById(R.id.diagnosisSerialNumberTextView)
        internal val deleteImageView: ImageView = itemView.findViewById(R.id.diagnosisDeleteImageView)
        internal val autoCompleteTextView: AppCompatAutoCompleteTextView = itemView.findViewById(R.id.diagnosisautoCompleteTextView)
        internal val mainLinearLayout: LinearLayout = itemView.findViewById(R.id.diagnosisMainLinearLayout)
        internal val typeTextView: TextView = itemView.findViewById(R.id.diagnosisTypeTextView)
    }

    interface OnItemClickListener {
        fun onItemClick(
            favouritesModel: FavouritesModel?,
            position: Int
        )
    }

    /*fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }*/

    interface OnDeleteClickListener {
        fun onDeleteClick(
            favouritesModel: FavouritesModel?,
            position: Int
        )
    }

    fun setOnDeleteClickListener(onDeleteClickListener: OnDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener
    }

    interface OnSearchDignisisInitiatedListener {
        fun onSearchInitiated(
            query: String,
            view: AppCompatAutoCompleteTextView,
            position: Int
        )
    }
    fun setOnSearchInitiatedListener(OnSearchDignisisInitiatedListener: OnSearchDignisisInitiatedListener) {
        this.onSearchDignisisInitiatedListener = OnSearchDignisisInitiatedListener
    }

    fun deleteRow(
        favouritesModel: FavouritesModel?,
        position1: Int
    ) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(context.currentFocus?.windowToken, 0)
        this.favouritesArrayList.removeAt(position1)
        notifyDataSetChanged()

    }

    fun deleteRowFromFavourites(
        favouritesModel: FavouritesModel?,
        position1: Int
    ) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(context.currentFocus?.windowToken, 0)
        this.favouritesArrayList.remove(favouritesModel)
        notifyDataSetChanged()

    }


    fun addFavouritesInRow(
        favouritesModel: FavouritesModel?
    ) {



        val check= favouritesArrayList.any{ it!!.diagnosis_id == favouritesModel?.diagnosis_id}

        if (!check) {

            favouritesArrayList.removeAt(favouritesArrayList.size - 1)
            favouritesModel?.itemAppendString = favouritesModel?.diagnosis_name
            favouritesArrayList.add(favouritesModel)
            favouritesArrayList.add(FavouritesModel())
            notifyDataSetChanged()
        }
        else{
            notifyDataSetChanged()
            Toast.makeText(context,"Already Item available in the list", Toast.LENGTH_LONG)?.show()

        }

    }

    fun addRow(
        favouritesModel: FavouritesModel?
    ) {
        favouritesArrayList.add(favouritesModel)
        notifyDataSetChanged()
    }

    fun addTempleteRow(
        responseContent: FavouritesModel?
    ) {

        val check= favouritesArrayList.any{ it!!.diagnosis_id == responseContent?.diagnosis_id}

        if (!check) {

            favouritesArrayList.add(responseContent)
            notifyItemInserted(favouritesArrayList.size - 1)
        }
        else{

            notifyDataSetChanged()
            Toast.makeText(context,"Already Item available in the list", Toast.LENGTH_LONG)?.show()

        }
    }

    fun clearall(){
        favouritesArrayList.clear()
        notifyDataSetChanged()
    }

    fun getall(): ArrayList<FavouritesModel?> {
        return favouritesArrayList }

    fun setAdapter(
        dropdownReferenceView: AppCompatAutoCompleteTextView,
        responseContents: List<ResponseContentsdiagonosissearch?>,
        selectedSearchPosition: Int
    ) {

        val responseContentAdapter = DianosisSearchResultAdapter(
            context,
            R.layout.row_chief_complaint_search_result,
            responseContents
        )
        dropdownReferenceView.threshold = 1
        dropdownReferenceView.setAdapter(responseContentAdapter)
        dropdownReferenceView.showDropDown()
        dropdownReferenceView.setOnItemClickListener { parent, _, position, id ->
            val selectedPoi = parent.adapter.getItem(position) as ResponseContentsdiagonosissearch?

            val check= favouritesArrayList.any{ it!!.diagnosis_id == selectedPoi?.uuid.toString()}

            if (!check) {

                dropdownReferenceView.setText(selectedPoi?.name)
                favouritesArrayList[selectedSearchPosition]?.diagnosis_name = selectedPoi?.name
                favouritesArrayList[selectedSearchPosition]?.itemAppendString = selectedPoi?.name
                favouritesArrayList[selectedSearchPosition]?.diagnosis_id = selectedPoi?.uuid.toString()
                favouritesArrayList[selectedSearchPosition]?.diagnosis_code = selectedPoi?.code.toString()
                notifyDataSetChanged()
                addRow(FavouritesModel())
            }
            else{

                notifyDataSetChanged()

                Toast.makeText(context,"Already Item available in the list", Toast.LENGTH_LONG)?.show()

            }
        }
    }

    fun addSnomed(data: SnomedData) {

        var favouritesModel:FavouritesModel= FavouritesModel()

        favouritesModel.itemAppendString=data.ConceptName

        favouritesModel.diagnosis_id=data.ConceptId


    }


}


