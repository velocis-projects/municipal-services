package org.egov.bookings.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "RoomsModel")
@Table(name = "BK_ROOMS_MODEL")
public class RoomsModel {
	@Id
	@Column(name = "ROOM_APPLICATION_NUMBER")
	private String roomApplicationNumber;

	@Column(name = "TYPE_OF_ROOM")
	private String typeOfRoom;

	@Column(name = "TOTAL_NO_OF_ROOMS")
	private String totalNoOfRooms;

	@Column(name = "COMMUNITY_APPLICATION_NUMBER")
	private String communityApplicationNumber;

	@Column(name = "ROOM_APPLICATION_STATUS")
	private String roomApplicationStatus;

	@Column(name = "ROOM_BUSINESS_SERVICE")
	private String roomBusinessService;

	@Column(name = "REMARKS")
	private String remarks;

	@Column(name = "ACTION")
	private String action;

}
