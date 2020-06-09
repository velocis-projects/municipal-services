package org.egov.prscp.web.controllers;

import java.io.IOException;

import javax.validation.Valid;

import org.egov.prscp.service.EventInvitationService;
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
@RequestMapping("/v1/invitation")
public class EventInvitationController {

	private final EventInvitationService eventInvitationService;

	@Autowired
	public EventInvitationController(EventInvitationService eventInvitationService) {
		this.eventInvitationService = eventInvitationService;
	}

	@PostMapping(value = "/guest/_upload")
	public ResponseEntity<ResponseInfoWrapper> uplaodExternalUser(
			@Valid @RequestBody RequestInfoWrapper requestInfoWrapper, @RequestHeader("User-Agent") String reques)
			throws IOException {
		return eventInvitationService.uplaodExternalGuest(requestInfoWrapper, reques);
	}

	@PostMapping(value = "/guest/_add")
	public ResponseEntity<ResponseInfoWrapper> addGuest(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestHeader("User-Agent") String reques) {
		return eventInvitationService.addGuest(requestInfoWrapper, reques);
	}

	@PostMapping(value = "/guest/_delete")
	public ResponseEntity<ResponseInfoWrapper> deleteGuest(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		return eventInvitationService.deleteGuest(requestInfoWrapper);
	}

	@PostMapping(value = "/guest/_get")
	public ResponseEntity<ResponseInfoWrapper> getGuest(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		return eventInvitationService.getGuest(requestInfoWrapper);
	}

	@PostMapping(value = "/_send")
	public ResponseEntity<ResponseInfoWrapper> sendInvitations(
			@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		return eventInvitationService.sendInvitations(requestInfoWrapper);
	}

}
