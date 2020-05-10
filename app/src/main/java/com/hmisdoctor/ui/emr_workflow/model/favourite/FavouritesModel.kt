package com.hmisdoctor.ui.emr_workflow.model.favourite

import android.os.Parcel
import android.os.Parcelable
import java.lang.reflect.Constructor


data class FavouritesModel(
    var chief_complaint_code: String? = "",
    var chief_complaint_id: Int? = 0,
    var chief_complaint_name: String? = "",
    var diagnosis_code: String? = "",
    var diagnosis_description: String? = "",
    var diagnosis_id: String? ="",
    var diagnosis_name: String? = "",
    var drug_active: Boolean? = null,
    var drug_duration: Int? = 0,
    var Presstrength :String?="",
    var drug_frequency_id: String? = "",
    var drug_frequency_name: String? = "",

    var drug_instruction_code: Any? = null,
    var drug_instruction_id: Int? = 0,
    var drug_instruction_name: String? = "",

    var drug_id: Int? = 0,
    var drug_name: String? = null,
    var drug_period_code: String? = "",
    var PrescriptiondurationPeriodId : Int = 1,
    var drug_period_id: Any? = null,
    var drug_period_name: String?="",
    var drug_route_id: Int? = 0,
    var drug_route_name: String? = "",
    var favourite_details_id: Int? = null,
    var favourite_display_order: Int? = null,
    var favourite_id: Int? = null,
    var favourite_name: Any? = null,
    var test_master_code: Any? = null,
    var test_master_description: Any? = null,
    var test_master_id: Int? = 0,
    var test_master_name: String? = "",
    var vital_master_name: Any? = null,
    var vital_master_uom: Any? = null,
    var isSelected: Boolean? = false,
    var itemAppendString: String? = "",
    var position: Int? = 0,
    var duration: String? = "1",
    var durationPeriodId: Int? = 0,
    var selectTypeUUID: Int =0,
    var selectRouteID : Int =0,
    var selecteFrequencyID : Int = 0,
    var selectInvestID : Int =0,
    var selectToLocationUUID: Int = 0,
    var viewLabstatus : Int = 0,
    var perdayquantityPrescription:String="1.00",
   var  viewRadiologystatus : Int = 0,
    var viewChiefcomplaintstatus : Int = 0,
    var viewPrescriptionstatus : Int = 0,
    var  viewDiagnosisstatus : Int = 0,
    var isFavposition:Int=0,
    var isTemposition:Int=0,
    var code:String="",
    var template_id:Int=1,
    var is_snomed:Int=0,
    var commands:String=""


) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString()

    ) {
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FavouritesModel> {
        override fun createFromParcel(parcel: Parcel): FavouritesModel {
            return FavouritesModel(parcel)
        }

        override fun newArray(size: Int): Array<FavouritesModel?> {
            return arrayOfNulls(size)
        }
    }
}