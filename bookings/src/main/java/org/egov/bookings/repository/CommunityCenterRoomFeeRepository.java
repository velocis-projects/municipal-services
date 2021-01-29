package org.egov.bookings.repository;

import java.util.List;

import org.egov.bookings.model.RoomMasterModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityCenterRoomFeeRepository extends JpaRepository<RoomMasterModel, String>{
	
	RoomMasterModel findById(String bookingVenue);

	List<RoomMasterModel> findByTypeOfRoomAndTotalNumberOfRoomsAndSectorName(String typeOfRoom,
			String totalNoOfRooms, String sector);
}
