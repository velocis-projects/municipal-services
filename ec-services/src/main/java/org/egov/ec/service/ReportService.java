package org.egov.ec.service;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.ec.config.EcConstants;
import org.egov.ec.repository.ReportRepository;
import org.egov.ec.web.models.DashboardDetails;
import org.egov.ec.web.models.Report;
import org.egov.ec.web.models.RequestInfoWrapper;
import org.egov.ec.web.models.ResponseInfoWrapper;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReportService {
	private ReportRepository repository;
	private final ObjectMapper objectMapper;

	@Autowired
	public ReportService(ObjectMapper objectMapper, ReportRepository repository) {
		this.objectMapper = objectMapper;
		this.repository = repository;

	}

	/**
	*This method will fetch report data based on search criteria
	*
	* @param RequestInfoWrapper SearchCriteria
	* @return ResponseInfoWrapper containing list of records for respective report format
	* @throws CustomException REPORT_GET_EXCEPTION
	*/
	public ResponseEntity<ResponseInfoWrapper> getReport(RequestInfoWrapper requestInfoWrapper) {
		try {
			Report reportData = objectMapper.convertValue(requestInfoWrapper.getRequestBody(), Report.class);

			List<Report> reportDetails = repository.getReport(reportData);
			return new ResponseEntity<>(ResponseInfoWrapper.builder()
					.responseInfo(ResponseInfo.builder().status(EcConstants.STATUS_SUCCESS).build()).responseBody(reportDetails).build(),
					HttpStatus.OK);
		} catch (Exception e) {
			log.error("Report Service - Get Report Exception"+e.getMessage());
			throw new CustomException("REPORT_GET_EXCEPTION", e.getMessage());
		}
	}

	/**
	*This method will fetch dashboard data against respective role
	*
	* @param RequestInfoWrapper SearchCriteria
	* @return ResponseInfoWrapper containing count of various modules
	* @throws CustomException REPORT_GETDASHBOARD_EXCEPTION
	*/
	public ResponseEntity<ResponseInfoWrapper> getDashboard(RequestInfoWrapper requestInfoWrapper) {
		try {
			
			List<DashboardDetails> reportDetails = repository.getDashboard(requestInfoWrapper);
			return new ResponseEntity<>(ResponseInfoWrapper.builder()
					.responseInfo(ResponseInfo.builder().status(EcConstants.STATUS_SUCCESS).build()).responseBody(reportDetails).build(),
					HttpStatus.OK);
		} catch (Exception e) {
			log.error("Report Service - Get Dashboard Exception"+e.getMessage());
			throw new CustomException("REPORT_GETDASHBOARD_EXCEPTION", e.getMessage());
		}
	}

}
