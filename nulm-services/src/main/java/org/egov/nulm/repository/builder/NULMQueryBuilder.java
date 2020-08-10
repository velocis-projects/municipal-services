package org.egov.nulm.repository.builder;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NULMQueryBuilder {
	public static final String GET_SEP_APPLICATION_QUERY = "SELECT  NA.application_uuid,  NA.application_id,  NA.nulm_application_id,  NA.application_status, \n" + 
			"        NA.name,  NA.gender,  NA.age,  NA.dob,  NA.adhar_no,  NA.mother_name,  NA.father_or_husband_name, \n" + 
			"        NA.occupation,  NA.address,  NA.contact,  NA.since_how_long_in_chandigarh,  NA.qualification, \n" + 
			"        NA.category,  NA.is_urban_poor,  NA.is_minority,  NA.is_handicapped,  NA.is_loan_from_bankinginstitute, \n" + 
			"        NA.is_repayment_made,  NA.bpl_no,  NA.minority,  NA.type_of_business_to_be_started, \n" + 
			"        NA.previous_experience,  NA.place_of_work,  NA.bank_details,  NA.no_of_family_members, \n" + 
			"        NA.project_cost,  NA.loan_amount,  NA.recommended_amount,  NA.recommended_by, \n" + 
			"        NA.representative_name,  NA.representative_address,  NA.tenant_id,NA.is_active,NA.created_by ,NA.created_time,NA.last_modified_by, NA.last_modified_time,\n" + 
			"        array_to_json(array_agg(json_build_object('documentType',ND.document_type,'filestoreId',ND.filestore_id,'documnetUuid',ND.document_uuid,'isActive',ND.is_active,\n" + 
			"        'tenantId',ND.tenant_id,'applicationUuid',ND.application_uuid) ))as document \n" + 
			"  FROM public.nulm_sep_application_detail NA inner  join nulm_sep_application_document ND on NA.application_uuid=ND.application_uuid and NA.tenant_id=ND.tenant_id\n" + 
			"  where NA.application_id=(case when ?  <>'' then ?  else NA.application_id end) and NA.created_by=(case when ?  <>'' then ?  else NA.created_by end) AND NA.tenant_id=? "
			+ "AND NA.application_status=(case when ?  <>'' then ?  else NA.application_status end) \n" + 
			"  AND NA.is_active='true' AND ND.is_active='true' AND  TO_DATE(TO_CHAR(TO_TIMESTAMP(NA.created_time / 1000), 'YYYY-MM-DD'),'YYYY-MM-DD') >= CASE WHEN ?<>'' THEN DATE(?) ELSE \n" + 
			" TO_DATE(TO_CHAR(TO_TIMESTAMP(NA.created_time / 1000), 'YYYY-MM-DD'),'YYYY-MM-DD') END \n" + 
			"AND  TO_DATE(TO_CHAR(TO_TIMESTAMP(NA.created_time / 1000), 'YYYY-MM-DD'),'YYYY-MM-DD') <= CASE WHEN ?<>'' THEN DATE(?) ELSE \n" + 
			" TO_DATE(TO_CHAR(TO_TIMESTAMP(NA.created_time / 1000), 'YYYY-MM-DD'),'YYYY-MM-DD') END aR group by NA.application_uuid    ORDER BY created_time desc";
	
	 public static final String GET_SEP_DOCUMENT_QUERY="SELECT count(*) \n" + 
	 		"        FROM public.nulm_sep_application_document \n" + 
	 		"        WHERE application_uuid=? and tenant_id=? and filestore_id=? and document_type=? and is_active='true'; ";
	 
	 public static final String GET_SMID_APPLICATION_QUERY="select * FROM nulm_smid_application_detail where application_uuid=(case when ?  <>'' then ?  else application_uuid end) and created_by=(case when ?  <>'' then ?  else created_by end) \n" + 
	 		"AND tenant_id=? AND application_status=(case when ?  <>'' then ?  else application_status end) AND is_active='true' AND  TO_DATE(TO_CHAR(TO_TIMESTAMP(created_time / 1000), 'YYYY-MM-DD'),'YYYY-MM-DD') >= CASE WHEN ?<>'' THEN DATE(?) ELSE \n" + 
	 		" TO_DATE(TO_CHAR(TO_TIMESTAMP(created_time / 1000), 'YYYY-MM-DD'),'YYYY-MM-DD') END \n" + 
	 		"AND  TO_DATE(TO_CHAR(TO_TIMESTAMP(created_time / 1000), 'YYYY-MM-DD'),'YYYY-MM-DD') <= CASE WHEN ?<>'' THEN DATE(?) ELSE \n" + 
	 		" TO_DATE(TO_CHAR(TO_TIMESTAMP(created_time / 1000), 'YYYY-MM-DD'),'YYYY-MM-DD') END  AND application_status<>?  ORDER BY created_time desc";
	 
	 public static final String SHG_UUID_EXIST_QUERY="select count(*) from nulm_smid_shg_detail where shg_uuid=? and tenant_id=? and is_active='true'";
	 
	 public static final String GET_SHG_QUERY="select GP.shg_uuid,GP.shg_id,GP.name,GP.type,GP.groups_nominated_by,GP.formed_through,GP.status,GP.address,GP.contact_no,GP.date_of_formation,GP.account_no,GP.date_of_opening_account,GP.bank_name,\n" + 
	 		"GP.branch_name,GP.main_activity,GP.remark,GP.tenant_id,GP.is_active,GP.created_by ,GP.created_time ,GP.last_modified_by,GP.last_modified_time,\n" + 
	 		"array_to_json(array_agg(json_build_object('applicationUuid',MB.application_uuid,'shgUuid',MB.shg_uuid,'applicationId',MB.application_id,'nulmApplicationId',MB.nulm_application_id,'applicationStatus',MB.application_status,\n" + 
	 		"'name',MB.name,'positionLevel',MB.position_level,'gender',MB.gender,'dob',MB.dob,'dateOfOpeningAccount',MB.date_of_opening_account,'adharNo',MB.adhar_no,'motherName',MB.mother_name,\n" + 
	 		"'fatherOrHusbandName',MB.father_or_husband_name,'address',MB.address,'phoneNo',MB.phone_no,'mobileNo',MB.mobile_no,'qualification',MB.qualification,'emailId',MB.email_id,\n" + 
	 		"'isUrbanPoor',MB.is_urban_poor,'isMinority',MB.is_minority,'isPwd',MB.is_pwd,'isStreetVendor',MB.is_street_vendor,'isHomeless',MB.is_homeless,'isInsurance',MB.is_insurance,\n" + 
	 		"'bplNo',MB.bpl_no,'minority',MB.minority,'caste',MB.caste,'wardNo',MB.ward_no,'nameAsPerAdhar',MB.name_as_per_adhar,'adharAcknowledgementNo',\n" + 
	 		"MB.adhar_acknowledgement_no,'insuranceThrough',MB.insurance_through,'documentAttachemnt',MB.document_attachemnt,'accountNo',MB.account_no,\n" + 
	 		"'bankName',MB.bank_name,'branchName',MB.branch_name,'remark',MB.remark,'tenantId',MB.tenant_id,'isActive',MB.is_active,'createdBy',MB.created_by,'createdTime',MB.created_time\n" + 
	 		",'lastModifiedBy',MB.last_modified_by,'lastModifiedTime',MB.last_modified_time) ))as member  from nulm_smid_shg_detail GP LEFT JOIN  nulm_smid_shg_member_details MB on GP.shg_uuid=MB.shg_uuid AND GP.tenant_id=MB.tenant_id\n" + 
	 		"  where ((GP.status='APPROVED' AND mb.application_status<>'CREATED') OR (GP.status<>'APPROVED'  and COALESCE(mb.application_status,'')=COALESCE(mb.application_status,'')) )  AND GP.created_by=(case when ?  <>'' then ? else GP.created_by end) and GP.tenant_id=? and GP.is_active='true'AND\n" + 
	 		"GP.status=(case when ?  <>'' then ? else GP.status end) AND GP.shg_uuid=(case when ?  <>'' then ? else GP.shg_uuid end) And GP.status<>? AND  GP.status<>?  AND  mb.application_status<>? AND  mb.application_status<>? \n" + 
	 		" GROUP BY GP.shg_uuid";
	 
	 
	 public static final String GET_SHG_MEMBER_QUERY= "SELECT MB.application_uuid, MB.shg_uuid, MB.application_id, MB.nulm_application_id,MB.application_status, MB.name, MB.position_level, MB.gender, MB.dob,\n" + 
	 		"MB.date_of_opening_account ,MB.adhar_no, MB.mother_name, MB.father_or_husband_name, MB.address, MB.phone_no,MB.mobile_no, MB.qualification, MB.email_id, MB.is_urban_poor, MB.is_minority,\n" + 
	 		"MB.is_pwd, MB.is_street_vendor, MB.is_homeless, MB.is_insurance, MB.bpl_no,MB.minority, MB.caste, MB.ward_no, MB.name_as_per_adhar, MB.adhar_acknowledgement_no,\n" + 
	 		"MB.insurance_through, document_attachemnt, MB.account_no, MB.bank_name,MB.branch_name, MB.remark, MB.tenant_id, MB.is_active, MB.created_by, MB.created_time,\n" + 
	 		"MB.last_modified_by, MB.last_modified_time ,\n" + 
	 		"array_to_json(array_agg(json_build_object('shgUuid',GP.shg_uuid,'shgId',GP.shg_id,'name',GP.name,'type',GP.type,'groupsNominatedBy',GP.groups_nominated_by,\n" + 
	 		"'formedThrough',GP.formed_through,'status',GP.status,'address',GP.address,'contactNo',GP.contact_no,'dateOfFormation',\n" + 
	 		"GP.date_of_formation,'accountNo',GP.account_no,'dateOfOpeningAccount',GP.date_of_opening_account,'bankName',GP.bank_name,'branchName',GP.branch_name,'mainActivity',GP.main_activity,'remark',GP.remark) ))as group \n" + 
	 		"FROM public.nulm_smid_shg_member_details MB Inner  join nulm_smid_shg_detail GP on GP.shg_uuid=MB.shg_uuid AND GP.tenant_id=MB.tenant_id\n" + 
	 		"WHERE application_uuid=(case when ?  <>'' then ?  else application_uuid end) and MB.created_by=(case when ?  <>'' then ?  else MB.created_by end)  \n" + 
	 		" AND MB.tenant_id=? AND MB.application_status=(case when ?  <>'' then ?  else MB.application_status end) AND MB.is_active='true'\n" + 
	 		" GROUP BY MB.application_uuid";
	 
	 public static final String GET_SHG_MEMBER_COUNT_QUERY="select count(*) from nulm_smid_shg_member_details where shg_uuid=? and tenant_id=? and is_active='true' and position_level='MEMBER'";
	 public static final String GET_ORGANIZATION_MOBILE_NO_QUERY="select count(*) from nulm_organization where tenant_id=? and is_active='true'and mobile_no=? ";
	 public static final String GET_ORGANIZATION_NAME_QUERY="select count(*) from nulm_organization where tenant_id=? and is_active='true'and organization_name=? ";
	 public static final String GET_ORGANIZATION_QUERY="SELECT organization_uuid, user_id, organization_name, address, email_id,representative_name, mobile_no, registration_no, tenant_id, is_active,created_by, created_time, last_modified_by, last_modified_time\n" + 
	 		"  FROM public.nulm_organization where tenant_id=? and is_active='true' and organization_uuid=(case when ?  <>'' then ?  else organization_uuid end)";
}
