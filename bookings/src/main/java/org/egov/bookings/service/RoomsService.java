package org.egov.bookings.service;

import java.math.BigDecimal;

import org.egov.bookings.model.BookingsModel;
import org.egov.bookings.web.models.BookingsRequest;
import org.springframework.stereotype.Service;

@Service
public interface RoomsService {

	BookingsModel createRoomForCommunityBooking(BookingsRequest bookingsRequest);

	BigDecimal getRoomAmount(BookingsRequest bookingsRequest);

	boolean isRoomBookingExists(String bkApplicationNumber);

	BookingsModel updateRoomForCommunityBooking(BookingsRequest bookingsRequest);

}
