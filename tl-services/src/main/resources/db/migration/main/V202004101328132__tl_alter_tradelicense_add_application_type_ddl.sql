ALTER TABLE eg_tl_TradeLicense
ADD COLUMN applicationtype character varying(64);

ALTER TABLE eg_tl_TradeLicense_audit
ADD COLUMN applicationtype character varying(64);