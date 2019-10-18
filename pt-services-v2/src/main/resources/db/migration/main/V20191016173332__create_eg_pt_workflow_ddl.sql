CREATE TABLE eg_pt_workflow_v2(

  id character varying(128),
  tenantId character varying(128),
  action character varying(256),
  status character varying(256),
  applicationNumber character varying(128),
  workflowName character varying(256),
  active boolean,
  createdby character varying(128),
  createdtime bigint,
  lastmodifiedby character varying(128),
  lastmodifiedtime bigint,

  CONSTRAINT pk_eg_pt_workflow_v2 PRIMARY KEY (applicationNumber),
  CONSTRAINT uk_eg_pt_workflow_v2 UNIQUE (id)
);