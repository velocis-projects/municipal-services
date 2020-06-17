package org.egov.prscp.web.models;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class MapPressNote {

	@Size(max = 64)
	@NotNull
	@JsonProperty("mapPressNoteUuid")
	private String mapPressNoteUuid;

	@Size(max = 64)
	@NotNull
	@JsonProperty("pressMasterUuid")
	private String pressMasterUuid;

	@Size(max = 64)
	@NotNull
	@JsonProperty("pressNoteUuid")
	private String pressNoteUuid;

	@Size(max = 64)
	@NotNull
	@JsonProperty("isActive")
	private String isActive;

	@Size(max = 256)
	@JsonProperty("tenantId")
	private String tenantId;
}
