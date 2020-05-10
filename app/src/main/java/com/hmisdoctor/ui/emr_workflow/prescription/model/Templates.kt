package com.hmisdoctor.ui.emr_workflow.prescription.model

import android.os.Parcel
import android.os.Parcelable
import android.widget.ArrayAdapter


data class Templates(
        var viewstatus : Int = 0,
        var isFavposition:Int=0,
        var isTemposition:Int=0,
        var drug_details: ArrayList<DrugDetail> = ArrayList(),
        val temp_details: PrescriptionTempDetails = PrescriptionTempDetails()
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            TODO("drug_details"),
            TODO("temp_details")) {
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun describeContents(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object CREATOR : Parcelable.Creator<Templates> {
        override fun createFromParcel(parcel: Parcel): Templates {
            return Templates(parcel)
        }

        override fun newArray(size: Int): Array<Templates?> {
            return arrayOfNulls(size)
        }
    }
}