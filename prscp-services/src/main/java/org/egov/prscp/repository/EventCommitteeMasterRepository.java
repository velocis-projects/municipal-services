package org.egov.prscp.repository;

import java.util.List;

import org.egov.prscp.repository.builder.PrQueryBuilder;
import org.egov.prscp.repository.rowmapper.CommitteeMasterRowMapper;
import org.egov.prscp.web.models.CommitteeDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class EventCommitteeMasterRepository {

	private JdbcTemplate jdbcTemplate;

	private CommitteeMasterRowMapper rowMapper;

	@Autowired
	public EventCommitteeMasterRepository(JdbcTemplate jdbcTemplate, CommitteeMasterRowMapper rowMapper) {
		this.jdbcTemplate = jdbcTemplate;
		this.rowMapper = rowMapper;
	}

	public List<CommitteeDetail> getCommittee(CommitteeDetail committeeDetail) {
		return jdbcTemplate.query(PrQueryBuilder.GET_COMMITTEE_DEATILS,
				new Object[] { committeeDetail.getTenantId(), committeeDetail.getModuleCode(),
						committeeDetail.getCommitteeUuid(), committeeDetail.getCommitteeUuid(),
						committeeDetail.getCommitteeName(), committeeDetail.getCommitteeName() },
				rowMapper);
	}

	public List<CommitteeDetail> getCommitteeByName(CommitteeDetail committeeDetail) {
		return jdbcTemplate.query(PrQueryBuilder.GET_COMMITTEE_DEATILS,
				new Object[] { committeeDetail.getTenantId(), committeeDetail.getModuleCode(), "", "",
						committeeDetail.getCommitteeName(), committeeDetail.getCommitteeName() },
				rowMapper);
	}

}
