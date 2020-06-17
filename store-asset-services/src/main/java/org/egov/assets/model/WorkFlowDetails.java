package org.egov.assets.model;

import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An Object to hold the Application workflow details for a given Object like
 * Indent, Material Transfer, Purchase Order, Material Issue, Outward Notice,
 * Inward Notice etc.
 */
@javax.annotation.Generated(value = "org.egov.inv.codegen.languages.SpringCodegen", date = "2017-11-08T13:51:07.770Z")

public class WorkFlowDetails {
	@JsonProperty("type")
	private String type = null;

	@JsonProperty("businessKey")
	private String businessKey = null;

	@JsonProperty("department")
	private String department = null;

	@JsonProperty("designation")
	private String designation = null;

	@JsonProperty("assignee")
	private Long assignee = null;

	@JsonProperty("action")
	private String action = null;

	@JsonProperty("status")
	private String status = null;

	@JsonProperty("comments")
	private String comments = null;

	@JsonProperty("senderName")
	private String senderName = null;

	@JsonProperty("details")
	private String details = null;

	@JsonProperty("workFlowDetails")
	private WorkFlowDetails workFlowDetails = null;

	@JsonProperty("stateId")
	private String stateId = null;

	public WorkFlowDetails type(String type) {
		this.type = type;
		return this;
	}

	/**
	 * Workflow type to be processed for an application the Inventory Object
	 * 
	 * @return type
	 **/

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public WorkFlowDetails businessKey(String businessKey) {
		this.businessKey = businessKey;
		return this;
	}

	/**
	 * Businesskey of the workflow processing application
	 * 
	 * @return businessKey
	 **/

	public String getBusinessKey() {
		return businessKey;
	}

	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}

	public WorkFlowDetails department(String department) {
		this.department = department;
		return this;
	}

	/**
	 * Department of the workflow processing authority
	 * 
	 * @return department
	 **/

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public WorkFlowDetails designation(String designation) {
		this.designation = designation;
		return this;
	}

	/**
	 * Designation of the workflow processing authority
	 * 
	 * @return designation
	 **/

	@Size(min = 3, max = 1024)
	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public WorkFlowDetails assignee(Long assignee) {
		this.assignee = assignee;
		return this;
	}

	/**
	 * Assignee id
	 * 
	 * @return assignee
	 **/

	public Long getAssignee() {
		return assignee;
	}

	public void setAssignee(Long assignee) {
		this.assignee = assignee;
	}

	public WorkFlowDetails action(String action) {
		this.action = action;
		return this;
	}

	/**
	 * Action to be taken for processing workflow
	 * 
	 * @return action
	 **/

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public WorkFlowDetails status(String status) {
		this.status = status;
		return this;
	}

	/**
	 * Status of the application
	 * 
	 * @return status
	 **/
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public WorkFlowDetails comments(String comments) {
		this.comments = comments;
		return this;
	}

	/**
	 * Comments given by the workflow processing person
	 * 
	 * @return comments
	 **/
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public WorkFlowDetails senderName(String senderName) {
		this.senderName = senderName;
		return this;
	}

	/**
	 * Name of the workflow processing person
	 * 
	 * @return senderName
	 **/
	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public WorkFlowDetails details(String details) {
		this.details = details;
		return this;
	}

	/**
	 * Details about the workflow process
	 * 
	 * @return details
	 **/
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public WorkFlowDetails workFlowDetails(WorkFlowDetails workFlowDetails) {
		this.workFlowDetails = workFlowDetails;
		return this;
	}

	/**
	 * Get workFlowDetails
	 * 
	 * @return workFlowDetails
	 **/
	@Valid
	public WorkFlowDetails getWorkFlowDetails() {
		return workFlowDetails;
	}

	public void setWorkFlowDetails(WorkFlowDetails workFlowDetails) {
		this.workFlowDetails = workFlowDetails;
	}

	public WorkFlowDetails stateId(String stateId) {
		this.stateId = stateId;
		return this;
	}

	/**
	 * State id of the workflow
	 * 
	 * @return stateId
	 **/
	public String getStateId() {
		return stateId;
	}

	public void setStateId(String stateId) {
		this.stateId = stateId;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		WorkFlowDetails workFlowDetails = (WorkFlowDetails) o;
		return Objects.equals(this.type, workFlowDetails.type)
				&& Objects.equals(this.businessKey, workFlowDetails.businessKey)
				&& Objects.equals(this.department, workFlowDetails.department)
				&& Objects.equals(this.designation, workFlowDetails.designation)
				&& Objects.equals(this.assignee, workFlowDetails.assignee)
				&& Objects.equals(this.action, workFlowDetails.action)
				&& Objects.equals(this.status, workFlowDetails.status)
				&& Objects.equals(this.comments, workFlowDetails.comments)
				&& Objects.equals(this.senderName, workFlowDetails.senderName)
				&& Objects.equals(this.details, workFlowDetails.details)
				&& Objects.equals(this.workFlowDetails, workFlowDetails.workFlowDetails)
				&& Objects.equals(this.stateId, workFlowDetails.stateId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, businessKey, department, designation, assignee, action, status, comments, senderName,
				details, workFlowDetails, stateId);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class WorkFlowDetails {\n");

		sb.append("    type: ").append(toIndentedString(type)).append("\n");
		sb.append("    businessKey: ").append(toIndentedString(businessKey)).append("\n");
		sb.append("    department: ").append(toIndentedString(department)).append("\n");
		sb.append("    designation: ").append(toIndentedString(designation)).append("\n");
		sb.append("    assignee: ").append(toIndentedString(assignee)).append("\n");
		sb.append("    action: ").append(toIndentedString(action)).append("\n");
		sb.append("    status: ").append(toIndentedString(status)).append("\n");
		sb.append("    comments: ").append(toIndentedString(comments)).append("\n");
		sb.append("    senderName: ").append(toIndentedString(senderName)).append("\n");
		sb.append("    details: ").append(toIndentedString(details)).append("\n");
		sb.append("    workFlowDetails: ").append(toIndentedString(workFlowDetails)).append("\n");
		sb.append("    stateId: ").append(toIndentedString(stateId)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}
