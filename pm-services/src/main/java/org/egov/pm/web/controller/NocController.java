package org.egov.pm.web.controller;

import java.io.IOException;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.pm.model.Errors;
import org.egov.pm.model.RequestData;
import org.egov.pm.service.NocService;
import org.egov.pm.util.CommonConstants;
import org.egov.pm.util.UserUtil;
import org.egov.pm.web.contract.NocResponse;
import org.egov.pm.web.contract.ResponseData;
import org.egov.pm.web.contract.factory.ResponseFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("noc")
public class NocController {

	@Autowired
	private NocService nocService;

	@Autowired
	private UserUtil userUtil;

	// Get NOC
	// Get NOC
	@PostMapping("_get")
	@ResponseBody
	public ResponseEntity<NocResponse> get(@RequestBody RequestData requestData) {

		log.debug(String.format("STARTED GET NOC REQUEST : %1s", requestData.toString()));
		return nocService.searchNoc(requestData);
	}

	// Viewing NOC
	@PostMapping("_view")
	@ResponseBody
	public ResponseEntity<NocResponse> view(@RequestBody RequestData requestData) {
		log.debug(String.format("STARTED VIEW NOC REQUEST : %1s", requestData.toString()));
		return nocService.viewNoc(requestData);
	}

	@PostMapping("_getCertificateData")
	@ResponseBody
	public ResponseEntity<NocResponse> getCertificateData(@RequestBody RequestData requestData) {
		log.debug(String.format("STARTED GET CERTIFICATEDATA NOC REQUEST : %1s", requestData.toString()));
		return nocService.getCertificateData(requestData);
	}

	// Adding NOC
	@PostMapping("_create")
	@ResponseBody
	public ResponseEntity<ResponseData> createNoc(@RequestBody RequestData requestData) {
		log.debug(String.format("STARTED ADD/CREATE NOC REQUEST : %1s", requestData.toString()));
		return nocService.saveNoc(requestData);
	}

	// update Noc
	@PostMapping("_update")
	public ResponseEntity<ResponseData> update(@RequestBody RequestData requestData, BindingResult bindingResult) {

		log.debug(String.format("STARTED UPDATE NOC REQUEST : %1s", requestData.toString()));
		return nocService.updateNoc(requestData);
	}

	@PostMapping("_updateappstatus")
	@ResponseBody
	public ResponseEntity<ResponseData> updateApplicationStatus(@RequestBody RequestData requestData) {

		log.debug(String.format("STARTED APPLICATION STATUS UPDATE NOC REQUEST : %1s", requestData.toString()));
		return nocService.updateNocApplicationStatus(requestData);
	}


	@PostMapping("_viewPriceBook")
	@ResponseBody
	public ResponseEntity<NocResponse> viewPriceBook(@RequestBody RequestData requestData, BindingResult bindingResult)
			throws ParseException {

		log.debug(String.format("STARTED viewPriceBook() NOC REQUEST : %1s", requestData.toString()));
		Errors response = userUtil.validateUser(requestData);
		JSONObject dataPayLoad = requestData.getDataPayload();
		NocResponse nocResponse = null;
		if (response.getError().getMessage().equals(CommonConstants.SUCCESS)) {
			if (dataPayLoad.get(CommonConstants.PRICE_BOOK_ID).toString() == null
					|| dataPayLoad.get(CommonConstants.PRICE_BOOK_ID).toString().equals("")) {
				nocResponse = nocService.viewPriceBook(requestData);
			} else {
				nocResponse = nocService.viewPriceBookById(requestData);
			}
			log.debug(String.format("ENDED viewPriceBook() NOC RESPONSE : %1s", nocResponse.getNocApplicationDetail()));
			return new ResponseEntity<>(nocResponse, HttpStatus.OK);
		}

		nocResponse = new NocResponse();
		nocResponse.setResposneInfo(
				new ResponseInfo().builder().resMsgId("INVALID USER").status(CommonConstants.FAIL).build());
		log.debug(String.format("ENDED viewPriceBook() NOC RESPONSE : %1s", nocResponse.toString()));
		return new ResponseEntity<>(nocResponse, HttpStatus.OK);
	}

	// Update PRiceBook
	@PostMapping("_updatepricebook")
	@ResponseBody
	public ResponseEntity<?> _updatepricebook(@RequestBody RequestData requestData, BindingResult bindingResult)
			throws JsonParseException, JsonMappingException, JsonProcessingException, IOException,
			java.text.ParseException {

		log.debug("updatepricebook Request:" + requestData);
		NocResponse responseDataResponse = null;

		Errors res = null;
		Errors response = userUtil.validateUser(requestData);
		if (response.getError().getMessage().equals("success")) {
			log.debug("_updatepricebook :" + requestData.getDataPayload());
			responseDataResponse = nocService.updatepricebook(requestData);

			return new ResponseEntity<>(responseDataResponse, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
}
