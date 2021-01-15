package org.egov.bookings.repository;

import org.egov.bookings.model.RoomMasterModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityCenterRoomFeeRepository extends JpaRepository<RoomMasterModel, String>{
	
	RoomMasterModel findById(String bookingVenue);
}
