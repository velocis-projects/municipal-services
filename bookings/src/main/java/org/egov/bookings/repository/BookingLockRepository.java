package org.egov.bookings.repository;

import java.util.List;

import org.egov.bookings.model.BookingLockModel;
import org.egov.bookings.repository.querybuilder.BookingsQueryBuilder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingLockRepository extends CrudRepository<BookingLockModel, String> {

	long deleteByApplicationNumber(String bkApplicationNumber);

	@Query(value = BookingsQueryBuilder.FETCH_LOCK_DATES, nativeQuery = true)
	List<BookingLockModel> fetchLockedDates(String bkBookingVenue, String bkBookingType,
			String bkSector);

}
