package org.egov.pt.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.repository.ServiceRequestRepository;
import org.egov.pt.util.PropertyUtil;
import org.egov.pt.web.models.Property;
import org.egov.pt.web.models.PropertyInfo;
import org.egov.pt.web.models.RequestInfoWrapper;
import org.egov.pt.web.models.Workflow;
import org.egov.pt.web.models.workflow.*;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WorkflowService {


    private ServiceRequestRepository serviceRequestRepository;

    private PropertyUtil util;

    private ObjectMapper mapper;

    @Autowired
    public WorkflowService(ServiceRequestRepository serviceRequestRepository, PropertyUtil util) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.util = util;
    }


    /**
     * Creates processInstance request for the given workflow object in property and calls workflow
     * transition api to update status in workflow service
     * @param requestInfo RequestInfo of the update request
     * @param properties Properties whose status are to be updated
     * @return
     */
    public List<Property> updateStatus(RequestInfo requestInfo, List<Property> properties){

        List<ProcessInstance> processInstances = new LinkedList<>();

        properties.forEach(property -> {
            Workflow workflow = property.getWorkflow();

            ProcessInstance processInstance = ProcessInstance.builder()
                    .action(workflow.getAction())
                    .businessId(workflow.getApplicationNumber())
                    .businessService(workflow.getWorkflowName())
                    .documents(workflow.getDocuments())
                    .tenantId(property.getTenantId())
                    .build();

            processInstances.add(processInstance);
        });

        ProcessInstanceRequest statusUpdateRequest = ProcessInstanceRequest.builder().requestInfo(requestInfo).processInstances(processInstances).build();
        Object res = serviceRequestRepository.fetchResult(util.getWorkflowStatusUpdateUrl(), statusUpdateRequest);
        ProcessInstanceResponse processInstanceResponse;
        try {
            processInstanceResponse = mapper.convertValue(res,ProcessInstanceResponse.class);
        }
        catch (Exception e){
            throw new CustomException("PARSING_ERROR","Failed to parse workflow update response");
        }

        Map<String, State> applicationNumberToStatusMap = processInstanceResponse.getProcessInstances().stream()
                .collect(Collectors.toMap(ProcessInstance::getBusinessId,ProcessInstance::getState));

        Map<String,String> errorMap = new HashMap<>();

        // Update the status in property workflow object from the response
        properties.forEach(property -> {
            if(applicationNumberToStatusMap.get(property.getWorkflow().getApplicationNumber())!=null){
                State updatedState = applicationNumberToStatusMap.get(property.getWorkflow().getApplicationNumber());
                property.getWorkflow().setStatus(updatedState.getState());
                if(updatedState.getIsTerminateState())
                    property.setStatus(PropertyInfo.StatusEnum.ACTIVE);

            }
            else{
                errorMap.put("PROCESSINSTANCE_NOT_FOUND","The processInStance for assessmentNumber is not found for: "+property.getWorkflow().getApplicationNumber()
                        +" in status update response");
            }
        });

        if(!errorMap.isEmpty())
            throw new CustomException(errorMap);

        return properties;

    }



    public BusinessService getBusinessService(RequestInfo requestInfo, Workflow workflow){

        String tenantId = workflow.getTenantId();
        String workflowName = workflow.getWorkflowName();

        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
        StringBuilder url = util.getBusinessServiceSearchUrl(tenantId,workflowName);
        Object res = serviceRequestRepository.fetchResult(url, requestInfoWrapper);
        BusinessServiceResponse businessServiceResponse = null;
        try{
             businessServiceResponse = mapper.convertValue(res,BusinessServiceResponse.class);
        }
        catch (Exception e){
           throw new  CustomException("BUSINESSSERVICE_ERROR","Failed to parse businessService search response");
        }

        return businessServiceResponse.getBusinessServices().get(0);
    }



    public Boolean isStateUpdatable(BusinessService businessService, Workflow workflow){
        State propertyState = null;
        for(State state : businessService.getStates()){
            if(state.getState().equalsIgnoreCase(workflow.getStatus())){
                propertyState = state;
                break;
            }
        }
        if(propertyState == null)
            throw new CustomException("INVALID_STATUS","The status: "+workflow.getStatus()+" is not found in the businessService: "+businessService.getBusinessService());

        return propertyState.getIsStateUpdatable();
    }








}
