package org.egov.hc.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.hc.consumer.HCNotificationConsumer;
import org.egov.hc.contract.Action;
import org.egov.hc.contract.ActionItem;
import org.egov.hc.contract.EmailRequest;
import org.egov.hc.contract.Event;
import org.egov.hc.contract.EventRequest;
import org.egov.hc.contract.Recepient;
import org.egov.hc.contract.RequestInfoWrapper;
import org.egov.hc.contract.SMSRequest;
import org.egov.hc.contract.ServiceRequest;
import org.egov.hc.model.ActionInfo;
import org.egov.hc.model.ServiceRequestData;

import org.egov.hc.producer.HCConfiguration;
import org.egov.hc.producer.HCProducer;
import org.egov.hc.repository.ServiceRepository;
import org.egov.hc.utils.HCConstants;
import org.egov.hc.utils.WorkFlowConfigs;
import org.egov.mdms.model.ModuleDetail;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@EnableAsync
@Component
public class ServiceRequestSchecduler {

	
	@Autowired
	private HCConfiguration hcConfiguration; 
	
	@Autowired
    private RestTemplate rest;
	
    @Autowired
    private HCConfiguration config;
	
	@Autowired
    private HCProducer hCProducer;
	
	@Autowired
	private HCNotificationConsumer hcNotificationConsumer;
	
	@Autowired
	private ServiceRepository serviceRepository;
	
	
	@Async	
	@Scheduled(cron = "0 23 * * * *")

	public void scheduleTask() {
		System.out.println("Scheduler started");
		
		
		String role = null;

		String serviceRequestId = null;
		Long serviceRequestDateEpoc = null;
				
	//get data from service request
 		List <ServiceRequestData> serviceRequestList = getServiceRequest();
 		
 		//generate current date
 		String currentDate = getCurrentDateTimeMS();
 		long currentDateepoch=0 ;
 		try
		{
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		Date date = df.parse(currentDate);
		currentDateepoch = date.getTime();	
			
		 System.out.println("service current date in mili  :  "+currentDate);
		  System.out.println("currentDate in Epoc convert  "+ currentDateepoch);
		}
 		catch(Exception ex)
 		{
 			
 		}
		
 		if(null !=serviceRequestList || serviceRequestList.size()>0)
 		{
 		for(ServiceRequestData request: serviceRequestList)
 		{
			role = request.getCurrent_assignee();
			serviceRequestId = request.getService_request_id();
			serviceRequestDateEpoc = request.getCreatedTime();

	//convert in epoc
		try
		{	
		
		 //old date - current date		
		  long dateDifference = (currentDateepoch - serviceRequestDateEpoc); 
			  
		  System.out.println("Date difference  :  "+ dateDifference);

	// taking days from bussines service
		 String  processInstanceSplit[] = null;
		 String tenantId = null;
		 long businessSla = 0;
		
		String processIntanceData = getBussinessServiceSla(serviceRequestId);
		if(processIntanceData != null) {
		 processInstanceSplit =processIntanceData.split("#");
		}
		if(processInstanceSplit != null) {
		 tenantId=processInstanceSplit[0];
		 businessSla= Long.parseLong(processInstanceSplit[1]);
		}
		 

	//check days greter or less than
		
//		request.setTenantId(tenantId);
	
	
		if( businessSla > dateDifference )
		{
			//geting role details
			int days = (int) ((businessSla / (1000 * 60 * 60 * 24)) % 7);
			
			List <String> employeeDetailList=  getRolewiseEmployeeDetail(role,serviceRequestId,HCConstants.REMINDER,request.getService_request_date(),tenantId,request.getServiceType(),days);
			
			
		}
		
		else  if(businessSla <= dateDifference ) {
			int days = (int) ((businessSla / (1000 * 60 * 60 * 24)) % 7);
			List <String> employeeDetailList=  getRolewiseEmployeeDetail(role,serviceRequestId,HCConstants.OVERDAYS,request.getService_request_date(),tenantId,request.getServiceType(),days);
			
		}
		

		System.out.println("Ended Generate Average Processing Time Report - " + System.currentTimeMillis() / 1000);
		}
		catch(Exception ex)
		{
			
		}
		
 		}
 		}
		
	}

	private String getBussinessServiceSla(String serviceRequestId) {
		
		long sla = 0;
		String tenantId=null;
		String processIntanceData = null;
		String payloadData="{\"RequestInfo\": {\"userInfo\": {\"roles\": [{ \"code\": \"EE\",\"name\": \"EE\",\"tenantId\": \"ch.chandigarh\"}]}}}";
   try {
		String procesinstanceData = sendPostRequest(config.getWfHost().concat(config.getWfProcessSearch()).concat("?").concat("businessIds="+serviceRequestId+"&history="+HCConstants.TRUE+"&tenantId="+HCConstants.TENANT_ID), payloadData);
		
			org.json.JSONObject obj = new org.json.JSONObject(procesinstanceData);
			org.json.JSONArray ProcessInstances  = obj.getJSONArray("ProcessInstances");
	        
			
	        org.json.JSONObject ProcessInstancesDetails = new org.json.JSONObject(ProcessInstances.get(0).toString());
	        sla = ProcessInstancesDetails.getLong("businesssServiceSla");
	        tenantId = ProcessInstancesDetails.getString("tenantId");
	        //sla#tennatid
	        
	        processIntanceData = tenantId+"#"+sla;
	        System.out.println(processIntanceData);
	        
         }
    catch (Exception e) {
	     
                }

		return processIntanceData;
		
	}

	private List<String> getRolewiseEmployeeDetail(String role,String service_request_id,String action,String serviceRequestDate,String tenantId,String serviceType,int days) {
		
		List requestInfoList = new ArrayList();
		String mobileNumber = null;
		String uuid = null;
		String emailId = null;
		String userName =null;
		String tenantid = null;
		String type=null;
		

		String employeeDetails = null;
		
		List<ModuleDetail> moduleDetail = new ArrayList<ModuleDetail>();
		
	    RequestInfo requestInfo = new RequestInfo();
	    
	    String requestData = null;

	   String payload="{ \"RequestInfo\": {\"USER_INFO\": {\"tenantId \": \"ch\" } }}";

		try {
			
			 employeeDetails= sendPostRequest(config.getEgovHRMShost().concat(config.getEgovHRMSSearchEndpoint()).concat("?").concat("roles="+"EE"+"&tenantId="+HCConstants.TENANT_ID),payload);
			
			
			
			try {
				if(null !=employeeDetails)
				{
					
			org.json.JSONObject obj = new org.json.JSONObject(employeeDetails);
			org.json.JSONObject requestInfoEmployee = obj.getJSONObject("ResponseInfo");
		//
			org.json.JSONArray employeesList  = obj.getJSONArray("Employees");

			//action inforlist
			List <String> employee = new ArrayList<>();
			ServiceRequestData serviceRequest = new ServiceRequestData();
		    
	        for (int i = 0; i < employeesList.length(); i++) {

	 		    List Actioninfolist= new ArrayList();
	 		    RequestInfoWrapper infoWrapper = null;
	            org.json.JSONObject empDetails = new org.json.JSONObject(employeesList.get(i).toString());
	        	
	            org.json.JSONObject user = empDetails.getJSONObject("user");
	        	mobileNumber = user.getString("mobileNumber");

	        	uuid = user.getString("uuid");
	        	tenantid = user.getString("tenantId");
	        	
	        	type = user.getString("type");
	        	if(!user.isNull("emailId")) {
	        		emailId = user.getString("emailId").toString();
	        	}
	        	else {
	        		emailId = "";
	        		}
	        	long id= user.getLong("id");
	        		

	        	userName = user.getString("userName");
	        	List<Role> roleList= new ArrayList();
	        	org.json.JSONArray Roles  = user.getJSONArray("roles");
	        	if(null != Roles)
	        	{
	        		org.json.JSONObject docDetails=null;
	        	for(int roleCnt=0;roleCnt<Roles.length();roleCnt++)
	        	{
	        	    docDetails = new org.json.JSONObject(Roles.get(roleCnt).toString());
	        		Role getrole= new Role();
	        		getrole.setCode(docDetails.getString("code"));
	        		getrole.setName(docDetails.getString("name"));
	        		roleList.add(getrole);

	        	}
	        	}

	        	serviceRequest.setOwnerName(userName);
	        	serviceRequest.setEmail(emailId);
	        	serviceRequest.setContactNumber(mobileNumber);
	        	serviceRequest.setService_request_uuid(uuid);
	        	serviceRequest.setService_request_id(service_request_id);
	        	serviceRequest.setServiceType(serviceType);
	        	String by = serviceRequest.getCreatedBy() ;
	        	RequestInfo requestInfoDetails =new RequestInfo();
	        	User userInfoDetails = new User();
	        	userInfoDetails.setUuid(uuid);
	        	userInfoDetails.setUserName(userName);
	        	userInfoDetails.setEmailId(emailId);
	        	userInfoDetails.setMobileNumber(mobileNumber);
	        	userInfoDetails.setRoles(roleList);
	        	userInfoDetails.setId(id);
	        	userInfoDetails.setTenantId(tenantid);
	        	userInfoDetails.setType(type);
	        	requestInfoDetails.setUserInfo(userInfoDetails);
	       
	          ActionInfo newActionInfo = ActionInfo.builder().uuid(UUID.randomUUID().toString())
//	        			.businessKey(serviceReqRequest.getServices().get(0).getBusinessService())
	        			.action(HCConstants.REMINDER)
	        			.assignee(uuid)
	        			.by(by)
						.when(serviceRequest.getCreatedTime())
	        			.tenantId(HCConstants.TENANT_ID)
	        			.status("REMINDER").build();
	          Actioninfolist.add(newActionInfo);
	          
	          
	      	List<ServiceRequestData> serviceRequestList = new ArrayList<>();
	    	serviceRequestList.add(serviceRequest);	
	    	
//	    	
	    	infoWrapper = RequestInfoWrapper.builder().services(serviceRequestList)
					.actionInfo(Actioninfolist).requestInfo(requestInfoDetails).requestBody(serviceRequest).build();
					
	    	
	    	ServiceRequest serviceRequestobj= new ServiceRequest();
	    	serviceRequestobj.setServices(serviceRequestList);
	    	serviceRequestobj.setActionInfo(Actioninfolist);
	    	serviceRequestobj.setRequestInfo(requestInfoDetails);
//	    	sviceRequestobj.setRequestInfo(infoWrapper);
	    	
	    	
			sendNotification(serviceRequestobj,action,serviceRequestDate,tenantId,days);
	    	
	    	

	        }
	        }
	    
			}
			
			
	        catch( Exception ex) {
	        	
	        }
		
		} catch (HttpClientErrorException e) {
			System.out.print("Handled exception");
		}
		System.out.println(employeeDetails);
	   
			return requestInfoList;
		}

	
	public static String sendPostRequest(String requestUrl, String payload) {
        StringBuffer jsonString = new StringBuffer();
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(payload);
            writer.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
                System.out.println(line);
            }
            br.close();
            connection.disconnect();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return jsonString.toString();
    }

	
	//Fetch all the service request whos status is not COmpleted and Rejected
	private List<ServiceRequestData>  getServiceRequest() {
		
		
		List<ServiceRequestData> ServiceRequestList= new ArrayList<>() ;
		
		ServiceRequestData serviceRequestDetails = null;
    	JSONObject  serviceRequestJson = serviceRepository.getServiceRequestList();
    	for (int i = 0; i < serviceRequestJson.size(); i++) {
    		
    		//createdtime,service_request_id,current_assignee
    		
		serviceRequestDetails= parseServiceRequestData(serviceRequestJson);
		ServiceRequestList.add(serviceRequestDetails);
    	}
		
		return ServiceRequestList;
	}
	
	private  ServiceRequestData parseServiceRequestData(JSONObject serviceRequest)
    {
		 ServiceRequestData serviceRequestData = new ServiceRequestData();

        //Get service request object within list

	 try
	    {
		 
		 String service_request_id = (String) serviceRequest.get("service_request_id");   
	        serviceRequestData.setService_request_id(service_request_id);
			 
	   
	     Long createdtime=Long.parseLong( (String)serviceRequest.get("createdtime"));
	     
       serviceRequestData.setCreatedTime(createdtime);
       
        String current_assignee = (String) serviceRequest.get("current_assignee");   
        serviceRequestData.setCurrent_assignee(current_assignee);
        

        String serviceRequestDate=(String) serviceRequest.get("servicerequestdate"); 
        serviceRequestData.setService_request_date(serviceRequestDate);
        
        String serviceType=(String) serviceRequest.get("service_type"); 
        serviceRequestData.setServiceType(serviceType);
   
        
//        String service_request_id = (String) serviceRequest.get("service_request_id");   
//        serviceRequestData.setService_request_id(service_request_id);
	        }
		 catch(Exception ex)
		 {
		 
		 }
        
		 return serviceRequestData;
    }
	
	public static String getCurrentDateTimeMS() {
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");
        String datetime = ft.format(dNow);
        return datetime;
    }
	
	public void sendNotification(ServiceRequest serviceReqRequest,String action,String serviceRequestDate,String tenantId,int days)
    {
		//geting massage from localization
		
		Map<String, String> messageMap = hcNotificationConsumer.getLocalizationMessage( HCConstants.TENANT_ID, serviceReqRequest.getRequestInfo());
				
    	 for (ActionInfo actionInfo : serviceReqRequest.getActionInfo()) {
             if (null != actionInfo && (!StringUtils.isEmpty(actionInfo.getStatus()))) {
                 ServiceRequestData service = serviceReqRequest.getServices().get(0);            
             
                	{ 	
                     if (hcConfiguration.getIsSMSNotificationEnabled()) {
                     	SMSRequest smsRequest = prepareSMSRequest(service, actionInfo, serviceReqRequest.getRequestInfo(),messageMap,action,serviceRequestDate,days);
                     	hCProducer.push(hcConfiguration.getSmsNotifTopic(), smsRequest);

                        
                     }
                     
                     
                     if (hcConfiguration.getIsEmailNotificationEnabled()
                             && (null != service.getEmail() && !service.getEmail().isEmpty())) {
                     	
                     	EmailRequest emailRequests = prepareEmailRequest(service, actionInfo, serviceReqRequest.getRequestInfo(),messageMap,action,serviceRequestDate,days);
                     	hCProducer.push(hcConfiguration.getEmailNotifTopic(), emailRequests);
                         
                     }
//                     
//
                     if (hcConfiguration.getIsUsrEventNotificationEnabled()) {
                         EventRequest request = preparePushNotificationRequest(service, actionInfo, serviceReqRequest.getRequestInfo(),messageMap,action,serviceRequestDate,tenantId,days);
                         hCProducer.push(hcConfiguration.getSaveUserEventsTopic(), request);
                     }

                 }
             } else {
                 continue;
             }}
         }
	
	public EmailRequest prepareEmailRequest(ServiceRequestData serviceReq, ActionInfo actionInfo, RequestInfo requestInfo,Map<String, String> messageMap,String action,String serviceRequestDate,int days) {
	 	
		 List<Event> events = new ArrayList<>();
	 	String emailIdRetrived = null;
	 	String employeeDetailsRetrived=null;
	 	String employeeNameRetrived=null;
	 	String message=null;
	 	String subject = null;
	    // List<SMSRequest> smsRequestsTobeSent = new ArrayList<>();
	     if (action.equals(HCConstants.REMINDER)) {    
	     	    emailIdRetrived = serviceReq.getEmail();
	            message = getMessage( HCConstants.REMINDER, serviceReq, requestInfo,  messageMap, null,HCConstants.EMAIL,serviceRequestDate,days );           	
	            subject = getSubject( HCConstants.REMINDER, serviceReq, requestInfo,  messageMap, null ,HCConstants.EMAIL);
	     	}
	    
	     else if (action.equals(HCConstants.OVERDAYS))
	     
	     {
	     	emailIdRetrived=serviceReq.getEmail();
	     	message=  getMessage(HCConstants.OVERDAYS, serviceReq, requestInfo,  messageMap, null ,HCConstants.EMAIL,serviceRequestDate,days);
	    	subject = getSubject( HCConstants.OVERDAYS, serviceReq, requestInfo,  messageMap, null ,HCConstants.EMAIL);
	     }
	    
	     
	     if(emailIdRetrived==null ||message== null)
	 	   return null;
	     return EmailRequest.builder().email(emailIdRetrived).subject(subject).body(message).isHTML(true).build();
	 }
    	 
 public SMSRequest prepareSMSRequest(ServiceRequestData serviceReq, ActionInfo actionInfo, RequestInfo requestInfo,Map<String, String> messageMap,String action,String serviceRequestDate, int days) {
    	    	
    	    	String phoneNumberRetrived = null;
    	    	String employeeDetailsRetrived=null;
    	    	String employeeNameRetrived=null;
    	    	String message=null;
    	       // List<SMSRequest> smsRequestsTobeSent = new ArrayList<>();
    	        if ((action.equals(HCConstants.REMINDER))) {    
    	        	   phoneNumberRetrived = serviceReq.getContactNumber();
    	               message =  getMessage( HCConstants.REMINDER, serviceReq, requestInfo,  messageMap, null,HCConstants.SMS,serviceRequestDate,days );
    	         
    	        	}
    	       
    	     	  
    	     	else if (action.equals(HCConstants.OVERDAYS)) { 
    	     	   phoneNumberRetrived = serviceReq.getContactNumber();
    	           message =  getMessage( HCConstants.OVERDAYS, serviceReq, requestInfo,  messageMap, null,HCConstants.SMS,serviceRequestDate,days );
    	       
    	     	}
    	       
    	        if(phoneNumberRetrived==null ||message== null)
    	    	   return null;
    	        return SMSRequest.builder().mobileNumber(phoneNumberRetrived).message(message).build();
    	    }
 
 
 
 public String getMessage(String action, ServiceRequestData serviceReq,RequestInfo requestInfo,  Map<String, String> messageMap, String employeeName,String  notifcationType,String serviceRequestDate,int days )
 {
 	String text=null;
 	
		//epoc date to date convert

		String employee=serviceReq.getOwnerName();
		
		switch(action)
		{
		
		case HCConstants.OVERDAYS :
			
			if(notifcationType.equals(HCConstants.SMS))
			{
				text = messageMap.get(HCConstants.HC_EMPLOYEE_OVERDAYS_SMS_NOTIFICATION);
			}
			else if (notifcationType.equals(HCConstants.EMAIL))
			{
				
				text = messageMap.get(HCConstants.HC_EMPLOYEE_OVERDAYS_EMAIL_NOTIFICATION);
			}
			else  if (notifcationType.equals(HCConstants.PUSH))
				text = messageMap.get(HCConstants.HC_EMPLOYEE_OVERDAYS_PUSH_NOTIFICATION);
			
			if(text != null)
			{
			text = text.replace(HCConstants.SMS_NOTIFICATION_EMP_NAME_KEY, employee)  
					.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_TYPE, serviceReq.getServiceType())
					.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_ID, serviceReq.getService_request_id())
                  .replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_DATE_KEY, serviceRequestDate)
                  .replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_DAYS_KEY, days+"");
			}
			break;
			
			
		case HCConstants.REMINDER :
			if(notifcationType.equals(HCConstants.SMS))
			{
				text = messageMap.get(HCConstants.HC_EMPLOYEE_REMAINDER_SMS_NOTIFICATION);
			}
			else if (notifcationType.equals(HCConstants.EMAIL))
			{
				
				text = messageMap.get(HCConstants.HC_EMPLOYEE_REMAINDER_EMAIL_NOTIFICATION);
			}
			else  if (notifcationType.equals(HCConstants.PUSH))
				text = messageMap.get(HCConstants.HC_EMPLOYEE_REMAINDER_PUSH_NOTIFICATION);
				
	          if(text != null)
	          {
			text = text.replace(HCConstants.SMS_NOTIFICATION_EMP_NAME_KEY, employee) 
					.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_TYPE, serviceReq.getServiceType())
					.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_ID, serviceReq.getService_request_id())
                  .replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_DATE_KEY, serviceRequestDate);
                 // .replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_DAYS_KEY, days+"");	
	          }
			break;			
			
		
		
		}
 	
 	return text;
 }private String getSubject(String action, ServiceRequestData serviceReq, RequestInfo requestInfo,
			Map<String, String> messageMap, String employeeName, String email) {
		
		String text=null;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dateobj = new Date();
		String genratedDate = df.format(dateobj);
		
		switch(action)
		{
		
		case HCConstants.REMINDER :
			
			text = messageMap.get(HCConstants.HC_EMPLOYEE_REMAINDER_EMAIL_SUBJECT);

			break;

		case HCConstants.OVERDAYS:
			
			text = messageMap.get(HCConstants.HC_EMPLOYEE_OVERDAYS_EMAIL_SUBJECT);
			break;
		
		}
		if(text != null)
		{
		text = text.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_ID, serviceReq.getService_request_id());
		}

		return text;
	}
 
 public EventRequest preparePushNotificationRequest(ServiceRequestData serviceReq, ActionInfo actionInfo, RequestInfo requestInfo,Map<String, String> messageMap,String action,String serviceRequestDate,String tenantId,int days) {
 	

 	String emailIdRetrived = null;
 	String employeeDetailsRetrived=null;
 	String employeeNameRetrived=null;
 	String message=null;
 	String subject = null;
 	List<Event> events = new ArrayList<>();
    
 	if (action.equals(HCConstants.REMINDER)) {    
 	   
        message =  getMessage( HCConstants.REMINDER, serviceReq, requestInfo,  messageMap, null,HCConstants.PUSH,serviceRequestDate,days );
  
 	}

	 else if (action.equals(HCConstants.OVERDAYS)) {    
		   message =  getMessage( HCConstants.OVERDAYS, serviceReq, requestInfo,  messageMap, null,HCConstants.PUSH,serviceRequestDate,days );

	}
     
     List<String> toUsers = new ArrayList<>();
     toUsers.add(requestInfo.getUserInfo().getUuid());
     
     List<String> toRole= new ArrayList<>();
     toRole.add("All");
     Recepient recepient = Recepient.builder()
             .toUsers(toUsers).toRoles(toRole).build();

     Event event = Event.builder()
             .tenantId(tenantId)
             .description(message)
             .eventType(HCConstants.USREVENTS_EVENT_TYPE)
             .name(HCConstants.USREVENTS_EVENT_NAME)
             .postedBy(HCConstants.USREVENTS_EVENT_POSTEDBY)
             .recepient(recepient)
             .referenceId(serviceReq.getService_request_id())

             .eventDetails(null).build();
     		 events.add(event);

 return EventRequest.builder().requestInfo(requestInfo).events(events).build();
    
     
 }
 

 

}
