package org.egov.bookings.repository;

import java.util.Date;
import java.util.List;

import org.egov.bookings.model.RoomMasterModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommunityCenterRoomFeeRepository extends JpaRepository<RoomMasterModel, String>{
	
	RoomMasterModel findById(String bookingVenue);

	List<RoomMasterModel> findByTypeOfRoomAndTotalNumberOfRoomsAndSectorName(String typeOfRoom,
			String totalNoOfRooms, String sector);
	
	/**
	 * Find room master list.
	 *
	 * @param currentDate the current date
	 * @param communityCenterName the community center name
	 * @return the list
	 */
	@Query(
			value = "SELECT * FROM BK_ROOM_MASTER WHERE FROM_DATE <=cast((?1) AS timestamp) AND TO_DATE >= cast((?1) AS timestamp) "
					+ "AND COMMUNITY_CENTER_NAME = (?2) AND TYPE_OF_ROOM in ('AC','NON-AC')",
			nativeQuery = true )
			List<RoomMasterModel> findRoomMasterList( Date currentDate, String communityCenterName );


	
	
	
	/**
	 * Find by sector name and total number of rooms and type of room and community center name.
	 *
	 * @param sector the sector
	 * @param totalNumberOfRooms the total number of rooms
	 * @param typeOfRomm the type of romm
	 * @param communityCenterName the community center name
	 * @return the room master model
	 */
	List<RoomMasterModel> findBySectorNameAndTotalNumberOfRoomsAndTypeOfRoomAndCommunityCenterName(String sector,
			String totalNumberOfRooms, String typeOfRomm, String communityCenterName);
}
