package org.egov.prscp.web.controllers;

import javax.validation.Valid;

import org.egov.prscp.service.GeneratePressNotesService;
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
@RequestMapping("/v1/pressnote")
public class GeneratePressNotesController {

	private final GeneratePressNotesService generatePressNotesService;

	@Autowired
	public GeneratePressNotesController(GeneratePressNotesService generatePressNotesService) {
		this.generatePressNotesService = generatePressNotesService;
	}

	@PostMapping(value = "/_create")
	public ResponseEntity<ResponseInfoWrapper> createPressNote(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestHeader("User-Agent") String request) {
		return generatePressNotesService.createPressNote(requestInfoWrapper, request);

	}

	@PostMapping(value = "/_get")
	public ResponseEntity<ResponseInfoWrapper> getPressNote(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		return generatePressNotesService.getPressNote(requestInfoWrapper);
	}

	@PostMapping(value = "/_update")
	public ResponseEntity<ResponseInfoWrapper> updatePressNote(
			@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		return generatePressNotesService.updatePressNote(requestInfoWrapper);
	}

}
