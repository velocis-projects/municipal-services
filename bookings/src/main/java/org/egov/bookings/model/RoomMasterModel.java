package org.egov.bookings.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "RoomMasterModel")
@Table(name = "BK_ROOM_MASTER")
public class RoomMasterModel {

	@Id
	@Column(name = "ID")
	private String id;

	@Column(name = "sector_name")
	private String sectorName;

	@Column(name = "total_number_of_rooms")
	private String totalNumberOfRooms;

	@Column(name = "type_of_room")
	private String typeOfRoom;

	@Column(name = "rent_for_one_day")
	private String rentForOneDay;

	@Column(name = "rent_for_3_hrs")
	private String rentFor3Hrs;

	@Column(name = "rent_for_6_hrs")
	private String rentFor6Hrs;

	@Column(name = "rent_for_9_hrs")
	private String rentFor9Hrs;

	@Column(name = "FROM_DATE")
	private Date fromDate;

	@Column(name = "TO_DATE")
	private Date toDate;
}
