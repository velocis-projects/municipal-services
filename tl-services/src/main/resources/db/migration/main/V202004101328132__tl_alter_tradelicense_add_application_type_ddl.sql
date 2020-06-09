ALTER TABLE eg_tl_TradeLicense
ADD COLUMN IF NOT EXISTS applicationtype character varying(64);

ALTER TABLE eg_tl_TradeLicense_audit
ADD COLUMN IF NOT EXISTS applicationtype character varying(64);