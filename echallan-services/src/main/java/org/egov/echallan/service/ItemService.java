package org.egov.echallan.service;

import java.util.List;
import java.util.UUID;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.echallan.config.EcConstants;
import org.egov.echallan.repository.ItemRepository;
import org.egov.echallan.service.validator.CustomBeanValidator;
import org.egov.echallan.web.models.ItemMaster;
import org.egov.echallan.web.models.RequestInfoWrapper;
import org.egov.echallan.web.models.ResponseInfoWrapper;
import org.egov.echallan.workflow.WorkflowIntegrator;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ItemService {
	private final ObjectMapper objectMapper;
	private WorkflowIntegrator wfIntegrator;
	private DeviceSourceService deviceSource;
	private ItemRepository repository;
	private CustomBeanValidator validate;

	@Autowired
	public ItemService(WorkflowIntegrator wfIntegrator, ObjectMapper objectMapper, ItemRepository repository,
			CustomBeanValidator validate,DeviceSourceService deviceSource) {
		this.objectMapper = objectMapper;
		this.wfIntegrator = wfIntegrator;
		this.repository = repository;
		this.validate=validate;
		this.deviceSource=deviceSource;

	}

	/**
	* This method will add entry into Item master
	*
	* @param RequestInfoWrapper containing fine record
	* @param requestHeader for saving device source information
	* @return HTTP status on success
	* @throws CustomException ITEMMASTER_ADD_EXCEPTION
	*/
	public ResponseEntity<ResponseInfoWrapper> addItem(RequestInfoWrapper requestInfoWrapper, String requestHeader) {
		log.info("Item Service - Add Item");
		try {
			ItemMaster itemMaster = objectMapper.convertValue(requestInfoWrapper.getRequestBody(), ItemMaster.class);
			itemMaster.setCreatedBy(requestInfoWrapper.getAuditDetails().getCreatedBy());
			itemMaster.setCreatedTime(requestInfoWrapper.getAuditDetails().getCreatedTime());
			itemMaster.setLastModifiedBy(requestInfoWrapper.getAuditDetails().getLastModifiedBy());
			itemMaster.setLastModifiedTime(requestInfoWrapper.getAuditDetails().getLastModifiedTime());
			validate.validateFields(itemMaster);


			itemMaster.setItemUuid(UUID.randomUUID().toString());
			
			String sourceUuid = deviceSource.saveDeviceDetails(requestHeader, "AddItemEvent",
					itemMaster.getTenantId(), requestInfoWrapper.getAuditDetails());
			itemMaster.setSourceUuid(sourceUuid);

			repository.addItems(itemMaster);
			return new ResponseEntity<>(ResponseInfoWrapper.builder()
					.responseInfo(ResponseInfo.builder().status(EcConstants.STATUS_SUCCESS).build()).responseBody(itemMaster).build(),
					HttpStatus.OK);
		} catch (Exception e) {
			log.error("Item Service - Add Item Exception"+e.getMessage());
			throw new CustomException("ITEMMASTER_ADD_EXCEPTION", e.getMessage());
		}

	}

	/**
	*This method will update item master entry
	*
	* @param RequestInfoWrapper containing item master object
	* @return HTTP status on success
	* @throws CustomException ITEMMASTER_UPDATE_EXCEPTION
	*/
	public ResponseEntity<ResponseInfoWrapper> updateItem(RequestInfoWrapper requestInfoWrapper) {
		log.info("Item Service - Update Item");
		try {
			ItemMaster itemMaster = objectMapper.convertValue(requestInfoWrapper.getRequestBody(), ItemMaster.class);
			itemMaster.setCreatedBy(requestInfoWrapper.getAuditDetails().getCreatedBy());
			itemMaster.setCreatedTime(requestInfoWrapper.getAuditDetails().getCreatedTime());
			itemMaster.setLastModifiedBy(requestInfoWrapper.getAuditDetails().getLastModifiedBy());
			itemMaster.setLastModifiedTime(requestInfoWrapper.getAuditDetails().getLastModifiedTime());
			validate.validateFields(itemMaster);


			repository.updateItem(itemMaster);
			return new ResponseEntity<>(ResponseInfoWrapper.builder()
					.responseInfo(ResponseInfo.builder().status(EcConstants.STATUS_SUCCESS).build()).responseBody(itemMaster).build(),
					HttpStatus.OK);
		} catch (Exception e) {
			log.error("Item Service - Update Item Exception"+e.getMessage());
			throw new CustomException("ITEMMASTER_UPDATE_EXCEPTION", e.getMessage());
		}

	}

	/**
	*This method will fetch item master entries
	*
	* @param RequestInfoWrapper SearchCriteria
	* @return ResponseInfoWrapper containing list of items
	* @throws CustomException ITEMMASTER_GET_EXCEPTION
	*/
	public ResponseEntity<ResponseInfoWrapper> getItem(RequestInfoWrapper requestInfoWrapper) {
		log.info("Item Service - Get Item");
		try {
			List<ItemMaster> listItemMaster = repository.getItem(requestInfoWrapper);
			return new ResponseEntity<>(
					ResponseInfoWrapper.builder().responseInfo(ResponseInfo.builder().status(EcConstants.STATUS_SUCCESS).build())
							.responseBody(listItemMaster).build(),
					HttpStatus.OK);
		} catch (Exception e) {
			log.error("Item Service - Get Item Exception"+e.getMessage());
			throw new CustomException("ITEMMASTER_GET_EXCEPTION", e.getMessage());
		}
	}

}
