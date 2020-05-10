package com.hmisdoctor.config

import com.hmisdoctor.R
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesModel
import java.util.ArrayList

class AppConstants {

    companion object {

        //DEV
//        const val BASE_URL = "https://qahmisgateway.oasyshealth.co/DEV"




        //QA
       // Request URL: https://qahmisgateway.oasyshealth.co/QAHMIS-Login/1.0.0/api/authentication/loginNew
        const val BASE_URL = "https://qahmisgateway.oasyshealth.co/"

        const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100
        const val TIMEOUT_VALUE = 60000
        const val SUCCESS_RESPONSE_CODE = 200
        const val SHARE_PREFERENCE_NAME = "hims_preference"
        const val WSO2_TOKEN =
            "ZmpvTWJZSHJoUmZuek1RTHhCaUFhSElESHNVYTp6UFdKR2ZTZk53NG5ObG5aaUxwWjROU0xWYkVh"

        const val BEARER_AUTH = "Bearer "

        const val ALERTDIALOG = "dialog"
        const val ALERTDIALOGTITLE = "Alert"
        const val ACCEPT_LANGUAGE_EN = "en"
        const val RESPONSECONTENT ="ResponseContant"
        const val RESPONSECONTENTS ="ResponseContant"

        const val RESPONSENEXT ="ResponseNext"
        const val DIAGNISISRESPONSECONTENT ="DiadnosisResponseContant"
        const val PRESCRIPTIONRESPONSECONTENT ="PrescriptionResponseContant"
        const val RADIOLOGYRESPONSECONTENT = "RadiologyResponseContent"
        const val INVESTIGATIONRESPONSECONTENT = "InvestigationResponseContent"
        const val ACTIVITY_CODE_CHIEF_COMPLAINTS = "OPE008"
        const val ACTIVITY_CODE_LAB_RESULT = "OPE014"
        const val ACTIVITY_CODE_LAB= "OPE004"
        const val ACTIVITY_CODE_RADIOLOGY= "OPE005"
        const val ACTIVITY_CODE_INVESTIGATION= "OPE011"
        const val ACTIVITY_CODE_DIAGNOSIS= "OPE012"
        const val ACTIVITY_CODE_VITALS= "OPE010"
        const val ACTIVITY_CODE_PRESCRIPTION= "OPE006"
        const val ACTIVITY_CODE_HISTORY = "OPE009"
        const val ACTIVITY_CODE_FAMILY_HISTORY = "FAM"
        const val ACTIVITY_CODE_RADIOLOGY_RESULT = "OPE015"
        const val ACTIVITY_CODE_TREATMENT_KIT= "OPE007"
        const val ACTIVITY_CODE_OP_NOTES= "OPE003"
        const val ACTIVITY_CODE_DOCUMENT= "DOCUM"
        const val ACTIVITY_CODE_INVESTIGATION_RESULT= "OPE016"
        const val ACTIVITY_CODE_ADMISSION= "OPE013"
        const val ACTIVITY_CODE_CERTIFICATE= "null"



        const val ACTIVITY_CODE_BLOOD_REQUEST= "BLOOD"  //Sri Not confirmed

        const val PDFDATA = "PDFDATA"



        const val REQ_CODE_MANAGE_FRAGMENT = 3





        const val SELECTED_ARRAY_LIST = "selectedArrayList"
        const val FAV_TYPE_ID_DIAGNOSIS = 6
        const val FAV_TYPE_ID_CHIEF_COMPLAINTS = 5
        const val FAV_TYPE_ID_LAB = 2
        const val FAV_TYPE_ID_PRESCRIPTION = 1
        const val FAV_TYPE_ID_Vitual = 4
        const val FAV_TYPE_ID_INVESTIGATION = 7

        const val FAV_TYPE_ID_RADIOLOGY = 3
        const val TEM_TYPE_ID_VITALS = 4
       const val LAB_TESTMASTER_UUID = 1

        const val LAB_MASTER_TYPE_ID_RADIOLOGY = 2
        const val LAB_MASTER_TYPE_ID = "LabMasterUUID"

        const val FAV_TYPE_ID_CHIEF = 5
        const val SEARCHKEYMOBILE: String ="SEARCHKEYMOBILE"
        const val SEARCHKEYPIN: String ="SEARCHKEYPIN"
        const val SEARCHNAME: String ="SEARCHNAME"

        const val History = 5


        const val ENCRYPT_KEY = "aesEncryptionKey"
        const val IV ="oasysoasysoasys1"
        const val ALGORITHAM = "AES"
        const val AES_SELECTED_MODE = "AES/CBC/PKCS7Padding"

        const val OFFICE_UUID = "office_uid"
        const val OFFICE_NAME = "office_name"

        const val FACILITY_UUID = "facility_uid"
        const val DEPARTMENT_UUID = "department_uuid"
        const val PATIENT_UUID = "patientUuid"
        const val ENCOUNTER_TYPE = "encounterType"
        const val ENCOUNTER_DOCTOR_UUID = "encounter_doctor_uuid"
        const val ENCOUNTER_UUID = "encounter_uuid"
        const val USER_NAME = "user_name"
       const val KEYFAVOURITEID = "favouriteid"
        const val STOREMASTER_UUID = "store_master_uuid"
        const val INSTITUTION_NAME = "ins_name"
        const val LASTPIN = "lastpin"
        const val ROLEUUID  ="RoleID"
        const val FirstTime = "firsttime"

        const val EMRCHECK  ="EMRCHECK"
        const val REGISTRATIONCHECK  ="REGISTRATIONCHECK"
        const val LMISCHECK  ="LMISCHECK"

        const val ROLE_LMIS= "LIS"
        const val ROLE_Registration = "Registration"
        const val ROLE_EMR = "EMR"

        const val TYPE_OUT_PATIENT = 1

        const val IN_PATIENT = "InPatient"
        const val OUT_PATIENT = "OutPatient"
        const val CONFRIGURATION = "configuration"
        const val PATIENT_TYPE = "patientType"

    }
}