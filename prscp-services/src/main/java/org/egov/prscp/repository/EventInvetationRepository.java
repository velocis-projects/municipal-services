package org.egov.prscp.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.egov.prscp.config.PrScpConfiguration;
import org.egov.prscp.producer.Producer;
import org.egov.prscp.repository.builder.PrQueryBuilder;
import org.egov.prscp.repository.rowmapper.InvitationGuestRowMapper;
import org.egov.prscp.repository.rowmapper.NotificationTemplateRowMapper;
import org.egov.prscp.web.models.EventDetail;
import org.egov.prscp.web.models.InviteGuest;
import org.egov.prscp.web.models.NotificationReceiver;
import org.egov.prscp.web.models.NotificationTemplate;
import org.egov.prscp.web.models.RequestInfoWrapper;
import org.egov.prscp.web.models.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class EventInvetationRepository {

	private JdbcTemplate jdbcTemplate;

	private InvitationGuestRowMapper invitationGuestRowMapper;

	private NotificationTemplateRowMapper notificationTemplateRowMapper;

	private Producer producer;

	private PrScpConfiguration config;

	@Autowired
	public EventInvetationRepository(JdbcTemplate jdbcTemplate, Producer producer, PrScpConfiguration config,
			NotificationTemplateRowMapper notificationTemplateRowMapper,
			InvitationGuestRowMapper invitationGuestRowMapper) {
		this.jdbcTemplate = jdbcTemplate;
		this.producer = producer;
		this.config = config;
		this.notificationTemplateRowMapper = notificationTemplateRowMapper;
		this.invitationGuestRowMapper = invitationGuestRowMapper;
	}

	public List<InviteGuest> saveGuest(List<InviteGuest> inviteGuests, String tenantId, String eventDetailUuid,
			String userId, String moduleCode) {

		List<InviteGuest> existingList = jdbcTemplate.query(PrQueryBuilder.GET_INVITATION_GUEST,
				new Object[] { tenantId, moduleCode, eventDetailUuid, userId }, invitationGuestRowMapper);

		List<InviteGuest> existing = existingList.stream()
				.filter(exits -> inviteGuests.stream()
						.filter(nwList -> (exits.getGuestEmail().equalsIgnoreCase(nwList.getGuestEmail())
								&& exits.getGuestMobile().equalsIgnoreCase(nwList.getGuestMobile())
								&& exits.getGuestName().equalsIgnoreCase(nwList.getGuestName())
								&& exits.getEventGuestType().equalsIgnoreCase(nwList.getEventGuestType()))
								&& exits.getModuleCode().equalsIgnoreCase(nwList.getModuleCode()))
						.findFirst().isPresent())
				.collect(Collectors.toList());

		existingList.stream().forEach(
				exits -> inviteGuests.removeIf(nwList -> (exits.getGuestEmail().equalsIgnoreCase(nwList.getGuestEmail())
						&& exits.getGuestMobile().equalsIgnoreCase(nwList.getGuestMobile())
						&& exits.getGuestName().equalsIgnoreCase(nwList.getGuestName())
						&& exits.getEventGuestType().equalsIgnoreCase(nwList.getEventGuestType())
						&& exits.getModuleCode().equalsIgnoreCase(nwList.getModuleCode()))));

		RequestInfoWrapper infoWrapper = RequestInfoWrapper.builder().requestBody(inviteGuests).build();
		producer.push(config.getInvitationSaveGuestTopic(), infoWrapper);

		inviteGuests.addAll(existing);
		return inviteGuests;
	}

	public void deleteGuest(List<InviteGuest> inviteGuest) {
		RequestInfoWrapper infoWrapper = RequestInfoWrapper.builder().requestBody(inviteGuest).build();
		producer.push(config.getInvitationDeleteGuestTopic(), infoWrapper);
	}

	public List<InviteGuest> getGuest(InviteGuest inviteGuests) {
		return jdbcTemplate.query(PrQueryBuilder.GET_INVITATION_GUEST_LIST,
				new Object[] { inviteGuests.getTenantId(), inviteGuests.getModuleCode(),
						inviteGuests.getEventDetailUuid(), inviteGuests.getCreatedBy(), inviteGuests.getCreatedBy(),
						inviteGuests.isSentFlag(), inviteGuests.isSentFlag() },
				invitationGuestRowMapper);
	}

	public List<InviteGuest> getGuestReminder(InviteGuest inviteGuests) {
		return jdbcTemplate.query(PrQueryBuilder.GET_INVITATION_GUEST_LIST_REMINDER, new Object[] {
				inviteGuests.getTenantId(), inviteGuests.getModuleCode(), inviteGuests.getEventDetailUuid() },
				invitationGuestRowMapper);
	}

	public NotificationTemplate getTemplate(Template template) {
		return jdbcTemplate.query(PrQueryBuilder.GET_TEMPLATE, new Object[] { template.getTenantId(),
				template.getModuleCode(), template.getEventDetailUuid(), template.getTemplateType() },
				notificationTemplateRowMapper);
	}

	public void saveTemplate(NotificationTemplate notificationTemplate) {
		RequestInfoWrapper infoWrapper = RequestInfoWrapper.builder().requestBody(notificationTemplate).build();
		producer.push(config.getInvitationSaveTemplateTopic(), infoWrapper);
	}

	public void updateTemplate(NotificationTemplate notificationTemplate) {
		RequestInfoWrapper infoWrapper = RequestInfoWrapper.builder().requestBody(notificationTemplate).build();
		producer.push(config.getInvitationUpdateTemplateTopic(), infoWrapper);
	}

	public void updateTemplateOfEvent(InviteGuest inviteGuest) {
		RequestInfoWrapper infoWrapper = RequestInfoWrapper.builder().requestBody(inviteGuest).build();
		producer.push(config.getInvitationUpdateEventTemplateTopic(), infoWrapper);
	}

	public void updateEventNotification(EventDetail eventDetail) {
		RequestInfoWrapper infoWrapper = RequestInfoWrapper.builder().requestBody(eventDetail).build();
		producer.push(config.getInvitationUpdateEventNotificaitonTopic(), infoWrapper);
	}

	public void sendInvitation(NotificationReceiver notificationReceiver) {
		producer.push(config.getInvitationSendTopic(), notificationReceiver);
	}
}
