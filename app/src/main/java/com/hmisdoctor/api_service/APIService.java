package com.hmisdoctor.api_service;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.hmisdoctor.ui.configuration.model.ConfigResponseModel;
import com.hmisdoctor.ui.configuration.model.ConfigUpdateRequestModel;
import com.hmisdoctor.ui.configuration.model.ConfigUpdateResponseModel;
import com.hmisdoctor.ui.covid.addpatientrequest.AddPatientDetailsRequest;
import com.hmisdoctor.ui.covid.addpatientrequest.ConditionDetailsResponseModel;
import com.hmisdoctor.ui.covid.addpatientrequest.CovidRegisterPatientResponseModel;
import com.hmisdoctor.ui.covid.addpatientrequest.UpdatePatientDetailsRequest;
import com.hmisdoctor.ui.covid.addpatientrequest.specimenResponseModel;
import com.hmisdoctor.ui.covid.addpatientrequest.symptomResponseModel;
import com.hmisdoctor.ui.covid.addpatientresponse.AddPatientResponse;
import com.hmisdoctor.ui.dashboard.model.ChangePasswordOTPResponseModel;
import com.hmisdoctor.ui.dashboard.model.CovidGenderResponseModel;
import com.hmisdoctor.ui.dashboard.model.CovidMobileBelongsToResponseModel;
import com.hmisdoctor.ui.dashboard.model.CovidNationalityResponseModel;
import com.hmisdoctor.ui.dashboard.model.CovidPatientCategoryResponseModel;
import com.hmisdoctor.ui.dashboard.model.CovidPeriodResponseModel;
import com.hmisdoctor.ui.dashboard.model.CovidSalutationTitleResponseModel;
import com.hmisdoctor.ui.dashboard.model.DashBoardResponse;
import com.hmisdoctor.ui.dashboard.model.PasswordChangeResponseModel;
import com.hmisdoctor.ui.dashboard.model.CovidQuarantineTypeResponseModel;
import com.hmisdoctor.ui.dashboard.model.registration.CovidRegistrationSearchResponseModel;
import com.hmisdoctor.ui.dashboard.model.registration.DistrictListResponseModel;
import com.hmisdoctor.ui.dashboard.model.registration.RepeatedIntervalReponseModel;
import com.hmisdoctor.ui.dashboard.model.registration.RepeatedResultResponseModel;
import com.hmisdoctor.ui.dashboard.model.registration.StateListResponseModel;
import com.hmisdoctor.ui.dashboard.model.registration.TalukListResponseModel;
import com.hmisdoctor.ui.dashboard.model.registration.VilliageListResponceModel;
import com.hmisdoctor.ui.dashboard.model.ResponseSpicemanType;

import com.hmisdoctor.ui.emr_workflow.admission_referal.model.AdmissionDepartmentResponsemodel;
import com.hmisdoctor.ui.emr_workflow.admission_referal.model.AdmissionWardResponseModel;
import com.hmisdoctor.ui.emr_workflow.admission_referal.model.ReasonResponseModel;
import com.hmisdoctor.ui.emr_workflow.admission_referal.model.RefferaNextResponseModel;
import com.hmisdoctor.ui.emr_workflow.admission_referal.model.RefferalNextRequestModel;
import com.hmisdoctor.ui.emr_workflow.certificate.model.CertificateRequestModel;
import com.hmisdoctor.ui.emr_workflow.certificate.model.CertificateResponseModel;
import com.hmisdoctor.ui.emr_workflow.certificate.model.GetCertificateResponseModel;
import com.hmisdoctor.ui.emr_workflow.certificate.model.TemplateItemResponseModel;
import com.hmisdoctor.ui.emr_workflow.certificate.model.TemplateResponseModel;
import com.hmisdoctor.ui.emr_workflow.blood_request.model.GetAllBloodGroupReq;
import com.hmisdoctor.ui.emr_workflow.blood_request.model.GetAllBloodGroupResp;
import com.hmisdoctor.ui.emr_workflow.blood_request.model.GetAllPurposeReq;
import com.hmisdoctor.ui.emr_workflow.blood_request.model.GetAllPurposeResp;
import com.hmisdoctor.ui.emr_workflow.blood_request.model.GetPreviousBloodReq;
import com.hmisdoctor.ui.emr_workflow.blood_request.model.GetPreviousBloodResp;
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.PrevChiefComplainResponseModel;
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.request.ChiefComplaintRequestModel;
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.response.ChiefComplaintResponse;
import com.hmisdoctor.ui.emr_workflow.chief_complaint.ui.chiefcomplaintadddialog.model.ChiefComplaintFavAddresponseModel;
import com.hmisdoctor.ui.emr_workflow.chief_complaint.ui.chiefcomplaintadddialog.model.request.ChiefCompliantAddRequestModel;
import com.hmisdoctor.ui.emr_workflow.delete.model.DeleteResponseModel;
import com.hmisdoctor.ui.emr_workflow.diagnosis.model.DiagnosisRequest;
import com.hmisdoctor.ui.emr_workflow.diagnosis.model.DiagnosisResponseModel;
import com.hmisdoctor.ui.emr_workflow.diagnosis.model.PreDiagnosisResponseModel;
import com.hmisdoctor.ui.emr_workflow.diagnosis.model.diagonosissearch.DiagonosisSearchResponse;
import com.hmisdoctor.ui.emr_workflow.diagnosis.view.snomed_dialog.response.SnomedChildDataResponseModel;
import com.hmisdoctor.ui.emr_workflow.diagnosis.view.snomed_dialog.response.SnomedDataResponseModel;
import com.hmisdoctor.ui.emr_workflow.diagnosis.view.snomed_dialog.response.SnomedParentDataResponseModel;
import com.hmisdoctor.ui.emr_workflow.documents.model.AddDocumentDetailsResponseModel;
import com.hmisdoctor.ui.emr_workflow.documents.model.DeleteDocumentResponseModel;
import com.hmisdoctor.ui.emr_workflow.documents.model.DocumentTypeResponseModel;
import com.hmisdoctor.ui.emr_workflow.documents.model.FileUploadResponseModel;
import com.hmisdoctor.ui.emr_workflow.history.allergy.model.response.AllergyCreateResponseModel;
import com.hmisdoctor.ui.emr_workflow.history.allergy.model.response.AllergyNameResponse;
import com.hmisdoctor.ui.emr_workflow.history.allergy.model.response.AllergySeverityResponse;
import com.hmisdoctor.ui.emr_workflow.history.allergy.model.response.AllergySourceResponse;
import com.hmisdoctor.ui.emr_workflow.history.allergy.model.response.AllergyUpdateResponseModel;
import com.hmisdoctor.ui.emr_workflow.history.allergy.model.response.EncounterAllergyTypeResponse;
import com.hmisdoctor.ui.emr_workflow.history.allergy.model.response.AllergyAllGetResponseModel;
import com.hmisdoctor.ui.emr_workflow.history.diagnosis.model.DiagnosisSearchResponseModel;
import com.hmisdoctor.ui.emr_workflow.history.diagnosis.model.HistoryDiagnosisCreateResponseModel;
import com.hmisdoctor.ui.emr_workflow.history.diagnosis.model.HistoryDiagnosisResponseModel;
import com.hmisdoctor.ui.emr_workflow.history.diagnosis.model.HistoryDiagnosisUpdateResponseModel;
import com.hmisdoctor.ui.emr_workflow.history.familyhistory.model.CreateFamilyHistoryResponseModel;
import com.hmisdoctor.ui.emr_workflow.history.familyhistory.model.FamilyHistoryResponseModel;
import com.hmisdoctor.ui.emr_workflow.history.familyhistory.model.FamilyTypeSpinnerResponseModel;
import com.hmisdoctor.ui.emr_workflow.history.familyhistory.model.FamilyUpdateResponseModel;
import com.hmisdoctor.ui.emr_workflow.history.immunization.model.CreateImmunizationResponseModel;
import com.hmisdoctor.ui.emr_workflow.history.immunization.model.GetImmunizationResponseModel;
import com.hmisdoctor.ui.emr_workflow.history.immunization.model.ImmunizationInstitutionResponseModel;
import com.hmisdoctor.ui.emr_workflow.history.immunization.model.ImmunizationNameResponseModel;
import com.hmisdoctor.ui.emr_workflow.history.model.response.HistoryResponce;
import com.hmisdoctor.ui.emr_workflow.history.prescription.model.PrescriptionHistoryResponseModel;
import com.hmisdoctor.ui.emr_workflow.history.surgery.model.response.CreateSurgeryResponseModel;
import com.hmisdoctor.ui.emr_workflow.history.surgery.model.response.HistorySurgeryResponseModel;
import com.hmisdoctor.ui.emr_workflow.history.surgery.model.response.SurgeryInstitutionResponseModel;
import com.hmisdoctor.ui.emr_workflow.history.surgery.model.response.SurgeryNameResponseModel;
import com.hmisdoctor.ui.emr_workflow.history.surgery.model.response.SurgeryUpdateResponseModel;
import com.hmisdoctor.ui.emr_workflow.history.vitals.model.response.HistoryVitalsResponseModel;
import com.hmisdoctor.ui.emr_workflow.investigation.model.InvestigationPrevResponseModel;
import com.hmisdoctor.ui.emr_workflow.investigation.model.InvestigationSearchResponseModel;
import com.hmisdoctor.ui.emr_workflow.investigation_result.model.InvestigationResultResponseModel;
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddAllDepatResponseModel;
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddResponseModel;
import com.hmisdoctor.ui.emr_workflow.lab.model.FavAddTestNameResponse;
import com.hmisdoctor.ui.emr_workflow.lab.model.LabFavManageResponseModel;

import com.hmisdoctor.ui.emr_workflow.lab.model.LabToLocationResponse;
import com.hmisdoctor.ui.emr_workflow.lab.model.faveditresponse.FavEditResponse;
import com.hmisdoctor.ui.emr_workflow.lab.model.favresponse.FavAddListResponse;
import com.hmisdoctor.ui.emr_workflow.lab.model.favresponse.FavSearchResponce;
import com.hmisdoctor.ui.emr_workflow.lab.model.request.RequestLabFavModel;
import com.hmisdoctor.ui.emr_workflow.lab.model.template.gettemplate.ResponseLabGetTemplateDetails;
import com.hmisdoctor.ui.emr_workflow.lab.model.template.request.RequestTemplateAddDetails;
import com.hmisdoctor.ui.emr_workflow.lab.model.template.response.ReponseTemplateadd;
import com.hmisdoctor.ui.emr_workflow.lab.model.updaterequest.UpdateRequestModule;
import com.hmisdoctor.ui.emr_workflow.lab.model.updateresponse.UpdateResponse;
import com.hmisdoctor.ui.emr_workflow.lab_result.model.LabResultGetByDataResponseModel;
import com.hmisdoctor.ui.emr_workflow.lab_result.model.LabResultResponseModel;
import com.hmisdoctor.ui.emr_workflow.model.EMR_Request.EmrRequestModel;
import com.hmisdoctor.ui.emr_workflow.model.EMR_Response.EmrResponceModel;
import com.hmisdoctor.ui.emr_workflow.model.EmrWorkFlowResponseModel;
import com.hmisdoctor.ui.emr_workflow.model.GetStoreMasterResponseModel;
import com.hmisdoctor.ui.emr_workflow.model.favourite.FavouritesResponseModel;
import com.hmisdoctor.ui.emr_workflow.model.create_encounter_request.CreateEncounterRequestModel;
import com.hmisdoctor.ui.emr_workflow.model.create_encounter_response.CreateEncounterResponseModel;
import com.hmisdoctor.ui.emr_workflow.model.fetch_encounters_response.FectchEncounterResponseModel;
import com.hmisdoctor.ui.emr_workflow.model.templete.TempleResponseModel;
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.duration.DurationResponseModel;
import com.hmisdoctor.ui.emr_workflow.chief_complaint.model.search_complaint.ComplaintSearchResponseModel;
import com.hmisdoctor.ui.emr_workflow.lab.model.LabTypeResponseModel;
import com.hmisdoctor.ui.emr_workflow.op_notes.model.OpNotesGetAllResponseModel;
import com.hmisdoctor.ui.emr_workflow.op_notes.model.OpNotesSpinnerResponseModel;
import com.hmisdoctor.ui.emr_workflow.op_notes.model.OpNotesExpandableResponseModel;
import com.hmisdoctor.ui.emr_workflow.op_notes.model.OpNotesResponsModel;
import com.hmisdoctor.ui.emr_workflow.prescription.model.PresDrugFrequencyResponseModel;
import com.hmisdoctor.ui.emr_workflow.prescription.model.PresInstructionResponseModel;

import com.hmisdoctor.ui.emr_workflow.prescription.model.PrescriptionDurationResponseModel;
import com.hmisdoctor.ui.emr_workflow.prescription.model.PrescriptionFrequencyResponseModel;
import com.hmisdoctor.ui.emr_workflow.prescription.model.PrescriptionFavResponseModel;
import com.hmisdoctor.ui.emr_workflow.prescription.model.PrescriptionInfoResponsModel;
import com.hmisdoctor.ui.emr_workflow.prescription.model.PrescriptionPostAllDataResponseModel;
import com.hmisdoctor.ui.emr_workflow.prescription.model.PrescriptionRoutResponseModel;
import com.hmisdoctor.ui.emr_workflow.prescription.model.PrescriptionSearchResponseModel;
import com.hmisdoctor.ui.emr_workflow.prescription.model.PrescriptionTemplateResponseModel;
import com.hmisdoctor.ui.emr_workflow.prescription.model.PrevPrescriptionModel;
import com.hmisdoctor.ui.emr_workflow.prescription.model.ResponsePrescriptionFav;
import com.hmisdoctor.ui.emr_workflow.prescription.model.ZeroStockResponseModel;
import com.hmisdoctor.ui.emr_workflow.prescription.model.emr_prescription_postalldata_requestmodel;
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.favouriteEdit.requestparamter.RequestPrecEditModule;
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.favouriteEdit.updaterequest.UpdatePrescriptionRequest;
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.favouriteEdit.updateresponse.ResponsePreFavEdit;
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.savetemplate.request.SaveTemplateRequestModel;
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.savetemplate.request.SearchPrescriptionResponseModel;
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.savetemplate.response.SaveTemplateResponseModel;
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.templeteEdit.request.UpdateRequestModel;
import com.hmisdoctor.ui.emr_workflow.prescription.ui.dialogFragment.templeteEdit.response.UpdateResponseModel;
import com.hmisdoctor.ui.emr_workflow.radiology_result.model.RadiologyResultResponseModel;
import com.hmisdoctor.ui.emr_workflow.treatment_kit.model.TreatmentKitCreateResponseModel;
import com.hmisdoctor.ui.emr_workflow.treatment_kit.model.TreatmentKitPrevResponsModel;
import com.hmisdoctor.ui.emr_workflow.treatment_kit.model.TreatmentkitSearchResponseModel;
import com.hmisdoctor.ui.emr_workflow.treatment_kit.model.request.CreateTreatmentkitRequestModel;
import com.hmisdoctor.ui.emr_workflow.view.lab.model.PrevLabResponseModel;

import com.hmisdoctor.ui.emr_workflow.vitals.model.UomListResponceModel;
import com.hmisdoctor.ui.emr_workflow.vitals.model.VitalSaveRequestModel;
import com.hmisdoctor.ui.emr_workflow.vitals.model.VitalsListResponseModel;

import com.hmisdoctor.ui.emr_workflow.vitals.model.VitalsSearchResponseModel;
import com.hmisdoctor.ui.emr_workflow.vitals.model.VitalsTemplateResponseModel;
import com.hmisdoctor.ui.emr_workflow.vitals.model.response.VitalSaveResponseModel;
import com.hmisdoctor.ui.emr_workflow.vitals.model.response.VitalSearchListResponseModel;
import com.hmisdoctor.ui.emr_workflow.vitals.model.responseedittemplatevitual.ResponseEditTemplate;
import com.hmisdoctor.ui.institute.model.DepartmentResponseModel;
import com.hmisdoctor.ui.institute.model.InstitutionResponseModel;
import com.hmisdoctor.ui.institute.model.OfficeResponseModel;
import com.hmisdoctor.ui.login.model.login_response_model.LoginResponseModel;
import com.hmisdoctor.ui.myprofile.model.MyProfileResponseModel;
import com.hmisdoctor.ui.out_patient.model.search_request_model.SearchPatientRequestModel;
import com.hmisdoctor.ui.out_patient.search_response_model.SearchResponseModel;
import com.hmisdoctor.ui.quick_reg.model.BlockZoneResponseModel;
import com.hmisdoctor.ui.quick_reg.model.FacilityLocationResponseModel;
import com.hmisdoctor.ui.quick_reg.model.GetApplicationRulesResponseModel;
import com.hmisdoctor.ui.quick_reg.model.GetLabNameListResponseModel;
import com.hmisdoctor.ui.quick_reg.model.GetReferenceResponseModel;
import com.hmisdoctor.ui.quick_reg.model.GettestResponseModel;
import com.hmisdoctor.ui.quick_reg.model.LocationMasterResponseModel;
import com.hmisdoctor.ui.quick_reg.model.LocationMasterResponseModelX;
import com.hmisdoctor.ui.quick_reg.model.PDFRequestModel;
import com.hmisdoctor.ui.quick_reg.model.QuickRegistrationSaveResponseModel;
import com.hmisdoctor.ui.quick_reg.model.SampleAcceptanceResponseModel;
import com.hmisdoctor.ui.quick_reg.model.SaveOrderResponseModel;
import com.hmisdoctor.ui.quick_reg.model.labapprovalresult.ApprovalRequestModel;
import com.hmisdoctor.ui.quick_reg.model.labapprovalresult.LabApprovalResultResponse;
import com.hmisdoctor.ui.quick_reg.model.labapprovalresult.LabApprovalSpinnerResponseModel;
import com.hmisdoctor.ui.quick_reg.model.labapprovalresult.response.OrderApprovedResponseModel;
import com.hmisdoctor.ui.quick_reg.model.labtest.request.AssigntootherRequest;
import com.hmisdoctor.ui.quick_reg.model.labtest.request.LabTestApprovalRequestModel;
import com.hmisdoctor.ui.quick_reg.model.labtest.request.LabTestRequestModel;
import com.hmisdoctor.ui.quick_reg.model.labtest.request.LabrapidSaveRequestModel;
import com.hmisdoctor.ui.quick_reg.model.labtest.request.RejectRequestModel;
import com.hmisdoctor.ui.quick_reg.model.labtest.request.SendApprovalRequestModel;
import com.hmisdoctor.ui.quick_reg.model.labtest.request.TestProcessRequestModel;
import com.hmisdoctor.ui.quick_reg.model.labtest.request.orderRequest.OrderProcessDetailsResponseModel;
import com.hmisdoctor.ui.quick_reg.model.labtest.request.orderRequest.OrderToProcessReqestModel;
import com.hmisdoctor.ui.quick_reg.model.labtest.request.orderRequest.Req;
import com.hmisdoctor.ui.quick_reg.model.labtest.response.LabAssignedToResponseModel;
import com.hmisdoctor.ui.quick_reg.model.labtest.response.LabTestResponseModel;
import com.hmisdoctor.ui.quick_reg.model.labtest.response.LabTestSpinnerResponseModel;
import com.hmisdoctor.ui.quick_reg.model.labtest.response.OrderProcessResponseModel;
import com.hmisdoctor.ui.quick_reg.model.labtest.response.RejectReferenceResponseModel;
import com.hmisdoctor.ui.quick_reg.model.labtest.response.SimpleResponseModel;
import com.hmisdoctor.ui.quick_reg.model.labtest.response.UserProfileResponseModel;
import com.hmisdoctor.ui.quick_reg.model.labtest.response.testprocess.TestProcessResponseModel;
import com.hmisdoctor.ui.quick_reg.model.request.LabNameSearchResponseModel;
import com.hmisdoctor.ui.quick_reg.model.QuickRegistrationUpdateResponseModel;
import com.hmisdoctor.ui.quick_reg.model.QuickSearchResponseModel;
import com.hmisdoctor.ui.quick_reg.model.SearchPatientRequestModelCovid;
import com.hmisdoctor.ui.quick_reg.model.request.QuickRegistrationRequestModel;
import com.hmisdoctor.ui.quick_reg.model.ResponsePrivillageModule;
import com.hmisdoctor.ui.quick_reg.model.ResponseTestMethod;
import com.hmisdoctor.ui.quick_reg.model.request.QuickRegistrationUpdateRequestModel;
import com.hmisdoctor.ui.quick_reg.model.request.SaveOrderRequestModel;
import com.hmisdoctor.ui.quick_reg.model.testprocess.sampleTransportRequestModel;
//import com.readystatesoftware.chuck.ChuckInterceptor;

import com.hmisdoctor.ui.quick_reg.model.labtest.response.LabTestApprovalResponseModel;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

import static com.hmisdoctor.config.AppConstants.BASE_URL;
import static com.hmisdoctor.config.AppConstants.TIMEOUT_VALUE;

public interface APIService {

    class Factory {
        public static APIService create(Context context) {
            OkHttpClient.Builder b = new OkHttpClient.Builder();
            b.readTimeout(TIMEOUT_VALUE, TimeUnit.MILLISECONDS);
            b.writeTimeout(TIMEOUT_VALUE, TimeUnit.MILLISECONDS);
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
       //     OkHttpClient client = b.addInterceptor(new ChuckInterceptor(context)).build();

           /* HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();*/
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
          //          .client(client)
                    .build();
            return retrofit.create(APIService.class);
        }
    }
  /*
     QA
   */

    String BaseDomain = "QA";
    // String BaseDomain = "DEV";


    //old login
    //  String Login = BaseDomain+"HMIS-Login/1.0.0/api/authentication/login";
    String Login = BaseDomain + "HMIS-Login/1.0.0/api/authentication/loginNew";
    String Register = BaseDomain + "registration/v1/api/patient/search";
    String EMRWORKFLOW = BaseDomain + "HMIS-EMR/v1/api/emr-workflow-settings/getEMRWorkflowByUserId";
    String DashBoardDetail = BaseDomain + "HMIS-EMR/v1/api/dashboard/getDashBoarddata";
    String GetConfigList = BaseDomain + "Appmaster/v1/api/contextActivityMap/getMappingByContextId";
    String GetconfigUpdate = BaseDomain + "HMIS-EMR/v1/api/emr-workflow-settings/update";
    String GetHistoryconfigUpdate = BaseDomain + "HMIS-EMR/v1/api/emr-history-settings/update";
    String GetFavorites = BaseDomain + "HMIS-EMR/v1/api/favourite/getFavourite";
    String GetTemplete = BaseDomain + "HMIS-EMR/v1/api/template/gettemplateByID";
    String GetPrescriptionTemplete = BaseDomain + "HMIS-EMR/v1/api/template/gettemplateByID";
    String GetOfficeList = BaseDomain + "Appmaster/v1/api/facility/getUserOfficeByUserId";
    String GetDuration = BaseDomain + "HMIS-EMR/v1/api/chief-complaints-duration/get";
    String GetChiefComplaintsSearchResult = BaseDomain + "HMIS-EMR/v1/api/chief-complaints-master/getByFilters";
    String GetLabSearchResult = BaseDomain + "HMIS-LIS/v1/api/testmaster/gettestandprofileinfo";
    String GetRadioSearchResult = BaseDomain + "HMIS-RMIS/v1/api/testmaster/gettestandprofileinfo";
    String GetInstitutionList = BaseDomain + "Appmaster/v1/api/facility/getFacilityByHealthOfficeId";
    String GetDepartmentList = BaseDomain + "Appmaster/v1/api/manageInstitution/getManageInstitutionByUFId";
    String GetEncounters = BaseDomain + "HMIS-EMR/v1/api/encounter/getEncounterByDocAndPatientId";
    String CreateEncounter = BaseDomain + "HMIS-EMR/v1/api/encounter/create";
    String GetLabType = BaseDomain + "HMIS-LIS/v1/api/commonReference/getReference";
    String GetToLocation = BaseDomain + "HMIS-LIS/v1/api/tolocationmaster/gettolocationmaster";
    String GetPrevLab = BaseDomain + "HMIS-LIS/v1/api/patientorders/getLatestRecords";
    String GetFavDepartmentList = BaseDomain + "Appmaster/v1/api/department/getDepartmentOnlyById";
    String GetFavaddDepartmentList = BaseDomain + "Appmaster/v1/api/department/getAllDepartment";
    String EmrPost = BaseDomain + "HMIS-LIS/v1/api/patientorders/postpatientorder";
    String GetVitalsTemplatet = BaseDomain + "HMIS-EMR/v1/api/template/gettemplateByID";
    String InsertChiefComplaint = BaseDomain + "HMIS-EMR/v1/api/patient-chief-complaints/create";
    String GetHistoryAll = BaseDomain + "HMIS-EMR/v1/api/emr-history-settings/getById";
    String GetEncounterAllergyType = BaseDomain + "HMIS-EMR/v1/api/encounter-type/getEncounterType";
    String GetPrevRadiology = BaseDomain + "HMIS-RMIS/v1/api/patientorders/getLatestRecords";
    String GetAllergyName = BaseDomain + "HMIS-EMR/v1/api/allergyMaster/getAllergyMaster";
    String GetAllergySource = BaseDomain + "HMIS-EMR/v1/api/commonReference/getReference";
    String GetAllergySeverity = BaseDomain + "HMIS-EMR/v1/api/commonReference/getReference";
    String GetPrevPrescription = BaseDomain + "HMIS-INVENTORY/v1/api/prescriptions/getPreviousPrescription";
    String GetFrequency = BaseDomain + "HMIS-INVENTORY/v1/api/commonReference/getReference";
    String GetPrescriptionDuration = BaseDomain + "HMIS-INVENTORY/v1/api/commonReference/getReference";
    String InsertDiagnosis = BaseDomain + "HMIS-EMR/v1/api/patient-diagnosis/create";
    String SearchDiagnosis = BaseDomain + "HMIS-EMR/v1/api/diagnosis/getDFilter";
    String GetToLocationRadiology = BaseDomain + "HMIS-RMIS/v1/api/tolocationmaster/gettolocationmaster";
    String EmrRadiologypost = BaseDomain + "HMIS-RMIS/v1/api/patientorders/postpatientorder";
    String GetPrescriptionsSearchResult = BaseDomain + "HMIS-INVENTORY/v1/api/itemMaster/drugNameSearch";
    String GatUomVitalList = BaseDomain + "HMIS-INVENTORY/v1/api/uomMaster/getUomMaster";
    String GetInstruction = BaseDomain + "HMIS-INVENTORY/v1/api/commonReference/getReference";
    String EmrPrescriptionPost = BaseDomain + "HMIS-INVENTORY/v1/api/prescriptions/addAllPrescriptionDetails";
    String GetRouteDetails = BaseDomain + "HMIS-INVENTORY/v1/api/commonReference/getReference";
    String GetFavAddDiagonosis = BaseDomain + "HMIS-EMR/v1/api/favourite/create?searchkey=diagnosis";
    String GetAllergyAll = BaseDomain + "HMIS-EMR/v1/api/patient-allergy/getPatientAllergies";
    String DeleteRows = BaseDomain + "HMIS-EMR/v1/api/favourite/delete";
    String DeleteTemplate = BaseDomain + "HMIS-EMR/v1/api/template/deleteTemplateDetails";
    String GetFavddAll = BaseDomain + "HMIS-EMR/v1/api/favourite/create?searchkey=lab";
    String GetRadiologyFavourite = BaseDomain + "HMIS-EMR/v1/api/favourite/create?searchkey=radiology";
    String GetFavddAllList = BaseDomain + "HMIS-EMR/v1/api/favourite/getFavouriteById";
    String GetMyProfile = BaseDomain + "Appmaster/v1/api/userProfile/getUserProfileById";
    String GetOtpForPasswordChange = BaseDomain + "HMIS-Login/1.0.0/api/authentication/sendOtp";
    String GetPasswordChanged = BaseDomain + "HMIS-Login/1.0.0/api/authentication/changePassword";
    String GetHistoryPrescription = BaseDomain + "HMIS-INVENTORY/v1/api/prescriptions/getLatestPrescription";
    String GetFamilyAllType = BaseDomain + "HMIS-EMR/v1/api/family-history/getFamilyHistory";
    String CreateAllergy = BaseDomain + "HMIS-EMR/v1/api/patient-allergy/create";
    String getHistoryVitals = BaseDomain + "HMIS-EMR/v1/api/emr-patient-vitals/getHistoryPatientVitals";
    String AddFavChiefComplaint = BaseDomain + "HMIS-EMR/v1/api/favourite/create";
    String getVitalName = BaseDomain + "HMIS-EMR/v1/api/vitalMaster/getVitals";
    String getZeroStock = BaseDomain + "HMIS-INVENTORY/v1/api/stockItems/getZeroStockItems";
    String GetFamilyType = BaseDomain + "HMIS-EMR/v1/api/commonReference/getReference";
    String CreateFamilyHistory = BaseDomain + "HMIS-EMR/v1/api/family-history/create";
    String GetSurgeryInstitutions = BaseDomain + "Appmaster/v1/api/userFacility/getUserFacilityByUId";
    String GetSurgeryDetails = BaseDomain + "HMIS-EMR/v1/api/surgery-history/getSurgeryHistory";
    String GetSurgeryName = BaseDomain + "HMIS-EMR/v1/api/commonReference/getReference";
    String CreateSugery = BaseDomain + "HMIS-EMR/v1/api/surgery-history/create";
    String GetImmunizationName = BaseDomain + "HMIS-EMR/v1/api/immunizations/getimmunization";
    String GetImmunizationList = BaseDomain + "HMIS-EMR/v1/api/immunization/getAll";
    String CreateImmunization = BaseDomain + "HMIS-EMR/v1/api/immunization/create";
    String GetImmunizationInstitution = BaseDomain + "Appmaster/v1/api/facility/faciltiySearchDropdown";
    String PrescriptionInfo = BaseDomain + "HMIS-INVENTORY/v1/api/itemMaster/getItemMasterById";
    String getDiagnosisHistory = BaseDomain + "HMIS-EMR/v1/api/patient-diagnosis/getByFilters";
    String FavouriteUpdate = BaseDomain + "HMIS-EMR/v1/api/favourite/updateFavouriteById";
    String LabTemplateCreate = BaseDomain + "HMIS-EMR/v1/api/template/create";
    String LabGetTemplate = BaseDomain + "HMIS-EMR/v1/api/template/gettempdetails";
    String GetVitalSearchName = BaseDomain + "HMIS-EMR/v1/api/vitalMaster/getAllVitals";
    String VitualGetTemplate = BaseDomain + "HMIS-EMR/v1/api/template/gettempdetails";
    String PrescriptionFavAdd = BaseDomain + "HMIS-EMR/v1/api/favourite/create?searchkey=drug";
    String LabUpdateTemplate = BaseDomain + "HMIS-EMR/v1/api/template/updatetemplateById";
    String DiagonosisSearcbValue = BaseDomain + "HMIS-EMR/v1/api/diagnosis/getDFilter?";
    String PrescriptionSearch = BaseDomain + "HMIS-INVENTORY/v1/api/itemMaster/drugNameSearch";
    String UpdateAllergy = BaseDomain + "HMIS-EMR/v1/api/patient-allergy/updatePatientAllergy";
    String UpdateFamilyHistory = BaseDomain + "HMIS-EMR/v1/api/family-history/updateFamilyHistory";
    String UpdateSurgery = BaseDomain + "HMIS-EMR/v1/api/surgery-history/updateSurgery";
    String VitalSave = BaseDomain + "HMIS-EMR/v1/api/emr-patient-vitals/create";
    String VitalSearch = BaseDomain + "HMIS-EMR/v1/api/vitalMaster/getAllVitals";
    String labResult = BaseDomain + "HMIS-LIS/v1/api/patientorders/getlatestlabresultsemr";
    String radiologyResult = BaseDomain + "HMIS-LIS/v1/api/patientworkorderdetails/getAuthorizedRadiologyResults";
    String GetStoreMaster = BaseDomain + "HMIS-INVENTORY/v1/api/storeMaster/getStoreDepartmentById";
    String GetLabResultByDate = BaseDomain + "HMIS-LIS/v1/api/patientorders/getlabresultsdatecompareemr";
    String GetPrevTreatmentKit = BaseDomain + "HMIS-EMR/v1/api/patient-treatment/prevKitOrdersById";
    String GetSpecimen_Type = BaseDomain + "registration/v1/api/masters/getActiveLists";
    String GetRoleBased = BaseDomain +"Appmaster/v1/api/roleControlActivity/getRoleControlActivityByRoleandActivityCode";

    String GetSnommed = BaseDomain + "HMIS-EMR/v1/api/snomed/getsnomeddetails";

    String GetParentSnommed = BaseDomain + "HMIS-EMR/v1/api/snomed/getsnomedparent";

    String GetChildSnommed = BaseDomain + "HMIS-EMR/v1/api/snomed/getsnomedchildren";


    String sendApprovel = BaseDomain + "HMIS-LIS/v1/api/patientworkorder/sendApprovalTestWise";

    String CreateHistoryDiagnosis = BaseDomain + "HMIS-EMR/v1/api/patient-diagnosis/create";

    String getOpNotes = BaseDomain + "HMIS-EMR/v1/api/profiles/getAll";

    String orderProcessApprovel = BaseDomain + "HMIS-LIS/v1/api/patientworkorder/orderProcessApproval";

    String getRejectReference = BaseDomain + "HMIS-LIS/v1/api/commonReference/getReference";
    String getOPNotesAll = BaseDomain + "HMIS-EMR/v1/api/profiles/getById";

    String CreateTreatmentKit = BaseDomain + "HMIS-EMR/v1/api/treatment-kit/create";

    String getTreatmentkitInvestigationSearch = BaseDomain + "HMIS-INV/v1/api/testmaster/gettestandprofileinfo";

    //Covid Registration Section A1 APIs
    String GetSalutationTitiles = BaseDomain + "Appmaster/v1/api/title/getTitle";
    String GetGender = BaseDomain + "Appmaster/v1/api/gender/getGender";
    String GetPeriod = BaseDomain + "registration/v1/api/period/getPeriod";
    String GetNationalityAndMobileAndPatientCateType = BaseDomain + "registration/v1/api/masters/getActiveLists";
    String getState = BaseDomain + "Appmaster/v1/api/state/getByCountryId";

    String getDistict = BaseDomain + "Appmaster/v1/api/districtMaster/getByStateId";

    String getTaluk = BaseDomain + "Appmaster/v1/api/taluk/getByDistrictId";
    String getVillage = BaseDomain + "Appmaster/v1/api/healthvillage/getbytalukid";
    String addPatientDetails = BaseDomain + "registration/v1/api/covidRegistration/addPatientDetails";
    String getCovidPatientSearch = BaseDomain + "registration/v1/api/patient/search";


    String GetQuarantineType = BaseDomain + "registration/v1/api/masters/getActiveLists?table_name=quarentine_status_type";

    String CovidUpdate = BaseDomain + "registration/v1/api/covidRegistration/updatePatientDetails";

    String PrevChiefComplaint = BaseDomain + "HMIS-EMR/v1/api/patient-chief-complaints/get-prev-pat-cc-by-patientId";
    String GetINvestigationsSearchResult = BaseDomain + "HMIS-INV/v1/api/testmaster/gettestandprofileinfo";

    String GetPrevInvestigation = BaseDomain + "HMIS-INV/v1/api/patientorders/getLatestRecords";

    String GetOpExpandableList = BaseDomain + "HMIS-EMR/v1/api/profiles/getById";

    String PrevDiagnosis = BaseDomain + "HMIS-EMR/v1/api/patient-diagnosis/getPreviousDiagnosisByPatiendId";

    String GetRepeatedResult = BaseDomain + "registration/v1/api/masters/getActiveLists?table_name=test_result";
    String GetIntervals = BaseDomain + "registration/v1/api/masters/getActiveLists?table_name=repeat_test_type";

    String GetPrescFrequency = BaseDomain + "HMIS-INVENTORY/v1/api/drugFrequency/getDrugFrequency";

    String GetDetailsbyTablename = BaseDomain + "registration/v1/api/patient/getDetailsByTableName";

    String orderSendtonext=BaseDomain+"HMIS-LIS/v1/api/patientorderdetails/assigntootherinstitute";


    String CovidQuickRegistrationSave = BaseDomain +"registration/v1/api/quickRegistration/addPatientDetails";

    String facilityLocation = BaseDomain +"Appmaster/v1/api/facilityLocation/getFacilityLocationByfacilityId";

    String orderDetailsGet = BaseDomain +"HMIS-LIS/v1/api/patientorderdetails/getOrderProcessDetails";
    String orderDetailsGetLabApproval = BaseDomain +"HMIS-LIS/v1/api/patientorderdetails/getOrderProcessDetails";



    String getBlockZone = BaseDomain + "Appmaster/v1/api/healthblock/getBlockOnlyById";

    String getTestMaster=BaseDomain+"HMIS-LIS/v1/api/testmaster/gettestmaster";

    String getSavedPDF = BaseDomain+"registration/v1/api/patient/printPatient";

    String saveOrder = BaseDomain+"HMIS-LIS/v1/api/patientorders/postPatientOrderReg";

    String getLocationMaster = BaseDomain+"HMIS-LIS/v1/api/tolocationmaster/gettolocationmasterbyfacilityid";

    String getLabName = BaseDomain+"Appmaster/v1/api/facility/otherFaciltiySearchDropdown";

    String GetRefrenceTestMethod = BaseDomain +"HMIS-LIS/v1/api/commonReference/getAllReference";

    String updateQuickRegistration = BaseDomain+"registration/v1/api/quickRegistration/updatePatientDetails";

    String rejectData = BaseDomain+"HMIS-LIS/v1/api/sampletransportdetails/sampleRejectForAll";

    String sampleRecived=BaseDomain+"HMIS-LIS/v1/api/sampletransportdetails/sampletransportreceived";

    String getLAbNameinList=BaseDomain+"Appmaster/v1/api/facility/getFacilityExclusiveById";

    String getApplicationRules=BaseDomain+"Appmaster/v1/api/applicationRuleSettings/getApplicationRuleSettings";

    String getLabTestList = BaseDomain+"HMIS-LIS/v1/api/viewlabtest/getviewlabtest";

    String getAssignedSpinnerList = BaseDomain+"Appmaster/v1/api/facility/getAllFacility";

    String orderProcess = BaseDomain+"HMIS-LIS/v1/api/patientworkorder/orderProcessSave";

    String getUserProfile = BaseDomain+"Appmaster/v1/api/userProfile/getUserProfile";

    String getSampleAcceptance = BaseDomain+"HMIS-LIS/v1/api/viewlabtest/getviewlabtest";

    String getLabTestApproval = BaseDomain+"HMIS-LIS/v1/api/viewlabtest/getviewlabtest";

    String rapidSave=BaseDomain+"HMIS-LIS/v1/api/patientworkorder/saveandapproval";

    String getApprovalResultSpinner = BaseDomain + "HMIS-LIS/v1/api/commonReference/getReference";
    String GetDocumentType = BaseDomain + "HMIS-EMR/v1/api/patientattachments/getattachmenttype";
    String GetAddDocumentType = BaseDomain + "HMIS-EMR/v1/api/patientattachments/getAllAttachments";

    String GetUploadFile = BaseDomain + "HMIS-EMR/v1/api/patientattachments/upload";
    String GetDownload = BaseDomain +"HMIS-Login/1.0.0/api/file/read";
    String DeleteAttachmentsRows = BaseDomain + "HMIS-EMR/v1/api/patientattachments/deleteAttachmentDetails";

    String GetAdmissionWardList = BaseDomain + "HMIS-IP-Management/v1/api/ward/getWardByLoggedInFacilityWithDept";

    String GetReason = BaseDomain + "HMIS-EMR/v1/api/referal-reasons/getReferralReasons";

    String GetNextOrder = BaseDomain+"HMIS-EMR/v1/api/patient-referral/createPatientReferral";

    String updateHistoryDiagnosis = BaseDomain + "HMIS-EMR/v1/api/patient-diagnosis/updatePatientDiagnosis";

    String investigationResult = BaseDomain + "HMIS-RMIS/v1/api/patientorders/getradioandinvestigationpatientorder";

    String GetNoteTemplate = BaseDomain+"HMIS-EMR/v1/api/notetemplate/getNoteTemplate";
    String GetTemplateItem= BaseDomain+"HMIS-EMR/v1/api/notetemplate/getNoteTemplateById";
    String GetSaveScertificate = BaseDomain+"HMIS-EMR/v1/api/certificates/create";
    String GetCertificateAll = BaseDomain+"HMIS-EMR/v1/api/certificates/GetAll?";



    String getAllBloodRequestOrPurpose = BaseDomain + "Bloodbank/v1/api/newReference/getall";

    String getPreviousBloodRequest = BaseDomain + "Bloodbank/v1/api/bloodRequest/getpreviousbloodRequestbyID";


    //Login
    @FormUrlEncoded
    @POST(Login)
    Call<LoginResponseModel> getLoginDetails(@Field("username") String username,
                                             @Field("password") String password);

    //Search
    @POST(Register)
    Call<SearchResponseModel> searchOutPatient(@Header("Accept-Language") String acceptLanguage,
                                               @Header("Authorization") String authorization,
                                               @Header("user_uuid") int user_uuid,
                                               @Header("facility_uuid") int facility_uuid,
                                               @Body SearchPatientRequestModel searchPatientRequestModel);
    //DashBoard
    @GET(DashBoardDetail)
    Call<DashBoardResponse> getDashBoardResponse( @Header("Authorization") String authorization,
                                                  @Header("user_uuid") int user_uuid,
                                                  @Query("depertment_Id") int depertment_id,
                                                  @Query("from_date") String fromData,
                                                  @Query("to_date") String toDate);

    //GetEMRWorkFlow
    @GET(EMRWORKFLOW)
    Call<EmrWorkFlowResponseModel> getEmrWorkflow(
            @Header("Authorization") String authorization,
            @Header("user_uuid") int user_uuid);

    //Configuation
    @POST(GetConfigList)
    Call<ConfigResponseModel> getConfigList(@Header("Authorization") String authorization,
                                            @Header("user_uuid") int user_uuid, @Body RequestBody body);

    //Config Update
    @PUT(GetconfigUpdate)
    Call<ConfigUpdateResponseModel> getConfigUpdate(@Header("Authorization") String authorization,
                                                    @Header("user_uuid") int user_uuid,
                                                    @Header("facility_uuid") int facility_uuid,
                                                    @Body ArrayList<ConfigUpdateRequestModel> configRequestData);

    @GET(GetFavorites)
    Call<FavouritesResponseModel> getFavourites(
            @Header("Authorization") String authorization,
            @Header("user_uuid") int user_uuid,
            @Query("dept_id") int dept_id,
            @Query("fav_type_id") int fav_type_id);

    @GET(GetFavorites)
    Call<PrescriptionFavResponseModel> getPrescriptionFavourites(
            @Header("Authorization") String authorization,
            @Header("user_uuid") int user_uuid,
            @Query("dept_id") int dept_id,
            @Query("fav_type_id") int fav_type_id);


    /*
      Templete
     */
    @GET(GetTemplete)
    Call<TempleResponseModel> getTemplete(
            @Header("Authorization") String authorization,
            @Header("user_uuid") int user_uuid,
            @Query("dept_id") int dept_id,
            @Query("temp_type_id") int temp_type_id);


    @GET(GetPrescriptionTemplete)
    Call<PrescriptionTemplateResponseModel> getPrescriptionTemplete(
            @Header("Authorization") String authorization,
            @Header("user_uuid") int user_uuid,
            @Query("dept_id") int dept_id,
            @Query("temp_type_id") int temp_type_id);


    @POST(GetOfficeList)
    Call<OfficeResponseModel> getOfficeList(@Header("Authorization") String authorization,
                                            @Header("user_uuid") int user_uuid, @Body RequestBody body);

    @GET(GetDuration)
    Call<DurationResponseModel> getDuration(@Header("Authorization") String authorization,
                                            @Header("user_uuid") int user_uuid);

    @GET(GetChiefComplaintsSearchResult)
    Call<ComplaintSearchResponseModel> getChiefComplaintsSearchResult(@Header("Authorization") String authorization,
                                                                      @Header("user_uuid") int user_uuid,
                                                                      @Query("searchBy") String searchBy,
                                                                      @Query("searchValue") String searchValue);

    @POST(GetLabSearchResult)
    Call<FavSearchResponce> getLAbSearchResult(@Header("Authorization") String authorization,
                                               @Header("user_uuid") int user_uuid,
                                               @Header("facility_uuid") int facility_uuid,
                                               @Body RequestBody body);

    @POST(GetRadioSearchResult)
    Call<FavSearchResponce> getRadioSearchResult(@Header("Authorization") String authorization,
                                                 @Header("user_uuid") int user_uuid,
                                                 @Header("facility_uuid") int facility_uuid,
                                                 @Body RequestBody body);

    @POST(GetINvestigationsSearchResult)
    Call<InvestigationSearchResponseModel> getInvestigationSearch(@Header("Authorization") String authorization,
                                                                  @Header("user_uuid") int user_uuid,
                                                                  @Header("facility_uuid") int facility_uuid,
                                                                  @Body RequestBody body);


    //getInstitutionlistrrrww
    @POST(GetInstitutionList)
    Call<InstitutionResponseModel> getInstitutionList(@Header("Authorization") String authorization,
                                                      @Header("user_uuid") int user_uuid, @Body RequestBody body);


    /**
     * Get Department List
     *
     * @param authorization
     * @param user_uuid
     * @param body
     * @return
     */
    @POST(GetDepartmentList)
    Call<DepartmentResponseModel> getDepartmentList(@Header("Authorization") String authorization,
                                                    @Header("user_uuid") int user_uuid, @Body RequestBody body);

    @GET(GetEncounters)
    Call<FectchEncounterResponseModel> getEncounters(@Header("Authorization") String authorization,
                                                     @Header("user_uuid") int user_uuid,
                                                     @Query("patientId") int patientId,
                                                     @Query("doctorId") int doctorId,
                                                     @Query("departmentId") int departmentId,
                                                     @Query("encounterType") int encounterType);

    @POST(CreateEncounter)
    Call<CreateEncounterResponseModel> createEncounter(@Header("Authorization") String authorization,
                                                       @Header("user_uuid") int user_uuid,
                                                       @Body CreateEncounterRequestModel createEncounterRequestModel);
    /*
    Type
     */
    @POST(GetLabType)
    Call<LabTypeResponseModel> getLabType(@Header("Authorization") String authorization,
                                          @Header("user_uuid") int user_uuid,
                                          @Header("facility_uuid") int facility_uuid,
                                          @Body RequestBody labtype);

    /*GET to Location*/
    @POST(GetToLocation)
    Call<LabToLocationResponse> getToLocation(@Header("Authorization") String authorization,
                                              @Header("user_uuid") int user_uuid,
                                              @Header("facility_uuid") int facility_uuid);

    @POST(GetPrevLab)
    Call<PrevLabResponseModel> getPrevLab(@Header("Authorization") String authorization,
                                          @Header("user_uuid") int user_uuid,
                                          @Header("facility_uuid") int facility_uuid,
                                          @Body RequestBody body);

    @POST(GetPrevInvestigation)
    Call<InvestigationPrevResponseModel> getPrevInvestigation(@Header("Authorization") String authorization,
                                                              @Header("user_uuid") int user_uuid,
                                                              @Header("facility_uuid") int facility_uuid,
                                                              @Body RequestBody body);

    @POST(GetFavDepartmentList)
    Call<FavAddResponseModel> getFavDepartmentList(@Header("Authorization") String authorization,
                                                   @Header("user_uuid") int user_uuid,
                                                   @Header("facility_uuid") int facility_uuid,
                                                   @Body RequestBody body);

    @POST(GetFavaddDepartmentList)
    Call<FavAddAllDepatResponseModel> getFavddAllADepartmentList(@Header("Authorization") String authorization,
                                                                 @Header("user_uuid") int user_uuid,
                                                                 @Header("facility_uuid") int facility_uuid,
                                                                 @Body RequestBody body);

    @POST(EmrPost)
    Call<EmrResponceModel> Emrpost(@Header("Authorization") String authorization,
                                   @Header("user_uuid") int user_uuid,
                                   @Header("facility_uuid") int facility_uuid,
                                   @Body EmrRequestModel emrRequestModelr);


    //DEVHMIS-LIS/v1/api/testmaster/gettestandprofileinfo

    @POST(GetLabSearchResult)
    Call<FavAddTestNameResponse> getAutocommitText(@Header("Authorization") String authorization,
                                                   @Header("user_uuid") int user_uuid,
                                                   @Header("facility_uuid") int facility_uuid,
                                                   @Body RequestBody body);

    @POST(GetRadioSearchResult)
    Call<FavAddTestNameResponse> getRadioAutocommitText(@Header("Authorization") String authorization,
                                                        @Header("user_uuid") int user_uuid,
                                                        @Header("facility_uuid") int facility_uuid,
                                                        @Body RequestBody body);

    @GET(GetVitalsTemplatet)
    Call<VitalsTemplateResponseModel> getVitalsTemplatet(@Header("Authorization") String authorization,
                                                         @Header("user_uuid") int user_uuid,
                                                         @Header("facility_uuid") int facility_uuid,
                                                         @Query("dept_id") int dept_id,
                                                         @Query("temp_type_id") int temp_type_id,
                                                         @Query("user_uuid") int user_uid);


    @POST(InsertChiefComplaint)
    Call<ChiefComplaintResponse> insertChiefComplaint(@Header("Authorization") String authorization,
                                                      @Header("user_uuid") int user_uuid,
                                                      @Header("facility_uuid") int facility_uuid,
                                                      @Body ArrayList<ChiefComplaintRequestModel> configRequestData);


    @GET(GetHistoryAll)
    Call<HistoryResponce> getHistoryAll(@Header("Authorization") String authorization,
                                        @Header("user_uuid") int user_uuid,
                                        @Header("facility_uuid") int facility_uuid);


    @GET(GetEncounterAllergyType)
    Call<EncounterAllergyTypeResponse> getEncounterAllergyType(@Header("Authorization") String authorization,
                                                               @Header("user_uuid") int user_uuid,
                                                               @Header("facility_uuid") int facility_uuid);

    /*
  Radiology Prev-Radiology
   */
    @POST(GetPrevRadiology)
    Call<PrevLabResponseModel> getPrevRadiology(@Header("Authorization") String authorization,
                                                @Header("user_uuid") int user_uuid,
                                                @Header("facility_uuid") int facility_uuid,
                                                @Body RequestBody body);

    @POST(GetAllergyName)
    Call<AllergyNameResponse> getAllergyName(@Header("Authorization") String authorization,
                                             @Header("user_uuid") int user_uuid,
                                             @Header("facility_uuid") int facility_uuid);

    @POST(GetAllergySource)
    Call<AllergySourceResponse> getAllergySource(@Header("Authorization") String authorization,
                                                 @Header("user_uuid") int user_uuid,
                                                 @Header("facility_uuid") int facility_uuid,
                                                 @Body RequestBody allergysource);

    @POST(GetAllergySeverity)
    Call<AllergySeverityResponse> getAllergySeverity(@Header("Authorization") String authorization,
                                                     @Header("user_uuid") int user_uuid,
                                                     @Header("facility_uuid") int facility_uuid,
                                                     @Body RequestBody allergysource);

    @POST(GetPrevPrescription)
    Call<PrevPrescriptionModel> getPrevPrescription(@Header("Authorization") String authorization,
                                                    @Header("user_uuid") int user_uuid,
                                                    @Header("facility_uuid") int facility_uuid,
                                                    @Body RequestBody body);

    @POST(GetFrequency)
    Call<PresDrugFrequencyResponseModel> getFrequency(@Header("Authorization") String authorization,
                                                      @Header("user_uuid") int user_uuid,
                                                      @Header("facility_uuid") int facility_uuid,
                                                      @Body RequestBody body);

    @POST(GetPrescriptionDuration)
    Call<PrescriptionDurationResponseModel> getPrescriptionDuration(@Header("Authorization") String authorization,
                                                                    @Header("user_uuid") int user_uuid,
                                                                    @Header("facility_uuid") int facility_uuid,
                                                                    @Body RequestBody body);

    @POST(InsertDiagnosis)
    Call<DiagnosisResponseModel> insertDiagnosis(@Header("Authorization") String authorization,
                                                 @Header("user_uuid") int user_uuid,
                                                 @Header("facility_uuid") int facility_uuid,
                                                 @Body ArrayList<DiagnosisRequest> configRequestData);

    @GET(SearchDiagnosis)
    Call<DiagnosisSearchResponseModel> searchDiagnosis(@Header("Authorization") String authorization,
                                                       @Header("user_uuid") int user_uuid,
                                                       @Header("facility_uuid") int facility_uuid,
                                                       @Query("searchBy") String searchBy,
                                                       @Query("searchValue") String searchValue);


    /*
    Radiology ORDER TO LOCATION
     */
    @POST(GetToLocationRadiology)
    Call<LabToLocationResponse> getToLocationRadiology(@Header("Authorization") String authorization,
                                                       @Header("user_uuid") int user_uuid,
                                                       @Header("facility_uuid") int facility_uuid);

    /*
    Radiology post data
     */
    @POST(EmrRadiologypost)
    Call<EmrResponceModel> EmrRadiologypost(@Header("Authorization") String authorization,
                                            @Header("user_uuid") int user_uuid,
                                            @Header("facility_uuid") int facility_uuid,
                                            @Body EmrRequestModel emrRequestModel);

    /*
    Prescription Search*/
    @POST(GetPrescriptionsSearchResult)
    Call<PrescriptionSearchResponseModel> getPrescriptionsSearchResult(@Header("Authorization") String authorization,
                                                                       @Header("user_uuid") int user_uuid,
                                                                       @Header("facility_uuid") int facility_uuid,
                                                                       @Body RequestBody body);


    @POST(GetInstruction)
    Call<PresInstructionResponseModel> getInstruction(@Header("Authorization") String authorization,
                                                      @Header("user_uuid") int user_uuid,
                                                      @Header("facility_uuid") int facility_uuid,
                                                      @Body RequestBody body);

    /*
    Prescription all data post
     */
    @POST(EmrPrescriptionPost)
    Call<PrescriptionPostAllDataResponseModel> EmrPrescriptionPost(@Header("Authorization") String authorization,
                                                                   @Header("user_uuid") int user_uuid,
                                                                   @Header("facility_uuid") int facility_uuid,
                                                                   @Body emr_prescription_postalldata_requestmodel emrPrescriptionRequestModel);

    /*
    Route
     */
    @POST(GetRouteDetails)
    Call<PrescriptionRoutResponseModel> getRouteDetails(@Header("Authorization") String authorization,
                                                        @Header("user_uuid") int user_uuid,
                                                        @Header("facility_uuid") int facility_uuid,
                                                        @Body RequestBody body);


    @GET(GetAllergyAll)
    Call<AllergyAllGetResponseModel> getAllergyAll(@Header("Authorization") String authorization,
                                                   @Header("user_uuid") int user_uuid,
                                                   @Header("facility_uuid") int facility_uuid,
                                                   @Query("patient_uuid") int patient_uuid);

    @PUT(DeleteRows)
    Call<DeleteResponseModel> deleteRows(@Header("Authorization") String authorization,
                                         @Header("user_uuid") int user_uuid,
                                         @Header("facility_uuid") int facility_uuid,
                                         @Body RequestBody body);

    @PUT(DeleteTemplate)
    Call<DeleteResponseModel> deleteTemplate(@Header("Authorization") String authorization,
                                             @Header("user_uuid") int user_uuid,
                                             @Header("facility_uuid") int facility_uuid,
                                             @Body RequestBody body);

    @POST(GetFavddAll)
    Call<LabFavManageResponseModel> getFavddAll(@Header("Authorization") String authorization,
                                                @Header("user_uuid") int user_uuid,
                                                @Header("facility_uuid") int facility_uuid,
                                                @Body RequestLabFavModel body);


    @POST(GetRadiologyFavourite)
    Call<LabFavManageResponseModel> getRadilogyFavAddAll(@Header("Authorization") String authorization,
                                                         @Header("user_uuid") int user_uuid,
                                                         @Header("facility_uuid") int facility_uuid,
                                                         @Body RequestLabFavModel body);


    @POST(GetFavAddDiagonosis)
    Call<LabFavManageResponseModel> getDiagonosisFavAddAll(@Header("Authorization") String authorization,
                                                           @Header("user_uuid") int user_uuid,
                                                           @Header("facility_uuid") int facility_uuid,
                                                           @Body ChiefCompliantAddRequestModel body);


    @GET(GetFavddAllList)
    Call<FavAddListResponse> getFavddAllList(@Header("Authorization") String authorization,
                                             @Header("user_uuid") int user_uuid,
                                             @Header("facility_uuid") int facility_uuid,
                                             @Query("favourite_id") int favourite_id);

    @POST(GetMyProfile)
    Call<MyProfileResponseModel> getMyProfile(@Header("Authorization") String authorization,
                                              @Header("user_uuid") int user_uuid,
                                              @Header("facility_uuid") int facility_uuid,
                                              @Body RequestBody body);

    @POST(GetOtpForPasswordChange)
    Call<ChangePasswordOTPResponseModel> getOtpForPasswordChange(@Body RequestBody body);

    @POST(GetPasswordChanged)
    Call<PasswordChangeResponseModel> getPasswordChanged(@Body RequestBody body);

    @POST(GetHistoryPrescription)
    Call<PrescriptionHistoryResponseModel> getHistoryPrescription(@Header("Authorization") String authorization,
                                                                  @Header("user_uuid") int user_uuid,
                                                                  @Header("facility_uuid") int facility_uuid,
                                                                  @Body RequestBody body);

    @GET(GetFamilyAllType)
    Call<FamilyHistoryResponseModel> getFamilyAllType(@Header("Authorization") String authorization,
                                                      @Header("user_uuid") int user_uuid,
                                                      @Header("facility_uuid") int facility_uuid, @Query("patient_uuid") int patient_uuid);

    @POST(AddFavChiefComplaint)
    Call<ChiefComplaintFavAddresponseModel> AddFavChiefComplaint(@Header("Authorization") String authorization,
                                                                 @Header("user_uuid") int user_uuid,
                                                                 @Header("facility_uuid") int facility_uuid,
                                                                 @Query("searchkey") String searchkey,
                                                                 @Body ChiefCompliantAddRequestModel chiefCompliantAddRequestModel);

    @POST(CreateAllergy)
    Call<AllergyCreateResponseModel> createAllergy(@Header("Authorization") String authorization,
                                                   @Header("user_uuid") int user_uuid,
                                                   @Header("facility_uuid") int facility_uuid,
                                                   @Body RequestBody body);

    @GET(getHistoryVitals)
    Call<HistoryVitalsResponseModel> getHistoryVitals(@Header("Authorization") String authorization,
                                                      @Header("user_uuid") int user_uuid,
                                                      @Header("facility_uuid") int facility_uuid,
                                                      @Query("patient_uuid") int patient_uuid,
                                                      @Query("department_uuid") int department_uuid);

    @GET(getVitalName)
    Call<VitalsListResponseModel> getVitalsName(@Header("Authorization") String authorization,
                                                @Header("user_uuid") int user_uuid,
                                                @Header("facility_uuid") int facility_uuid);

    @POST(getZeroStock)
    Call<ZeroStockResponseModel> getZeroStock(@Header("Authorization") String authorization,
                                              @Header("user_uuid") int user_uuid,
                                              @Header("facility_uuid") int facility_uuid,
                                              @Body RequestBody body);


    //Config Update
    @PUT(GetHistoryconfigUpdate)
    Call<ConfigUpdateResponseModel> getHistoryConfigUpdate(@Header("Authorization") String authorization,
                                                           @Header("user_uuid") int user_uuid,
                                                           @Header("facility_uuid") int facility_uuid,
                                                           @Body ArrayList<ConfigUpdateRequestModel> configRequestData);


    @POST(GetFamilyType)
    Call<FamilyTypeSpinnerResponseModel> getHistoryFamilyType(@Header("Authorization") String authorization,
                                                              @Header("user_uuid") int user_uuid,
                                                              @Header("facility_uuid") int facility_uuid,
                                                              @Body RequestBody body);

    @POST(CreateFamilyHistory)
    Call<CreateFamilyHistoryResponseModel> createFamilyHistory(@Header("Authorization") String authorization,
                                                               @Header("user_uuid") int user_uuid,
                                                               @Header("facility_uuid") int facility_uuid,
                                                               @Body RequestBody body);

    @POST(GetSurgeryInstitutions)
    Call<SurgeryInstitutionResponseModel> getInstitutions(@Header("Authorization") String authorization,
                                                          @Header("user_uuid") int user_uuid,
                                                          @Header("facility_uuid") int facility_uuid,
                                                          @Body RequestBody body);

    @POST(GetSurgeryInstitutions)
    Call<SurgeryInstitutionResponseModel> getFaciltyCheck(@Header("Authorization") String authorization,
                                                          @Header("user_uuid") int user_uuid,
                                                          @Body RequestBody body);

    @GET(GetSurgeryDetails)
    Call<HistorySurgeryResponseModel> getSurgery(@Header("Authorization") String authorization,
                                                 @Header("user_uuid") int user_uuid,
                                                 @Header("facility_uuid") int facility_uuid,
                                                 @Query("patient_uuid") int patient_uuid,
                                                 @Query("patient_uuid") int patient_uuidd);

    @POST(GetSurgeryName)
    Call<SurgeryNameResponseModel> getName(@Header("Authorization") String authorization,
                                           @Header("user_uuid") int user_uuid,
                                           @Header("facility_uuid") int facility_uuid,
                                           @Body RequestBody body);


    @POST(CreateSugery)
    Call<CreateSurgeryResponseModel> createSurgery(@Header("Authorization") String authorization,
                                                   @Header("user_uuid") int user_uuid,
                                                   @Header("facility_uuid") int facility_uuid,
                                                   @Body RequestBody body);

    @GET(getDiagnosisHistory)
    Call<HistoryDiagnosisResponseModel> getDiagnosisList(@Header("Authorization") String authorization,
                                                         @Header("user_uuid") int user_uuid,
                                                         @Header("facility_uuid") int facility_uuid,
                                                         @Query("searchKey") String searchKey,
                                                         @Query("searchValue") int searchValue,
                                                         @Query("patientId") int patientId,
                                                         @Query("departmentId") int departmentId
    );

    @POST(PrescriptionInfo)
    Call<PrescriptionInfoResponsModel> getPrescriptionInfo(@Header("Authorization") String authorization,
                                                           @Header("user_uuid") int user_uuid,
                                                           @Header("facility_uuid") int facility_uuid,
                                                           @Body RequestBody body);


    @PUT(FavouriteUpdate)
    Call<FavEditResponse> labEditFav(@Header("Authorization") String authorization,
                                     @Header("user_uuid") int user_uuid,
                                     @Header("facility_uuid") int facility_uuid,
                                     @Body RequestBody body);


    @PUT(FavouriteUpdate)
    Call<ResponsePreFavEdit> PresEditFav(@Header("Authorization") String authorization,
                                         @Header("user_uuid") int user_uuid,
                                         @Header("facility_uuid") int facility_uuid,
                                         @Body UpdatePrescriptionRequest body);

    @POST(LabTemplateCreate)
    Call<ReponseTemplateadd> createTemplate(@Header("Authorization") String authorization,
                                            @Header("user_uuid") int user_uuid,
                                            @Header("facility_uuid") int facility_uuid,
                                            @Body RequestTemplateAddDetails body);

    @POST(GetImmunizationName)
    Call<ImmunizationNameResponseModel> getImmunizationName(@Header("Authorization") String authorization,
                                                            @Header("user_uuid") int user_uuid,
                                                            @Header("facility_uuid") int facility_uuid);


    @GET(GetImmunizationList)
    Call<GetImmunizationResponseModel> getImmunizationAll(@Header("Authorization") String authorization,
                                                          @Header("user_uuid") int user_uuid,
                                                          @Header("facility_uuid") int facility_uuid,
                                                          @Query("patient_uuid") int patient_uuid);


    @POST(CreateImmunization)
    Call<CreateImmunizationResponseModel> createImmunization(@Header("Authorization") String authorization,
                                                             @Header("user_uuid") int user_uuid,
                                                             @Header("facility_uuid") int facility_uuid,
                                                             @Body RequestBody body);

    @POST(GetImmunizationInstitution)
    Call<ImmunizationInstitutionResponseModel> getImmunizationInstitution(@Header("Authorization") String authorization,
                                                                          @Header("user_uuid") int user_uuid,
                                                                          @Header("facility_uuid") int facility_uuid,
                                                                          @Body RequestBody body);

    @GET(LabGetTemplate)
    Call<ResponseLabGetTemplateDetails> getLastTemplate(@Header("Authorization") String authorization,
                                                        @Header("user_uuid") int user_uuid,
                                                        @Header("facility_uuid") int facility_uuid,
                                                        @Query("temp_id") int temp_id,
                                                        @Query("temp_type_id") int temp_type_id,
                                                        @Query("dept_id") int dept_id);

    @GET(VitualGetTemplate)
    Call<ResponseEditTemplate> getLastVitualTemplate(@Header("Authorization") String authorization,
                                                     @Header("user_uuid") int user_uuid,
                                                     @Header("facility_uuid") int facility_uuid,
                                                     @Query("temp_id") int temp_id,
                                                     @Query("temp_type_id") int temp_type_id,
                                                     @Query("dept_id") int dept_id);
/*
    @GET(GetVitalSearchName)
    Call<VitalsSearchResponseModel> getVitalSearch(@Header("Authorization") String authorization,
                                                    @Header("user_uuid")int user_uuid,
                                                    @Header("facility_uuid")int facility_uuid);
*/
 /*   @GET(GetVitalSearchName)
    Call<VitalsSearchResponseModel> getVitalSearch(@Header("Authorization") String authorization,
                                                @Header("user_uuid") int user_uuid,
                                                @Header("facility_uuid") int facility_uuid);*/


    @PUT(LabUpdateTemplate)
    Call<UpdateResponse> getTemplateUpdate(@Header("Authorization") String authorization,
                                           @Header("user_uuid") int user_uuid,
                                           @Header("facility_uuid") int facility_uuid,
                                           @Body UpdateRequestModule body);

    @POST(GatUomVitalList)
    Call<UomListResponceModel> gatUomVitalList(@Header("Authorization") String authorization,
                                               @Header("user_uuid") int user_uuid,
                                               @Header("facility_uuid") int facility_uuid,
                                               @Body RequestBody body);

    @GET(DiagonosisSearcbValue)
    Call<DiagonosisSearchResponse> getDiagonosisName(@Header("Authorization") String authorization,
                                                     @Header("user_uuid") int user_uuid,
                                                     @Header("facility_uuid") int facility_uuid,
                                                     @Query("searchBy") String searchby,
                                                     @Query("searchValue") String searchValue);


    @POST(PrescriptionSearch)
    Call<SearchPrescriptionResponseModel> getprescriptionSearch(@Header("Authorization") String authorization,
                                                                @Header("user_uuid") int user_uuid,
                                                                @Header("facility_uuid") int facility_uuid,
                                                                @Body RequestBody body);


    @POST(LabTemplateCreate)
    Call<SaveTemplateResponseModel> savePrescriptionTemplate(@Header("Authorization") String authorization,
                                                             @Header("user_uuid") int user_uuid,
                                                             @Header("facility_uuid") int facility_uuid,
                                                             @Body SaveTemplateRequestModel body);


    @POST(LabUpdateTemplate)
    Call<UpdateResponseModel> updatePrescriptionTemplate(@Header("Authorization") String authorization,
                                                         @Header("user_uuid") int user_uuid,
                                                         @Header("facility_uuid") int facility_uuid,
                                                         @Body UpdateRequestModel body);

    @POST(labResult)
    Call<LabResultResponseModel> getLabResult(@Header("Authorization") String authorization,
                                              @Header("user_uuid") int user_uuid,
                                              @Header("facility_uuid") int facility_uuid,
                                              @Body RequestBody body);

    @POST(radiologyResult)
    Call<RadiologyResultResponseModel> getRadiologyResult(@Header("Authorization") String authorization,
                                                          @Header("user_uuid") int user_uuid,
                                                          @Header("facility_uuid") int facility_uuid,
                                                          @Body RequestBody body);

    @POST(investigationResult)
    Call<InvestigationResultResponseModel> getInvestigationResult(@Header("Accept-Language") String acceptLanguage,
                                                                     @Header("Authorization") String authorization,
                                                                  @Header("user_uuid") int user_uuid,
                                                                  @Header("facility_uuid") int facility_uuid,
                                                                  @Body RequestBody body);



    @PUT(UpdateAllergy)
    Call<AllergyUpdateResponseModel> updateAllergy(@Header("Authorization") String authorization,
                                                   @Header("user_uuid") int user_uuid,
                                                   @Header("facility_uuid") int facility_uuid,
                                                   @Query("uuid") int uuid,
                                                   @Body RequestBody body);


    @PUT(UpdateFamilyHistory)
    Call<FamilyUpdateResponseModel> updateFamilyHistory(@Header("Authorization") String authorization,
                                                        @Header("user_uuid") int user_uuid,
                                                        @Header("facility_uuid") int facility_uuid,
                                                        @Query("uuid") int uuid,
                                                        @Body RequestBody body);

    @PUT(UpdateSurgery)
    Call<SurgeryUpdateResponseModel> updateSurgery(@Header("Authorization") String authorization,
                                                   @Header("user_uuid") int user_uuid,
                                                   @Header("facility_uuid") int facility_uuid,
                                                   @Query("uuid") int uuid,
                                                   @Body RequestBody body);

    @GET(GetVitalSearchName)
    Call<VitalsSearchResponseModel> getVitalSearch(@Header("Authorization") String authorization,
                                                   @Header("user_uuid") int user_uuid,
                                                   @Header("facility_uuid") int facility_uuid);

    @POST(VitalSave)
    Call<VitalSaveResponseModel> saveVitals(@Header("Authorization") String authorization,
                                            @Header("user_uuid") int user_uuid,
                                            @Header("facility_uuid") int facility_uuid,
                                            @Body ArrayList<VitalSaveRequestModel> configRequestData);


    @GET(VitalSearch)
    Call<VitalSearchListResponseModel> getVitals(@Header("Authorization") String authorization,
                                                 @Header("user_uuid") int user_uuid,
                                                 @Header("facility_uuid") int facility_uuid);

    @POST(GetStoreMaster)
    Call<GetStoreMasterResponseModel> getStoreMaster(@Header("Authorization") String authorization,
                                                     @Header("user_uuid") int user_uuid,
                                                     @Header("facility_uuid") int facility_uuid,
                                                     @Body RequestBody body);

    @POST(PrescriptionFavAdd)
    Call<ResponsePrescriptionFav> getPrescriptionFavAddAll(@Header("Authorization") String authorization,
                                                           @Header("user_uuid") int user_uuid,
                                                           @Header("facility_uuid") int facility_uuid,
                                                           @Body RequestPrecEditModule body);

    @POST(GetLabResultByDate)
    Call<LabResultGetByDataResponseModel> getLabResultByDate(@Header("Authorization") String authorization,
                                                             @Header("user_uuid") int user_uuid,
                                                             @Header("facility_uuid") int facility_uuid,
                                                             @Body RequestBody body);

    @POST(GetPrevTreatmentKit)
    Call<TreatmentKitPrevResponsModel> getPrevTreatmentKit(@Header("Authorization") String authorization,
                                                           @Header("user_uuid") int user_uuid,
                                                           @Header("facility_uuid") int facility_uuid,
                                                           @Query("patient_uuid") int uuid);

    @POST(CreateHistoryDiagnosis)
    Call<HistoryDiagnosisCreateResponseModel> createDiagnosis(@Header("Authorization") String authorization,
                                                              @Header("user_uuid") int user_uuid,
                                                              @Header("facility_uuid") int facility_uuid,
                                                              @Body RequestBody body);

    @GET(GetSnommed)
    Call<SnomedDataResponseModel> getSnommed(@Header("Authorization") String authorization,
                                             @Header("user_uuid") int user_uuid,
                                             @Header("facility_uuid") int facility_uuid,
                                             @Query("key") String key);

    @GET(GetParentSnommed)
    Call<SnomedParentDataResponseModel> getParentSnommed(@Header("Authorization") String authorization,
                                                         @Header("user_uuid") int user_uuid,
                                                         @Header("facility_uuid") int facility_uuid,
                                                         @Query("key") String key);

    @GET(GetChildSnommed)
    Call<SnomedChildDataResponseModel> getChildSnommed(@Header("Authorization") String authorization,
                                                       @Header("user_uuid") int user_uuid,
                                                       @Header("facility_uuid") int facility_uuid,
                                                       @Query("key") String key);

    @POST(getOpNotes)
    Call<OpNotesResponsModel> getOpNotes(@Header("Authorization") String authorization,
                                         @Header("user_uuid") int user_uuid,
                                         @Header("facility_uuid") int facility_uuid);

    /*Call<OpNotesSpinnerResponseModel> getOpNotes(@Header("Authorization") String authorization,
                                                 @Header("user_uuid") int user_uuid,
                                                 @Header("facility_uuid") int facility_uuid);
*/
    @GET(getOPNotesAll)
    Call<OpNotesGetAllResponseModel> getAllOP(@Header("Authorization") String authorization,
                                              @Header("user_uuid") int user_uuid,
                                              @Header("facility_uuid") int facility_uuid,
                                              @Query("profile_uuid") int profile_uuid);


    @POST(getState)
    Call<StateListResponseModel> getState(@Header("Authorization") String authorization,
                                          @Header("user_uuid") int user_uuid,
                                          @Header("facility_uuid") int facility_uuid,
                                          @Body RequestBody body);

    @POST(getDistict)
    Call<DistrictListResponseModel> getDistict(@Header("Authorization") String authorization,
                                               @Header("user_uuid") int user_uuid,
                                               @Header("facility_uuid") int facility_uuid,
                                               @Body RequestBody body);

    @POST(getTaluk)
    Call<TalukListResponseModel> getTaluk(@Header("Authorization") String authorization,
                                          @Header("user_uuid") int user_uuid,
                                          @Header("facility_uuid") int facility_uuid,

                                          @Body RequestBody body);

    @POST(getVillage)
    Call<VilliageListResponceModel> getVillage(@Header("Authorization") String authorization,
                                               @Header("user_uuid") int user_uuid,
                                               @Header("facility_uuid") int facility_uuid,
                                               @Body RequestBody body);


    @POST(GetSalutationTitiles)
    Call<CovidSalutationTitleResponseModel> getCovidNameTitle(@Header("Authorization") String authorization,
                                                              @Header("user_uuid") int user_uuid,
                                                              @Header("facility_uuid") int facility_uuid);

    @POST(GetGender)
    Call<CovidGenderResponseModel> getCovidGender(@Header("Authorization") String authorization,
                                                  @Header("user_uuid") int user_uuid,
                                                  @Header("facility_uuid") int facility_uuid);

    @GET(GetPeriod)
    Call<CovidPeriodResponseModel> getCovidPeriod(@Header("Accept-Language") String acceptLanguage,
                                                  @Header("Authorization") String authorization,
                                                  @Header("user_uuid") int user_uuid,
                                                  @Header("facility_uuid") int facility_uuid);

    @GET(GetNationalityAndMobileAndPatientCateType)
    Call<CovidNationalityResponseModel> getNationality(@Header("Accept-Language") String acceptLanguage,
                                                       @Header("Authorization") String authorization,
                                                       @Header("user_uuid") int user_uuid,
                                                       @Header("facility_uuid") int facility_uuid,
                                                       @Query("table_name") String tableName);

    @GET(GetNationalityAndMobileAndPatientCateType)
    Call<CovidMobileBelongsToResponseModel> getMobileBelongsTo(@Header("Accept-Language") String acceptLanguage,
                                                               @Header("Authorization") String authorization,
                                                               @Header("user_uuid") int user_uuid,
                                                               @Header("facility_uuid") int facility_uuid,
                                                               @Query("table_name") String tableName);

    @GET(GetNationalityAndMobileAndPatientCateType)
    Call<CovidPatientCategoryResponseModel> getPatientCategory(@Header("Accept-Language") String acceptLanguage,
                                                               @Header("Authorization") String authorization,
                                                               @Header("user_uuid") int user_uuid,
                                                               @Header("facility_uuid") int facility_uuid,
                                                               @Query("table_name") String tableName);


    @POST(addPatientDetails)
    Call<AddPatientResponse> getAddPatientDetails(@Header("Accept-Language") String acceptLanguage,
                                                  @Header("Authorization") String authorization,
                                                  @Header("user_uuid") int user_uuid,
                                                  @Header("facility_uuid") int facility_uuid,
                                                  @Body AddPatientDetailsRequest body);


    @GET(GetSpecimen_Type)
    Call<ResponseSpicemanType> getSpecimen_Type(@Header("Authorization") String authorization,
                                                @Header("user_uuid") int user_uuid,
                                                @Header("Accept-Language") String value,
                                                @Header("facility_uuid") int facility_uuid,
                                                @Query("table_name") String table_name);

    @GET(GetQuarantineType)
    Call<CovidQuarantineTypeResponseModel> getQuarantineType(@Header("Accept-Language") String acceptLanguage,
                                                             @Header("Authorization") String authorization,
                                                             @Header("user_uuid") int user_uuid,
                                                             @Header("facility_uuid") int facility_uuid);

    @POST(getCovidPatientSearch)
    Call<CovidRegistrationSearchResponseModel> getCovidPatientSearch(@Header("Authorization") String authorization,
                                                                     @Header("user_uuid") int user_uuid,
                                                                     @Header("Accept-Language") String value,
                                                                     @Header("facility_uuid") int facility_uuid,
                                                                     @Body RequestBody body);


    @PUT(CovidUpdate)
    Call<AddPatientResponse> getUpdatePatientDetails(@Header("Accept-Language") String acceptLanguage,
                                                     @Header("Authorization") String authorization,
                                                     @Header("user_uuid") int user_uuid,
                                                     @Header("facility_uuid") int facility_uuid,
                                                     @Body UpdatePatientDetailsRequest body);


    @POST(CreateTreatmentKit)
    Call<TreatmentKitCreateResponseModel> postTreatmentKit(@Header("Authorization") String authorization,
                                                           @Header("user_uuid") int user_uuid,
                                                           @Header("facility_uuid") int facility_uuid,
                                                           @Body CreateTreatmentkitRequestModel body);

    @GET(PrevChiefComplaint)
    Call<PrevChiefComplainResponseModel> getPrevChiefComplaint(@Header("Authorization") String authorization,
                                                               @Header("user_uuid") int user_uuid,
                                                               @Header("facility_uuid") int facility_uuid,
                                                               @Query("encounterTypeId") String encounterTypeId,
                                                               @Query("patientId") String patientId,
                                                               @Query("limit") String limit);


    @POST(getTreatmentkitInvestigationSearch)
    Call<TreatmentkitSearchResponseModel> getTraetmentkitSearch(@Header("Authorization") String authorization,
                                                                @Header("user_uuid") int user_uuid,
                                                                @Header("facility_uuid") int facility_uuid,
                                                                @Body RequestBody body);


    @POST(GetPrescFrequency)
    Call<PrescriptionFrequencyResponseModel> getPrescFrequency(@Header("Authorization") String authorization,
                                                               @Header("user_uuid") int user_uuid,
                                                               @Header("facility_uuid") int facility_uuid,
                                                               @Body RequestBody body);

    @GET(GetOpExpandableList)
    Call<OpNotesExpandableResponseModel> getOpExpandableList(@Header("Authorization") String authorization,
                                                             @Header("user_uuid") int user_uuid,
                                                             @Header("facility_uuid") int facility_uuid,
                                                             @Query("profile_uuid") int profile_uuid);

    @GET(PrevDiagnosis)
    Call<PreDiagnosisResponseModel> getPrevDiagnosis(@Header("Authorization") String authorization,
                                                     @Header("user_uuid") int user_uuid,
                                                     @Header("facility_uuid") int facility_uuid,
                                                     @Query("encounterTypeId") String encounterTypeId,
                                                     @Query("patientId") String patientId);

    @GET(GetRepeatedResult)
    Call<RepeatedResultResponseModel> getRepeatedResult(@Header("Accept-Language") String acceptLanguage,
                                                        @Header("Authorization") String authorization,
                                                        @Header("user_uuid") int user_uuid,
                                                        @Header("facility_uuid") int facility_uuid);

    @GET(GetIntervals)
    Call<RepeatedIntervalReponseModel> getIntervals(@Header("Accept-Language") String acceptLanguage,
                                                    @Header("Authorization") String authorization,
                                                    @Header("user_uuid") int user_uuid,
                                                    @Header("facility_uuid") int facility_uuid);


    @GET(GetDetailsbyTablename)
    Call<specimenResponseModel> getspecimenDetails(@Header("Accept-Language") String acceptLanguage,
                                                   @Header("Authorization") String authorization,
                                                   @Header("user_uuid") int user_uuid,
                                                   @Header("facility_uuid") int facility_uuid,
                                                   @Query("tableName") String tableName,
                                                   @Query("patientId") String patientId);

    @GET(GetDetailsbyTablename)
    Call<symptomResponseModel> getsymptomsDetails(@Header("Accept-Language") String acceptLanguage,
                                                  @Header("Authorization") String authorization,
                                                  @Header("user_uuid") int user_uuid,
                                                  @Header("facility_uuid") int facility_uuid,
                                                  @Query("tableName") String tableName,
                                                  @Query("patientId") String patientId);

    @GET(GetDetailsbyTablename)
    Call<CovidRegisterPatientResponseModel> getpatientDetails(@Header("Accept-Language") String acceptLanguage,
                                                              @Header("Authorization") String authorization,
                                                              @Header("user_uuid") int user_uuid,
                                                              @Header("facility_uuid") int facility_uuid,
                                                              @Query("tableName") String tableName,
                                                              @Query("patientId") String patientId);

    @GET(GetDetailsbyTablename)
    Call<ConditionDetailsResponseModel> getconditionDetails(@Header("Accept-Language") String acceptLanguage,
                                                            @Header("Authorization") String authorization,
                                                            @Header("user_uuid") int user_uuid,
                                                            @Header("facility_uuid") int facility_uuid,
                                                            @Query("tableName") String tableName,
                                                            @Query("patientId") String patientId);


    @POST(CovidQuickRegistrationSave)
    Call<QuickRegistrationSaveResponseModel> quickRegistrationSave(@Header("Accept-Language") String acceptLanguage,
                                                                   @Header("Authorization") String authorization,
                                                                   @Header("user_uuid") int user_uuid,
                                                                   @Header("facility_uuid") int facility_uuid,
                                                                   @Body QuickRegistrationRequestModel body);




    @POST(GetRoleBased)

    Call<ResponsePrivillageModule> getPrivilliageModule(@Header("Authorization") String authorization,
                                                         @Header("Accept-Language") String language,
                                                         @Header("user_uuid") int user_uuid,
                                                         @Header("facility_uuid") int facility_uuid,
                                                         @Body RequestBody body);

    @POST(GetRefrenceTestMethod)
    Call<ResponseTestMethod> getTestMethod(@Header("Authorization") String authorization,
                                                  @Header("user_uuid") int user_uuid,
                                                  @Header("facility_uuid") int facility_uuid,
                                                  @Body RequestBody body);

    @Streaming
    @POST(getSavedPDF)
    Call<ResponseBody> getPDF(@Header("Accept-Language") String acceptLanguage,
                              @Header("Authorization") String authorization,
                              @Header("user_uuid") int user_uuid,
                              @Header("facility_uuid") int facility_uuid,
                              @Body PDFRequestModel body);

    @POST(facilityLocation)
    Call<FacilityLocationResponseModel> getFacilityLocation(@Header("Accept-Language") String acceptLanguage,
                                                            @Header("Authorization") String authorization,
                                                            @Header("user_uuid") int user_uuid,
                                                            @Header("facility_uuid") int facility_uuid,
                                                            @Body RequestBody body);

    @POST(getLabName)
    Call<LabNameSearchResponseModel> getLabname(@Header("Accept-Language") String acceptLanguage,
                                                @Header("Authorization") String authorization,
                                                @Header("user_uuid") int user_uuid,
                                                @Header("facility_uuid") int facility_uuid,
                                                @Body RequestBody body);


    @POST(getLocationMaster)
    Call<LocationMasterResponseModel> getLocationMaster(@Header("Authorization") String authorization,
                                                        @Header("user_uuid") int user_uuid,
                                                        @Header("facility_uuid") int facility_uuid,
                                                        @Body RequestBody body);

    @POST(getBlockZone)
    Call<BlockZoneResponseModel> getBlockZone(@Header("Authorization") String authorization,
                                              @Header("user_uuid") int user_uuid,
                                              @Header("facility_uuid") int facility_uuid,
                                              @Body RequestBody body);

    //Search
    @POST(Register)
    Call<QuickSearchResponseModel> searchOutPatientcovid(@Header("Accept-Language") String acceptLanguage,
                                                         @Header("Authorization") String authorization,
                                                         @Header("user_uuid") int user_uuid,
                                                         @Header("facility_uuid") int facility_uuid,
                                                         @Body SearchPatientRequestModelCovid searchPatientRequestModel);


    @POST(orderProcessApprovel)
    Call<OrderApprovedResponseModel> orderApproved(@Header("Accept-Language") String acceptLanguage,
                                                           @Header("Authorization") String authorization,
                                                           @Header("user_uuid") int user_uuid,
                                                           @Header("facility_uuid") int facility_uuid,
                                                           @Body ApprovalRequestModel approvalRequestModel);

    @POST(GetToLocation)
    Call<LocationMasterResponseModelX> getLocation(@Header("Accept-Language") String acceptLanguage,
                                                   @Header("Authorization") String authorization,
                                                   @Header("user_uuid") int user_uuid,
                                                   @Header("facility_uuid") int facility_uuid,
                                                   @Body RequestBody body);

    @POST(getTestMaster)
    Call<GettestResponseModel> getTest(@Header("Authorization") String authorization,
                                       @Header("user_uuid") int user_uuid,
                                       @Header("facility_uuid") int facility_uuid,
                                       @Body RequestBody body);

    @POST(GetLabType)
    Call<GetReferenceResponseModel> getReference(@Header("Authorization") String authorization,
                                                 @Header("user_uuid") int user_uuid,
                                                 @Header("facility_uuid") int facility_uuid,
                                                 @Body RequestBody labtype);



    @POST(saveOrder)
    Call<SaveOrderResponseModel> saveOrder(@Header("Authorization") String authorization,
                                           @Header("user_uuid") int user_uuid,
                                           @Header("facility_uuid") int facility_uuid,
                                           @Body SaveOrderRequestModel saveOrderRequestModel);

    @PUT(updateQuickRegistration)
    Call<QuickRegistrationUpdateResponseModel> updateQuickRegistration(@Header("Accept-Language") String acceptLanguage,
                                                                       @Header("Authorization") String authorization,
                                                                       @Header("user_uuid") int user_uuid,
                                                                       @Header("facility_uuid") int facility_uuid,

                                                                       @Body QuickRegistrationUpdateRequestModel body);


    @POST(getLAbNameinList)
    Call<GetLabNameListResponseModel> getLabNameinList(@Header("Accept-Language") String acceptLanguage,
                                                     @Header("Authorization") String authorization,
                                                     @Header("user_uuid") int user_uuid,
                                                     @Header("facility_uuid") int facility_uuid,
                                                     @Body RequestBody body);

    @GET(getApplicationRules)
    Call<GetApplicationRulesResponseModel> getApplicationRules(@Header("Accept-Language") String acceptLanguage,
                                                               @Header("Authorization") String authorization,
                                                               @Header("user_uuid") int user_uuid,
                                                               @Header("facility_uuid") int facility_uuid);


    @POST(getLabTestList)
    Call<LabTestResponseModel> getLabTestList(@Header("Accept-Language") String acceptLanguage,
                                              @Header("Authorization") String authorization,
                                              @Header("user_uuid") int user_uuid,
                                              @Header("facility_uuid") int facility_uuid,
                                              @Header("isMobileApi") boolean value,
                                              @Body LabTestRequestModel body);

    @POST(rapidSave)
    Call<SimpleResponseModel> rapidSave(@Header("Accept-Language") String acceptLanguage,
                                        @Header("Authorization") String authorization,
                                        @Header("user_uuid") int user_uuid,
                                        @Header("facility_uuid") int facility_uuid,
                                        @Body LabrapidSaveRequestModel body);

    @POST(sampleRecived)
    Call<SimpleResponseModel> sampleRecived(@Header("Accept-Language") String acceptLanguage,
                                        @Header("Authorization") String authorization,
                                        @Header("user_uuid") int user_uuid,
                                        @Header("facility_uuid") int facility_uuid,
                                        @Body sampleTransportRequestModel body);

    @POST(orderSendtonext)
    Call<SimpleResponseModel> orderSendtonext(@Header("Accept-Language") String acceptLanguage,
                                        @Header("Authorization") String authorization,
                                        @Header("user_uuid") int user_uuid,
                                        @Header("facility_uuid") int facility_uuid,
                                        @Body AssigntootherRequest body);

    @POST(getLabTestApproval)
    Call<LabTestApprovalResponseModel> getLabTestApproval(@Header("Accept-Language") String acceptLanguage,
                                                          @Header("Authorization") String authorization,
                                                          @Header("user_uuid") int user_uuid,
                                                          @Header("facility_uuid") int facility_uuid,
                                                          @Header("isMobileApi") boolean value,
                                                          @Body LabTestApprovalRequestModel body);


        @POST(orderDetailsGet)
        Call<OrderProcessDetailsResponseModel> orderDetailsGet(@Header("Accept-Language") String acceptLanguage,
                                                               @Header("Authorization") String authorization,
                                                               @Header("user_uuid") int user_uuid,
                                                               @Header("facility_uuid") int facility_uuid,
                                                           @Body Req body);
    @POST(orderDetailsGetLabApproval)
    Call<LabApprovalResultResponse> orderDetailsGetLabApproval(@Header("Accept-Language") String acceptLanguage,
                                                    @Header("Authorization") String authorization,
                                                    @Header("user_uuid") int user_uuid,
                                                    @Header("facility_uuid") int facility_uuid,
                                                    @Body com.hmisdoctor.ui.quick_reg.model.labapprovalresult.Req body);
    @POST(rejectData)
    Call<SimpleResponseModel> rejectTestLab(@Header("Accept-Language") String acceptLanguage,
                                           @Header("Authorization") String authorization,
                                           @Header("user_uuid") int user_uuid,
                                           @Header("facility_uuid") int facility_uuid,
                                           @Body RejectRequestModel body);

    @POST(sendApprovel)
    Call<SimpleResponseModel> sendApprovel(@Header("Accept-Language") String acceptLanguage,
                                                @Header("Authorization") String authorization,
                                                @Header("user_uuid") int user_uuid,
                                                @Header("facility_uuid") int facility_uuid,
                                                @Body SendApprovalRequestModel body);


    @POST(orderProcess)
    Call<OrderProcessResponseModel> orderProcess(@Header("Accept-Language") String acceptLanguage,
                                                 @Header("Authorization") String authorization,
                                                 @Header("user_uuid") int user_uuid,
                                                 @Header("facility_uuid") int facility_uuid,
                                                 @Body OrderToProcessReqestModel body);

    //TestProcessResponseModel
    @POST(getLabTestApproval)
    Call<TestProcessResponseModel> getLabTestProcess(@Header("Accept-Language") String acceptLanguage,
                                                      @Header("Authorization") String authorization,
                                                      @Header("user_uuid") int user_uuid,
                                                      @Header("facility_uuid") int facility_uuid,
                                                      @Header("isMobileApi") boolean value,
                                                      @Body TestProcessRequestModel body);

    @POST(getRejectReference)
    Call<RejectReferenceResponseModel> getRejectReference(@Header("Accept-Language") String acceptLanguage,
                                                          @Header("Authorization") String authorization,
                                                          @Header("user_uuid") int user_uuid,
                                                          @Header("facility_uuid") int facility_uuid,
                                                          @Body RequestBody body);


    @POST(getUserProfile)
    Call<UserProfileResponseModel> getUserProfile(@Header("Accept-Language") String acceptLanguage,
                                                  @Header("Authorization") String authorization,
                                                  @Header("user_uuid") int user_uuid,
                                                  @Header("facility_uuid") int facility_uuid,
                                                  @Body RequestBody body);



    @POST(getSampleAcceptance)
    Call<SampleAcceptanceResponseModel> getSampleAccept(@Header("Accept-Language") String acceptLanguage,
                                                        @Header("Authorization") String authorization,
                                                        @Header("user_uuid") int user_uuid,
                                                        @Header("facility_uuid") int facility_uuid,
                                                        @Header("isMobileApi") boolean value,
                                                        @Body RequestBody body);

    @POST(GetLabSearchResult)
    Call<LabTestSpinnerResponseModel> getLabTestSpinner(@Header("Accept-Language") String acceptLanguage,
                                                        @Header("Authorization") String authorization,
                                                        @Header("user_uuid") int user_uuid,
                                                        @Header("facility_uuid") int facility_uuid,
                                                        @Header("isMobileApi") boolean value,
                                                        @Body RequestBody body);

    @POST(getAssignedSpinnerList)
    Call<LabAssignedToResponseModel> getLabAssignedToSpinner(@Header("Accept-Language") String acceptLanguage,
                                                       @Header("Authorization") String authorization,
                                                       @Header("user_uuid") int user_uuid,
                                                       @Header("facility_uuid") int facility_uuid,
                                                       @Header("isMobileApi") boolean value,
                                                       @Body RequestBody body);

    @POST(getApprovalResultSpinner)
    Call<LabApprovalSpinnerResponseModel> getApprovalResultSpinner(@Header("Authorization") String authorization,
                                                                   @Header("user_uuid") int user_uuid,
                                                                   @Header("facility_uuid") int facility_uuid,
                                                                   @Header("Accept-Language") String acceptLanguage,
                                                                   @Body RequestBody body);
    @GET(GetDocumentType)
    Call<DocumentTypeResponseModel> getDocumentType(@Header("Accept-Language") String acceptLanguage,
                                                    @Header("Authorization") String authorization,
                                                    @Header("user_uuid") int user_uuid,
                                                    @Header("facility_uuid") int facility_uuid);
    @GET(GetAddDocumentType)
    Call<AddDocumentDetailsResponseModel> getAddDocumentDetails(@Header("Accept-Language") String acceptLanguage,
                                                                @Header("Authorization") String authorization,
                                                                @Header("user_uuid") int user_uuid,
                                                                @Header("facility_uuid") int facility_uuid);

    @Multipart
    @POST(GetUploadFile)
    Call<FileUploadResponseModel> getUploadFile(@Header("Accept-Language") String acceptLanguage,
                                                @Header("Authorization") String authorization,
                                                @Header("user_uuid") int user_uuid,
                                                @Header("facility_uuid") int facility_uuid,
                                                @Part MultipartBody.Part file,
                                                @Part ("folder_name") RequestBody foldername,
                                                @Part ("attached_date") RequestBody multipartattachedDate,
                                                @Part ("patient_uuid") RequestBody patientuuid,
                                                @Part ("encounter_uuid") RequestBody encounteruuid,
                                                @Part ("attachment_type_uuid") RequestBody attachmenttypeuuid,
                                                @Part ("comments") RequestBody comments,
                                                @Part ("attachment_name") RequestBody attachmentname);
    @PUT(DeleteAttachmentsRows)
    Call<DeleteDocumentResponseModel> deleteAttachmentsRows(@Header("Accept-Language") String acceptLanguage,
                                                @Header("Authorization") String authorization,
                                                 @Header("user_uuid") int user_uuid,
                                                 @Header("facility_uuid") int facility_uuid,
                                                 @Query("attachment_uuid") int attachementid);





    @PUT(updateHistoryDiagnosis)
    Call<HistoryDiagnosisUpdateResponseModel> updateDiagnosis(@Header("Authorization") String authorization,
                                                              @Header("user_uuid") int user_uuid,
                                                              @Header("facility_uuid") int facility_uuid,
                                                              @Query("patient_diagnosis_id") int patient_diagnosis_id,
                                                              @Query("patient_uuid") int patient_uuid,
                                                              @Query("department_uuid") int department_uuid,
                                                              @Body RequestBody body);

    @POST(GetAdmissionWardList)
    Call<AdmissionWardResponseModel> getAdmissionWardList(@Header("Accept-Language") String acceptLanguage, @Header("Authorization") String authorization,
                                                          @Header("user_uuid") int user_uuid,
                                                          @Header("facility_uuid") int facility_uuid,
                                                          @Body RequestBody body);

    @GET(GetReason)
    Call<ReasonResponseModel> getReason(@Header("Accept-Language") String acceptLanguage,
                                                @Header("Authorization") String authorization,
                                                @Header("user_uuid") int user_uuid,
                                                @Header("facility_uuid") int facility_uuid);

    @POST(GetNextOrder)
    Call<RefferaNextResponseModel> nextOrder(@Header("Authorization") String authorization,
                                             @Header("user_uuid") int user_uuid,
                                             @Header("facility_uuid") int facility_uuid,
                                             @Body RefferalNextRequestModel refferalNextRequestModel);





    @Streaming
    @POST(GetDownload)
    Call<ResponseBody> getDownload(@Header("Accept-Language") String acceptLanguage,
                              @Header("Authorization") String authorization,
                              @Header("user_uuid") int user_uuid,
                              @Header("facility_uuid") int facility_uuid,
                              @Body RequestBody body);

    @POST(GetNoteTemplate)
    Call<TemplateResponseModel> getNoteTEmplate(@Header("Accept-Language") String acceptLanguage,
                                                @Header("Authorization") String authorization,
                                                @Header("user_uuid") int user_uuid,
                                                @Header("facility_uuid") int facility_uuid);

    @POST(GetTemplateItem)
    Call<TemplateItemResponseModel> getItemTemplate(@Header("Accept-Language") String acceptLanguage,
                                                @Header("Authorization") String authorization,
                                                @Header("user_uuid") int user_uuid,
                                                @Header("facility_uuid") int facility_uuid,
                                                @Body RequestBody body);

    @POST(GetSaveScertificate)
    Call<CertificateResponseModel> saveCertificate(@Header("Accept-Language") String acceptLanguage,
            @Header("Authorization") String authorization,
                                                   @Header("user_uuid") int user_uuid,
                                                   @Header("facility_uuid") int facility_uuid,
                                                   @Body CertificateRequestModel certificateRequestModel);

    @GET(GetCertificateAll)
    Call<GetCertificateResponseModel> getCertificateAll(@Header("Accept-Language") String acceptLanguage,
                                                        @Header("Authorization") String authorization,
                                                        @Header("user_uuid") int user_uuid,
                                                        @Header("facility_uuid") int facility_uuid,
                                                        @Query("patient_uuid") int patientId);






    @POST(getAllBloodRequestOrPurpose)
    Call<GetAllBloodGroupResp> getAllBloodGroup(@Header("Accept-Language") String acceptLanguage,
                                                @Header("Authorization") String authorization,
                                                @Header("user_uuid") int user_uuid,
                                                @Header("facility_uuid") int facility_uuid,
                                                @Body GetAllBloodGroupReq getAllBloodGroupReq);

    @POST(getAllBloodRequestOrPurpose)
    Call<GetAllPurposeResp> getAllPurpose(@Header("Accept-Language") String acceptLanguage,
                                          @Header("Authorization") String authorization,
                                          @Header("user_uuid") int user_uuid,
                                          @Header("facility_uuid") int facility_uuid,
                                          @Body GetAllPurposeReq getAllPurposeReq);

    @POST(getPreviousBloodRequest)
    Call<GetPreviousBloodResp> getPreviousBlood(@Header("Accept-Language") String acceptLanguage,
                                                @Header("Authorization") String authorization,
                                                @Header("user_uuid") int user_uuid,
                                                @Header("facility_uuid") int facility_uuid,
                                                @Body GetPreviousBloodReq getPreviousBloodReq);
}





