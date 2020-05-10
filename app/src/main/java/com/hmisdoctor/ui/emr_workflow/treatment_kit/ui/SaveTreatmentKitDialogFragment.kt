package com.hmisdoctor.ui.emr_workflow.treatment_kit.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.hmisdoctor.R
import com.hmisdoctor.config.AppConstants
import com.hmisdoctor.databinding.SaveTemplateDialogFragmentBinding
import com.hmisdoctor.databinding.TreatmentKitChildFragmentBindingImpl
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.savetemplate.SaveTemplateAdapter
import com.hmisdoctor.ui.emr_workflow.treatment_kit.model.request.CreateTreatmentkitRequestModel
import com.hmisdoctor.ui.emr_workflow.treatment_kit.view_model.TreatmentKitViewModel

class SaveTreatmentKitDialogFragment : DialogFragment() {

    private var content: String? = null
    private var viewModel: TreatmentKitViewModel? = null
    var binding: TreatmentKitChildFragmentBindingImpl? = null

    val favouriteData = null

    var treatmentCreate : CreateTreatmentkitRequestModel = CreateTreatmentkitRequestModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content = arguments?.getString(AppConstants.ALERTDIALOG)
        val style = STYLE_NO_FRAME
        val theme = R.style.DialogTheme
        setStyle(style, theme)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.treatment_kit_child_fragment,
            container,
            false
        )







        return binding!!.root
    }
}