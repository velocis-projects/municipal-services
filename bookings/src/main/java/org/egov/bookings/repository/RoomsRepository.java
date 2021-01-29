package org.egov.bookings.repository;

import org.egov.bookings.model.RoomsModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomsRepository extends JpaRepository<RoomsModel, String>{

	RoomsModel findByRoomApplicationNumber(String roomApplicationNumber);

}
