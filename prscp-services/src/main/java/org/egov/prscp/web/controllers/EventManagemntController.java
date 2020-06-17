package org.egov.prscp.web.controllers;

import javax.validation.Valid;

import org.egov.prscp.service.EventManagementService;
import org.egov.prscp.web.models.RequestInfoWrapper;
import org.egov.prscp.web.models.ResponseInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/event")
public class EventManagemntController {

	private final EventManagementService eventManagementService;

	@Autowired
	public EventManagemntController(EventManagementService eventManagementService) {
		this.eventManagementService = eventManagementService;
	}

	@PostMapping(value = "/_create")
	public ResponseEntity<ResponseInfoWrapper> createEvent(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestHeader("User-Agent") String request) {
		return eventManagementService.createEvent(requestInfoWrapper, request);
	}

	@PostMapping(value = "/_update")
	public ResponseEntity<ResponseInfoWrapper> updateEvent(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		return eventManagementService.updateEvent(requestInfoWrapper);
	}

	@PostMapping(value = "/_get")
	public ResponseEntity<ResponseInfoWrapper> getEvent(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		return eventManagementService.getEvent(requestInfoWrapper);
	}

	@PostMapping(value = "/_updateStatus")
	public ResponseEntity<ResponseInfoWrapper> updateEventStatus(@RequestBody RequestInfoWrapper requestInfoWrapper) {
		return eventManagementService.updateEventStatus(requestInfoWrapper);
	}
}
