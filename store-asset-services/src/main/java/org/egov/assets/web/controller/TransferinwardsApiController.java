package org.egov.assets.web.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.egov.assets.model.MaterialReceiptSearch;
import org.egov.assets.model.TransferInwardRequest;
import org.egov.assets.model.TransferInwardResponse;
import org.egov.assets.service.TransferinwardsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/transferinwards")
public class TransferinwardsApiController {

	@Autowired
	private TransferinwardsService transferinwardsService;

	@PostMapping(value = "/_create", produces = { "application/json" }, consumes = { "application/json" })
	public ResponseEntity<TransferInwardResponse> transferinwardsCreatePost(
			@NotNull @RequestParam(value = "tenantId", required = true) String tenantId,
			@Valid @RequestBody TransferInwardRequest transferInwardRequest) {
		return new ResponseEntity<>(
				transferinwardsService.create(transferInwardRequest, tenantId), HttpStatus.OK);
	}

	@PostMapping(value = "/_search", produces = { "application/json" }, consumes = { "application/json" })
	public ResponseEntity<TransferInwardResponse> transferinwardsSearchPost(
			@NotNull @RequestParam(value = "tenantId", required = true) String tenantId,
			@Valid @RequestBody org.egov.common.contract.request.RequestInfo requestInfo,
			@Size(max = 50) @RequestParam(value = "ids", required = false) List<String> ids,
			@RequestParam(value = "receiptDate", required = false) Long receiptDate,
			@RequestParam(value = "issueNumber", required = false) List<String> issueNumber,
			@RequestParam(value = "description", required = false) String description,
			@RequestParam(value = "mrnNumber", required = false) List<String> mrnNumber,
			@RequestParam(value = "status", required = false) List<String> status,
			@RequestParam(value = "stateId", required = false) Long stateId,
			@Min(0) @Max(100) @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize,
			@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
			@RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy) {
		MaterialReceiptSearch materialReceiptSearch = MaterialReceiptSearch.builder().tenantId(tenantId)
				.mrnNumber(mrnNumber).receiptDate(receiptDate).issueNumber(issueNumber).mrnStatus(status)
				.pageNumber(pageNumber).pageSize(pageSize).build();
		TransferInwardResponse response = transferinwardsService.search(materialReceiptSearch, tenantId);
		return new ResponseEntity<TransferInwardResponse>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/_update", produces = { "application/json" }, consumes = { "application/json" })
	public ResponseEntity<TransferInwardResponse> transferinwardsUpdatePost(
			@NotNull @RequestParam(value = "tenantId", required = true) String tenantId,
			@Valid @RequestBody TransferInwardRequest transferInwardRequest) {
		return new ResponseEntity<TransferInwardResponse>(
				transferinwardsService.update(transferInwardRequest, tenantId), HttpStatus.OK);
	}
}
