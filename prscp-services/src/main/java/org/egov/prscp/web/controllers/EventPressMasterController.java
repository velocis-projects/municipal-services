package org.egov.prscp.web.controllers;

import javax.validation.Valid;

import org.egov.prscp.service.EventPressMasterService;
import org.egov.prscp.web.models.RequestInfoWrapper;
import org.egov.prscp.web.models.ResponseInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/press")
public class EventPressMasterController {

	private final EventPressMasterService eventPressMasterService;

	@Autowired
	public EventPressMasterController(EventPressMasterService eventPressMasterService) {
		this.eventPressMasterService = eventPressMasterService;
	}

	@PostMapping(value = "/_create")
	public ResponseEntity<ResponseInfoWrapper> createPress(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		return eventPressMasterService.createPress(requestInfoWrapper);
	}

	@PostMapping(value = "/_update")
	public ResponseEntity<ResponseInfoWrapper> updatePress(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		return eventPressMasterService.updatePress(requestInfoWrapper);
	}

	@PostMapping(value = "/_get")
	public ResponseEntity<ResponseInfoWrapper> getPress(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		return eventPressMasterService.getPress(requestInfoWrapper);
	}

	@PostMapping(value = "/_delete")
	public ResponseEntity<ResponseInfoWrapper> deletePress(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		return eventPressMasterService.deletePress(requestInfoWrapper);
	}

}
