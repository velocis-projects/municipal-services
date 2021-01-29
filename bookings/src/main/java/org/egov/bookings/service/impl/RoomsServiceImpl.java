package org.egov.bookings.service.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.egov.bookings.config.BookingsConfiguration;
import org.egov.bookings.contract.BookingsRequestKafka;
import org.egov.bookings.model.BookingsModel;
import org.egov.bookings.model.RoomMasterModel;
import org.egov.bookings.model.RoomsModel;
import org.egov.bookings.producer.BookingsProducer;
import org.egov.bookings.repository.CommunityCenterRoomFeeRepository;
import org.egov.bookings.repository.RoomsRepository;
import org.egov.bookings.service.RoomsService;
import org.egov.bookings.validator.BookingsFieldsValidator;
import org.egov.bookings.web.models.BookingsRequest;
import org.egov.bookings.workflow.WorkflowIntegrator;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class RoomsServiceImpl implements RoomsService {

	@Autowired
	private EnrichmentService enrichmentService;

	@Autowired
	private CommunityCenterRoomFeeRepository communityCenterRoomFeeRepository;

	@Autowired
	private BookingsConfiguration config;

	@Autowired
	private WorkflowIntegrator workflowIntegrator;

	@Autowired
	private BookingsProducer bookingsProducer;
	
	@Autowired
	private RoomsRepository roomsRepository; 

	@Override
	public BookingsModel createRoomForCommunityBooking(BookingsRequest bookingsRequest) {
		boolean flag = isRoomBookingExists(bookingsRequest.getBookingsModel().getRoomsModel().get(0).getRoomApplicationNumber());
		if (!flag)
			enrichmentService.enrichRoomForCommunityBookingRequest(bookingsRequest);
		enrichmentService.enrichRoomDetails(bookingsRequest);
		enrichmentService.generateDemandForRoom(bookingsRequest);
		if (config.getIsExternalWorkFlowEnabled()) {
			if (!flag)
			workflowIntegrator.callRoomWorkFlow(bookingsRequest);
		}
		BookingsRequestKafka kafkaBookingRequest = enrichmentService.enrichForKafka(bookingsRequest);
		if(!flag)
		bookingsProducer.push(config.getSaveRoomDetails(), kafkaBookingRequest);
		else {
			bookingsProducer.push(config.getUpdateRoomDetails(), kafkaBookingRequest);	
		}
		return bookingsRequest.getBookingsModel();
	}

	@Override
	public BigDecimal getRoomAmount(BookingsRequest bookingsRequest) {
		List<RoomMasterModel> roomMasterModelList = null;
		LocalDate currentDate = LocalDate.now();
		BigDecimal amount = null;
		RoomsModel roomsModel = bookingsRequest.getBookingsModel().getRoomsModel().get(0);
		String typeOfRoom = roomsModel.getTypeOfRoom();
		String totalNoOfRooms = roomsModel.getTotalNoOfRooms();
		String sector = bookingsRequest.getBookingsModel().getBkSector();
		roomMasterModelList = communityCenterRoomFeeRepository
				.findByTypeOfRoomAndTotalNumberOfRoomsAndSectorName(typeOfRoom, totalNoOfRooms, sector);
		if (BookingsFieldsValidator.isNullOrEmpty(roomMasterModelList)) {
			throw new CustomException("DATA_NOT_FOUND",
					"There is not any amount for this room body criteria in database");
		}

		for (RoomMasterModel roomMasterModel : roomMasterModelList) {
			if (BookingsFieldsValidator.isNullOrEmpty(roomMasterModel.getFromDate())) {
				throw new CustomException("DATA_NOT_FOUND", "There is no from date for this room criteria in database");
			}
			String pattern = "yyyy-MM-dd";
			DateFormat df = new SimpleDateFormat(pattern);
			String fromDateInString = df.format(roomMasterModel.getFromDate());
			LocalDate fromDate = LocalDate.parse(fromDateInString);
			// LocalDate toDate = LocalDate.parse(toDateInString);
			if (BookingsFieldsValidator.isNullOrEmpty(roomMasterModel.getToDate()) && currentDate.isAfter(fromDate)
					|| currentDate.isEqual(fromDate)) {
				// toDateInString = df.format(osbmFeeModel1.getToDate());
				amount = new BigDecimal(roomMasterModel.getRentFor3Hrs());
			}
			if (!BookingsFieldsValidator.isNullOrEmpty(roomMasterModel.getToDate())
					&& (fromDate.isEqual(currentDate) || fromDate.isBefore(currentDate))
					&& (currentDate.isBefore(LocalDate.parse(df.format(roomMasterModel.getToDate()))))) {
				amount = new BigDecimal(roomMasterModel.getRentFor3Hrs());
				break;
			}
		}

		return amount;
	}

	@Override
	public boolean isRoomBookingExists(String roomApplicationNumber) {
		RoomsModel roomsModel = roomsRepository.findByRoomApplicationNumber(roomApplicationNumber);

		if (null == roomsModel) {
			return false;
		} else {
			return true;
		}

	}
	
	
	@Override
	public BookingsModel updateRoomForCommunityBooking(BookingsRequest bookingsRequest) {
		boolean flag = isRoomBookingExists(bookingsRequest.getBookingsModel().getRoomsModel().get(0).getRoomApplicationNumber());
		bookingsRequest.getBookingsModel().getRoomsModel().get(0)
				.setRoomBusinessService(bookingsRequest.getBookingsModel().getRoomBusinessService());
		if (config.getIsExternalWorkFlowEnabled()) {
			workflowIntegrator.callRoomWorkFlow(bookingsRequest);
		}
		BookingsRequestKafka kafkaBookingRequest = enrichmentService.enrichForKafka(bookingsRequest);
			bookingsProducer.push(config.getUpdateRoomDetails(), kafkaBookingRequest);	
		//br.save(bookingsRequest.getBookingsModel());
		return bookingsRequest.getBookingsModel();
	}
	
	
	}
