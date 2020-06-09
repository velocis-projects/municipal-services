package org.egov.tlcalculator.utils;

public class CTLConstants {
	
	public static final String MDMS_JSONPATH_FOR_MASTER_CATEGORY = "$.MdmsRes.#module#.#master#.*.category";
	public static final String MDMS_JSONPATH_FOR_MASTER_SERVICE = "$.MdmsRes.#module#.#master#.*.service";

	public static final String INVALID_CATEGORY_CODE = "INVALID_FEETYPE";
	public static final String INVALID_CATEGORY_MSG = "The following FeeType is not valid";
	
	public static final String INVALID_SERVICE_CODE = "INVALID_SERVICE";
	public static final String INVALID_SERVICE_MSG = "The following service is invalid";

	public static final String businessService_REHRI_RC = "CTL.REHRI_REGISTRATION";
	public static final String businessService_REHRI_DL = "CTL.REHRI_DRIVING_LICENSE";
	public static final String businessService_DHOBI_GHAT = "CTL.DHOBI_GHAT";
	public static final String businessService_BOOK_SHOP = "CTL.OLD_BOOK_MARKET";
	public static final String CTL_MDMS_CATEGORY = "category";
	public static final String CTL_MDMS_SERVICE = "service";
	public static final String CTL_MDMS_TAXHEADMASTER = "TaxHeadMaster";
	public static final String CTL_MDMS_BILLINGSERVICE ="BillingService";
	
	public static final String INVALID_CTL_FEETYPE_MSG = "Billing Slab should contain fee type";
	public static final String INVALID_CTL_BUSINESS_MSG = "Billing Slab should contain Business Service value";
	public static final String INVALID_CTL_FROMUOM_MSG = "Billing Slab should contain From UOM value";
	public static final String INVALID_CTL_TOUOM_MSG = "Billing Slab should contain To UOM value";
}
