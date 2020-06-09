CREATE TABLE eg_ctl_billingslab_audit (
tenantid varchar,
id varchar,
businessService varchar,
applicationtype varchar,
feetype varchar,
uom varchar,
fromUom numeric(12,2),
toUom numeric(12,2),
rate numeric(12,2),
createdtime bigint,
createdby varchar,
lastmodifiedtime bigint,
lastmodifiedby varchar 
);