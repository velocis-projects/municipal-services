package org.egov.prscp.web.controllers;

import javax.validation.Valid;

import org.egov.prscp.service.NotificationTemplateService;
import org.egov.prscp.web.models.RequestInfoWrapper;
import org.egov.prscp.web.models.ResponseInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/template")
public class NotificationTemplateController {

	private final NotificationTemplateService notificationTemplateService;

	@Autowired
	public NotificationTemplateController(NotificationTemplateService notificationTemplateService) {
		this.notificationTemplateService = notificationTemplateService;
	}

	@PostMapping(value = "/_gettemplate")
	public ResponseEntity<ResponseInfoWrapper> getTemplate(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		return notificationTemplateService.getTemplate(requestInfoWrapper);
	}

	@PostMapping(value = "/_resend")
	public ResponseEntity<ResponseInfoWrapper> resendInvitation(
			@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		return notificationTemplateService.resendInvitation(requestInfoWrapper);
	}
}
