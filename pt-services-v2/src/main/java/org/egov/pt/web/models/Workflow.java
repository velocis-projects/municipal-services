package org.egov.pt.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Workflow {

    @Size(max=64)
    @JsonProperty("id")
    private String id;

    @Size(max=64)
    @JsonProperty("action")
    private String action;

    @Size(max=64)
    @JsonProperty("status")
    private String status;

    @Size(max=64)
    @JsonProperty("applicationNumber")
    private String applicationNumber;

    @Size(max=64)
    @JsonProperty("workflowName")
    private String workflowName;

    @Valid
    @JsonProperty("documents")
    private List<Document> documents;

    @JsonProperty("additionalDetails")
    private JsonNode additionalDetails;

    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;


    public Workflow addDocumentsItem(Document document){
        if(this.documents==null){
            this.documents = new LinkedList<>();
        }
        this.documents.add(document);
        return this;
    }


    public Boolean isNull(){
        if(StringUtils.isEmpty(this.id) && StringUtils.isEmpty(this.action) && StringUtils.isEmpty(this.status)
                && StringUtils.isEmpty(this.applicationNumber) && CollectionUtils.isEmpty(documents)
                && (this.additionalDetails==null || this.additionalDetails.isNull()))
                return true;
        else return false;
    }

}
