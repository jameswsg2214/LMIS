package com.hmisdoctor.ui.emr_workflow.lab.model.template.gettemplate

import android.os.Parcel
import android.os.Parcelable

data class ResponseContentLabGetDetails(
    var lab_details: List<LabDetail?>? = listOf(),
    var temp_details: TempDetails? = TempDetails()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        TODO("lab_details"),
        TODO("temp_details")
    ) {
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ResponseContentLabGetDetails> {
        override fun createFromParcel(parcel: Parcel): ResponseContentLabGetDetails {
            return ResponseContentLabGetDetails(parcel)
        }

        override fun newArray(size: Int): Array<ResponseContentLabGetDetails?> {
            return arrayOfNulls(size)
        }
    }
}