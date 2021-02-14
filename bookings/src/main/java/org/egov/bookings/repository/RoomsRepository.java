package org.egov.bookings.repository;

import java.util.List;

import org.egov.bookings.model.RoomsModel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The Interface RoomsRepository.
 */
public interface RoomsRepository extends JpaRepository<RoomsModel, String>{

	/**
	 * Find by room application number.
	 *
	 * @param roomApplicationNumber the room application number
	 * @return the rooms model
	 */
	List<RoomsModel> findByRoomApplicationNumber(String roomApplicationNumber);
	
	/**
	 * Find by community application number.
	 *
	 * @param applicationNumber the application number
	 * @return the list
	 */
	public List<RoomsModel> findByCommunityApplicationNumber(String applicationNumber);

}
