package org.egov.bookings.controller;

import org.egov.bookings.common.model.ResponseModel;
import org.egov.bookings.model.BookingsModel;
import org.egov.bookings.service.RoomsService;
import org.egov.bookings.validator.BookingsFieldsValidator;
import org.egov.bookings.web.models.BookingsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/community/room")
public class RoomsController {

	@Autowired
	private BookingsFieldsValidator bookingsFieldsValidator;

	@Autowired
	private RoomsService roomsService;
	
	@PostMapping("_create")
	private ResponseEntity<?> createRoomForCommunityBooking(@RequestBody BookingsRequest bookingsRequest) {

		bookingsFieldsValidator.validatePAndCBookingRequest(bookingsRequest);
		BookingsModel bookingsModel = roomsService.createRoomForCommunityBooking(bookingsRequest);
		ResponseModel rs = new ResponseModel();
		if (bookingsModel == null) {
			rs.setStatus("400");
			rs.setMessage("Error while Creating Room fo Community Booking");
			rs.setData(bookingsModel);
		} else {
			rs.setStatus("200");
			rs.setMessage("Community Room Booking Created Successfully");
			rs.setData(bookingsModel);
		}

		return ResponseEntity.ok(rs);
	}

	
	@PostMapping("_update")
	private ResponseEntity<?> updateRoomForCommunityBooking(@RequestBody BookingsRequest bookingsRequest) {

		bookingsFieldsValidator.validatePAndCBookingRequest(bookingsRequest);
		BookingsModel bookingsModel = roomsService.updateRoomForCommunityBooking(bookingsRequest);
		ResponseModel rs = new ResponseModel();
		if (bookingsModel == null) {
			rs.setStatus("400");
			rs.setMessage("Error while Creating Room fo Community Booking");
			rs.setData(bookingsModel);
		} else {
			rs.setStatus("200");
			rs.setMessage("Community Room Booking Created Successfully");
			rs.setData(bookingsModel);
		}

		return ResponseEntity.ok(rs);
	}

	
}
