package com.hmisdoctor.ui.quick_reg.model.labtest.request.orderRequest

import android.os.Parcel
import android.os.Parcelable

data class Header(
    var acknowledge_by: Any = Any(),
    var acknowledge_date: Any = Any(),
    var analyte_master: Any = Any(),
    var analyte_master_uuid: Any = Any(),
    var analyte_ref_master: Any = Any(),
    var analyte_ref_master_uuid: Any = Any(),
    var analyte_uom_uuid: Int = 0,
    var analyzer_analyte_ref_master_uuid: Any = Any(),
    var approved_by: Any = Any(),
    var approved_date: Any = Any(),
    var auth_status_uuid: Any = Any(),
    var cancel_reason: Any = Any(),
    var canceled_by: Any = Any(),
    var canceled_datetime: Any = Any(),
    var comments: Any = Any(),
    var confidential_uuid: Any = Any(),
    var created_by: Any = Any(),
    var created_date: String = "",
    var doctor_uuid: Int = 0,
    var encounter_uuid: Int = 0,
    var facility_uuid: Int = 0,
    var from_department_uuid: Int = 0,
    var from_facility_uuid: Int = 0,
    var from_to_location_uuid: Int = 0,
    var group_uuid: Int = 0,
    var is_acknowledged: Any = Any(),
    var is_active: Boolean = false,
    var is_approval_requried: Any = Any(),
    var is_confidential: Boolean = false,
    var is_other_facility: Any = Any(),
    var is_profile: Boolean = false,
    var is_visible_from_facility: Boolean = false,
    var is_visible_to_facility: Boolean = false,
    var lab_master_type_uuid: Int = 0,
    var lis_result_value: Any = Any(),
    var modified_by: Int = 0,
    var modified_date: String = "",
    var order_priority_uuid: Int = 0,
    var order_process_date: String = "",
    var patient_order_detail_uuid: Int = 0,
    var patient_order_test_detail_uuid: Int = 0,
    var patient_order_uuid: Int = 0,
    var patient_uuid: Int = 0,
    var patient_work_order_uuid: Int = 0,
    var profile_master: Any = Any(),
    var profile_master_uuid: Any = Any(),
    var qualifier_uuid: Int = 0,
    var qualifierid: Int = 0,
    var ref_value: Any = Any(),
    var released_by: Any = Any(),
    var released_date: Any = Any(),
    var result_value: String = "",
    var revision: Any = Any(),
    var sample_collection_by: Int = 0,
    var sample_collection_date: String = "",
    var sample_identifier: String = "",
    var status: Boolean = false,
    var tat_session_end: String = "",
    var tat_session_start: String = "",
    var tech_validation_by: Any = Any(),
    var tech_validation_date: Any = Any(),
    var test_diseases_uuid: Any = Any(),
    var test_master: TestMaster = TestMaster(),
    var test_master_uuid: Int = 0,
    var test_ref_master: Any = Any(),
    var test_ref_master_uuid: Any = Any(),
    var test_to_location_uuid: Int = 0,
    var to_department_uuid: Int = 0,
    var to_facility_uuid: Any = Any(),
    var to_sub_department_uuid: Int = 0,
    var uuid: Int = 0,
    var vendor_sample_uuid: Int = 0,
    var vendor_test_uuid: Int = 0,
    var work_order_attachment_uuid: Any = Any(),
    var work_order_status_uuid: Int = 0
):Parcelable {
    constructor(parcel: Parcel) : this(
        TODO("acknowledge_by"),
        TODO("acknowledge_date"),
        TODO("analyte_master"),
        TODO("analyte_master_uuid"),
        TODO("analyte_ref_master"),
        TODO("analyte_ref_master_uuid"),
        parcel.readInt(),
        TODO("analyzer_analyte_ref_master_uuid"),
        TODO("approved_by"),
        TODO("approved_date"),
        TODO("auth_status_uuid"),
        TODO("cancel_reason"),
        TODO("canceled_by"),
        TODO("canceled_datetime"),
        TODO("comments"),
        TODO("confidential_uuid"),
        TODO("created_by"),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        TODO("is_acknowledged"),
        parcel.readByte() != 0.toByte(),
        TODO("is_approval_requried"),
        parcel.readByte() != 0.toByte(),
        TODO("is_other_facility"),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        TODO("lis_result_value"),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        TODO("profile_master"),
        TODO("profile_master_uuid"),
        parcel.readInt(),
        parcel.readInt(),
        TODO("ref_value"),
        TODO("released_by"),
        TODO("released_date"),
        parcel.readString().toString(),
        TODO("revision"),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        TODO("tech_validation_by"),
        TODO("tech_validation_date"),
        TODO("test_diseases_uuid"),
        TODO("test_master"),
        parcel.readInt(),
        TODO("test_ref_master"),
        TODO("test_ref_master_uuid"),
        parcel.readInt(),
        parcel.readInt(),
        TODO("to_facility_uuid"),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        TODO("work_order_attachment_uuid"),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun describeContents(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object CREATOR : Parcelable.Creator<Header> {
        override fun createFromParcel(parcel: Parcel): Header {
            return Header(parcel)
        }

        override fun newArray(size: Int): Array<Header?> {
            return arrayOfNulls(size)
        }
    }
}