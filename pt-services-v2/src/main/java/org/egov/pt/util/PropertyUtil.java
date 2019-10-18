package org.egov.pt.util;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.web.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static org.egov.pt.util.PTConstants.NOTIFICATION_LOCALE;

@Component
public class PropertyUtil {



    @Autowired
    private PropertyConfiguration config;


    /**
     * Method to return auditDetails for create/update flows
     *
     * @param by
     * @param isCreate
     * @return AuditDetails
     */
    public AuditDetails getAuditDetails(String by, Boolean isCreate) {
        Long time = System.currentTimeMillis();
        if(isCreate)
            return AuditDetails.builder().createdBy(by).lastModifiedBy(by).createdTime(time).lastModifiedTime(time).build();
        else
            return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(time).build();
    }

    public MdmsCriteriaReq prepareMdMsRequest(String tenantId,String moduleName, List<String> names, String filter, RequestInfo requestInfo) {

        List<MasterDetail> masterDetails = new ArrayList<>();
        MasterDetail masterDetail;

        names.forEach(name -> {
            masterDetails.add(MasterDetail.builder().name(name).filter(filter).build());
        });

        ModuleDetail moduleDetail = ModuleDetail.builder()
                .moduleName(moduleName).masterDetails(masterDetails).build();
        List<ModuleDetail> moduleDetails = new ArrayList<>();
        moduleDetails.add(moduleDetail);
        MdmsCriteria mdmsCriteria = MdmsCriteria.builder().tenantId(tenantId).moduleDetails(moduleDetails).build();
        return MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
    }


    public void addAddressIds(List<Property> responseProperties,List<Property> requestProperties){
        Map<String,String > propertyIdToAddressId = new HashMap<>();
        responseProperties.forEach(property -> {
            propertyIdToAddressId.put(property.getPropertyId(),property.getAddress().getId());
        });
        requestProperties.forEach(property -> {
            property.getAddress().setId(propertyIdToAddressId.get(property.getPropertyId()));
        });
    }


    /**
     * Returns the uri for the localization call
     * @param tenantId TenantId of the propertyRequest
     * @return The uri for localization search call
     */
    public StringBuilder getUri(String tenantId, RequestInfo requestInfo){
        if(config.getIsStateLevel())
            tenantId = tenantId.split("\\.")[0];

        String locale = NOTIFICATION_LOCALE;
        if(!StringUtils.isEmpty(requestInfo.getMsgId()) && requestInfo.getMsgId().split("\\|").length>=2)
            locale = requestInfo.getMsgId().split("\\|")[1];

        StringBuilder uri = new StringBuilder();
        uri.append(config.getLocalizationHost())
                .append(config.getLocalizationContextPath()).append(config.getLocalizationSearchEndpoint());
        uri.append("?").append("locale=").append(locale)
                .append("&tenantId=").append(tenantId)
                .append("&module=").append(PTConstants.MODULE);
        return uri;
    }


    /**
     * Createsn and returns the workflow search url
     * @return workflow search url
     */
    public StringBuilder getWorkflowSearchUrl(){
        StringBuilder builder = new StringBuilder(config.getWorkflowHost());
        builder.append(config.getWorkflowSearchEndpoint());
        return builder;
    }

    /**
     * Creates and returns the businessService search url
     * @return businessService search url
     */
    public StringBuilder getBusinessServiceSearchUrl(){
        StringBuilder builder = new StringBuilder(config.getWorkflowHost());
        builder.append(config.getBusinessServiceSearchEndpoint());
        return builder;
    }


    /**
     * Createsn and returns the workflow update url
     * @return workflow update url
     */
    public StringBuilder getWorkflowStatusUpdateUrl(){
        StringBuilder builder = new StringBuilder(config.getWorkflowHost());
        builder.append(config.getWorkflowUpdateEndpoint());
        return builder;
    }


    public  void addItemToMap(Map<String,List<Property>> map,String key,Property value){
        if(map.containsKey(key))
            map.get(key).add(value);
        else {
            LinkedList<Property> temp = new LinkedList<>();
            temp.add(value);
            map.put(key,temp);
        }
    }


    /**
     * Returns PropertyCriteria to search for properties in database with ids set from properties in request
     *
     * @param request PropertyRequest received for update
     * @return PropertyCriteria containg ids of all properties and all its childrens
     */
    public PropertyCriteria getPropertyCriteriaForSearch(PropertyRequest request) {

        RequestInfo requestInfo = request.getRequestInfo();
        List<Property> properties=request.getProperties();
        Set<String> ids = new HashSet<>();
        Set<String> propertyDetailids = new HashSet<>();
        Set<String> unitids = new HashSet<>();
        Set<String> documentids = new HashSet<>();
        Set<String> ownerids = new HashSet<>();
        Set<String> addressids = new HashSet<>();

        PropertyCriteria propertyCriteria = new PropertyCriteria();
        /*
         * Is search on ids other than propertyIds required?
         * */
        properties.forEach(property -> {
            ids.add(property.getPropertyId());
            if(!CollectionUtils.isEmpty(ids)) {
                if (property.getAddress().getId() != null)
                    addressids.add(property.getAddress().getId());
                property.getPropertyDetails().forEach(propertyDetail -> {
                    if (propertyDetail.getAssessmentNumber() != null)
                        propertyDetailids.add(propertyDetail.getAssessmentNumber());
                    if (!CollectionUtils.isEmpty(propertyDetail.getOwners()))
                        ownerids.addAll(getOwnerids(propertyDetail));
                    if (!CollectionUtils.isEmpty(propertyDetail.getDocuments()))
                        documentids.addAll(getDocumentids(propertyDetail));
                    if (!CollectionUtils.isEmpty(propertyDetail.getUnits())) {
                        unitids.addAll(getUnitids(propertyDetail));
                    }
                });
            }
        });

        propertyCriteria.setTenantId(properties.get(0).getTenantId());
        propertyCriteria.setIds(ids);
        propertyCriteria.setPropertyDetailids(propertyDetailids);
        propertyCriteria.setAddressids(addressids);
        propertyCriteria.setOwnerids(ownerids);
        propertyCriteria.setUnitids(unitids);
        propertyCriteria.setDocumentids(documentids);

        return propertyCriteria;
    }



    /**
     * Extract all ownerids from given propertyDetail
     * @param propertyDetail PropertyDetail whose owner ids are to be returned
     * @return Set of ids of all owners of the given propertyDetail
     */
    private Set<String> getOwnerids(PropertyDetail propertyDetail){
        Set<OwnerInfo> owners= propertyDetail.getOwners();
        Set<String> ownerIds = new HashSet<>();
        owners.forEach(owner -> {
            if(owner.getUuid()!=null)
                ownerIds.add(owner.getUuid());
        });
        return ownerIds;
    }


    /**
     * Adds ids of all Units of property to a list
     * @param propertyDetail PropertyDetail whose unit ids are to be returned
     * @return Set of all unitids of a propertyDetail
     */
    private List<String> getUnitids(PropertyDetail propertyDetail){
        List<Unit> units= propertyDetail.getUnits();
        List<String> unitIds = new ArrayList<>();
        units.forEach(unit -> {
            if(unit.getId()!=null)
                unitIds.add(unit.getId());
        });
        return unitIds;
    }


    /**
     * Adds ids of all Documents of property to a list
     * @param propertyDetail PropertyDetail whose document ids are to be returned
     * @return Set of all documentids of properyDetail
     */
    private Set<String> getDocumentids(PropertyDetail propertyDetail){
        Set<Document> documents= propertyDetail.getDocuments();
        Set<String> documentIds = new HashSet<>();
        documents.forEach(document -> {
            if(document.getId()!=null)
                documentIds.add(document.getId());
        });
        return documentIds;
    }



}
