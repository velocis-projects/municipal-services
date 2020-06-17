package org.egov.assets.web.controller;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.egov.assets.model.IndentRequest;
import org.egov.assets.model.IndentResponse;
import org.egov.assets.model.IndentSearch;
import org.egov.assets.service.IndentService;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/indents")
public class IndentsApiController {
	@Autowired
	private IndentService indentService;

	@PostMapping(value = "/_create", produces = { "application/json" }, consumes = { "application/json" })
	public ResponseEntity<IndentResponse> indentsCreatePost(
			@NotNull @RequestParam(value = "tenantId", required = true) String tenantId,
			@Valid @RequestBody IndentRequest indentRequest) {
		IndentResponse response = indentService.create(indentRequest);
		return new ResponseEntity(response, HttpStatus.OK);
	}

	@PostMapping(value = "/_search", produces = { "application/json" }, consumes = { "application/json" })
	public ResponseEntity<IndentResponse> indentsSearchPost(
			@NotNull @RequestParam(value = "tenantId", required = true) String tenantId,
			@RequestBody RequestInfo requestInfo,
			@Size(max = 50) @RequestParam(value = "ids", required = false) List<String> ids,
			@RequestParam(value = "issueStore", required = false) String issueStore,
			@RequestParam(value = "indentDate", required = false) Long indentDate,
			@RequestParam(value = "indentNumber", required = false) String indentNumber,
			@RequestParam(value = "indentPurpose", required = false) String indentPurpose,
			@RequestParam(value = "indentStatus", required = false) String indentStatus,
			@RequestParam(value = "totalIndentValue", required = false) BigDecimal totalIndentValue,
			@RequestParam(value = "indentType", required = false) String indentType,
			@RequestParam(value = "searchPurpose", required = false) String searchPurpose,
			@RequestParam(value = "inventoryType", required = false) String inventoryType,
			@Min(0) @Max(100) @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize,
			@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
			@RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy) {
		System.out.println("RequestInfo...................." + requestInfo);
		System.out.println("RequestInfo....................");

		IndentSearch is = new IndentSearch().builder().tenantId(tenantId).ids(ids).indentDate(indentDate)
				.indentNumber(indentNumber).indentPurpose(indentPurpose).inventoryType(inventoryType)
				.issueStore(issueStore).indentType(indentType).searchPurpose(searchPurpose).build();
		IndentResponse response = indentService.search(is, requestInfo);
		return new ResponseEntity(response, HttpStatus.OK);
	}

	@PostMapping(value = "/_searchindentforpo", produces = { "application/json" }, consumes = { "application/json" })
	public ResponseEntity<IndentResponse> indentsSearchindentforpoPost(
			@NotNull @RequestParam(value = "tenantId", required = true) String tenantId,
			@Valid @RequestBody RequestInfo requestInfo,
			@Size(max = 50) @RequestParam(value = "ids", required = false) List<String> ids,
			@RequestParam(value = "issueStore", required = false) Long issueStore,
			@RequestParam(value = "indentDate", required = false) Long indentDate,
			@RequestParam(value = "indentNumber", required = false) String indentNumber,
			@RequestParam(value = "indentPurpose", required = false) String indentPurpose,
			@RequestParam(value = "searchPurpose", required = false) String searchPurpose,
			@RequestParam(value = "indentStatus", required = false) String indentStatus,
			@RequestParam(value = "totalIndentValue", required = false) BigDecimal totalIndentValue,
			@Min(0) @Max(100) @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize,
			@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
			@RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy) {
		// do some magic!
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value = "/_update", produces = { "application/json" }, consumes = { "application/json" })
	public ResponseEntity<IndentResponse> indentsUpdatePost(
			@NotNull @RequestParam(value = "tenantId", required = true) String tenantId,
			@Valid @RequestBody IndentRequest indentRequest) {
		IndentResponse response = indentService.update(indentRequest);
		return new ResponseEntity(response, HttpStatus.OK);
	}

}