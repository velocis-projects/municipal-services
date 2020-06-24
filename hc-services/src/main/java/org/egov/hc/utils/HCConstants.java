package org.egov.hc.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;


@Component
public class HCConstants {
	
	private HCConstants() {}

	public static final String SERV_REQ_ID_NAME = "hc.servicerequestid";
	public static final String SERV_REQ_ID_FORMAT = "[cy:dd]/[cy:MM]/[cy:yyyy]/[SEQ_EG_HC_SERVICEREQUESTID]";
	
	public static final String TENANT_ID = "ch.chandigarh";
	public static final String TENANT_ID_CITIZEN = "ch";
	public static final String CITIZEN = "CITIZEN";
	
	
	public static final String TRUE = "true";
	
	//Scheduler
	
	public static final String OVERDAYS = "OVERDAYS";
	
	public static final String REMINDER = "REMINDER";
	
	//Roll
	
	public static final String ROLE = "EE";
	public static final String EXECUTIVE_ENGINEER = "EE";
	public static final String JUNIER_ENGINEERE = "JE";
	public static final String SDO = "SDO";
	
	// Actions
	
	public static final String SUBMITTED = "SUBMITTED";
	public static final String VERIFY_AND_FORWARD = "VERIFY AND FORWARD";
	public static final String VERIFY_AND_FORWARD_TO_SDO = "VERIFY AND FORWARD TO SDO";
	public static final String FORWARD_FOR_INSPECTION = "FORWARD FOR INSPECTION";
	public static final String EDIT = "EDIT";
	public static final String REJECT = "REJECT";
	public static final String INSPECT = "INSPECT";
	public static final String APPROVE = "APPROVE";
	public static final String REQUEST_CLARIFICATION = "REQUEST CLARIFICATION";
	public static final String COMPLETE = "COMPLETE";
	public static final String INITIATE = "INITIATE";
	public static final String ACTION_ASSIGN = "ASSIGN";
	
	//comment
	public static final String COMMENT = "your service has been rejected as service request type has been changed.";
	
	//Service Request STatus
	public static final String INITIATED_STATUS = "INITIATED";
	public static final String REJECTED_STATUS = "REJECTED";
	public static final String APPROVED_STATUS = "APPROVED";
	public static final String COMPLETED_STATUS = "COMPLETED";
	public static final String ASSIGNED_STATUS = "ASSIGNED";
	
	public static final String INSPECTED_STATUS = "INSPECTED";
	public static final String VERIFYANDFORWARD = "VERIFYANDFORWARD";
	public static final String REVIEW = "REVIEW";
	public static final String REJECTED = "REJECTED";
	
	//Notification type
	public static final String SMS = "sms";
	public static final String EMAIL = "email";
	public static final String PUSH = "push";

	//Device_source_detail
	
	public static final String DEVICE_HORTICULTURE = "raise service request";
	public static final String MODULE_CODE = "HC";
	
	// Massage
	
	public static final String SUCCESS = "success";
	public static final String MEDIA = "media";
	public static final String RAISE_SEVICE_REQUEST = "Raise Service Request";
	public static  final String businessService_BPA = "BPAREG";
	public static  final String businessService_HC = "HORTICULTURE";
	public static final String TRIGGER_NOWORKFLOW  = "NOWORKFLOW";
	
	public static final String ACTION_UPDATE  = "UPDATE";
	
		
	//Notification
	public static final String TEMPLATE_COMPLAINT_EMAIL = "./src/main/resources/email-templates/velocityEmailNotifSample.vm";
	
	public static final String SEARCHER_HC_MOD_NAME = "rainmaker-hc-V2";
	public static final String SEARCHER_SRSEARCH_DEF_NAME = "serviceSearchWithDetails";
	public static final String SEARCHER_PLAINSEARCH_DEF_NAME = "plainSearch";
	public static final String SEARCHER_COUNT_DEF_NAME = "count";
	public static final String PG_JSONPATH_COUNT = "$.count[0].count";
	public static final String SEARCHER_SRID_ASSIGNEDTO_DEF_NAME = "getServiceRequestsOnAssignedTo";
	public static final String SRID_ASSIGNEDTO_JSONPATH = "$.servicesRequestIds.*.businesskey";
	public static final String MDMS_HC_MOD_NAME = "rainmaker-hc";
	public static final String MDMS_SERVICETYPE_MASTER_NAME = "ServiceDefs";
	public static final String MDMS_COMMON_MASTERS_MODULE_NAME = "common-masters";
	public static final String MDMS_DEPT_MASTERS_MASTER_NAME = "Department";
	public static final String MDMS_DESIGNATION_MASTERS_MASTER_NAME = "Designation";
	public static final String LOCALIZATION_MODULE_NAME = "rainmaker-hc";
	

	public static final String LOCALIZATION_CODE_SUBMIT_CITIZEN = "hc.sms.notification.submit.citizen";
	
	public static final String LOCALIZATION_CODE_REOPEN_CITIZEN = "hc.sms.notification.reopen.citizen";
	public static final String LOCALIZATION_CODE_REOPEN_EMPLOYEE = "hc.sms.notification.reopen.employee";

	public static final String LOCALIZATION_CODE_ASSIGN_CITIZEN = "hc.sms.notification.assign.citizen";
	public static final String LOCALIZATION_CODE_ASSIGN_EMPLOYEE = "hc.sms.notification.assign.employee";

	public static final String LOCALIZATION_CODE_REASSIGN_CITIZEN = "hc.sms.notification.reassign.citizen";
	public static final String LOCALIZATION_CODE_REASSIGN_EMPLOYEE = "hc.sms.notification.reassign.employee";
	
	public static final String LOCALIZATION_CODE_REJECT_CITIZEN = "hc.sms.notification.reject.citizen";
	
	public static final String LOCALIZATION_CODE_RESOLVE_CITIZEN = "hc.sms.notification.resolve.citizen";
	
	public static final String LOCALIZATION_CODE_CLOSE_EMPLOYEE = "hc.sms.notification.close.employee";
	
	
	
	
	public static final String LOCALIZATION_CODE_COMMENT = "hc.sms.notification.comment";
	public static final String LOCALIZATION_CODE_DEFAULT = "hc.sms.notification.default";
	public static final String LOCALIZATION_CODE_COMMENT_DEFAULT = "hc.sms.notification.comment.default";
	public static final String LOCALIZATION_COMP_CATEGORY_PREFIX = "hc.servicerequest.category.";
	public static final String LOCALIZATION_CODE_SERVICE_REQUEST = "hc.event.notification.service.request";

	public static final String SERVICE_CODES = "serviceCode";
	public static final String JSONPATH_SERVICEDEFS = "$.MdmsRes.RAINMAKER-HC.ServiceDefs";
	public static final String JSONPATH_SERVICE_CODES = "$.MdmsRes.RAINMAKER-HC.ServiceDefs.*.serviceCode";
	public static final String JSONPATH_SLA = "$.MdmsRes.RAINMAKER-HC.ServiceDefs.*.slaHours";
	public static final String JSONPATH_DEPARTMENTS = "$.MdmsRes.common-masters.Department";
	public static final String JSONPATH_DESIGNATIONS = "$.MdmsRes.common-masters.Designation";

	public static final String SERVICE_NAME = "serviceName";
	public static final String DEFAULT_COMPLAINT_TYPE = "resolution";
	public static final String EMPLOYEE_DEPTCODES_JSONPATH = "$.Employees[0].assignments.*.department";
	public static final String EMPLOYEE_DEPTCODE_JSONPATH = "$.Employees.[0].assignments.[?(@.isCurrentAssignment == true)].department";
	public static final String EMPLOYEE_DESGCODE_JSONPATH = "$.Employees.[0].assignments.[?(@.isCurrentAssignment == true)].designation";
	public static final String EMPLOYEE_NAME_JSONPATH = "$.Employees[0].user.name";
	public static final String EMPLOYEE_PHNO_JSONPATH = "$.Employees[0].user.mobileNumber"; 
	public static final String EMPLOYEE_EMAIL_JSONPATH = "$.Employees[0].user.email";
	public static final String EMPLOYEE_EMAILID_JSONPATH = "$.Employees[0].user.emailId";
	
	public static final String EMPLOYEE_TENANTID_JSONPATH = "$.Employees[0].tenantId";
	public static final String EMPLOYEE_BASE_JSONPATH = "$.Employees";
	public static final String DEPARTMENTNAME_EMPLOYEE_JSONPATH = "$.Department[0].name";
	
	public static final String LOCALIZATION_CODES_JSONPATH = "$.messages.*.code";
	public static final String LOCALIZATION_MSGS_JSONPATH = "$.messages.*.message";
	
	public static final String LOCATION__BOUNDARY_NAMES_JSONPATH = "$.TenantBoundary.*.boundary.*.name";
	public static final String LOCATION__BOUNDARY_CODES_JSONPATH = "$.TenantBoundary.*.boundary.*.code";
	public static final String LOCATION__BOUNDARY_HIERARCHYTYPE_ADMIN = "ADMIN";
	public static final String LOCATION__BOUNDARY_BOUNDARYTYPE_LOCALITY = "Locality";

	
	public static final String SEARCHER_RESPONSE_TEXT = "Searcher response : ";
	
	//SMS
	
	public static final String HC_CITIZEN_REQUEST_SMS_NOTIFICATION="HC_CITIZEN_REQUEST_SMS_NOTIFICATION";
	public static final String HC_CITIZEN_REJECTSERVICEREQUEST_SMS_NOTIFICATION="HC_CITIZEN_REJECTSERVICEREQUEST_SMS_NOTIFICATION";
	public static final String HC_CITIZEN_SERVICEREQUESTCOMPLETED_SMS_NOTIFICATION="HC_CITIZEN_SERVICEREQUESTCOMPLETED_SMS_NOTIFICATION";
	public static final String HC_EMPLOYEE_VERIFYANDFORWARD_SMS_NOTIFICATION="HC_EMPLOYEE_VERIFYANDFORWARD_SMS_NOTIFICATION";
	public static final String HC_CITIZEN_REQUEST_INITIATE_SMS_NOTIFICATION="HC_CITIZEN_REQUEST_INITIATE_SMS_NOTIFICATION";
	public static final String HC_CITIZEN_REQUEST_UPDATE_SMS_NOTIFICATION="HC_CITIZEN_REQUEST_UPDATE_SMS_NOTIFICATION";
	public static final String HC_EMPLOYEE_SERVICEREQUEST_SMS_NOTIFICATION="HC_EMPLOYEE_SERVICEREQUEST_SMS_NOTIFICATION";
	public static final String HC_EMPLOYEE_ACTION_APPROVE_SMS_NOTIFICATION="HC_EMPLOYEE_ACTION_APPROVE_SMS_NOTIFICATION";
	public static final String HC_EMPLOYEE_ACTION_ASSIGN_SMS_NOTIFICATION="HC_EMPLOYEE_ACTION_ASSIGN_SMS_NOTIFICATION";
	public static final String HC_EMPLOYEE_REQUEST_SMS_NOTIFICATION="HC_EMPLOYEE_REQUEST_SMS_NOTIFICATION";
	public static final String HC_EMPLOYEE_FORWARD_TO_SDO_SMS_NOTIFICATION="HC_EMPLOYEE_FORWARD_TO_SDO_SMS_NOTIFICATION";
	public static final String HC_EMPLOYEE_INSPECTION_SMS_NOTIFICATION="HC_EMPLOYEE_INSPECTION_SMS_NOTIFICATION";
	public static final String HC_EMPLOYEE_CLARIFICATION_SMS_NOTIFICATION="HC_EMPLOYEE_CLARIFICATION_SMS_NOTIFICATION";
	
	
	//EMAIL
	public static final String HC_CITIZEN_REQUEST_EMAIL_NOTIFICATION="HC_CITIZEN_REQUEST_EMAIL_NOTIFICATION";
	public static final String HC_CITIZEN_REJECTSERVICEREQUEST_EMAIL_NOTIFICATION="HC_CITIZEN_REJECTSERVICEREQUEST_EMAIL_NOTIFICATION";
	public static final String HC_CITIZEN_SERVICEREQUESTCOMPLETED_EMAIL_NOTIFICATION="HC_CITIZEN_SERVICEREQUESTCOMPLETED_EMAIL_NOTIFICATION";
	public static final String HC_CITIZEN_REQUEST_INITIATE_EMAIL_NOTIFICATION="HC_CITIZEN_REQUEST_INITIATE_EMAIL_NOTIFICATION";
	public static final String HC_CITIZEN_REQUEST_UPDATE_EMAIL_NOTIFICATION="HC_CITIZEN_REQUEST_UPDATE_EMAIL_NOTIFICATION";
	public static final String HC_EMPLOYEE_REQUEST_EMAIL_NOTIFICATION="HC_EMPLOYEE_REQUEST_EMAIL_NOTIFICATION";
	public static final String HC_EMPLOYEE_ACTION_APPROVE_EMAIL_NOTIFICATION="HC_EMPLOYEE_ACTION_APPROVE_EMAIL_NOTIFICATION";
	public static final String HC_EMPLOYEE_ACTION_ASSIGN_EMAIL_NOTIFICATION="HC_EMPLOYEE_ACTION_ASSIGN_EMAIL_NOTIFICATION";
	public static final String HC_EMPLOYEE_FORWARD_TO_SDO_EMAIL_NOTIFICATION="HC_EMPLOYEE_FORWARD_TO_SDO_EMAIL_NOTIFICATION";
	public static final String HC_EMPLOYEE_INSPECTION_EMAIL_NOTIFICATION="HC_EMPLOYEE_INSPECTION_EMAIL_NOTIFICATION";
	public static final String HC_EMPLOYEE_CLARIFICATION_EMAIL_NOTIFICATION="HC_EMPLOYEE_CLARIFICATION_EMAIL_NOTIFICATION";
	
	//EMAIL SUBJECT
	public static final String HC_CITIZEN_REQUEST_EMAIL_SUBJECT_ACTION_ASSIGN="HC_CITIZEN_REQUEST_EMAIL_SUBJECT_ACTION_ASSIGN";
	public static final String HC_CITIZEN_REQUEST_EMAIL_SUBJECT_ACTION_REJECT="HC_CITIZEN_REQUEST_EMAIL_SUBJECT_ACTION_REJECT";
	public static final String HC_CITIZEN_REQUEST_EMAIL_SUBJECT_ACTION_COMPLETE="HC_CITIZEN_REQUEST_EMAIL_SUBJECT_ACTION_COMPLETE";
	public static final String HC_CITIZEN_REQUEST_EMAIL_SUBJECT_ACTION_OPEN="HC_CITIZEN_REQUEST_EMAIL_SUBJECT_ACTION_OPEN";
	public static final String HC_EMPLOYEE_ACTION_APPROVE_EMAIL_SUBJECT = "HC_EMPLOYEE_ACTION_APPROVE_EMAIL_SUBJECT";
	public static final String HC_EMPLOYEE_ACTION_ASSIGN_EMAIL_SUBJECT = "HC_EMPLOYEE_ACTION_ASSIGN_EMAIL_SUBJECT";
	public static final String HC_EMPLOYEE_FORWARD_TO_SDO_EMAIL_SUBJECT = "HC_EMPLOYEE_FORWARD_TO_SDO_EMAIL_SUBJECT";
	public static final String HC_EMPLOYEE_INSPECTION_EMAIL_SUBJECT = "HC_EMPLOYEE_INSPECTION_EMAIL_SUBJECT";
	public static final String HC_EMPLOYEE_CLARIFICATION_EMAIL_SUBJECT = "HC_EMPLOYEE_CLARIFICATION_EMAIL_SUBJECT";
	public static final String HC_CITIZEN_REQUEST_EMAIL_SUBJECT_ACTION_UPDATE = "HC_CITIZEN_REQUEST_EMAIL_SUBJECT_ACTION_UPDATE";
	public static final String HC_EMPLOYEE_REQUEST_EMAIL_SUBJECT="HC_EMPLOYEE_REQUEST_EMAIL_SUBJECT";
	
	
	//PUSH
	public static final String HC_EMPLOYEE_SERVICEREQUEST_PUSH_NOTIFICATION="HC_EMPLOYEE_SERVICEREQUEST_PUSH_NOTIFICATION";
	public static final String HC_EMPLOYEE_SERVICEREQUEST_EMAIL_NOTIFICATION="HC_EMPLOYEE_SERVICEREQUEST_EMAIL_NOTIFICATION";
	public static final String HC_CITIZEN_REJECTSERVICEREQUEST_PUSH_NOTIFICATION="HC_CITIZEN_REJECTSERVICEREQUEST_PUSH_NOTIFICATION";
	public static final String HC_CITIZEN_SERVICEREQUESTCOMPLETED_PUSH_NOTIFICATION="HC_CITIZEN_SERVICEREQUESTCOMPLETED_PUSH_NOTIFICATION";
	public static final String HC_EMPLOYEE_REQUEST_PUSH_NOTIFICATION="HC_EMPLOYEE_REQUEST_PUSH_NOTIFICATION";
	public static final String HC_CITIZEN_REQUEST_UPDATE_PUSH_NOTIFICATION="HC_CITIZEN_REQUEST_UPDATE_PUSH_NOTIFICATION";
	
	public static final String HC_CITIZEN_REQUEST_PUSH_NOTIFICATION="HC_CITIZEN_REQUEST_PUSH_NOTIFICATION";
	public static final String HC_CITIZEN_REQUEST_INITIATE_PUSH_NOTIFICATION="HC_CITIZEN_REQUEST_INITIATE_PUSH_NOTIFICATION";
	public static final String HC_EMPLOYEE_ACTION_APPROVE_PUSH_NOTIFICATION="HC_EMPLOYEE_ACTION_APPROVE_PUSH_NOTIFICATION";
	public static final String HC_EMPLOYEE_ACTION_ASSIGN_PUSH_NOTIFICATION="HC_EMPLOYEE_ACTION_ASSIGN_PUSH_NOTIFICATION";
	public static final String HC_EMPLOYEE_FORWARD_TO_SDO_PUSH_NOTIFICATION="HC_EMPLOYEE_FORWARD_TO_SDO_PUSH_NOTIFICATION";
	public static final String HC_EMPLOYEE_INSPECTION_PUSH_NOTIFICATION="HC_EMPLOYEE_INSPECTION_PUSH_NOTIFICATION";
	public static final String HC_EMPLOYEE_CLARIFICATION_PUSH_NOTIFICATION="HC_EMPLOYEE_CLARIFICATION_PUSH_NOTIFICATION";
	
	//REMAINSER/OVERDAYS NOTIFICATION
	
	public static final String HC_EMPLOYEE_REMAINDER_SMS_NOTIFICATION="HC_EMPLOYEE_REMAINDER_SMS_NOTIFICATION";
	public static final String HC_EMPLOYEE_OVERDAYS_SMS_NOTIFICATION="HC_EMPLOYEE_OVERDAYS_SMS_NOTIFICATION";
	public static final String HC_EMPLOYEE_OVERDAYS_EMAIL_NOTIFICATION="HC_EMPLOYEE_OVERDAYS_EMAIL_NOTIFICATION";
	public static final String HC_EMPLOYEE_REMAINDER_EMAIL_NOTIFICATION="HC_EMPLOYEE_REMAINDER_EMAIL_NOTIFICATION";
	public static final String HC_EMPLOYEE_OVERDAYS_PUSH_NOTIFICATION="HC_EMPLOYEE_OVERDAYS_PUSH_NOTIFICATION";
	public static final String HC_EMPLOYEE_REMAINDER_PUSH_NOTIFICATION="HC_EMPLOYEE_REMAINDER_PUSH_NOTIFICATION";
	
	//REMAINSER/OVERDAYS EMAIL SUBJECT 
	
	public static final String HC_EMPLOYEE_REMAINDER_EMAIL_SUBJECT="HC_EMPLOYEE_REMAINDER_EMAIL_SUBJECT";
	public static final String HC_EMPLOYEE_OVERDAYS_EMAIL_SUBJECT="HC_EMPLOYEE_OVERDAYS_EMAIL_SUBJECT";
	
	
	
	//approve, request for clarification
	
	public static final String SMS_NOTIFICATION_STATUS_KEY = "<status>";
	public static final String SMS_NOTIFICATION_COMPLAINT_TYPE_KEY = "<complaint_type>";
	public static final String SMS_NOTIFICATION_DATE_KEY = "<date>";
	public static final String SMS_NOTIFICATION_ID_KEY = "<id>";
	public static final String SMS_NOTIFICATION_EMP_NAME_KEY = "<emp_name>";
	public static final String SMS_NOTIFICATION_USER_NAME_KEY = "<citizen_name>";
	public static final String SMS_NOTIFICATION_SERVICEREQUEST_ID = "<service_request_id>";
	public static final String SMS_NOTIFICATION_SERVICEREQUEST_TYPE = "<service_request_type>";
	public static final String NOTIFICATION_SERVICEREQUEST_ID_NEW = "<service_request_id_new>";
	
		
	public static final String SMS_NOTIFICATION_SERVICEREQUEST_DATE_KEY = "<service_request_date>";
	public static final String  USREVENTS_EVENT_TYPE = "SYSTEMGENERATED";
	public static final String  USREVENTS_EVENT_NAME = "Horticulture Service Request";
	public static final String  USREVENTS_EVENT_POSTEDBY = "SYSTEM-HC";
	public static final String SMS_NOTIFICATION_SERVICEREQUEST_DAYS_KEY = "<days>";

	
	
	/*  search on roles constant */

	public static final String ROLE_EMPLOYEE = "EMPLOYEE";
	public static final String ROLE_CITIZEN = "CITIZEN";
	public static final String ROLE_GRO = "GRO";
	public static final String ROLE_DGRO = "DGRO";
	public static final String ROLE_CSR = "CSR";
	
	
	public static final String ROLE_NAME_CITIZEN = "Citizen";
	public static final String ROLE_NAME_EMPLOYEE = "Employee";
	public static final String ROLE_NAME_GRO = "Grievance Routing Officer";
	public static final String ROLE_NAME_DGRO = "Department Grievance Routing Officer";
	public static final String ROLE_NAME_CSR = "Customer Support Representative";
	public static final String SUBJECT = "SERVICE REQUEST FORM";
		
}