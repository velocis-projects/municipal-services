package org.egov.tl.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class EmailRequest {
    private String email;
    private String subject;
    private String body;
    @JsonProperty("isHTML")
    private boolean isHTML;
}
