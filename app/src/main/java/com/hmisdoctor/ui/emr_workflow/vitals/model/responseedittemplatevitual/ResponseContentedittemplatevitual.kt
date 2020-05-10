package com.hmisdoctor.ui.emr_workflow.vitals.model.responseedittemplatevitual

import android.os.Parcel
import android.os.Parcelable

data class ResponseContentedittemplatevitual(
    var active_from: String? = "",
    var active_to: String? = "",
    var code: Any? = Any(),
    var comments: Any? = Any(),
    var created_by: Int? = 0,
    var created_date: String? = "",
    var department_uuid: Int? = 0,
    var description: String? = "",
    var diagnosis_uuid: Int? = 0,
    var display_order: Int? = 0,
    var facility_uuid: Int? = 0,
    var is_active: Boolean? = false,
    var is_public: Boolean? = false,
    var modified_by: Int? = 0,
    var modified_date: String? = "",
    var name: String? = "",
    var revision: Int? = 0,
    var status: Boolean? = false,
    var template_master_details: List<TemplateMasterDetail?>? = listOf(),
    var template_type_uuid: Int? = 0,
    var user_uuid: Int? = 0,
    var uuid: Int? = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        TODO("code"),
        TODO("comments"),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        TODO("template_master_details"),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ResponseContentedittemplatevitual> {
        override fun createFromParcel(parcel: Parcel): ResponseContentedittemplatevitual {
            return ResponseContentedittemplatevitual(parcel)
        }

        override fun newArray(size: Int): Array<ResponseContentedittemplatevitual?> {
            return arrayOfNulls(size)
        }
    }
}